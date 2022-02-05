package java.sql

// Не работает
class QueryExecutor {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres",
        "postgres",
        "qwerty"
    )

    fun selectUniversities() {
        val prepareStatement = connection.prepareStatement("select * from university where id = 3148")

        val resultSet = prepareStatement.executeQuery()

        resultSet.use {
            while (it.next())
                println(it.getString("jsonygsn"))

        }
    }
}
