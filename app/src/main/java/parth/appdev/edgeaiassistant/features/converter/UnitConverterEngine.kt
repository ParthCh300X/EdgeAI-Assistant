package parth.appdev.edgeaiassistant.features.converter

object UnitConverterEngine {

    fun convert(value: Double, from: String, to: String): Double? {

        // 🔷 LENGTH
        if (from == "km" && to == "m") return value * 1000
        if (from == "m" && to == "km") return value / 1000
        if (from == "m" && to == "cm") return value * 100
        if (from == "cm" && to == "m") return value / 100

        // 🔷 MASS
        if (from == "kg" && to == "g") return value * 1000
        if (from == "g" && to == "kg") return value / 1000

        // 🔷 TEMPERATURE
        if (from == "c" && to == "f") return (value * 9 / 5) + 32
        if (from == "f" && to == "c") return (value - 32) * 5 / 9

        return null
    }
}