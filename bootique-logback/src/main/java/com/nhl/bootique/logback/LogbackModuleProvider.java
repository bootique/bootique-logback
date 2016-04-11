package com.nhl.bootique.logback;

import com.google.inject.Module;
import com.nhl.bootique.BQModuleProvider;

public class LogbackModuleProvider implements BQModuleProvider {

	@Override
	public Module module() {
		return new LogbackModule();
	}
}
