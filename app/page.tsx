"use client";

import { useState } from "react";
import { ArrowRight, Heart, Search, ShoppingBag, Sparkles, Truck, RotateCcw, ShieldCheck, Users, ChevronRight } from "lucide-react";

const categories = ["T-Shirts", "Hoodies", "Jeans & Pants", "Jackets", "Sneakers", "Caps & Hats", "Bags", "Sunglasses", "Watches", "Jewellery", "Accessories"];
const products = [
  { name: "Oversized Graphic Tee", price: "₹799", rating: "4.8", img: "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=700&q=80" },
  { name: "Street Classic Sneakers", price: "₹2,499", rating: "4.7", img: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=80" },
  { name: "Baggy Cargo Jeans", price: "₹1,899", rating: "4.6", img: "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=700&q=80" },
  { name: "Minimal Hoodie", price: "₹1,699", rating: "4.8", img: "https://images.unsplash.com/photo-1556821840-3a63f95609a7?auto=format&fit=crop&w=700&q=80" },
  { name: "NY Baseball Cap", price: "₹599", rating: "4.5", img: "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?auto=format&fit=crop&w=700&q=80" },
  { name: "Aero Sunglasses", price: "₹999", rating: "4.6", img: "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=700&q=80" },
];

export default function Home() {
  const [liked, setLiked] = useState<number[]>([]);
  const [chat, setChat] = useState("");
  const [chatOpen, setChatOpen] = useState(false);
  const toggleLike = (i: number) => setLiked((v) => v.includes(i) ? v.filter((x) => x !== i) : [...v, i]);

  return (
    <main>
      <header className="header">
        <div className="brand">NEXORA <span>WEAR YOUR STORY</span></div>
        <nav>{["Home", "Shop", "New Drops", "Categories", "Outfits", "AI Stylist", "Sale"].map((x, i) => <a className={i === 0 ? "active" : ""} href="#" key={x}>{x}</a>)}</nav>
        <div className="search"><Search size={17}/><input placeholder="Search for clothes, sneakers, accessories..." /></div>
        <div className="actions"><Heart/><ShoppingBag/><span className="avatar">N</span></div>
      </header>

      <section className="hero">
        <div className="heroCopy">
          <p className="eyebrow">NEW VIBES / BOLDER FITS / SAME YOU</p>
          <h1>FASHION<br/>THAT GETS YOU <em>GEN Z</em></h1>
          <p>Trendy clothes. Iconic accessories. Personalized by AI. Because your style is more than just an outfit — it&apos;s your story.</p>
          <div className="buttons"><button className="primary">Shop Now <ArrowRight size={17}/></button><button className="secondary" onClick={() => setChatOpen(true)}>Try AI Stylist <Sparkles size={16}/></button></div>
          <div className="trust"><span><Truck/> Free Shipping<small>on orders above ₹999</small></span><span><RotateCcw/> Easy Returns<small>7-day hassle free</small></span><span><ShieldCheck/> Secure Payments<small>100% safe & encrypted</small></span><span><Users/> 20K+<small>Happy Customers</small></span></div>
        </div>
        <div className="heroImage"><img src="https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=1300&q=90" alt="Gen Z streetwear"/><div className="scribble">Same Fits.<br/>Different You</div></div>
        <aside className="stylist">
          <div className="stylistHead"><Sparkles/><strong>Meet Your AI Stylist</strong><b>BETA</b></div>
          <p>Get personalized outfit ideas, style recommendations, and more — just for you.</p>
          {["Suggest a college outfit under ₹2000", "What goes with my black cargos?", "Show me Korean streetwear looks", "What’s trending this week?"].map(q => <button key={q} onClick={() => { setChat(q); setChatOpen(true); }}>{q}<ChevronRight size={15}/></button>)}
          <button className="primary full" onClick={() => setChatOpen(true)}>Start Chatting <ArrowRight size={16}/></button>
        </aside>
      </section>

      <section className="categories">{categories.map((c, i) => <div className="cat" key={c}><div className="catImg"><img src={`https://images.unsplash.com/photo-${[1521572163474,1556821840,1542272604,1551488831,1542291026,1588850561407,1553062407,1511499767150,1523275335684,1523779918550,1505740420928][i]}?auto=format&fit=crop&w=300&q=75`} alt=""/></div><span>{c}</span></div>)}<div className="view">→</div></section>

      <section className="promoGrid"><article className="promo street"><span>NEW DROP</span><h2>Streetwear<br/><i>Reimagined</i></h2><button>Shop Now <ArrowRight size={15}/></button></article><article className="promo look"><div><h2>Shop<br/>the Look</h2><p>Complete outfits,<br/>curated by AI for<br/>every occasion.</p><button>Explore Outfits <ArrowRight size={15}/></button></div><img src="https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=700&q=80" alt="Outfit"/></article><article className="promo sale"><span>GEN Z</span><h2>SALE</h2><p>Up to 50% Off</p><button>Shop Sale <ArrowRight size={15}/></button></article></section>

      <section className="trending"><div className="sectionTitle"><div><h2>🔥 Trending Now</h2><p>Most loved by our community</p></div><a href="#">View All →</a></div><div className="filters"><b>All</b><span>T-Shirts</span><span>Hoodies</span><span>Jeans</span><span>Sneakers</span><span>Accessories</span></div><div className="products">{products.map((p, i) => <article className="product" key={p.name}><div className="productImg"><img src={p.img} alt={p.name}/><button onClick={() => toggleLike(i)} aria-label="Wishlist"><Heart fill={liked.includes(i) ? "currentColor" : "none"}/></button></div><h3>{p.name}</h3><strong>{p.price}</strong><span className="rating">★ {p.rating}</span><button className="add">Add to Cart</button></article>)}</div></section>

      <section className="aiBanner"><div><h2>Style Smarter<br/>with <em>AI</em></h2><p>Upload a photo or tell us your vibe.<br/>Get personalized outfit recommendations in seconds.</p></div><div className="miniLooks"><span>👕</span><span>👖</span><span>👜</span><span>＋</span></div><button className="primary" onClick={() => setChatOpen(true)}>Try AI Stylist <ArrowRight size={16}/></button><i>Your Style<br/>Buddy ✨</i></section>

      {chatOpen && <div className="chatOverlay" onClick={() => setChatOpen(false)}><div className="chat" onClick={e => e.stopPropagation()}><div className="chatTop"><strong>✦ AI Stylist</strong><button onClick={() => setChatOpen(false)}>×</button></div><div className="bot">Hey! Tell me your vibe, budget, or what you&apos;re wearing and I&apos;ll build the look.</div>{chat && <div className="userBubble">{chat}</div>}<div className="suggestions"><button>College fit under ₹2000</button><button>Style my black cargos</button><button>Trending streetwear</button></div><div className="chatInput"><input value={chat} onChange={e => setChat(e.target.value)} placeholder="Tell your AI stylist..."/><button>→</button></div></div></div>}
    </main>
  );
}
