<p align="center">
  <img src="https://img.shields.io/badge/BNB_Chain-F0B90B?style=for-the-badge&logo=binance&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Groq_AI-00C853?style=for-the-badge&logo=openai&logoColor=white" />
  <img src="https://img.shields.io/badge/Web3j-3C3C3D?style=for-the-badge&logo=ethereum&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
</p>

<h1 align="center">
  ⚡ VibeAgent
</h1>

<h3 align="center">
  Your AI-Powered Financial Assistant on BNB Chain 🤖💸
</h3>

<p align="center">
  <strong>Talk to your wallet. No complex UIs. No learning curves. Just vibes. ✨</strong>
</p>

<p align="center">
  <a href="#-key-features">Features</a> •
  <a href="#-the-problem">Problem</a> •
  <a href="#-the-solution">Solution</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-user-flow">User Flow</a> •
  <a href="#%EF%B8%8F-architecture">Architecture</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-security">Security</a> •
  <a href="#-roadmap">Roadmap</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/platform-Android-green?style=flat-square" />
  <img src="https://img.shields.io/badge/network-BSC_Mainnet-F0B90B?style=flat-square" />
  <img src="https://img.shields.io/badge/license-MIT-purple?style=flat-square" />
  <img src="https://img.shields.io/badge/hackathon-2026-red?style=flat-square" />
</p>

---

## 🔥 The Problem

> **Web3 adoption is stalled by complex interfaces.**

Beginners are **intimidated** by wallet addresses, gas fees, confusing dashboards, and seed phrases. Managing crypto in 2026 still feels like you need a computer science degree. The gap between Web3's potential and real-world usability is *massive*.

**Current Pain Points:**
- 😵 Overwhelming wallet UIs with dozens of buttons
- 🤯 Users must understand hex addresses, gas limits, and nonces
- ❌ No natural way to interact — only forms and copy-paste
- 🔒 Security feels like a burden, not a feature

---

## 💡 The Solution

**VibeAgent** is a **Native Android Application** that replaces the traditional wallet UI with a **conversational AI interface**. Powered by **Groq (Llama 3.3-70B)**, it allows users to manage their assets on **BNB Smart Chain** simply by **talking or texting** — like chatting with a friend.

```
👤 User: "Hey, how much BNB do I have?"
🤖 VibeAgent: "💰 Your balance is 0.4521 BNB (~$285 USD) on BSC Mainnet!"

👤 User: "Send 0.01 BNB to 0x742d...4Cc9"
🤖 VibeAgent: "📤 Confirming: 0.01 BNB → 0x742d...4Cc9. Processing... ✅ Done! TX: 0xabc..."
```

**No menus. No forms. No confusion. Just natural conversation.** 🎤

---

## ✨ Key Features

