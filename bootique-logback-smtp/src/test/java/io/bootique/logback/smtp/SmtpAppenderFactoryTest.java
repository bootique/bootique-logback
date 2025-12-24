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

package io.bootique.logback.smtp;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SMTPAppender;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SmtpAppenderFactoryTest {

    @Test
    public void createSmtpAppender_Minimal() {

        SmtpAppenderFactory factory = new SmtpAppenderFactory()
                .setTo(List.of("a@example.org", "b@example.org"));

        SMTPAppender appender = factory.createSmtpAppender(new LoggerContext(), "%c{20}: %m%n");
        assertNull(appender.getSmtpHost());
        assertEquals(25, appender.getSMTPPort());
        assertNull(appender.getFrom());
        assertEquals("[Bootique SMTP appender] log entry", appender.getSubject());
        assertNull(appender.getUsername());
        assertNull(appender.getPassword());
        assertFalse(appender.isSTARTTLS());
        assertFalse(appender.isSSL());
        assertNull(appender.getLocalhost());
        assertEquals(Charset.defaultCharset().toString(), appender.getCharsetEncoding());
        assertEquals(0, appender.getCopyOfAttachedFiltersList().size());

        assertEquals(List.of("a@example.org%nopex", "b@example.org%nopex"), appender.getToAsListOfString());
    }
}
