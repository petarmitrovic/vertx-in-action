package com.neperix.vertxinaction.chapter03

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.file.FileSystemOptions
import java.nio.file.FileSystem

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HeatSensor", DeploymentOptions().setInstances(4))
    vertx.deployVerticle("com.neperix.vertxinaction.chapter03.Listener")
    vertx.deployVerticle("com.neperix.vertxinaction.chapter03.SensorData")
    vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HttpServer")
}
