package io.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;
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

@BQConfig
public class LogbackContextFactory {

    private LogbackLevel level;
    private Map<String, LoggerFactory> loggers;
    private Collection<AppenderFactory> appenders;
    private boolean useLogbackConfig;

    public LogbackContextFactory() {
        this.level = LogbackLevel.info;
        this.loggers = Collections.emptyMap();
        this.appenders = Collections.emptyList();

        // TODO: to write unit tests for this flag we are waiting for
        // https://github.com/bootique/bootique/issues/52 to be implemented.
        this.useLogbackConfig = false;
    }

    public Logger createRootLogger(ShutdownManager shutdownManager, Map<String, java.util.logging.Level> defaultLevels) {

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

        root.setLevel(Level.toLevel(level.name(), Level.INFO));

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

    protected String mapJULLevel(java.util.logging.Level level) {
        return JulLevel.valueOf(level.getName()).getLevel().toString();
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

    private static enum JulLevel {

        ALL(Level.ALL),
        CONFIG(Level.DEBUG),
        FINE(Level.DEBUG),
        FINER(Level.DEBUG),
        FINEST(Level.TRACE),
        INFO(Level.INFO),
        OFF(Level.OFF),
        SEVERE(Level.ERROR),
        WARNING(Level.WARN);

        private Level level;

        JulLevel(Level level) {
            this.level = level;
        }

        public Level getLevel() {
            return level;
        }
    }
}
