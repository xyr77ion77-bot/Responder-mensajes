const seedRules = [
  {id:1,name:'Saludo inicial',trigger:'hola',match:'contains',replies:['¡Hola! 👋 Gracias por escribirnos. ¿En qué podemos ayudarte?'],replyMode:'one',audience:'all',delay:1,active:true},
  {id:2,name:'Horario de atención',trigger:'horario',match:'contains',replies:['Atendemos de lunes a viernes, de 9:00 a 18:00 h.'],replyMode:'one',audience:'all',delay:3,active:true},
  {id:3,name:'Catálogo y precios',trigger:'precio|catálogo',match:'pattern',replies:['Claro, puedes ver nuestro catálogo actualizado aquí: misitio.com/catalogo','Si me dices qué producto buscas, te ayudo a encontrarlo.'],replyMode:'all',audience:'all',delay:1,active:true},
  {id:4,name:'Fuera de la oficina',trigger:'*',match:'any',replies:['Ahora no estamos disponibles. Te responderemos en cuanto volvamos.'],replyMode:'one',audience:'all',delay:3,active:false}
];

const $ = (s, root=document) => root.querySelector(s);
const $$ = (s, root=document) => [...root.querySelectorAll(s)];
const storageKey = 'replyflow-rules-v1';
let rules = loadRules();
let editingId = null;
let sortNewest = true;
let toastTimer;

const els = {
  rulesList: $('#rulesList'), empty: $('#emptyState'), summary: $('#ruleSummary'), master: $('#masterToggle'),
  notice: $('#serviceNotice'), nav: $('#bottomNav'), back: $('#backBtn'), brand: $('#appBrand'), title: $('#pageTitle'),
  masterControl: $('#masterControl'), help: $('#helpBtn'), editor: $('#editorView'), form: $('#ruleForm'),
  replyFields: $('#replyFields'), deleteBtn: $('#deleteRuleBtn'), chat: $('#chatMessages')
};

