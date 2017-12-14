package io.bootique.logback.sentry;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.logback.LogbackModuleProvider;

import java.util.Collection;
import java.util.Collections;

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

    @Override
    public Collection<BQModuleProvider> dependencies() {
        return Collections.singletonList(new LogbackModuleProvider());
    }
}
