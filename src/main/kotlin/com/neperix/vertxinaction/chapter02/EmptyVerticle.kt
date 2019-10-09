package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import org.slf4j.LoggerFactory

class EmptyVerticle : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(EmptyVerticle::class.java)

    override fun start() {
        logger.info("Start {}", this.deploymentID())
    }

    override fun stop() {
        logger.info("Stop {}", this.deploymentID())
    }
}
