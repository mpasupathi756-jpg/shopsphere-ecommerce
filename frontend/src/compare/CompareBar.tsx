import { Link } from 'react-router-dom'
import { ArrowUpRight, GitCompareArrows, X } from 'lucide-react'
import { useCompare } from './CompareContext'
export default function CompareBar(){const {items,clear}=useCompare();if(!items.length)return null;return <div className="compare-bar"><span><GitCompareArrows size={16}/><b>{items.length}/4</b> selected for comparison</span><div>{items.map(x=><small key={x.id}>{x.title}</small>)}</div><Link to="/compare">Compare now <ArrowUpRight size={14}/></Link><button onClick={clear} aria-label="Clear comparison"><X size={15}/></button></div>}
