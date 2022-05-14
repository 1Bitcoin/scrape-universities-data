package com.example.web.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class MvcController() {

    @Value("\${spring.application.name}")
    var appName: String? = null


    @GetMapping("/")
    fun homePage(model: Model): String {
        return "index"
    }
}