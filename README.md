![](https://github.com/petarmitrovic/vertx-in-action/workflows/.github/workflows/gradle.yml/badge.svg)

# Vertx in Action (Kotlin)

Here are the code samples from the https://www.manning.com/books/vertx-in-action.

While the book focuses on Java, these ones are written in Kotlin.

## Running the examples

Just like in the book, all examples are stored in the single Gradle project and in order to run each of them the Gradle build file defines a custom run task:
```kotlin
tasks.create<JavaExec>("run") {
    main = project.properties.getOrDefault("mainClass", "com.neperix.vertxinaction.chapter01.VertxEchoKt") as String
    classpath = sourceSets["main"].runtimeClasspath
    systemProperties["vertx.logger-delegate-factory-class-name"] = "io.vertx.core.logging.SLF4JLogDelegateFactory"
}
```

Please note that you need to add `Kt` suffix to the Kotlin class/file name. For example, if you want to run `HelloVerticle` the command should be:
```shell script
./gradlew run -PmainClass=com.neperix.vertxinaction.chapter02.HelloVerticleKt
```
