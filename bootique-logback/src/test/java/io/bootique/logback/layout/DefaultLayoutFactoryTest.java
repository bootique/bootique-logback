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

package io.bootique.logback.layout;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLayoutFactoryTest {

    @Test
    void createLayoutTest() {
        LoggerContext context = new LoggerContext();
        DefaultLayoutFactory factory = new DefaultLayoutFactory();
        Layout<ILoggingEvent> layout = factory.createLayout(context, "%c{20}: %m%n");
        assertTrue(layout instanceof PatternLayout);
        assertTrue(layout.isStarted());
    }

    @Test
    void createLayoutTestFormatNull() {
        LoggerContext context = new LoggerContext();
        DefaultLayoutFactory factory = new DefaultLayoutFactory();
        Layout<ILoggingEvent> layout = factory.createLayout(context, null);
        assertTrue(layout instanceof PatternLayout);
        assertFalse(layout.isStarted());
    }
}