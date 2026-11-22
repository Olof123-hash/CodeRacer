# AGENTS.md — working on CodeRacer

Guidance for anyone (human or AI agent) changing this repository. It describes how the project is
built, how the code is organised, and the conventions and requirements the work so far has settled
on. Follow it instead of inventing new structure.

## What this is

CodeRacer (a.k.a. TyperRacer) is a Java 21 Swing desktop app for speed typing with code snippets,
inspired by MonkeyType and TypeRacer. One window holds the race screen; separate windows/dialogs
hold the settings, the game-over summary and the history.

- Entry point: `src/main/java/TyperRacerApp.java` (`main` applies `Theme.installDefaults()` and opens
  a `JFrame` with a `RacePanel`).
- Snippets and other data: JSON files in `src/main/resources` (`Java.json`, `Python.json`, …).
- Player state: `PlayedGames.json` and `Settings.json` in the working directory, read/written by
  `database/`.

## Build, run, verify

Maven project (`pom.xml`, Java 21, Gson 2.8.6, the NetBeans `AbsoluteLayout` jar in `lib/`):

```bash
mvn package                 # target/TyperRacerApp-jar-with-dependencies.jar
java -jar target/TyperRacerApp-jar-with-dependencies.jar
```

Maven is **not** installed on the current development machine, so verification is done with `javac`
from the `src` folder:

```bash
find main/java -name '*.java' > target/verify-sources.txt
javac -nowarn \
  -cp "target/verify-libs/gson-2.8.6.jar;../lib/unknown/binary/AbsoluteLayout/SNAPSHOT/AbsoluteLayout-SNAPSHOT.jar" \
  -d target/verify-classes @target/verify-sources.txt
```

Run compiled classes headless with `-Djava.awt.headless=true` and the classpath
`main/resources;target/verify-classes;target/verify-libs/gson-2.8.6.jar`. `target/` is git-ignored
scratch space.

There is no test framework and `src/test/java` is empty. Do not add one unasked. For UI behaviour
that can be exercised without a display, write a throwaway headless probe (panels can be sized and
painted into a `BufferedImage`), run it, then delete it. Dialog layout and look can not be verified
headless — say so instead of claiming it was checked.

## Layout and responsibilities

| Package | Holds | Rule |
| --- | --- | --- |
| `model` | `Settings`, `Snippet` | Plain data and defaults; no Swing, no I/O. |
| `logic` | `RaceEngine` | Typing rules and scoring; no painting, no settings persistence. |
| `database` | `Database`, `SettingsStore`, `PlayedGame(sDatabase)` | Reading/writing JSON. `SettingsStore` saves the whole `Settings` object atomically. |
| `UI` | Every window, panel, dialog, styler and `ColourScheme` | Presentation only. One file per class, `.form` files belong to the NetBeans designer. |
| resources | Snippets, persons, `backgrounds/*.jpg` | Add data here; reference by resource path, not by absolute file path. |

`RacePanel extends BackgroundPanel`; the visible text is `snippetPane` while an invisible
`typingField` laid over it receives the keystrokes. Keep that trick — do not merge the two.

## Code conventions

- Google Java Format style: 2-space indentation, 4-space continuation indents, ~100 column lines,
  one class per file, `final` utility classes with a private constructor.
- Javadoc on public classes and non-obvious methods. Explain **why** (the reason a guard exists, why
  a value is clamped), not what the line already says. Inline comments only where the code is not
  self-explanatory.
- Names are descriptive and spelled in British English (`Colour`, `colour`, `normalise`), matching
  the existing classes. Keep that spelling in new code and UI text.
- No new dependencies and no build-system changes unless the task requires them and the user agrees.
- Keep edits minimal and local: fix the cause in the smallest place that owns it. Do not refactor,
  restyle or "clean up" surrounding code that the request did not mention.

## Settings

A new preference is added in exactly these places:

1. `model/Settings`: a field with a default constant (`DEFAULT_…`), a getter and a setter that
   clamps or masks the value, plus a call in `normalise()` so a hand-edited or old `Settings.json`
   is repaired on load. Gson writes fields directly, so `normalise()` is the only validation.
2. `UI/SettingsPanel`: the control, its value in `restoreFromSettings()`, its entry in
   `resetToDefaults()`, the copy into the settings object in `fireSettingsChanged()`, and the
   comparison in `updateResetVisibility()`. The panel stays in sync with `settings` at all times;
   `notifying` guards re-entrant saves.
