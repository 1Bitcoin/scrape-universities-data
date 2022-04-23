package main.kotlin

import main.kotlin.datasource.setUniversityDataSource
import main.kotlin.datasource.setUniversityYGSNDataSource
import main.kotlin.generation.student.Generator
import main.kotlin.modeling.Modeller
import org.jetbrains.exposed.sql.Database
import java.io.File


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
    )

    //scrapeUniversityHSE(dataAboutUniversity)

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    startModeling(4000, 2020)

    //generateStudents()

    //val generator = Generator().generateStudent()

//    val helper = ModelingHelper()

//    helper.enrichStudentDataSet(limit = false)
//
//    for (item in helper.informationStudent) {
//        println(item.studentData)
//        println(item.ygsnList)
//        for (egeItem in item.egeList)
//            println("egeId: ${egeItem.egeId} score: ${egeItem.score}")
//        println()
//    }

//    val generator = Generator()
//    generator.generateStudent()

//    val executor = MyQueryExecutor()
//    executor.selectFullInformationStudent()

//    helper.enrichUniversityDataSet()
//
//    for (item in helper.informationUniversityMap2020) {
//        println("Регион: ${item.key}")
//        for (university in item.value)
//            println("Вузы: ${university.universityData}")
//        println()
//    }

    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

//    val test = mutableListOf<StudentData>()
//
//    for (i in 1..50000) {
//        test.add(StudentData())
//    }
//
//    println("list done")
//
//    val ans = batchInsertStudent(test)


//    for (an in ans) {
//        println(an[Student.id])
//    }
}

fun generateStudents() {
    val generator = Generator()
    generator.generateStudent()
}

fun startModeling(limitStudents: Int, modelingYear: Int) {
    val bufferedWriter = File("D:\\logs.txt").bufferedWriter()

    val modeller = Modeller(limitStudent = limitStudents, modelingYear, bufferedWriter)

    modeller.modeling()

    bufferedWriter.close()
}


