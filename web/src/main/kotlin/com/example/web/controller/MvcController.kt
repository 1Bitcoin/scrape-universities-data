package com.example.web.controller

import com.example.web.service.ModellerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class MvcController(val modellerService: ModellerService) {
    @Value("\${spring.application.name}")
    var appName: String? = null


    @GetMapping("/")
    fun homePage(model: Model): String {
        model.addAttribute("appName", appName)
        return "home"
    }

    @RequestMapping(value= ["/start"])
    fun start(model: Model): String {
        modellerService.startModelling()

        model.addAttribute("appName", appName)
        return "home"
    }
}