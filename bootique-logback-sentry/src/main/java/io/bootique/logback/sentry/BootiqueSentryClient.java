package io.bootique.logback.sentry;

import io.sentry.SentryClient;
import io.sentry.connection.Connection;
import io.sentry.context.ContextManager;

/**
 * Wrapper for {@link SentryClient}.
 *
 * @author Ibragimov Ruslan
 * @since 0.25
 */
public class BootiqueSentryClient extends SentryClient {
    public BootiqueSentryClient(Connection connection, ContextManager contextManager) {
        super(connection, contextManager);
    }
}
