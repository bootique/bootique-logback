package com.nhl.bootique.logback.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;

import java.util.Locale;

public class FileNamePatternValidator {

    private static final String SEE_LOGBACK_APPENDER = "See http://logback.qos.ch/manual/appenders.html";
    private static final String SEE_TIME_BASED_ROLLING_POLICY = SEE_LOGBACK_APPENDER + "#TimeBasedRollingPolicy";
    private static final String SEE_SIZE_AND_TIME_BASED_ROLLING_POLICY = SEE_LOGBACK_APPENDER + "#SizeAndTimeBasedRollingPolicy";
    private static final String SEE_FIXED_WINDOW_ROLLING_POLICY = SEE_LOGBACK_APPENDER + "#FixedWindowRollingPolicy";

    private static final String FILE_NAME_PATTERN_MANDATORY = "The property \"fileNamePattern\" is mandatory";
    private static final String MISSING_INTEGER_TOKEN = "Missing integer token, that is %%i, in property \"fileNamePattern\" [%s]";
    private static final String UNEXPECTED_INTEGER_TOKEN = "Unexpected integer token, that is %%i, in property \"fileNamePattern\" [%s]";
    private static final String MISSING_DATE_TOKEN = "Missing date token, that is %%d, in property \"fileNamePattern\" [%s]";
    private static final String UNEXPECTED_DATE_TOKEN = "Unexpected date token, that is %%d, in property \"fileNamePattern\" [%s]";
    private static final String INCORRECT_DATE_FORMAT = "Incorrect date format in the date token in property \"fileNamePattern\" [%s]";

    public static void validate(String fileNamePattern, LoggerContext context, Class<? extends RollingPolicy> policyType) {
        if (fileNamePattern == null || fileNamePattern.length() == 0) {
            throw new IllegalStateException(FILE_NAME_PATTERN_MANDATORY);
        }
        if (TimeBasedRollingPolicy.class.equals(policyType)) {
            validateTimeBasedRollingPolicyPattern(fileNamePattern, context);
        } else if (SizeAndTimeBasedRollingPolicy.class.equals(policyType)) {
            validateSizeAndTimeBasedRollingPolicyPattern(fileNamePattern, context);
        } else if (FixedWindowRollingPolicy.class.equals(policyType)) {
            validateFixedWindowRollingPolicyPattern(fileNamePattern, context);
        }
    }

    private static void validateTimeBasedRollingPolicyPattern(String fileNamePattern, LoggerContext context) {
        checkDateToken(fileNamePattern, context, true, SEE_TIME_BASED_ROLLING_POLICY);
        checkIntegerToken(fileNamePattern, context, false, SEE_TIME_BASED_ROLLING_POLICY);
    }

    private static void validateSizeAndTimeBasedRollingPolicyPattern(String fileNamePattern, LoggerContext context) {
        checkDateToken(fileNamePattern, context, true, SEE_SIZE_AND_TIME_BASED_ROLLING_POLICY);
        checkIntegerToken(fileNamePattern, context, true, SEE_SIZE_AND_TIME_BASED_ROLLING_POLICY);
    }

    private static void validateFixedWindowRollingPolicyPattern(String fileNamePattern, LoggerContext context) {
        checkDateToken(fileNamePattern, context, false, SEE_FIXED_WINDOW_ROLLING_POLICY);
        checkIntegerToken(fileNamePattern, context, true, SEE_FIXED_WINDOW_ROLLING_POLICY);
    }

    private static void checkDateToken(String fileNamePattern, LoggerContext context, boolean checkOnExisting, String docHref) {
        FileNamePattern pattern = new FileNamePattern(fileNamePattern, context);
        DateTokenConverter<Object> token = pattern.getPrimaryDateTokenConverter();
        checkToken(token, checkOnExisting, fileNamePattern, docHref, MISSING_DATE_TOKEN, UNEXPECTED_DATE_TOKEN);
        if (checkOnExisting) {
            checkDateFormat(token, fileNamePattern, docHref);
        }
    }

    private static void checkIntegerToken(String fileNamePattern, LoggerContext context, boolean checkOnExisting, String docHref) {
        FileNamePattern pattern = new FileNamePattern(fileNamePattern, context);
        IntegerTokenConverter token = pattern.getIntegerTokenConverter();
        checkToken(token, checkOnExisting, fileNamePattern, docHref, MISSING_INTEGER_TOKEN, UNEXPECTED_INTEGER_TOKEN);
    }

    private static void checkToken(Object token, boolean checkOnExisting, String... errorFormatParams) {
        Integer errorCodeIndex = null;
        if (checkOnExisting && token == null) {
            errorCodeIndex = 2;
        } else if (!checkOnExisting && token != null) {
            errorCodeIndex = 3;
        }
        if (errorCodeIndex != null) {
            throw new IllegalStateException(String.format("%s %s", String.format(errorFormatParams[errorCodeIndex], errorFormatParams[0]), errorFormatParams[1]));
        }
    }

    private static void checkDateFormat(DateTokenConverter<Object> token, String pattern, String docRef) {
        RollingCalendar rollingCalendar;
        if (token.getTimeZone() != null) {
            rollingCalendar = new RollingCalendar(token.getDatePattern(), token.getTimeZone(), Locale.getDefault());
        } else {
            rollingCalendar = new RollingCalendar(token.getDatePattern());
        }
        if (!rollingCalendar.isCollisionFree()) {
            throw new IllegalStateException(String.format("%s %s", String.format(INCORRECT_DATE_FORMAT, pattern), docRef));
        }
    }
}
