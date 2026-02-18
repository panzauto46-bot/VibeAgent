<p align="center">
  <img src="https://img.shields.io/badge/BNB_Chain-F0B90B?style=for-the-badge&logo=binance&logoColor=white" />
  <img src="https://img.shields.io/badge/Solidity-363636?style=for-the-badge&logo=solidity&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Groq_AI-00C853?style=for-the-badge&logo=openai&logoColor=white" />
  <img src="https://img.shields.io/badge/Web3j-3C3C3D?style=for-the-badge&logo=ethereum&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black" />
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
  <a href="#-smart-contract">Smart Contract</a> •
  <a href="#%EF%B8%8F-tech-stack">Tech Stack</a> •
  <a href="#-user-flow">User Flow</a> •
  <a href="#%EF%B8%8F-architecture">Architecture</a> •
  <a href="#-project-structure">Project Structure</a> •
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-security">Security</a> •
  <a href="#%EF%B8%8F-roadmap">Roadmap</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/platform-Android_|_Web-green?style=flat-square" />
  <img src="https://img.shields.io/badge/network-BSC_Mainnet_|_Testnet-F0B90B?style=flat-square" />
  <img src="https://img.shields.io/badge/smart_contract-deployed-brightgreen?style=flat-square" />
  <img src="https://img.shields.io/badge/license-MIT-purple?style=flat-square" />
  <img src="https://img.shields.io/badge/hackathon-BNB_Chain_2026-red?style=flat-square" />
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

**VibeAgent** is a **Native Android Application** with a **React Web companion** that replaces the traditional wallet UI with a **conversational AI interface**. Powered by **Groq (Llama 3.3-70B)**, it allows users to manage their assets on **BNB Smart Chain** simply by **talking or texting** — like chatting with a friend.

```
👤 User: "Hey, how much BNB do I have?"
🤖 VibeAgent: "💰 Your balance is 0.4521 BNB (~$285 USD) on BSC Mainnet!"

👤 User: "Send 0.01 BNB to 0x742d...4Cc9"
🤖 VibeAgent: "📤 Confirming: 0.01 BNB → 0x742d...4Cc9. Processing... ✅ Done! TX: 0xabc..."

👤 User: "Check my contract registration"
🤖 VibeAgent: "📋 VibeAgent Registry: You are registered! Nickname: CryptoKing, Total Users: 42"
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
      <h3>📋 On-Chain Smart Contract</h3>
      <p>VibeAgent has its own <strong>deployed smart contract</strong> on BNB Chain — the <strong>VibeAgentRegistry</strong>. Users can register on-chain, verify identity, and track community growth directly from the chat interface.</p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>📱 Native Android + Web</h3>
      <p>Built with <strong>Kotlin + Web3j</strong> for Android and <strong>React + Ethers.js</strong> for web, ensuring maximum performance across platforms with <strong>Google Voice Recognition</strong>.</p>
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
      <h3>🟢 BSC Mainnet + Testnet Ready</h3>
      <p>Fully integrated with <strong>BNB Chain Mainnet</strong> (Chain ID: 56) and <strong>Testnet</strong> (Chain ID: 97). Reads <strong>real-time BNB balances</strong> and scans <strong>10+ BEP-20 tokens</strong> (USDT, USDC, BUSD, CAKE, WBNB, etc).</p>
    </td>
  </tr>
</table>

---

## 📋 Smart Contract

VibeAgent has a deployed **on-chain smart contract** — `VibeAgentRegistry` — on BNB Smart Chain.

### Contract Details

| Field | Value |
|-------|-------|
| **Contract Name** | VibeAgentRegistry |
| **Network** | BNB Smart Chain Testnet (Chain ID: 97) |
| **Contract Address** | [`0x49Ee39851956df07E5d3B430dC91e5A00B7E6059`](https://testnet.bscscan.com/address/0x49Ee39851956df07E5d3B430dC91e5A00B7E6059) |
| **Deployment TX** | [`0x38faa9e4bf753264409e351edb6f69caf1a0ad94a8176df2f59ba2f65116dd37`](https://testnet.bscscan.com/tx/0x38faa9e4bf753264409e351edb6f69caf1a0ad94a8176df2f59ba2f65116dd37) |
| **Solidity Version** | ^0.8.19 |
| **License** | MIT |

### Contract Functions

| Function | Type | Description |
|----------|------|-------------|
| `register(string nickname)` | Write | Register as a VibeAgent user on-chain |
| `updateNickname(string nickname)` | Write | Update your display nickname |
| `isRegistered(address)` | Read | Check if an address is registered |
| `getProfile(address)` | Read | Get user profile (registered, timestamp, nickname) |
| `totalUsers()` | Read | Total number of registered users |
| `APP_NAME()` | Read | Returns "VibeAgent" |
| `APP_VERSION()` | Read | Returns "1.0.0" |

### How It Works in the App

The Android app interacts with the smart contract through `ContractManager.kt`:

```kotlin
// Check if user is registered on-chain
val isRegistered = contractManager.isUserRegistered(walletAddress)

