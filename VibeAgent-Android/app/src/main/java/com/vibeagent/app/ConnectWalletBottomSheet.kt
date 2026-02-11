package com.vibeagent.app

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vibeagent.app.databinding.BottomSheetConnectWalletBinding
import com.vibeagent.app.viewmodel.ChatViewModel

class ConnectWalletBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConnectWalletBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel

    // Wallet app package names for deep linking
    private val walletApps = mapOf(
        "trust" to WalletAppInfo(
            packageName = "com.wallet.crypto.trustapp",
            deepLink = "trust://",
            storeName = "Trust Wallet"
        ),
        "metamask" to WalletAppInfo(
            packageName = "io.metamask",
            deepLink = "metamask://",
            storeName = "MetaMask"
        ),
        "binance" to WalletAppInfo(
            packageName = "com.binance.dev",
            deepLink = "bnc://",
            storeName = "Binance"
        ),
        "okx" to WalletAppInfo(
            packageName = "com.okinc.okex.gp",
            deepLink = "okx://",
            storeName = "OKX"
        ),
        "bitget" to WalletAppInfo(
            packageName = "com.bitget.exchange",
            deepLink = "bitget://",
            storeName = "Bitget"
        )
    )

    data class WalletAppInfo(
        val packageName: String,
        val deepLink: String,
        val storeName: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetConnectWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        setupListeners()
    }

    private fun setupListeners() {
        // Trust Wallet
        binding.btnTrustWallet.setOnClickListener {
            openWalletApp("trust")
        }

        // MetaMask
        binding.btnMetaMask.setOnClickListener {
            openWalletApp("metamask")
        }

        // Binance Wallet
        binding.btnBinanceWallet.setOnClickListener {
            openWalletApp("binance")
        }

        // OKX Wallet
        binding.btnOkxWallet.setOnClickListener {
            openWalletApp("okx")
        }

        // Bitget Wallet
        binding.btnBitgetWallet.setOnClickListener {
            openWalletApp("bitget")
        }

        // Paste from clipboard
        binding.btnPasteAddress.setOnClickListener {
            pasteFromClipboard()
        }

        // Connect with manual address
        binding.btnConnectManual.setOnClickListener {
            val address = binding.editWalletAddress.text.toString().trim()
            connectWithAddress(address)
        }

        // Create new wallet
        binding.btnCreateNewWallet.setOnClickListener {
            viewModel.connectWallet()
            dismiss()
        }
    }

    private fun openWalletApp(walletKey: String) {
        val walletInfo = walletApps[walletKey] ?: return

        // Check if wallet app is installed
        if (isAppInstalled(walletInfo.packageName)) {
            // Open wallet app
            try {
                val intent = requireContext().packageManager.getLaunchIntentForPackage(walletInfo.packageName)
                if (intent != null) {
                    startActivity(intent)
                    Toast.makeText(
                        context,
                        "📋 Buka ${walletInfo.storeName}, salin alamat wallet Anda, lalu kembali dan paste di sini",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // Try deep link
                try {
                    val deepIntent = Intent(Intent.ACTION_VIEW, Uri.parse(walletInfo.deepLink))
                    startActivity(deepIntent)
                    Toast.makeText(
                        context,
                        "📋 Salin alamat wallet Anda dari ${walletInfo.storeName}, lalu kembali dan paste",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e2: Exception) {
                    Toast.makeText(context, "Gagal membuka ${walletInfo.storeName}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // App not installed → open Play Store
            try {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${walletInfo.packageName}")
                )
                startActivity(playStoreIntent)
                Toast.makeText(
                    context,
                    "📥 ${walletInfo.storeName} belum terinstall. Redirecting ke Play Store...",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                // If Play Store not available, open browser
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${walletInfo.packageName}")
                )
                startActivity(browserIntent)
            }
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun pasteFromClipboard() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val pastedText = clipData.getItemAt(0).text?.toString() ?: ""
            binding.editWalletAddress.setText(pastedText)
            binding.editWalletAddress.setSelection(pastedText.length)
            Toast.makeText(context, "📋 Alamat di-paste dari clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Clipboard kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectWithAddress(address: String) {
        if (address.isBlank()) {
            Toast.makeText(context, "⚠️ Masukkan alamat wallet terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate address format (Ethereum-style: 0x + 40 hex chars)
        if (!address.matches(Regex("^0x[a-fA-F0-9]{40}$"))) {
            Toast.makeText(
                context,
                "⚠️ Format alamat tidak valid. Harus dimulai dengan 0x dan 42 karakter.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Connect to the wallet (read-only mode)
        viewModel.connectExistingWallet(address)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ConnectWalletBottomSheet"
    }
}
