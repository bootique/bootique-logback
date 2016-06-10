package com.nhl.bootique.logback.policy;

import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;

/**
 * A factory that can be used to setup "fixed window" log rolling policy. The
 * policy is triggered by the main log file reaching a certain size and will
 * keep up to "historySize" rotated files. Follow the logback link below for
 * file name pattern rules, etc.
 * 
 * @see <a href=
 *      "http://logback.qos.ch/manual/appenders.html#FixedWindowRollingPolicy">
 *      Logback documentation</a>
 *
 * @since 0.10
 */
@JsonTypeName("fixedWindow")
public class FixedWindowPolicyFactory extends RollingPolicyFactory {

	private String fileSize;

	/**
	 * Sets a maximum size of a single log file. Exceeding this size causes
	 * rotation.
	 *
	 * @param fileSize
	 *            maximum size of a single log file expressed in bytes,
	 *            kilobytes, megabytes or gigabytes by suffixing a numeric value
	 *            with KB, MB and respectively GB. For example: 5000000, 5000KB,
	 *            5MB and 2GB.
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	protected FixedWindowRollingPolicy instantiatePolicy(LoggerContext context) {
		FixedWindowRollingPolicy policy = new FixedWindowRollingPolicy();
		policy.setFileNamePattern(getFileNamePattern());
		if (getHistorySize() > 0) {
			policy.setMinIndex(1);
			policy.setMaxIndex(getHistorySize());
		}
		policy.setContext(context);
		return policy;
	}

	@Override
	public TriggeringPolicy<ILoggingEvent> createTriggeringPolicy(LoggerContext context) {
		SizeBasedTriggeringPolicy<ILoggingEvent> policy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
		if (fileSize != null && fileSize.length() > 0) {
			policy.setMaxFileSize(fileSize);
		}
		policy.setContext(context);
		return policy;
	}

	@Override
	protected Class<? extends RollingPolicy> getRollingPolicyType() {
		return FixedWindowRollingPolicy.class;
	}
}