// Get total community size
val totalUsers = contractManager.getTotalUsers()

// Get user profile from blockchain
val profile = contractManager.getUserProfile(walletAddress)
```

Users can interact with the contract via natural language:
```
👤 "Show contract info"    → Displays contract details + registration status
👤 "Check my registration" → Queries on-chain profile
```

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
| **Android Frontend** | Kotlin + Material Design 3 | Native Android UI with modern design language |
| **Web Frontend** | React 19 + TypeScript + Vite | Responsive web chat interface |
| **AI Brain** | Groq API (Llama 3.3-70B Versatile) | Ultra-fast NLP inference — converts human language to blockchain commands |
| **Smart Contract** | Solidity ^0.8.19 + Hardhat | On-chain user registry deployed on BNB Chain |
| **Blockchain (Android)** | Web3j 4.9.8 | Wallet creation (BIP39/BIP44), transaction signing, BSC RPC interaction |
| **Blockchain (Web)** | Ethers.js 6.13.1 | Web-based wallet operations and contract interaction |
| **Voice** | Google Speech Recognition | Real-time speech-to-text with partial results |
| **Security** | EncryptedSharedPreferences (AES-256-GCM) | Non-custodial encrypted storage via Android Keystore |
| **Auth** | Firebase Authentication + Google Sign-In | Secure user authentication |
| **Network** | OkHttp3 | Fast, reliable HTTP client for API and RPC calls |
| **Architecture** | MVVM + LiveData + Coroutines | Clean, reactive, maintainable codebase |

---

## 🏗️ Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                     📱 VibeAgent Android App                      │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────────┐  │
│  │  Splash      │  │  Auth        │  │  Main Activity           │  │
│  │  Activity    │→ │  Activity    │→ │  (Chat Interface)        │  │
│  │              │  │  (Firebase)  │  │                          │  │
│  └─────────────┘  └─────────────┘  └────────────┬─────────────┘  │
│                                                  │                │
│                    ┌─────────────────────────────┤                │
│                    │                             │                │
│  ┌─────────────────▼───────┐  ┌──────────────────▼────────────┐  │
│  │   ChatViewModel          │  │  Bottom Sheets                │  │
│  │   (MVVM Brain)           │  │  • ConnectWalletSheet         │  │
│  │                          │  │  • WalletDetailsSheet         │  │
│  │  • processMessage()      │  └──────────────────────────────┘  │
│  │  • analyzePortfolio()    │                                    │
│  │  • handleContractInfo()  │                                    │
│  │  • connectWallet()       │                                    │
│  └──┬────────┬────────┬─────┘                                    │
│     │        │        │                                          │
│  ┌──▼──┐ ┌──▼───┐ ┌──▼──────────┐                               │
│  │Groq │ │Web3j │ │Contract     │                               │
│  │ API │ │      │ │Manager      │                               │
│  │     │ │      │ │(Registry)   │                               │
│  └──┬──┘ └──┬───┘ └──┬──────────┘                               │
│     │       │        │                                           │
├─────▼───────▼────────▼───────────────────────────────────────────┤
│  ☁️ External Services                                             │
│  ┌────────────────┐  ┌──────────────────┐  ┌──────────────────┐  │
│  │ Groq Cloud      │  │ BNB Smart Chain   │  │ VibeAgent        │  │
│  │ Llama 3.3-70B   │  │ BSC Mainnet:56    │  │ Registry         │  │
│  │ ~500ms latency  │  │ BSC Testnet:97    │  │ (Smart Contract) │  │
│  └────────────────┘  └──────────────────┘  └──────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
VibeAgent/
│
├── 📱 VibeAgent-Android/                    # Native Android Application
│   ├── app/src/main/
│   │   ├── java/com/vibeagent/app/
│   │   │   ├── MainActivity.kt              # Chat UI + Voice Recognition
│   │   │   ├── AuthActivity.kt              # Firebase Google Sign-In
│   │   │   ├── SplashActivity.kt            # Animated Splash Screen
│   │   │   ├── ConnectWalletBottomSheet.kt  # Multi-wallet connector UI
│   │   │   ├── WalletBottomSheet.kt         # Wallet details view
│   │   │   │
│   │   │   ├── adapter/
│   │   │   │   └── ChatAdapter.kt           # RecyclerView chat adapter
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── ChatMessage.kt           # Message data model
│   │   │   │   └── WalletInfo.kt            # Wallet data model
│   │   │   │
│   │   │   ├── util/
│   │   │   │   ├── WalletManager.kt         # Web3j wallet operations (BIP39/BIP44)
│   │   │   │   ├── ContractManager.kt       # Smart contract interaction (Registry)
│   │   │   │   ├── CommandParser.kt         # NLP command parser
│   │   │   │   ├── SecureWalletStorage.kt   # AES-256-GCM encrypted storage
│   │   │   │   └── PrefsManager.kt          # SharedPreferences helper
│   │   │   │
│   │   │   └── viewmodel/
│   │   │       └── ChatViewModel.kt         # MVVM ViewModel (AI + Web3j + Contract)
│   │   │
│   │   └── res/
│   │       ├── layout/                      # XML layouts (Material Design 3)
│   │       ├── drawable/                    # Icons, backgrounds, animations
│   │       └── values/                      # Themes, colors, strings
│   │
│   ├── build.gradle.kts                     # App dependencies & config
│   └── README.md                            # Android-specific documentation
│
├── 🌐 src/                                  # Web Frontend (React + TypeScript)
│   ├── App.tsx                              # Main chat interface component
│   ├── main.tsx                             # React entry point
│   ├── index.css                            # Global styles (Tailwind CSS)
│   │
│   ├── components/
│   │   ├── ChatBubble.tsx                   # Message bubble component
│   │   ├── WalletPanel.tsx                  # Wallet sidebar panel
│   │   ├── QuickActions.tsx                 # Quick action buttons
│   │   └── TypingIndicator.tsx              # AI typing animation
│   │
│   ├── hooks/
│   │   └── useChat.ts                       # Chat state & logic hook
│   │
│   ├── types/
│   │   └── index.ts                         # TypeScript interfaces
│   │
│   └── utils/
│       ├── commandParser.ts                 # Command parsing logic
│       ├── wallet.ts                        # Ethers.js wallet utilities
│       └── cn.ts                            # Class name utility
│
├── 📜 contracts/                            # Smart Contracts (Solidity + Hardhat)
│   ├── contracts/
│   │   └── VibeAgentRegistry.sol            # On-chain user registry contract
│   ├── scripts/
│   │   └── deploy.js                        # Deployment script for BSC
│   ├── hardhat.config.js                    # Hardhat network configuration
│   └── package.json                         # Contract dependencies
│
├── .gitignore                               # Git ignore rules
├── index.html                               # Web entry point
├── package.json                             # Web dependencies
├── tsconfig.json                            # TypeScript configuration
├── vite.config.ts                           # Vite build configuration
├── LICENSE                                  # MIT License
└── README.md                                # This file 📍
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
| **Smart Contract** | Immutable on-chain registry — data verified by blockchain consensus |
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
- **Node.js 16+** (for web frontend and smart contracts)
- A physical Android device or emulator (API 24+)

### Build & Run (Android)

```bash
# 1. Clone the repository
git clone https://github.com/panzauto46-bot/VibeAgent.git
cd VibeAgent

