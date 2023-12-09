/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.annotation.LogLevels;
import io.bootique.logback.appender.AppenderFactory;
import io.bootique.logback.appender.ConsoleAppenderFactory;
import io.bootique.shutdown.ShutdownManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@BQConfig
public class LogbackContextFactory {

    private final ShutdownManager shutdownManager;
    private final Map<String, java.util.logging.Level> diLevels;

    private LoggerFactory rootLoggerFactory;
    private String logFormat;
    private Map<String, LoggerFactory> loggers;
    private Collection<AppenderFactory> appenders;
    private boolean useLogbackConfig;
    private boolean debugLogback;

    @Inject
    public LogbackContextFactory(
            ShutdownManager shutdownManager,
            @LogLevels Map<String, java.util.logging.Level> diLevels) {

        this.shutdownManager = shutdownManager;
        this.diLevels = diLevels;

        this.rootLoggerFactory = new LoggerFactory();
        this.loggers = Collections.emptyMap();
        this.appenders = Collections.emptyList();

        // TODO: to write unit tests for this flag we are waiting for
        // https://github.com/bootique/bootique/issues/52 to be implemented.
        this.useLogbackConfig = false;
    }

    public Logger createRootLogger() {

        LoggerContext context = shutdownManager.onShutdown(
                createLogbackContext(),
                LoggerContext::stop);

        rerouteJUL();

        if (!useLogbackConfig) {
            Map<String, LoggerFactory> loggers = mergeLevels();
            configLogbackContext(context, loggers);
        }

        return context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    protected void configLogbackContext(LoggerContext context, Map<String, LoggerFactory> loggers) {
        context.reset();

        if (debugLogback) {
            StatusListenerConfigHelper.addOnConsoleListenerInstance(context, new OnConsoleStatusListener());
        }

        LevelChangePropagator propagator = new LevelChangePropagator();
        propagator.setContext(context);
        propagator.setResetJUL(true);
        context.addListener(propagator);

        if (appenders.isEmpty()) {
            setAppenders(Collections.singletonList(new ConsoleAppenderFactory()));
        }

        Map<String, Appender<ILoggingEvent>> namedAppenders = createNamedAppenders(context);
        Collection<Appender<ILoggingEvent>> anonAppenders = createAnonymousAppenders(context);

        // do not pass anonymous appenders to the child logger, only use them with the root logger
        loggers.forEach((name, lf) -> lf.configLogger(context.getLogger(name), namedAppenders, Collections.emptyList()));
        rootLoggerFactory.configLogger(context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME), namedAppenders, anonAppenders);
    }

    private Map<String, Appender<ILoggingEvent>> createNamedAppenders(LoggerContext context) {

        Set<String> uniqueAppenderRefs = new HashSet<>(getAppenderRefs());
        loggers.values().forEach(f -> uniqueAppenderRefs.addAll(f.getAppenderRefs()));

        Map<String, Appender<ILoggingEvent>> namedAppenders = new HashMap<>();

        appenders.forEach(a -> {
            // do not create appenders that are not explicitly referenced anywhere
            if (a.getName() != null && uniqueAppenderRefs.contains(a.getName())) {
                namedAppenders.put(a.getName(), a.createAppender(context, getLogFormat()));
            }
        });

        return namedAppenders;
    }

    private Collection<Appender<ILoggingEvent>> createAnonymousAppenders(LoggerContext context) {

        Collection<Appender<ILoggingEvent>> namedAppenders = new ArrayList<>();

        appenders.forEach(a -> {
            if (a.getName() == null) {
                namedAppenders.add(a.createAppender(context, getLogFormat()));
            }
        });

        return namedAppenders;
    }

    private String getLogFormat() {
        return logFormat != null ? logFormat : "%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx";
    }

    /**
     * @param logFormat Log format specification used by all appenders unless redefined for a given appender.
     */
    @BQConfigProperty("Log format specification used by child appenders unless redefined at the appender level, or not " +
            "relevant for a given type of appender. The spec is " +
            "compatible with Logback framework. The default is '%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx'")
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    /**
     * Merges a map of logging levels with this factory loggers configuration, returning a new map with combined
     * configuration. Factory logger levels take precedence over the provided levels argument (i.e. configuration
     * overrides code settings).
     *
     * @return a new map that is combination of factory loggers config and provided set of levels.
     */
    protected Map<String, LoggerFactory> mergeLevels() {

        if (diLevels.isEmpty()) {
            return this.loggers;
        }

        Map<String, LoggerFactory> merged = new HashMap<>(loggers);

        diLevels.forEach((name, level) -> {
            merged.computeIfAbsent(name, n -> {
                LoggerFactory f = new LoggerFactory();
                f.setLevel(mapJULLevel(level));
                return f;
            });
        });

        return merged;
    }

