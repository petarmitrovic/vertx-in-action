package com.neperix.vertxinaction.chapter03

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.eventbus.EventBusOptions
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

val secondLogger
    get() = LoggerFactory.getLogger("com.neperix.vertxinaction.chapter3.SecondInstance")


fun main() {

    Vertx.clusteredVertx(VertxOptions().setEventBusOptions(EventBusOptions().setClustered(true))) { ar ->
        if (ar.succeeded()) {
            secondLogger.info("Second instance is started")

            val vertx = ar.result()
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HeatSensor", DeploymentOptions().setInstances(4))
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.Listener")
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.SensorData")
            vertx.deployVerticle("com.neperix.vertxinaction.chapter03.HttpServer", DeploymentOptions().setConfig(
                JsonObject(mapOf("port" to 8081))
            ))
        } else {
            secondLogger.error("Could not start", ar.cause())
        }
    }
}
