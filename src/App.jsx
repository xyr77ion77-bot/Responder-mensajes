import { useMemo, useState } from 'react'
import {
  Activity, BarChart3, Bell, Bot, Check, ChevronDown, ChevronRight,
  CircleHelp, Clock3, Copy, Edit3, Eye, EyeOff, Filter, LayoutDashboard,
  MessageCircle, MoreHorizontal, Pause, Play, Plus, Search, Settings,
  ShieldCheck, Smartphone, Sparkles, Trash2, Users, X, Zap
} from 'lucide-react'

const initialRules = [
  { id: 1, name: 'Saludo inicial', trigger: 'Hola', match: 'Coincidencia exacta', reply: '¡Hola! 👋 Gracias por escribirnos. ¿En qué podemos ayudarte?', audience: 'Todos', active: true, replies: 142, color: 'green' },
  { id: 2, name: 'Horario de atención', trigger: '*horario*', match: 'Contiene', reply: 'Nuestro horario es de lunes a viernes, de 9:00 a 18:00 h.', audience: 'Todos', active: true, replies: 89, color: 'blue' },
  { id: 3, name: 'Catálogo y precios', trigger: '*precio* | *catálogo*', match: 'Patrón', reply: 'Claro, puedes ver nuestro catálogo actualizado aquí: misitio.com/catalogo', audience: 'Contactos', active: true, replies: 64, color: 'violet' },
  { id: 4, name: 'Fuera de horario', trigger: 'Cualquier mensaje', match: 'Automático', reply: 'Ahora estamos fuera de horario. Te responderemos mañana a primera hora.', audience: 'Todos', active: false, replies: 27, color: 'amber' },
]

const activity = [
  { id: 1, name: 'María González', initials: 'MG', color: '#9367D3', incoming: 'Hola', reply: '¡Hola! 👋 Gracias por escribirnos. ¿En qué podemos ayudarte?', rule: 'Saludo inicial', time: 'Hace 2 min' },
  { id: 2, name: 'Carlos Méndez', initials: 'CM', color: '#E8994E', incoming: '¿Me compartes el horario?', reply: 'Nuestro horario es de lunes a viernes, de 9:00 a 18:00 h.', rule: 'Horario de atención', time: 'Hace 18 min' },
  { id: 3, name: 'Tienda Magnolia', initials: 'TM', color: '#43A68D', incoming: 'Quiero conocer sus precios', reply: 'Claro, puedes ver nuestro catálogo actualizado aquí: misitio.com/catalogo', rule: 'Catálogo y precios', time: 'Hace 43 min' },
  { id: 4, name: 'Número desconocido', initials: '?', color: '#7690AA', incoming: 'Buenas tardes', reply: '¡Hola! 👋 Gracias por escribirnos. ¿En qué podemos ayudarte?', rule: 'Saludo inicial', time: 'Hace 1 h' },
]

const nav = [
  { id: 'inicio', label: 'Resumen', icon: LayoutDashboard },
  { id: 'reglas', label: 'Mis reglas', icon: Zap, badge: 4 },
  { id: 'actividad', label: 'Actividad', icon: Activity },
  { id: 'contactos', label: 'Contactos', icon: Users },
]

function Logo() {
  return <div className="logo"><span className="logo-mark"><MessageCircle size={22}/><Zap size={10}/></span><span>Reply<span>Flow</span></span></div>
}

function Sidebar({ page, setPage }) {
  return <aside className="sidebar">
    <Logo />
    <nav>
      <p className="nav-label">ESPACIO DE TRABAJO</p>
      {nav.map(({ id, label, icon: Icon, badge }) => <button key={id} className={page === id ? 'nav-item active' : 'nav-item'} onClick={() => setPage(id)}>
        <Icon size={19}/><span>{label}</span>{badge && <em>{badge}</em>}
      </button>)}
      <p className="nav-label second">PREFERENCIAS</p>
      <button className={page === 'config' ? 'nav-item active' : 'nav-item'} onClick={() => setPage('config')}><Settings size={19}/><span>Configuración</span></button>
      <button className="nav-item"><CircleHelp size={19}/><span>Centro de ayuda</span></button>
    </nav>
    <div className="sidebar-bottom">
      <div className="plan-card">
        <div className="plan-icon"><Sparkles size={16}/></div>
        <div><b>Plan gratuito</b><span>4 de 5 reglas activas</span></div>
        <div className="progress"><i /></div>
        <button>Mejorar plan</button>
      </div>
      <div className="profile">
        <div className="avatar">LS</div><div><b>Laura Studio</b><span>laura@studio.com</span></div><MoreHorizontal size={18}/>
      </div>
    </div>
  </aside>
}

