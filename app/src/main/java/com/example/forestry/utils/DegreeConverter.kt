package com.example.forestry.utils

object DegreeConverter {
    fun toDecimalDegree(sexagesimalDegree: String): Double {
        val split = sexagesimalDegree.split(".")

        val rawDegArcmin = split[0]

        val rawDeg = rawDegArcmin.slice(0..(rawDegArcmin.length - 3))
        val deg = rawDeg.toDouble()

        val wholeArcmin = rawDegArcmin.substring(rawDegArcmin.length - 2)
        val decimalArcmin = split[1]
        val stringArcsec = "$wholeArcmin.$decimalArcmin"
        val arcmin = stringArcsec.toDouble()

        val result = deg + arcmin / 60
        return "%.5f".format(result).replace(",", ".").toDouble()
    }
}
