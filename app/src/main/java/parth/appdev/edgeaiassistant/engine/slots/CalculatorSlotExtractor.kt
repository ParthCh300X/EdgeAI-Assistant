package parth.appdev.edgeaiassistant.engine.slots

import parth.appdev.edgeaiassistant.domain.model.CalculatorSlots

class CalculatorSlotExtractor {

    fun extract(input: String): CalculatorSlots {
        return CalculatorSlots(
            expression = input.replace("calculate", "", ignoreCase = true).trim()
        )
    }
}