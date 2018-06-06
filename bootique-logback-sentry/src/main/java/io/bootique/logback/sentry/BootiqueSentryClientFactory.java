/**
 *    Licensed to the ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.logback.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.dsn.Dsn;

import java.util.Collection;
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
    public BootiqueSentryClient createSentryClient(Dsn dsn) {
        BootiqueSentryClient sentryClient = new BootiqueSentryClient(createConnection(dsn), getContextManager(dsn));

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

        if (logbackSentryFactory.getDistribution() != null) {
            sentryClient.setDist(logbackSentryFactory.getDistribution());
        }

        return sentryClient;
    }

    @Override
    protected Collection<String> getInAppFrames(Dsn dsn) {
        return logbackSentryFactory.getApplicationPackages();
    }

    @Override
    protected boolean getHideCommonFramesEnabled(Dsn dsn) {
        return logbackSentryFactory.isCommonFramesEnabled();
    }
}
