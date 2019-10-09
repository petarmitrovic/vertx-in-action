package com.neperix.vertxinaction.chapter02

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class SomeVerticle : AbstractVerticle() {

    override fun start(startPromise: Promise<Void>) {
        vertx.createHttpServer()
            .requestHandler { req -> req.response().end("OK") }
            .listen(8080) {
                if (it.succeeded()) {
                    startPromise.complete()
                } else {
                    startPromise.fail(it.cause())
                }
            }
    }
}
