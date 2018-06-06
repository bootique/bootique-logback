/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.logback.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ConsoleAppenderFactoryTest {

    private LoggerContext mockContext;

    @Before
    public void before() {
        mockContext = mock(LoggerContext.class);
    }

    @Test
    public void testCreateConsoleAppenderTarget_Default() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext, "");

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut.getName(), appender.getTarget());
    }

    @Test
    public void testCreateConsoleAppenderTarget_Stderr() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        factory.setTarget(ConsoleTarget.stderr);
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext, "");

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemErr.getName(), appender.getTarget());
    }

    @Test
    public void testCreateConsoleAppenderTarget_Stdout() {
        ConsoleAppenderFactory factory = new ConsoleAppenderFactory();
        factory.setTarget(ConsoleTarget.stdout);
        ConsoleAppender<ILoggingEvent> appender = factory.createConsoleAppender(mockContext, "");

        assertEquals(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut.getName(), appender.getTarget());
    }

}
