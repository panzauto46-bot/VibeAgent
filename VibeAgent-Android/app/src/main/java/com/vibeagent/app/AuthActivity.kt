package com.vibeagent.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vibeagent.app.databinding.ActivityAuthBinding
import com.vibeagent.app.util.SecureWalletStorage
import com.vibeagent.app.util.WalletManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    // Modes: "login", "register", "phrase"
    private var currentMode = "login"

    private lateinit var googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient
    private lateinit var auth: com.google.firebase.auth.FirebaseAuth
    private val walletManager = WalletManager()
    private lateinit var secureStorage: SecureWalletStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        secureStorage = SecureWalletStorage(this)

        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)

        // Set mainnet by default
        walletManager.setNetwork(true)

        setupListeners()
        updateUI()
    }

    private fun setupListeners() {
        // Toggle Login/Register
        binding.btnToggleAuth.setOnClickListener {
            currentMode = when (currentMode) {
                "login" -> "register"
                "register" -> "login"
                "phrase" -> "login"
                else -> "login"
            }
            updateUI()
        }

        // Login Submit
        binding.btnLoginSubmit.setOnClickListener {
            handleLogin()
        }

        // Register Submit
        binding.btnRegisterSubmit.setOnClickListener {
            handleRegister()
        }

        // Google Sign In
        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Import Phrase button
        binding.btnImportPhrase.setOnClickListener {
            currentMode = "phrase"
            updateUI()
        }

        // Create Wallet button (on auth page)
        binding.btnCreateWalletAuth.setOnClickListener {
            handleCreateNewWallet()
        }

        // Import Submit
        binding.btnImportSubmit.setOnClickListener {
            handleImportWallet()
        }
    }

    // ===== WALLET IMPORT (Seed Phrase Only) =====
    private fun handleImportWallet() {
        val seedPhrase = binding.editSeedPhrase.text.toString().trim()

        if (seedPhrase.isEmpty()) {
            Toast.makeText(this, "⚠️ Please enter a seed phrase (12 or 24 words)", Toast.LENGTH_SHORT).show()
            return
        }

        val words = seedPhrase.lowercase().split("\\s+".toRegex())
        if (words.size != 12 && words.size != 24) {
            Toast.makeText(this, "⚠️ Seed phrase must be 12 or 24 words (you entered ${words.size} words)", Toast.LENGTH_LONG).show()
            return
        }

        binding.btnImportSubmit.text = "Processing..."
        binding.btnImportSubmit.isEnabled = false

        try {
            val walletInfo = walletManager.importFromSeedPhrase(seedPhrase)

            // ===== SAVE ENCRYPTED (Non-Custodial) =====
            // Private key & mnemonic ONLY stored in EncryptedSharedPreferences
            // NEVER sent to Firebase or any remote server
            secureStorage.saveWallet(
                address = walletInfo.address,
                privateKey = walletInfo.privateKey,
                mnemonic = walletInfo.mnemonic,
                loginMethod = "phrase"
            )

            // Regular prefs only store login state & address (no secrets!)
            saveLoginStateAndProceed(walletInfo.address, "wallet_phrase")

        } catch (e: Exception) {
            Toast.makeText(this, "❌ ${e.message}", Toast.LENGTH_LONG).show()
            binding.btnImportSubmit.text = "🔗 Import & Connect"
            binding.btnImportSubmit.isEnabled = true
        }
    }

    private fun handleCreateNewWallet() {
        binding.btnCreateWalletAuth.isEnabled = false

        try {
            val newWallet = walletManager.createWallet()

            // Save encrypted (non-custodial)
            secureStorage.saveWallet(
                address = newWallet.address,
                privateKey = newWallet.privateKey,
                mnemonic = newWallet.mnemonic,
                loginMethod = "new_wallet"
            )

            Toast.makeText(this, "✅ Wallet created! Save your seed phrase!", Toast.LENGTH_LONG).show()
            saveLoginStateAndProceed(newWallet.address, "wallet_new")

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Failed to create wallet: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.btnCreateWalletAuth.isEnabled = true
        }
    }

    /**
     * Save ONLY login state and wallet address to regular SharedPreferences.
     * ⚠️ Private key & seed phrase are NEVER stored here — only in EncryptedSharedPreferences.
     * ⚠️ This data is NOT synced to Firebase.
     */
    private fun saveLoginStateAndProceed(address: String, method: String) {
        val prefs = getSharedPreferences("VibeAgentPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("login_method", method)
            // Only store wallet address (public data) — NOT private key or seed phrase!
            putString("wallet_address", address)
            putBoolean("wallet_connected", true)
            apply()
        }

        Toast.makeText(this, "🔗 Wallet connected! Entering VibeAgent...", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // ===== UI UPDATE =====
    private fun updateUI() {
        // Hide all forms
        binding.loginForm.visibility = View.GONE
        binding.registerForm.visibility = View.GONE
        binding.phraseForm.visibility = View.GONE

        when (currentMode) {
            "login" -> {
                binding.loginForm.visibility = View.VISIBLE
                binding.textTitle.text = "Welcome! 👋"
                binding.textSubtitle.text = "Sign in to your account to continue"
                binding.textToggleHint.text = "Don't have an account?"
                binding.btnToggleAuth.text = "Register now"
            }
            "register" -> {
                binding.registerForm.visibility = View.VISIBLE
                binding.textTitle.text = "Create New Account 🚀"
                binding.textSubtitle.text = "Register to start using VibeAgent"
                binding.textToggleHint.text = "Already have an account?"
                binding.btnToggleAuth.text = "Sign in here"
            }
            "phrase" -> {
                binding.phraseForm.visibility = View.VISIBLE
                binding.textTitle.text = "Import Wallet 🔑"
                binding.textSubtitle.text = "Connect your existing wallet"
                binding.textToggleHint.text = "Want to use email?"
                binding.btnToggleAuth.text = "Email login"
            }
        }
    }

    // ===== GOOGLE SIGN IN =====
    private val googleSignInLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: com.google.android.gms.common.api.ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        binding.btnGoogle.isEnabled = false

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    handleSuccessfulLogin(user?.email ?: "")
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    binding.btnGoogle.isEnabled = true
                }
            }
    }

    // ===== EMAIL AUTH =====
    private fun handleLogin() {
        val email = binding.editLoginEmail.text.toString().trim()
        val password = binding.editLoginPassword.text.toString().trim()

        if (email.isEmpty()) { binding.editLoginEmail.error = "Email is required"; return }
        if (password.isEmpty()) { binding.editLoginPassword.error = "Password is required"; return }

        binding.btnLoginSubmit.text = "Loading..."
        binding.btnLoginSubmit.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    handleSuccessfulLogin(email)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.btnLoginSubmit.text = "Sign In"
                    binding.btnLoginSubmit.isEnabled = true
                }
            }
    }

    private fun handleRegister() {
        val name = binding.editRegisterName.text.toString().trim()
        val email = binding.editRegisterEmail.text.toString().trim()
        val password = binding.editRegisterPassword.text.toString().trim()
        val confirmPassword = binding.editRegisterConfirmPassword.text.toString().trim()

        if (name.isEmpty()) { binding.editRegisterName.error = "Name is required"; return }
        if (email.isEmpty()) { binding.editRegisterEmail.error = "Email is required"; return }
        if (password.isEmpty() || password.length < 6) { binding.editRegisterPassword.error = "Password must be at least 6 characters"; return }
        if (confirmPassword != password) { binding.editRegisterConfirmPassword.error = "Passwords do not match"; return }
        if (!binding.checkTerms.isChecked) { Toast.makeText(this, "You must agree to the Terms & Conditions", Toast.LENGTH_SHORT).show(); return }

        binding.btnRegisterSubmit.text = "Loading..."
        binding.btnRegisterSubmit.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)
                    handleSuccessfulLogin(email)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    binding.btnRegisterSubmit.text = "Register Now"
                    binding.btnRegisterSubmit.isEnabled = true
                }
            }
    }

    private fun handleSuccessfulLogin(email: String) {
        val prefs = getSharedPreferences("VibeAgentPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_logged_in", true).apply()
        prefs.edit().putString("user_email", email).apply()

        Toast.makeText(this, "Successfully signed in!", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
