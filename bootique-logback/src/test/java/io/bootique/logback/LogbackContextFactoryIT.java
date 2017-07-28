package io.bootique.logback;


import io.bootique.config.ConfigurationFactory;
import io.bootique.logback.appender.AppenderFactory;
import io.bootique.logback.appender.ConsoleAppenderFactory;
import io.bootique.logback.appender.ConsoleTarget;
import io.bootique.logback.appender.FileAppenderFactory;
import io.bootique.logback.unit.LogbackTestFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LogbackContextFactoryIT {

    @Rule
    public LogbackTestFactory LOGGER_STACK = new LogbackTestFactory();

    @Test
    public void testInitFromConfig() {

        ConfigurationFactory configFactory = LOGGER_STACK
                .newBQRuntime("--config=classpath:io/bootique/logback/test-multi-appender.yml")
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());

        assertNotNull(rootFactory.getAppenders());
        assertEquals(2, rootFactory.getAppenders().size());
        AppenderFactory[] appenders = rootFactory.getAppenders().toArray(new AppenderFactory[2]);

        assertTrue(appenders[0] instanceof ConsoleAppenderFactory);
        ConsoleAppenderFactory a1 = (ConsoleAppenderFactory) appenders[0];
        assertEquals(ConsoleTarget.stderr, a1.getTarget());
        assertEquals("%c{20}: %m%n", a1.getLogFormat());

        assertTrue(appenders[1] instanceof FileAppenderFactory);
        FileAppenderFactory a2 = (FileAppenderFactory) appenders[1];
        assertEquals("%c{10}: %m%n", a2.getLogFormat());
        assertEquals("target/logs/rotate/logfile123.log", a2.getFile());
    }

    @Test
    public void testVerboseOption() {
        ConfigurationFactory configFactory = LOGGER_STACK
                .newBQRuntime("--config=classpath:io/bootique/logback/test-multi-appender.yml", "--verbose")
                .getInstance(ConfigurationFactory.class);

        LogbackContextFactory rootFactory = configFactory.config(LogbackContextFactory.class, "log");

        assertEquals(LogbackLevel.debug, rootFactory.getLevel());
    }
}
