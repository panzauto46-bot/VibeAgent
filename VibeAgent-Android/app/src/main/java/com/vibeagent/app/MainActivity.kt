package com.vibeagent.app

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vibeagent.app.adapter.ChatAdapter
import com.vibeagent.app.databinding.ActivityMainBinding
import com.vibeagent.app.viewmodel.ChatViewModel
import com.vibeagent.app.util.WalletManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private val walletManager = WalletManager()

    // Speech Recognition
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var pulseAnimator: ObjectAnimator? = null

    companion object {
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        setupSpeechRecognizer()
        autoConnectWallet()
    }

    private fun autoConnectWallet() {
        val prefs = getSharedPreferences("VibeAgentPrefs", android.content.Context.MODE_PRIVATE)
        val walletConnected = prefs.getBoolean("wallet_connected", false)
        val walletAddress = prefs.getString("wallet_address", null)

        if (walletConnected && !walletAddress.isNullOrBlank()) {
            // Auto-connect the saved wallet
            viewModel.connectExistingWallet(walletAddress)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    // ===== SPEECH RECOGNITION SETUP =====
    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech Recognition is not available on this device", Toast.LENGTH_LONG).show()
            binding.btnMic.visibility = View.GONE
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    isListening = true
                    binding.btnMic.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_mic_recording)
                    binding.editMessage.hint = "🎤 Listening..."
                    startPulseAnimation()
                }
            }

            override fun onBeginningOfSpeech() {
                runOnUiThread {
                    binding.editMessage.hint = "🎤 Speak now..."
                }
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Optional: visual feedback based on volume
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                runOnUiThread {
                    stopListeningUI()
                    binding.editMessage.hint = "⏳ Processing voice..."
                }
            }

            override fun onError(error: Int) {
                runOnUiThread {
                    stopListeningUI()
                    val errorMsg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "Could not recognize speech. Please try again."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout. Try speaking sooner."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio error. Check your microphone."
                        SpeechRecognizer.ERROR_NETWORK -> "Network error. Check your internet connection."
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                        else -> "Error: $error"
                    }
                    Toast.makeText(this@MainActivity, "🎤 $errorMsg", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResults(results: Bundle?) {
                runOnUiThread {
                    stopListeningUI()
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]
                        // Put recognized text into input field and send immediately
                        binding.editMessage.setText(recognizedText)
                        // Auto-send the message
                        sendMessage()
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                runOnUiThread {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        // Show partial text as hint while speaking
                        binding.editMessage.setText(matches[0])
                        binding.editMessage.setSelection(binding.editMessage.text.length)
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startListening() {
        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
            return
        }

        if (isListening) {
            stopListening()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // English language
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start voice recognition: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        stopListeningUI()
    }

    private fun stopListeningUI() {
        isListening = false
        binding.btnMic.background = ContextCompat.getDrawable(this, R.drawable.bg_mic_button)
        binding.editMessage.hint = getString(R.string.hint_message)
        stopPulseAnimation()
    }

    private fun startPulseAnimation() {
        pulseAnimator = ObjectAnimator.ofFloat(binding.btnMic, "scaleX", 1f, 1.2f, 1f).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        // Also animate scaleY
        ObjectAnimator.ofFloat(binding.btnMic, "scaleY", 1f, 1.2f, 1f).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun stopPulseAnimation() {
        pulseAnimator?.cancel()
        binding.btnMic.scaleX = 1f
        binding.btnMic.scaleY = 1f
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening
                startListening()
            } else {
                Toast.makeText(this, "Microphone permission is required for voice input", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ===== CLICK LISTENERS =====
    private fun setupClickListeners() {
        // Send button
        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        // Mic button
        binding.btnMic.setOnClickListener {
            startListening()
        }

        // Enter key on keyboard
        binding.editMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        // Menu Button (Open Wallet)
        binding.btnMenu.setOnClickListener {
            val walletSheet = WalletBottomSheet()
            walletSheet.show(supportFragmentManager, WalletBottomSheet.TAG)
        }

        // Connect Wallet Button
        binding.btnConnectWallet.setOnClickListener {
            if (viewModel.isWalletConnected.value == true) {
                // Already connected, open wallet details
                val walletSheet = WalletBottomSheet()
                walletSheet.show(supportFragmentManager, WalletBottomSheet.TAG)
            } else {
                // Show Connect Wallet options
                val connectSheet = ConnectWalletBottomSheet()
                connectSheet.show(supportFragmentManager, ConnectWalletBottomSheet.TAG)
            }
        }

        // Refresh Balance
        binding.btnRefreshBalance.setOnClickListener {
            viewModel.checkBalance()
            Toast.makeText(this, "🔄 Refreshing balance...", Toast.LENGTH_SHORT).show()
        }

        // Quick Actions
        binding.btnCheckBalance.setOnClickListener {
            viewModel.processMessage("Check balance")
        }

        binding.btnSendBnb.setOnClickListener {
            binding.editMessage.setText("Send 0.01 BNB to ")
            binding.editMessage.setSelection(binding.editMessage.text.length)
            binding.editMessage.requestFocus()
        }

        // Analisis AI button
        binding.btnAnalyze.setOnClickListener {
            viewModel.analyzePortfolio()
        }

        binding.btnHelp.setOnClickListener {
            viewModel.processMessage("help")
        }
    }

    private fun sendMessage() {
        val message = binding.editMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.processMessage(message)
            binding.editMessage.text.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages.toList())
            if (messages.isNotEmpty()) {
                binding.recyclerMessages.smoothScrollToPosition(messages.size - 1)
            }
        }

        viewModel.isProcessing.observe(this) { isProcessing ->
            binding.btnSend.isEnabled = !isProcessing
            binding.editMessage.isEnabled = !isProcessing
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // Observe wallet connection state
        viewModel.isWalletConnected.observe(this) { isConnected ->
            if (isConnected) {
                binding.textConnectWallet.text = "Connected"
                binding.walletStatusBar.visibility = View.VISIBLE
            } else {
                binding.textConnectWallet.text = "Connect"
                binding.walletStatusBar.visibility = View.GONE
            }
        }

        // Observe wallet data for status bar
        viewModel.wallet.observe(this) { wallet ->
            if (wallet != null) {
                binding.textWalletAddress.text = walletManager.shortenAddress(wallet.address)
                binding.textWalletBalance.text = "${wallet.balance} BNB"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        pulseAnimator?.cancel()
    }
}
