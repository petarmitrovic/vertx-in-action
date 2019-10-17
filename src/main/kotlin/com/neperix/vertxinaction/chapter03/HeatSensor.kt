package com.neperix.vertxinaction.chapter03

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import java.util.*
import kotlin.random.Random

class HeatSensor : AbstractVerticle() {

    val id = UUID.randomUUID().toString()
    val random: Random = Random.Default
    var currentTemp: Double = 21.0

    override fun start() {
        scheduleNextUpdate()
    }

    private fun scheduleNextUpdate() {
        vertx.setTimer(random.nextLong(5000L) + 1000L, this::update)
    }

    private fun update(tid: Long) {
        currentTemp = currentTemp + delta() / 10
        vertx.eventBus().publish(
            "sensor.updates",
            JsonObject(mapOf("id" to id, "temp" to currentTemp))
        )
        scheduleNextUpdate()
    }

    private fun delta(): Double {
        return random.nextDouble(-1.0, 1.0)
    }
}
