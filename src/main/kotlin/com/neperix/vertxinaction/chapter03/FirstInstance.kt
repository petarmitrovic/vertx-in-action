package com.neperix.vertxinaction.chapter03

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import org.slf4j.LoggerFactory

val firstLogger
    get() = LoggerFactory.getLogger("com.neperix.vertxinaction.chapter3.FirstInstance")


fun main() {

    Vertx.clusteredVertx(VertxOptions()) { ar ->
        if (ar.succeeded()) {
            firstLogger.info("First instance is started")

            val vertx = ar.result()
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HeatSensor", DeploymentOptions().setInstances(4))
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HttpServer")
        } else {
            firstLogger.error("Could not start", ar.cause())
        }
    }
}
