package com.vibeagent.app.util

data class ParsedCommand(
    val action: String,
    val params: Map<String, String> = emptyMap()
)

class CommandParser {

    fun parse(input: String): ParsedCommand {
        val lowerInput = input.lowercase().trim()

        return when {
            // Help command
            lowerInput == "help" || lowerInput == "bantuan" || lowerInput == "?" -> {
                ParsedCommand("help")
            }

            // Create wallet (English + Indonesian aliases)
            lowerInput.contains("create wallet") ||
            lowerInput.contains("create new wallet") ||
            lowerInput.contains("new wallet") ||
            lowerInput.contains("buat wallet") || 
            lowerInput.contains("wallet baru") -> {
                ParsedCommand("create_wallet")
            }

            // Import wallet
            lowerInput.contains("import") && lowerInput.contains("private key") -> {
                val privateKey = extractPrivateKey(input)
                ParsedCommand("import_wallet", mapOf("privateKey" to privateKey))
            }

            // Check balance (English + Indonesian aliases)
            lowerInput.contains("check balance") ||
            lowerInput.contains("balance") ||
            lowerInput.contains("cek saldo") ||
            lowerInput.contains("saldo") -> {
                ParsedCommand("balance")
            }

            // Send BNB (English + Indonesian aliases)
            lowerInput.contains("send") || lowerInput.contains("transfer") || lowerInput.contains("kirim") -> {
                val (amount, address) = extractSendParams(input)
                ParsedCommand("send", mapOf("amount" to amount, "toAddress" to address))
            }

            else -> ParsedCommand("unknown")
        }
    }

    private fun extractPrivateKey(input: String): String {
        // Look for hex string starting with 0x
        val hexPattern = Regex("0x[a-fA-F0-9]{64}")
        val match = hexPattern.find(input)
        if (match != null) {
            return match.value
        }

        // Look for 64-character hex string without 0x prefix
        val plainHexPattern = Regex("[a-fA-F0-9]{64}")
        val plainMatch = plainHexPattern.find(input)
        return if (plainMatch != null) {
            "0x${plainMatch.value}"
        } else {
            ""
        }
    }

    private fun extractSendParams(input: String): Pair<String, String> {
        // Extract amount (number followed by BNB)
        val amountPattern = Regex("([0-9]+\\.?[0-9]*)\\s*(?:bnb)?", RegexOption.IGNORE_CASE)
        val amountMatch = amountPattern.find(input)
        val amount = amountMatch?.groupValues?.get(1) ?: ""

        // Extract address (0x followed by 40 hex characters)
        val addressPattern = Regex("0x[a-fA-F0-9]{40}")
        val addressMatch = addressPattern.find(input)
        val address = addressMatch?.value ?: ""

        return Pair(amount, address)
    }
}
