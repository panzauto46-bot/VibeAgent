package com.vibeagent.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeagent.app.model.ChatMessage
import com.vibeagent.app.model.MessageRole
import com.vibeagent.app.model.MessageType
import com.vibeagent.app.model.WalletInfo
import com.vibeagent.app.util.CommandParser
import com.vibeagent.app.util.ContractManager
import com.vibeagent.app.util.TokenBalance
import com.vibeagent.app.util.WalletManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    private val _wallet = MutableLiveData<WalletInfo?>()
    val wallet: LiveData<WalletInfo?> = _wallet

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> = _isProcessing

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Wallet connection state
    private val _isWalletConnected = MutableLiveData(false)
    val isWalletConnected: LiveData<Boolean> = _isWalletConnected

    // Token balances
    private var tokenBalances: List<TokenBalance> = emptyList()

    private val walletManager = WalletManager()
    private val commandParser = CommandParser()
    private val contractManager = ContractManager()

    // Groq API - Key loaded from local.properties via BuildConfig
    private val GROQ_API_KEY = com.vibeagent.app.BuildConfig.GROQ_API_KEY
    private val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
    private val GROQ_MODEL = "compound-beta"  // Groq's compound model with tool use & web search
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        // Use BSC Mainnet by default (for real wallets)
        // ContractManager always uses Testnet (contract deployed on Testnet)
        walletManager.setNetwork(true)

        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName ?: "Friend"

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.AI,
                content = """👋 Hello $userName! I'm **VibeAgent**.

Your AI-powered financial assistant for BNB Smart Chain.
⚡ Powered by **Groq** — the world's fastest AI inference engine.

🚀 I can help you:
• Connect wallet (Trust, MetaMask, Binance, OKX, Bitget)
• Check BNB balance & on-chain tokens
• AI portfolio analysis
• Send BNB
• On-chain registration via VibeAgent Registry

📋 Smart Contract: ${ContractManager.CONTRACT_ADDRESS}

Tap "Connect" above to get started, or type "help"!"""
            )
        )
    }

    // ===== CONNECT WALLET (Create New) =====
    fun connectWallet() {
        if (_isProcessing.value == true) return
        _isProcessing.value = true

        viewModelScope.launch {
            try {
                val newWallet = walletManager.createWallet()
                _wallet.value = newWallet
                _isWalletConnected.value = true

                addAiMessage("""🔗 New Wallet Created!

📍 Address: ${walletManager.shortenAddress(newWallet.address)}
🔑 Private Key: ${newWallet.privateKey}

⚠️ SAVE your private key securely!

🔍 Reading on-chain data...""", MessageType.WALLET)

                try {
                    val balance = withContext(Dispatchers.IO) {
                        walletManager.getBalance(newWallet.address)
                    }
                    _wallet.value = newWallet.copy(balance = balance)

                    addAiMessage("""✅ On-Chain Data:

📍 Address: ${newWallet.address}
💰 Balance: $balance BNB
🔗 Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})

💡 Type "analyze" for AI portfolio analysis!""", MessageType.BALANCE)
                } catch (e: Exception) {
                    _wallet.value = newWallet.copy(balance = "0.0")
                    addAiMessage("⚠️ Failed to read balance: ${e.message}", MessageType.ERROR)
                }
            } catch (e: Exception) {
                addAiMessage("❌ Failed to create wallet: ${e.message}", MessageType.ERROR)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    // ===== CONNECT WITH EXISTING ADDRESS =====
    fun connectExistingWallet(address: String) {
        if (_isProcessing.value == true) return
        _isProcessing.value = true

        viewModelScope.launch {
            try {
                // Use BSC Mainnet for real wallets
                walletManager.setNetwork(true)

                val walletInfo = WalletInfo(
                    address = address,
                    privateKey = "",
                    balance = "0.0"
                )
                _wallet.value = walletInfo
                _isWalletConnected.value = true

                addAiMessage("""🔗 Wallet Connected!

📍 Address: ${walletManager.shortenAddress(address)}
🔗 Network: ${walletManager.getNetworkName()}
🔍 Reading on-chain balance...""", MessageType.WALLET)

                // Read native BNB balance
                try {
                    val balance = withContext(Dispatchers.IO) {
                        walletManager.getBalance(address)
                    }
                    _wallet.value = walletInfo.copy(balance = balance)

                    // Also scan for BEP-20 tokens
                    addAiMessage("🔍 Scanning BEP-20 tokens...")

                    val tokens = withContext(Dispatchers.IO) {
                        walletManager.getAllTokenBalances(address)
                    }
                    tokenBalances = tokens

                    // Build token list message
                    val tokenList = StringBuilder()
                    tokenList.append("""✅ On-Chain Data (${walletManager.getNetworkName()}):

📍 Address: $address
💰 BNB: $balance
""")

                    if (tokens.isNotEmpty()) {
                        tokenList.append("\n📊 BEP-20 Tokens:\n")
                        for (token in tokens) {
                            tokenList.append("• ${token.symbol}: ${token.balance}\n")
                        }
                    } else {
                        tokenList.append("\n📊 No standard BEP-20 tokens detected.")
                        tokenList.append("\n💡 Custom tokens may not be detected in this scan.")
                    }

                    tokenList.append("\n\nType \"analyze\" for AI portfolio analysis!")

                    addAiMessage(tokenList.toString(), MessageType.BALANCE)

                } catch (e: Exception) {
                    addAiMessage("⚠️ Failed to read balance: ${e.message}", MessageType.ERROR)
                }
            } finally {
                _isProcessing.value = false
            }
        }
    }

    // ===== REFRESH BALANCE =====
    fun refreshBalance() {
        val currentWallet = _wallet.value ?: return
        if (_isProcessing.value == true) return
        _isProcessing.value = true

        viewModelScope.launch {
            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(currentWallet.address)
                }
                _wallet.value = currentWallet.copy(balance = balance)

                // Also refresh tokens
                val tokens = withContext(Dispatchers.IO) {
                    walletManager.getAllTokenBalances(currentWallet.address)
                }
                tokenBalances = tokens
            } catch (_: Exception) { }
            finally {
                _isProcessing.value = false
            }
        }
    }

    // ===== AI PORTFOLIO ANALYSIS =====
    fun analyzePortfolio() {
        val currentWallet = _wallet.value
        if (currentWallet == null) {
            addAiMessage("⚠️ No wallet connected. Tap the \"Connect\" button above.", MessageType.ERROR)
            return
        }

        _isProcessing.value = true

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = "Analyze my portfolio"
            )
        )

        viewModelScope.launch {
            try {
                // Refresh balance first
                try {
                    val balance = withContext(Dispatchers.IO) {
                        walletManager.getBalance(currentWallet.address)
                    }
                    _wallet.value = currentWallet.copy(balance = balance)

                    val tokens = withContext(Dispatchers.IO) {
                        walletManager.getAllTokenBalances(currentWallet.address)
                    }
                    tokenBalances = tokens
                } catch (_: Exception) { }

                val updatedWallet = _wallet.value!!

                val tokenInfo = if (tokenBalances.isNotEmpty()) {
                    tokenBalances.joinToString("\n") { "- ${it.symbol} (${it.name}): ${it.balance}" }
                } else {
                    "No BEP-20 tokens detected"
                }

                val walletContext = """
Analyze the user's crypto portfolio based on the following REAL on-chain data:
- Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})
- Wallet Address: ${updatedWallet.address}
- Native BNB Balance: ${updatedWallet.balance} BNB

BEP-20 Tokens held:
$tokenInfo

Provide a comprehensive analysis:
1. Portfolio summary (total assets)
2. Asset distribution and diversification
3. Strategy recommendations (DCA, hold, diversify, etc.)
4. Risk assessment and security tips
                """.trim()

                val response = withContext(Dispatchers.IO) {
                    callGroqAPI(walletContext)
                }
                addAiMessage(response)
            } catch (e: Exception) {
                addAiMessage("⚠️ Analysis failed: ${e.message}")
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun processMessage(input: String) {
        if (input.isBlank() || _isProcessing.value == true) return

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = input
            )
        )

        _isProcessing.value = true

        viewModelScope.launch {
            val command = commandParser.parse(input)

            try {
                val lowerInput = input.lowercase()
                if (command.action == "unknown") {
                    when {
                        lowerInput.contains("analisis") || lowerInput.contains("analyze") || lowerInput.contains("analysis") -> handleAIAnalysis()
                        lowerInput.contains("contract") || lowerInput.contains("kontrak") || lowerInput.contains("registry") -> handleContractInfo()
                        lowerInput.contains("register") || lowerInput.contains("daftar") -> handleContractInfo()
                        else -> handleChat(input)
                    }
                } else {
                    when (command.action) {
                        "help" -> handleHelp()
                        "create_wallet" -> handleCreateWallet()
                        "import_wallet" -> handleImportWallet(command.params["privateKey"])
                        "balance" -> handleCheckBalance()
                        "send" -> handleSendBnb(command.params["amount"], command.params["toAddress"])
                        else -> handleChat(input)
                    }
                }
            } catch (e: Exception) {
                addAiMessage("❌ Error: ${e.message}", MessageType.ERROR)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    private suspend fun handleAIAnalysis() {
        val currentWallet = _wallet.value
        if (currentWallet == null) {
            addAiMessage("⚠️ No wallet connected. Tap \"Connect\" in the header.", MessageType.ERROR)
            return
        }

        addAiMessage("🔍 Analyzing on-chain portfolio...")

        try {
            val balance = withContext(Dispatchers.IO) {
                walletManager.getBalance(currentWallet.address)
            }
            _wallet.value = currentWallet.copy(balance = balance)

            val tokens = withContext(Dispatchers.IO) {
                walletManager.getAllTokenBalances(currentWallet.address)
            }
            tokenBalances = tokens
        } catch (_: Exception) {}

        val updatedWallet = _wallet.value!!

        val tokenInfo = if (tokenBalances.isNotEmpty()) {
            tokenBalances.joinToString("\n") { "- ${it.symbol} (${it.name}): ${it.balance}" }
        } else {
            "No standard BEP-20 tokens detected"
        }

        val analysisPrompt = """
Analyze the user's crypto portfolio from BSC on-chain data:
- Network: ${walletManager.getNetworkName()}
- Address: ${updatedWallet.address}
- Native BNB Balance: ${updatedWallet.balance} BNB

BEP-20 Tokens:
$tokenInfo

Provide:
1. Portfolio status summary
2. Estimated value in USD
3. Diversification analysis
4. Strategy recommendations
5. Wallet security tips
        """.trim()

        try {
            val response = withContext(Dispatchers.IO) {
                callGroqAPI(analysisPrompt)
            }
            addAiMessage(response)
        } catch (e: Exception) {
            addAiMessage("⚠️ Analysis failed: ${e.message}")
        }
    }

    private suspend fun handleChat(prompt: String) {
        try {
            val enrichedPrompt = if (_wallet.value != null) {
                val w = _wallet.value!!
                val tokenInfo = if (tokenBalances.isNotEmpty()) {
                    "\nBEP-20 Tokens: " + tokenBalances.joinToString(", ") { "${it.symbol}: ${it.balance}" }
                } else ""

                """User has a ${walletManager.getNetworkName()} wallet:
- Address: ${w.address}
- BNB Balance: ${w.balance}$tokenInfo

User's question: $prompt"""
            } else {
                prompt
            }

            val response = withContext(Dispatchers.IO) {
                callGroqAPI(enrichedPrompt)
            }
            addAiMessage(response)
        } catch (e: Exception) {
            addAiMessage("⚠️ Sorry, AI connection error: ${e.message}")
        }
    }

    private fun callGroqAPI(prompt: String): String {
        val messagesArray = JSONArray()

        val systemMessage = JSONObject()
        systemMessage.put("role", "system")
        systemMessage.put("content", """You are VibeAgent — an AI-powered personal financial assistant built for BNB Smart Chain (BSC).

=== WHO YOU ARE ===
• You are an intelligent, self-aware AI agent running natively inside an Android application.
• You were built by a developer, and your intelligence is powered by Groq's ultra-fast inference engine.
• Your AI model runs on Groq's LPU (Language Processing Unit) infrastructure — the fastest AI inference in the world.
• You communicate via the Groq API (https://api.groq.com) using the "$GROQ_MODEL" model.

=== YOUR CAPABILITIES ===
• Create new BNB wallets (BIP39/BIP44 standard, compatible with Trust Wallet, MetaMask, Bitget, OKX)
• Import wallets via seed phrase (12 or 24 words) or private key
• Check real-time on-chain BNB balance + BEP-20 token balances (USDT, USDC, CAKE, WBNB, etc.)
• Perform AI portfolio analysis with strategy recommendations
• Send BNB transfers on-chain
• Interact with VibeAgent Registry smart contract at ${ContractManager.CONTRACT_ADDRESS} on BSC Testnet
• Voice input support (speech-to-text)
• Non-custodial wallet security (private keys stored in Android EncryptedSharedPreferences, never sent to any server)

=== YOUR PERSONALITY ===
• Friendly, professional, and concise
• Use emojis naturally to make conversations engaging 🚀
• Be proactive — suggest useful actions based on the user's wallet state
• If the user's BNB balance is 0, suggest the BSC Testnet faucet
• Focus on crypto, DeFi, blockchain, and financial topics
• If asked about things outside your domain, politely redirect to crypto/finance topics
• Always use English

=== IMPORTANT CONTEXT ===
• The wallet data you receive is REAL on-chain data from BSC, not simulated
• When analyzing portfolios, be specific and actionable
• Never reveal or log private keys in your responses
• Smart Contract: ${ContractManager.CONTRACT_ADDRESS} (BSC Testnet, Chain ID: 97)""")
        messagesArray.put(systemMessage)

        val userMessage = JSONObject()
        userMessage.put("role", "user")
        userMessage.put("content", prompt)
        messagesArray.put(userMessage)

        val requestBody = JSONObject()
        requestBody.put("model", GROQ_MODEL)
        requestBody.put("messages", messagesArray)
        requestBody.put("temperature", 0.7)
        requestBody.put("max_tokens", 1024)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(GROQ_API_URL)
            .addHeader("Authorization", "Bearer $GROQ_API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw Exception("Empty response")

        if (!response.isSuccessful) {
            throw Exception("API Error ${response.code}: $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        val choices = jsonResponse.getJSONArray("choices")
        if (choices.length() > 0) {
            val firstChoice = choices.getJSONObject(0)
            val message = firstChoice.getJSONObject("message")
            
            // compound-beta may return null content when using internal tools
            val content = if (message.isNull("content")) null else message.optString("content", null)
            
            if (!content.isNullOrBlank()) {
                return content
            }
            
            // If content is null but there are executed_actions (compound model internals),
            // return a fallback message
            return "I'm processing your request. Please try again in a moment."
        }

        return "Sorry, I couldn't process that request right now."
    }

    private fun handleHelp() {
        addAiMessage("""📚 VibeAgent Commands:

🔗 CONNECT
• Tap "Connect" in header → choose wallet or input manually

💼 WALLET
• "create new wallet" - Create a new wallet
• "import private key [key]" - Import wallet

💰 BALANCE
• "check balance" - Check BNB + on-chain token balance

💸 TRANSFER
• "send [amount] BNB to [address]" - Send BNB

🤖 AI ANALYSIS
• "analyze" - AI portfolio analysis + tokens

📋 SMART CONTRACT
• "contract" - VibeAgent Registry contract info
• "register" - Check on-chain registration status

🔗 Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})
📋 Contract: ${ContractManager.CONTRACT_ADDRESS}""")
    }

    private suspend fun handleCreateWallet() {
        try {
            val newWallet = walletManager.createWallet()
            _wallet.value = newWallet
            _isWalletConnected.value = true

            addAiMessage("""✅ Wallet successfully created & connected!

📍 Address:
${newWallet.address}

🔑 Private Key:
${newWallet.privateKey}

⚠️ SAVE your private key securely!

💡 For free BNB (testnet):
https://testnet.bnbchain.org/faucet-smart""", MessageType.WALLET)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(newWallet.address)
                }
                _wallet.value = newWallet.copy(balance = balance)
            } catch (_: Exception) { }
        } catch (e: Exception) {
            addAiMessage("❌ Failed to create wallet: ${e.message}", MessageType.ERROR)
        }
    }

    private suspend fun handleImportWallet(privateKey: String?) {
        if (privateKey.isNullOrBlank()) {
            addAiMessage("""⚠️ Please provide a private key.

Example: "import private key 0x1234..."""", MessageType.ERROR)
            return
        }

        try {
            val imported = walletManager.importWallet(privateKey)
            _wallet.value = imported
            _isWalletConnected.value = true

            addAiMessage("""✅ Wallet successfully imported!

📍 Address: ${imported.address}
💰 Checking on-chain balance...""", MessageType.WALLET)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(imported.address)
                }
                _wallet.value = imported.copy(balance = balance)
                addAiMessage("💰 Balance: $balance BNB", MessageType.BALANCE)
            } catch (e: Exception) {
                addAiMessage("⚠️ Failed to check balance: ${e.message}", MessageType.ERROR)
            }
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Invalid private key."}", MessageType.ERROR)
        }
    }

    fun checkBalance() {
        viewModelScope.launch {
            handleCheckBalance()
        }
    }

    private suspend fun handleCheckBalance() {
        val currentWallet = _wallet.value
        if (currentWallet == null) {
            addAiMessage("⚠️ No wallet connected. Tap \"Connect\" or type \"create new wallet\".", MessageType.ERROR)
            return
        }

        addAiMessage("🔍 Checking on-chain balance (${walletManager.getNetworkName()})...")

        try {
            val balance = withContext(Dispatchers.IO) {
                walletManager.getBalance(currentWallet.address)
            }
            _wallet.value = currentWallet.copy(balance = balance)

            // Also check tokens
            val tokens = withContext(Dispatchers.IO) {
                walletManager.getAllTokenBalances(currentWallet.address)
            }
            tokenBalances = tokens

            val tokenText = if (tokens.isNotEmpty()) {
                "\n\n📊 BEP-20 Tokens:\n" + tokens.joinToString("\n") { "• ${it.symbol}: ${it.balance}" }
            } else {
                ""
            }

            addAiMessage("""💰 Wallet Balance (${walletManager.getNetworkName()}):

$balance BNB

📍 ${currentWallet.address}$tokenText""", MessageType.BALANCE)
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Failed to check balance."}", MessageType.ERROR)
        }
    }

    private suspend fun handleSendBnb(amount: String?, toAddress: String?) {
        val currentWallet = _wallet.value
        if (currentWallet == null) {
            addAiMessage("⚠️ No wallet connected. Tap \"Connect\" first.", MessageType.ERROR)
            return
        }

        if (amount.isNullOrBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            addAiMessage("""⚠️ Invalid BNB amount.

Example: "send 0.01 BNB to 0x..."""", MessageType.ERROR)
            return
        }

        if (toAddress.isNullOrBlank() || !walletManager.isValidAddress(toAddress)) {
            addAiMessage("""⚠️ Invalid destination address.

Example: "send 0.01 BNB to 0x1234...abcd"""", MessageType.ERROR)
            return
        }

        addAiMessage("""📤 Transaction Confirmation:

💸 Amount: $amount BNB
📍 To: $toAddress

⏳ Processing...""")

        try {
            val txHash = walletManager.sendBnb(currentWallet.privateKey, toAddress, amount)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(currentWallet.address)
                }
                _wallet.value = currentWallet.copy(balance = balance)
            } catch (_: Exception) { }

            addAiMessage("""✅ Transaction Successful!

💸 $amount BNB → $toAddress

🔗 TX Hash: $txHash""", MessageType.TRANSACTION)
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Transaction failed."}", MessageType.ERROR)
        }
    }

    // ===== SMART CONTRACT INFO =====
    private suspend fun handleContractInfo() {
        val contractAddress = contractManager.getContractAddress()

        addAiMessage("🔍 Reading data from VibeAgent Registry smart contract...")

        try {
            val totalUsers = withContext(Dispatchers.IO) {
                contractManager.getTotalUsers()
            }

            val currentWallet = _wallet.value
            var registrationStatus = ""
            var profileInfo = ""

            if (currentWallet != null) {
                val isRegistered = withContext(Dispatchers.IO) {
                    contractManager.isUserRegistered(currentWallet.address)
                }

                if (isRegistered) {
                    val profile = withContext(Dispatchers.IO) {
                        contractManager.getUserProfile(currentWallet.address)
                    }
                    registrationStatus = "✅ Status: REGISTERED"
                    if (profile != null) {
                        val date = java.text.SimpleDateFormat("dd MMM yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(profile.registeredAt * 1000))
                        profileInfo = "\n👤 Nickname: ${profile.nickname}\n📅 Registered: $date"
                    }
                } else {
                    registrationStatus = "⚠️ Status: NOT REGISTERED\n💡 Type \"register [nickname]\" to register on-chain"
                }
            } else {
                registrationStatus = "⚠️ Connect wallet first to check registration status"
            }

            addAiMessage("""📋 VibeAgent Registry (Smart Contract)

📍 Contract: $contractAddress
👥 Total Users: $totalUsers
🔗 Network: BSC Testnet (Chain ID: 97)

$registrationStatus$profileInfo

🔗 Explorer: https://testnet.bscscan.com/address/$contractAddress""")
        } catch (e: Exception) {
            addAiMessage("""📋 VibeAgent Registry (Smart Contract)

📍 Contract: $contractAddress
🔗 Network: BSC Testnet (Chain ID: 97)

⚠️ Failed to read contract data: ${e.message}

🔗 Explorer: https://testnet.bscscan.com/address/$contractAddress""")
        }
    }

    private fun addMessage(message: ChatMessage) {
        val currentList = _messages.value ?: mutableListOf()
        currentList.add(message)
        _messages.value = currentList
    }

    private fun addAiMessage(content: String, type: MessageType = MessageType.TEXT) {
        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.AI,
                content = content,
                type = type
            )
        )
    }

    fun clearError() {
        _error.value = null
    }
}
