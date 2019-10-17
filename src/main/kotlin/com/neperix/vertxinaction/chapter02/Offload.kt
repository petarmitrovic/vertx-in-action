package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class Offload : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(Offload::class.java)

    override fun start() {
        vertx.setPeriodic(5000) {
            logger.info("Tick")
            vertx.executeBlocking(this::blockingCode, this::resultHandler)
        }
    }

    fun blockingCode(promise: Promise<String>) {
        logger.info("Blocking code running...")
        Thread.sleep(4000)
        logger.info("Done")

        promise.complete("Ok!")
    }

    fun resultHandler(result: AsyncResult<String>) {
        if (result.succeeded()) {
            logger.info("Blocking code result: {}", result.result())
        } else {
            logger.error("Woops!", result.cause())
        }
    }
}
fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(Offload())
}
