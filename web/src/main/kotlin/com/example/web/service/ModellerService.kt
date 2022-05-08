package com.example.web.service

import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter


@Service
class ModellerService {
    final var FILE_PATH = "D:\\logs.txt"

    fun startModelling() {
        //main()
    }

    fun getMessages(): String {
        val bufferedReader = File(FILE_PATH).bufferedReader()
        val message = bufferedReader.readText()
        bufferedReader.close()

        clearLogsFile()

        return message
    }

    fun clearLogsFile() {
        FileWriter(FILE_PATH, false).close()
    }
}