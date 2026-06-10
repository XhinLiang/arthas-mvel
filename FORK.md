# Fork notes — arthas-mvel

`XhinLiang/arthas-mvel` is a fork of [`alibaba/arthas`](https://github.com/alibaba/arthas).
It adds an **MVEL expression command** on top of upstream. This file documents exactly
what diverges from upstream so the fork stays cheap to maintain.

## What the fork adds

| Capability | Where it lives | Touches upstream? |
| --- | --- | --- |
| `mvel <expr>` command | **`arthas-mvel-command/`** module (external command, loaded via the `CommandResolver` SPI) | **No** |
| mvel-as-default (a bare expression with no known command is evaluated as MVEL) | `core/.../JobControllerImpl.java` (patch) | Yes — small |
| arthas-mvel branding (logo + github link) | `core/.../util/ArthasBanner.java` (patch) | Yes — cosmetic |

The goal of this layout: keep the *command itself* (the bulk of the feature) out of
upstream files so syncs don't conflict, and confine the unavoidable changes to two small,
clearly-labeled patches.

### 1. The `mvel` command — `arthas-mvel-command/` (no upstream edits)

A standalone module that depends on `arthas-core` (`provided`) and ships its own `mvel2`
dependency shaded into a single jar. It registers via
`META-INF/services/com.taobao.arthas.core.shell.command.CommandResolver`
→ `com.taobao.arthas.mvel.MvelCommandResolver`.

The assembly (`packaging/src/main/assembly/assembly.xml`) drops the shaded jar into the
distribution's default `commands/` directory, so `mvel` is auto-loaded at startup
(see upstream's `site/docs/doc/external-command.md`). It can also be loaded ad hoc with
`--command-locations /path/to/arthas-mvel-command.jar`.

### Single self-contained launcher — `arthas-mvel-launcher/`

Module `arthas-mvel-launcher/` produces **`arthas-mvel.jar`**: a self-extracting fat jar so a
user needs only this one file to run `java -jar arthas-mvel.jar <pid>` (offline, no extra
downloads). It embeds the whole `packaging/target/arthas-bin` distribution under the jar's
`arthas-home/` prefix (via an antrun copy at `prepare-package`) plus `arthas-boot`'s
`Bootstrap` (shaded in). At runtime `ArthasMvelLauncher` extracts `arthas-home/` to
`~/.arthas-mvel/<version>/arthas` on first run (idempotent — skips if `arthas-core.jar` is
already there) and calls `Bootstrap.main` with `--arthas-home`. It builds after `arthas-packaging`
(declared as a `provided` dependency to order the reactor).

Classes (package `com.taobao.arthas.mvel`): `MvelCommand`, `MvelCommandResolver`,
`MvelExpress` (implements core's `Express`), `MvelEvalKiller`, `MvelContext`,
`MvelExpressFactory` (per-classloader cache).

> Note: the command no longer needs `guava`/`commons-lang` — `exceptionToString` was
> rewritten with the JDK only, so `mvel2` is the sole third-party runtime dependency.
>
> `mvel2` must stay on **2.5.x or newer**: 2.4.x references `java.lang.Compiler` (removed
> from the JDK) and fails with `NoClassDefFoundError: java/lang/Compiler` on JDK 22+.
> `MvelExpressTest` guards this across the JavaCI JDK matrix (8–25).

### 2. & 3. The two core patches

- `JobControllerImpl.createProcess`: when no command matches a line, resolve the command
  named `"mvel"` **by name** (`commandManager.getCommand("mvel")`) and feed it the raw
  line. Resolving by name means core has **no compile-time dependency** on the plugin —
  if the plugin isn't loaded, it cleanly falls back to upstream's "command not found".
  Also adds the helper `createMvelCommandProcess(...)`.
- `ArthasBanner`: `LOGO` text, a `GITHUB` constant + `github()` accessor, and a `github`
  row in the welcome table.

## Syncing with upstream

1. `git fetch upstream` (remote = `https://github.com/alibaba/arthas.git`).
2. Prefer syncing against a **release tag** (e.g. `arthas-all-4.2.2`) over `master` HEAD.
3. Merge, resolve conflicts. The plugin module won't conflict (upstream doesn't know it).
   Only `JobControllerImpl` and `ArthasBanner` can conflict — re-apply the two patches above.
4. **Verify the feature survived**: `arthas-mvel-command`'s `MvelExpressTest` is the
   guardrail — if MVEL evaluation breaks (missing dep, mvel2 API change, …), the build
   goes red instead of failing silently. Run `./mvnw -pl arthas-mvel-command -am test`.

## Build / verify

```bash
# JAVA_HOME must be set or arthas-vmtool's native build fails (jni.h not found)
export JAVA_HOME=/path/to/jdk
./mvnw -V -ntp clean install -P full verify
```

The MCP integration tests need a local `telnet` binary (`brew install telnet` on macOS),
otherwise they fail with `as.sh attach 失败` — environmental, not a code bug.

## gh CLI gotcha

The `upstream` remote points at `alibaba/arthas`, and `gh` resolves its default repo there.
Use `gh repo set-default XhinLiang/arthas-mvel`, or call REST directly:
`gh api repos/XhinLiang/arthas-mvel/...`.
