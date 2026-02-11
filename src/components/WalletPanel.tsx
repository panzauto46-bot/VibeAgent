import { WalletInfo } from '../types';
import { Wallet, Copy, Check, Eye, EyeOff, RefreshCw } from 'lucide-react';
import { useState } from 'react';
import { shortenAddress } from '../utils/wallet';
import { cn } from '../utils/cn';

interface WalletPanelProps {
  wallet: WalletInfo | null;
  onRefreshBalance: () => void;
  isRefreshing: boolean;
}

export function WalletPanel({ wallet, onRefreshBalance, isRefreshing }: WalletPanelProps) {
  const [showKey, setShowKey] = useState(false);
  const [copiedField, setCopiedField] = useState<string | null>(null);

  const handleCopy = (text: string, field: string) => {
    navigator.clipboard.writeText(text);
    setCopiedField(field);
    setTimeout(() => setCopiedField(null), 2000);
  };

  if (!wallet) {
    return (
      <div className="bg-gradient-to-br from-gray-800/80 to-gray-900/80 backdrop-blur-lg rounded-2xl p-4 border border-gray-700/50">
        <div className="flex items-center gap-2 mb-3">
          <div className="w-8 h-8 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-lg flex items-center justify-center">
            <Wallet className="w-4 h-4 text-white" />
          </div>
          <h3 className="text-sm font-semibold text-white">Wallet</h3>
        </div>
        <div className="text-center py-4">
          <p className="text-gray-400 text-xs">Belum ada wallet.</p>
          <p className="text-gray-500 text-xs mt-1">
            Ketik "buat wallet baru" di chat
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-gray-800/80 to-gray-900/80 backdrop-blur-lg rounded-2xl p-4 border border-gray-700/50">
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-lg flex items-center justify-center">
            <Wallet className="w-4 h-4 text-white" />
          </div>
          <h3 className="text-sm font-semibold text-white">Wallet</h3>
        </div>
        <span className="text-[10px] bg-green-500/20 text-green-400 px-2 py-0.5 rounded-full border border-green-500/30">
          BSC Testnet
        </span>
      </div>

      {/* Balance */}
      <div className="bg-black/30 rounded-xl p-3 mb-3">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-[10px] text-gray-400 uppercase tracking-wider">Saldo</p>
            <p className="text-xl font-bold text-white">
              {parseFloat(wallet.balance).toFixed(4)}{' '}
              <span className="text-yellow-400 text-sm">BNB</span>
            </p>
          </div>
          <button
            onClick={onRefreshBalance}
            disabled={isRefreshing}
            className="p-2 hover:bg-white/10 rounded-lg transition-colors disabled:opacity-50"
          >
            <RefreshCw
              className={cn(
                'w-4 h-4 text-gray-400',
                isRefreshing && 'animate-spin'
              )}
            />
          </button>
        </div>
      </div>

      {/* Address */}
      <div className="space-y-2">
        <div className="flex items-center justify-between bg-black/20 rounded-lg px-3 py-2">
          <div>
            <p className="text-[10px] text-gray-500">Address</p>
            <p className="text-xs text-gray-300 font-mono">
              {shortenAddress(wallet.address)}
            </p>
          </div>
          <button
            onClick={() => handleCopy(wallet.address, 'address')}
            className="p-1.5 hover:bg-white/10 rounded transition-colors"
          >
            {copiedField === 'address' ? (
              <Check className="w-3.5 h-3.5 text-green-400" />
            ) : (
              <Copy className="w-3.5 h-3.5 text-gray-400" />
            )}
          </button>
        </div>

        {/* Private Key */}
        <div className="flex items-center justify-between bg-black/20 rounded-lg px-3 py-2">
          <div className="flex-1 min-w-0">
            <p className="text-[10px] text-gray-500">Private Key</p>
            <p className="text-xs text-gray-300 font-mono truncate">
              {showKey ? wallet.privateKey : '••••••••••••••••'}
            </p>
          </div>
          <div className="flex items-center gap-1">
            <button
              onClick={() => setShowKey(!showKey)}
              className="p-1.5 hover:bg-white/10 rounded transition-colors"
            >
              {showKey ? (
                <EyeOff className="w-3.5 h-3.5 text-gray-400" />
              ) : (
                <Eye className="w-3.5 h-3.5 text-gray-400" />
              )}
            </button>
            <button
              onClick={() => handleCopy(wallet.privateKey, 'key')}
              className="p-1.5 hover:bg-white/10 rounded transition-colors"
            >
              {copiedField === 'key' ? (
                <Check className="w-3.5 h-3.5 text-green-400" />
              ) : (
                <Copy className="w-3.5 h-3.5 text-gray-400" />
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
