import { useState, useRef, useEffect } from 'react';
import { Send, Menu, X, Zap } from 'lucide-react';
import { ChatBubble } from './components/ChatBubble';
import { WalletPanel } from './components/WalletPanel';
import { QuickActions } from './components/QuickActions';
import { TypingIndicator } from './components/TypingIndicator';
import { useChat } from './hooks/useChat';
import { cn } from './utils/cn';

export function App() {
  const [input, setInput] = useState('');
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const {
    messages,
    wallet,
    isProcessing,
    isRefreshing,
    processMessage,
    refreshBalance,
  } = useChat();

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isProcessing]);

  const handleSend = () => {
    if (!input.trim() || isProcessing) return;
    processMessage(input);
    setInput('');
    inputRef.current?.focus();
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleQuickAction = (command: string) => {
    if (command.endsWith(' ')) {
      setInput(command);
      inputRef.current?.focus();
    } else {
      processMessage(command);
    }
  };

  return (
    <div className="h-screen w-screen flex bg-gray-950 text-white overflow-hidden">
      {/* Sidebar Overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/60 z-30 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          'fixed lg:relative z-40 h-full w-72 bg-gray-900/95 backdrop-blur-xl border-r border-gray-800 transition-transform duration-300 flex flex-col',
          sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        )}
      >
        {/* Sidebar Header */}
        <div className="p-4 border-b border-gray-800">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-10 h-10 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-xl flex items-center justify-center shadow-lg shadow-orange-500/20">
                <Zap className="w-5 h-5 text-white" />
              </div>
              <div>
                <h1 className="text-lg font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent">
                  VibeAgent
                </h1>
                <p className="text-[10px] text-gray-500">AI Financial Assistant</p>
              </div>
            </div>
            <button
              onClick={() => setSidebarOpen(false)}
              className="p-1 hover:bg-gray-800 rounded-lg lg:hidden"
            >
              <X className="w-5 h-5 text-gray-400" />
            </button>
          </div>
        </div>

        {/* Sidebar Content */}
        <div className="flex-1 p-4 space-y-4 overflow-y-auto">
          <WalletPanel
            wallet={wallet}
            onRefreshBalance={refreshBalance}
            isRefreshing={isRefreshing}
          />

          {/* Network Info */}
          <div className="bg-gradient-to-br from-gray-800/50 to-gray-900/50 rounded-2xl p-4 border border-gray-700/50">
            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">
              Network
            </h3>
            <div className="space-y-1.5">
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Chain</span>
                <span className="text-yellow-400">BSC Testnet</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Chain ID</span>
                <span className="text-gray-300">97</span>
              </div>
              <div className="flex justify-between text-xs">
                <span className="text-gray-500">Currency</span>
                <span className="text-gray-300">tBNB</span>
              </div>
            </div>
          </div>

          {/* Faucet Link */}
          <a
            href="https://testnet.bnbchain.org/faucet-smart"
            target="_blank"
            rel="noopener noreferrer"
            className="block bg-gradient-to-r from-yellow-500/10 to-orange-500/10 border border-yellow-500/20 rounded-xl p-3 text-center hover:from-yellow-500/20 hover:to-orange-500/20 transition-colors"
          >
            <p className="text-xs font-medium text-yellow-400">🚰 BNB Testnet Faucet</p>
            <p className="text-[10px] text-gray-500 mt-0.5">Dapatkan BNB gratis untuk testing</p>
          </a>
        </div>

        {/* Sidebar Footer */}
        <div className="p-4 border-t border-gray-800">
          <p className="text-[10px] text-gray-600 text-center">
            VibeAgent v1.0 • BSC Testnet Only
          </p>
        </div>
      </aside>

      {/* Main Chat Area */}
      <main className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="bg-gray-900/80 backdrop-blur-lg border-b border-gray-800 px-4 py-3 flex items-center gap-3">
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-1.5 hover:bg-gray-800 rounded-lg lg:hidden"
          >
            <Menu className="w-5 h-5 text-gray-400" />
          </button>
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-gradient-to-br from-yellow-400 to-orange-500 rounded-lg flex items-center justify-center lg:hidden">
              <Zap className="w-4 h-4 text-white" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-white">VibeAgent Chat</h2>
              <div className="flex items-center gap-1">
                <div className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
                <span className="text-[10px] text-gray-500">Online • BSC Testnet</span>
              </div>
            </div>
          </div>
        </header>

        {/* Messages */}
        <div className="flex-1 overflow-y-auto p-4 space-y-1 bg-gradient-to-b from-gray-950 via-gray-950 to-gray-900">
          {/* Background Pattern */}
          <div className="fixed inset-0 pointer-events-none opacity-[0.02]">
            <div className="absolute inset-0" style={{
              backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
            }} />
          </div>

          <div className="relative z-10">
            {messages.map((msg) => (
              <ChatBubble key={msg.id} message={msg} />
            ))}
            {isProcessing && <TypingIndicator />}
            <div ref={messagesEndRef} />
          </div>
        </div>

        {/* Quick Actions */}
        <div className="px-4 py-2 bg-gray-900/50 border-t border-gray-800/50">
          <QuickActions onAction={handleQuickAction} />
        </div>

        {/* Input Area */}
        <div className="bg-gray-900/80 backdrop-blur-lg border-t border-gray-800 p-3">
          <div className="flex items-center gap-2 max-w-3xl mx-auto">
            <div className="flex-1 relative">
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder='Ketik perintah... contoh: "buat wallet baru"'
                disabled={isProcessing}
                className="w-full bg-gray-800/80 border border-gray-700/50 rounded-xl px-4 py-2.5 text-sm text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500/50 focus:border-yellow-500/50 transition-all disabled:opacity-50"
              />
            </div>
            <button
              onClick={handleSend}
              disabled={!input.trim() || isProcessing}
              className={cn(
                'p-2.5 rounded-xl transition-all',
                input.trim() && !isProcessing
                  ? 'bg-gradient-to-r from-yellow-400 to-orange-500 hover:from-yellow-500 hover:to-orange-600 shadow-lg shadow-orange-500/20 text-white'
                  : 'bg-gray-800 text-gray-600 cursor-not-allowed'
              )}
            >
              <Send className="w-5 h-5" />
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
