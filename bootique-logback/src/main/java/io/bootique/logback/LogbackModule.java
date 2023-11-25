/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.logback;

import ch.qos.logback.classic.Logger;
import io.bootique.BQModuleProvider;
import io.bootique.annotation.LogLevels;
import io.bootique.bootstrap.BuiltModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.BQModule;
import io.bootique.di.Binder;
import io.bootique.di.Provides;
import io.bootique.shutdown.ShutdownManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

public class LogbackModule implements BQModule, BQModuleProvider {

    private static final String CONFIG_PREFIX = "log";

    @Override
    public BuiltModule buildModule() {
        return BuiltModule.of(this)
                .description("Integrates Logback logging library")
                .config(CONFIG_PREFIX, LogbackContextFactory.class)
                .build();
    }

    @Override
    public void configure(Binder binder) {
        // Binding a dummy class to trigger eager init of Logback as
        // @Provides below can not be invoked eagerly..
        binder.bind(LogInitTrigger.class).initOnStartup();
    }

    @Singleton
    @Provides
    Logger provideRootLogger(
            ConfigurationFactory configFactory,
            ShutdownManager shutdownManager,
            @LogLevels Map<String, java.util.logging.Level> defaultLevels) {

        return configFactory
                .config(LogbackContextFactory.class, CONFIG_PREFIX)
                .createRootLogger(shutdownManager, defaultLevels);
    }

    static class LogInitTrigger {

        @Inject
        public LogInitTrigger(Logger rootLogger) {
            rootLogger.debug("Logback started");
        }
    }

}
