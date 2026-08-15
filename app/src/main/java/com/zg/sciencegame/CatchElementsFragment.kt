package com.zg.sciencegame

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zg.sciencegame.databinding.CatchElementsFragmentBinding
import kotlin.random.Random

class CatchElementsFragment : Fragment(), SensorEventListener {

    private var _binding: CatchElementsFragmentBinding? = null
    private val binding get() = _binding!!

    private val caughtElements = mutableListOf<String>()
    private var score = 0
    private var currentLevel = 1
    private var hearts = 3
    private var isGameRunning = false
    private val fallingElements = mutableListOf<View>()
    private val handler = Handler(Looper.getMainLooper())
    private var flaskX = 0f
    private var flaskWidth = 0f
    private val elementSpawnInterval = 1200L
    private var spawnRunnable: Runnable? = null
    
    private val createdCompounds = mutableSetOf<String>()
    
    private val levelRequirements = mapOf(
        1 to 100,
        2 to 200,
        3 to 300
    )
    
    private val levelHints = mapOf(
        1 to listOf(
            "Try mixing a metal with a halogen (like Sodium + Chlorine)",
            "Combine Hydrogen with Oxygen to create something essential for life",
            "Alkali metals react well with halogens to form salts",
            "Think about table salt - what two elements make it?",
            "Potassium and Chlorine form a compound used in fertilizers",
            "Sodium and Fluorine create a compound found in toothpaste",
            "Calcium and Chlorine combine to make a compound that melts ice",
            "Hydrogen and Chlorine form an acid when dissolved in water",
            "Try combining elements from Group 1 (alkali metals) with Group 17 (halogens)",
            "What do you get when you mix the lightest element with the most abundant gas?"
        ),
        2 to listOf(
            "Metals can burn in Oxygen - try Iron or Copper with Oxygen",
            "Carbon reacts with Oxygen in combustion reactions",
            "Try combining Hydrogen with other non-metals",
            "Magnesium burns brightly - what does it need?",
            "Iron rusts when exposed to Oxygen - what compound forms?",
            "Copper develops a green patina when it reacts with Oxygen",
            "Aluminum forms a protective layer when it reacts with Oxygen",
            "Lithium batteries use reactions with Oxygen",
            "Zinc and Chlorine create a compound used in deodorants",
            "What happens when Carbon burns completely in Oxygen?"
        ),
        3 to listOf(
            "For acid-base reactions, you need 4 elements: Sodium, Hydrogen, Oxygen, and Chlorine",
            "Try combining Sulfur or Phosphorus with Oxygen",
            "Some reactions need multiple elements - experiment with different combinations",
            "Think about what happens when metals react with water",
            "Sulfur burns to form a gas that contributes to acid rain",
            "Phosphorus burns brightly in Oxygen producing white smoke",
            "Hydrogen and Nitrogen combine to form ammonia (needs high pressure)",
            "Carbon and Hydrogen form the simplest hydrocarbon",
            "Alkali metals react explosively with water - try Sodium or Potassium",
            "What four elements do you need for a neutralization reaction?"
        )
    )
    
    private var sensorManager: SensorManager? = null
    private var gyroscopeSensor: Sensor? = null
    private var hasGyroscope = false
    private var gyroX = 0f
    private val gyroSensitivity = 40f
    private val gyroUpdateHandler = Handler(Looper.getMainLooper())
    private var gyroUpdateRunnable: Runnable? = null
    
    private var vibrator: Vibrator? = null
    private var soundManager: SoundManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatchElementsFragmentBinding.inflate(inflater, container, false)

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        soundManager = SoundManager.getInstance(requireContext())

        checkGyroscopeSupport()
        setupFlask()
        setupControls()
        binding.backToHomeButton.setOnClickListener {
            cleanupBeforeNavigation()
            if (isAdded && view != null) {
                findNavController().navigateUp()
            }
        }
        
        val activity = activity as? MainActivity
        activity?.onGyroscopeSettingChanged = {
            if (isAdded && _binding != null) {
                checkGyroscopeSupport()
                updateGyroscopeControls()
            }
        }

