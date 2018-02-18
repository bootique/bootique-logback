package io.bootique.logback;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class LogbackModuleProviderIT {

	@Test
	public void testAutoLoadable() {
		BQModuleProviderChecker.testAutoLoadable(LogbackModuleProvider.class);
	}

	@Test
	public void testMetadata() {
		BQModuleProviderChecker.testMetadata(LogbackModuleProvider.class);
	}
}
