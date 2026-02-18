package com.vibeagent.app.util

import com.vibeagent.app.model.WalletInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

data class TokenBalance(
    val symbol: String,
    val name: String,
    val balance: String,
    val contractAddress: String
)

class WalletManager {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // BSC MAINNET (for real wallets)
    private val bscMainnetRpc = "https://bsc-dataseed1.binance.org/"
    // BSC TESTNET (as fallback)
    private val bscTestnetRpc = "https://data-seed-prebsc-1-s1.binance.org:8545/"

    // Use mainnet by default for real wallet connections
    private var activeRpc = bscMainnetRpc
    private var isMainnet = true

    fun setNetwork(mainnet: Boolean) {
        isMainnet = mainnet
        activeRpc = if (mainnet) bscMainnetRpc else bscTestnetRpc
    }

    fun getNetworkName(): String = if (isMainnet) "BSC Mainnet" else "BSC Testnet"
    fun getChainId(): Int = if (isMainnet) 56 else 97

    // Generate a new wallet using Web3j (proper BIP39)
    fun createWallet(): WalletInfo {
        // Generate random mnemonic (seed phrase)
        val initialEntropy = ByteArray(16) // 128 bits = 12 words
        SecureRandom().nextBytes(initialEntropy)
        val mnemonic = org.web3j.crypto.MnemonicUtils.generateMnemonic(initialEntropy)

        // Derive wallet from mnemonic using BIP44 path m/44'/60'/0'/0/0
        val seed = org.web3j.crypto.MnemonicUtils.generateSeed(mnemonic, "")
        val masterKeypair = org.web3j.crypto.Bip32ECKeyPair.generateKeyPair(seed)
        val path = intArrayOf(
            44 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            60 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            0 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            0,
            0
        )
        val childKeypair = org.web3j.crypto.Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)
        val credentials = org.web3j.crypto.Credentials.create(childKeypair)

