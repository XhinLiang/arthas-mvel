# Arthas-MVEL

[![JavaCI](https://github.com/XhinLiang/arthas-mvel/workflows/JavaCI/badge.svg)](https://github.com/XhinLiang/arthas-mvel/actions)
[![release](https://img.shields.io/github/v/release/XhinLiang/arthas-mvel?include_prereleases&label=release)](https://github.com/XhinLiang/arthas-mvel/releases)
![license](https://img.shields.io/github/license/XhinLiang/arthas-mvel.svg)

**Arthas-MVEL is a fork of [Alibaba Arthas](https://github.com/alibaba/arthas) that adds an `mvel` command** — evaluate arbitrary [MVEL](https://github.com/mvel/mvel) expressions against a running JVM, with bean lookup helpers, so you can read/poke application state and call methods on live objects from the Arthas console.

Everything else is stock Arthas. For general Arthas usage (`watch`, `trace`, `jad`, `tt`, profiler, …) see the **[official Arthas documentation](https://arthas.aliyun.com/doc/en/)**. This README only covers what the fork adds.

> Looking for how this fork is structured and kept in sync with upstream? See **[FORK.md](FORK.md)**.

## What the fork adds

| | |
| --- | --- |
| **`mvel` command** | Evaluate an MVEL expression in the target JVM: `mvel 'com.example.FooService.getInstance().reload()'`. |
| **mvel-as-default** | At the Arthas prompt, any line that isn't a known command is evaluated as MVEL — so you can just type `com.example.Config.getInstance().getValue()` directly. |
| **Bean helpers** | If you define `getBeanByName` / `getBeanByClass` / `getClassByName` (e.g. via a Spring `BeanFactory`), the command resolves bare bean names automatically. |

The command is packaged as a standard Arthas **external command** (`arthas-mvel-command`), loaded via the `CommandResolver` SPI — it does not patch `arthas-core`.

## Quick start

### Option A — download the full distribution (Arthas + mvel built in)

Download `arthas-bin.zip` from the [latest release](https://github.com/XhinLiang/arthas-mvel/releases), unzip, and attach to your Java process:

```bash
unzip arthas-bin.zip -d arthas-mvel && cd arthas-mvel
./as.sh <pid>      # Linux / macOS   (as.bat on Windows)
```

The `mvel` command is bundled in the distribution's `commands/` directory and loads automatically.

### Option B — add `mvel` to an existing Arthas install

Grab `arthas-mvel-command.jar` from the [latest release](https://github.com/XhinLiang/arthas-mvel/releases) and either drop it into your Arthas home `commands/` directory, or point Arthas at it on startup:

```bash
./as.sh --command-locations /path/to/arthas-mvel-command.jar <pid>
```

## Using the `mvel` command

```bash
# evaluate an expression
[arthas@1234]$ mvel '1 + 1'
2

# call a static method / read a static field
[arthas@1234]$ mvel 'com.example.Singleton.getInstance().getName()'

# pick the classloader by hash (see `sc -d <class>` / `classloader`)
[arthas@1234]$ mvel -c 1be6f5c3 'com.example.Foo.staticField'

# expand the result object to N levels (default 3)
[arthas@1234]$ mvel -x 5 'com.example.Foo.getInstance()'

# mvel-as-default: no command prefix needed
[arthas@1234]$ com.example.Config.getInstance().getValue()
```

Options: `-c, --classLoader <hash>` (target classloader, default = system), `-x, --expand <level>` (object expand depth, default 3).

## Build

Requires JDK 8+ (`JAVA_HOME` must be set — the native `arthas-vmtool` module needs `jni.h`).

```bash
./mvnw -V -ntp clean install -P full
```

The build produces `packaging/target/arthas-bin.zip` (full distribution) and `arthas-mvel-command/target/arthas-mvel-command-shade.jar` (the standalone command jar). See [FORK.md](FORK.md) for build/test notes (e.g. the MCP integration tests need a local `telnet`).

## Relationship to upstream & credits

This is a fork of **[alibaba/arthas](https://github.com/alibaba/arthas)** (currently based on `4.2.2`). All of Arthas's diagnostics, the website, docs, and the bulk of the code are the work of the Arthas authors and contributors at Alibaba — please star and support the upstream project. This fork only adds the MVEL command described above and tracks upstream periodically.

Licensed under the [Apache License 2.0](LICENSE), same as upstream.
