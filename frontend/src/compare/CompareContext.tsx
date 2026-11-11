import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
export type CompareItem={id:string;slug:string;title:string;categoryName:string;brandName:string|null}
type CompareValue={items:CompareItem[];toggle:(item:CompareItem)=>void;remove:(id:string)=>void;clear:()=>void}
const Context=createContext<CompareValue|null>(null)
function readSaved():CompareItem[]{try{const value=localStorage.getItem('shopsphere.compare');if(!value)return[];const parsed=JSON.parse(value);return Array.isArray(parsed)?parsed.slice(0,4):[]}catch{return[]}}
export function CompareProvider({children}:{children:ReactNode}){const [items,setItems]=useState<CompareItem[]>(readSaved);useEffect(()=>{try{localStorage.setItem('shopsphere.compare',JSON.stringify(items))}catch{/* comparison still works for this session */}},[items]);const value=useMemo<CompareValue>(()=>({items,toggle(item){setItems(current=>current.some(x=>x.id===item.id)?current.filter(x=>x.id!==item.id):current.length>=4?current:[...current,item])},remove(id){setItems(current=>current.filter(x=>x.id!==id))},clear(){setItems([])}}),[items]);return <Context.Provider value={value}>{children}</Context.Provider>}
export function useCompare(){const value=useContext(Context);if(!value)throw new Error('useCompare must be used inside CompareProvider');return value}
