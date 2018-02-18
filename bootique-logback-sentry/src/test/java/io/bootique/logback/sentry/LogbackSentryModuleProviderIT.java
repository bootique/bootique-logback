package io.bootique.logback.sentry;

import io.bootique.BQRuntime;
import io.bootique.logback.LogbackModule;
import io.bootique.test.junit.BQModuleProviderChecker;
import io.bootique.test.junit.BQRuntimeChecker;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

public class LogbackSentryModuleProviderIT {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testAutoLoadable() {
        BQModuleProviderChecker.testAutoLoadable(LogbackSentryModuleProvider.class);
    }

    @Test
    public void testMetadata() {
        BQModuleProviderChecker.testMetadata(LogbackSentryModuleProvider.class);
    }

    @Test
    public void testModuleDeclaresDependencies() {
        final BQRuntime bqRuntime = testFactory.app().module(new LogbackSentryModuleProvider()).createRuntime();
        BQRuntimeChecker.testModulesLoaded(bqRuntime, LogbackSentryModule.class, LogbackModule.class);
    }
}