3. Whatever consumes the value (`RacePanel.applyTheme()`, which every settings change runs), read it
   from `Settings` and apply it on every refresh so live previews keep working.

Runtime JSON files (`Settings.json`, `PlayedGames.json`) change when the app runs. Do not include
their churn in commits unless the request is about them.

## Look and feel

- All colours, fonts and borders live in `UI/Theme`. Read them at paint time (`Theme.ACCENT`) so a
  scheme or accent change needs only a repaint. Do not hard-code `new Color(...)` in screens; new
  defaults belong in `Theme` or `Settings`.
- Use the existing stylers rather than setting properties by hand: `ButtonStyler` (`apply`,
  `applyPrimary`, `applySecondary`, `applyGhost`, `applyDanger`), `LabelStyler`, `TextStyler`,
  `SliderStyler`, `ComboBoxStyler`, `ToggleSwitchStyler`, `WindowStyler` (frame/dialog chrome,
  resizable edge borders, fullscreen), `Theme.roundedBorder(...)`. Stylers must stay idempotent:
  `SliderStyler.apply` checks `getUI() instanceof FlatSliderUI` for a reason — installing a second
  UI while its handle is dragged throws an NPE. Never remove such guards.
- Panels and dialogs that are restyled while open expose a `refreshTheme()`-style method and call it
  after a colour change; `SettingsPanel` repaints its custom buttons that way.
- Hard requirements from the project owner: **no gradients**, no Tailwind/CSS (it is Swing), no
  "old school" `JColorChooser` — colour picking uses `ColourPickerDialog` (swatch grid + hex field,
  click applies and closes, Cancel/Escape). Settings and history windows are undecorated and
  edge-resizable: `WindowStyler.apply(frame)` adds the thin invisible margin (`resizableFrameBorder`)
  and installs the private `ResizeHandler`, with a 360×240 minimum. Dialogs get `frameBorder()` only
  and are not resizable. Do not add a second resize mechanism or remove that border.
- Sizes are deliberate: dialogs set a preferred size and resizable frames a minimum size. Keep
  widgets inside the existing column widths instead of reflowing layouts.

## Backgrounds

`BackgroundPanel` owns the map from display name to classpath resource plus the solid colours. To add
a bundled image: drop it in `src/main/resources/backgrounds/`, add one entry to `BACKGROUNDS`, and it
appears in the settings background picker automatically (`imageBackgroundNames()`). The picker offers
bundled images or a plain colour (`Settings.BACKGROUND_CUSTOM_COLOUR` + `Settings.backgroundColour`);
it deliberately has **no** file chooser or upload option. Unknown names are ignored, and a missing
image leaves the plain colour.

## Race screen

- Snippet typography: one font size for every snippet, from `Settings.getSnippetFontSize()` and the
  settings slider (12–40, default 22). `applySnippetFont()` plus `scrollToCaret()` /
  `resetSnippetScroll()` keep the text in the same place and scroll it when it overflows. Do not
  reintroduce per-snippet font fitting.
- Typing colours (caret, correct, error, untyped) come from `Settings` and are applied through
  `Theme.applyTypingColours(...)` / `RacePanel.colorTypingField()`; they are independent of
  `ColourScheme`.
- A lost mistake on a space, tab or line break is shown as a background colour, because those
  characters have no glyph of their own. `RaceEngine` normalises `\r\n` / `\r` to `\n`, and
  `newGame()` gives the pane the same normalised text, so positions in the engine and the pane agree.
- Do not restructure the typing logic (`RaceEngine`, keystroke handling in `RacePanel`) to fix a
  cosmetic problem. Cosmetic fixes are colour/geometry changes only.

## Workflow

- Work on the current branch (`feature/game-window`); commit and push only when asked. Before a
  sizeable implementation the owner may ask for a backup commit — do that first.
- `.freebuff/` is tooling state and must not be edited or committed.
- When the owner asks for a written record, changes go in `SESSION_CHANGES.md` at the repository
  root: what was asked, what changed, which files, why, and how it was checked (including what was
  not checked). Update the existing document rather than starting a second one.
- Report verification honestly: compile result, what was exercised headless, and that visual
  layout was not seen on screen.

## Do not

- Add upload/file-picker features, accounts, networking, telemetry or extra windows that were not
  requested.
- Change dependency versions, the Maven setup or the source layout for tidiness.
- Commit build output (`target/`), runtime JSON churn, or `.freebuff/`.
- Claim a UI change looks right when only a compile was run.
