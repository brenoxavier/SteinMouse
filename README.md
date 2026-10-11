# Mouse

A Stein Loader mod for Minecraft 1.8.9: hold Shift and drag across an inventory or a chest to transfer stack after
stack. Each slot the cursor passes over is shift-clicked once, through normal clicks (one per tick), so the server
sees exactly what it would see from a player clicking. Always on, nothing to configure.

This repository is **private** and the mod is proprietary (see [LICENSE](LICENSE)). Players get the `.steinmod`
from its public release repository (the `RELEASE_REPO` variable of this repository, see below) through the
[Stein Client](https://github.com/Stein-Project/stein-client/releases/latest) and the mods index of
[Stein-Project/stein-loader](https://github.com/Stein-Project/stein-loader).

## Building

JDK 17 or newer to run Gradle (the JDK 25 the mod compiles with is downloaded if missing). The
[Stein SDK](https://github.com/Stein-Project/stein-sdk) Gradle plugin prepares Minecraft 1.8.9 for compiling the
first time, from your own installation or from Mojang.

```sh
./gradlew build            # build/libs/Mouse.steinmod (also checks the formatting)
./gradlew spotlessApply    # format the code (google-java-format, AOSP style) and add the licence header
./gradlew installMod       # build and copy into .minecraft/mods
```

Before a Stein SDK release, build against the local SDK (`../../sdk`, built there with
`./gradlew build publishPlugin`): `./gradlew build -Pstein.sdkDir=../../sdk`.

Before a release, check in the game: Shift and drag in the player's inventory and in a chest transfers each stack
once, nothing happens with items on the cursor, and closing the screen drops the clicks still queued.

## Release

A `v<version>` tag (equal to `"version"` in `src/main/resources/steinmod.json`), with
`changelog/<version>.en_US.md` (and optionally `.pt_BR.md`) written first. Running the workflow by hand is a dry run. See
`.github/workflows/release.yml` for the settings it needs: the `RELEASE_REPO` variable (the **public** repository
that hosts the downloads: x4vieer/stein-mouse for now) and the `RELEASE_TOKEN` and `DIST_TOKEN` secrets.

## Licence

Proprietary, all rights reserved: see [LICENSE](LICENSE) and [NOTICE](NOTICE). The Stein name and logo are covered by
the [trademark policy](https://github.com/Stein-Project/.github/blob/main/TRADEMARK.md).

Stein is not an official Minecraft product. It is not approved by or associated with Mojang or Microsoft.

Created by [Breno Xavier](https://github.com/brenoxavier).