function Topbar({ page, botActive, setBotActive }) {
  const names = { inicio: 'Resumen', reglas: 'Mis reglas', actividad: 'Actividad', contactos: 'Contactos', config: 'Configuración' }
  return <header className="topbar">
    <div className="mobile-logo"><Logo /></div>
    <div className="crumb"><span>ReplyFlow</span><ChevronRight size={14}/><b>{names[page]}</b></div>
    <div className="top-actions">
      <button className="icon-button"><Bell size={19}/><i /></button>
      <div className="status"><span className={botActive ? 'pulse' : 'pulse off'}></span><span className="desktop-only">Autorespuesta {botActive ? 'activa' : 'pausada'}</span>
        <button onClick={() => setBotActive(!botActive)} className={botActive ? 'switch on' : 'switch'}><i/></button>
      </div>
    </div>
  </header>
}

function MetricCard({ icon: Icon, tone, label, value, detail, trend }) {
  return <div className="metric-card">
    <div className={`metric-icon ${tone}`}><Icon size={20}/></div>
    <div className="metric-heading"><span>{label}</span><MoreHorizontal size={18}/></div>
    <strong>{value}</strong>
    <small className={trend ? 'up' : ''}>{trend && '↗ '}{detail}</small>
  </div>
}

function EmptyHint({ setModal }) {
  return <div className="hint-card">
    <div className="hint-art"><Bot size={30}/><span>✦</span></div>
    <div><b>Haz que ReplyFlow trabaje por ti</b><p>Crea respuestas automáticas para las preguntas que recibes todos los días.</p></div>
    <button onClick={() => setModal(true)}>Crear regla <ChevronRight size={16}/></button>
    <X size={17} className="hint-close"/>
  </div>
}

function RecentActivity({ setPage }) {
  return <section className="panel activity-panel">
    <div className="panel-header"><div><h3>Actividad reciente</h3><p>Últimas respuestas enviadas automáticamente</p></div><button className="text-button" onClick={() => setPage('actividad')}>Ver toda <ChevronRight size={16}/></button></div>
    <div className="activity-list">
      {activity.slice(0, 4).map(item => <div className="activity-row" key={item.id}>
        <div className="contact-avatar" style={{background: item.color}}>{item.initials}</div>
        <div className="activity-content"><div><b>{item.name}</b><span>{item.time}</span></div><p><span>Mensaje:</span> “{item.incoming}”</p></div>
        <div className="rule-pill"><Zap size={12}/>{item.rule}</div>
        <Check size={15} className="sent-check"/>
      </div>)}
    </div>
  </section>
}

function Dashboard({ setPage, setModal, rules }) {
  return <>
    <div className="page-heading"><div><h1>Buenos días, Laura <span>👋</span></h1><p>Aquí tienes un resumen de lo que ReplyFlow hizo por ti.</p></div><button className="primary" onClick={() => setModal(true)}><Plus size={18}/> Nueva regla</button></div>
    <div className="metrics">
      <MetricCard icon={MessageCircle} tone="mint" label="Respuestas enviadas" value="342" detail="12% vs. semana pasada" trend/>
      <MetricCard icon={Users} tone="lilac" label="Contactos atendidos" value="218" detail="27 nuevos esta semana" />
      <MetricCard icon={Clock3} tone="peach" label="Tiempo ahorrado" value="8.4 h" detail="≈ 1.2 h por día" />
      <MetricCard icon={Zap} tone="sky" label="Reglas activas" value={`${rules.filter(r=>r.active).length} / ${rules.length}`} detail="Límite del plan: 5" />
    </div>
    <EmptyHint setModal={setModal}/>
    <div className="dashboard-grid">
      <RecentActivity setPage={setPage}/>
      <section className="panel performance">
        <div className="panel-header"><div><h3>Rendimiento</h3><p>Últimos 7 días</p></div><button className="select-button">7 días <ChevronDown size={14}/></button></div>
        <div className="chart-total"><strong>342</strong><span>respuestas</span><em>+12.4%</em></div>
        <div className="bars">
          {[48,65,53,82,70,95,73].map((h,i)=><div key={i} className="bar-wrap"><div className={`bar ${i===5?'peak':''}`} style={{height:`${h}%`}}>{i===5&&<i>67</i>}</div><span>{['L','M','M','J','V','S','D'][i]}</span></div>)}
        </div>
      </section>
    </div>
  </>
}

