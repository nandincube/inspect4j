# inspect4j

## Features:


## Install

### Preliminaries

Make sure you have JDK 17 installed. This can be downloaded [here](https://www.oracle.com/java/technologies/downloads/#java17).

### Java version
`inspect4j` has been tested using Java SE 17.0. 

### Operative System
`inspect4py` has been tested in Unix and Windows 11(22621.3007).

### Installation from code

`cd` into the `inspect4j` folder and install the package as follows:

```
git clone https://github.com/nandincube/inspect4j.git

```
You are done!

## Execution

The tool can be executed to inspect a file, or all the files of a given directory (and its subdirectories).
For example, it can be used to inspect all the java files of a given GitHub repository (that has been previously cloned locally).

To run analysis on a repository:

First, `cd` into `demo` within the `inspect4j` repository/package and run the following commands:

```
mvn clean package
java -jar target/inspect4j-1.0-jar-with-dependencies.jar <FILE.java | DIRECTORY> [OUTPUT_DIRECTORY]
```
To view the repository hierarchy:
 `cd` into `demo` within the `inspect4j` repository/package and run the following commands:

```
mvn clean package
java -jar target/inspect4j-1.0-jar-with-dependencies.jar <DIRECTORY> [OPTIONS]

options: 
    -t, --tree  Prints the directory/repository hierarchy of the given repository.
```
### Package dependencies:
```
junit:junit==4.11
com.github.javaparser:javaparser-symbol-solver-core==3.25.5
commons-io:commons-io==2.8.0
info.picocli:picocli==4.7.5
com.github.javaparser:javaparser-core-serialization==3.25.5
com.google.code.gson:gson==2.10.1
org.json:json==20230618
org.apache.maven:maven-model==3.6.3


```
