## CodeRacer

A speed typing app with an emphasis on coding and programming languages, inspired by popular websites such as MonkeyType and TypeRacer. This project originally started as a project in an introduction to programming course at TU/e. It has since been redeveloped and expanded into its current state.

## Installation

### Windows

Download and run the latest [CodeRacer Windows installer](https://github.com/Olof123-hash/CodeRacer/releases/latest). It installs the app with its Java runtime; Java and Maven do not need to be installed separately. The installer adds a Start Menu entry and desktop shortcut.

Settings and race history are stored in `%USERPROFILE%\.coderacer`.

### Build from source

Install Java 21 and Maven, then run:

```bash
mvn package
java -jar target/TyperRacerApp-jar-jar-with-dependencies.jar
```

To publish a Windows installer, push a version tag such as `v1.0.0`. GitHub Actions builds the installer and publishes it as a GitHub Release. The tag version is used for the installer version. You can also run the **Windows installer** workflow manually to produce a downloadable workflow artifact without creating a release.

## Usage

## Features

-

## Project Structure

## Tech Stack

## Roadmap

## Contributing

## Authors

## License
