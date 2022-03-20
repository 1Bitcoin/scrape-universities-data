package datasource

import datasource.vo.DataAboutUniversity

fun setUniversityDataSource(): DataAboutUniversity {
    return DataAboutUniversity(
        mapOf(
            2020 to Pair("hse-universities/БюджетВУЗ2020.html", "hse-universities/ПлаткаВУЗ2020.html"),
            2019 to Pair("hse-universities/БюджетВУЗ2019.html", "hse-universities/ПлаткаВУЗ2019.html"),
            2018 to Pair("hse-universities/БюджетВУЗ2018.html", "hse-universities/ПлаткаВУЗ2018.html")
        ),

        mutableListOf(
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=1",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=2",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=4",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=3",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=25",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=5",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=6",
            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=7"
        )
    )
}