function RuleCard({ rule, onToggle, onEdit, onDelete }) {
  return <div className={`rule-card ${!rule.active ? 'disabled' : ''}`}>
    <div className={`rule-color ${rule.color}`}/>
    <div className="rule-main">
      <div className="rule-top"><div className="rule-title"><span className={`rule-symbol ${rule.color}`}><Zap size={16}/></span><div><h3>{rule.name}</h3><span>{rule.match}</span></div></div>
        <div className="rule-actions"><button onClick={() => onToggle(rule.id)} className={rule.active ? 'switch on' : 'switch'}><i/></button><button className="small-icon" onClick={() => onEdit(rule)}><Edit3 size={17}/></button><button className="small-icon danger" onClick={() => onDelete(rule.id)}><Trash2 size={17}/></button></div>
      </div>
      <div className="rule-flow">
        <div><label>CUANDO RECIBES</label><p>“{rule.trigger}”</p></div><ChevronRight size={17}/><div className="reply-preview"><label>RESPONDER CON</label><p>{rule.reply}</p></div>
      </div>
      <div className="rule-footer"><span><Users size={14}/>{rule.audience}</span><span><MessageCircle size={14}/>{rule.replies} respuestas</span><span className={rule.active ? 'active-label':'paused-label'}>{rule.active ? <><Play size={11}/> Activa</> : <><Pause size={11}/> Pausada</>}</span></div>
    </div>
  </div>
}

function RulesPage({ rules, setRules, setModal, setEditing }) {
  const [query,setQuery]=useState('')
  const filtered=rules.filter(r=>r.name.toLowerCase().includes(query.toLowerCase()) || r.trigger.toLowerCase().includes(query.toLowerCase()))
  const edit=(r)=>{setEditing(r);setModal(true)}
  return <>
    <div className="page-heading"><div><h1>Mis reglas</h1><p>Define qué responder y cuándo hacerlo.</p></div><button className="primary" onClick={()=>{setEditing(null);setModal(true)}}><Plus size={18}/> Nueva regla</button></div>
    <div className="rule-toolbar"><div className="search"><Search size={18}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Buscar una regla…"/></div><button className="filter-button"><Filter size={17}/> Filtros <span>1</span></button><div className="rule-count">{filtered.length} reglas</div></div>
    <div className="rules-list">{filtered.map(r=><RuleCard key={r.id} rule={r} onToggle={id=>setRules(rules.map(x=>x.id===id?{...x,active:!x.active}:x))} onEdit={edit} onDelete={id=>setRules(rules.filter(x=>x.id!==id))}/>)}</div>
  </>
}

function ActivityPage() {
  return <><div className="page-heading"><div><h1>Actividad</h1><p>Revisa cada conversación atendida automáticamente.</p></div><button className="filter-button"><Filter size={17}/> Filtrar</button></div>
    <section className="panel full-activity"><div className="activity-head"><span>CONTACTO Y MENSAJE</span><span>RESPUESTA ENVIADA</span><span>REGLA</span><span>HORA</span></div>
      {activity.concat(activity.slice(0,2).map((a,i)=>({...a,id:a.id+10,time:i?'Ayer, 16:20':'Hace 3 h'}))).map(a=><div className="activity-table-row" key={a.id}>
        <div className="contact-cell"><div className="contact-avatar" style={{background:a.color}}>{a.initials}</div><div><b>{a.name}</b><span>“{a.incoming}”</span></div></div><p>{a.reply}</p><span className="rule-pill"><Zap size={12}/>{a.rule}</span><time>{a.time}</time>
      </div>)}
    </section></>
}

function PlaceholderPage({type}) {
  const contacts=type==='contactos'
  return <><div className="page-heading"><div><h1>{contacts?'Contactos':'Configuración'}</h1><p>{contacts?'Personas que interactuaron con tus respuestas.':'Administra la conexión y el comportamiento de ReplyFlow.'}</p></div></div>
  {contacts ? <section className="panel empty-state"><div className="empty-icon"><Users size={28}/></div><h3>Tus contactos aparecerán aquí</h3><p>Cuando ReplyFlow atienda una conversación, guardaremos el contacto y su actividad.</p></section>:
  <div className="settings-grid">
    <section className="panel setting-card"><div className="setting-icon mint"><Smartphone size={21}/></div><div><h3>Conexión del dispositivo</h3><p>Permite el acceso a notificaciones para detectar mensajes entrantes.</p><span className="connected"><Check size={13}/> Conectado</span></div><button className="outline">Administrar</button></section>
    <section className="panel setting-card"><div className="setting-icon lilac"><ShieldCheck size={21}/></div><div><h3>Privacidad</h3><p>Los mensajes se procesan de forma segura y solo se usan para responder.</p></div><button className="outline">Ver opciones</button></section>
    <section className="panel setting-card"><div className="setting-icon peach"><Clock3 size={21}/></div><div><h3>Retraso de respuesta</h3><p>Espera un tiempo natural antes de enviar cada respuesta.</p><b>3 segundos</b></div><button className="outline">Cambiar</button></section>
  </div>}</>
}

