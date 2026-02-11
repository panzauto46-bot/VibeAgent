package com.vibeagent.app.model

data class WalletInfo(
    val address: String,
    val privateKey: String,
    val balance: String = "0.0",
    val mnemonic: String = ""
)
