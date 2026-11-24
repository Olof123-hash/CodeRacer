## CodeRacer

A speed typing app with an emphasis on coding and programming languages, inspired by popular
websites such as MonkeyType and TypeRacer. This project originally started as a project in an
introduction to programming course at TU/e. It has since been redeveloped and expanded into its
current state.

## Installation

### Windows

Choose the instructions for the release you downloaded from the [CodeRacer releases page](https://github.com/Olof123-hash/CodeRacer/releases):

#### v1.0.0 (`TypeRacer.zip`)

1. Download `TypeRacer.zip` from the v1.0.0 release assets and extract it to a folder where you
   want to keep the app.
2. Open the extracted `TypeRacer` folder and run `TypeRacer.exe`.

The extracted app includes its own Java runtime, so you do not need to install Java or Maven. This
zip is a portable app folder: it does not run an installer or automatically create a desktop
shortcut. To make one, right-click `TypeRacer.exe`, choose **Show more options → Send to → Desktop
(create shortcut)**. Keep the extracted folder in place because the shortcut runs the app from it.

#### v1.0.1 (`CodeRacer-<version>.exe`)

1. Download the `CodeRacer-1.0.1.exe` asset and run it.
2. Follow the installer prompts.

This per-user installer puts CodeRacer and its Java 21 runtime under your Windows user profile. It
creates a Start Menu entry and a desktop shortcut. Java and Maven do not need to be installed
separately. Uninstall it from **Windows Settings → Apps → Installed apps**.

Settings and race history are stored in `%USERPROFILE%\.coderacer`.

### Build from source

Install Java 21 and Maven, then run:

```bash
mvn package
java -jar target/TyperRacerApp-jar-jar-with-dependencies.jar
```

The JAR needs a Java 21 installation to run; the installer above does not.

## Usage

1. Choose what you type. With *Code snippets* switched on in Settings, the **Select Language**
   drop down offers Random, Python, Java, JavaScript, C++, Rust, Go, Assembly and Test. With it
   switched off, the drop down offers the plain text sources `Words` and `Text` instead.
2. Press **Start New Race**.
3. Type what the race screen shows. A wrong character cannot be typed past — Backspace removes it
   and the race continues once it is corrected. Correct text can never be deleted. Enter moves to
   the next line and skips the leading indentation, so you do not have to type the spaces or tabs
   of an indented block.
4. WPM, accuracy, mistakes and time update while you type. The race ends on the last character and
   opens the **Race stats** window.
5. Finished races are added to **View Typing History**. **End Race** stops early and is not saved.

Settings and race history are windows of their own, so they can stay open next to the race screen.
Every colour, the background, the snippet text size (12–40 pt) and the content type can be changed
there and are applied live.

## Features

- Code snippets in Python, Java, JavaScript, C++, Rust, Go and Assembly, or plain text from word
  and text sources.
- Live WPM, accuracy, mistakes and time readout, with a stats window and a saved typing history.
- Per-character colouring with configurable colours for the caret, correct text, wrong text and
  untyped text.
- Colour schemes (Ember, Ocean, Forest, Grape, Sand) plus a custom accent picked from a swatch grid
  or typed in as hex.
- Four bundled race backgrounds or a plain black or white one.
- Snippet text size from 12 to 40 pt, one size for every snippet.
- A "Reset to defaults" button that appears only while the settings differ from the defaults.

## Project Structure

```
src/main/java/
  TyperRacerApp.java        entry point, opens the race window
  model/                    Settings and Snippet; plain data, no Swing, no I/O
  logic/                    RaceEngine; typing rules and scoring
  database/                 JSON storage for settings and played games
  UI/                       every window, panel, dialog and styler
src/main/resources/
  *.json                    snippets per language, plus words and plain text
  backgrounds/              bundled race backgrounds
  fonts/                    fonts used by the UI
```

Settings and race history live in `%USERPROFILE%\.coderacer` on Windows and in `~/.coderacer`
elsewhere.

## Tech Stack

- Java 21 with Swing, laid out with NetBeans `AbsoluteLayout`.
- Maven for the build; `maven-assembly-plugin` produces a single runnable JAR with all
  dependencies.
- [Gson](https://github.com/google/gson) 2.8.6 for the JSON snippets, settings and history.
- `jpackage` (part of the JDK) for the Windows installer.

## Releasing

The **Windows installer** workflow (`.github/workflows/windows-installer.yml`) builds the app and
packages it with `jpackage`. Push a version tag to build and publish in one step:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The tag number becomes the installer version, so `v1.0.0` produces `CodeRacer-1.0.0.exe`, and the
workflow publishes a GitHub Release with that file attached. It is the only asset the release
needs: the Java runtime, Gson, `AbsoluteLayout` and all snippets, backgrounds and fonts are already
bundled inside the `.exe`. Do not attach the plain
`target/TyperRacerApp-jar-jar-with-dependencies.jar` — it needs a Java installation of its own.

To build an installer without publishing a release, run the workflow manually from the Actions tab
(**Windows installer** → **Run workflow**). That produces the `CodeRacer-Windows-Installer`
artifact, a zip containing `CodeRacer-<version>.exe`; a manual run that is not on a `v*` tag uses
version `1.0.0`.

If you upload that installer by hand with the GitHub "Attach binaries" box, attach only the
unzipped `.exe`, publish the release and keep it marked as the latest one — the download link in
this README points at `/releases/latest`, which only resolves once a non-draft, non-prerelease
release exists.

## Contributing

[`AGENTS.md`](AGENTS.md) describes how the project is built, how the code is organised and the
conventions new code should follow.
