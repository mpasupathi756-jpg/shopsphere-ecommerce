import { Navigate, Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ArrowLeft, ShieldCheck } from 'lucide-react'
import { useAuth } from '../auth/AuthContext'
import { adminReviewsApi } from '../lib/api'
export default function AdminReviewsPage(){
 const {user,loading}=useAuth();const qc=useQueryClient();const reports=useQuery({queryKey:['admin-review-reports'],queryFn:adminReviewsApi.reports,enabled:!!user?.roles.includes('ADMIN')});
 const refresh=()=>qc.invalidateQueries({queryKey:['admin-review-reports']});
 const hide=useMutation({mutationFn:async(input:{reviewId:string;reportId:string})=>{await adminReviewsApi.moderate(input.reviewId,'HIDDEN','Hidden after customer report review');await adminReviewsApi.resolve(input.reportId,'ACTIONED','Review hidden after moderation');},onSuccess:refresh});
 const dismiss=useMutation({mutationFn:(id:string)=>adminReviewsApi.resolve(id,'DISMISSED','Reviewed by administrator'),onSuccess:refresh});
 const action=useMutation({mutationFn:(id:string)=>adminReviewsApi.resolve(id,'ACTIONED','Review hidden by administrator'),onSuccess:refresh});
 if(loading)return <div className="account-loading">Loading…</div>;if(!user)return <Navigate to="/login" replace/>;if(!user.roles.includes('ADMIN'))return <main className="workspace-page"><h1>Administrator access required.</h1><Link to="/">Return home</Link></main>;
 return <main className="workspace-page"><header className="workspace-header"><Link to="/admin" className="auth-back"><ArrowLeft size={15}/> Admin console</Link><span className="workspace-role"><ShieldCheck size={15}/> Review moderation</span></header><section className="workspace-heading"><span className="section-label">TRUST & SAFETY</span><h1>Review <em>reports.</em></h1><p>Inspect customer reports. A report alone never hides a review; moderation requires an explicit administrator action.</p></section>
 {reports.isLoading?<p>Loading report queue…</p>:reports.data?.content.length?reports.data.content.map(r=><article className="workspace-card review-report-card" key={r.id}><div><small>{r.reasonCode} · Reported by {r.reporterName}</small><h3>{r.productTitle}</h3><b>{r.reviewTitle}</b><p>{r.details||'No additional details supplied.'}</p><small>Review ID: {r.reviewId}</small></div><div className="review-report-actions"><button disabled={hide.isPending||action.isPending} onClick={()=>hide.mutate({reviewId:r.reviewId,reportId:r.id})}>Hide review</button><button disabled={dismiss.isPending} onClick={()=>dismiss.mutate(r.id)}>Dismiss report</button><button disabled={action.isPending} onClick={()=>action.mutate(r.id)}>Mark actioned</button></div></article>):<section className="workspace-card"><p>No open review reports.</p></section>}
 </main>
}