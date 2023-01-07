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

package io.bootique.logback.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;

import java.util.Locale;
import java.util.TimeZone;

abstract class FileNamePatternValidator {

    private static final String SEE_LOGBACK_APPENDER = "See http://logback.qos.ch/manual/appenders.html";

    private static final String MISSING_INTEGER_TOKEN = "Missing integer token, that is %%i, in property \"fileNamePattern\" [%s]";
    private static final String UNEXPECTED_INTEGER_TOKEN = "Unexpected integer token, that is %%i, in property \"fileNamePattern\" [%s]";
    private static final String MISSING_DATE_TOKEN = "Missing date token, that is %%d, in property \"fileNamePattern\" [%s]";
    private static final String UNEXPECTED_DATE_TOKEN = "Unexpected date token, that is %%d, in property \"fileNamePattern\" [%s]";
    private static final String INCORRECT_DATE_FORMAT = "Incorrect date format in the date token in property \"fileNamePattern\" [%s]";

    private LoggerContext context;
    private String fileNamePattern;
    private String docRefId;

    FileNamePatternValidator(LoggerContext context, String fileNamePattern, String docRefId) {
        this.context = context;
        this.fileNamePattern = fileNamePattern;
        this.docRefId = docRefId;
    }

    abstract void validate();

    void checkPattern(boolean dateTokenExists, boolean integerTokenExists) {
        checkPatternMandatory();
        checkDateToken(dateTokenExists);
        checkIntegerToken(integerTokenExists);
    }

    void checkPatternMandatory() {
        if (fileNamePattern == null || fileNamePattern.isEmpty()) {
            throw new IllegalStateException("The property \"fileNamePattern\" is mandatory");
        }
    }

    void checkDateToken(boolean checkOnExisting) {
        FileNamePattern pattern = new FileNamePattern(fileNamePattern, context);
        DateTokenConverter<Object> token = pattern.getPrimaryDateTokenConverter();
        checkToken(token, checkOnExisting, fileNamePattern, MISSING_DATE_TOKEN, UNEXPECTED_DATE_TOKEN);
        if (checkOnExisting) {
            checkDateFormat(token, fileNamePattern);
        }
    }

    void checkIntegerToken(boolean checkOnExisting) {
        FileNamePattern pattern = new FileNamePattern(fileNamePattern, context);
        IntegerTokenConverter token = pattern.getIntegerTokenConverter();
        checkToken(token, checkOnExisting, fileNamePattern, MISSING_INTEGER_TOKEN, UNEXPECTED_INTEGER_TOKEN);
    }

    private void checkToken(Object token, boolean checkOnExisting, String... errorFormatParams) {
        Integer errorCodeIndex = null;
        if (checkOnExisting && token == null) {
            errorCodeIndex = 1;
        } else if (!checkOnExisting && token != null) {
            errorCodeIndex = 2;
        }
        if (errorCodeIndex != null) {
            throw new IllegalStateException(String.format("%s %s", String.format(errorFormatParams[errorCodeIndex], errorFormatParams[0]), getDocRef()));
        }
    }

    private void checkDateFormat(DateTokenConverter<Object> token, String pattern) {
        RollingCalendar rollingCalendar;
        if (token.getZoneId() != null) {
            TimeZone tz = TimeZone.getTimeZone(token.getZoneId());
            rollingCalendar = new RollingCalendar(token.getDatePattern(), tz, Locale.getDefault());
        } else {
            rollingCalendar = new RollingCalendar(token.getDatePattern());
        }
        if (!rollingCalendar.isCollisionFree()) {

            throw new IllegalStateException(String.format("%s %s", String.format(INCORRECT_DATE_FORMAT, pattern), getDocRef()));
        }
    }

    private String getDocRef() {
        return SEE_LOGBACK_APPENDER + "#" + docRefId;
    }
}
