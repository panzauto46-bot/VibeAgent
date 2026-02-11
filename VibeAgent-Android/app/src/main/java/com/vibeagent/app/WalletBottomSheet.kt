package com.vibeagent.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vibeagent.app.databinding.BottomSheetWalletBinding
import com.vibeagent.app.util.SecureWalletStorage
import com.vibeagent.app.viewmodel.ChatViewModel

class WalletBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetWalletBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatViewModel
    private var isPrivateKeyVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Use activity view model to share state with MainActivity
        viewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            // ===== FULL LOGOUT: Clear ALL data =====

            // 1. Sign out from Firebase Auth
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            
            // 2. Clear regular SharedPreferences (login state, email, address)
            val prefs = com.vibeagent.app.util.PrefsManager(requireContext())
            prefs.logout()

            // 3. Clear EncryptedSharedPreferences (private key, seed phrase, mnemonic)
            // ⚠️ This ensures NO wallet data persists after logout
            val secureStorage = SecureWalletStorage(requireContext())
            secureStorage.clearAll()

            // 4. Also clear the VibeAgentPrefs directly (wallet_connected, wallet_address, etc)
            val vibePrefs = requireContext().getSharedPreferences("VibeAgentPrefs", Context.MODE_PRIVATE)
            vibePrefs.edit().clear().apply()

            // 5. Sign out from Google
            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
            ).build()
            val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(requireContext(), gso)
            
            googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                Toast.makeText(context, "✅ Logout berhasil. Data wallet telah dihapus dari perangkat.", Toast.LENGTH_SHORT).show()
                
                val intent = android.content.Intent(requireContext(), AuthActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        binding.btnCopyAddress.setOnClickListener {
            val address = binding.textAddress.text.toString()
            if (address.isNotEmpty() && address != "-") {
                copyToClipboard("Wallet Address", address)
            }
        }

        binding.btnCopyKey.setOnClickListener {
            // Read private key from encrypted storage, NOT from plain ViewModel
            val secureStorage = SecureWalletStorage(requireContext())
            val privateKey = secureStorage.getPrivateKey()
            if (!privateKey.isNullOrBlank()) {
                copyToClipboard("Private Key", privateKey)
            } else {
                Toast.makeText(context, "⚠️ Mode read-only: tidak ada private key", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShowKey.setOnClickListener {
            isPrivateKeyVisible = !isPrivateKeyVisible
            updatePrivateKeyDisplay()
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.checkBalance()
            Toast.makeText(context, "🔍 Checking balance...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.wallet.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null) {
                binding.walletInfoContainer.visibility = View.VISIBLE
                binding.textNoWallet.visibility = View.GONE
                
                binding.textAddress.text = wallet.address
                binding.textBalance.text = wallet.balance
                
                updatePrivateKeyDisplay()
            } else {
                binding.walletInfoContainer.visibility = View.GONE
                binding.textNoWallet.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePrivateKeyDisplay() {
        if (isPrivateKeyVisible) {
            // Read private key from ENCRYPTED storage
            val secureStorage = SecureWalletStorage(requireContext())
            val privateKey = secureStorage.getPrivateKey()
            if (!privateKey.isNullOrBlank()) {
                binding.textPrivateKey.text = privateKey
            } else {
                binding.textPrivateKey.text = "(read-only mode — tidak ada private key)"
            }
            binding.btnShowKey.alpha = 1.0f
        } else {
            binding.textPrivateKey.text = "••••••••••••••••••••••••••••••••"
            binding.btnShowKey.alpha = 0.5f
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "WalletBottomSheet"
    }
}