<table>
  <tr>
    <td width="50%">
      <h3>🗣️ Natural Language Transactions</h3>
      <p>No need to navigate complex menus. Just say <code>"Send 0.001 BNB to this address"</code> or <code>"Check my balance"</code> — the AI handles the rest. Supports both <strong>voice input</strong> and <strong>text commands</strong>.</p>
    </td>
    <td width="50%">
      <h3>⚡ Powered by Groq AI (Llama 3.3-70B)</h3>
      <p>Utilizes the <strong>ultra-fast inference</strong> of Llama 3.3 via Groq to parse user intents <em>instantly</em>. Response times under 500ms ensure a <strong>lag-free, real-time</strong> conversational experience.</p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🔐 Non-Custodial & Secure</h3>
      <p>Your private keys <strong>NEVER leave your device</strong>. We use <strong>AES-256-GCM encryption</strong> via Android's <code>EncryptedSharedPreferences</code> backed by the <strong>Android Keystore</strong> to store credentials locally.</p>
    </td>
    <td width="50%">
      <h3>🟢 BSC Mainnet Ready</h3>
      <p>Fully integrated with <strong>BNB Chain Mainnet</strong> (Chain ID: 56). VibeAgent reads <strong>real-time native BNB balances</strong> AND scans for <strong>10+ popular BEP-20 tokens</strong> (USDT, USDC, BUSD, CAKE, WBNB, etc).</p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>📱 Native Android Performance</h3>
      <p>Built with <strong>Kotlin</strong> and <strong>Web3j</strong>, ensuring maximum performance and direct hardware access for <strong>Google Voice Recognition</strong> with real-time partial results display.</p>
    </td>
    <td width="50%">
      <h3>🤖 AI Portfolio Analysis</h3>
      <p>Get <strong>intelligent portfolio insights</strong> powered by AI. VibeAgent reads your on-chain data and provides <strong>personalized investment strategy</strong>, risk assessment, and diversification advice.</p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>👛 Multi-Wallet Support</h3>
      <p>Connect via <strong>Trust Wallet, MetaMask, Binance, OKX, Bitget</strong> — or create a brand new wallet with <strong>BIP39 mnemonic generation</strong>. Import existing wallets via <strong>seed phrase or private key</strong>.</p>
    </td>
    <td width="50%">
      <h3>🔑 BIP39/BIP44 Standard</h3>
      <p>Proper wallet derivation using <strong>BIP39 mnemonic → BIP44 HD path</strong> (<code>m/44'/60'/0'/0/0</code>). Fully compatible with Trust Wallet, MetaMask, and all major wallets.</p>
    </td>
  </tr>
</table>

---

## 🚀 User Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        VibeAgent Flow                          │
├────────┬──────────────┬───────────────────┬─────────────────────┤
│  STEP  │    INPUT     │     PROCESS       │      OUTPUT         │
├────────┼──────────────┼───────────────────┼─────────────────────┤
│   1    │ 🎤 Voice /   │ Speech-to-Text    │ Raw text string     │
│        │ ⌨️ Text      │ (Google STT)      │                     │
├────────┼──────────────┼───────────────────┼─────────────────────┤
│   2    │ Raw text     │ 🧠 Groq AI        │ Parsed intent +     │
│        │              │ (Llama 3.3-70B)   │ structured data     │
├────────┼──────────────┼───────────────────┼─────────────────────┤
│   3    │ Parsed       │ 🔗 Web3j signs    │ Signed transaction  │
│        │ intent       │ TX locally        │ broadcast to BSC    │
├────────┼──────────────┼───────────────────┼─────────────────────┤
│   4    │ TX result    │ 💬 AI formats     │ ✅ TxHash + status  │
│        │              │ response          │ in chat bubble      │
└────────┴──────────────┴───────────────────┴─────────────────────┘
```

### 🎬 Step-by-Step Example

1. **🎤 Input** → User presses the microphone button and says: *"Transfer 0.01 BNB to my friend at 0x742d..."*
2. **🧠 Process** → Groq AI analyzes the intent, extracts the amount (`0.01`), destination address (`0x742d...`), and returns a confirmation prompt
3. **🔐 Execution** → Once confirmed, the app signs the transaction **locally on-device** using Web3j and broadcasts it to the **BNB Smart Chain**
4. **✅ Result** → The transaction hash (TxID) is returned in the chat window **in seconds**

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | Kotlin + Material Design 3 | Native Android UI with modern design language |
| **AI Brain** | Groq API (Llama 3.3-70B Versatile) | Ultra-fast NLP inference — converts human language to blockchain commands |
| **Blockchain** | Web3j Library | Wallet creation (BIP39/BIP44), transaction signing, BSC RPC interaction |
| **Voice** | Google Speech Recognition | Real-time speech-to-text with partial results |
| **Security** | EncryptedSharedPreferences (AES-256-GCM) | Non-custodial encrypted storage via Android Keystore |
| **Auth** | Firebase Authentication + Google Sign-In | Secure user authentication |
| **Network** | OkHttp3 | Fast, reliable HTTP client for API and RPC calls |
| **Architecture** | MVVM + LiveData + Coroutines | Clean, reactive, maintainable codebase |

---

## 🏗️ Architecture

```
┌───────────────────────────────────────────────────────────────┐
│                     📱 VibeAgent Android App                   │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────┐  │
│  │  Splash      │  │  Auth        │  │  Main Activity       │  │
│  │  Activity    │→ │  Activity    │→ │  (Chat Interface)    │  │
│  │             │  │  (Firebase)  │  │                      │  │
│  └─────────────┘  └─────────────┘  └──────────┬───────────┘  │
│                                                │              │
│                    ┌───────────────────────────┤              │
│                    │                           │              │
│  ┌─────────────────▼──────┐  ┌─────────────────▼──────────┐  │
│  │   ChatViewModel        │  │  Bottom Sheets              │  │
│  │   (MVVM Brain)         │  │  • ConnectWalletSheet       │  │
│  │                        │  │  • WalletDetailsSheet       │  │
│  │  • processMessage()    │  └────────────────────────────┘  │
│  │  • analyzePortfolio()  │                                  │
│  │  • connectWallet()     │                                  │
│  └──┬────────┬────────┬───┘                                  │
│     │        │        │                                      │
│  ┌──▼──┐ ┌──▼──┐ ┌───▼────┐                                │
│  │Groq │ │Web3j│ │Command │                                 │
│  │ API │ │     │ │Parser  │                                 │
│  │     │ │     │ │        │                                 │
│  └──┬──┘ └──┬──┘ └────────┘                                 │
│     │       │                                                │
├─────▼───────▼────────────────────────────────────────────────┤
│  ☁️ External Services                                        │
│  ┌────────────────┐  ┌─────────────────────────────────────┐ │
│  │ Groq Cloud      │  │ BNB Smart Chain (BSC Mainnet)       │ │
│  │ Llama 3.3-70B   │  │ RPC: bsc-dataseed1.binance.org     │ │
│  │ ~500ms latency  │  │ Chain ID: 56                        │ │
│  └────────────────┘  └─────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
VibeAgent/
├── 📱 VibeAgent-Android/              # Native Android App
│   ├── app/src/main/
│   │   ├── java/com/vibeagent/app/
│   │   │   ├── MainActivity.kt        # Chat + Voice Recognition
│   │   │   ├── AuthActivity.kt        # Firebase Google Auth
│   │   │   ├── SplashActivity.kt      # Animated Splash Screen
│   │   │   ├── ConnectWalletBottomSheet.kt  # Multi-wallet connector
│   │   │   ├── WalletBottomSheet.kt   # Wallet details view
│   │   │   ├── adapter/
│   │   │   │   └── ChatAdapter.kt     # RecyclerView chat adapter
│   │   │   ├── model/
│   │   │   │   ├── ChatMessage.kt     # Message data model
│   │   │   │   └── WalletInfo.kt      # Wallet data model
│   │   │   ├── util/
│   │   │   │   ├── CommandParser.kt   # NLP command parser
│   │   │   │   ├── WalletManager.kt   # Web3j wallet operations
│   │   │   │   ├── SecureWalletStorage.kt  # AES-256 encrypted storage
│   │   │   │   └── PrefsManager.kt    # Preferences helper
│   │   │   └── viewmodel/
│   │   │       └── ChatViewModel.kt   # MVVM ViewModel (Groq + Web3j)
│   │   └── res/
│   │       ├── layout/                # XML layouts (Material Design 3)
│   │       ├── drawable/              # Icons, backgrounds, animations
│   │       └── values/                # Themes, colors, strings
│   └── build.gradle.kts              # Dependencies & config
│
├── 🌐 src/                           # Web Frontend (React + TypeScript)
│   ├── App.tsx                       # Main chat interface
│   ├── components/
│   │   ├── ChatBubble.tsx            # Message bubble component
│   │   ├── WalletPanel.tsx           # Wallet sidebar panel
│   │   ├── QuickActions.tsx          # Quick action buttons
│   │   └── TypingIndicator.tsx       # AI typing animation
│   ├── hooks/
│   │   └── useChat.ts                # Chat logic hook
│   └── utils/
│       ├── commandParser.ts          # Command parser
│       └── wallet.ts                 # Wallet utilities
│
├── .gitignore
├── package.json
└── README.md                         # You are here! 📍
```

---

## 🔐 Security

VibeAgent follows the **non-custodial** security model. Your keys, your crypto.

| Security Feature | Implementation |
|-----------------|----------------|
| **Key Storage** | AES-256-GCM encryption via `EncryptedSharedPreferences` |
| **Key Derivation** | Android Keystore `MasterKey` with `AES256_GCM` scheme |
| **Seed Phrase Validation** | BIP39 wordlist validation + checksum verification |
| **HD Wallet** | BIP44 derivation path `m/44'/60'/0'/0/0` |
| **Data Isolation** | Private keys NEVER sent to any server (Groq, Firebase, or otherwise) |
| **Logout** | Complete data wipe from encrypted storage on logout |
| **Network** | Direct RPC calls to BSC — no intermediary servers |

```kotlin
// How we store your keys — AES-256-GCM, Android Keystore backed
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context, "vibeagent_secure_wallet", masterKey,
    PrefKeyEncryptionScheme.AES256_SIV,
    PrefValueEncryptionScheme.AES256_GCM
)
```

---

## 🏁 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34**
- A physical Android device or emulator (API 24+)

### Build & Run

```bash
# 1. Clone the repository
git clone https://github.com/panzauto46-bot/VibeAgent.git
cd VibeAgent

# 2. Open VibeAgent-Android/ in Android Studio

# 3. Sync Gradle & Build
cd VibeAgent-Android
./gradlew assembleDebug

# 4. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Configuration

Create `local.properties` in the `VibeAgent-Android/` directory:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

> **Note:** The Groq API key for the AI brain is pre-configured in the app for hackathon demo purposes.

---

## 🗺️ Roadmap

- [x] ✅ Conversational AI chat interface
- [x] ✅ Voice input with Google Speech Recognition
- [x] ✅ BIP39/BIP44 wallet generation
- [x] ✅ Seed phrase import with validation
- [x] ✅ BSC Mainnet integration
- [x] ✅ BEP-20 token scanning (10+ tokens)
- [x] ✅ AI portfolio analysis
- [x] ✅ Non-custodial AES-256 encrypted storage
- [x] ✅ Multi-wallet connection (Trust, MetaMask, Binance, OKX, Bitget)
- [x] ✅ Firebase Authentication
- [ ] 🔜 Swap tokens via PancakeSwap integration
- [ ] 🔜 NFT viewing & management
- [ ] 🔜 Multi-chain support (Ethereum, Polygon, Arbitrum)
- [ ] 🔜 Transaction history with AI-powered insights
- [ ] 🔜 DeFi yield farming recommendations
- [ ] 🔜 Biometric authentication (Fingerprint/Face)

---

## 🏆 Why VibeAgent Wins

| Traditional Wallet | VibeAgent |
|---|---|
| 😵 Complex forms & menus | 🗣️ Just talk or type naturally |
| 📋 Copy-paste addresses | 🤖 AI understands context |
| ❌ Steep learning curve | ✅ Zero learning curve |
| 🔇 No voice support | 🎤 Full voice recognition |
| 📊 Raw data display | 🧠 AI-powered portfolio insights |
| 🔐 Confusing security | 🛡️ Invisible AES-256 encryption |

---

## 🤝 Team

Built with ❤️ and ☕ for the **BNB Chain Hackathon 2026**

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <strong>⚡ VibeAgent — Making Web3 feel like Web2, one conversation at a time.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Built_for-BNB_Chain_Hackathon_2026-F0B90B?style=for-the-badge&logo=binance&logoColor=white" />
</p>
