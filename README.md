# micrometer-jvm-extras

A set of additional JVM process metrics for [micrometer.io](https://micrometer.io/).

[![Apache License 2](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/mweirauch/micrometer-jvm-extras/main/LICENSE)
[![Build status](https://img.shields.io/github/workflow/status/mweirauch/micrometer-jvm-extras/CI?logo=GitHub)](https://github.com/mweirauch/micrometer-jvm-extras/actions?query=workflow%3ACI+branch%3Amain)
[![Quality Gate Status](https://img.shields.io/sonar/alert_status/mweirauch_micrometer-jvm-extras?logo=sonarcloud&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=mweirauch_micrometer-jvm-extras)
[![Coverage](https://img.shields.io/sonar/coverage/mweirauch_micrometer-jvm-extras?logo=sonarcloud&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=mweirauch_micrometer-jvm-extras)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mweirauch/micrometer-jvm-extras.svg?maxAge=300)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.github.mweirauch%22%20AND%20a%3A%22micrometer-jvm-extras%22)

## Motivation

* get "real" memory usage of the JVM beyond its managed parts
* get ahold of that info from within the JVM in environments where you can't
  instrument from the outside (e.g. PaaS)

## Usage

```xml
<dependency>
    <groupId>io.github.mweirauch</groupId>
    <artifactId>micrometer-jvm-extras</artifactId>
    <version>x.y.z</version>
</dependency>
```

```java
    /* Plain Java */
    final MeterRegistry registry = new SimpleMeterRegistry();
    new ProcessMemoryMetrics().bindTo(registry);
    new ProcessThreadMetrics().bindTo(registry);
```

```java
    /* With Spring */
    @Bean
    public MeterBinder processMemoryMetrics() {
        return new ProcessMemoryMetrics();
    }

    @Bean
    public MeterBinder processThreadMetrics() {
        return new ProcessThreadMetrics();
    }
```

## Available Metrics

### ProcessMemoryMetrics

`ProcessMemoryMetrics` reads process-level memory information from `/proc/self/status`.
All `Meter`s are reporting in `bytes`.

> Please note that `procfs` is only available on Linux-based systems.

* `process.memory.vss`: Virtual set size. The amount of virtual memory the process can access.
  Mostly irrelevant, but included for completeness sake.
* `process.memory.rss`: Resident set size. The amount of process memory currently in RAM.
* `process.memory.swap`: The amount of process memory paged out to swap.

### ProcessThreadMetrics

`ProcessThreadMetrics` reads process-level thread information from `/proc/self/status`.

> Please note that `procfs` is only available on Linux-based systems.

* `process.threads`: The number of process threads as seen by the operating system.
* `process.threads.context.switches.voluntary`: The accumulated number of voluntary context switches since application start.
  A voluntary context switch occurs when a thread is in a waiting or blocked state and the scheduler switches control to another
  thread.
* `process.threads.context.switches.nonvoluntary`: The accumulated number of non-voluntary context switches since application start.
  An involuntary context switch occurs when a thread consumed the whole time slice it was granted from the scheduler. The thread is
  suspended and control is switched to another thread.

## Notes

* `procfs` data is cached for `1000ms` in order to relief the filesystem pressure
  when `Meter`s based on this data are queried by the registry one after
  another on collection run.
* Snapshot builds are pushed to [Sonatype Nexus Snapshot Repository](https://oss.sonatype.org/content/repositories/snapshots/io/github/mweirauch/micrometer-jvm-extras/) on successful `main` builds.
