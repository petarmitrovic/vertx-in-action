package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx

class BlockEventLoop : AbstractVerticle() {

    override fun start() {
        vertx.setTimer(1000) {
            while (true) { }
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(BlockEventLoop())
}
