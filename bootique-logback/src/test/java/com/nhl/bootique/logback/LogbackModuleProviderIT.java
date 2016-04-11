package com.nhl.bootique.logback;

import org.junit.Test;

import com.nhl.bootique.test.junit.BQModuleProviderChecker;

public class LogbackModuleProviderIT {

	@Test
	public void testPresentInJar() {
		BQModuleProviderChecker.testPresentInJar(LogbackModuleProvider.class);
	}
}
