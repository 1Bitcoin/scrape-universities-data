package com.example.web.service

import dto.contoller.Generating
import dto.contoller.Modelling
import main.kotlin.Modeller
import org.springframework.stereotype.Service


@Service
class ModellerService {
    final var FILE_PATH = "D:\\logs.txt"
    val modeller = Modeller()

    fun startModelling(modellingDTO: Modelling, logToggle: Int) {
        modeller.startModeling(modellingDTO, logToggle)
    }

    fun startGenerateStudent(generatingDTO: Generating) {
        modeller.generateStudents(generatingDTO)
    }

    fun deleteStudents() {
        modeller.deleteStudents();
    }
}