        return binding.root
    }
    
    private fun vibrateSuccess(short: Boolean = false) {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (short) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val pattern = longArrayOf(0, 100, 50, 100, 50, 100)
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            }
        } else {
            @Suppress("DEPRECATION")
            if (short) {
                vibrator?.vibrate(50L)
            } else {
                val pattern = longArrayOf(0, 100, 50, 100, 50, 100)
                vibrator?.vibrate(pattern, -1)
            }
        }
    }
    
    private fun vibrateError(short: Boolean = false) {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        
        val duration = if (short) 100L else 300L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = if (short) {
                longArrayOf(0, 50, 50, 50)
            } else {
                longArrayOf(0, 100, 100, 100)
            }
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val pattern = if (short) {
                longArrayOf(0, 50, 50, 50)
            } else {
                longArrayOf(0, 100, 100, 100)
            }
            vibrator?.vibrate(pattern, -1)
        }
    }
    
    private fun vibrateQuizCorrect() {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        val context = context ?: return
        val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibratorService == null || !vibratorService.hasVibrator()) return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 50, 30, 50)
                vibratorService.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val pattern = longArrayOf(0, 50, 30, 50)
                vibratorService.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun vibrateQuizWrong() {
        val activity = activity as? MainActivity
        if (activity?.isVibrationEnabled() != true) return
        
        if (vibrator == null || !vibrator!!.hasVibrator()) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(200L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(200L)
        }
    }
    
    private fun checkGyroscopeSupport() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val deviceHasGyroscope = gyroscopeSensor != null
        
        val activity = activity as? MainActivity
        val gyroscopeEnabled = activity?.isGyroscopeEnabled() ?: true
        
        hasGyroscope = deviceHasGyroscope && gyroscopeEnabled
        
        if (hasGyroscope) {
            binding.controlButtonsLayout.visibility = View.GONE
            binding.gyroIndicator.visibility = View.VISIBLE
        } else {
            binding.controlButtonsLayout.visibility = View.VISIBLE
            binding.gyroIndicator.visibility = View.GONE
        }
    }

    private fun setupFlask() {
        binding.flask.post {
            flaskX = binding.flask.x
            flaskWidth = binding.flask.width.toFloat()
        }
    }

    private fun setupControls() {
        binding.startButton.setOnClickListener { startGame() }
        binding.stopButton.setOnClickListener { stopGame() }
        binding.leftButton.setOnClickListener { moveFlaskLeft() }
        binding.rightButton.setOnClickListener { moveFlaskRight() }
        binding.mixCaughtButton.setOnClickListener { mixCaughtElements() }
        binding.clearCaughtButton.setOnClickListener { clearCaughtElements() }

        if (!hasGyroscope) {
            binding.gameArea.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    moveFlaskTo(event.x)
                    true
                } else {
                    false
                }
            }
        }
    }
    
    private fun clearCaughtElements() {
        caughtElements.clear()
        updateUI()
    }

    private fun moveFlaskLeft() {
        if (!isGameRunning) return
        val maxX = binding.gameArea.width - flaskWidth
        val newX = (flaskX - 50f).coerceAtLeast(0f).coerceAtMost(maxX)
        binding.flask.x = newX
        flaskX = newX
    }

    private fun moveFlaskRight() {
        if (!isGameRunning) return
        val maxX = binding.gameArea.width - flaskWidth
        val newX = (flaskX + 50f).coerceAtLeast(0f).coerceAtMost(maxX)
        binding.flask.x = newX
        flaskX = newX
    }

    private fun moveFlaskTo(x: Float) {
        val maxX = binding.gameArea.width - flaskWidth
        val newX = (x - flaskWidth / 2).coerceIn(0f, maxX)
        
        binding.flask.animate()
            .x(newX)
            .setDuration(100)
            .start()
        
        flaskX = newX
    }

    private fun startGame() {
        if (isGameRunning) return
        
        isGameRunning = true
        binding.startButton.isEnabled = false
        binding.stopButton.isEnabled = true
        caughtElements.clear()
        score = 0
        currentLevel = 1
        hearts = 3
        createdCompounds.clear()
        updateUI()
        updateHint()
        
        if (hasGyroscope && gyroscopeSensor != null) {
            sensorManager?.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
            startGyroUpdateLoop()
        }
        
        spawnRunnable = object : Runnable {
            override fun run() {
                if (isGameRunning && _binding != null && isAdded) {
                    spawnFallingElement()
                    handler.postDelayed(this, elementSpawnInterval)
                }
            }
        }
        handler.post(spawnRunnable!!)
    }
    
    private fun startGyroUpdateLoop() {
        gyroUpdateRunnable = object : Runnable {
            override fun run() {
                if (isGameRunning && hasGyroscope && _binding != null && isAdded) {
                    updateFlaskFromGyro()
                    gyroUpdateHandler.postDelayed(this, 16)
                }
            }
        }
        gyroUpdateHandler.post(gyroUpdateRunnable!!)
    }
    
    private fun updateFlaskFromGyro() {
        if (_binding == null || !isAdded) return
        
        val newX = flaskX + (gyroX * gyroSensitivity)
        val maxX = binding.gameArea.width - flaskWidth
        val clampedX = newX.coerceIn(0f, maxX)
        binding.flask.x = clampedX
        flaskX = clampedX
    }

    private fun stopGame() {
        isGameRunning = false
        
        if (hasGyroscope) {
            sensorManager?.unregisterListener(this)
            gyroUpdateRunnable?.let { gyroUpdateHandler.removeCallbacks(it) }
        }
        
        spawnRunnable?.let { handler.removeCallbacks(it) }
        
        if (_binding != null) {
            fallingElements.forEach { elementView ->
                try {
                    binding.gameArea.removeView(elementView)
                } catch (e: Exception) {
                }
            }
            fallingElements.clear()
            
            binding.startButton.isEnabled = true
            binding.stopButton.isEnabled = false
            caughtElements.clear()
            updateUI()
        } else {
            fallingElements.clear()
            caughtElements.clear()
        }
    }
    
    private fun cleanupBeforeNavigation() {
        isGameRunning = false
        
        spawnRunnable?.let { 
            handler.removeCallbacks(it)
            handler.removeCallbacksAndMessages(null)
        }
        gyroUpdateRunnable?.let { 
            gyroUpdateHandler.removeCallbacks(it)
            gyroUpdateHandler.removeCallbacksAndMessages(null)
        }
        
        fallingElements.forEach { elementView ->
            try {
                elementView.animate().cancel()
            } catch (e: Exception) {
            }
        }
        
        if (_binding != null) {
            try {
                binding.quizCard.animate().cancel()
                binding.feedbackCard.animate().cancel()
            } catch (e: Exception) {
            }
            binding.quizCard.visibility = View.GONE
        }
        
        if (hasGyroscope) {
            try {
                sensorManager?.unregisterListener(this)
            } catch (e: Exception) {
            }
        }
        
        fallingElements.clear()
        pausedElementPositions.clear()
    }

    private fun spawnFallingElement() {
        val element = ScienceData.elements.random()
        val elementView = createElementView(element)
        
        elementView.tag = element.name
        
        val startX = Random.nextFloat() * (binding.gameArea.width - elementView.width)
        elementView.x = startX
        elementView.y = -elementView.height.toFloat()
        
        binding.gameArea.addView(elementView)
        fallingElements.add(elementView)
        
        val animator = ObjectAnimator.ofFloat(elementView, "y", -elementView.height.toFloat(), binding.gameArea.height.toFloat())
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        
        animator.addUpdateListener {
            if (_binding != null && isAdded) {
                checkCollision(elementView, element.name)
            }
        }
        
        animator.start()
        
        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (_binding != null && isAdded) {
                    try {
                        binding.gameArea.removeView(elementView)
                    } catch (e: Exception) {
                    }
                }
                fallingElements.remove(elementView)
            }
        })
    }

    private fun createElementView(element: Element): View {
        val textView = TextView(requireContext()).apply {
            text = element.symbol
            textSize = 12f
            gravity = android.view.Gravity.CENTER
            setPadding(2, 2, 2, 2)
        }
        
        val categoryColor = ContextCompat.getColor(requireContext(), getCategoryColor(element.category))
        val drawable = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(categoryColor)
            setStroke(1, ContextCompat.getColor(requireContext(), R.color.white))
        }
        textView.background = drawable
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_dark))
        
        val density = resources.displayMetrics.density
        val size = (35 * density).toInt()
        textView.layoutParams = ViewGroup.LayoutParams(size, size)
        
        return textView
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

    private fun checkCollision(elementView: View, elementName: String) {
        if (!isGameRunning || _binding == null || !isAdded) return
        
        val isQuizVisible = binding.popupOverlay.visibility == View.VISIBLE || 
                           binding.quizCard.visibility == View.VISIBLE
        if (isQuizVisible) return
        
        val elementCenterX = elementView.x + elementView.width / 2
        val elementBottom = elementView.y + elementView.height
        
        val flaskCenterX = flaskX + flaskWidth / 2
        val flaskTop = binding.flask.y
        
        if (elementBottom >= flaskTop - 5 && elementBottom <= flaskTop + 20) {
            val flaskLeft = flaskX - 5
            val flaskRight = flaskX + flaskWidth + 5
            
            if (elementCenterX >= flaskLeft && elementCenterX <= flaskRight) {
                catchElement(elementView, elementName)
            }
        }
    }

    private fun catchElement(elementView: View, elementName: String) {
        if (caughtElements.size >= 4) {
            return
        }
        
        if (!caughtElements.contains(elementName)) {
            caughtElements.add(elementName)
            
            val isQuizVisible = binding.popupOverlay.visibility == View.VISIBLE || 
                               binding.quizCard.visibility == View.VISIBLE
            
            if (caughtElements.size <= 4 && !isQuizVisible) {
                val activity = activity as? MainActivity
                if (activity?.isSoundEnabled() == true) {
                    soundManager?.playChemicalIntoFlask()
                }
            }
            
            updateUI()
            
            binding.gameArea.removeView(elementView)
            fallingElements.remove(elementView)
            elementView.animate().cancel()
        }
    }

    private fun updateUI() {
        if (_binding == null || !isAdded) return
        
        val levelRequirement = levelRequirements[currentLevel] ?: 0
        binding.scoreText.text = "Level $currentLevel | Score: $score / $levelRequirement"
        
        val heartsText = "❤️".repeat(hearts) + "♡".repeat(3 - hearts)
        binding.heartsText.text = heartsText
        
        if (caughtElements.isEmpty()) {
            binding.caughtElementsText.text = "Caught: None"
            binding.mixCaughtButton.isEnabled = false
            binding.clearCaughtButton.isEnabled = false
        } else {
            val elements = caughtElements.map { 
                ScienceData.getElementByName(it)?.name ?: it 
            }.joinToString(" + ")
            binding.caughtElementsText.text = "Caught: $elements"
            binding.mixCaughtButton.isEnabled = caughtElements.size >= 2
            binding.clearCaughtButton.isEnabled = true
        }
    }
    
    private fun updateHint() {
        val hints = levelHints[currentLevel] ?: emptyList()
        if (hints.isNotEmpty()) {
            val hintIndex = Random.nextInt(hints.size)
            binding.hintText.text = "Hint: ${hints[hintIndex]}"
        } else {
            binding.hintText.text = "Hint: Try mixing different elements!"
        }
    }
    
    private fun checkLevelProgression(createdCompound: String? = null) {
        val levelRequirement = levelRequirements[currentLevel]
        
        if (levelRequirement != null && score >= levelRequirement) {
            if (currentLevel < 3) {
                currentLevel++
                updateHint()
                showLevelCompleteMessage(createdCompound)
            } else {
                showGameCompleteMessage()
                handler.postDelayed({
                    if (_binding != null && isAdded) {
                        stopGame()
                    }
                }, 2000)
            }
        } else if (createdCompound != null) {
            showReactionResultMessage(createdCompound)
        }
    }
    
    private fun showLevelCompleteMessage(createdCompound: String? = null) {
        val levelRequirement = levelRequirements[currentLevel] ?: 0
        val nextLevelHints = levelHints[currentLevel]?.firstOrNull() ?: "Try new combinations!"
        
        val message = buildString {
            if (createdCompound != null) {
                append("You created: $createdCompound\n\n")
            }
            append("Congratulations! You've reached Level $currentLevel!\n\n")
            append("Next goal: $levelRequirement points\n\n")
            append("Next hint: $nextLevelHints")
        }
        
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playLevelUp()
        }
        
        vibrateSuccess(false)
        
        showFeedback(
            "Level $currentLevel Complete!",
            message,
            true
        )
    }
    
    private fun showReactionResultMessage(createdCompound: String) {
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playSuccess()
        }
        
        vibrateSuccess(true)
        
        showFeedback(
            "Reaction Successful!",
            "You created: $createdCompound\n\n+100 points!",
            true
        )
    }
    
    private fun showGameCompleteMessage() {
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playLevelUp()
        }
        
        vibrateSuccess(false)
        
        showFeedback(
            "Game Complete!",
            "Amazing! You've completed all 3 levels!\n\nFinal Score: $score points\nCompounds Created: ${createdCompounds.size}\n\nYou're a chemistry master!",
            true
        )
    }

    private fun mixCaughtElements() {
        if (caughtElements.size < 2) {
            return
        }

        val reaction = ScienceData.findReaction(caughtElements)
        
        if (reaction != null) {
            val compoundKey = reaction.products.joinToString(" + ")
            
            if (createdCompounds.contains(compoundKey)) {
                showDuplicateCompoundMessage(compoundKey)
                caughtElements.clear()
                updateUI()
                return
            }
            
            createdCompounds.add(compoundKey)
            caughtElements.clear()
            score += 100
            
            val createdCompound = reaction.products.joinToString(" + ")
            
            checkLevelProgression(createdCompound)
            updateUI()
        } else {
            showNoReactionMessage()
            caughtElements.clear()
            showHeartSaveQuestion()
            updateUI()
        }
    }
    
    private fun loseHeart() {
        if (hearts > 0) {
            hearts--
            if (hearts <= 0) {
                gameOver()
            }
        }
    }
    
    private var currentQuizQuestion: ChemistryQuestion? = null
    private var currentCorrectIndex: Int = -1
    private var wasGameRunningBeforeQuiz = false
    private val pausedElementPositions = mutableMapOf<View, Pair<Float, Float>>()
    
    private fun showHeartSaveQuestion() {
        if (_binding == null || !isAdded) return
        
        wasGameRunningBeforeQuiz = isGameRunning
        pauseGameForQuiz()
        
        val question = ChemistryQuestions.questions.random()
        val options = question.options.shuffled()
        val correctIndex = options.indexOf(question.options[question.correctAnswer])
        
        currentQuizQuestion = question
        currentCorrectIndex = correctIndex
        
        binding.quizTitle.text = "Save Your Heart!"
        binding.quizQuestion.text = "Answer this question correctly to keep your heart:\n\n${question.question}"
        
        binding.quizOptionA.text = "A. ${options[0]}"
        binding.quizOptionB.text = "B. ${options[1]}"
        binding.quizOptionC.text = "C. ${options[2]}"
        binding.quizOptionD.text = "D. ${options[3]}"
        
        binding.quizOptionA.setOnClickListener { handleQuizAnswer(0) }
        binding.quizOptionB.setOnClickListener { handleQuizAnswer(1) }
        binding.quizOptionC.setOnClickListener { handleQuizAnswer(2) }
        binding.quizOptionD.setOnClickListener { handleQuizAnswer(3) }
        
        binding.continueGameButton.text = "Continue Game"
        
        binding.flask.visibility = View.GONE
        binding.controlPanel.visibility = View.GONE
        
        binding.popupOverlay.visibility = View.VISIBLE
        binding.gameArea.setOnTouchListener { _, _ -> true }
        
        binding.quizCard.visibility = View.VISIBLE
        binding.quizCard.alpha = 0f
        binding.quizCard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }
    
    private fun pauseGameForQuiz() {
        spawnRunnable?.let { handler.removeCallbacks(it) }
        
        pausedElementPositions.clear()
        fallingElements.forEach { elementView ->
            pausedElementPositions[elementView] = Pair(elementView.x, elementView.y)
            elementView.animate().cancel()
        }
        
        gyroUpdateRunnable?.let { gyroUpdateHandler.removeCallbacks(it) }
    }
    
    private fun resumeGameAfterQuiz() {
        if (_binding == null || !isAdded) return
        
        if (!isGameRunning) {
            isGameRunning = true
        }
        
        val elementsToResume = fallingElements.toList()
        elementsToResume.forEach { elementView ->
            val position = pausedElementPositions[elementView]
            if (position != null && _binding != null && isAdded) {
                elementView.x = position.first
                elementView.y = position.second
                
                val elementName = elementView.tag as? String
                if (elementName != null && elementView.y < binding.gameArea.height) {
                    val remainingDistance = binding.gameArea.height - elementView.y
                    val animator = ObjectAnimator.ofFloat(
                        elementView, 
                        "y", 
                        elementView.y, 
                        binding.gameArea.height.toFloat()
                    )
                    animator.duration = (remainingDistance / binding.gameArea.height * 5000).toLong().coerceAtLeast(100)
                    animator.interpolator = LinearInterpolator()
                    
                    animator.addUpdateListener {
                        if (_binding != null && isAdded) {
                            checkCollision(elementView, elementName)
                        }
                    }
                    
                    animator.addListener(object : android.animation.Animator.AnimatorListener {
                        override fun onAnimationStart(animation: android.animation.Animator) {}
                        override fun onAnimationCancel(animation: android.animation.Animator) {}
                        override fun onAnimationRepeat(animation: android.animation.Animator) {}
                        override fun onAnimationEnd(animation: android.animation.Animator) {
                            if (_binding != null && isAdded) {
                                try {
                                    binding.gameArea.removeView(elementView)
                                } catch (e: Exception) {
                                }
                            }
                            fallingElements.remove(elementView)
                        }
                    })
                    
                    animator.start()
                }
            }
        }
        pausedElementPositions.clear()
        
        spawnRunnable?.let { handler.removeCallbacks(it) }
        spawnRunnable = object : Runnable {
            override fun run() {
                if (isGameRunning && _binding != null && isAdded) {
                    spawnFallingElement()
                    handler.postDelayed(this, elementSpawnInterval)
                }
            }
        }
        handler.post(spawnRunnable!!)
        handler.post {
            if (isGameRunning && _binding != null && isAdded) {
                spawnFallingElement()
            }
        }
        
        if (hasGyroscope && gyroscopeSensor != null && isAdded) {
            try {
                sensorManager?.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
                startGyroUpdateLoop()
            } catch (e: Exception) {
            }
        }
    }
    
    private fun handleQuizAnswer(selectedIndex: Int) {
        if (_binding == null || !isAdded) return
        
        val isCorrect = selectedIndex == currentCorrectIndex
        
        binding.quizOptionA.visibility = View.GONE
        binding.quizOptionB.visibility = View.GONE
        binding.quizOptionC.visibility = View.GONE
        binding.quizOptionD.visibility = View.GONE
        
        if (isCorrect) {
            val activity = activity as? MainActivity
            if (activity?.isSoundEnabled() == true) {
                soundManager?.playSuccess()
            }
            vibrateQuizCorrect()
            binding.quizTitle.text = "Correct!"
            binding.quizQuestion.text = "Great job! You saved your heart!"
        } else {
            val activity = activity as? MainActivity
            if (activity?.isSoundEnabled() == true) {
                soundManager?.playError()
            }
            vibrateQuizWrong()
            
            if (hearts <= 1) {
                binding.quizTitle.text = "Incorrect"
                binding.quizQuestion.text = "Wrong answer. You lost your last heart!"
            } else {
                binding.quizTitle.text = "Incorrect"
                binding.quizQuestion.text = "Wrong answer. You lost a heart."
            }
            
            loseHeart()
        }
        
        updateUI()
        
        binding.continueGameButton.visibility = View.VISIBLE
        if (!isCorrect && hearts <= 0) {
            binding.continueGameButton.text = "Play Again"
        } else {
            binding.continueGameButton.text = "Continue Game"
        }
        binding.continueGameButton.setOnClickListener {
            continueGameAfterQuiz(isCorrect)
        }
        
        currentQuizQuestion = null
        currentCorrectIndex = -1
    }
    
    private fun continueGameAfterQuiz(wasCorrect: Boolean) {
        val shouldResume = wasGameRunningBeforeQuiz
        
        binding.quizCard.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.quizCard.visibility = View.GONE
                binding.quizOptionA.visibility = View.VISIBLE
                binding.quizOptionB.visibility = View.VISIBLE
                binding.quizOptionC.visibility = View.VISIBLE
                binding.quizOptionD.visibility = View.VISIBLE
                binding.continueGameButton.visibility = View.GONE
                    binding.flask.visibility = View.VISIBLE
                    binding.controlPanel.visibility = View.VISIBLE
                    
                    binding.popupOverlay.visibility = View.GONE
                    if (!hasGyroscope) {
                        binding.gameArea.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                                moveFlaskTo(event.x)
                                true
                            } else {
                                false
                            }
                        }
                    } else {
                        binding.gameArea.setOnTouchListener(null)
                    }
                    
                if (!wasCorrect && hearts <= 0) {
                    gameOver()
                } else if (shouldResume) {
                    resumeGameAfterQuiz()
                }
            }
            .start()
        
        wasGameRunningBeforeQuiz = false
    }
    
    private fun gameOver() {
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playError()
        }
        
        vibrateError(false)
        
        stopGame()
        showFeedback(
            "Game Over",
            "You've run out of hearts!\n\nFinal Score: $score points\nLevel Reached: $currentLevel\nCompounds Created: ${createdCompounds.size}\n\nBetter luck next time!",
            false
        )
        
        handler.postDelayed({
            if (_binding != null && isAdded) {
                hearts = 3
                score = 0
                currentLevel = 1
                createdCompounds.clear()
                updateUI()
                updateHint()
            }
        }, 4000)
    }
    
    private fun showFeedback(title: String, message: String, isSuccess: Boolean) {
        if (_binding == null || !isAdded) return
        
        binding.feedbackTitle.text = title
        binding.feedbackMessage.text = message
        
        val context = context ?: return
        val backgroundColor = if (isSuccess) {
            ContextCompat.getColor(context, R.color.alkaline_earth)
        } else {
            ContextCompat.getColor(context, R.color.alkali_metal)
        }
        binding.feedbackCard.setCardBackgroundColor(backgroundColor)
        
        binding.feedbackCard.visibility = View.VISIBLE
        binding.feedbackCard.alpha = 0f
        binding.feedbackCard.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
        
        binding.feedbackCard.setOnClickListener {
            hideFeedback()
        }
        
        handler.postDelayed({
            if (_binding != null && isAdded) {
                hideFeedback()
            }
        }, 4000)
    }
    
    private fun hideFeedback() {
        if (_binding == null || !isAdded) return
        
        binding.feedbackCard.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                if (_binding != null && isAdded) {
                    binding.feedbackCard.visibility = View.GONE
                }
            }
            .start()
    }

    
    private fun showDuplicateCompoundMessage(compoundKey: String) {
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playError()
        }
        
        vibrateError(true)
        
        showFeedback(
            "Already Created",
            "You've already created: $compoundKey\n\nTry mixing different elements to discover new compounds!\n\nTotal compounds created: ${createdCompounds.size}",
            false
        )
    }
    
    private fun showNoReactionMessage() {
        val activity = activity as? MainActivity
        if (activity?.isSoundEnabled() == true) {
            soundManager?.playError()
        }
        
        vibrateError(true)
        
        showFeedback(
            "No Reaction",
            "The caught elements don't react with each other. Try catching different combinations!",
            false
        )
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

    override fun onPause() {
        super.onPause()
        if (hasGyroscope) {
            sensorManager?.unregisterListener(this)
        }
    }
    
    override fun onResume() {
        super.onResume()
        checkGyroscopeSupport()
        
        if (isGameRunning && hasGyroscope && gyroscopeSensor != null) {
            sensorManager?.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
            startGyroUpdateLoop()
        } else if (isGameRunning && !hasGyroscope) {
            sensorManager?.unregisterListener(this)
            gyroUpdateRunnable?.let { gyroUpdateHandler.removeCallbacks(it) }
            if (_binding != null && !hasGyroscope) {
                binding.gameArea.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                        moveFlaskTo(event.x)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activity = activity as? MainActivity
        activity?.onGyroscopeSettingChanged = null
        cleanupBeforeNavigation()
        _binding = null
    }
    
    private fun updateGyroscopeControls() {
        if (_binding == null || !isAdded) return
        
        if (hasGyroscope) {
            binding.controlButtonsLayout.visibility = View.GONE
            binding.gyroIndicator.visibility = View.VISIBLE
            binding.gameArea.setOnTouchListener(null)
            
            if (isGameRunning && gyroscopeSensor != null) {
                try {
                    sensorManager?.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
                    startGyroUpdateLoop()
                } catch (e: Exception) {
                }
            }
        } else {
            binding.controlButtonsLayout.visibility = View.VISIBLE
            binding.gyroIndicator.visibility = View.GONE
            
            sensorManager?.unregisterListener(this)
            gyroUpdateRunnable?.let { gyroUpdateHandler.removeCallbacks(it) }
            
            binding.gameArea.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    moveFlaskTo(event.x)
                    true
                } else {
                    false
                }
            }
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE && isGameRunning) {
            gyroX = event.values[1]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

