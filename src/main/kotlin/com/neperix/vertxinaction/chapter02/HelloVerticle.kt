package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class HelloVerticle : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(HelloVerticle::class.java)

    var counter = 0

    override fun start() {
        vertx.setPeriodic(5000) {
            logger.info("tick")
        }

        vertx.createHttpServer()
            .requestHandler { req ->
                logger.info("Request #{} from {}", ++counter, req.remoteAddress().host())
                req.response().end("Hello!");
            }
            .listen(8080)

        logger.info("Open http://localhost:8080")
    }
}

fun main() {
    val vertx: Vertx = Vertx.vertx()
    vertx.deployVerticle(HelloVerticle())
}
