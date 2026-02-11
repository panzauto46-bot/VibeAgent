export interface ChatMessage {
  id: string;
  role: 'user' | 'ai';
  content: string;
  timestamp: Date;
  type: 'text' | 'transaction' | 'wallet' | 'balance' | 'error';
  txHash?: string;
  metadata?: Record<string, string>;
}

export interface WalletInfo {
  address: string;
  privateKey: string;
  balance: string;
}

export interface ParsedCommand {
  action: 'send' | 'balance' | 'create_wallet' | 'import_wallet' | 'help' | 'unknown';
  params: Record<string, string>;
}
