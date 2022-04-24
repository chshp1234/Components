package com.common.component

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val test1 = "test"
    val test2
        get() = "test2"

    @Test
    fun addition_isCorrect() {
        val name = Name("World")
        print(name)
    }


    @JvmInline
    value class Name(private val s: String) {
        init {
            println("Hello, $s")
        }

        private val length: Int
            get() = s.length


        fun greet(): String {
            return "$s,length=$length"
        }

        fun set(s: String) {

        }
    }

}