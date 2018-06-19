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
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

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
@BQConfig
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
	@BQConfigProperty
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
			policy.setMaxFileSize(FileSize.valueOf(fileSize));
		}
		policy.setContext(context);
		return policy;
	}

	@Override
	protected FileNamePatternValidator getFileNamePatternValidator(LoggerContext context) {

		return new FileNamePatternValidator(context, getFileNamePattern(), FixedWindowRollingPolicy.class.getSimpleName()) {
			@Override
			protected void validate() {
				checkPattern(false, true);
			}
		};
	}
}
