import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, Star, Trash2 } from 'lucide-react'
import { useAuth } from '../auth/AuthContext'
import { reviewsApi } from '../lib/api'
export default function MyReviewsPage(){
 const {user,loading}=useAuth();const qc=useQueryClient();const [pendingDelete,setPendingDelete]=useState<string|null>(null);
 const query=useQuery({queryKey:['my-reviews'],queryFn:()=>reviewsApi.mine()});
 const remove=useMutation({mutationFn:reviewsApi.remove,onSuccess:()=>{setPendingDelete(null);void qc.invalidateQueries({queryKey:['my-reviews']})}});
 if(loading)return <div className="account-loading">Loading…</div>;if(!user)return <Navigate to="/login" replace/>;
 return <main className="workspace-page"><header className="workspace-header"><Link to="/account" className="auth-back"><ArrowLeft size={15}/> Account</Link><span className="workspace-role">Your reviews</span></header><section className="workspace-heading"><span className="section-label">CUSTOMER FEEDBACK</span><h1>My <em>reviews.</em></h1><p>Manage reviews you have written for products you purchased and received.</p></section>
 {query.isLoading?<p>Loading reviews…</p>:query.data?.content.length?<div className="my-review-list">{query.data.content.map(r=><article className="workspace-card my-review-card" key={r.id}><div><Link to={'/products/'+r.productSlug}>{r.productSlug}</Link><div className="review-stars">{Array.from({length:5},(_,i)=><Star key={i} size={13} fill={i<r.rating?'currentColor':'none'}/>)}</div><h3>{r.title}</h3><p>{r.body}</p><small>{new Date(r.createdAt).toLocaleDateString('en-IN')}</small></div>{pendingDelete===r.id?<div className="review-delete-confirm"><span>Remove this review?</span><button onClick={()=>remove.mutate(r.id)} disabled={remove.isPending}>Confirm</button><button onClick={()=>setPendingDelete(null)}>Keep</button></div>:<button className="review-remove-button" onClick={()=>setPendingDelete(r.id)}><Trash2 size={14}/> Remove</button>}</article>)}</div>:<section className="workspace-card"><p>You have not written any reviews yet. You can review a product after its seller order is delivered.</p><Link to="/catalog">Explore products</Link></section>}
 </main>
}