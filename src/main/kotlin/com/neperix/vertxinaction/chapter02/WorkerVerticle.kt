package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class WorkerVerticle : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(WorkerVerticle::class.java)

    override fun start() {
        vertx.setPeriodic(10_000) {
            logger.info("Zzz... ")
            Thread.sleep(8000)
            logger.info("Up!")
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()
    val opts = DeploymentOptions().apply {
        instances = 2
        setWorker(true)
    }

    vertx.deployVerticle(WorkerVerticle::class.java.canonicalName, opts)
}
