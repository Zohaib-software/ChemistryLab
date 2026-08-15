package com.zg.sciencegame

import android.content.Context
import com.zg.sciencegame.R

object ScienceData {
    
    val elements = listOf(
        Element("Hydrogen", "H", 1, "Group 1", ElementCategory.NON_METAL,
            "The lightest and most abundant element in the universe. Essential for water formation."),
        Element("Helium", "He", 2, "Group 18", ElementCategory.NOBLE_GAS,
            "A noble gas that doesn't react with other elements. Used in balloons and cooling systems."),
        Element("Lithium", "Li", 3, "Group 1", ElementCategory.ALKALI_METAL,
            "The lightest metal. Highly reactive and used in batteries."),
        Element("Carbon", "C", 6, "Group 14", ElementCategory.NON_METAL,
            "The basis of all organic life. Forms millions of compounds."),
        Element("Nitrogen", "N", 7, "Group 15", ElementCategory.NON_METAL,
            "Makes up 78% of Earth's atmosphere. Essential for proteins and DNA."),
        Element("Oxygen", "O", 8, "Group 16", ElementCategory.NON_METAL,
            "Essential for respiration and combustion. Makes up 21% of air."),
        Element("Fluorine", "F", 9, "Group 17", ElementCategory.HALOGEN,
            "The most reactive element. Used in toothpaste and Teflon."),
        Element("Neon", "Ne", 10, "Group 18", ElementCategory.NOBLE_GAS,
            "Noble gas used in neon signs. Very stable and non-reactive."),
        Element("Sodium", "Na", 11, "Group 1", ElementCategory.ALKALI_METAL,
            "Highly reactive alkali metal. Essential for nerve function and table salt."),
        Element("Magnesium", "Mg", 12, "Group 2", ElementCategory.ALKALINE_EARTH_METAL,
            "Lightweight metal that burns with bright white flame. Used in alloys."),
        Element("Aluminum", "Al", 13, "Group 13", ElementCategory.OTHER_METAL,
            "Lightweight, corrosion-resistant metal. Most abundant metal in Earth's crust."),
        Element("Silicon", "Si", 14, "Group 14", ElementCategory.METALLOID,
            "Essential for computer chips and glass. Second most abundant element in Earth's crust."),
        Element("Phosphorus", "P", 15, "Group 15", ElementCategory.NON_METAL,
            "Essential for DNA, RNA, and ATP. Used in matches and fertilizers."),
        Element("Sulfur", "S", 16, "Group 16", ElementCategory.NON_METAL,
            "Yellow solid with distinct smell. Used in gunpowder and rubber production."),
        Element("Chlorine", "Cl", 17, "Group 17", ElementCategory.HALOGEN,
            "Greenish-yellow gas. Used in water treatment and PVC production."),
        Element("Potassium", "K", 19, "Group 1", ElementCategory.ALKALI_METAL,
            "Highly reactive alkali metal. Essential for plant growth and nerve function."),
        Element("Calcium", "Ca", 20, "Group 2", ElementCategory.ALKALINE_EARTH_METAL,
            "Essential for bones and teeth. Found in limestone and chalk."),
        Element("Iron", "Fe", 26, "Transition", ElementCategory.TRANSITION_METAL,
            "Most important metal for construction. Core of Earth is mostly iron."),
        Element("Copper", "Cu", 29, "Transition", ElementCategory.TRANSITION_METAL,
            "Excellent conductor of electricity. Used in wiring and coins."),
        Element("Zinc", "Zn", 30, "Transition", ElementCategory.TRANSITION_METAL,
            "Used in galvanization to prevent rust. Essential trace element."),
        Element("Silver", "Ag", 47, "Transition", ElementCategory.TRANSITION_METAL,
            "Best electrical conductor. Used in jewelry and photography."),
        Element("Gold", "Au", 79, "Transition", ElementCategory.TRANSITION_METAL,
            "Precious metal that doesn't tarnish. Used in jewelry and electronics.")
    )
    
