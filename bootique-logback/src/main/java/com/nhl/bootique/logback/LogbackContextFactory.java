package com.nhl.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import com.nhl.bootique.logback.appender.AppenderFactory;
import com.nhl.bootique.logback.appender.ConsoleAppenderFactory;
import com.nhl.bootique.shutdown.ShutdownManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogbackContextFactory {

    private Level level;
    private Map<String, LoggerFactory> loggers;
    private Collection<AppenderFactory> appenders;
    private boolean useLogbackConfig;

    public LogbackContextFactory() {
        this.level = Level.INFO;
        this.loggers = Collections.emptyMap();
        this.appenders = Collections.emptyList();

        // TODO: to write unit tests for this flag we are waiting for
        // https://github.com/nhl/bootique/issues/52 to be implemented.
        this.useLogbackConfig = false;
    }

    public Logger createRootLogger(ShutdownManager shutdownManager, Map<String, Level> defaultLevels) {

        LoggerContext context = createLogbackContext();
        shutdownManager.addShutdownHook(() -> {
            context.stop();
        });

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

        final LevelChangePropagator propagator = new LevelChangePropagator();
        propagator.setContext(context);
        propagator.setResetJUL(true);

        context.addListener(propagator);

        root.setLevel(level);

        loggers.forEach((name, lf) -> lf.configLogger(name, context));

        if (appenders.isEmpty()) {
            setAppenders(Collections.singletonList(new ConsoleAppenderFactory()));
        }

        appenders.forEach(a -> root.addAppender(a.createAppender(context)));
    }

    /**
     * Merges a map of logging levels with this factory loggers configuration, returning a new map with combined
     * configuration. Factory logger levels take precedence over the provided levels argument (i.e. configuration
     * overrides code settings).
     *
     * @param levels a map of levels keyed by logger name.
     * @return a new map that is combination of factory loggers config and provided set of levels.
     */
    protected Map<String, LoggerFactory> mergeLevels(Map<String, Level> levels) {

        if (levels.isEmpty()) {
            return this.loggers;
        }

        Map<String, LoggerFactory> merged = new HashMap<>(loggers);

        levels.forEach((name, level) -> {

            LoggerFactory factory = loggers.get(name);
            if(factory == null) {
                factory = new LoggerFactory();
                factory.setLevel(level.toString());
                merged.put(name, factory);
            }
        });

        return merged;
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

    public void setLevel(String level) {
        this.level = Level.toLevel(level, Level.INFO);
    }

    public void setLoggers(Map<String, LoggerFactory> loggers) {
        this.loggers = loggers;
    }

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
    public void setUseLogbackConfig(boolean useLogbackConfig) {
        this.useLogbackConfig = useLogbackConfig;
    }
}
