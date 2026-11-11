import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { Bot, ArrowLeft, Send, Sparkles, UserRound, LoaderCircle, ShieldCheck } from 'lucide-react'
import { useAuth } from '../auth/AuthContext'
import { assistantApi } from '../lib/api'

type Message = { role: 'assistant' | 'user'; content: string }
const suggestions = ['Find a laptop for coding', 'Compare wireless headphones', 'Where is my latest order?']

export default function AssistantPage() {
  const { user, loading } = useAuth()
  const [draft, setDraft] = useState('')
  const [pending, setPending] = useState(false)
  const [messages, setMessages] = useState<Message[]>([
    { role: 'assistant', content: 'Hi! I can search ShopSphere products, compare seller offers and check your own orders. What are you looking for?' },
  ])

  async function sendMessage(value = draft) {
    const message = value.trim()
    if (!message || pending) return
    setMessages(current => [...current, { role: 'user', content: message }])
    setDraft('')
    setPending(true)
    try {
      const response = await assistantApi.chat(message)
      setMessages(current => [...current, { role: 'assistant', content: response.answer }])
    } catch (error) {
      const text = error instanceof Error ? error.message : 'The assistant is temporarily unavailable.'
      setMessages(current => [...current, { role: 'assistant', content: text }])
    } finally {
      setPending(false)
    }
  }

  if (loading) return <main className="assistant-loading">Loading your account…</main>
  if (!user) return <Navigate to="/login" replace />

  return <main className="assistant-page">
    <header className="assistant-header">
      <Link to="/" className="assistant-back"><ArrowLeft size={16}/> ShopSphere</Link>
      <div className="assistant-brand"><span><Bot size={19}/></span><div><b>ShopSphere AI</b><small>SHOPPING ASSISTANT</small></div></div>
      <span className="assistant-secure"><ShieldCheck size={14}/> Private session</span>
    </header>
    <section className="assistant-workspace">
      <div className="assistant-intro">
        <span className="assistant-kicker"><Sparkles size={14}/> SHOP WITH MORE CLARITY</span>
        <h1>What can I help<br/>you <em>find?</em></h1>
        <p>Ask naturally. I'll use live ShopSphere catalogue and your own order information to help you decide.</p>
      </div>
      <div className="assistant-chat">
        <div className="assistant-messages" aria-live="polite">
          {messages.map((message, index) => <article className={'assistant-message '+message.role} key={index}>
            <span className="assistant-avatar">{message.role === 'assistant' ? <Bot size={16}/> : <UserRound size={16}/>}</span>
            <p>{message.content}</p>
          </article>)}
          {pending && <article className="assistant-message assistant"><span className="assistant-avatar"><Bot size={16}/></span><p className="assistant-thinking"><LoaderCircle size={15}/> Checking ShopSphere data…</p></article>}
        </div>
        {messages.length === 1 && <div className="assistant-suggestions"><small>TRY ASKING</small>{suggestions.map(s => <button key={s} onClick={() => sendMessage(s)} disabled={pending}>{s}</button>)}</div>}
        <form className="assistant-composer" onSubmit={event => { event.preventDefault(); void sendMessage() }}>
          <textarea value={draft} onChange={event => setDraft(event.target.value)} placeholder="Tell me what you're looking for…" maxLength={1000} rows={2} disabled={pending} />
          <div><small>{draft.length}/1000 · Product and order questions only</small><button type="submit" disabled={!draft.trim() || pending} aria-label="Send message"><Send size={17}/></button></div>
        </form>
        <p className="assistant-disclaimer">Answers use current ShopSphere data. This assistant can read catalogue and your orders only; it cannot place orders, make payments or change account details. Chat history is held only in this page and is not saved yet.</p>
      </div>
    </section>
  </main>
}