    val reactions = listOf(
        Reaction(
            listOf("Sodium", "Chlorine"),
            listOf("Sodium Chloride"),
            "2Na + Cl₂ → 2NaCl",
            "Ionic bonding occurs when sodium (a metal) transfers an electron to chlorine (a non-metal), forming table salt. This is an exothermic reaction that releases energy.",
            ReactionType.IONIC_BONDING,
            "Table salt (NaCl) is essential for life and has been used as a preservative for thousands of years!"
        ),
        Reaction(
            listOf("Potassium", "Chlorine"),
            listOf("Potassium Chloride"),
            "2K + Cl₂ → 2KCl",
            "Potassium and chlorine form potassium chloride through ionic bonding. This compound is used in fertilizers and as a salt substitute.",
            ReactionType.IONIC_BONDING
        ),
        Reaction(
            listOf("Sodium", "Fluorine"),
            listOf("Sodium Fluoride"),
            "2Na + F₂ → 2NaF",
            "Sodium fluoride forms through ionic bonding. It's added to toothpaste and water to prevent tooth decay.",
            ReactionType.IONIC_BONDING
        ),
        Reaction(
            listOf("Calcium", "Chlorine"),
            listOf("Calcium Chloride"),
            "Ca + Cl₂ → CaCl₂",
            "Calcium chloride forms through ionic bonding. It's used as a drying agent and to melt ice on roads.",
            ReactionType.IONIC_BONDING
        ),
        Reaction(
            listOf("Hydrogen", "Oxygen"),
            listOf("Water"),
            "2H₂ + O₂ → 2H₂O",
            "Hydrogen and oxygen combine to form water through covalent bonding. This is a highly exothermic reaction that releases a lot of energy.",
            ReactionType.COMBUSTION,
            "Water covers 71% of Earth's surface and is essential for all known forms of life!"
        ),
        Reaction(
            listOf("Hydrogen", "Chlorine"),
            listOf("Hydrogen Chloride"),
            "H₂ + Cl₂ → 2HCl",
            "Hydrogen and chlorine form hydrogen chloride gas, which dissolves in water to form hydrochloric acid.",
            ReactionType.COVALENT_BONDING
        ),
        Reaction(
            listOf("Carbon", "Oxygen"),
            listOf("Carbon Dioxide"),
            "C + O₂ → CO₂",
            "Carbon burns in oxygen to form carbon dioxide. This is a combustion reaction that releases energy.",
            ReactionType.COMBUSTION,
            "Carbon dioxide is essential for photosynthesis, but excess CO₂ contributes to climate change."
        ),
        Reaction(
            listOf("Hydrogen", "Nitrogen"),
            listOf("Ammonia"),
            "3H₂ + N₂ → 2NH₃",
            "Hydrogen and nitrogen combine to form ammonia through covalent bonding. This reaction requires high pressure and temperature (Haber process).",
            ReactionType.COVALENT_BONDING,
            "Ammonia is crucial for fertilizer production, feeding billions of people worldwide!"
        ),
        
        Reaction(
            listOf("Sodium", "Hydrogen", "Oxygen", "Chlorine"),
            listOf("Sodium Chloride", "Water"),
            "NaOH + HCl → NaCl + H₂O",
            "Sodium hydroxide (a base) reacts with hydrochloric acid to form salt and water. This is a neutralization reaction.",
            ReactionType.ACID_BASE,
            "Neutralization reactions are used in antacids to relieve heartburn!"
        ),
        Reaction(
            listOf("Hydrogen", "Sulfur"),
            listOf("Hydrogen Sulfide"),
            "H₂ + S → H₂S",
            "Hydrogen and sulfur combine to form hydrogen sulfide, a toxic gas with a rotten egg smell.",
            ReactionType.COVALENT_BONDING
        ),
        Reaction(
            listOf("Sodium", "Oxygen"),
            listOf("Sodium Oxide"),
            "4Na + O₂ → 2Na₂O",
            "Sodium reacts with oxygen to form sodium oxide. This reaction is highly exothermic.",
            ReactionType.OXIDATION
        ),
        Reaction(
            listOf("Calcium", "Oxygen"),
            listOf("Calcium Oxide"),
            "2Ca + O₂ → 2CaO",
            "Calcium burns in oxygen to form calcium oxide (quicklime), which is used in cement production.",
            ReactionType.COMBUSTION
        ),
        Reaction(
            listOf("Zinc", "Chlorine"),
            listOf("Zinc Chloride"),
            "Zn + Cl₂ → ZnCl₂",
            "Zinc reacts with chlorine to form zinc chloride, used in deodorants and soldering flux.",
            ReactionType.IONIC_BONDING
        ),
        Reaction(
            listOf("Aluminum", "Oxygen"),
            listOf("Aluminum Oxide"),
            "4Al + 3O₂ → 2Al₂O₃",
            "Aluminum reacts with oxygen to form aluminum oxide, creating a protective layer that prevents further corrosion.",
            ReactionType.OXIDATION,
            "Aluminum's protective oxide layer makes it corrosion-resistant!"
        ),
        Reaction(
            listOf("Lithium", "Oxygen"),
            listOf("Lithium Oxide"),
            "4Li + O₂ → 2Li₂O",
            "Lithium reacts with oxygen to form lithium oxide. Lithium is used in rechargeable batteries.",
            ReactionType.OXIDATION
        ),
        Reaction(
            listOf("Iron", "Oxygen"),
            listOf("Iron Oxide (Rust)"),
            "4Fe + 3O₂ → 2Fe₂O₃",
            "Iron reacts with oxygen in the presence of water to form rust (iron oxide). This is an oxidation reaction that weakens the metal.",
            ReactionType.OXIDATION,
            "Rust costs billions of dollars in damage each year, but it's also used as a pigment in paints!"
        ),
        Reaction(
            listOf("Copper", "Oxygen"),
            listOf("Copper Oxide"),
            "2Cu + O₂ → 2CuO",
            "Copper reacts with oxygen to form copper oxide, giving copper its characteristic green patina over time.",
            ReactionType.OXIDATION
        ),
        Reaction(
            listOf("Magnesium", "Oxygen"),
            listOf("Magnesium Oxide"),
            "2Mg + O₂ → 2MgO",
            "Magnesium burns brightly in oxygen to form magnesium oxide. This reaction produces intense white light.",
            ReactionType.COMBUSTION,
            "Magnesium flares are used in emergency signals because of this bright reaction!"
        ),
        Reaction(
            listOf("Potassium", "Water"),
            listOf("Potassium Hydroxide", "Hydrogen"),
            "2K + 2H₂O → 2KOH + H₂",
            "Potassium reacts violently with water, producing hydrogen gas and heat. This is a highly dangerous reaction!",
            ReactionType.EXPLOSIVE,
            "Alkali metals like potassium are stored in oil to prevent contact with water!"
        ),
        Reaction(
            listOf("Sodium", "Water"),
            listOf("Sodium Hydroxide", "Hydrogen"),
            "2Na + 2H₂O → 2NaOH + H₂",
            "Sodium reacts vigorously with water, producing hydrogen gas and sodium hydroxide. The reaction is exothermic and can ignite the hydrogen.",
            ReactionType.EXPLOSIVE
        ),
        
        Reaction(
            listOf("Carbon", "Hydrogen"),
            listOf("Methane"),
            "C + 2H₂ → CH₄",
            "Carbon and hydrogen combine to form methane, the simplest hydrocarbon and a major component of natural gas.",
            ReactionType.COVALENT_BONDING,
            "Methane is a potent greenhouse gas, but also a valuable fuel source!"
        ),
        Reaction(
            listOf("Sulfur", "Oxygen"),
            listOf("Sulfur Dioxide"),
            "S + O₂ → SO₂",
            "Sulfur burns in oxygen to form sulfur dioxide, a gas with a pungent smell that contributes to acid rain.",
            ReactionType.COMBUSTION
        ),
        Reaction(
            listOf("Phosphorus", "Oxygen"),
            listOf("Phosphorus Pentoxide"),
            "4P + 5O₂ → 2P₂O₅",
            "Phosphorus burns brightly in oxygen to form phosphorus pentoxide, producing white smoke.",
            ReactionType.COMBUSTION,
            "White phosphorus was used in early matches because it ignites easily!"
        ),
        Reaction(
            listOf("Helium", "Oxygen"),
            listOf(),
            "No Reaction",
            "Helium is a noble gas with a full outer electron shell, so it doesn't form compounds with other elements.",
            ReactionType.NO_REACTION
        ),
        Reaction(
            listOf("Neon", "Chlorine"),
            listOf(),
            "No Reaction",
            "Neon is a noble gas and is chemically inert. It doesn't react with other elements.",
            ReactionType.NO_REACTION
        ),
        Reaction(
            listOf("Gold", "Oxygen"),
            listOf(),
            "No Reaction",
            "Gold is highly unreactive and doesn't tarnish or corrode. This is why it's called a 'noble metal'.",
            ReactionType.NO_REACTION,
            "Gold's resistance to corrosion is why it's been valued for thousands of years!"
        ),
        Reaction(
            listOf("Silver", "Oxygen"),
            listOf(),
            "No Reaction",
            "Pure silver doesn't react with oxygen at room temperature, though it can tarnish with sulfur compounds.",
            ReactionType.NO_REACTION
        )
    )
    
    fun findReaction(elements: List<String>): Reaction? {
        val sortedElements = elements.sorted()
        
        reactions.forEach { reaction ->
            val sortedReactants = reaction.reactants.sorted()
            if (sortedReactants == sortedElements) {
                return reaction
            }
        }
        
        if (sortedElements.size == 4 && sortedElements.containsAll(listOf("Sodium", "Oxygen", "Hydrogen", "Chlorine"))) {
            val acidBaseReaction = reactions.find { it.reactionType == ReactionType.ACID_BASE && 
                it.reactants.contains("Sodium") }
            if (acidBaseReaction != null) {
                val sortedReactionReactants = acidBaseReaction.reactants.sorted()
                if (sortedReactionReactants == sortedElements) {
                    return acidBaseReaction
                }
            }
        }
        
        return null
    }
    
    fun getElementByName(name: String): Element? {
        return elements.find { it.name == name }
    }
}

