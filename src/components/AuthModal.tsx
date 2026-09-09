import { FormEvent, useState } from 'react';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080';

type Props = { onClose: () => void; onAuthenticated: (user: { firstName: string; email: string }) => void };

export default function AuthModal({ onClose, onAuthenticated }: Props) {
  const [mode, setMode] = useState<'login'|'register'>('login');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function submit(e: FormEvent) {
    e.preventDefault(); setError(''); setLoading(true);
    try {
      const body = mode === 'login' ? { email, password } : { firstName, lastName, email, password };
      const res = await fetch(`${API}/api/v1/auth/${mode}`, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
      const data = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(data.message || data.error || 'Unable to continue');
      localStorage.setItem('nextgen_token', data.token);
      localStorage.setItem('nextgen_user', JSON.stringify(data.user));
      onAuthenticated(data.user); onClose();
    } catch (err) { setError(err instanceof Error ? err.message : 'Unable to continue'); }
    finally { setLoading(false); }
  }

  return <div className="authOverlay" onClick={onClose}>
    <section className="authCard" onClick={e=>e.stopPropagation()}>
      <button className="authClose" onClick={onClose}>×</button>
      <div className="authBrand">NEXTGEN<small>WEAR WHAT'S NEXT</small></div>
      <h2>{mode === 'login' ? 'Welcome back.' : 'Create your account.'}</h2>
      <p className="authSub">{mode === 'login' ? 'Sign in to save your wishlist, cart and orders.' : 'Join NEXTGEN for a personalized shopping experience.'}</p>
      <div className="authTabs"><button className={mode==='login'?'active':''} onClick={()=>{setMode('login');setError('')}}>Sign In</button><button className={mode==='register'?'active':''} onClick={()=>{setMode('register');setError('')}}>Create Account</button></div>
      <form onSubmit={submit}>
        {mode==='register' && <div className="authRow"><input required value={firstName} onChange={e=>setFirstName(e.target.value)} placeholder="First name"/><input value={lastName} onChange={e=>setLastName(e.target.value)} placeholder="Last name"/></div>}
        <input required type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="Email address" autoComplete="email"/>
        <input required minLength={8} type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Password (8+ characters)" autoComplete={mode==='login'?'current-password':'new-password'}/>
        {error && <div className="authError">{error}</div>}
        <button className="authSubmit" disabled={loading}>{loading ? 'Please wait…' : mode==='login' ? 'Sign In →' : 'Create Account →'}</button>
      </form>
      <p className="authFine">By continuing, you agree to the NEXTGEN terms and privacy policy.</p>
    </section>
  </div>;
}
