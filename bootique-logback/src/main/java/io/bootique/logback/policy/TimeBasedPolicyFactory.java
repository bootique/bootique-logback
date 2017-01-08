package io.bootique.logback.policy;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

/**
 * A factory what defines rules for creation time-based rolling policy.
 *
 * It is not needed to add any triggering policy to appender
 * @see <a href="http://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy">Logback documentation</a>
 *
 * @since 0.10
 */
@JsonTypeName("time")
@BQConfig
public class TimeBasedPolicyFactory extends RollingPolicyFactory {

    private String totalSize;

    /**
     * Sets a maximum size of all log files combined. Equivalent to Logback
     * 'totalSizeCap' property.
     *
     * @param totalSize
     *            maximum size of all log files combined expressed in bytes,
     *            kilobytes, megabytes or gigabytes by suffixing a numeric value
     *            with KB, MB and respectively GB. For example: 5000000, 5000KB,
     *            5MB and 2GB.
     */
    @BQConfigProperty
    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    protected TimeBasedRollingPolicy<ILoggingEvent> instantiatePolicy(LoggerContext context) {
		TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        setupBasePolicySettings(policy);
		policy.setContext(context);
		return policy;
    }

    @Override
    public TriggeringPolicy<ILoggingEvent> createTriggeringPolicy(LoggerContext context) {
        return null; // There is no triggering policy
    }

    @Override
    protected FileNamePatternValidator getFileNamePatternValidator(LoggerContext context) {
        return new FileNamePatternValidator(context, getFileNamePattern(), TimeBasedRollingPolicy.class.getSimpleName()) {
            @Override
            protected void validate() {
                checkPattern(true, false);
            }
        };
    }

    protected void setupBasePolicySettings(TimeBasedRollingPolicy<ILoggingEvent> policy) {
        policy.setFileNamePattern(getFileNamePattern());
        if (getHistorySize() > 0) {
            policy.setMaxHistory(getHistorySize());
            policy.setCleanHistoryOnStart(true);
        }
        if (totalSize != null && totalSize.length() > 0) {
            policy.setTotalSizeCap(FileSize.valueOf(totalSize));
        }
    }
}
