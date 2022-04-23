package main.kotlin.datasource

import main.kotlin.datasource.vo.DataAboutUniversityYGSN

fun setUniversityYGSNDataSource(): DataAboutUniversityYGSN {
    return DataAboutUniversityYGSN(
        mapOf(
            2020 to Pair(
                "hse-ygsn/БюджетУГСН2020.html",
                "hse-ygsn/ПлаткаУГСН2020.html"
            ),

            2019 to Pair(
                "hse-ygsn/БюджетУГСН2019.html",
                "hse-ygsn/ПлаткаУГСН2019.html"
            ),

            2018 to Pair(
                "hse-ygsn/БюджетУГСН2018.html",
                "hse-ygsn/ПлаткаУГСН2018.html")
        )
    )
}