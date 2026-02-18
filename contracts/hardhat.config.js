require("@nomicfoundation/hardhat-toolbox");

// PENTING: Ganti PRIVATE_KEY dengan private key wallet yang punya BNB untuk gas fee
// Untuk testnet: dapatkan BNB gratis di https://testnet.bnbchain.org/faucet-smart
const PRIVATE_KEY = process.env.DEPLOYER_PRIVATE_KEY || "0x0000000000000000000000000000000000000000000000000000000000000001";

/** @type import('hardhat/config').HardhatUserConfig */
module.exports = {
  solidity: "0.8.19",
  networks: {
    bscTestnet: {
      url: "https://data-seed-prebsc-1-s1.binance.org:8545/",
      chainId: 97,
      accounts: [PRIVATE_KEY],
    },
    bscMainnet: {
      url: "https://bsc-dataseed1.binance.org/",
      chainId: 56,
      accounts: [PRIVATE_KEY],
    },
  },
};
