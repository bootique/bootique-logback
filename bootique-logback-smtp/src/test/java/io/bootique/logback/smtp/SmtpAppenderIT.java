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

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@BQTest
public class SmtpAppenderIT {

    @RegisterExtension
    static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP);

    @BQApp(skipRun = true)
    static final BQRuntime app = Bootique.app()
            .autoLoadModules()
            .module(b -> BQCoreModule.extend(b)
                    .setProperty("bq.log.appenders[0].type", "smtp")
                    .setProperty("bq.log.appenders[0].smtpPort", "3025")
                    .setProperty("bq.log.appenders[0].subject", "[%c %p] %m")
                    .setProperty("bq.log.appenders[0].from", "f@example.org")
                    .setProperty("bq.log.appenders[0].to[0]", "t@example.org")
                    .setProperty("bq.log.appenders[0].logFormat", "%c %p| %m"))
            .createRuntime();

    @Test
    public void deliverErrors() throws MessagingException {
        Logger logger = LoggerFactory.getLogger("deliverErrors");
        logger.error("This is an error");

        MimeMessage logMessage = readMail();
        assertEquals("f@example.org", logMessage.getFrom()[0].toString());
        assertEquals("t@example.org", logMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("[deliverErrors ERROR] This is an error", logMessage.getSubject());

        String body = GreenMailUtil.getBody(logMessage);
        assertTrue(body.contains("deliverErrors ERROR| This is an error"), body);
    }

    @Test
    public void doNotDeliverInfo() {
        Logger logger = LoggerFactory.getLogger("doNotDeliverInfo");
        logger.info("This is an info");
        assertFalse(greenMail.waitForIncomingEmail(500, 1));
    }

    private MimeMessage readMail() {
        assertTrue(greenMail.waitForIncomingEmail(500, 1));
        MimeMessage[] received = greenMail.getReceivedMessages();
        assertEquals(1, received.length);
        return received[0];
    }
}
