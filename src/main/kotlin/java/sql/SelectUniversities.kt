package java.sql

fun selectUniversities(connection: Connection) {
    connection.use { conn ->
        val prepareStatement = connection.prepareStatement("select * from university where id = 3148")

        val resultSet = prepareStatement.executeQuery()

        resultSet.use {
            while (it.next())
                println(it.getString("jsonygsn"))

        }
    }
}