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

    // Groq API - Key loaded from local.properties via BuildConfig
    private val GROQ_API_KEY = com.vibeagent.app.BuildConfig.GROQ_API_KEY
    private val GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        // Use BSC Mainnet by default (for real wallets)
        walletManager.setNetwork(true)

        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName ?: "Kawan"

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.AI,
                content = """👋 Halo $userName! Saya VibeAgent.

Asisten keuangan pribadi berbasis AI untuk BNB Smart Chain.

🚀 Saya bisa membantu Anda:
• Menghubungkan wallet (Trust, MetaMask, Binance, OKX, Bitget)
• Membaca saldo BNB & token on-chain
• Analisis portofolio dengan AI
• Mengirim BNB

Klik "Connect" di atas untuk mulai, atau ketik "help"!"""
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

                addAiMessage("""🔗 Wallet Baru Dibuat!

📍 Address: ${walletManager.shortenAddress(newWallet.address)}
🔑 Private Key: ${newWallet.privateKey}

⚠️ SIMPAN private key dengan aman!

🔍 Membaca data on-chain...""", MessageType.WALLET)

                try {
                    val balance = withContext(Dispatchers.IO) {
                        walletManager.getBalance(newWallet.address)
                    }
                    _wallet.value = newWallet.copy(balance = balance)

                    addAiMessage("""✅ On-Chain Data:

📍 Address: ${newWallet.address}
💰 Saldo: $balance BNB
🔗 Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})

💡 Ketik "analisis" untuk AI analisis portofolio Anda!""", MessageType.BALANCE)
                } catch (e: Exception) {
                    _wallet.value = newWallet.copy(balance = "0.0")
                    addAiMessage("⚠️ Gagal baca saldo: ${e.message}", MessageType.ERROR)
                }
            } catch (e: Exception) {
                addAiMessage("❌ Gagal membuat wallet: ${e.message}", MessageType.ERROR)
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
🔍 Membaca saldo on-chain...""", MessageType.WALLET)

                // Read native BNB balance
                try {
                    val balance = withContext(Dispatchers.IO) {
                        walletManager.getBalance(address)
                    }
                    _wallet.value = walletInfo.copy(balance = balance)

                    // Also scan for BEP-20 tokens
                    addAiMessage("🔍 Scanning token BEP-20...")

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
                        tokenList.append("\n📊 Token BEP-20:\n")
                        for (token in tokens) {
                            tokenList.append("• ${token.symbol}: ${token.balance}\n")
                        }
                    } else {
                        tokenList.append("\n📊 Tidak ada token BEP-20 standar terdeteksi.")
                        tokenList.append("\n💡 Token custom mungkin tidak terdeteksi di scan ini.")
                    }

                    tokenList.append("\n\nKetik \"analisis\" untuk AI analisis portofolio!")

                    addAiMessage(tokenList.toString(), MessageType.BALANCE)

                } catch (e: Exception) {
                    addAiMessage("⚠️ Gagal membaca saldo: ${e.message}", MessageType.ERROR)
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
            addAiMessage("⚠️ Anda belum menghubungkan wallet. Klik tombol \"Connect\" di atas.", MessageType.ERROR)
            return
        }

        _isProcessing.value = true

        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.USER,
                content = "Analisis portofolio saya"
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
                    "Tidak ada token BEP-20 terdeteksi"
                }

                val walletContext = """
Analisis portofolio crypto pengguna berdasarkan data on-chain REAL berikut:
- Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})
- Alamat Wallet: ${updatedWallet.address}
- Saldo BNB Native: ${updatedWallet.balance} BNB

Token BEP-20 yang dimiliki:
$tokenInfo

Berikan analisis lengkap:
1. Ringkasan portofolio (total aset)
2. Distribusi aset dan diversifikasi
3. Saran strategi (DCA, hold, diversifikasi, dll)
4. Risiko dan tips keamanan
                """.trim()

                val response = withContext(Dispatchers.IO) {
                    callGroqAPI(walletContext)
                }
                addAiMessage(response)
            } catch (e: Exception) {
                addAiMessage("⚠️ Gagal menganalisis: ${e.message}")
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
                if (command.action == "unknown") {
                    if (input.lowercase().contains("analisis") || input.lowercase().contains("analyze")) {
                        handleAIAnalysis()
                    } else {
                        handleChat(input)
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
            addAiMessage("⚠️ Anda belum menghubungkan wallet. Klik \"Connect\" di header.", MessageType.ERROR)
            return
        }

        addAiMessage("🔍 Menganalisis portofolio on-chain...")

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
            "Tidak ada token BEP-20 standar terdeteksi"
        }

        val analysisPrompt = """
