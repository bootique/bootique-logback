/**
 *  Licensed to ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.logback.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;

/**
 * A base abstract factory for defining of the rolling policy for rotation. This
 * factory defines file name pattern and history size what are used in all
 * rolling policies
 *
 * @since 0.10
 * @see <a href=
 *      "http://logback.qos.ch/manual/appenders.html#RollingFileAppender">
 *      Logback documentation</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@BQConfig
public abstract class RollingPolicyFactory implements PolymorphicConfiguration {

	private String fileNamePattern;
	private int historySize;
	
	/**
	 * Sets a filename pattern for the archived log files. The filename can be a
	 * pattern per <a href=
	 * "http://logback.qos.ch/manual/appenders.html#RollingFileAppender">Logback
	 * documentation</a> that determines time-based or fixed-window rotation
	 * interval.
	 *
	 * @param fileNamePattern
	 *            a filename pattern for the archived log files.
	 */
	@BQConfigProperty
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	/**
	 * Sets a size of rolling history. This property controls the maximum number
	 * of archive files to keep, asynchronously deleting older files.
	 *
	 * In case of {@link TimeBasedPolicyFactory}
	 * and {@link SizeAndTimeBasedPolicyFactory}
	 * , for example, if you specify monthly rollover (fileNamePattern is
	 * "logfile-%d{yyyy-MM}.log"), and set historySize to 6, then 6 months worth
	 * of archives files will be kept with files older than 6 months deleted.
	 * Note as old archived log files are removed, any folders which were
	 * created for the purpose of log file archiving will be removed as
	 * appropriate.
	 *
	 * In case of
	 * {@link FixedWindowPolicyFactory}, for
	 * example, if you set historySize to 6 then 6 archived files will be kept
	 *
	 * @param historySize
	 *            a size of rolling history
	 */
    @BQConfigProperty
	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	protected String getFileNamePattern() {
		return fileNamePattern;
	}

	protected int getHistorySize() {
		return historySize;
	}

	/**
	 * Creates rolling policy for rotation. This method validates rolling policy
	 * properties before creation policy.
	 *
	 * @param context
	 *            a logger context
	 * @return a rolling policy for rotation
	 * @throws IllegalStateException
	 *             if rolling policy properties are incorrect
	 */
	public RollingPolicy createRollingPolicy(LoggerContext context) {
		getFileNamePatternValidator(context).validate();
		return instantiatePolicy(context);
	}

	/**
	 * Creates triggering policy for rotation, if it is necessary.
	 *
	 * @param context
	 *            a logger context
	 * @return triggering policy for rotation or null
	 */
	public abstract TriggeringPolicy<ILoggingEvent> createTriggeringPolicy(LoggerContext context);

	/**
	 * Instantiates rolling policy for rotation.
	 *
	 * @param context
	 *            a logger context
	 * @return rolling policy for rotation
	 */
	protected abstract RollingPolicy instantiatePolicy(LoggerContext context);

	protected abstract FileNamePatternValidator getFileNamePatternValidator(LoggerContext context);
}
