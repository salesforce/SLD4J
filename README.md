# SLD4J

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.salesforce.sld4j/sld4j/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser)

## SLD
The Secure Libraries for Developers project ("SLD") is a multi-language project to provide helpful security controls that aid developers and engineers in writing secure code. Every control is designed to be "secure-by-default" and wherever possible "secure-no-matter-what".

## SLD4J
The SLD Java project has two primary kinds of controls: those that thinly wrap default-insecure objects to make them secure and those that are utility controls to solve a difficult security problem.

## Installation

### Maven Central

Use the following coordinates
```
<groupId>com.salesforce.sld4j</groupId>
<artifactId>sld4j</artifactId>
```

## List of Controls

Control                         | Description
------------------------------- | --------------------------
SecureEncoder/SecureFilter | This pair of controls provides a set of context-based character modifiers that allow application developers to sanitize application data for safe output or processing.

## License
Please see the License.txt file
