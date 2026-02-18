const { ethers } = require("hardhat");

async function main() {
  console.log("===========================================");
  console.log("  VibeAgent Registry - Deploy to BNB Chain");
  console.log("===========================================\n");

  const [deployer] = await ethers.getSigners();
  console.log("Deployer address:", deployer.address);

  const balance = await ethers.provider.getBalance(deployer.address);
  console.log("Deployer balance:", ethers.formatEther(balance), "BNB\n");

  console.log("Deploying VibeAgentRegistry...");
  const VibeAgentRegistry = await ethers.getContractFactory("VibeAgentRegistry");
  const registry = await VibeAgentRegistry.deploy();
  await registry.waitForDeployment();

  const contractAddress = await registry.getAddress();
  const deployTx = registry.deploymentTransaction();

  console.log("\n=== DEPLOYMENT SUCCESSFUL ===");
  console.log("Contract Address:", contractAddress);
  console.log("Transaction Hash:", deployTx.hash);
  console.log("Block Number:", (await deployTx.wait()).blockNumber);
  console.log("\n=== SALIN INFO INI KE FORMULIR DORAHACKS ===");
  console.log("Smart Contract Address:", contractAddress);
  console.log("Transaction Hash:", deployTx.hash);
  console.log("================================================\n");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("Deployment failed:", error);
    process.exit(1);
  });
