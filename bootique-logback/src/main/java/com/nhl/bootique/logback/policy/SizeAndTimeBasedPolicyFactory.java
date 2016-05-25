package com.nhl.bootique.logback.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A factory what defines rules for creation size-and-time-based rolling policy.
 *
 * It is not needed to add any triggering policy to appender
 * @see <a href="http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy">Logback documentation</a>
 *
 * @since 0.10
 */
@JsonTypeName("size")
public class SizeAndTimeBasedPolicyFactory extends TimeBasedPolicyFactory {

    private String fileSize;

    /**
     * Sets a maximum size of a single log file. Exceeding this size causes
     * rotation.
     *
     * @param fileSize
     *            maximum size of a single log file expressed in bytes, kilobytes,
     *            megabytes or gigabytes by suffixing a numeric value with KB,
     *            MB and respectively GB. For example: 5000000, 5000KB, 5MB and
     *            2GB.
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public SizeAndTimeBasedRollingPolicy instantiatePolicy(LoggerContext context) {
		SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
        setupBasePolicySettings(policy);
        if (fileSize != null && fileSize.length() > 0) {
            policy.setMaxFileSize(fileSize);
        }
        policy.setContext(context);
        return policy;
    }

    @Override
    protected Class<? extends RollingPolicy> getRollingPolicyType() {
        return SizeAndTimeBasedRollingPolicy.class;
    }
}
