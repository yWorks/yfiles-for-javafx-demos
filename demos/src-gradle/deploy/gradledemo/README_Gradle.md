# yFiles for JavaFX Gradle Demo

This is a simple demo showing how to set up a yFiles for JavaFX project using Gradle.

## Prerequisites

* An up-to-date Gradle installation. The demo was tested with Gradle >= 7.3.
* A JDK version 11 or newer.
* A JavaFX SDK that is compatible with the JDK version that is used to run this demo/build.
* `gradle --version` shows the relevant versions.

## Building

The demo is configured using the packaged yFiles library. If the library is also available via the local maven
repository (usually located in the user's home directory in `.m2/repository`) _this_ can also be used by replacing
the `repositories` and `dependencies` sections in the `build.gradle` with:

```groovy
repositories {
  mavenCentral()
  mavenLocal() // Used for the manually installed yfiles library (see below)
}
```

and

```groovy
dependencies {
  yfiles 'com.yworks.yfiles:yfiles-for-javafx-complete:3.5'
  yguard 'com.yworks:yguard:3.1.0'
}
```

The yFiles library can be installed into the local Maven repository as follows: In the `lib` folder of the package run:

```shell
mvn install:install-file -Dfile="yfiles-for-javafx.jar" -DgroupId="com.yworks.yfiles" -DartifactId="yfiles-for-javafx-complete" -Dversion="3.5" -Dpackaging="jar"
```

## Running

* Copy the license (e.g. `com.yworks.yfiles.java.developmentlicense.xml`) into the `src/main/resources` folder of the demo.
  * In the yFiles evaluation package the license file is already provided in this folder.
* Run `gradle run` in the directory where the `build.gradle` resides.

## Packaging and Obfuscation

The demo provides configurations for packaging and obfuscation. The whole build (including obfuscation) can be run with

`gradle obfuscate`

in the directory where the `build.gradle` resides.

### Packaging

The Gradle shadow plugin packages all necessary classes into one big jar file.

### Obfuscation

Obfuscation is accomplished with yWork's free yGuard tool. yGuard is configured as a Maven build plugin and therefore
need not be downloaded separately.

See the [yGuard home page](https://www.yworks.com/products/yguard) for additional information.

### Running the obfuscated demo App

After running `gradle obfuscate`, change to the `build/libs` directory. Here a `gradledemo-all_obf.jar` should have been
built. This Jar file contains all yFiles dependencies but not the JavaFX classes. To run it from the command line use:

```shell
java --module-path $PATH_TO_OPENJFX_LIB_DIR \ 
     --add-modules javafx.controls,javafx.fxml \
     -jar gradledemo-all_obf.jar
```

where `$PATH_TO_OPENJFX_LIB_DIR` points to the `lib` directory of a locally installed OpenJFX SDK.
