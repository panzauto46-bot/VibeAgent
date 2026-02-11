import { ParsedCommand } from '../types';

export function parseCommand(input: string): ParsedCommand {
  const lower = input.toLowerCase().trim();

  // Help command
  if (
    lower === 'help' ||
    lower === 'bantuan' ||
    lower === 'tolong' ||
    lower.includes('apa yang bisa') ||
    lower.includes('fitur') ||
    lower.includes('perintah')
  ) {
    return { action: 'help', params: {} };
  }

  // Create wallet
  if (
    lower.includes('buat wallet') ||
    lower.includes('create wallet') ||
    lower.includes('generate wallet') ||
    lower.includes('wallet baru') ||
    lower.includes('bikin wallet') ||
    lower.includes('new wallet') ||
    lower.includes('buat dompet') ||
    lower.includes('dompet baru')
  ) {
    return { action: 'create_wallet', params: {} };
  }

  // Import wallet
  if (
    lower.includes('import wallet') ||
    lower.includes('import key') ||
    lower.includes('import private') ||
    lower.includes('impor wallet') ||
    lower.includes('masukkan private key') ||
    lower.includes('pakai private key')
  ) {
    // Try to extract private key from input
    const hexMatch = input.match(/0x[a-fA-F0-9]{64}/);
    const rawHexMatch = input.match(/[a-fA-F0-9]{64}/);
    const privateKey = hexMatch ? hexMatch[0] : rawHexMatch ? rawHexMatch[0] : '';

    return {
      action: 'import_wallet',
      params: { privateKey },
    };
  }

  // Check balance
  if (
    lower.includes('cek saldo') ||
    lower.includes('check balance') ||
    lower.includes('saldo') ||
    lower.includes('balance') ||
    lower.includes('berapa saldo') ||
    lower.includes('lihat saldo')
  ) {
    return { action: 'balance', params: {} };
  }

  // Send BNB
  if (
    lower.includes('kirim') ||
    lower.includes('send') ||
    lower.includes('transfer') ||
    lower.includes('bayar') ||
    lower.includes('pay')
  ) {
    // Extract amount
    const amountMatch = input.match(/(\d+\.?\d*)\s*(?:bnb|BNB)?/);
    const amount = amountMatch ? amountMatch[1] : '';

    // Extract address
    const addressMatch = input.match(/0x[a-fA-F0-9]{40}/);
    const toAddress = addressMatch ? addressMatch[0] : '';

    return {
      action: 'send',
      params: { amount, toAddress },
    };
  }

  return { action: 'unknown', params: {} };
}

export function getHelpMessage(): string {
  return `🤖 **VibeAgent - Perintah yang Tersedia:**

💰 **Wallet:**
• "Buat wallet baru" - Generate wallet baru
• "Import private key 0x..." - Import wallet dari private key
• "Cek saldo" - Lihat saldo BNB

💸 **Transaksi:**
• "Kirim 0.01 BNB ke 0x..." - Transfer BNB
• "Transfer 0.5 BNB ke 0x..." - Transfer BNB

📌 **Catatan:**
• Semua transaksi berjalan di BSC Testnet
• Dapatkan BNB testnet di: https://testnet.bnbchain.org/faucet-smart
• Ketik "help" untuk melihat perintah ini lagi`;
}