function loadRules(){
  try { return JSON.parse(localStorage.getItem(storageKey)) || structuredClone(seedRules); }
  catch { return structuredClone(seedRules); }
}
function saveRules(){ localStorage.setItem(storageKey, JSON.stringify(rules)); }
function esc(value=''){ return String(value).replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
function matchName(type){ return ({exact:'Coincidencia exacta',contains:'Contiene',similar:'Similitud',pattern:'Patrón',any:'Cualquier mensaje'})[type] || 'Contiene'; }
function audienceName(type){ return ({all:'Todos',contacts:'Contactos',unknown:'Desconocidos'})[type] || 'Todos'; }
function showToast(message){ const el=$('#toast'); el.textContent=message; el.classList.add('show'); clearTimeout(toastTimer); toastTimer=setTimeout(()=>el.classList.remove('show'),2300); }

function renderRules(){
  const visible = [...rules].sort((a,b)=>sortNewest ? b.id-a.id : a.id-b.id);
  els.rulesList.innerHTML = visible.map((r,i)=>`
    <div class="rule-wrap" data-id="${r.id}">
      <button class="swipe-delete" data-action="delete">Eliminar</button>
      <article class="rule-card ${r.active?'':'off'}" tabindex="0" role="button" aria-label="Editar ${esc(r.name)}">
        <div class="rule-top">
          <span class="rule-num">${String(i+1).padStart(2,'0')}</span>
          <div class="rule-title"><b>${esc(r.name)}</b><small>${matchName(r.match)}</small></div>
          <label class="switch mini-switch" title="${r.active?'Pausar':'Activar'} regla"><input type="checkbox" data-action="toggle" ${r.active?'checked':''}><span></span></label>
        </div>
        <div class="rule-flow"><div><label>RECIBES</label><p>${r.match==='any'?'Cualquier mensaje':'“'+esc(r.trigger)+'”'}</p></div><span class="flow-arrow">›</span><div><label>RESPONDES</label><p>${esc(r.replies[0])}</p></div></div>
        <div class="rule-meta"><span>◉ ${audienceName(r.audience)}</span><span>◷ ${r.delay?safeDelay(r.delay):'Al instante'}</span><span class="state">${r.active?'● Activa':'● Pausada'}</span></div>
      </article>
    </div>`).join('');
  const active=rules.filter(r=>r.active).length;
  els.summary.textContent=`${active} ${active===1?'activa':'activas'} de ${rules.length} ${rules.length===1?'regla':'reglas'}`;
  els.empty.classList.toggle('hidden',rules.length>0); els.rulesList.classList.toggle('hidden',rules.length===0);
}
function safeDelay(n){return `${n} ${Number(n)===1?'segundo':'segundos'}`}

function showView(name){
  $$('.view').forEach(v=>v.classList.remove('active'));
  $(`#${name}View`).classList.add('active');
  const editor=name==='editor';
  $('#app').classList.toggle('editor-mode',editor);
  els.nav.classList.toggle('hidden',editor); els.back.classList.toggle('hidden',!editor);
  els.brand.classList.toggle('hidden',editor); els.title.classList.toggle('hidden',!editor);
  els.masterControl.classList.toggle('hidden',editor); els.help.classList.toggle('hidden',editor);
  if(!editor){
    $$('[data-view]',els.nav).forEach(b=>b.classList.toggle('active',b.dataset.view===name));
    els.title.textContent='';
  }
  if(name==='test') initChat();
  location.hash=name==='rules'?'':name;
}

function openEditor(rule=null){
  editingId=rule?.id || null; els.form.reset(); els.replyFields.innerHTML='';
  els.title.textContent=rule?'Editar regla':'Nueva regla'; els.deleteBtn.classList.toggle('hidden',!rule);
  $('#ruleName').value=rule?.name||''; $('#ruleTrigger').value=rule?.trigger||''; $('#ruleDelay').value=String(rule?.delay??3); $('#ruleActive').checked=rule?.active??true;
  const match=rule?.match||'contains', audience=rule?.audience||'all', mode=rule?.replyMode||'one';
  $(`input[name="match"][value="${match}"]`).checked=true; $(`input[name="audience"][value="${audience}"]`).checked=true; $(`input[name="replyMode"][value="${mode}"]`).checked=true;
  (rule?.replies||['']).forEach(addReplyField); updateReplyControls(); showView('editor'); setTimeout(()=>$('#ruleName').focus(),100);
}
function addReplyField(value=''){
  const item=document.createElement('label'); item.className='field reply-item';
  item.innerHTML=`<span>Respuesta ${els.replyFields.children.length+1}</span><textarea required maxlength="1000" placeholder="Escribe la respuesta automática…">${esc(value)}</textarea><button type="button" class="remove-reply" aria-label="Eliminar respuesta">×</button>`;
  item.querySelector('.remove-reply').addEventListener('click',()=>{item.remove();renumberReplies();updateReplyControls()});
  els.replyFields.append(item); updateReplyControls();
}
function renumberReplies(){ $$('.reply-item>span',els.replyFields).forEach((s,i)=>s.textContent=`Respuesta ${i+1}`); }
function updateReplyControls(){
  const count=els.replyFields.children.length;
  $$('.remove-reply',els.replyFields).forEach(b=>b.classList.toggle('hidden',count===1));
  $('#multipleOptions').classList.toggle('hidden',count<2);
}
function submitRule(e){
  e.preventDefault();
  const replies=$$('textarea',els.replyFields).map(t=>t.value.trim()).filter(Boolean);
  const data={
    id:editingId||Date.now(),name:$('#ruleName').value.trim(),trigger:$('#ruleTrigger').value.trim(),
    match:$('input[name="match"]:checked').value,replies,replyMode:$('input[name="replyMode"]:checked').value,
    audience:$('input[name="audience"]:checked').value,delay:Number($('#ruleDelay').value),active:$('#ruleActive').checked
  };
  if(data.match==='any') data.trigger='*';
  if(!data.name||!data.trigger||!replies.length){showToast('Completa todos los campos');return;}
  if(editingId) rules=rules.map(r=>r.id===editingId?data:r); else rules.push(data);
  saveRules();renderRules();showView('rules');showToast(editingId?'Cambios guardados':'Regla creada correctamente');
}
function deleteRule(id){
  const rule=rules.find(r=>r.id===id); if(!rule)return;
  if(confirm(`¿Eliminar “${rule.name}”?`)){rules=rules.filter(r=>r.id!==id);saveRules();renderRules();if(editingId===id)showView('rules');showToast('Regla eliminada');}
}

function doesMatch(rule,message){
  const msg=message.toLocaleLowerCase().trim(), trigger=rule.trigger.toLocaleLowerCase().trim();
  if(rule.match==='any'||trigger==='*')return true;
  if(rule.match==='exact')return msg===trigger;
  if(rule.match==='contains')return msg.includes(trigger.replaceAll('*',''));
  if(rule.match==='similar')return similarity(msg,trigger)>=.62||msg.includes(trigger);
  if(rule.match==='pattern'){
    const alternatives=trigger.split('|').map(x=>x.trim()).filter(Boolean);
    return alternatives.some(part=>{try{const pattern=part.split('*').map(escapeRegex).join('.*');return new RegExp(pattern,'i').test(message)}catch{return false}});
  }
  return false;
}
function escapeRegex(s){return s.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')}
function similarity(a,b){
  if(!a.length&&!b.length)return 1; const costs=Array(b.length+1).fill(0).map((_,i)=>i);
  for(let i=1;i<=a.length;i++){let prev=costs[0];costs[0]=i;for(let j=1;j<=b.length;j++){const hold=costs[j];costs[j]=Math.min(costs[j]+1,costs[j-1]+1,prev+(a[i-1]===b[j-1]?0:1));prev=hold}}
  return 1-costs[b.length]/Math.max(a.length,b.length);
}
function initChat(){
  if(els.chat.children.length)return;
  addBubble('bot','¡Hola! Soy tu simulador de ReplyFlow. Escribe un mensaje para comprobar qué regla responde.');
}
function addBubble(type,text,rule=''){
  const b=document.createElement('div');b.className=`bubble ${type}`;
  const time=new Date().toLocaleTimeString('es',{hour:'2-digit',minute:'2-digit'});
  b.innerHTML=`${esc(text).replaceAll('\n','<br>')}${rule?`<span class="matched">⚡ ${esc(rule)}</span>`:''}<small>${time}</small>`;els.chat.append(b);els.chat.scrollTop=els.chat.scrollHeight;
}
function runChat(message){
  addBubble('user',message);
  if(!els.master.checked){setTimeout(()=>addBubble('bot','El servicio está pausado. Actívalo para probar tus reglas.'),350);return;}
  const rule=rules.find(r=>r.active&&doesMatch(r,message));
  const typing=document.createElement('div');typing.className='bubble bot typing';typing.innerHTML='<i></i><i></i><i></i>';els.chat.append(typing);els.chat.scrollTop=els.chat.scrollHeight;
  setTimeout(()=>{
    typing.remove();
    if(!rule){addBubble('bot','Ninguna regla coincide con este mensaje.','Sin coincidencias');return;}
    let responses=rule.replies;
    if(rule.replyMode==='one')responses=[responses[0]];
    if(rule.replyMode==='random')responses=[responses[Math.floor(Math.random()*responses.length)]];
    responses.forEach((response,i)=>setTimeout(()=>addBubble('bot',response,rule.name),i*350));
  },Math.min(900,250+(rule?.delay||0)*100));
}

let ignoreCardClick=false;
els.rulesList.addEventListener('click',e=>{
  if(ignoreCardClick){ignoreCardClick=false;return;}
  const wrap=e.target.closest('.rule-wrap');if(!wrap)return;const id=Number(wrap.dataset.id);
  if(e.target.closest('[data-action="toggle"]')){e.stopPropagation();const rule=rules.find(r=>r.id===id);rule.active=e.target.checked;saveRules();renderRules();showToast(rule.active?'Regla activada':'Regla pausada');return;}
  if(e.target.closest('[data-action="delete"]')){deleteRule(id);return;}
  openEditor(rules.find(r=>r.id===id));
});
els.rulesList.addEventListener('keydown',e=>{if(e.key==='Enter'&&e.target.classList.contains('rule-card'))e.target.click()});
let startX=0,currentCard=null;
els.rulesList.addEventListener('pointerdown',e=>{currentCard=e.target.closest('.rule-card');startX=e.clientX});
els.rulesList.addEventListener('pointermove',e=>{if(!currentCard)return;const dx=Math.min(0,Math.max(-85,e.clientX-startX));if(Math.abs(dx)>8)currentCard.style.transform=`translateX(${dx}px)`});
els.rulesList.addEventListener('pointerup',e=>{if(!currentCard)return;const dx=e.clientX-startX;if(Math.abs(dx)>12)ignoreCardClick=true;currentCard.style.transform=dx<-45?'translateX(-85px)':'';currentCard=null});

$('#newRuleBtn').addEventListener('click',()=>openEditor());
document.addEventListener('click',e=>{if(e.target.closest('[data-action="new-rule"]'))openEditor()});
$('#addReplyBtn').addEventListener('click',()=>{addReplyField();els.replyFields.lastElementChild.querySelector('textarea').focus()});
$('#wildcardBtn').addEventListener('click',()=>{$('#ruleTrigger').value+='*';$('#ruleTrigger').focus()});
els.form.addEventListener('submit',submitRule);
els.deleteBtn.addEventListener('click',()=>deleteRule(editingId));
els.back.addEventListener('click',()=>showView('rules'));
$$('[data-view]',els.nav).forEach(b=>b.addEventListener('click',()=>showView(b.dataset.view)));
els.master.addEventListener('change',()=>{localStorage.setItem('replyflow-master',els.master.checked?'1':'0');$('.service-status b').textContent=els.master.checked?'Activo':'Pausado';$('.service-status i').style.background=els.master.checked?'#5cf397':'#f3a4a4';showToast(els.master.checked?'Autorespuestas activadas':'Autorespuestas pausadas')});
els.master.checked=localStorage.getItem('replyflow-master')!=='0';
$('#serviceNotice button').addEventListener('click',()=>els.notice.remove());
$('#sortBtn').addEventListener('click',e=>{sortNewest=!sortNewest;e.target.textContent=sortNewest?'Recientes ⇅':'Antiguas ⇅';renderRules()});
$('#chatForm').addEventListener('submit',e=>{e.preventDefault();const input=$('#chatInput'),value=input.value.trim();if(value){runChat(value);input.value=''}});
$('#clearChat').addEventListener('click',()=>{els.chat.innerHTML='';initChat()});
$('#helpBtn').addEventListener('click',()=>$('#helpSheet').classList.remove('hidden'));
$$('.sheet-close,.sheet-close-action').forEach(b=>b.addEventListener('click',()=>$('#helpSheet').classList.add('hidden')));
$('#helpSheet').addEventListener('click',e=>{if(e.target.id==='helpSheet')e.currentTarget.classList.add('hidden')});
$('#permissionBtn').addEventListener('click',e=>{e.target.textContent='Listo ✓';e.target.style.background='#dff6e9';showToast('Permiso simulado en este prototipo')});
$('#defaultDelay').addEventListener('click',()=>showToast('El retraso se configura en cada regla'));
$('#exportBtn').addEventListener('click',()=>{const blob=new Blob([JSON.stringify(rules,null,2)],{type:'application/json'}),a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='replyflow-reglas.json';a.click();URL.revokeObjectURL(a.href);showToast('Copia exportada')});
$('#resetBtn').addEventListener('click',()=>{if(confirm('¿Restaurar las reglas de demostración?')){rules=structuredClone(seedRules);saveRules();renderRules();showToast('Demostración restaurada')}});
window.addEventListener('hashchange',()=>{const name=location.hash.slice(1);if(['rules','test','settings'].includes(name))showView(name)});

renderRules();
const initial=location.hash.slice(1);showView(['test','settings'].includes(initial)?initial:'rules');
