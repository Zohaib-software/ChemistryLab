package com.zg.sciencegame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zg.sciencegame.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private var currentTab = "lab"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_labFragment)
        }

        binding.catchElementsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_catchElementsFragment)
        }

        binding.howToPlayButton.setOnClickListener {
            showHowToPlayPopup()
        }

        setupHowToPlayTabs()

        return binding.root
    }

    private fun setupHowToPlayTabs() {
    }

    private fun switchToLabModeTab() {
        if (!isAdded || view == null) return
        
        currentTab = "lab"
        
        val context = context ?: return
        binding.labModeTab.backgroundTintList = ContextCompat.getColorStateList(context, R.color.pink_accent)
        binding.labModeTab.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        
        binding.catchElementsModeTab.backgroundTintList = ContextCompat.getColorStateList(context, R.color.maroon_muted)
        binding.catchElementsModeTab.setTextColor(ContextCompat.getColor(context, R.color.text_on_dark))
        
        binding.labModeInstructions.visibility = View.VISIBLE
        binding.catchElementsModeInstructions.visibility = View.GONE
    }

    private fun switchToCatchElementsModeTab() {
        if (!isAdded || view == null) return
        
        currentTab = "catch"
        
        val context = context ?: return
        binding.labModeTab.backgroundTintList = ContextCompat.getColorStateList(context, R.color.maroon_muted)
        binding.labModeTab.setTextColor(ContextCompat.getColor(context, R.color.text_on_dark))
        
        binding.catchElementsModeTab.backgroundTintList = ContextCompat.getColorStateList(context, R.color.pink_accent)
        binding.catchElementsModeTab.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        
        binding.labModeInstructions.visibility = View.GONE
        binding.catchElementsModeInstructions.visibility = View.VISIBLE
    }

    private fun showHowToPlayPopup() {
        binding.labModeTab.setOnClickListener {
            switchToLabModeTab()
        }

        binding.catchElementsModeTab.setOnClickListener {
            switchToCatchElementsModeTab()
        }

        binding.labModeGotItButton.setOnClickListener { hideHowToPlayPopup() }
        binding.catchElementsModeGotItButton.setOnClickListener { hideHowToPlayPopup() }
        
        binding.popupOverlay.visibility = View.VISIBLE
        
        binding.howToPlayCard.visibility = View.VISIBLE
        binding.howToPlayCard.alpha = 0f
        binding.howToPlayCard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        switchToLabModeTab()
    }

    private fun hideHowToPlayPopup() {
        binding.howToPlayCard.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.howToPlayCard.visibility = View.GONE
                binding.popupOverlay.visibility = View.GONE
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
