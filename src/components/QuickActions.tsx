import { Wallet, Send, Search, HelpCircle } from 'lucide-react';

interface QuickActionsProps {
  onAction: (command: string) => void;
}

export function QuickActions({ onAction }: QuickActionsProps) {
  const actions = [
    {
      icon: Wallet,
      label: 'Buat Wallet',
      command: 'Buat wallet baru',
      color: 'from-yellow-400 to-orange-500',
    },
    {
      icon: Search,
      label: 'Cek Saldo',
      command: 'Cek saldo',
      color: 'from-green-400 to-emerald-500',
    },
    {
      icon: Send,
      label: 'Kirim BNB',
      command: 'Kirim 0.01 BNB ke ',
      color: 'from-blue-400 to-indigo-500',
    },
    {
      icon: HelpCircle,
      label: 'Bantuan',
      command: 'help',
      color: 'from-purple-400 to-pink-500',
    },
  ];

  return (
    <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      {actions.map((action) => (
        <button
          key={action.label}
          onClick={() => onAction(action.command)}
          className="flex items-center gap-1.5 bg-gray-800/60 hover:bg-gray-700/80 border border-gray-700/50 rounded-full px-3 py-1.5 text-xs text-gray-300 hover:text-white transition-all whitespace-nowrap group"
        >
          <div
            className={`w-5 h-5 rounded-full bg-gradient-to-br ${action.color} flex items-center justify-center group-hover:scale-110 transition-transform`}
          >
            <action.icon className="w-3 h-3 text-white" />
          </div>
          {action.label}
        </button>
      ))}
    </div>
  );
}
