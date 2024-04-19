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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.LogbackLevel;
import io.bootique.logback.appender.AppenderFactory;
import io.sentry.Sentry;
import io.sentry.logback.SentryAppender;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO: Add child factory for some advanced configuration like buffers, async and exception handler
 * https://docs.sentry.io/clients/java/config/#in-application-stack-frames
 */
@JsonTypeName("sentry")
@BQConfig("Appender that sends errors to Sentry.")
@Deprecated(since = "3.0", forRemoval = true)
public class LogbackSentryFactory extends AppenderFactory {

    private String dsn;
    private String serverName;
    private String environment;
    private String release;
    private String minLevel;
    private String distribution;
    private List<String> applicationPackages;

    @Deprecated
    private boolean commonFramesEnabled = true;

    private Map<String, String> tags;
    private Map<String, String> extra;

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {
        Sentry.init(options -> {
            options.setDsn(getDsn());
            options.setServerName(getServerName());
            options.setEnvironment(getEnvironment());
            options.setRelease(getRelease());
            options.setDist(getDistribution());

            getTags().forEach(options::setTag);
            getApplicationPackages().forEach(options::addInAppInclude);
        });

        getExtra().forEach(Sentry::setExtra);

        final SentryAppender sentryAppender = new SentryAppender();

        final ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(getMinLevel());
        thresholdFilter.start();

        sentryAppender.addFilter(thresholdFilter);
        sentryAppender.start();

        return sentryAppender;
    }

    public String getDsn() {
        return dsn;
    }

    @BQConfigProperty("Your Sentry DSN (client key). If left blank, Raven will not perform logging. " +
            "Alternatively can be set via environment variable SENTRY_DSN.")
    public void setDsn(String dsn) {
        this.dsn = dsn;
    }

    public String getServerName() {
        return serverName;
    }

    @BQConfigProperty("Optional. Sets fixed server name, rather than looking it up dynamically.")
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getEnvironment() {
        return environment;
    }

    @BQConfigProperty("Optional. Sets environment your application is running in. Example: production")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRelease() {
        return release;
    }

    @BQConfigProperty("Optional. Sets release version of your application. Example: 1.0.0")
    public void setRelease(String release) {
        this.release = release;
    }

    public Map<String, String> getTags() {
        return tags != null ? tags : Collections.emptyMap();
    }

    @BQConfigProperty("Set the tags that should be sent along with the events. Example: tag1:value1,tag2:value2")
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }


    public Map<String, String> getExtra() {
        return extra != null ? extra : Collections.emptyMap();
    }

    @BQConfigProperty("By default all MDC parameters are sent under the Additional Data Tab. " +
        "By setting \"extra\" in your configuration you can define MDC keys to send as tags instead of " +
        "including them in Additional Data. " +
        "This allows them to be filtered within Sentry. Example: extra1:value1,extra2:value2")
    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    public String getMinLevel() {
        return minLevel != null ? minLevel : LogbackLevel.error.name();
    }

    public String getDistribution() {
        return distribution;
    }

    @BQConfigProperty("To set the application distribution that will be sent with each event. " +
        "Note that the distribution is only useful (and used) if the release is also set.")
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public List<String> getApplicationPackages() {
        return applicationPackages != null ? applicationPackages : Collections.emptyList();
    }

    @BQConfigProperty("Sentry differentiates stack frames that are directly related to your application (\"in application\") from stack frames that come from other packages such as the standard library, frameworks, or other dependencies. The difference is visible in the Sentry web interface where only the \"in application\" frames are displayed by default.")
    public void setApplicationPackages(List<String> applicationPackages) {
        this.applicationPackages = applicationPackages;
    }

    /**
     * @deprecated as it doesn't have corresponding config parameter in a new version of the Sentry API
     */
    @Deprecated
    public boolean isCommonFramesEnabled() {
        return commonFramesEnabled;
    }

    /**
     * @deprecated as it doesn't have corresponding config parameter in a new version of the Sentry API
     */
    @Deprecated
    @BQConfigProperty("Deprecated config parameter. Does nothing.")
    public void setCommonFramesEnabled(boolean commonFramesEnabled) {
        this.commonFramesEnabled = commonFramesEnabled;
    }

    @BQConfigProperty("Default minimal level for logging event. Example: error")
    public void setMinLevel(String minLevel) {
        this.minLevel = minLevel;
    }
}
