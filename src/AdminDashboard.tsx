import { useEffect, useMemo, useState } from 'react';
import { ArrowLeft, Package, ShoppingCart, Star, ImagePlus, Plus, Pencil, Archive, X } from 'lucide-react';

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const headers = () => ({ Authorization: `Bearer ${localStorage.getItem('genz_store_token') || ''}`, 'Content-Type': 'application/json' });
const money = (n: number) => `₹${Number(n || 0).toLocaleString('en-IN')}`;

type Category = { id:number; name:string; slug:string };
type Product = { id:number; name:string; slug:string; description:string; price:number; rating:number; imageUrl:string; active:boolean; categoryId:number; category:string };
type Variant = { id:number; sku:string; size:string; color:string; stockQuantity:number; priceOverride:number|null; productPrice:number; active:boolean };
type Order = { id:number; status:string; paymentMethod:string; paymentStatus:string; totalAmount:number; createdAt:string };
type Review = { id:number; productId:number; productName:string; rating:number; title:string|null; body:string|null; status:string; createdAt:string };
type ProductForm = { name:string; slug:string; description:string; price:string; rating:string; imageUrl:string; categoryId:string; active:boolean };
type VariantForm = { sku:string; size:string; color:string; stockQuantity:string; priceOverride:string; active:boolean };

const emptyProduct:ProductForm = { name:'', slug:'', description:'', price:'', rating:'0', imageUrl:'', categoryId:'', active:true };
const emptyVariant:VariantForm = { sku:'', size:'', color:'', stockQuantity:'0', priceOverride:'', active:true };

