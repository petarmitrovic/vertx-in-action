package com.neperix.vertx

import io.vertx.core.Vertx

fun main() {

    var connectionCount = 0
    fun howMany(): String = "We now have $connectionCount connections"

    val vertx: Vertx = Vertx.vertx()
    vertx.createNetServer()
        .connectHandler { socket ->
            connectionCount++
            socket.handler { buffer ->
                socket.write(buffer)
                if (buffer.toString().endsWith("/quit\n")) {
                    socket.close()
                }
            }

            val remoteAddress = socket.remoteAddress()
            println("Established a new connection: $remoteAddress")

            socket.closeHandler { v -> connectionCount-- }
        }
        .listen(3000)

    vertx.setPeriodic(3000L) {
        println(howMany())
    }

    vertx.createHttpServer()
        .requestHandler { req -> req.response().end(howMany())}
        .listen(8080)

}