Analisis portofolio crypto pengguna dari data on-chain BSC:
- Network: ${walletManager.getNetworkName()}
- Address: ${updatedWallet.address}
- Saldo BNB Native: ${updatedWallet.balance} BNB

Token BEP-20:
$tokenInfo

Berikan:
1. Ringkasan status portofolio
2. Nilai estimasi dalam USD
3. Analisis diversifikasi
4. Saran strategi
5. Tips keamanan wallet
        """.trim()

        try {
            val response = withContext(Dispatchers.IO) {
                callGroqAPI(analysisPrompt)
            }
            addAiMessage(response)
        } catch (e: Exception) {
            addAiMessage("⚠️ Gagal menganalisis: ${e.message}")
        }
    }

    private suspend fun handleChat(prompt: String) {
        try {
            val enrichedPrompt = if (_wallet.value != null) {
                val w = _wallet.value!!
                val tokenInfo = if (tokenBalances.isNotEmpty()) {
                    "\nToken BEP-20: " + tokenBalances.joinToString(", ") { "${it.symbol}: ${it.balance}" }
                } else ""

                """User memiliki wallet ${walletManager.getNetworkName()}:
- Address: ${w.address}
- Saldo BNB: ${w.balance}$tokenInfo

Pertanyaan user: $prompt"""
            } else {
                prompt
            }

            val response = withContext(Dispatchers.IO) {
                callGroqAPI(enrichedPrompt)
            }
            addAiMessage(response)
        } catch (e: Exception) {
            addAiMessage("⚠️ Maaf, terjadi kesalahan koneksi AI: ${e.message}")
        }
    }

    private fun callGroqAPI(prompt: String): String {
        val messagesArray = JSONArray()

        val systemMessage = JSONObject()
        systemMessage.put("role", "system")
        systemMessage.put("content", """Kamu adalah VibeAgent, asisten keuangan pribadi berbasis AI untuk BNB Smart Chain.
Jawab dengan bahasa Indonesia yang ramah, ringkas, dan informatif.
Gunakan emoji untuk membuat percakapan lebih hidup.
Fokus pada topik crypto, blockchain, dan keuangan.
Jika user memiliki wallet terhubung, gunakan data on-chain tersebut untuk memberikan advice yang relevan.
Data yang kamu terima adalah data REAL dari blockchain, bukan simulasi.""")
        messagesArray.put(systemMessage)

        val userMessage = JSONObject()
        userMessage.put("role", "user")
        userMessage.put("content", prompt)
        messagesArray.put(userMessage)

        val requestBody = JSONObject()
        requestBody.put("model", "llama-3.3-70b-versatile")
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
        val responseBody = response.body?.string() ?: throw Exception("Response kosong")

        if (!response.isSuccessful) {
            throw Exception("API Error ${response.code}: $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        val choices = jsonResponse.getJSONArray("choices")
        if (choices.length() > 0) {
            val firstChoice = choices.getJSONObject(0)
            val message = firstChoice.getJSONObject("message")
            return message.getString("content")
        }

        return "Maaf, saya tidak dapat memproses permintaan tersebut saat ini."
    }

    private fun handleHelp() {
        addAiMessage("""📚 Daftar Perintah VibeAgent:

🔗 CONNECT
• Klik "Connect" di header → pilih wallet atau input manual

💼 WALLET
• "buat wallet baru" - Membuat wallet baru
• "import private key [key]" - Import wallet

💰 SALDO
• "cek saldo" - Cek saldo BNB + token on-chain

💸 TRANSFER
• "kirim [jumlah] BNB ke [alamat]" - Kirim BNB

🤖 ANALISIS AI
• "analisis" - AI analisis portofolio + token Anda

