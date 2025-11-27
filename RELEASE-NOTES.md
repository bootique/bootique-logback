## 4.0-M2

* #74 "bootique-logback-smtp" and SMTPAppender

## 4.0-M1

* #71 Remove deprecated "bootique-logback-sentry" module
* #72 Upgrade to bootique-logback 1.5.18

## 3.0-RC2

* #73 Upgrade Logback to 1.5.18

## 3.0-M4

* #68 Upgrade logback to 1.5.5
* #69 Upgrade sentry-logback to 7.8.0
* #70 Deprecate bootique-logback-sentry

## 3.0-M3

* #66 Upgrade to Logback 1.4.13
* #67 LayoutFactory is not annotated and not showing subclasses in the docs

## 3.0.M2

* #63 Upgrade Logback to 1.4.5
* #64 Logback / Sentry - app crashes on startup when tags or extras are empty

## 3.0.M1

* #57 Config for JSON layout 
* #60 Upgrade logback-sentry to 5.5.2
* #62 Upgrade Logback to 1.2.11

## 2.0.RC1

* #52 "LOGBACK: No context given for c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy" printed
* #54 Log files are not deleted after file count reaches 1000 
* #58 Upgrade to Logback 1.2.9

## 2.0.M1

* Migrated from Guice to "bootique-di"
* #49 Upgrade Logback from v1.1.9 to 1.2.3
* #50 Logback "filters" in Bootique configurations

## 1.1

* #48 'append' flag in appender config

## 1.0.RC1

* #40 Support loggers.appender to route different loggers to different files
* #44 Update sentry-logback

## 0.25

* #41 Support appender-independent default log format
* #42 Update sentry-logback 
* #43 Upgrade to bootique-modules-parent 0.8

## 0.24

* #39 Update Logback Sentry module. 

## 0.15

* #34 Upgrade to BQ 0.23 

## 0.14

* #30 Bootique Logback Sentry module 
* #32 Upgrade SLF4J from 1.7.13 to 1.7.22
* #33 Upgrade to Bootique 0.22

## 0.13

* #24 "Debug" mode support
* #25 Upgrade to LogBack 1.1.9 #25
* #26 Upgrade to Bootique 0.21 and annotate configs

## 0.12

* #21  Allow redirecting console logging to stderr
* #23  Upgrade to Bootique 0.20

## 0.11

* #15 Support rolling policies in FileAppenderFactory - cleanup 
* #17 A la carte overrides of log levels
* #18 Upgrade to bootique 0.19
* #20 Move to io.bootique namespace

## 0.10

* #14 Support rolling policies in FileAppenderFactory

## 0.9: 

* #7 Log file rotation configuration
* #8  Switch to ServiceLoader-based polymorphic configs for AppenderFactory
* #9  Upgrade to Bootique 0.15
* #10 'log.useLogbackConfig' : support for Logback XML file.
* #12 Upgrade Logback to 1.1.7

## 0.8:

* #4 Adding Shutdown hook
* #5 Support for file appenders
* #6 Upgrade Bootique to 0.12

## 0.7:

* #1 Take over JUL logger
* #3 Start publishing Bootique to Maven central repo
