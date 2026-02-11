import { ChatMessage } from '../types';
import { Bot, User, ExternalLink, Copy, Check } from 'lucide-react';
import { useState } from 'react';
import { cn } from '../utils/cn';

interface ChatBubbleProps {
  message: ChatMessage;
}

export function ChatBubble({ message }: ChatBubbleProps) {
  const isUser = message.role === 'user';
  const [copied, setCopied] = useState(false);

  const handleCopy = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const formatContent = (content: string) => {
    // Bold text
    let formatted = content.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
    // Line breaks
    formatted = formatted.replace(/\n/g, '<br/>');
    // Links
    formatted = formatted.replace(
      /(https?:\/\/[^\s<]+)/g,
      '<a href="$1" target="_blank" rel="noopener noreferrer" class="text-yellow-300 underline hover:text-yellow-200">$1</a>'
    );
    return formatted;
  };

  return (
    <div
      className={cn(
        'flex gap-2 mb-3 animate-in',
        isUser ? 'flex-row-reverse' : 'flex-row'
      )}
    >
      {/* Avatar */}
      <div
        className={cn(
          'flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center',
          isUser
            ? 'bg-gradient-to-br from-blue-500 to-blue-600'
            : 'bg-gradient-to-br from-yellow-400 to-orange-500'
        )}
      >
        {isUser ? (
          <User className="w-4 h-4 text-white" />
        ) : (
          <Bot className="w-4 h-4 text-white" />
        )}
      </div>

      {/* Bubble */}
      <div
        className={cn(
          'max-w-[80%] rounded-2xl px-4 py-2.5 shadow-md',
          isUser
            ? 'bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-tr-md'
            : 'bg-gradient-to-br from-gray-800 to-gray-900 text-gray-100 rounded-tl-md border border-gray-700'
        )}
      >
        <div
          className="text-sm leading-relaxed break-words"
          dangerouslySetInnerHTML={{ __html: formatContent(message.content) }}
        />

        {/* Transaction Hash */}
        {message.txHash && (
          <div className="mt-2 pt-2 border-t border-white/20">
            <div className="flex items-center gap-1 text-xs text-green-300 mb-1">
              ✅ Transaksi Berhasil
            </div>
            <div className="flex items-center gap-1">
              <code className="text-xs bg-black/30 rounded px-1.5 py-0.5 break-all">
                {message.txHash}
              </code>
              <button
                onClick={() => handleCopy(message.txHash!)}
                className="p-1 hover:bg-white/10 rounded transition-colors"
              >
                {copied ? (
                  <Check className="w-3 h-3 text-green-400" />
                ) : (
                  <Copy className="w-3 h-3 text-gray-400" />
                )}
              </button>
              <a
                href={`https://testnet.bscscan.com/tx/${message.txHash}`}
                target="_blank"
                rel="noopener noreferrer"
                className="p-1 hover:bg-white/10 rounded transition-colors"
              >
                <ExternalLink className="w-3 h-3 text-blue-400" />
              </a>
            </div>
          </div>
        )}

        {/* Timestamp */}
        <div
          className={cn(
            'text-[10px] mt-1',
            isUser ? 'text-blue-200' : 'text-gray-500'
          )}
        >
          {message.timestamp.toLocaleTimeString('id-ID', {
            hour: '2-digit',
            minute: '2-digit',
          })}
        </div>
      </div>
    </div>
  );
}
