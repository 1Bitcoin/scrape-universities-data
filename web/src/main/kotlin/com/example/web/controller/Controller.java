package com.example.web.controller;

import com.example.web.model.AjaxResponseBody;
import com.example.web.service.ModellerService;
import dto.contoller.Generating;
import dto.contoller.Modelling;
import main.kotlin.dto.ModellerLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class Controller {

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private final ModellerService service;

    public Controller(ModellerService service) {
        this.service = service;
    }

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
        LOGGER.info("DOCKER exits");
        return sseEmitter;
    }

    @PostMapping(value = "/logs")
    public void dispatchEventToClients(@RequestBody ModellerLog log) {
        for (SseEmitter sseEmitter : emitters) {
            try {
                sseEmitter.send(SseEmitter.event().name("message").data(log.getMessage()));
            } catch (IOException e) {
                e.printStackTrace();
                emitters.remove(sseEmitter);
            }
        }
    }

    @PostMapping(value = "/modelling")
    public ResponseEntity<AjaxResponseBody> startModelling(
            @RequestBody Modelling modellingDTO) {
        AjaxResponseBody result = new AjaxResponseBody();
        service.startModelling(modellingDTO, 0);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/generating")
    public ResponseEntity<AjaxResponseBody> startGenerateStudent(
            @RequestBody Generating generatingDTO
            ) {
        AjaxResponseBody result = new AjaxResponseBody();
        service.startGenerateStudent(generatingDTO, 0);
        return ResponseEntity.ok(result);
    }
}
