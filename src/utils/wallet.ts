import { ethers } from 'ethers';

const BSC_TESTNET_RPC = 'https://data-seed-prebsc-1-s1.binance.org:8545/';
const BSC_TESTNET_CHAIN_ID = 97;

export function getProvider() {
  return new ethers.JsonRpcProvider(BSC_TESTNET_RPC, {
    name: 'bsc-testnet',
    chainId: BSC_TESTNET_CHAIN_ID,
  });
}

export function createWallet(): { address: string; privateKey: string } {
  const wallet = ethers.Wallet.createRandom();
  return {
    address: wallet.address,
    privateKey: wallet.privateKey,
  };
}

export function importWallet(privateKey: string): { address: string; privateKey: string } {
  try {
    let pk = privateKey.trim();
    if (!pk.startsWith('0x')) {
      pk = '0x' + pk;
    }
    const wallet = new ethers.Wallet(pk);
    return {
      address: wallet.address,
      privateKey: wallet.privateKey,
    };
  } catch {
    throw new Error('Private key tidak valid. Pastikan format sudah benar.');
  }
}

export async function getBalance(address: string): Promise<string> {
  try {
    const provider = getProvider();
    const balance = await provider.getBalance(address);
    return ethers.formatEther(balance);
  } catch {
    throw new Error('Gagal mengecek saldo. Pastikan koneksi internet stabil.');
  }
}

export async function sendBNB(
  privateKey: string,
  toAddress: string,
  amount: string
): Promise<string> {
  try {
    const provider = getProvider();
    const wallet = new ethers.Wallet(privateKey, provider);

    const tx = await wallet.sendTransaction({
      to: toAddress,
      value: ethers.parseEther(amount),
    });

    const receipt = await tx.wait();
    return receipt?.hash || tx.hash;
  } catch (error: unknown) {
    const msg = error instanceof Error ? error.message : 'Unknown error';
    if (msg.includes('insufficient funds')) {
      throw new Error('Saldo BNB tidak cukup untuk transaksi ini.');
    }
    throw new Error(`Gagal mengirim transaksi: ${msg}`);
  }
}

export function isValidAddress(address: string): boolean {
  try {
    return ethers.isAddress(address);
  } catch {
    return false;
  }
}

export function shortenAddress(address: string): string {
  return `${address.slice(0, 6)}...${address.slice(-4)}`;
}