export default function AdminDashboard({ onBack }:{ onBack:()=>void }) {
  const [tab,setTab] = useState<'products'|'orders'|'reviews'|'images'>('products');
  const [products,setProducts] = useState<Product[]>([]);
  const [categories,setCategories] = useState<Category[]>([]);
  const [orders,setOrders] = useState<Order[]>([]);
  const [reviews,setReviews] = useState<Review[]>([]);
  const [allVariants,setAllVariants] = useState<Record<number,Variant[]>>({});
  const [variants,setVariants] = useState<Variant[]>([]);
  const [selected,setSelected] = useState<number|null>(null);
  const [loading,setLoading] = useState(true);
  const [message,setMessage] = useState('');
  const [productForm,setProductForm] = useState<ProductForm>(emptyProduct);
  const [variantForm,setVariantForm] = useState<VariantForm>(emptyVariant);
  const [editingProduct,setEditingProduct] = useState<number|null>(null);
  const [editingVariant,setEditingVariant] = useState<number|null>(null);
  const [showProductForm,setShowProductForm] = useState(false);
  const [showVariantForm,setShowVariantForm] = useState(false);
  const [uploadProduct,setUploadProduct] = useState('');

  const notice = (m:string) => { setMessage(m); window.setTimeout(() => setMessage(''),3500); };

  const load = async () => {
    setLoading(true);
    try {
      const [p,o,r,c] = await Promise.all([
        fetch(`${API}/api/v1/admin/products`,{headers:headers()}),
        fetch(`${API}/api/v1/admin/orders`,{headers:headers()}),
        fetch(`${API}/api/v1/admin/reviews`,{headers:headers()}),
        fetch(`${API}/api/v1/categories`)
      ]);
      if ([p,o,r].some(x => x.status === 403)) throw Error('Admin access required');
      if (!p.ok || !o.ok || !r.ok) throw Error('Unable to load admin data');
      const pj = await p.json();
      const oj = await o.json();
      const rj = await r.json();
      setProducts(pj.content ?? []);
      setOrders(oj.content ?? []);
      setReviews(rj.content ?? []);
      if (c.ok) { const cj = await c.json(); setCategories(cj.content ?? cj ?? []); }
    } catch (e:any) { notice(e.message || 'Unable to load dashboard'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const refreshVariants = async (id:number) => {
    setSelected(id);
    const r = await fetch(`${API}/api/v1/admin/products/${id}/variants`,{headers:headers()});
    if (!r.ok) { notice('Could not load variants'); return; }
    const v = await r.json();
    setVariants(v);
    setAllVariants(a => ({...a,[id]:v}));
  };

  const loadAllVariants = async () => {
    const entries = await Promise.all(products.map(async p => {
      const r = await fetch(`${API}/api/v1/admin/products/${p.id}/variants`,{headers:headers()});
      return [p.id,r.ok ? await r.json() : []] as const;
    }));
    setAllVariants(Object.fromEntries(entries));
  };

  useEffect(() => { if (products.length) loadAllVariants(); }, [products.length]);
  const lowStock = useMemo(() => Object.values(allVariants).flat().filter(v => v.active && v.stockQuantity < 5).length,[allVariants]);

  const saveProduct = async () => {
    if (!productForm.name.trim() || !productForm.slug.trim() || !productForm.description.trim() || !productForm.price || !productForm.categoryId) { notice('Fill all required product fields'); return; }
    try {
      const body = {...productForm,price:Number(productForm.price),rating:Number(productForm.rating),categoryId:Number(productForm.categoryId)};
      const path = editingProduct ? `/api/v1/admin/products/${editingProduct}` : '/api/v1/admin/products';
      const r = await fetch(`${API}${path}`,{method:editingProduct ? 'PUT':'POST',headers:headers(),body:JSON.stringify(body)});
      if (!r.ok) throw Error(await r.text());
      notice(editingProduct ? 'Product updated' : 'Product created');
      setShowProductForm(false); setEditingProduct(null); setProductForm(emptyProduct); load();
    } catch (e:any) { notice(e.message || 'Could not save product'); }
  };

  const saveVariant = async () => {
    if (!selected || !variantForm.sku.trim() || !variantForm.size.trim() || !variantForm.color.trim()) { notice('Fill all required variant fields'); return; }
    try {
      const body = {...variantForm,stockQuantity:Number(variantForm.stockQuantity),priceOverride:variantForm.priceOverride === '' ? null : Number(variantForm.priceOverride)};
      const path = editingVariant ? `/api/v1/admin/variants/${editingVariant}` : `/api/v1/admin/products/${selected}/variants`;
      const r = await fetch(`${API}${path}`,{method:editingVariant ? 'PUT':'POST',headers:headers(),body:JSON.stringify(body)});
      if (!r.ok) throw Error(await r.text());
      notice(editingVariant ? 'Variant updated' : 'Variant created');
      setShowVariantForm(false); setEditingVariant(null); setVariantForm(emptyVariant); refreshVariants(selected);
    } catch (e:any) { notice(e.message || 'Could not save variant'); }
  };

  const archiveProduct = async (id:number) => {
    if (!confirm('Archive this product?')) return;
    const r = await fetch(`${API}/api/v1/admin/products/${id}`,{method:'DELETE',headers:headers()});
    r.ok ? (notice('Product archived'),load()) : notice('Could not archive product');
  };

  const archiveVariant = async (id:number) => {
    if (!confirm('Archive this variant?')) return;
    const r = await fetch(`${API}/api/v1/admin/variants/${id}`,{method:'DELETE',headers:headers()});
    if (r.ok && selected) { notice('Variant archived'); refreshVariants(selected); } else notice('Could not archive variant');
  };

  const updateStatus = async (path:string,value:string) => {
    const r = await fetch(`${API}${path}`,{method:'PATCH',headers:headers(),body:JSON.stringify({status:value})});
    r.ok ? (notice('Saved'),load()) : notice('Update failed');
  };

  const upload = async (file:File) => {
    if (file.size > 5 * 1024 * 1024) { notice('Maximum file size is 5 MB'); return; }
    const fd = new FormData(); fd.append('file',file);
    const r = await fetch(`${API}/api/v1/images`,{method:'POST',headers:{Authorization:`Bearer ${localStorage.getItem('genz_store_token') || ''}`},body:fd});
    if (!r.ok) { notice('Upload failed'); return; }
    const data = await r.json();
    const url = data.url;
    if (uploadProduct) {
      const p = products.find(x => x.id === Number(uploadProduct));
      if (p) {
        const u = await fetch(`${API}/api/v1/admin/products/${p.id}`,{method:'PUT',headers:headers(),body:JSON.stringify({name:p.name,slug:p.slug,description:p.description,price:Number(p.price),rating:Number(p.rating),imageUrl:url,categoryId:p.categoryId,active:p.active})});
        u.ok ? (notice('Image uploaded and assigned'),load()) : notice('Image uploaded but assignment failed');
      }
    } else notice('Image uploaded');
  };

  const editProduct = (p:Product) => { setEditingProduct(p.id); setProductForm({name:p.name,slug:p.slug,description:p.description,price:String(p.price),rating:String(p.rating),imageUrl:p.imageUrl,categoryId:String(p.categoryId),active:p.active}); setShowProductForm(true); };
  const editVariant = (v:Variant) => { setEditingVariant(v.id); setVariantForm({sku:v.sku,size:v.size,color:v.color,stockQuantity:String(v.stockQuantity),priceOverride:v.priceOverride == null ? '' : String(v.priceOverride),active:v.active}); setShowVariantForm(true); };

  return (
    <div className="adminShell">
      <style>{`.adminShell{min-height:100vh;background:#f6f5f2;color:#151515}.adminTop{background:#111;color:#fff;padding:18px 5%;display:flex;align-items:center;gap:20px}.adminTop button{background:none;border:0;color:inherit;cursor:pointer}.adminTop h1{margin:0;font-size:24px}.adminLayout{display:grid;grid-template-columns:210px 1fr;min-height:calc(100vh - 64px)}.adminSide{background:#fff;border-right:1px solid #ddd;padding:20px}.adminSide button{display:flex;gap:10px;width:100%;padding:13px;border:0;background:none;text-align:left;cursor:pointer}.adminSide button.active{background:#eee;font-weight:700}.adminMain{padding:30px 5%}.adminStats{display:grid;grid-template-columns:repeat(3,1fr);gap:15px;margin-bottom:25px}.stat{background:#fff;padding:20px}.stat b{font-size:28px;display:block;margin-top:8px}.adminTable{background:#fff;overflow:auto}.adminTable table{width:100%;border-collapse:collapse}.adminTable th,.adminTable td{padding:13px;border-bottom:1px solid #eee;text-align:left;white-space:nowrap}.adminBtn{border:1px solid #ccc;background:#fff;padding:8px 12px;cursor:pointer;margin-right:5px;display:inline-flex;gap:5px;align-items:center}.danger{border-color:#d99}.variantBox,.uploadBox,.formBox{background:#fff;margin-top:15px;padding:20px}.formGrid{display:grid;grid-template-columns:repeat(2,1fr);gap:12px}.formGrid label{display:block}.formGrid input,.formGrid textarea,.formGrid select,.uploadBox select{width:100%;box-sizing:border-box;padding:10px;border:1px solid #ccc;margin-top:5px}.formGrid textarea{min-height:90px}.formActions{display:flex;gap:8px;margin-top:15px}.primary{background:#111;color:#fff;border:0;padding:10px 16px;cursor:pointer;display:inline-flex;gap:6px;align-items:center}.statusSelect{padding:8px}.notice{padding:12px;background:#fff3cd;margin-bottom:15px}.variantRow{padding:10px 0;border-bottom:1px solid #eee;display:flex;gap:10px;align-items:center;justify-content:space-between}@media(max-width:750px){.adminLayout{grid-template-columns:1fr}.adminSide{display:flex;overflow:auto}.adminSide button{white-space:nowrap}.adminStats,.formGrid{grid-template-columns:1fr}}`}</style>
      <header className="adminTop"><button onClick={onBack}><ArrowLeft/></button><h1>GenZ Store · Admin</h1></header>
      <div className="adminLayout">
        <aside className="adminSide">{[['products',Package,'Products'],['orders',ShoppingCart,'Orders'],['reviews',Star,'Reviews'],['images',ImagePlus,'Images']].map(([key,Icon,label]:any)=><button className={tab===key?'active':''} onClick={()=>setTab(key)} key={key}><Icon size={18}/>{label}</button>)}</aside>
        <main className="adminMain">
          {message && <div className="notice">{message}</div>}
          {loading ? <p>Loading dashboard…</p> : <>
            {tab==='products' && <>
              <div className="adminStats"><div className="stat">Products<b>{products.length}</b></div><div className="stat">Active<b>{products.filter(x=>x.active).length}</b></div><div className="stat">Low stock<b>{lowStock}</b></div></div>
              <button className="primary" onClick={()=>{setEditingProduct(null);setProductForm(emptyProduct);setShowProductForm(true)}}><Plus size={16}/> Add product</button>
              {showProductForm && <div className="formBox"><h2>{editingProduct?'Edit product':'New product'}</h2><div className="formGrid">{[['name','Name'],['slug','Slug'],['price','Price'],['rating','Rating'],['imageUrl','Image URL']].map(([k,l])=><label key={k}>{l}<input value={(productForm as any)[k]} onChange={e=>setProductForm(f=>({...f,[k]:e.target.value}))}/></label>)}<label>Category<select value={productForm.categoryId} onChange={e=>setProductForm(f=>({...f,categoryId:e.target.value}))}><option value="">Select category</option>{categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></label><label>Description<textarea value={productForm.description} onChange={e=>setProductForm(f=>({...f,description:e.target.value}))}/></label></div><div className="formActions"><button className="primary" onClick={saveProduct}>Save</button><button className="adminBtn" onClick={()=>setShowProductForm(false)}><X size={16}/> Cancel</button></div></div>}
              <div className="adminTable" style={{marginTop:15}}><table><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Rating</th><th>Status</th><th>Actions</th></tr></thead><tbody>{products.map(p=><tr key={p.id}><td>{p.name}</td><td>{p.category}</td><td>{money(p.price)}</td><td>★ {p.rating}</td><td>{p.active?'Active':'Archived'}</td><td><button className="adminBtn" onClick={()=>refreshVariants(p.id)}>Variants</button><button className="adminBtn" onClick={()=>editProduct(p)}><Pencil size={14}/> Edit</button>{p.active&&<button className="adminBtn danger" onClick={()=>archiveProduct(p.id)}><Archive size={14}/> Archive</button>}</td></tr>)}</tbody></table></div>
              {selected && <div className="variantBox"><div style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}><h3>Variants · {products.find(x=>x.id===selected)?.name}</h3><button className="primary" onClick={()=>{setEditingVariant(null);setVariantForm(emptyVariant);setShowVariantForm(true)}}><Plus size={15}/> Add variant</button></div>{showVariantForm&&<div className="formBox"><h3>{editingVariant?'Edit variant':'New variant'}</h3><div className="formGrid">{[['sku','SKU'],['size','Size'],['color','Color'],['stockQuantity','Stock'],['priceOverride','Price override']].map(([k,l])=><label key={k}>{l}<input value={(variantForm as any)[k]} onChange={e=>setVariantForm(f=>({...f,[k]:e.target.value}))}/></label>)}</div><div className="formActions"><button className="primary" onClick={saveVariant}>Save</button><button className="adminBtn" onClick={()=>setShowVariantForm(false)}>Cancel</button></div></div>}{variants.map(v=><div className="variantRow" key={v.id}><span>{v.sku} · {v.size} · {v.color} · stock {v.stockQuantity} · {v.active?'Active':'Archived'}</span><span><button className="adminBtn" onClick={()=>editVariant(v)}>Edit</button>{v.active&&<button className="adminBtn danger" onClick={()=>archiveVariant(v.id)}>Archive</button>}</span></div>)}</div>}
            </>}
            {tab==='orders' && <div className="adminTable"><table><thead><tr><th>Order</th><th>Status</th><th>Payment</th><th>Total</th><th>Created</th></tr></thead><tbody>{orders.map(o=><tr key={o.id}><td>#{o.id}</td><td><select className="statusSelect" value={o.status} onChange={e=>updateStatus(`/api/v1/admin/orders/${o.id}/status`,e.target.value)}><option value={o.status}>{o.status}</option>{o.status==='PENDING_PAYMENT'&&<><option>CONFIRMED</option><option>CANCELLED</option></>}{o.status==='CONFIRMED'&&<><option>PROCESSING</option><option>CANCELLED</option></>}{o.status==='PROCESSING'&&<option>SHIPPED</option>}{o.status==='SHIPPED'&&<option>DELIVERED</option>}</select></td><td>{o.paymentMethod} · {o.paymentStatus}</td><td>{money(o.totalAmount)}</td><td>{new Date(o.createdAt).toLocaleString()}</td></tr>)}</tbody></table></div>}
            {tab==='reviews' && <div className="adminTable"><table><thead><tr><th>Product</th><th>Rating</th><th>Review</th><th>Status</th></tr></thead><tbody>{reviews.map(r=><tr key={r.id}><td>{r.productName}</td><td>★ {r.rating}</td><td>{r.title||'Review'}<br/>{r.body||''}</td><td><select className="statusSelect" value={r.status} onChange={e=>updateStatus(`/api/v1/admin/reviews/${r.id}/status`,e.target.value)}><option>PENDING</option><option>APPROVED</option><option>REJECTED</option></select></td></tr>)}</tbody></table></div>}
            {tab==='images' && <div className="uploadBox"><h2>Product image upload</h2><p>JPG, PNG or WebP · max 5 MB</p><label>Assign to product<select value={uploadProduct} onChange={e=>setUploadProduct(e.target.value)}><option value="">None</option>{products.map(p=><option key={p.id} value={p.id}>{p.name}</option>)}</select></label><div style={{marginTop:12}}><input type="file" accept="image/jpeg,image/png,image/webp" onChange={e=>{const f=e.target.files?.[0];if(f)upload(f)}}/></div></div>}
          </>}
        </main>
      </div>
    </div>
  );
}
