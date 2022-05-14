package com.example.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class Controller {

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    @GetMapping("/sse-endpoint-address")
    @CrossOrigin
    public SseEmitter streamDateTime() {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> {
            LOGGER.info("SseEmitter is completed");
            emitters.remove(sseEmitter);
        });

        sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        emitters.add(sseEmitter);

        LOGGER.info("Controller exits");
        return sseEmitter;
    }

    @PostMapping(value = "/logs")
    public void dispatchEventToClients(@RequestBody String log) {
        for (SseEmitter sseEmitter : emitters) {
            try {
                sseEmitter.send(SseEmitter.event().name("message").data(log));
            } catch (IOException e) {
                e.printStackTrace();
                emitters.remove(sseEmitter);
            }
        }
    }
}
