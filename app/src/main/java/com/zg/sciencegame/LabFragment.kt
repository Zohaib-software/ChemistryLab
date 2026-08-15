package com.zg.sciencegame

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zg.sciencegame.databinding.LabFragmentBinding

class LabFragment : Fragment() {

    private var _binding: LabFragmentBinding? = null
    private val binding get() = _binding!!

    private val selectedElements = mutableListOf<String>()
    private val maxElements = 4
    
    private var vibrator: Vibrator? = null
    private var soundManager: SoundManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LabFragmentBinding.inflate(inflater, container, false)

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        soundManager = SoundManager.getInstance(requireContext())

        setupElementButtons()
        binding.mixButton.setOnClickListener { mixElements() }
        binding.clearButton.setOnClickListener { clearSelection() }
        binding.infoButton.setOnClickListener { showElementInfoDialog() }
        binding.backToHomeButton.setOnClickListener {
            if (isAdded && view != null) {
                findNavController().navigateUp()
            }
        }

        updateSelectedElementsDisplay()

        return binding.root
    }
    
    private fun vibrateSuccess() {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 100, 50, 100)
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 100, 50, 100)
            vibrator?.vibrate(pattern, -1)
        }
    }
    
    private fun vibrateError() {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 50)
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val pattern = longArrayOf(0, 50, 50, 50)
            vibrator?.vibrate(pattern, -1)
        }
    }

    private val elementButtons = mutableMapOf<String, Button>()

    private fun setupElementButtons() {
        val grid = binding.elementGrid
        grid.removeAllViews()
        elementButtons.clear()

        ScienceData.elements.forEach { element ->
            val button = Button(requireContext(), null, 0, R.style.ElementButton).apply {
                text = "${element.name}\n${element.symbol}"
                setOnClickListener { 
                    toggleElement(element.name)
                    updateButtonAppearance(element.name)
                }
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )
                    setMargins(8, 8, 8, 8)
                }
                val categoryColor = ContextCompat.getColor(requireContext(), getCategoryColor(element.category))
                val drawable = GradientDrawable().apply {
                    setColor(categoryColor)
                    cornerRadius = 12f
                    setStroke(2, ContextCompat.getColor(requireContext(), R.color.white))
                }
                background = drawable
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_dark))
                elevation = 4f
                stateListAnimator = null
            }
            
            updateButtonAppearance(element.name)
            elementButtons[element.name] = button
            grid.addView(button)
        }
    }

    private fun getCategoryColor(category: ElementCategory): Int {
        return when (category) {
            ElementCategory.ALKALI_METAL -> R.color.alkali_metal
            ElementCategory.ALKALINE_EARTH_METAL -> R.color.alkaline_earth
            ElementCategory.HALOGEN -> R.color.halogen
            ElementCategory.NOBLE_GAS -> R.color.noble_gas
            ElementCategory.TRANSITION_METAL -> R.color.transition_metal
            ElementCategory.NON_METAL -> R.color.non_metal
            ElementCategory.METALLOID -> R.color.metalloid
            ElementCategory.OTHER_METAL -> R.color.other_metal
        }
    }

    private fun updateButtonAppearance(elementName: String) {
        val button = elementButtons[elementName] ?: return
        val element = ScienceData.getElementByName(elementName) ?: return
        val isSelected = selectedElements.contains(elementName)
        
        val backgroundColorRes = if (isSelected) {
            R.color.maroon_muted
        } else {
            getCategoryColor(element.category)
        }
        
        val backgroundColor = ContextCompat.getColor(requireContext(), backgroundColorRes)
        val drawable = GradientDrawable().apply {
            setColor(backgroundColor)
            cornerRadius = 12f
            setStroke(2, ContextCompat.getColor(requireContext(), R.color.white))
        }
        button.background = drawable
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_dark))
    }

    private fun toggleElement(elementName: String) {
        if (selectedElements.contains(elementName)) {
            selectedElements.remove(elementName)
            binding.resultText.text = "${ScienceData.getElementByName(elementName)?.symbol ?: elementName} removed"
        } else {
            addElement(elementName)
        }
        updateSelectedElementsDisplay()
        elementButtons.keys.forEach { updateButtonAppearance(it) }
    }

    private fun addElement(elementName: String) {
        if (selectedElements.size >= maxElements) {
            binding.resultText.text = "Maximum $maxElements elements allowed. Tap a selected element to remove it, or press Clear."
            return
        }

        if (selectedElements.contains(elementName)) {
            return // Already handled by toggleElement
        }

        selectedElements.add(elementName)
        updateSelectedElementsDisplay()
        
        val element = ScienceData.getElementByName(elementName)
        binding.resultText.text = "${element?.symbol ?: elementName} added (${selectedElements.size}/$maxElements)\nTap again to remove."
    }

    private fun clearSelection() {
        selectedElements.clear()
        updateSelectedElementsDisplay()
        binding.resultText.text = "Selection cleared. Select 2-4 elements to mix."
        elementButtons.keys.forEach { updateButtonAppearance(it) }
    }

    private fun updateSelectedElementsDisplay() {
        if (selectedElements.isEmpty()) {
            binding.selectedElementsText.text = "Selected: None"
        } else {
            val elements = selectedElements.map { 
                ScienceData.getElementByName(it)?.symbol ?: it 
            }.joinToString(" + ")
            binding.selectedElementsText.text = "Selected: $elements"
        }
    }

    private fun mixElements() {
        if (selectedElements.size < 2) {
            binding.resultText.text = "Please select at least 2 elements to mix."
            return
        }

        val reaction = ScienceData.findReaction(selectedElements)

        if (reaction != null) {
            vibrateSuccess()
            val activity = activity as? MainActivity
            if (activity?.isSoundEnabled() == true) {
                soundManager?.playSuccess()
            }
            displayReaction(reaction)
        } else {
            vibrateError()
            val activity = activity as? MainActivity
            if (activity?.isSoundEnabled() == true) {
                soundManager?.playError()
            }
            val result = when {
                selectedElements.size == 1 -> {
                    val element = ScienceData.getElementByName(selectedElements[0])
                    "Only one element selected. ${element?.description ?: "Select another element to create a reaction."}"
                }
                selectedElements.all { it == selectedElements[0] } -> {
                    "No compound formed from identical elements. Try mixing different elements!"
                }
                selectedElements.any { ScienceData.getElementByName(it)?.category == ElementCategory.NOBLE_GAS } -> {
                    val nobleGas = selectedElements.find { 
                        ScienceData.getElementByName(it)?.category == ElementCategory.NOBLE_GAS 
                    }
                    "$nobleGas is a noble gas and typically doesn't form compounds. Try other combinations!"
                }
                else -> {
                    val elementNames = selectedElements.joinToString(" and ")
                    "No known reaction between $elementNames. Try different combinations!\n\n" +
                    "Tip: Some reactions require specific conditions (temperature, pressure, catalysts)."
                }
            }
            binding.resultText.text = result
        }
    }

    private fun displayReaction(reaction: Reaction) {
        val elementInfo = selectedElements.mapNotNull { 
            ScienceData.getElementByName(it) 
        }

        val products = if (reaction.products.isNotEmpty()) {
            reaction.products.joinToString(", ")
        } else {
            "No products"
        }
        binding.resultText.text = "Reaction occurred! Created: $products"

        showReactionDialog(reaction, elementInfo)
    }

    private fun formatReactionType(type: ReactionType): String {
        return when (type) {
            ReactionType.IONIC_BONDING -> "Ionic Bonding"
            ReactionType.COVALENT_BONDING -> "Covalent Bonding"
            ReactionType.ACID_BASE -> "Acid-Base Reaction"
            ReactionType.COMBUSTION -> "Combustion"
            ReactionType.OXIDATION -> "Oxidation"
            ReactionType.EXPLOSIVE -> "Explosive Reaction"
            ReactionType.PRECIPITATION -> "Precipitation"
            ReactionType.NO_REACTION -> "No Reaction"
        }
    }

    private fun showReactionDialog(reaction: Reaction, elements: List<Element>) {
        val products = if (reaction.products.isNotEmpty()) {
            reaction.products.joinToString(" + ")
        } else {
            "No products"
        }
        
        val message = buildString {
            append("You created: $products\n\n")
            append("Chemical Equation:\n")
            append("${reaction.formula}\n\n")
            append("${reaction.description}\n\n")
            append("Type: ${formatReactionType(reaction.reactionType)}")
            if (reaction.funFact != null) {
                append("\n\nFun Fact: ${reaction.funFact}")
            }
        }
        
        binding.reactionTitle.text = "Reaction Successful!"
        binding.reactionMessage.text = message
        
        binding.learnMoreButton.setOnClickListener {
            hideReactionDialog()
            showElementDetailsDialog(elements)
        }
        
        binding.closeReactionButton.setOnClickListener {
            hideReactionDialog()
        }
        
        showPopup(binding.reactionCard)
    }
    
    private fun hideReactionDialog() {
        hidePopup(binding.reactionCard)
    }

    private fun showElementInfoDialog() {
        binding.elementSelectionTitle.text = "Select Element for Information"
        
        binding.elementSelectionList.removeAllViews()
        
        ScienceData.elements.forEach { element ->
            val button = Button(requireContext(), null, 0, R.style.PrimaryButton).apply {
                text = "${element.name} (${element.symbol})"
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setOnClickListener {
                    hideElementSelectionDialog()
                    showElementDetailsDialog(listOf(element))
                }
            }
            binding.elementSelectionList.addView(button)
        }
        
        binding.cancelElementSelectionButton.setOnClickListener {
            hideElementSelectionDialog()
        }
        
        showPopup(binding.elementSelectionCard)
    }
    
    private fun hideElementSelectionDialog() {
        hidePopup(binding.elementSelectionCard)
    }

    private fun showElementDetailsDialog(elements: List<Element>) {
        if (elements.isEmpty()) return

        val element = elements[0]
        val message = buildString {
            append("Element: ${element.name} (${element.symbol})\n")
            append("Atomic Number: ${element.atomicNumber}\n")
            append("Group: ${element.group}\n")
            append("Category: ${formatCategory(element.category)}\n\n")
            append("${element.description}")
        }

        binding.elementDetailsTitle.text = "Element Information"
        binding.elementDetailsMessage.text = message
        
        binding.closeElementDetailsButton.setOnClickListener {
            hideElementDetailsDialog()
        }
        
        showPopup(binding.elementDetailsCard)
    }
    
    private fun hideElementDetailsDialog() {
        hidePopup(binding.elementDetailsCard)
    }
    
    private fun showPopup(cardView: android.view.View) {
        binding.popupOverlay.visibility = android.view.View.VISIBLE
        binding.mainScrollView.setOnTouchListener { _, _ -> true }
        
        cardView.visibility = android.view.View.VISIBLE
        cardView.alpha = 0f
        cardView.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun hidePopup(cardView: android.view.View) {
        cardView.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                cardView.visibility = android.view.View.GONE
                binding.popupOverlay.visibility = android.view.View.GONE
                binding.mainScrollView.setOnTouchListener(null)
            }
            .start()
    }

    private fun formatCategory(category: ElementCategory): String {
        return when (category) {
            ElementCategory.ALKALI_METAL -> "Alkali Metal"
            ElementCategory.ALKALINE_EARTH_METAL -> "Alkaline Earth Metal"
            ElementCategory.HALOGEN -> "Halogen"
            ElementCategory.NOBLE_GAS -> "Noble Gas"
            ElementCategory.TRANSITION_METAL -> "Transition Metal"
            ElementCategory.NON_METAL -> "Non-Metal"
            ElementCategory.METALLOID -> "Metalloid"
            ElementCategory.OTHER_METAL -> "Other Metal"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
