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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.appender.AppenderFactory;
import io.bootique.logback.appender.ConsoleAppenderFactory;
import io.bootique.shutdown.ShutdownManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@BQConfig
public class LogbackContextFactory {

    private LogbackLevel level;
    private String logFormat;
    private Map<String, LoggerFactory> loggers;
    private Collection<AppenderFactory> appenders;
    private Collection<String> appenderRefs;
    private boolean useLogbackConfig;
    private boolean debugLogback;

    public LogbackContextFactory() {
        this.level = LogbackLevel.info;
        this.loggers = Collections.emptyMap();
        this.appenders = Collections.emptyList();
        this.appenderRefs = Collections.emptyList();

        // TODO: to write unit tests for this flag we are waiting for
        // https://github.com/bootique/bootique/issues/52 to be implemented.
        this.useLogbackConfig = false;
    }

    public Logger createRootLogger(ShutdownManager shutdownManager, Map<String, java.util.logging.Level> defaultLevels) {

        LoggerContext context = createLogbackContext();
        shutdownManager.addShutdownHook(() -> context.stop());

        rerouteJUL();

        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        if (!useLogbackConfig) {

            Map<String, LoggerFactory> loggers = mergeLevels(defaultLevels);

            configLogbackContext(context, root, loggers);
        }

        return root;
    }

    protected void configLogbackContext(LoggerContext context, Logger root, Map<String, LoggerFactory> loggers) {
        context.reset();

        if (debugLogback) {
            StatusListenerConfigHelper.addOnConsoleListenerInstance(context, new OnConsoleStatusListener());
        }

        LevelChangePropagator propagator = new LevelChangePropagator();
        propagator.setContext(context);
        propagator.setResetJUL(true);

        context.addListener(propagator);

        root.setLevel(Level.toLevel(level.name(), Level.INFO));

        if (appenders.isEmpty()) {
            setAppenders(Collections.singletonList(new ConsoleAppenderFactory()));
        }

        Map<String, AppenderWithFlag> appenderMap =
                appenders.stream().filter(a -> a.getName() != null && !appenderRefs.contains(a.getName()))
                .collect(Collectors.toMap(AppenderFactory::getName,
                        a -> new AppenderWithFlag(a.createAppender(context, getLogFormat()))));

        appenders.forEach(a -> {
            if (a.getName() == null || appenderRefs.contains(a.getName())) {
                root.addAppender(a.createAppender(context, getLogFormat()));
            }
        });

        Map<String, Appender> appenderToAdd =
        appenderMap.entrySet().stream()
                .filter(a -> appenderRefs.contains(a.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, a -> a.getValue().getAppender()));

        loggers.forEach((name, lf) -> lf.configLogger(name, context, appenderMap));

        appenderMap.entrySet().forEach(a -> {
            if(!(a.getValue().isUsed() || appenderToAdd.containsKey(a.getKey()))) {
                appenderToAdd.put(a.getKey(), a.getValue().getAppender());
            }
        });

        appenderToAdd.values().forEach(root::addAppender);
    }

    private String getLogFormat() {
        return logFormat != null ? logFormat : "%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx";
    }

    /**
     * Merges a map of logging levels with this factory loggers configuration, returning a new map with combined
     * configuration. Factory logger levels take precedence over the provided levels argument (i.e. configuration
     * overrides code settings).
     *
     * @param levels a map of levels keyed by logger name.
     * @return a new map that is combination of factory loggers config and provided set of levels.
     */
    protected Map<String, LoggerFactory> mergeLevels(Map<String, java.util.logging.Level> levels) {

        if (levels.isEmpty()) {
            return this.loggers;
        }

        Map<String, LoggerFactory> merged = new HashMap<>(loggers);

        levels.forEach((name, level) -> {

            LoggerFactory factory = loggers.get(name);
            if (factory == null) {
                factory = new LoggerFactory();
                factory.setLevel(mapJULLevel(level));


                merged.put(name, factory);
            }
        });

        return merged;
    }

    protected LogbackLevel mapJULLevel(java.util.logging.Level level) {
        return JulLevel.valueOf(level.getName()).getLevel();
    }

    // inspired by Dropwizard. See DW DefaultLoggingFactory and
    // http://jira.qos.ch/browse/SLF4J-167. Though presumably Bootique calls
    // this from the main thread, so we should not be affected by the issue.
    protected LoggerContext createLogbackContext() {
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
     * @since 0.13
     */
    public LogbackLevel getLevel() {
        return level;
    }

    @BQConfigProperty("Root log level. Can be overridden by individual loggers. The default is 'info'.")
    public void setLevel(LogbackLevel level) {
        this.level = level;
    }

    /**
     * @return collection of log level configurations.
     * @since 0.12
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
     * @since 0.12
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
     * @param useLogbackConfig if true, all other logback configuration present in YAML is
     *                         ignored.
     * @since 0.9
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
     * @since 0.13
     */
    @BQConfigProperty("If true, Logback configuration debugging information will be printed to console. Helps to deal" +
            " with Logback configuration issues.")
    public void setDebugLogback(boolean debugLogback) {
        this.debugLogback = debugLogback;
    }

    /**
     * @param logFormat Log format specification used by all appenders unless redefined for a given appender.
     * @since 0.25
     */
    @BQConfigProperty("Log format specification used by child appenders unless redefined at the appender level, or not " +
            "relevant for a given type of appender. The spec is " +
            "compatible with Logback framework. The default is '%-5p [%d{ISO8601,UTC}] %thread %c{20}: %m%n%rEx'")
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }

    public Collection<String> getAppenderRefs() {
        return appenderRefs;
    }

    /**
     * @since 0.26
     */
    @BQConfigProperty("Collection of appender names which should be added to root Logger.")
    public void setAppenderRefs(Collection<String> appenderRefs) {
        this.appenderRefs = appenderRefs;
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

    static class AppenderWithFlag {

        private Appender appender;
        private boolean isUsed;

        public AppenderWithFlag(Appender appender) {
            this.appender = appender;
            this.isUsed = false;
        }

        public Appender getAppender() {
            return appender;
        }

        public void setAppender(Appender appender) {
            this.appender = appender;
        }

        public boolean isUsed() {
            return isUsed;
        }

        public void setUsed(boolean used) {
            isUsed = used;
        }
    }
}
