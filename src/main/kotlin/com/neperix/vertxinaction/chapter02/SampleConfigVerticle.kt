package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class SampleConfigVerticle : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(SampleConfigVerticle::class.java)

    override fun start() {
        logger.info("n = {}", config().getInteger("n", -1))
    }
}

fun main() {
    val vertx = Vertx.vertx()

    for (n in 1..4) {
        val deploymentOptions = DeploymentOptions().apply {
            config = JsonObject(mapOf("n" to n))
            instances = n
        }
        vertx.deployVerticle(SampleConfigVerticle::class.java, deploymentOptions)
    }
}
