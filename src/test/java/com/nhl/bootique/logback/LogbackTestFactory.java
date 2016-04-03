package com.nhl.bootique.logback;

import java.util.Objects;

import com.nhl.bootique.BQRuntime;
import com.nhl.bootique.test.junit.BQTestFactory;

import ch.qos.logback.classic.Logger;

public class LogbackTestFactory extends BQTestFactory {

	public Logger newRootLogger(String config) {
		String arg0 = "--config=" + Objects.requireNonNull(config);
		BQRuntime runtime = newRuntime().configurator(bootique -> bootique.module(LogbackModule.class)).build(arg0);
		return runtime.getInstance(Logger.class);
	}

}
