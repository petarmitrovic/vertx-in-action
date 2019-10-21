package com.neperix.vertxinaction.chapter04.jukebox

import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.NetSocket
import io.vertx.core.parsetools.RecordParser
import org.slf4j.LoggerFactory

class NetControl : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(NetControl::class.java)

    override fun start() {
        vertx.createNetServer()
            .connectHandler(this::handleClient)
            .listen(3000)
    }

    private fun handleClient(socket: NetSocket) {
        RecordParser.newDelimited("\n", socket)
            .handler { buffer -> handleBuffer(socket, buffer) }
            .endHandler { logger.info("Connection ended") }
    }

    private fun handleBuffer(socket: NetSocket, buffer: Buffer) {
        val command = buffer.toString()
        when (command) {
            "/list" -> listCommand(socket)
            "/play" -> vertx.eventBus().send("jukebox.play", "")
            "/pause" -> vertx.eventBus().send("jukebox.pause", "")
            else -> {
                if (command.startsWith("/schedule")) {
                    schedule(command)
                } else {
                    socket.write("Unknown command")
                }
            }
        }
    }

    private fun listCommand(socket: NetSocket) {
        vertx.eventBus().request<JsonObject>("jukebox.list", "") {
            if (it.succeeded()) {
                it.result().body().getJsonArray("files")
                    .stream()
                    .forEach { name -> socket.write("$name\n") }
            } else {
                logger.error("/list error", it.cause())
            }
        }
    }

    private fun schedule(command: String) {
        vertx.eventBus().send("jukebox.schedule", JsonObject().put("file", command.substring(10)))
    }
}
