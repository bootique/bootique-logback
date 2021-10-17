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
package io.bootique.logback.unit;

import ch.qos.logback.classic.Logger;
import io.bootique.BQRuntime;
import io.bootique.junit5.BQTestFactory;
import io.bootique.junit5.BQTestScope;
import io.bootique.junit5.scope.BQBeforeScopeCallback;
import io.bootique.logback.LogbackModule;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogTester implements BQBeforeScopeCallback {

    private final BQTestFactory testFactory;
    private final Path logDir;

    public LogTester(BQTestFactory testFactory, String logDir) {
        this.testFactory = testFactory;
        this.logDir = Paths.get(logDir);
    }

    @Override
    public void beforeScope(BQTestScope scope, ExtensionContext context) throws Exception {

        File logDirFile = logDir.toFile();

        if (logDirFile.exists()) {
            Files.walk(logDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        assertTrue(logDirFile.mkdirs());
    }

    public String run(String appConfig, String logFile, Consumer<Logger> logger) {
        Map<String, String> logs = run(appConfig, logger);
        assertTrue(logs.containsKey(logFile), () -> "No log file " + appConfig);
        return logs.get(logFile);
    }

    public Map<String, String> run(String appConfig, Consumer<Logger> logger) {
        try {
            return doRun(appConfig, logger);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> doRun(String appConfig, Consumer<Logger> logger) throws IOException {

        BQRuntime app = testFactory.app("-c", appConfig)
                .module(LogbackModule.class)
                .createRuntime();

        logger.accept(app.getInstance(Logger.class));

        // must stop to ensure logs are flushed...
        app.shutdown();

        return logsByFile();
    }

    private Map<String, String> logsByFile() throws IOException {

        Map<String, String> logs = new HashMap<>();
        Files.walk(logDir)
                .sorted(Comparator.reverseOrder())
                .filter(p -> p.toFile().isFile())
                .forEach(p -> logs.put(p.toFile().getName(), readLog(p)));


        return logs;
    }

    private String readLog(Path log) {
        try {
            return Files.lines(log).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
