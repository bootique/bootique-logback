package io.bootique.logback.sentry;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.getsentry.raven.logback.SentryAppender;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.logback.appender.AppenderFactory;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@JsonTypeName("sentry")
@BQConfig("Appender that sends errors to Sentry.")
public class LogbackSentryFactory extends AppenderFactory {

    private String dsn;

    private String serverName;

    private String environment;

    private String release;

    private String minLevel;

    private Map<String, String> tags;

    private List<String> extraTags;

    private String ravenFactory;

    public LogbackSentryFactory() {
        this.minLevel = "error";
    }

    @Override
    public Appender<ILoggingEvent> createAppender(LoggerContext context) {
        final SentryAppender sentryAppender = new SentryAppender();

        if (dsn != null) sentryAppender.setDsn(dsn);
        if (serverName != null) sentryAppender.setServerName(serverName);
        if (release != null) sentryAppender.setRelease(release);
        if (environment != null) sentryAppender.setEnvironment(environment);
        if (ravenFactory != null) sentryAppender.setRavenFactory(ravenFactory);

        if (tags != null) {
            final String allTags = tags.entrySet().stream()
                    .map(it -> it.getKey() + ":" + it.getValue())
                    .collect(joining(","));
            sentryAppender.setTags(allTags);
        }

        if (extraTags != null) {
            final String allExtraTags = extraTags.stream()
                    .collect(joining(","));
            sentryAppender.setExtraTags(allExtraTags);
        }

        sentryAppender.setMinLevel(minLevel);

        sentryAppender.start();

        return sentryAppender;
    }

    @BQConfigProperty("Your Sentry DSN (client key). If left blank, Raven will not perform logging. " +
            "Alternatively can be set via environment variable SENTRY_DSN.")
    public void setDsn(String dsn) {
        this.dsn = dsn;
    }

    @BQConfigProperty("Optional. Sets fixed server name, rather than looking it up dynamically.")
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @BQConfigProperty("Optional. Sets environment your application is running in. Example: production")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @BQConfigProperty("Optional. Sets release version of your application. Example: 1.0.0")
    public void setRelease(String release) {
        this.release = release;
    }

    @BQConfigProperty("Set the tags that should be sent along with the events. Example: tag1:value1,tag2:value2")
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @BQConfigProperty("By default all MDC parameters are sent under the Additional Data Tab. " +
            "By setting \"extraTags\" in your configuration you can define MDC keys to send as tags instead of " +
            "including them in Additional Data. " +
            "This allows them to be filtered within Sentry. Example: foo,bar,baz")
    public void setExtraTags(List<String> extraTags) {
        this.extraTags = extraTags;
    }

    @BQConfigProperty("Optional. Sets com.getsentry.raven.RavenFactory class. Example: com.getsentry.raven.DefaultRavenFactory")
    // TODO: should factory classe be managed in DI?
    public void setRavenFactory(String ravenFactory) {
        this.ravenFactory = ravenFactory;
    }

    @BQConfigProperty("Default minimal level for logging event. Example: error")
    // TODO: Can we eliminate this?. We can already set log level at the top of the common "log" config.
    public void setMinLevel(String minLevel) {
        this.minLevel = minLevel;
    }
}