    protected LogbackLevel mapJULLevel(java.util.logging.Level level) {
        return JulLevel.valueOf(level.getName()).getLevel();
    }

    protected LoggerContext createLogbackContext() {

        // deal with startup thread-safety issues (see Dropwizard DefaultLoggingFactory).
        // TODO: https://jira.qos.ch/browse/SLF4J-167 is already fixed. This should not be needed

        long startTime = System.nanoTime();
        while (true) {
            ILoggerFactory iLoggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();

            if (iLoggerFactory instanceof LoggerContext) {
                return (LoggerContext) iLoggerFactory;
            }

            if ((System.nanoTime() - startTime) > 10_000_000) {
                throw new IllegalStateException("Unable to acquire the logger context");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    void rerouteJUL() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * @return default log level.
     */
    public LogbackLevel getLevel() {
        return rootLoggerFactory.getLevel();
    }

    @BQConfigProperty("Root log level. Can be overridden by individual loggers. The default is 'info'.")
    public void setLevel(LogbackLevel level) {
        rootLoggerFactory.setLevel(level);
    }

    /**
     * @return collection of log level configurations.
     */
    public Map<String, LoggerFactory> getLoggers() {
        return loggers;
    }

    @BQConfigProperty("Per-package or per-class loggers. Keys in the map are logger names.")
    public void setLoggers(Map<String, LoggerFactory> loggers) {
        this.loggers = loggers;
    }

    /**
     * @return collection of appender configurations.
     */
    public Collection<AppenderFactory> getAppenders() {
        return appenders;
    }

    @BQConfigProperty("One or more appenders that will render the logs to console, file, etc. If the list is empty, " +
            "console appender is used with default settings.")
    public void setAppenders(Collection<AppenderFactory> appenders) {
        this.appenders = appenders;
    }

    /**
     * If true, all other logback configuration present in YAML is ignored and
     * the user is expected to provide its own config file per
     * <a href="http://logback.qos.ch/manual/configuration.html">Logback
     * documentation</a>.
     *
     * @param useLogbackConfig if true, all other logback configuration present in YAML is ignored.
     */
    @BQConfigProperty("If true, all Bootique logback settings are ignored and the user is expected to provide its own " +
            "config file per Logback documentation. This is only needed for a few advanced options not directly " +
            "available via Bootique config. So the value should stay false (which is the default).")
    public void setUseLogbackConfig(boolean useLogbackConfig) {
        this.useLogbackConfig = useLogbackConfig;
    }

    /**
     * Sets whether to debug Logback startup and configuration loading.
     *
     * @param debugLogback if true, turns on tracing of Logback startup.
     */
    @BQConfigProperty("If true, Logback configuration debugging information will be printed to console. Helps to deal" +
            " with Logback configuration issues.")
    public void setDebugLogback(boolean debugLogback) {
        this.debugLogback = debugLogback;
    }

    public Collection<String> getAppenderRefs() {
        return rootLoggerFactory.getAppenderRefs();
    }

    @BQConfigProperty("Collection of appender names which should be added to root Logger.")
    public void setAppenderRefs(Collection<String> appenderRefs) {
        rootLoggerFactory.setAppenderRefs(appenderRefs);
    }

    private enum JulLevel {

        ALL(LogbackLevel.all),
        CONFIG(LogbackLevel.debug),
        FINE(LogbackLevel.debug),
        FINER(LogbackLevel.debug),
        FINEST(LogbackLevel.trace),
        INFO(LogbackLevel.info),
        OFF(LogbackLevel.off),
        SEVERE(LogbackLevel.error),
        WARNING(LogbackLevel.warn);

        private LogbackLevel level;

        JulLevel(LogbackLevel level) {
            this.level = level;
        }

        public LogbackLevel getLevel() {
            return level;
        }
    }
}
