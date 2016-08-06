package io.bootique.logback;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

public class LogbackModuleProvider implements BQModuleProvider {

	@Override
	public Module module() {
		return new LogbackModule();
	}
}
