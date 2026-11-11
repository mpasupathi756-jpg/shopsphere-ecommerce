import { useState, type FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, BadgeCheck, GitCompareArrows, Heart, PackageCheck, ShieldCheck, ShoppingBag, Store, Star } from 'lucide-react'
import { useAuth } from '../auth/AuthContext'
import { useCompare } from '../compare/CompareContext'
import { cartApi, catalogueApi, wishlistApi, reviewsApi } from '../lib/api'
import { formatINR } from '../components/ProductCard'

export default function ProductPage(){
 const {slug=''}=useParams();const {user}=useAuth();const {items,toggle}=useCompare();const navigate=useNavigate();const qc=useQueryClient();
 const [reviewPage,setReviewPage]=useState(0);const [rating,setRating]=useState(5);const [reviewTitle,setReviewTitle]=useState('');const [reviewBody,setReviewBody]=useState('');
 const product=useQuery({queryKey:['product',slug],queryFn:()=>catalogueApi.product(slug),enabled:!!slug});
 const reviewList=useQuery({queryKey:['product-reviews',slug,reviewPage],queryFn:()=>reviewsApi.list(slug,reviewPage),enabled:!!slug});
 const eligibility=useQuery({queryKey:['review-eligibility',slug],queryFn:()=>reviewsApi.eligibility(slug),enabled:!!slug&&!!user});
 const addCart=useMutation({mutationFn:(listingId:string)=>cartApi.add(listingId,1),onSuccess:()=>qc.invalidateQueries({queryKey:['cart']})});
 const addWish=useMutation({mutationFn:(productId:string)=>wishlistApi.add(productId),onSuccess:()=>qc.invalidateQueries({queryKey:['wishlist']})});
 const createReview=useMutation({mutationFn:()=>reviewsApi.create(slug,{rating,title:reviewTitle,body:reviewBody}),onSuccess:()=>{setReviewTitle('');setReviewBody('');setRating(5);void qc.invalidateQueries({queryKey:['product-reviews',slug]});void qc.invalidateQueries({queryKey:['product',slug]});void qc.invalidateQueries({queryKey:['review-eligibility',slug]})}});
 function submitReview(e:FormEvent){e.preventDefault();createReview.mutate()}
 if(product.isLoading)return <main className="product-page"><p>Loading product details…</p></main>;
 if(product.isError||!product.data)return <main className="product-page"><Link to="/catalog"><ArrowLeft size={15}/> Back to catalogue</Link><h1>Product not found</h1></main>;
 const p=product.data;
 return <main className="product-page">
  <Link to="/catalog" className="product-back"><ArrowLeft size={15}/> Back to catalogue</Link>
  <div className="product-detail-grid">
   <section className="product-visual"><span>{p.categoryName}</span><b>{p.title.slice(0,1).toUpperCase()}</b><small>PRODUCT IMAGE WILL APPEAR HERE</small></section>
   <section className="product-summary">
    <span className="section-label">{p.brandName??p.categoryName}</span><h1>{p.title}</h1>
    {p.reviewCount>0?<div className="product-rating-summary"><Star size={16} fill="currentColor"/> <b>{p.averageRating?.toFixed(1)}</b><span>({p.reviewCount} {p.reviewCount===1?'review':'reviews'})</span></div>:<p className="product-no-rating">No customer reviews yet</p>}
    <p>{p.description}</p>
    <div className="product-actions"><button className="wishlist-action" onClick={()=>user?addWish.mutate(p.id):navigate("/login")}><Heart size={15}/> Save to wishlist</button><button className="compare-add" onClick={()=>toggle({id:p.id,slug:p.slug,title:p.title,categoryName:p.categoryName,brandName:p.brandName})}><GitCompareArrows size={13}/>{items.some(x=>x.id===p.id)?"In comparison":"Compare"}</button></div>
    <div className="product-proof"><span><ShieldCheck size={15}/> Seller details shown clearly</span><span><BadgeCheck size={15}/> Offers reviewed before publishing</span></div>
    <h2>Compare seller offers</h2>
    {addCart.isError&&<p className="form-error">{addCart.error instanceof Error?addCart.error.message:'Unable to add this offer'}</p>}
    {addCart.isSuccess&&<p className="inline-success">Added to your cart. <Link to="/cart">View cart</Link></p>}
    <div className="offer-list">{p.offers.map(o=><article className="offer-card" key={o.listingId}><div className="offer-seller"><span className="offer-store-icon"><Store size={16}/></span><div><b>{o.storeName}</b><small>Seller SKU: {o.sellerSku}</small></div></div><strong>{formatINR(o.priceMinor)}</strong><div className="offer-facts"><span><PackageCheck size={14}/> {o.availableQuantity} in stock</span><span>Delivery: {o.shippingDaysMin}–{o.shippingDaysMax} days</span><span>{o.returnWindowDays}-day return window</span></div><button className="offer-add" disabled={o.availableQuantity<1||addCart.isPending} onClick={()=>user?addCart.mutate(o.listingId):navigate('/login')}>{o.availableQuantity<1?'Out of stock':addCart.isPending?'Adding…':'Add to cart'} <ShoppingBag size={14}/></button></article>)}</div>
   </section>
  </div>
  <section className="product-specs"><small className="section-label">PRODUCT DETAILS</small><h2>Specifications</h2>{p.attributes.length?<div className="spec-list">{p.attributes.map(a=><div key={a.key}><span>{a.label}</span><b>{a.value}{a.unit?' '+a.unit:''}</b></div>)}</div>:<p>Specifications will be added as this product catalogue grows.</p>}</section>
  <section className="product-reviews-section">
   <div className="reviews-heading"><div><small className="section-label">CUSTOMER FEEDBACK</small><h2>Reviews & ratings</h2><p>Reviews are limited to customers with a delivered purchase.</p></div><div className="reviews-average">{p.reviewCount>0?<><b><Star size={17} fill="currentColor"/> {p.averageRating?.toFixed(1)}</b><span>{p.reviewCount} verified {p.reviewCount===1?'review':'reviews'}</span></>:<span>Be the first to review</span>}</div></div>
   {user&&eligibility.data?.eligible&&<form className="review-form" onSubmit={submitReview}><h3>Write a product review</h3><label>Rating<select value={rating} onChange={e=>setRating(Number(e.target.value))}><option value={5}>5 — Excellent</option><option value={4}>4 — Good</option><option value={3}>3 — Average</option><option value={2}>2 — Below average</option><option value={1}>1 — Poor</option></select></label><label>Review title<input value={reviewTitle} onChange={e=>setReviewTitle(e.target.value)} maxLength={120} required placeholder="Summarise your experience"/></label><label>Your review<textarea value={reviewBody} onChange={e=>setReviewBody(e.target.value)} maxLength={3000} required rows={4} placeholder="What should other customers know?"/></label>{createReview.isError&&<p className="form-error">{createReview.error instanceof Error?createReview.error.message:'Unable to submit review'}</p>}<button className="auth-submit" disabled={createReview.isPending}>{createReview.isPending?'Submitting…':'Publish review'}</button></form>}
   {user&&eligibility.data&&!eligibility.data.eligible&&<p className="review-eligibility-note">{eligibility.data.message}</p>}
   {!user&&<p className="review-eligibility-note"><Link to="/login">Sign in</Link> to check whether you can review this product after delivery.</p>}
   {reviewList.isLoading?<p>Loading reviews…</p>:reviewList.data?.content.length?<div className="review-list">{reviewList.data.content.map(review=><article className="customer-review" key={review.id}><div className="customer-review-head"><b>{review.authorName}</b><span><BadgeCheck size={13}/> Verified purchase</span></div><div className="review-stars">{Array.from({length:5},(_,i)=><Star key={i} size={13} fill={i<review.rating?'currentColor':'none'}/>)}</div><h3>{review.title}</h3><p>{review.body}</p><small>{new Date(review.createdAt).toLocaleDateString('en-IN')}</small></article>)}</div>:<p className="review-eligibility-note">No published reviews yet.</p>}
   {reviewList.data&&reviewList.data.totalPages>1&&<div className="review-pagination"><button disabled={reviewPage===0} onClick={()=>setReviewPage(v=>Math.max(0,v-1))}>Previous</button><span>Page {reviewPage+1} of {reviewList.data.totalPages}</span><button disabled={reviewPage+1>=reviewList.data.totalPages} onClick={()=>setReviewPage(v=>v+1)}>Next</button></div>}
  </section>
 </main>
}
