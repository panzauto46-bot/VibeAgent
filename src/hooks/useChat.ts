import { useState, useCallback } from 'react';
import { ChatMessage, WalletInfo } from '../types';
import { parseCommand, getHelpMessage } from '../utils/commandParser';
import {
  createWallet,
  importWallet,
  getBalance,
  sendBNB,
  isValidAddress,
} from '../utils/wallet';

function createMessage(
  role: 'user' | 'ai',
  content: string,
  type: ChatMessage['type'] = 'text',
  txHash?: string
): ChatMessage {
  return {
    id: Date.now().toString() + Math.random().toString(36).slice(2),
    role,
    content,
    timestamp: new Date(),
    type,
    txHash,
  };
}

export function useChat() {
  const [messages, setMessages] = useState<ChatMessage[]>([
    createMessage(
      'ai',
      `👋 **Halo! Saya VibeAgent.**\n\nAsisten keuangan pribadi berbasis AI untuk BNB Smart Chain (Testnet).\n\n🚀 Saya bisa membantu Anda:\n• Membuat wallet baru\n• Mengecek saldo BNB\n• Mengirim BNB ke alamat lain\n\nKetik **"help"** untuk melihat semua perintah, atau langsung mulai dengan **"buat wallet baru"**!`
    ),
  ]);
  const [wallet, setWallet] = useState<WalletInfo | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const addMessage = useCallback((msg: ChatMessage) => {
    setMessages((prev) => [...prev, msg]);
  }, []);

  const refreshBalance = useCallback(async () => {
    if (!wallet) return;
    setIsRefreshing(true);
    try {
      const balance = await getBalance(wallet.address);
      setWallet((prev) => (prev ? { ...prev, balance } : null));
    } catch {
      // silently fail
    } finally {
      setIsRefreshing(false);
    }
  }, [wallet]);

  const processMessage = useCallback(
    async (input: string) => {
      if (!input.trim() || isProcessing) return;

      const userMsg = createMessage('user', input);
      addMessage(userMsg);
      setIsProcessing(true);

      // Simulate small delay for natural feel
      await new Promise((r) => setTimeout(r, 500 + Math.random() * 500));

      const command = parseCommand(input);

      try {
        switch (command.action) {
          case 'help': {
            addMessage(createMessage('ai', getHelpMessage()));
            break;
          }

          case 'create_wallet': {
            const newWallet = createWallet();
            const walletInfo: WalletInfo = {
              address: newWallet.address,
              privateKey: newWallet.privateKey,
              balance: '0.0',
            };
            setWallet(walletInfo);

            addMessage(
              createMessage(
                'ai',
                `✅ **Wallet berhasil dibuat!**\n\n📍 **Address:**\n\`${newWallet.address}\`\n\n🔑 **Private Key:**\n\`${newWallet.privateKey}\`\n\n⚠️ **PENTING:** Simpan private key Anda dengan aman! Jangan pernah bagikan ke siapapun.\n\n💡 Untuk mendapatkan BNB testnet gratis, kunjungi:\nhttps://testnet.bnbchain.org/faucet-smart`,
                'wallet'
              )
            );

            // Try to fetch balance
            try {
              const balance = await getBalance(newWallet.address);
              setWallet((prev) => (prev ? { ...prev, balance } : null));
            } catch {
              // ignore
            }
            break;
          }

          case 'import_wallet': {
            const { privateKey } = command.params;
            if (!privateKey) {
              addMessage(
                createMessage(
                  'ai',
                  '⚠️ Mohon sertakan private key Anda.\n\nContoh:\n"Import private key 0x1234567890abcdef..."',
                  'error'
                )
              );
              break;
            }

            try {
              const imported = importWallet(privateKey);
              const walletInfo: WalletInfo = {
                address: imported.address,
                privateKey: imported.privateKey,
                balance: '0.0',
              };
              setWallet(walletInfo);

              addMessage(
                createMessage(
                  'ai',
                  `✅ **Wallet berhasil diimport!**\n\n📍 **Address:**\n\`${imported.address}\`\n\n💰 Mengecek saldo...`,
                  'wallet'
                )
              );

              try {
                const balance = await getBalance(imported.address);
                setWallet((prev) => (prev ? { ...prev, balance } : null));
                addMessage(
                  createMessage(
                    'ai',
                    `💰 **Saldo:** ${parseFloat(balance).toFixed(6)} BNB`,
                    'balance'
                  )
                );
              } catch {
                addMessage(
                  createMessage(
                    'ai',
                    '⚠️ Gagal mengecek saldo. Coba lagi nanti dengan "cek saldo".',
                    'error'
                  )
                );
              }
            } catch (error: unknown) {
              const msg =
                error instanceof Error ? error.message : 'Private key tidak valid.';
              addMessage(createMessage('ai', `❌ ${msg}`, 'error'));
            }
            break;
          }

          case 'balance': {
            if (!wallet) {
              addMessage(
                createMessage(
                  'ai',
                  '⚠️ Anda belum memiliki wallet. Ketik **"buat wallet baru"** untuk membuat wallet.',
                  'error'
                )
              );
              break;
            }

            addMessage(
              createMessage('ai', '🔍 Mengecek saldo...')
            );

            try {
              const balance = await getBalance(wallet.address);
              setWallet((prev) => (prev ? { ...prev, balance } : null));

              // Remove the "checking" message and add the result
              setMessages((prev) => {
                const filtered = prev.filter(
                  (m) => m.content !== '🔍 Mengecek saldo...'
                );
                return [
                  ...filtered,
                  createMessage(
                    'ai',
                    `💰 **Saldo Wallet Anda:**\n\n${parseFloat(balance).toFixed(6)} BNB\n\n📍 ${wallet.address}`,
                    'balance'
                  ),
                ];
              });
            } catch (error: unknown) {
              const msg =
                error instanceof Error
                  ? error.message
                  : 'Gagal mengecek saldo.';
              setMessages((prev) => {
                const filtered = prev.filter(
                  (m) => m.content !== '🔍 Mengecek saldo...'
                );
                return [
                  ...filtered,
                  createMessage('ai', `❌ ${msg}`, 'error'),
                ];
              });
            }
            break;
          }

          case 'send': {
            if (!wallet) {
              addMessage(
                createMessage(
                  'ai',
                  '⚠️ Anda belum memiliki wallet. Ketik **"buat wallet baru"** untuk membuat wallet terlebih dahulu.',
                  'error'
                )
              );
              break;
            }

            const { amount, toAddress } = command.params;

            if (!amount || parseFloat(amount) <= 0) {
              addMessage(
                createMessage(
                  'ai',
                  '⚠️ Jumlah BNB tidak valid.\n\nContoh: "Kirim **0.01** BNB ke 0x..."',
                  'error'
                )
              );
              break;
            }

            if (!toAddress || !isValidAddress(toAddress)) {
              addMessage(
                createMessage(
                  'ai',
                  '⚠️ Alamat tujuan tidak valid.\n\nContoh: "Kirim 0.01 BNB ke **0x1234...abcd**"',
                  'error'
                )
              );
              break;
            }

            addMessage(
              createMessage(
                'ai',
                `📤 **Konfirmasi Transaksi:**\n\n💸 Jumlah: ${amount} BNB\n📍 Ke: ${toAddress}\n\n⏳ Memproses transaksi...`
              )
            );

            try {
              const txHash = await sendBNB(
                wallet.privateKey,
                toAddress,
                amount
              );

              // Update balance
              try {
                const balance = await getBalance(wallet.address);
                setWallet((prev) => (prev ? { ...prev, balance } : null));
              } catch {
                // ignore
              }

              setMessages((prev) => {
                const filtered = prev.filter(
                  (m) =>
                    !m.content.includes('⏳ Memproses transaksi...')
                );
                return [
                  ...filtered,
                  createMessage(
                    'ai',
                    `✅ **Transaksi Berhasil!**\n\n💸 ${amount} BNB telah dikirim ke:\n${toAddress}`,
                    'transaction',
                    txHash
                  ),
                ];
              });
            } catch (error: unknown) {
              const msg =
                error instanceof Error
                  ? error.message
                  : 'Transaksi gagal.';
              setMessages((prev) => {
                const filtered = prev.filter(
                  (m) =>
                    !m.content.includes('⏳ Memproses transaksi...')
                );
                return [
                  ...filtered,
                  createMessage('ai', `❌ ${msg}`, 'error'),
                ];
              });
            }
            break;
          }

          default: {
            addMessage(
              createMessage(
                'ai',
                `🤔 Maaf, saya tidak mengerti perintah "${input}".\n\nKetik **"help"** untuk melihat daftar perintah yang tersedia.`
              )
            );
          }
        }
      } catch (error: unknown) {
        const msg =
          error instanceof Error ? error.message : 'Terjadi kesalahan.';
        addMessage(createMessage('ai', `❌ Error: ${msg}`, 'error'));
      } finally {
        setIsProcessing(false);
      }
    },
    [wallet, isProcessing, addMessage]
  );

  return {
    messages,
    wallet,
    isProcessing,
    isRefreshing,
    processMessage,
    refreshBalance,
  };
}