        return WalletInfo(
            address = credentials.address,
            privateKey = "0x" + credentials.ecKeyPair.privateKey.toString(16).padStart(64, '0'),
            balance = "0.0",
            mnemonic = mnemonic
        )
    }

    // Import wallet from private key using Web3j
    fun importWallet(privateKey: String): WalletInfo {
        val cleanKey = if (privateKey.startsWith("0x")) privateKey else "0x$privateKey"

        if (!isValidPrivateKey(cleanKey)) {
            throw IllegalArgumentException("Invalid private key. Must be 64 hex characters.")
        }

        // Use Web3j Credentials for proper secp256k1 derivation
        val credentials = org.web3j.crypto.Credentials.create(cleanKey)

        return WalletInfo(
            address = credentials.address,
            privateKey = cleanKey,
            balance = "0.0"
        )
    }

    // Import wallet from seed phrase using proper BIP39/BIP44 (SAME as Trust/MetaMask/Bitget!)
    fun importFromSeedPhrase(seedPhrase: String): WalletInfo {
        val words = seedPhrase.trim().lowercase().split("\\s+".toRegex())
        if (words.size != 12 && words.size != 24) {
            throw IllegalArgumentException("Seed phrase must be 12 or 24 words. You entered ${words.size} words.")
        }

        // ===== BIP39 WORD VALIDATION =====
        // Validate each word against the official BIP39 English wordlist
        val wordList = org.web3j.crypto.MnemonicUtils.getWords()
        val invalidWords = mutableListOf<String>()
        for (word in words) {
            if (!wordList.contains(word)) {
                invalidWords.add(word)
            }
        }
        if (invalidWords.isNotEmpty()) {
            throw IllegalArgumentException(
                "Invalid words in BIP39 wordlist: ${invalidWords.joinToString(", ")}. " +
                "Please double-check your seed phrase spelling."
            )
        }

        val cleanPhrase = words.joinToString(" ")

        // BIP39: Validate the entire mnemonic (checksum validation)
        if (!org.web3j.crypto.MnemonicUtils.validateMnemonic(cleanPhrase)) {
            throw IllegalArgumentException(
                "Invalid seed phrase (checksum error). Please make sure the word order is correct."
            )
        }

        // BIP39: Convert mnemonic to seed
        val seed = org.web3j.crypto.MnemonicUtils.generateSeed(cleanPhrase, "")

        // BIP32: Generate master key pair from seed
        val masterKeypair = org.web3j.crypto.Bip32ECKeyPair.generateKeyPair(seed)

        // BIP44: Derive path m/44'/60'/0'/0/0 (standard Ethereum/BSC path)
        val path = intArrayOf(
            44 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            60 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            0 or org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT,
            0,
            0
        )
        val childKeypair = org.web3j.crypto.Bip32ECKeyPair.deriveKeyPair(masterKeypair, path)
        val credentials = org.web3j.crypto.Credentials.create(childKeypair)

        return WalletInfo(
            address = credentials.address,
            privateKey = "0x" + credentials.ecKeyPair.privateKey.toString(16).padStart(64, '0'),
            balance = "0.0",
            mnemonic = cleanPhrase
        )
    }

    // Get native BNB balance
    suspend fun getBalance(address: String): String = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("method", "eth_getBalance")
                put("params", JSONArray().apply {
                    put(address)
                    put("latest")
                })
                put("id", 1)
            }

            val request = Request.Builder()
                .url(activeRpc)
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")

            val json = JSONObject(responseBody)

            if (json.has("error")) {
                throw Exception(json.getJSONObject("error").getString("message"))
            }

            val balanceHex = json.getString("result")
            val balanceWei = BigInteger(balanceHex.removePrefix("0x"), 16)
            val balanceBnb = BigDecimal(balanceWei).divide(BigDecimal("1000000000000000000"), 8, RoundingMode.HALF_UP)

            balanceBnb.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            throw Exception("Failed to check balance: ${e.message}")
        }
    }

    // Get BEP-20 token balance
    suspend fun getTokenBalance(walletAddress: String, contractAddress: String, decimals: Int = 18): String = withContext(Dispatchers.IO) {
        try {
            // balanceOf(address) function selector = 0x70a08231
            val paddedAddress = walletAddress.removePrefix("0x").lowercase().padStart(64, '0')
            val data = "0x70a08231$paddedAddress"

            val requestBody = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("method", "eth_call")
                put("params", JSONArray().apply {
                    put(JSONObject().apply {
                        put("to", contractAddress)
                        put("data", data)
                    })
                    put("latest")
                })
                put("id", 1)
            }

            val request = Request.Builder()
                .url(activeRpc)
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")

            val json = JSONObject(responseBody)

            if (json.has("error")) {
                return@withContext "0"
            }

            val resultHex = json.getString("result")
            if (resultHex == "0x" || resultHex.isEmpty()) {
                return@withContext "0"
            }

            val balanceWei = BigInteger(resultHex.removePrefix("0x"), 16)
            val divisor = BigDecimal.TEN.pow(decimals)
            val balance = BigDecimal(balanceWei).divide(divisor, 6, RoundingMode.HALF_UP)

            balance.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            "0"
        }
    }

    // Get all token balances for known BSC tokens
    suspend fun getAllTokenBalances(walletAddress: String): List<TokenBalance> = withContext(Dispatchers.IO) {
        val tokens = mutableListOf<TokenBalance>()

        // Known BEP-20 tokens on BSC Mainnet
        val knownTokens = listOf(
            Triple("USDT", "Tether USD", "0x55d398326f99059fF775485246999027B3197955"),
            Triple("USDC", "USD Coin", "0x8AC76a51cc950d9822D68b83fE1Ad97B32Cd580d"),
            Triple("BUSD", "Binance USD", "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56"),
            Triple("CAKE", "PancakeSwap", "0x0E09FaBB73Bd3Ade0a17ECC321fD13a19e81cE82"),
            Triple("WBNB", "Wrapped BNB", "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c"),
            Triple("ETH", "Ethereum (BSC)", "0x2170Ed0880ac9A755fd29B2688956BD959F933F8"),
            Triple("BTCB", "Bitcoin (BSC)", "0x7130d2A12B9BCbFAe4f2634d864A1Ee1Ce3Ead9c"),
            Triple("DOT", "Polkadot (BSC)", "0x7083609fCE4d1d8Dc0C979AAb8c869Ea2C873402"),
            Triple("LINK", "Chainlink (BSC)", "0xF8A0BF9cF54Bb92F17374d9e9A321E6a111a51bD"),
            Triple("XRP", "XRP (BSC)", "0x1D2F0da169ceB9fC7B3144628dB156f3F6c60dBE")
        )

        for ((symbol, name, contractAddress) in knownTokens) {
            try {
                val balance = getTokenBalance(walletAddress, contractAddress)
                if (balance != "0" && balance.toDoubleOrNull() ?: 0.0 > 0) {
                    tokens.add(TokenBalance(symbol, name, balance, contractAddress))
                }
            } catch (_: Exception) { }
        }

        tokens
    }

    suspend fun sendBnb(privateKey: String, toAddress: String, amount: String): String = withContext(Dispatchers.IO) {
        if (!isValidAddress(toAddress)) {
            throw IllegalArgumentException("Invalid destination address")
        }

        val amountValue = amount.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid amount")
        if (amountValue <= 0) {
            throw IllegalArgumentException("Amount must be greater than 0")
        }

        // Simulate transaction hash for demo
        val random = SecureRandom()
        val txHashBytes = ByteArray(32)
        random.nextBytes(txHashBytes)

        "0x" + txHashBytes.joinToString("") { "%02x".format(it) }
    }

    fun isValidAddress(address: String): Boolean {
        if (!address.startsWith("0x")) return false
        if (address.length != 42) return false
        return address.substring(2).all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
    }

    private fun isValidPrivateKey(privateKey: String): Boolean {
        val cleanKey = privateKey.removePrefix("0x")
        if (cleanKey.length != 64) return false
        return cleanKey.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
    }

    fun shortenAddress(address: String): String {
        return if (address.length > 10) {
            "${address.take(6)}...${address.takeLast(4)}"
        } else {
            address
        }
    }
}
