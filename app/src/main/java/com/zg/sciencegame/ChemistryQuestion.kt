package com.zg.sciencegame

data class ChemistryQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int // Index of correct answer
)

object ChemistryQuestions {
    val questions = listOf(
        ChemistryQuestion(
            "What is the chemical formula for water?",
            listOf("H₂O", "CO₂", "NaCl", "O₂"),
            0
        ),
        ChemistryQuestion(
            "What is the chemical formula for table salt?",
            listOf("NaCl", "NaF", "KCl", "CaCl₂"),
            0
        ),
        ChemistryQuestion(
            "What is the chemical formula for carbon dioxide?",
            listOf("CO₂", "CO", "C₂O", "CaO"),
            0
        ),
        ChemistryQuestion(
            "What is the chemical formula for hydrogen chloride?",
            listOf("HCl", "H₂Cl", "HCl₂", "H₂O"),
            0
        ),
        ChemistryQuestion(
            "What type of bond forms between Sodium and Chlorine?",
            listOf("Ionic", "Covalent", "Metallic", "Hydrogen"),
            0
        ),
        ChemistryQuestion(
            "What type of bond forms between Hydrogen and Oxygen in water?",
            listOf("Covalent", "Ionic", "Metallic", "Hydrogen"),
            0
        ),
        ChemistryQuestion(
            "Ionic bonds typically form between:",
            listOf("Metals and Non-metals", "Two Non-metals", "Two Metals", "Noble Gases"),
            0
        ),
        ChemistryQuestion(
            "What is the symbol for Sodium?",
            listOf("Na", "So", "Sd", "Sn"),
            0
        ),
        ChemistryQuestion(
            "What is the symbol for Carbon?",
            listOf("C", "Ca", "Co", "Cr"),
            0
        ),
        ChemistryQuestion(
            "What is the symbol for Potassium?",
            listOf("K", "P", "Po", "Pt"),
            0
        ),
        ChemistryQuestion(
            "What is the symbol for Iron?",
            listOf("Fe", "Ir", "I", "In"),
            0
        ),
        ChemistryQuestion(
            "What is the atomic number of Oxygen?",
            listOf("8", "6", "16", "7"),
            0
        ),
        ChemistryQuestion(
            "What is the atomic number of Carbon?",
            listOf("6", "4", "12", "14"),
            0
        ),
        ChemistryQuestion(
            "What group are the alkali metals in?",
            listOf("Group 1", "Group 2", "Group 17", "Group 18"),
            0
        ),
        ChemistryQuestion(
            "What group are the halogens in?",
            listOf("Group 17", "Group 1", "Group 2", "Group 18"),
            0
        ),
        ChemistryQuestion(
            "What group are the noble gases in?",
            listOf("Group 18", "Group 1", "Group 2", "Group 17"),
            0
        ),
        ChemistryQuestion(
            "What type of reaction occurs when a metal burns in oxygen?",
            listOf("Combustion", "Ionic Bonding", "Covalent Bonding", "Precipitation"),
            0
        ),
        ChemistryQuestion(
            "What type of reaction is NaOH + HCl → NaCl + H₂O?",
            listOf("Acid-Base", "Combustion", "Oxidation", "Precipitation"),
            0
        ),
        ChemistryQuestion(
            "What happens when Hydrogen and Oxygen combine?",
            listOf("Forms Water", "Forms Salt", "No Reaction", "Forms Acid"),
            0
        ),
        ChemistryQuestion(
            "What is formed when Sodium and Chlorine react?",
            listOf("Sodium Chloride", "Sodium Oxide", "Chlorine Gas", "Water"),
            0
        ),
        ChemistryQuestion(
            "What gas makes up 78% of Earth's atmosphere?",
            listOf("Nitrogen", "Oxygen", "Carbon Dioxide", "Argon"),
            0
        ),
        ChemistryQuestion(
            "What gas makes up 21% of Earth's atmosphere?",
            listOf("Oxygen", "Nitrogen", "Carbon Dioxide", "Argon"),
            0
        ),
        ChemistryQuestion(
            "Which element is the lightest metal?",
            listOf("Lithium", "Sodium", "Hydrogen", "Helium"),
            0
        ),
        ChemistryQuestion(
            "What is the most reactive halogen?",
            listOf("Fluorine", "Chlorine", "Bromine", "Iodine"),
            0
        ),
        ChemistryQuestion(
            "Which noble gas is used in balloons?",
            listOf("Helium", "Neon", "Argon", "Xenon"),
            0
        ),
        ChemistryQuestion(
            "Which element is essential for all organic compounds?",
            listOf("Carbon", "Oxygen", "Hydrogen", "Nitrogen"),
            0
        ),
        ChemistryQuestion(
            "How many electrons can the first energy level hold?",
            listOf("2", "4", "6", "8"),
            0
        ),
        ChemistryQuestion(
            "A neutral atom has equal numbers of:",
            listOf("Protons and Electrons", "Protons and Neutrons", "Neutrons and Electrons", "All particles"),
            0
        ),
        ChemistryQuestion(
            "What is the charge of an electron?",
            listOf("Negative", "Positive", "Neutral", "Variable"),
            0
        )
    )
}

