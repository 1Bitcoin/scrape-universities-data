package com.example.web.service

import main.kotlin.main
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter


@Service
class ModellerService {
    final var FILE_PATH = "D:\\logs.txt"

    fun startModelling(logToggle: Int) {
        main("modelling", logToggle)
    }

    fun startGenerateStudent(logToggle: Int) {
        main("generating", logToggle)
    }
}