package io.bootique.logback.sentry;

import com.google.inject.Binder;
import com.google.inject.Module;

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
}
