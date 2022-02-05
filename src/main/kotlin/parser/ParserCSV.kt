package parser

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

fun parseCSVFileWithDolyaYGSN(): MutableMap<String, Map<String, Double>> {
    val tsvReader = csvReader {
        charset = "windows-1251"
    }

    val map: MutableMap<String, Map<String, Double>> = mutableMapOf()

    tsvReader.open("DolyaYGSN.csv") {
        var elem = readNext()

        // Цикл по каждой строке файла
        while (!elem.isNullOrEmpty()) {
            val refactoredElem = elem
                .toString()
                .replace("\"", "")
                .replace("[", "")
                .replace("]", "")

            // Убрали ненужные символы и получили массив с разделенными элементами
            val newArray = refactoredElem.split("|").toTypedArray()

            val nameYGSN = newArray[0]
            val dolyaYGSNMap = mutableMapOf<String, Double>()

            // Цикл по элементам вида 24:0.67, где 24 - номер УГСН, а 0.67 - доля этого УГСН в данной группе
            for (item in newArray.drop(1)) {
                val dolyaYGSN = item.split(":").toTypedArray()

                val numberYGSN = dolyaYGSN[0]
                val valueDolya = dolyaYGSN[1].toDouble()
                dolyaYGSNMap.put(numberYGSN, valueDolya)

            }
            map.put(nameYGSN, dolyaYGSNMap)

            elem = readNext()
        }
    }

//    for (item in map) {
//        if (item.value.containsKey("24")) {
//            println("Есть, значение: ${item.value["24"]}")
//        }
//    }

    return map
}