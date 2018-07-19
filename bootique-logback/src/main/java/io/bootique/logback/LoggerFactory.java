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

package io.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@BQConfig
public class LoggerFactory {

	private LogbackLevel level;
	private Collection<String> appenderRefs;

	public LoggerFactory() {
		this.appenderRefs = Collections.emptyList();
		this.level = LogbackLevel.info;
	}

	@BQConfigProperty("Logging level of a given logger and its children.")
	public void setLevel(LogbackLevel level) {
		this.level = level;
	}

	/**
	 * @deprecated since 0.26 now use {{@link #configLogger(String, LoggerContext, Map)}}
	 */
	@Deprecated
	public void configLogger(String loggerName, LoggerContext context) {
		configLogger(loggerName, context, Collections.emptyMap());
	}

	public void configLogger(String loggerName, LoggerContext context, Map<String, LogbackContextFactory.AppenderWithFlag> appendersWithFlag) {
		Logger logger = context.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level.name(), Level.INFO));

		appendersWithFlag.entrySet().stream().filter(a -> appenderRefs.contains(a.getKey()))
				.forEach(a -> {
					logger.addAppender(a.getValue().getAppender());
					a.getValue().setUsed(true);
				});
	}

	public Collection<String> getAppenderRefs() {
		return appenderRefs;
	}

	@BQConfigProperty("Collection of references to named appenders")
	public void setAppenderRefs(Collection<String> appenderRefs) {
		this.appenderRefs = appenderRefs;
	}

}
