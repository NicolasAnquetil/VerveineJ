# VerveineJ

[![Use Java 17](https://img.shields.io/badge/Java-17-brightgreen)](https://adoptium.net/) ![CI](https://github.com/moosetechnology/VerveineJ/workflows/CI/badge.svg?branch=master)

A Java to JSON/MSE parser

Based on JDT, it parser java code to export it in the MSE format used by the Moose data analysis platform.
(Similar to the https://github.com/feenkcom/jdt2famix project, but more complete in what it extracts)

## Installation

[Installation page](https://moosetechnology.github.io/moose-wiki/Developers/Parsers/VerveineJ.html)

You only have to clone this project and then run verveineJ

```sh
# https
git clone https://github.com/moosetechnology/VerveineJ.git

# ssh
git clone git@github.com:moosetechnology/VerveineJ.git
```

## Developers

To test the project, remember that you **must** disable the `assert` by removing (or not using) the -ea parameter.
You also need to run tests one by one (fork method in IntelliJ).

> You can also use Ant or the pre-created IntelliJ build
