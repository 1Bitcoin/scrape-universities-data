package com.example.web.controller

import com.example.web.service.ModellerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class MvcController(val modellerService: ModellerService) {

    var logs = StringBuilder()

    @Value("\${spring.application.name}")
    var appName: String? = null


    @GetMapping("/")
    fun homePage(model: Model): String {
        model.addAttribute("request", true)

        return "index"
    }

    @RequestMapping(value= ["/start"])
    fun start(model: Model): String {
        modellerService.startModelling()

        // Прекращаем опрос в js коде
        model.addAttribute("request", false)

        return "index"
    }

    @RequestMapping(value= ["/message"])
    fun getMessages(model: Model): String {
        val message = modellerService.getMessages()
        logs.append(message)
        model.addAttribute("message", logs)

        return "index"
    }
}