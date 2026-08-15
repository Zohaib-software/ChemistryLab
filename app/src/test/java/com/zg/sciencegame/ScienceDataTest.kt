package com.zg.sciencegame

import org.junit.Assert.*
import org.junit.Test

class ScienceDataTest {

    @Test
    fun `all defined reactions resolve with correct products and type`() {
        ScienceData.reactions.forEach { reaction ->
            val result = ScienceData.findReaction(reaction.reactants)
            assertNotNull("No reaction found for ${reaction.reactants}", result)
            assertEquals("Products mismatch for ${reaction.reactants}", reaction.products, result?.products)
            assertEquals("Reaction type mismatch for ${reaction.reactants}", reaction.reactionType, result?.reactionType)
        }
    }

    @Test
    fun `reactions are order independent`() {
        ScienceData.reactions.forEach { reaction ->
            val shuffled = reaction.reactants.shuffled()
            val result = ScienceData.findReaction(shuffled)
            assertNotNull("No reaction found when reactants shuffled: $shuffled", result)
            assertEquals(reaction.products, result?.products)
            assertEquals(reaction.reactionType, result?.reactionType)
        }
    }

    @Test
    fun `reactions require exact reactants no extras`() {
        val inertExtras = listOf("Helium", "Neon", "Argon")
        ScienceData.reactions.forEach { reaction ->
            val extra = inertExtras.firstOrNull { !reaction.reactants.contains(it) } ?: "Helium"
            val result = ScienceData.findReaction(reaction.reactants + extra)
            assertNull("Expected no reaction when adding $extra to ${reaction.reactants}", result)
        }
    }

    @Test
    fun `unknown combinations return null`() {
        val reaction = ScienceData.findReaction(listOf("Unobtanium", "Kryptonite"))
        assertNull(reaction)
    }

    @Test
    fun `single element returns null`() {
        val reaction = ScienceData.findReaction(listOf("Sodium"))
        assertNull(reaction)
    }

    @Test
    fun `empty reactant list returns null`() {
        val reaction = ScienceData.findReaction(emptyList())
        assertNull(reaction)
    }

    @Test
    fun `all defined elements are retrievable by name`() {
        ScienceData.elements.forEach { element ->
            val found = ScienceData.getElementByName(element.name)
            assertNotNull("Expected to find element ${element.name}", found)
            assertEquals(element.symbol, found?.symbol)
            assertEquals(element.atomicNumber, found?.atomicNumber)
            assertEquals(element.category, found?.category)
        }
    }

    @Test
    fun `invalid element name returns null`() {
        val element = ScienceData.getElementByName("InvalidElement")
        assertNull(element)
    }

    @Test
    fun `element lookup is case sensitive`() {
        // Lower-case the first defined element to ensure case sensitivity
        val lower = ScienceData.elements.first().name.lowercase()
        val element = ScienceData.getElementByName(lower)
        assertNull(element)
    }

    @Test
    fun `acid base reactions include water and multiple products`() {
        val acidBaseReactions = ScienceData.reactions.filter { it.reactionType == ReactionType.ACID_BASE }
        acidBaseReactions.forEach { reaction ->
            assertTrue("Acid-base should include Water: ${reaction.reactants}", reaction.products.contains("Water"))
            assertTrue("Acid-base should have at least two products: ${reaction.reactants}", reaction.products.size >= 2)
        }
    }

    @Test
    fun `combustion reactions consume oxygen and yield oxides or water`() {
        val combustionReactions = ScienceData.reactions.filter { it.reactionType == ReactionType.COMBUSTION }
        combustionReactions.forEach { reaction ->
            assertTrue("Combustion must include Oxygen: ${reaction.reactants}", reaction.reactants.contains("Oxygen"))
            assertTrue(
                "Combustion should form oxide/water/CO2: ${reaction.reactants}",
                reaction.products.any {
                    it.contains("Oxide", ignoreCase = true) ||
                    it == "Water" ||
                    it == "Carbon Dioxide"
                }
            )
        }
    }

    @Test
    fun `oxidation reactions consume oxygen`() {
        val oxidationReactions = ScienceData.reactions.filter { it.reactionType == ReactionType.OXIDATION }
        oxidationReactions.forEach { reaction ->
            assertTrue("Oxidation must include Oxygen: ${reaction.reactants}", reaction.reactants.contains("Oxygen"))
        }
    }

    @Test
    fun `noble gases do not react and yield no products`() {
        val nobleGases = setOf("Helium", "Neon", "Argon")
        ScienceData.reactions.forEach { reaction ->
            if (reaction.reactants.any { it in nobleGases }) {
                assertEquals("Noble gas should yield no reaction: ${reaction.reactants}", ReactionType.NO_REACTION, reaction.reactionType)
                assertTrue("Noble gas reaction should have no products: ${reaction.reactants}", reaction.products.isEmpty())
            }
        }
    }
}

