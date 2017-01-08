package io.bootique.logback;

import com.google.inject.Module;
import io.bootique.BQModuleProvider;

import java.util.Collections;
import java.util.Map;
import io.bootique.BQModule;

public class LogbackModuleProvider implements BQModuleProvider {

	@Override
	public Module module() {
		return new LogbackModule();
	}

	@Override
	public Map<String, Class<?>> configs() {
		// TODO: config prefix is hardcoded. Refactor away from ConfigModule, and make provider
		// generate config prefix, reusing it in metadata...
		return Collections.singletonMap("log", LogbackContextFactory.class);
	}

	@Override
	public BQModule.Builder moduleBuilder() {
		return BQModuleProvider.super
				.moduleBuilder()
				.description("Provides logging configuration.");
	}
}
