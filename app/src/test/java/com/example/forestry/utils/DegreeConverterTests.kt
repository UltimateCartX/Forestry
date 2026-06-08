package com.example.forestry.utils

import org.junit.Test
import org.junit.Assert.assertEquals

class DegreeConverterTests {

    @Test
    fun sexagesimalToDecimal_onlydecimal() {
        val degree = "0300.0"
        val expectedDegree = 3.0
        val actualDegree = DegreeConverter.toDecimalDegree(degree)
        assertEquals(expectedDegree, actualDegree, 0.0)
    }

    @Test
    fun sexagesimalToDecimal_decimalminute() {
        val degree = "3020.0"
        val expectedDegree = 30.33333
        val actualDegree = DegreeConverter.toDecimalDegree(degree)
        assertEquals(expectedDegree, actualDegree, 0.0)
    }

    @Test
    fun sexagesimalToDecimal_decimalfractionnalminute() {
        val degree = "3020.4"
        val expectedDegree = 30.34
        val actualDegree = DegreeConverter.toDecimalDegree(degree)
        assertEquals(expectedDegree, actualDegree, 0.0)
    }
}