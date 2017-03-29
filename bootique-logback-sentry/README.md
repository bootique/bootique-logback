# Bootique Logback Sentry

Provides [Sentry](https://docs.sentry.io/clients/java/modules/logback/) integration with [Bootique-Logback](https://github.com/bootique/bootique-logback).

## Setup

Get DSN from [sentry.io](https://sentry.io/) or from your own instance of [Sentry](https://github.com/getsentry/sentry).

## Add bootique-logback-sentry to your build tool:

**Maven**
```xml
<dependency>
    <groupId>io.bootique.logback</groupId>
    <artifactId>bootique-logback-sentry</artifactId>
    <version>0.14</version>
</dependency>
```

**Gradle**
```groovy
compile("io.bootique.logback:bootique-logback-sentry:0.14")
```

*Note:* **bootique-logback-sentry** is a part of [bootique-bom](https://github.com/bootique/bootique-bom), and version can be 
imported from there.

## Write Configuration

**config.yml:**
```yaml
log:
  level: warn
  appenders:
    - type: console
      logFormat: '[%d{dd/MMM/yyyy:HH:mm:ss}] %t %-5p %c{1}: %m%n'
    - type: sentry
      dsn: 'your_dsn_here'
      serverName: aurora
      environment: development
      release: 42.0
      extraTags: foo,bar,baz
      minLevel: error
      ravenFactory: com.getsentry.raven.DefaultRavenFactory
      tags:
        tag1: value1
        tag2: value2
```

Also DSN can be provided via environment variable [SENTRY_DSN](https://github.com/getsentry/raven-java/tree/master/raven-logback).
