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
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A factory what defines rules for creation size-and-time-based rolling policy.
 *
 * It is not needed to add any triggering policy to appender
 * 
 * @see <a href=
 *      "http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy">
 *      Logback documentation</a>
 */
@JsonTypeName("sizeAndTime")
public class SizeAndTimeBasedPolicyFactory extends TimeBasedPolicyFactory {

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
	protected SizeAndTimeBasedRollingPolicy<ILoggingEvent> instantiatePolicy(LoggerContext context) {
		SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();
		setupBasePolicySettings(policy);
		if (fileSize != null && fileSize.length() > 0) {
			policy.setMaxFileSize(FileSize.valueOf(fileSize));
		}
		policy.setContext(context);
		return policy;
	}

	@Override
	protected FileNamePatternValidator getFileNamePatternValidator(LoggerContext context) {
		return new FileNamePatternValidator(context, getFileNamePattern(), SizeAndTimeBasedRollingPolicy.class.getSimpleName()) {
			@Override
			protected void validate() {
				checkPattern(true, true);
			}
		};
	}
}
