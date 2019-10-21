package com.neperix.vertxinaction.chapter04.parsetools

import io.vertx.core.Vertx
import io.vertx.core.file.OpenOptions

fun main() {
    val vertx = Vertx.vertx()
    val opts = OpenOptions().apply {
        setRead(true)
        setWrite(false)
    }
    vertx.fileSystem().open("build.gradle.kts", opts) { asyncResult ->
        if (asyncResult.succeeded()) {
            asyncResult.result()
                .handler { println(it) }
                .exceptionHandler { it.printStackTrace() }
                .endHandler {
                    println("\n--- DONE")
                    vertx.close()
                }

        } else {
            asyncResult.cause().printStackTrace()
        }
    }
}