# 2. Open VibeAgent-Android/ in Android Studio

# 3. Create local.properties with your API keys
echo "GROQ_API_KEY=your_groq_api_key" > VibeAgent-Android/local.properties

# 4. Sync Gradle & Build
cd VibeAgent-Android
./gradlew assembleDebug

# 5. Install on device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Build & Run (Web)

```bash
# 1. Install dependencies
npm install

# 2. Start development server
npm run dev

# 3. Build for production (single-file HTML)
npm run build
```

### Deploy Smart Contract

```bash
# 1. Install contract dependencies
cd contracts
npm install

# 2. Set deployer private key
export DEPLOYER_PRIVATE_KEY="0xYOUR_PRIVATE_KEY"

# 3. Deploy to BSC Testnet
npm run deploy:testnet

# 4. Deploy to BSC Mainnet
npm run deploy:mainnet
```

---

## 🗺️ Roadmap

### Phase 1 — Core (Completed ✅)

- [x] Conversational AI chat interface with Groq (Llama 3.3-70B)
- [x] Voice input with Google Speech Recognition
- [x] BIP39/BIP44 wallet generation and seed phrase import
- [x] BSC Mainnet + Testnet integration
- [x] BEP-20 token scanning (10+ tokens: USDT, USDC, BUSD, CAKE, WBNB, etc.)
- [x] AI-powered portfolio analysis with on-chain data
- [x] Non-custodial AES-256-GCM encrypted key storage
- [x] Multi-wallet connection (Trust, MetaMask, Binance, OKX, Bitget)
- [x] Firebase Authentication with Google Sign-In
- [x] React + TypeScript web companion app
- [x] **Smart contract deployment** — VibeAgentRegistry on BNB Chain
- [x] **On-chain user registration** — query and display via ContractManager

### Phase 2 — DeFi Integration (In Progress 🔄)

- [ ] Swap tokens via PancakeSwap integration
- [ ] Transaction history with AI-powered insights
- [ ] Gas fee estimation and optimization
- [ ] Token price feeds and real-time charts

### Phase 3 — Advanced Features (Planned 📋)

- [ ] NFT viewing & management
- [ ] Multi-chain support (Ethereum, Polygon, Arbitrum, opBNB)
- [ ] DeFi yield farming recommendations
- [ ] Biometric authentication (Fingerprint/Face)
- [ ] Push notifications for price alerts and transaction confirmations
- [ ] On-chain identity verification via VibeAgentRegistry

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
| ❌ No on-chain identity | 📋 Smart contract user registry |

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
