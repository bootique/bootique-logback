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
import ch.qos.logback.classic.boolex.OnErrorEvaluator;
import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.appender.AppenderFactory;

import java.util.List;

/**
 * @since 4.0
 */
@JsonTypeName("smtp")
@BQConfig("Appender that prints its output to stdout or stderr.")
public class SmtpAppenderFactory extends AppenderFactory {

    private String smtpHost;
    private Integer smtpPort;
    private List<String> to;
    private String from;
    private String subject;
    private String username;
    private String password;
    private Boolean startTls;
    private Boolean ssl;
    private String localhost;
    private String charsetEncoding;

    @BQConfigProperty("Optional SMTP host. The default is localhost")
    public SmtpAppenderFactory setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    @BQConfigProperty("Optional SMTP port. The default is 25")
    public SmtpAppenderFactory setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
        return this;
    }

    @BQConfigProperty("""
            One or more destination addresses. At least one 'to' address is required. Can be a Logback pattern, 
            incorporating dynamic elements in the address""")
    public SmtpAppenderFactory setTo(List<String> to) {
        this.to = to;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setFrom(String from) {
        this.from = from;
        return this;
    }

    @BQConfigProperty("Log email subject. Can be a Logback pattern, incorporating parts of the log message")
    public SmtpAppenderFactory setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setUsername(String username) {
        this.username = username;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setPassword(String password) {
        this.password = password;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setStartTls(Boolean startTls) {
        this.startTls = startTls;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setSsl(Boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setLocalhost(String localhost) {
        this.localhost = localhost;
        return this;
    }

    @BQConfigProperty
    public SmtpAppenderFactory setCharsetEncoding(String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
        return this;
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {
        SMTPAppender appender = createSmtpAppender(context, defaultLogFormat);
        appender.start();
        return appender;
    }

    protected SMTPAppender createSmtpAppender(LoggerContext context, String defaultLogFormat) {

        // OnErrorEvaluator ensures that levels below ERROR are not logged. Looks like we don't need an extra
        // filter for this to work.

        // TODO: configurable evaluators
        SMTPAppender appender = new SMTPAppender(new OnErrorEvaluator());

        appender.setName(name);
        appender.setContext(context);
        appender.setAsynchronousSending(true);
        appender.setSmtpHost(smtpHost);
        appender.setSmtpPort(smtpPort != null ? smtpPort : 25);
        to().forEach(appender::addTo);
        appender.setFrom(from);
        appender.setSubject(subject != null ? subject : "[Bootique SMTP appender] log entry");
        appender.setUsername(username);
        appender.setPassword(password);

        appender.setLayout(createLayout(context, defaultLogFormat));

        if (startTls != null) {
            appender.setSTARTTLS(startTls);
        }

        if (ssl != null) {
            appender.setSSL(ssl);
        }

        if (localhost != null) {
            appender.setLocalhost(localhost);
        }

        if (charsetEncoding != null) {
            appender.setCharsetEncoding(charsetEncoding);
        }

        if (filters != null) {
            filters.forEach(filter -> appender.addFilter(filter.createFilter()));
        }

        return appender;
    }

    private List<String> to() {
        if (to == null || to.isEmpty()) {
            throw new IllegalStateException("No 'to' addresses are provided");
        }

        return to;
    }
}
