package com.neperix.vertxinaction.chapter03

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

class Listener : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(Listener::class.java)

    override fun start() {
        val bus = vertx.eventBus()
        bus.consumer<JsonObject>("sensor.updates") { msg ->
            val id = msg.body().getString("id")
            val temp = msg.body().getDouble("temp")

            logger.info("{} reports a temperature ~{}C", id, temp)
        }
    }
}
