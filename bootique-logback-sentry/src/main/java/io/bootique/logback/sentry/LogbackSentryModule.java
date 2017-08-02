package io.bootique.logback.sentry;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.sentry.Sentry;
import io.sentry.SentryClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads configuration for {@link io.sentry.Sentry}.
 *
 * @author Ibragimov Ruslan
 * @since 0.16
 */
public class LogbackSentryModule implements Module {
    private final static Logger LOGGER = LoggerFactory.getLogger(LogbackSentryModule.class);

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
