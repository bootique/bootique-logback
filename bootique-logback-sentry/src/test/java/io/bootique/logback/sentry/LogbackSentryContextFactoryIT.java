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

package io.bootique.logback.sentry;

import ch.qos.logback.classic.Logger;
import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.config.ConfigurationFactory;
import io.bootique.logback.LogbackContextFactory;
import io.bootique.logback.LogbackLevel;
import io.bootique.logback.LogbackModule;
import io.bootique.logback.appender.AppenderFactory;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogbackSentryContextFactoryIT {

    @Test
    public void testInitFromConfig() {
        ConfigurationFactory configFactory = createRuntime().getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(1, rootFactory.getAppenders().size());
    }

    @Test
    public void testSentryAppenderFactory() {
        ConfigurationFactory configFactory = createRuntime().getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[2]);

        assertTrue(appenders[0] instanceof LogbackSentryFactory);
        LogbackSentryFactory appender = (LogbackSentryFactory) appenders[0];
        assertEquals("warn", appender.getMinLevel());
        assertEquals("testServerName", appender.getServerName());
        assertEquals("testDistribution", appender.getDistribution());
        assertEquals("https://public:private@sentry.io/123456?options", appender.getDsn());
        assertEquals("tests", appender.getEnvironment());
        assertEquals("4.2.0", appender.getRelease());

        assertEquals(2, appender.getTags().size());
        assertEquals("value1", appender.getTags().get("tag1"));
        assertEquals("value2", appender.getTags().get("tag2"));

        assertEquals(2, appender.getExtra().size());
        assertEquals("value1", appender.getExtra().get("extra1"));
        assertEquals("value2", appender.getExtra().get("extra2"));

        assertEquals(2, appender.getApplicationPackages().size());
        assertEquals("io.bootique.logback", appender.getApplicationPackages().get(0));
        assertEquals("com.myapp.package", appender.getApplicationPackages().get(1));
    }

    @Test
    public void testSentryClientInit() {
        createRuntime().getInstance(Logger.class).trace("Init logging");

        final SentryClient storedClient = Sentry.getStoredClient();

        assertEquals(BootiqueSentryClient.class, storedClient.getClass());
        assertEquals("testDistribution", storedClient.getDist());
    }

    private BQRuntime createRuntime() {
        String args = "--config=classpath:test-sentry-appender.yml";
        return Bootique.app(args).modules(LogbackModule.class, LogbackSentryModule.class).createRuntime();
    }
}
