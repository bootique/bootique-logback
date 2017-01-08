package io.bootique.logback;

/**
 * @since 0.13
 */
public enum LogbackLevel {

    // names must be in lowercase - LC is what we expect in YAML..
    off, error, warn, info, debug, trace, all
}
