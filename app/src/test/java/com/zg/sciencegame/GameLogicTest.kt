package com.zg.sciencegame

import org.junit.Assert.*
import org.junit.Test

class GameLogicTest {

    @Test
    fun `ionic reactions resolve with correct products`() {
        val ionicReactions = ScienceData.reactions.filter { it.reactionType == ReactionType.IONIC_BONDING }
        ionicReactions.forEach { reaction ->
            val result = ScienceData.findReaction(reaction.reactants)
            assertNotNull("No ionic reaction found for ${reaction.reactants}", result)
            assertEquals("Products mismatch for ${reaction.reactants}", reaction.products, result?.products)
            assertEquals("Type mismatch for ${reaction.reactants}", ReactionType.IONIC_BONDING, result?.reactionType)
            assertTrue("Ionic reaction should have at least one product for ${reaction.reactants}", result?.products?.isNotEmpty() == true)
        }
    }

    @Test
    fun `acid base reaction returns salt and water`() {
        val reaction = ScienceData.findReaction(listOf("Sodium", "Hydrogen", "Oxygen", "Chlorine"))

        assertNotNull(reaction)
        assertEquals(ReactionType.ACID_BASE, reaction?.reactionType)
        assertTrue(reaction?.products?.contains("Sodium Chloride") == true)
        assertTrue(reaction?.products?.contains("Water") == true)
        assertEquals(2, reaction?.products?.size)
    }

    @Test
    fun `noble gases produce no reaction`() {
        val reaction = ScienceData.findReaction(listOf("Helium", "Oxygen"))

        assertNotNull(reaction)
        assertEquals(ReactionType.NO_REACTION, reaction?.reactionType)
        assertTrue(reaction?.products?.isEmpty() == true)
    }

    @Test
    fun `explosive reaction sodium plus water produces hydroxide and hydrogen`() {
        val reaction = ScienceData.findReaction(listOf("Sodium", "Water"))

        assertNotNull(reaction)
        assertEquals(ReactionType.EXPLOSIVE, reaction?.reactionType)
        assertTrue(reaction?.products?.contains("Sodium Hydroxide") == true)
        assertTrue(reaction?.products?.contains("Hydrogen") == true)
    }

    @Test
    fun `requires exact reactants when duplicates are present`() {
        val reaction = ScienceData.findReaction(listOf("Sodium", "Chlorine", "Chlorine"))

        assertNull(reaction)
    }
}

