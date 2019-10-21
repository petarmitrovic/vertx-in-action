package com.neperix.vertxinaction.chapter04.jukebox

import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(Jukebox())
    vertx.deployVerticle(NetControl())
}
