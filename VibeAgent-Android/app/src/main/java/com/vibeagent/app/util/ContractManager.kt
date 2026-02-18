package com.vibeagent.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.util.concurrent.TimeUnit

/**
 * ContractManager - Interaction with VibeAgentRegistry smart contract on BNB Chain
 */
class ContractManager {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        // ===== UPDATE AFTER DEPLOYMENT =====
        // Contract address will be set after deployment
        const val CONTRACT_ADDRESS = "0x49Ee39851956df07E5d3B430dC91e5A00B7E6059"

        // BSC RPC
        const val BSC_MAINNET_RPC = "https://bsc-dataseed1.binance.org/"
        const val BSC_TESTNET_RPC = "https://data-seed-prebsc-1-s1.binance.org:8545/"

        // Function selectors (keccak256 hash of function signature, first 4 bytes)
        const val FUNC_TOTAL_USERS = "0x24bc1a64"        // totalUsers()
        const val FUNC_IS_REGISTERED = "0xc3c5a547"      // isRegistered(address)
        const val FUNC_APP_NAME = "0x350cb118"            // APP_NAME()
        const val FUNC_APP_VERSION = "0xd28d8852"         // APP_VERSION()
        const val FUNC_GET_PROFILE = "0xf08f4f64"         // getProfile(address)
    }

    // Contract is deployed on BSC Testnet — always use Testnet RPC for contract calls
    private val activeRpc = BSC_TESTNET_RPC

    /**
     * Check if an address is registered in the VibeAgent Registry
     */
    suspend fun isUserRegistered(walletAddress: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val paddedAddress = walletAddress.removePrefix("0x").lowercase().padStart(64, '0')
            val data = "$FUNC_IS_REGISTERED$paddedAddress"

            val result = ethCall(data)
            // Result: 0x...0001 = true, 0x...0000 = false
            val cleaned = result.removePrefix("0x").trimStart('0')
            cleaned == "1"
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get total number of registered users
     */
    suspend fun getTotalUsers(): Long = withContext(Dispatchers.IO) {
        try {
            val result = ethCall(FUNC_TOTAL_USERS)
            val cleaned = result.removePrefix("0x")
            if (cleaned.isEmpty() || cleaned.all { it == '0' }) 0L
            else BigInteger(cleaned, 16).toLong()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Get user profile from contract
     */
    suspend fun getUserProfile(walletAddress: String): UserContractProfile? = withContext(Dispatchers.IO) {
        try {
            val paddedAddress = walletAddress.removePrefix("0x").lowercase().padStart(64, '0')
            val data = "$FUNC_GET_PROFILE$paddedAddress"

            val result = ethCall(data)
            val hex = result.removePrefix("0x")

            if (hex.length < 192) return@withContext null

            // Decode ABI: bool (32 bytes) + uint256 (32 bytes) + offset (32 bytes) + string
            val isRegistered = hex.substring(0, 64).trimStart('0') == "1"
            if (!isRegistered) return@withContext null

            val registeredAt = BigInteger(hex.substring(64, 128), 16).toLong()

            // Decode string: offset at bytes 128-192, length at offset, then string data
            val stringOffset = BigInteger(hex.substring(128, 192), 16).toInt() * 2
            val stringLength = BigInteger(hex.substring(stringOffset, stringOffset + 64), 16).toInt()
            val nicknameHex = hex.substring(stringOffset + 64, stringOffset + 64 + stringLength * 2)
            val nickname = String(nicknameHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray())

            UserContractProfile(
                isRegistered = true,
                registeredAt = registeredAt,
                nickname = nickname
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get app name from contract
     */
    suspend fun getAppName(): String = withContext(Dispatchers.IO) {
        try {
            val result = ethCall(FUNC_APP_NAME)
            decodeString(result)
        } catch (e: Exception) {
            "VibeAgent"
        }
    }

    /**
     * Build transaction data for register(string nickname)
     * User needs to sign & broadcast via WalletManager
     */
    fun buildRegisterTxData(nickname: String): String {
        // register(string) selector = 0x1aa3a008 (keccak256 dari "register(string)")
        val selector = "0xf2c298be"

        // ABI encode string parameter
        val offsetHex = "0000000000000000000000000000000000000000000000000000000000000020" // offset = 32
        val lengthHex = nickname.length.toString(16).padStart(64, '0')
        val dataHex = nickname.toByteArray().joinToString("") { "%02x".format(it) }
            .padEnd(64, '0') // pad to 32 bytes

        return "$selector$offsetHex$lengthHex$dataHex"
    }

    /**
     * Get contract address for display
     */
    fun getContractAddress(): String = CONTRACT_ADDRESS

    // ===== Internal Helpers =====

    private fun ethCall(data: String): String {
        val requestBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "eth_call")
            put("params", JSONArray().apply {
                put(JSONObject().apply {
                    put("to", CONTRACT_ADDRESS)
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
            throw Exception(json.getJSONObject("error").getString("message"))
        }

        return json.getString("result")
    }

    private fun decodeString(result: String): String {
        val hex = result.removePrefix("0x")
        if (hex.length < 192) return ""

        val stringOffset = BigInteger(hex.substring(0, 64), 16).toInt() * 2
        val stringLength = BigInteger(hex.substring(stringOffset, stringOffset + 64), 16).toInt()
        val stringHex = hex.substring(stringOffset + 64, stringOffset + 64 + stringLength * 2)
        return String(stringHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray())
    }
}

data class UserContractProfile(
    val isRegistered: Boolean,
    val registeredAt: Long,
    val nickname: String
)
