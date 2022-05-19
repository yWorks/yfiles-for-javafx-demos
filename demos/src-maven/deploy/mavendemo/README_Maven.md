# yFiles for JavaFX Maven demo

This is a simple demo showing howto set up a yFiles for JavaFX project using Apache Maven.

## Prerequisites

* An up-to date Maven installation. The demo was tested with Maven >= 3.8.  
* A JDK version 11.
* `mvn --version` shows the relevant versions. 

JavaFX/OpenJFX is configured as a Maven dependency. It is therefore not necessary to download an OpenJFX SDK explicitly. Maven will take care of downloading all relevant dependencies. 

## Running during development
* yFiles is delivered as a single JAR file. To use it in a Maven project, the simplest approach is to install it as a Maven dependency into the local repository.
  * In the `lib` folder of the package run: `mvn install:install-file -Dfile="yfiles-for-javafx.jar" -DgroupId="com.yworks.yfiles" -DartifactId="yfiles-for-javafx-complete" -Dversion="3.5" -Dpackaging="jar"`
* Copy the license (e.g. `com.yworks.yfiles.javafx.developmentlicense.xml`) into the `src/main/java` folder of the demo.
  * The license is going to be copied during build to the build folder via the `maven-resources-plugin` as configured in the `pom.xml`.
* Run `mvn javafx:run` in the demo folder.

## Packaging and Obfuscation

The demo provides configurations for packaging and obfuscation. The whole built (including obfuscation) can be run with

`mvn clean package`

in the demo directory.

### Packaging

The `maven-assembly-plugin` packages all necessary classes into one big JAR file.

### Obfuscation

Obfuscation is accomplished with yWork's free yGuard tool. yGuard is configured as a maven build plugin and therefore need not be downloaded separately. 

See the [yGuard home page](https://www.yworks.com/products/yguard) for additional information.

### Running the obfuscated demo App

After running `mvn clean package`, change to the `target` directory. Here a `mavendemo-1.0.0-jar-with-dependencies_obfuscated.jar` should have been built.
This Jar file contains all dependencies except the JavaFX classes. To run it from the command line use:

```shell
java --module-path $PATH_TO_OPENJFX_LIB_DIR \ 
     --add-modules javafx.controls,javafx.fxml \
     -cp .\mavendemo-1.0.0-jar-with-dependencies_obfuscated.jar \
     mavendemo.MavenDemo
```

where `$PATH_TO_OPENJFX_LIB_DIR` points to the `lib` directory of a locally installed OpenJFX SDK.

