/**
 *    Licensed to the ObjectStyle LLC under one
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

package io.bootique.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;

@BQConfig
public class LoggerFactory {

	private LogbackLevel level;

	public LoggerFactory() {
		this.level = LogbackLevel.info;
	}

	@BQConfigProperty("Logging level of a given logger and its children.")
	public void setLevel(LogbackLevel level) {
		this.level = level;
	}

	public void configLogger(String loggerName, LoggerContext context) {
		Logger logger = context.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level.name(), Level.INFO));
		
		// TODO: appenders
	}

}
