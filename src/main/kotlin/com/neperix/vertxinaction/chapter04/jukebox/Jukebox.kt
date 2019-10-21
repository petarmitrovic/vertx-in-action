package com.neperix.vertxinaction.chapter04.jukebox

import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.core.file.AsyncFile
import io.vertx.core.file.OpenOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

enum class State {
    Paused,
    Playing
}

class Jukebox : AbstractVerticle() {

    val logger
        get() = LoggerFactory.getLogger(Jukebox::class.java)

    var currentMode = State.Paused
    val playlist = ArrayDeque<String>()
    val streamers = mutableSetOf<HttpServerResponse>()

    var currentFile: AsyncFile? = null
    var positionInFile: Long = 0

    override fun start() {
        val eventBus = vertx.eventBus()
        eventBus.consumer("jukebox.list", this::list)
        eventBus.consumer("jukebox.schedule", this::schedule)
        eventBus.consumer("jukebox.play", this::play)
        eventBus.consumer("jukebox.pause", this::pause)

        vertx.createHttpServer()
            .requestHandler(this::httpHandler)
            .listen(8080)

        vertx.setPeriodic(100L) {
            streamAudioChunk()
        }
    }

    private fun schedule(request: Message<JsonObject>) {
        val file = request.body().getString("file")
        if (playlist.isEmpty() && currentMode == State.Paused) {
            currentMode = State.Playing
        }
        playlist.offer(file)
    }

    private fun play(request: Message<Any>) {
        currentMode = State.Playing
    }

    private fun pause(request: Message<Any>) {
        currentMode = State.Paused
    }

    private fun streamAudioChunk() {
        if (currentMode == State.Paused) {
            logger.info("Starting the player...")
            currentMode = State.Playing
            return
        }

        if (currentFile == null && playlist.isEmpty()) {
            currentMode = State.Paused
            return
        }

        if (currentFile == null) {
            logger.info("Current file is not set...")
            openNextFile()
        }

        currentFile?.read(Buffer.buffer(4096), 0, positionInFile, 4096) {
            if (it.succeeded()) {
                processReadBuffer(it.result())
            } else {
                logger.error("Read failed", it.cause())
                closeCurrentFile()
            }
        }
    }

    private fun processReadBuffer(buffer: Buffer) {
        if (buffer.length() == 0) {
            closeCurrentFile()
            return
        }
        positionInFile += buffer.length()
        streamers.forEach {
            if (!it.writeQueueFull()) {
                it.write(buffer.copy())
            }
        }
    }

    private fun openNextFile() {
        val nextFile = playlist.poll()
        logger.info("Opening next file $nextFile")
        currentFile = vertx.fileSystem()
            .openBlocking("tracks/$nextFile", OpenOptions().setRead(true))
        positionInFile = 0
    }

    private fun closeCurrentFile() {

        logger.info("Closing current file $currentFile")

        positionInFile = 0
        currentFile?.close()
        currentFile = null
    }

    private fun list(request: Message<Any>) {
        vertx.fileSystem().readDir("tracks", ".+\\.mp3") { asyncResult ->
            if (asyncResult.succeeded()) {
                val files = asyncResult.result()
                    .map { filename -> File(filename) }
                    .map { file -> file.name }
                request.reply(JsonObject(mapOf("files" to JsonArray(files))))
            } else {
                logger.error("Reading the tracks directory failed", asyncResult.cause())
                request.fail(500, asyncResult.cause().message)
            }
        }
    }

    private fun httpHandler(request: HttpServerRequest) {
        if ("/".equals(request.path())) {
            openAudioStream(request)
        } else if (request.path().startsWith("/download/")) {
            val sanitizedPath = request.path().substring(10).replace("/", "")
            download(sanitizedPath, request.response())
        } else {
            request.response().setStatusCode(404).end()
        }
    }

    private fun openAudioStream(request: HttpServerRequest) {
        val response = request.response()
            .putHeader("Content-Type", "audio/mpeg")
            .setChunked(true)

        streamers.add(response)
        response.endHandler {
            streamers.remove(response)
            logger.info("A streamer {} left", request.remoteAddress())
        }
    }

    private fun download(path: String, response: HttpServerResponse) {
        val file = "tracks/$path"
        if (!vertx.fileSystem().existsBlocking(file)) {
            response.setStatusCode(404).end()
            return
        }

        val opts = OpenOptions().setRead(true)
        vertx.fileSystem().open(file, opts) {
            if (it.succeeded()) {
                downloadFile(it.result(), response)
            } else {
                logger.error("Read failed", it.cause())
            }
        }
    }

    private fun downloadFile(file: AsyncFile, response: HttpServerResponse) {
        response.setStatusCode(200)
            .putHeader("Content-Type", "audio/mpeg")
            .setChunked(true)

        file
            .handler { buffer ->
                response.write(buffer)
                if (response.writeQueueFull()) {
                    file.pause()
                    file.drainHandler { file.resume() }
                }
            }
            .endHandler {
                response.end()
            }
    }
}
