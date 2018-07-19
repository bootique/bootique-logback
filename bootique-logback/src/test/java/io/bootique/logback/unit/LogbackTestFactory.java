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
import io.bootique.logback.LogbackModule;
import io.bootique.test.junit.BQTestFactory;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogbackTestFactory extends BQTestFactory {

    public Logger newRootLogger(String config) {
        return newBQRuntime(config).getInstance(Logger.class);
    }

    public BQRuntime newBQRuntime(String config) {
        String arg0 = "--config=" + Objects.requireNonNull(config);
        return app(arg0).module(LogbackModule.class).createRuntime();
    }

    public void stop() {
        after();
    }

    public void prepareLogDir(String dir) {
        File parent = new File(dir);
        if (parent.exists()) {
            assertTrue(parent.isDirectory());
            asList(parent.list()).stream().forEach(name -> {
                File file = new File(parent, name);
                file.delete();
                assertFalse(file.exists());
            });
        } else {
            parent.mkdirs();
            assertTrue(parent.isDirectory());
        }
    }

    public Map<String, String[]> loglines(String dir, String expectedLogFilePrefix) {

        File parent = new File(dir);
        assertTrue(parent.isDirectory());

        Map<String, String[]> linesByFile = new HashMap<>();

        asList(parent.list()).stream().filter(name -> name.startsWith(expectedLogFilePrefix)).forEach(name -> {

            Path p = Paths.get(parent.getAbsolutePath(), name);
            try {
                linesByFile.put(name, Files.lines(p).toArray(String[]::new));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return linesByFile;
    }

}
