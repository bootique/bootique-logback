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

import io.bootique.config.ConfigurationFactory;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestTool;
import io.bootique.logback.appender.AppenderFactory;
import io.bootique.logback.appender.ConsoleAppenderFactory;
import io.bootique.logback.appender.ConsoleTarget;
import io.bootique.logback.appender.FileAppenderFactory;
import io.bootique.logback.unit.LogTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class LogbackContextFactoryIT {

    @BQTestTool
    final BQTestFactory testFactory = new BQTestFactory().autoLoadModules();

    @BQTestTool
    final LogTester logTester = new LogTester(testFactory, "target/logs");

    @Test
    public void initFromConfig() {

        ConfigurationFactory configFactory = testFactory.app("-c", "classpath:io/bootique/logback/test-multi-appender.yml")
                .module(LogbackModule.class)
                .createRuntime()
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(2, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[2]);

        assertTrue(appenders[0] instanceof ConsoleAppenderFactory);
        ConsoleAppenderFactory a1 = (ConsoleAppenderFactory) appenders[0];
        assertEquals(ConsoleTarget.stderr, a1.getTarget());
        assertEquals("%c{20}: %m%n", a1.getLogFormat());

        assertTrue(appenders[1] instanceof FileAppenderFactory);
        FileAppenderFactory a2 = (FileAppenderFactory) appenders[1];
        assertEquals("%c{10}: %m%n", a2.getLogFormat());
        assertEquals("target/logs/logfile123.log", a2.getFile());
    }
}