🔗 Network: ${walletManager.getNetworkName()} (Chain ID: ${walletManager.getChainId()})""")
    }

    private suspend fun handleCreateWallet() {
        try {
            val newWallet = walletManager.createWallet()
            _wallet.value = newWallet
            _isWalletConnected.value = true

            addAiMessage("""✅ Wallet berhasil dibuat & terhubung!

📍 Address:
${newWallet.address}

🔑 Private Key:
${newWallet.privateKey}

⚠️ SIMPAN private key dengan aman!

💡 Untuk BNB gratis (testnet):
https://testnet.bnbchain.org/faucet-smart""", MessageType.WALLET)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(newWallet.address)
                }
                _wallet.value = newWallet.copy(balance = balance)
            } catch (_: Exception) { }
        } catch (e: Exception) {
            addAiMessage("❌ Gagal membuat wallet: ${e.message}", MessageType.ERROR)
        }
    }

    private suspend fun handleImportWallet(privateKey: String?) {
        if (privateKey.isNullOrBlank()) {
            addAiMessage("""⚠️ Mohon sertakan private key.

Contoh: "Import private key 0x1234..."""", MessageType.ERROR)
            return
        }

        try {
            val imported = walletManager.importWallet(privateKey)
            _wallet.value = imported
            _isWalletConnected.value = true

            addAiMessage("""✅ Wallet berhasil diimport!

📍 Address: ${imported.address}
💰 Mengecek saldo on-chain...""", MessageType.WALLET)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(imported.address)
                }
                _wallet.value = imported.copy(balance = balance)
                addAiMessage("💰 Saldo: $balance BNB", MessageType.BALANCE)
            } catch (e: Exception) {
                addAiMessage("⚠️ Gagal mengecek saldo: ${e.message}", MessageType.ERROR)
            }
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Private key tidak valid."}", MessageType.ERROR)
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
            addAiMessage("⚠️ Belum ada wallet. Klik \"Connect\" atau ketik \"buat wallet baru\".", MessageType.ERROR)
            return
        }

        addAiMessage("🔍 Mengecek saldo on-chain (${walletManager.getNetworkName()})...")

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
                "\n\n📊 Token BEP-20:\n" + tokens.joinToString("\n") { "• ${it.symbol}: ${it.balance}" }
            } else {
                ""
            }

            addAiMessage("""💰 Saldo Wallet (${walletManager.getNetworkName()}):

$balance BNB

📍 ${currentWallet.address}$tokenText""", MessageType.BALANCE)
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Gagal mengecek saldo."}", MessageType.ERROR)
        }
    }

    private suspend fun handleSendBnb(amount: String?, toAddress: String?) {
        val currentWallet = _wallet.value
        if (currentWallet == null) {
            addAiMessage("⚠️ Belum ada wallet. Klik \"Connect\" dulu.", MessageType.ERROR)
            return
        }

        if (amount.isNullOrBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            addAiMessage("""⚠️ Jumlah BNB tidak valid.

Contoh: "Kirim 0.01 BNB ke 0x..."""", MessageType.ERROR)
            return
        }

        if (toAddress.isNullOrBlank() || !walletManager.isValidAddress(toAddress)) {
            addAiMessage("""⚠️ Alamat tujuan tidak valid.

Contoh: "Kirim 0.01 BNB ke 0x1234...abcd"""", MessageType.ERROR)
            return
        }

        addAiMessage("""📤 Konfirmasi Transaksi:

💸 Jumlah: $amount BNB
📍 Ke: $toAddress

⏳ Memproses...""")

        try {
            val txHash = walletManager.sendBnb(currentWallet.privateKey, toAddress, amount)

            try {
                val balance = withContext(Dispatchers.IO) {
                    walletManager.getBalance(currentWallet.address)
                }
                _wallet.value = currentWallet.copy(balance = balance)
            } catch (_: Exception) { }

            addAiMessage("""✅ Transaksi Berhasil!

💸 $amount BNB → $toAddress

🔗 TX Hash: $txHash""", MessageType.TRANSACTION)
        } catch (e: Exception) {
            addAiMessage("❌ ${e.message ?: "Transaksi gagal."}", MessageType.ERROR)
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
