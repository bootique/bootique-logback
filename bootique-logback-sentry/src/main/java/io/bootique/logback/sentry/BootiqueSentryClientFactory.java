package io.bootique.logback.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.dsn.Dsn;

import java.util.HashMap;

/**
 * Factory for providing config to {@link io.sentry.Sentry}.
 *
 * @author Ibragimov Ruslan
 * @since 0.16
 */
public class BootiqueSentryClientFactory extends DefaultSentryClientFactory {
    private final LogbackSentryFactory logbackSentryFactory;

    public BootiqueSentryClientFactory(LogbackSentryFactory logbackSentryFactory) {
        this.logbackSentryFactory = logbackSentryFactory;
    }

    @Override
    public SentryClient createSentryClient(Dsn dsn) {
        SentryClient sentryClient = new SentryClient(createConnection(dsn), getContextManager(dsn));

        if (logbackSentryFactory.getRelease() != null) {
            sentryClient.setRelease(logbackSentryFactory.getRelease());
        }

        if (logbackSentryFactory.getServerName() != null) {
            sentryClient.setServerName(logbackSentryFactory.getServerName());
        }

        if (logbackSentryFactory.getEnvironment() != null) {
            sentryClient.setEnvironment(logbackSentryFactory.getEnvironment());
        }

        if (logbackSentryFactory.getTags() != null) {
            sentryClient.setTags(logbackSentryFactory.getTags());
        }

        if (logbackSentryFactory.getExtra() != null) {
            sentryClient.setExtra(new HashMap<>(logbackSentryFactory.getExtra()));
        }

        return sentryClient;
    }
}
