package io.bootique.logback.sentry;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

/**
 * Provider for {@link LogbackSentryModule}.
 *
 * @author Ibragimov Ruslan
 * @since 0.16
 */
public class LogbackSentryModuleProvider implements BQModuleProvider {

    @Override
    public Module module() {
        return new LogbackSentryModule();
    }
}
