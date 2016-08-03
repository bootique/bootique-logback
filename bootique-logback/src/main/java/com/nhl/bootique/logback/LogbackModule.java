package com.nhl.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.nhl.bootique.ConfigModule;
import com.nhl.bootique.config.ConfigurationFactory;
import com.nhl.bootique.logback.annotation.LogLevels;
import com.nhl.bootique.shutdown.ShutdownManager;

import java.util.Map;

public class LogbackModule extends ConfigModule {

    public LogbackModule(String configPrefix) {
        super(configPrefix);
    }

    public LogbackModule() {
    }

    /**
     * Provides a way to contribute fixed
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return {@link MapBinder} for Bootique properties.
     * @since 0.11
     */
    public static MapBinder<String, Level> contributeLevels(Binder binder) {
        return MapBinder.newMapBinder(binder, String.class, Level.class, LogLevels.class);
    }

    @Override
    protected String defaultConfigPrefix() {
        return "log";
    }

    @Override
    public void configure(Binder binder) {
        // Binding a dummy class to trigger eager init of Logback as
        // @Provides below can not be invoked eagerly..
        binder.bind(LogInitTrigger.class).asEagerSingleton();

        // empty collections...
        LogbackModule.contributeLevels(binder);
    }

    @Singleton
    @Provides
    Logger provideRootLogger(ConfigurationFactory configFactory, ShutdownManager shutdownManager, @LogLevels Map<String, Level> defaultLevels) {
        return configFactory.config(LogbackContextFactory.class, configPrefix).createRootLogger(shutdownManager, defaultLevels);
    }

    static class LogInitTrigger {

        @Inject
        public LogInitTrigger(Logger rootLogger) {
            rootLogger.debug("Logback started");
        }
    }

}
