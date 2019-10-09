package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

class Deployer : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(Deployer::class.java)

    override fun start() {
        val delay = 1000L
        for (i in 1..50) {
            vertx.setTimer(i * delay) {
                deploy()
            }
        }
    }

    private fun deploy() {
        vertx.deployVerticle(EmptyVerticle()) {
            if (it.succeeded()) {
                val id = it.result()
                logger.info("Successfully deployed $id")
                vertx.setTimer(3000L) {
                    undeploy(id)
                }
            } else {
                logger.error("Error while deploying", it.cause())
            }
        }
    }

    private fun undeploy(id: String) {
        vertx.undeploy(id) {
            if (it.succeeded()) {
                logger.info("Successfully undeployed {}", id)
            } else {
                logger.error("Could not undeploy $id", it.cause())
            }
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(Deployer())
}
