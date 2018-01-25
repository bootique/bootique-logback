package io.bootique.logback.sentry;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class LogbackSentryModuleProviderIT {

    @Test
    public void testPresentInJar() {
        BQModuleProviderChecker.testPresentInJar(LogbackSentryModuleProvider.class);
    }

    @Test
    public void testMetadata() {
        BQModuleProviderChecker.testMetadata(LogbackSentryModuleProvider.class);
    }
}
