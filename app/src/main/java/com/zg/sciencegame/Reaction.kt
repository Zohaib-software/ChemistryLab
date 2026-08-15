package com.zg.sciencegame

data class Reaction(
    val reactants: List<String>,
    val products: List<String>,
    val formula: String,
    val description: String,
    val reactionType: ReactionType,
    val funFact: String? = null
)

enum class ReactionType {
    IONIC_BONDING,
    COVALENT_BONDING,
    ACID_BASE,
    COMBUSTION,
    OXIDATION,
    NO_REACTION,
    EXPLOSIVE,
    PRECIPITATION
}

