package com.neperix.vertxinaction.chapter03

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class SensorData : AbstractVerticle() {

    val lastValues = mutableMapOf<String, Double>()

    val logger
        get() = LoggerFactory.getLogger(SensorData::class.java)

    override fun start() {
        vertx.eventBus().consumer("sensor.updates", this::update)
        vertx.eventBus().consumer("sensor.average", this::average)
    }

    fun update(message: Message<JsonObject>) {
        lastValues.put(
            message.body().getString("id"),
            message.body().getDouble("temp")
        )
    }

    fun average(message: Message<JsonObject>) {
        message.reply(JsonObject().put("average", lastValues.values.average()))
    }
}
