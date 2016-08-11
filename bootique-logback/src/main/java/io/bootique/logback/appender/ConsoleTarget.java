package io.bootique.logback.appender;

/**
 * @since 0.12
 */
public enum ConsoleTarget {

    stdout(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemOut.getName()),
    stderr(ch.qos.logback.core.joran.spi.ConsoleTarget.SystemErr.getName());

    private String logbackTarget;

    ConsoleTarget(String logbackTarget) {
        this.logbackTarget = logbackTarget;
    }

    public String getLogbackTarget() {
        return logbackTarget;
    }
}
