import kotlin.test.Test
import kotlin.test.assertEquals

internal class Test {

    @Test
    fun testSum() {
        println(6 * 80 / 100)
    }

    @Test
    fun testDuration() {
        val duration = "70-81"

        val elem = duration.split("-")

        val aa = elem[0].toInt()
        val bb = elem[1].toInt()

        val range = aa..bb
        println(range.random())
    }
}
