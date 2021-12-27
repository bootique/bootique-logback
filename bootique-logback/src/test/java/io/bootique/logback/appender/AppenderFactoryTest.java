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

package io.bootique.logback.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.log4j.XMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import io.bootique.config.ConfigurationFactory;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.logback.LogbackContextFactory;
import io.bootique.logback.LogbackLevel;
import io.bootique.logback.LogbackModule;
import io.bootique.logback.unit.LogTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@BQTest
class AppenderFactoryTest {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final LogTester logTester = new LogTester(testFactory, "target/logs");

    @Test
    void createLayout() {

        ConfigurationFactory configFactory = testFactory.app("-c", "classpath:io/bootique/logback/test-file-appender-pattern-layout.yml")
                .module(LogbackModule.class)
                .createRuntime()
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(1, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[1]);
        assertTrue(appenders[0] instanceof FileAppenderFactory);
        FileAppenderFactory a1 = (FileAppenderFactory) appenders[0];
        Layout<ILoggingEvent> actualLayout = a1.createLayout(new LoggerContext(), "");
        assertTrue(actualLayout instanceof PatternLayout);
    }

    @Test
    void createDefaultLayout() {

        ConfigurationFactory configFactory = testFactory.app("-c", "classpath:io/bootique/logback/test-file-appender-default-layout.yml")
                .module(LogbackModule.class)
                .createRuntime()
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(1, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[1]);
        assertTrue(appenders[0] instanceof FileAppenderFactory);
        FileAppenderFactory a1 = (FileAppenderFactory) appenders[0];
        Layout<ILoggingEvent> actualLayout = a1.createLayout(new LoggerContext(), "");
        assertTrue(actualLayout instanceof PatternLayout);
    }


    @Test
    void createHtmlLayout() {
        ConfigurationFactory configFactory = testFactory.app("-c", "classpath:io/bootique/logback/test-file-appender-html-layout.yml")
                .module(LogbackModule.class)
                .createRuntime()
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(1, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[1]);
        assertTrue(appenders[0] instanceof FileAppenderFactory);
        FileAppenderFactory a1 = (FileAppenderFactory) appenders[0];
        Layout<ILoggingEvent> actualLayout = a1.createLayout(new LoggerContext(), "");
        assertTrue(actualLayout instanceof HTMLLayout);
    }

    @Test
    void createXmlLayout() {
        ConfigurationFactory configFactory = testFactory.app("-c", "classpath:io/bootique/logback/test-file-appender-xml-layout.yml")
                .module(LogbackModule.class)
                .createRuntime()
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(1, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[1]);
        assertTrue(appenders[0] instanceof FileAppenderFactory);
        FileAppenderFactory a1 = (FileAppenderFactory) appenders[0];
        Layout<ILoggingEvent> actualLayout = a1.createLayout(new LoggerContext(), "");
        assertTrue(actualLayout instanceof XMLLayout);
    }
}