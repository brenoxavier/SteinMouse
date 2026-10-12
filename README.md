# Mouse

A [Stein Loader](https://github.com/Stein-Project) mod for Minecraft 1.8.9: hold Shift and drag across an inventory
or a chest to transfer stack after stack.

- Each slot the cursor passes over is shift-clicked once, through normal clicks (one per tick), so the server sees
  exactly what it would see from a player clicking.
- Nothing happens with an item on the cursor, and closing the screen drops the clicks still queued.
- Always on, nothing to configure.

## Download

Mouse is a third-party mod: it is not on the Stein Client's official list. In the
[Stein Client](https://github.com/Stein-Project/stein-client/releases/latest), add its index under third-party mods
and the client installs and updates it for you:

```
https://github.com/brenoxavier/SteinMouse/releases/latest/download/steinmods.json
```

By hand: download `Mouse.steinmod` from the [latest release](https://github.com/brenoxavier/SteinMouse/releases/latest),
put it in `.minecraft/mods` and play with the **Stein Loader** profile.

## Building

You need **JDK 25 or newer**.
The [Stein SDK](https://github.com/Stein-Project/stein-sdk) Gradle plugin prepares Minecraft 1.8.9 for compiling the
first time, from your own installation or from Mojang.

```sh
./gradlew build            # build/libs/Mouse.steinmod (also checks the formatting)
./gradlew spotlessApply    # format the code (google-java-format, AOSP style) and add the licence header
./gradlew installMod       # build and copy into .minecraft/mods
```

Before a Stein SDK release (developing the SDK and the mod together), build against a local SDK: in a clone of
[sdk](https://github.com/Stein-Project/sdk) run `./gradlew build publishPlugin`, then here
`./gradlew build -Pstein.sdkDir=<path to that clone>` (the plugin comes from its `build/maven-repo`, the SDK from
its `build/sdk/stein-sdk.jar`).

## Contributing

Bug reports, ideas and pull requests are welcome. Read the
[contributing guide](https://github.com/Stein-Project/.github/blob/main/CONTRIBUTING.md) first; your first pull
request asks you to sign the [CLA](https://github.com/Stein-Project/.github/blob/main/CLA.md). For a change to this
mod, also check in the game that Shift and drag in the player's inventory and in a chest transfers each stack once,
that nothing happens with an item on the cursor, and that closing the screen drops the clicks still queued.

Releases are made by the maintainer: a `v<version>` tag, with `changelog/<version>.en_US.md` (and optionally
`.pt_BR.md`) written first. Each stable release carries the mod's index, `steinmods.json` (see
`.github/workflows/release.yml`).

## Licence

Mouse is licensed under the [Mozilla Public License 2.0](LICENSE). Third-party notices are in [NOTICE](NOTICE).
The Stein name and logo are not covered by the licence: see the
[trademark policy](https://github.com/Stein-Project/.github/blob/main/TRADEMARK.md).

Stein is not an official Minecraft product. It is not approved by or associated with Mojang or Microsoft.

Created by [Breno Xavier](https://github.com/brenoxavier).

