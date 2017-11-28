package io.bootique.logback.sentry;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.appender.AppenderFactory;
import io.sentry.logback.SentryAppender;

import java.util.Map;

@JsonTypeName("sentry")
@BQConfig("Appender that sends errors to Sentry.")
public class LogbackSentryFactory extends AppenderFactory {

    private String dsn;

    private String serverName;

    private String environment;

    private String release;

    private String minLevel;

    private Map<String, String> tags;

    private Map<String, String> extra;

    public LogbackSentryFactory() {
        this.minLevel = "error";
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context, String defaultLogFormat) {

        final SentryAppender sentryAppender = new SentryAppender();

        final ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(minLevel);

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
        return tags;
    }

    @BQConfigProperty("Set the tags that should be sent along with the events. Example: tag1:value1,tag2:value2")
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }


    public Map<String, String> getExtra() {
        return extra;
    }

    @BQConfigProperty("By default all MDC parameters are sent under the Additional Data Tab. " +
        "By setting \"extra\" in your configuration you can define MDC keys to send as tags instead of " +
        "including them in Additional Data. " +
        "This allows them to be filtered within Sentry. Example: extra1:value1,extra2:value2")
    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    public String getMinLevel() {
        return minLevel;
    }

    @BQConfigProperty("Default minimal level for logging event. Example: error")
    public void setMinLevel(String minLevel) {
        this.minLevel = minLevel;
    }
}
