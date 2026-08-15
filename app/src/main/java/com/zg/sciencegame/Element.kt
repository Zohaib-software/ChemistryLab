package com.zg.sciencegame

data class Element(
    val name: String,
    val symbol: String,
    val atomicNumber: Int,
    val group: String,
    val category: ElementCategory,
    val description: String,
    val color: Int = R.color.slate_dark
)

enum class ElementCategory {
    ALKALI_METAL,
    HALOGEN,
    NOBLE_GAS,
    TRANSITION_METAL,
    NON_METAL,
    METALLOID,
    ALKALINE_EARTH_METAL,
    OTHER_METAL
}

