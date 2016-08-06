package io.bootique.logback;

import io.bootique.test.junit.BQModuleProviderChecker;
import org.junit.Test;

public class LogbackModuleProviderIT {

	@Test
	public void testPresentInJar() {
		BQModuleProviderChecker.testPresentInJar(LogbackModuleProvider.class);
	}
}