function RuleModal({ editing, onClose, onSave }) {
  const [form,setForm]=useState(editing || {name:'',trigger:'',match:'Contiene',reply:'',audience:'Todos',active:true})
  const update=(k,v)=>setForm({...form,[k]:v})
  return <div className="modal-backdrop" onMouseDown={e=>e.target===e.currentTarget&&onClose()}>
    <div className="modal">
      <div className="modal-header"><div><span className="modal-icon"><Zap size={18}/></span><div><h2>{editing?'Editar regla':'Nueva regla'}</h2><p>Configura tu respuesta automática</p></div></div><button className="small-icon" onClick={onClose}><X size={20}/></button></div>
      <div className="modal-body">
        <label className="field"><span>Nombre de la regla</span><input autoFocus value={form.name} onChange={e=>update('name',e.target.value)} placeholder="Ej. Consulta de precios"/></label>
        <div className="field-row"><label className="field"><span>Si el mensaje…</span><select value={form.match} onChange={e=>update('match',e.target.value)}><option>Contiene</option><option>Coincidencia exacta</option><option>Patrón</option><option>Cualquier mensaje</option></select></label><label className="field grow"><span>Palabra o frase</span><input value={form.trigger} onChange={e=>update('trigger',e.target.value)} placeholder="Ej. precio"/></label></div>
        <div className="helper"><Sparkles size={15}/><span>Usa <b>*</b> para aceptar cualquier texto antes o después de una palabra.</span></div>
        <label className="field"><span>Responder con</span><textarea rows="4" value={form.reply} onChange={e=>update('reply',e.target.value)} placeholder="Escribe la respuesta que recibirá la persona…"/><small>{form.reply.length} / 1,000</small></label>
        <label className="field"><span>Enviar a</span><select value={form.audience} onChange={e=>update('audience',e.target.value)}><option>Todos</option><option>Contactos</option><option>Desconocidos</option><option>Grupos</option></select></label>
        <div className="modal-toggle"><div><b>Activar esta regla</b><p>Comenzará a responder en cuanto la guardes.</p></div><button onClick={()=>update('active',!form.active)} className={form.active?'switch on':'switch'}><i/></button></div>
      </div>
      <div className="modal-footer"><button className="cancel" onClick={onClose}>Cancelar</button><button className="primary" disabled={!form.name||!form.trigger||!form.reply} onClick={()=>onSave(form)}><Check size={17}/>{editing?'Guardar cambios':'Crear regla'}</button></div>
    </div>
  </div>
}

function MobileNav({page,setPage}) {
  return <nav className="mobile-nav">{nav.slice(0,3).map(({id,label,icon:Icon})=><button key={id} className={page===id?'active':''} onClick={()=>setPage(id)}><Icon size={20}/><span>{label}</span></button>)}<button className={page==='config'?'active':''} onClick={()=>setPage('config')}><Settings size={20}/><span>Ajustes</span></button></nav>
}

export default function App() {
  const [page,setPage]=useState('inicio')
  const [botActive,setBotActive]=useState(true)
  const [rules,setRules]=useState(initialRules)
  const [modal,setModal]=useState(false)
  const [editing,setEditing]=useState(null)
  const saveRule=(form)=>{
    if(editing) setRules(rules.map(r=>r.id===editing.id?{...r,...form}:r))
    else setRules([{...form,id:Date.now(),replies:0,color:['green','blue','violet','amber'][rules.length%4]},...rules])
    setModal(false);setEditing(null)
  }
  const content=useMemo(()=>{
    if(page==='inicio') return <Dashboard setPage={setPage} setModal={()=>{setEditing(null);setModal(true)}} rules={rules}/>
    if(page==='reglas') return <RulesPage rules={rules} setRules={setRules} setModal={setModal} setEditing={setEditing}/>
    if(page==='actividad') return <ActivityPage/>
    return <PlaceholderPage type={page}/>
  },[page,rules])
  return <div className="app-shell">
    <Sidebar page={page} setPage={setPage}/>
    <div className="main-shell"><Topbar page={page} botActive={botActive} setBotActive={setBotActive}/><main>{content}</main></div>
    <MobileNav page={page} setPage={setPage}/>
    {modal&&<RuleModal editing={editing} onClose={()=>{setModal(false);setEditing(null)}} onSave={saveRule}/>} 
  </div>
}
