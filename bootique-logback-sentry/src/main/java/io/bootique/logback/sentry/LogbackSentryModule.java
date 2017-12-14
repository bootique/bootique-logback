package io.bootique.logback.sentry;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.sentry.Sentry;
import io.sentry.SentryClientFactory;

/**
 * Loads configuration for {@link io.sentry.Sentry}.
 *
 * @author Ibragimov Ruslan
 * @since 0.16
 */
public class LogbackSentryModule implements Module {

    @Override
    public void configure(Binder binder) {
    }

    @Singleton
    @Provides
    public SentryClientFactory bootiqueSentryClientFactory(LogbackSentryFactory logbackSentryFactory) {
        final BootiqueSentryClientFactory sentryClientFactory = new BootiqueSentryClientFactory(logbackSentryFactory);
        Sentry.init(logbackSentryFactory.getDsn(), sentryClientFactory);
        return sentryClientFactory;
    }
}
