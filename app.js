const $=(s,r=document)=>r.querySelector(s), $$=(s,r=document)=>[...r.querySelectorAll(s)];
const channels=[['sms','SMS'],['wa','WA'],['business','Business'],['messenger','Messenger'],['instagram','Instagram'],['telegram','Telegram'],['linkedin','LinkedIn'],['chat','Chat']];
const seedRules=[
 {id:1,name:'Ocupado',trigger:'*',match:'any',reply:'Lo siento, estoy ocupado ahora mismo. Te responderé lo antes posible.',channels:['sms','wa','business'],active:true,scheduled:true,start:'12:00',end:'23:02',allow:[],deny:[],sent:12},
 {id:2,name:'Horario comercial',trigger:'horario',match:'contains',reply:'Nuestro horario es de lunes a viernes, de 9:00 a 18:00.',channels:['wa','business','messenger'],active:true,scheduled:false,start:'09:00',end:'18:00',allow:[],deny:[],sent:7},
 {id:3,name:'Información y precios',trigger:'precio|información',match:'pattern',reply:'¡Claro! Cuéntame qué producto te interesa y te envío toda la información.',channels:['wa','business','instagram','telegram'],active:false,scheduled:false,start:'09:00',end:'18:00',allow:[],deny:['Grupo familia'],sent:5}
];
const key='replyflow-dark-mvp-v2';
let rules=load(), currentFilter='all', editingId=null, editingList='allow', editorReturnScreen='responses', permission=localStorage.getItem('replyflow-permission')==='1', toastTimer;
const screenNames={home:['Añadir / editar','ReplyFlow'],responses:['Respuestas','ReplyFlow'],reports:['Informes','Actividad'],help:['Ayuda','Centro de ayuda'],settings:['Ajustes','Preferencias']};

function load(){try{return JSON.parse(localStorage.getItem(key))||structuredClone(seedRules)}catch{return structuredClone(seedRules)}}
function persist(){localStorage.setItem(key,JSON.stringify(rules))}
function esc(v=''){return String(v).replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]))}
function toast(msg){const t=$('#toast');t.textContent=msg;t.classList.add('show');clearTimeout(toastTimer);toastTimer=setTimeout(()=>t.classList.remove('show'),2200)}
function matchLabel(v){return({exact:'Exacta',contains:'Contiene',pattern:'Patrón',any:'Cualquier mensaje'})[v]||'Contiene'}

function renderRules(){
 let list=rules.filter(r=>currentFilter==='all'||(currentFilter==='active'&&r.active)||(currentFilter==='scheduled'&&r.scheduled));
 $('#allCount').textContent=rules.length;
 $('#rulesList').innerHTML=list.map(r=>`<article class="rule-card ${r.active?'':'paused'}" data-id="${r.id}">
   <div class="rule-badge">${r.scheduled?'◷':'↩'}</div><div class="rule-copy"><h3>${esc(r.name)}</h3><p>${esc(r.reply)}</p><div class="rule-meta"><span>${matchLabel(r.match)}</span><span>${r.channels.length} canales</span>${r.scheduled?`<span>${r.start} – ${r.end}</span>`:''}</div></div>
   <div class="rule-side"><button class="rule-actions" data-edit aria-label="Editar">✎</button><label class="switch"><input data-toggle type="checkbox" ${r.active?'checked':''}><span></span></label></div></article>`).join('');
 $('#emptyState').hidden=list.length!==0; $('.fab').hidden=list.length===0&&currentFilter!=='all';
 $('#homeRulesCount').textContent=rules.length;
 $('#homeSentCount').textContent=rules.reduce((n,r)=>n+(r.sent||0),0);
 renderReports();
}
function renderReports(){
 const total=rules.reduce((n,r)=>n+(r.sent||0),0);$('#totalReplies').textContent=total;
 $('#reportList').innerHTML=rules.map((r,i)=>`<div class="report-row"><i style="background:${['#5865f2','#23a559','#f0b232','#9b84ee'][i%4]}"></i><div><b>${esc(r.name)}</b><small>${r.active?'Activa':'Pausada'} · ${r.channels.length} canales</small></div><strong>${r.sent||0}</strong></div>`).join('');
 const vals=[3,5,2,7,4,8,6],days=['L','M','M','J','V','S','D'];
 $('#reportChart').innerHTML=vals.map((v,i)=>`<div class="bar-col ${v===8?'peak':''}"><i style="height:${v*10}%"></i><span>${days[i]}</span></div>`).join('');
}
function renderChannels(selected=[]){$('#channelGrid').innerHTML=channels.map(([id,label])=>`<label class="channel"><input type="checkbox" value="${id}" ${selected.includes(id)?'checked':''}><span>${label.slice(0,2).toUpperCase()}</span><small>${label}</small></label>`).join('')}

function showScreen(name,push=true){
 $$('.screen').forEach(s=>s.classList.remove('active'));$(`#${name}Screen`).classList.add('active');
 const editor=name==='editor';$('#phone').classList.toggle('editor-mode',editor);$('#bottomNav').hidden=editor;$('#menuBtn').hidden=editor;$('#backBtn').hidden=!editor;$('#saveTop').hidden=!editor;$('#searchBtn').hidden=editor;$('#moreBtn').hidden=editor;
 if(editor){$('#screenTitle').textContent=editingId?'Editar respuesta':'Añadir respuesta';$('#screenSubtitle').textContent=editingId?'Modifica la regla':'Nueva automatización'}
 else{const [title,sub]=screenNames[name];$('#screenTitle').textContent=title;$('#screenSubtitle').textContent=sub;$$('#bottomNav [data-screen]').forEach(b=>b.classList.toggle('active',b.dataset.screen===name))}
 if(push)location.hash=name==='home'?'':name;
}
function openEditor(rule){
 const active=$('.screen.active');
 if(active&&!active.id.startsWith('editor')) editorReturnScreen=active.id.replace('Screen','');
 editingId=rule?.id||null;$('#ruleForm').reset();$('#ruleActive').checked=rule?.active??true;$('#ruleName').value=rule?.name||'';$('#ruleTrigger').value=rule?.trigger||'';$('#ruleReply').value=rule?.reply||'';$('#replyLength').textContent=(rule?.reply||'').length;
 const match=rule?.match||'contains';$(`input[name="match"][value="${match}"]`).checked=true;renderChannels(rule?.channels||['sms','wa','business']);
 $('#scheduleToggle').checked=rule?.scheduled??false;$('#scheduleFields').classList.toggle('disabled',!$('#scheduleToggle').checked);$('#startTime').value=rule?.start||'09:00';$('#endTime').value=rule?.end||'18:00';
 $('#allowCount').textContent=rule?.allow?.length?`${rule.allow.length} añadidos`:'Todos los contactos';$('#denyCount').textContent=rule?.deny?.length?`${rule.deny.length} añadidos`:'Ninguno';$('#deleteRule').hidden=!rule;showScreen('editor');setTimeout(()=>$('#ruleName').focus(),80);
}
function saveRule(e){
 e.preventDefault();const selected=$$('#channelGrid input:checked').map(i=>i.value);if(!selected.length){toast('Selecciona al menos un canal');return}
 const old=rules.find(r=>r.id===editingId),data={id:editingId||Date.now(),name:$('#ruleName').value.trim(),trigger:$('#ruleTrigger').value.trim(),match:$('input[name="match"]:checked').value,reply:$('#ruleReply').value.trim(),channels:selected,active:$('#ruleActive').checked,scheduled:$('#scheduleToggle').checked,start:$('#startTime').value,end:$('#endTime').value,allow:old?.allow||[],deny:old?.deny||[],sent:old?.sent||0};
 if(data.match==='any')data.trigger='*';if(!data.name||!data.trigger||!data.reply){toast('Completa nombre, activador y respuesta');return}
 rules=editingId?rules.map(r=>r.id===editingId?data:r):[...rules,data];persist();renderRules();showScreen('responses');toast(editingId?'Respuesta actualizada':'Respuesta creada');
}
function deleteRule(){const r=rules.find(x=>x.id===editingId);if(r&&confirm(`¿Eliminar “${r.name}”?`)){rules=rules.filter(x=>x.id!==editingId);persist();renderRules();showScreen('responses');toast('Respuesta eliminada')}}

function openModal(id){$(id).hidden=false}
function closeModals(){$$('.scrim').forEach(s=>s.hidden=true)}
function updatePermission(){
 $('#permissionStatus').textContent=permission?'Acceso concedido':'Pendiente de configurar';$('#permissionStatus').style.color=permission?'#23a559':'';$('#homePermissionStatus').textContent=permission?'Permiso concedido':'Configurar permiso';$('#homePermissionStatus').style.color=permission?'#23a559':'';$('#permissionBanner').hidden=permission;$('#permissionToggle').checked=permission;
}
const helpData={
 start:['Primeros pasos','1. Concede acceso a las notificaciones.<br>2. Pulsa el botón + para crear una respuesta.<br>3. Escribe el activador y el mensaje.<br>4. Elige los canales y guarda la regla.'],
 manual:['Manual de usuario','Las respuestas se evalúan de arriba hacia abajo. Puedes usar coincidencia exacta, palabras contenidas, patrones separados por | o el comodín * para cualquier mensaje.'],
 faq:['Preguntas frecuentes','<b>¿Funciona con la pantalla apagada?</b><br>La versión Android deberá ejecutar un servicio autorizado.<br><br><b>¿Los mensajes salen del teléfono?</b><br>Este prototipo guarda todo localmente.'],
 issues:['Solución de problemas','Comprueba que el servicio maestro esté activo, que la regla esté habilitada y que el canal tenga acciones de respuesta en sus notificaciones.']
};

$('#rulesList').addEventListener('click',e=>{const card=e.target.closest('.rule-card');if(!card)return;const rule=rules.find(r=>r.id===Number(card.dataset.id));if(e.target.matches('[data-toggle]')){rule.active=e.target.checked;persist();renderRules();toast(rule.active?'Respuesta activada':'Respuesta pausada')}else openEditor(rule)});
$$('[data-new-rule]').forEach(b=>b.addEventListener('click',()=>openEditor(null)));
$$('[data-screen]').forEach(b=>b.addEventListener('click',()=>{showScreen(b.dataset.screen);closeModals()}));
$$('.filter-tabs button').forEach(b=>b.addEventListener('click',()=>{currentFilter=b.dataset.filter;$$('.filter-tabs button').forEach(x=>x.classList.toggle('active',x===b));renderRules()}));
$('#ruleForm').addEventListener('submit',saveRule);$('#saveTop').addEventListener('click',()=>$('#ruleForm').requestSubmit());$('#cancelEdit').addEventListener('click',()=>showScreen(editorReturnScreen));$('#backBtn').addEventListener('click',()=>showScreen(editorReturnScreen));$('#deleteRule').addEventListener('click',deleteRule);
$('#ruleReply').addEventListener('input',e=>$('#replyLength').textContent=e.target.value.length);$('#wildcardBtn').addEventListener('click',()=>{$('#ruleTrigger').value+='*';$('#ruleTrigger').focus()});
$('#scheduleToggle').addEventListener('change',e=>$('#scheduleFields').classList.toggle('disabled',!e.target.checked));
$('#toggleChannels').addEventListener('click',()=>{const checks=$$('#channelGrid input'),all=checks.every(x=>x.checked);checks.forEach(x=>x.checked=!all);$('#toggleChannels').textContent=all?'Ninguno':'Todos'});
$$('[data-list]').forEach(b=>b.addEventListener('click',()=>{editingList=b.dataset.list;$('#listTitle').textContent=editingList==='allow'?'Lista permitida':'Lista de no responder';$('#listValue').value='';openModal('#listModal')}));
$('#addToList').addEventListener('click',()=>{const value=$('#listValue').value.trim();if(!value){toast('Escribe un nombre o número');return}const old=rules.find(r=>r.id===editingId);if(old){old[editingList]=[...(old[editingList]||[]),value];persist()}$(`#${editingList}Count`).textContent=old?`${old[editingList].length} añadidos`:'1 añadido';closeModals();toast('Añadido a la lista')});
$('#masterToggle').addEventListener('change',e=>{localStorage.setItem('replyflow-master',e.target.checked?'1':'0');$('#masterLabel').textContent=e.target.checked?'Respuestas activas':'Servicio pausado';$('#masterDescription').textContent=e.target.checked?'Escuchando mensajes entrantes':'No se enviarán respuestas';$('.status-orb i').style.background=e.target.checked?'#23a559':'#f23f42';toast(e.target.checked?'Servicio activado':'Servicio pausado')});
$('#masterToggle').checked=localStorage.getItem('replyflow-master')!=='0';
$('#menuBtn').addEventListener('click',()=>openModal('#drawer'));$('#drawer').addEventListener('click',e=>{if(e.target.id==='drawer')closeModals()});
$('#openPermission').addEventListener('click',()=>openModal('#permissionModal'));$('#homePermission').addEventListener('click',()=>openModal('#permissionModal'));$('#notificationSetting').addEventListener('click',()=>openModal('#permissionModal'));$('#permissionToggle').addEventListener('change',e=>{permission=e.target.checked;localStorage.setItem('replyflow-permission',permission?'1':'0');updatePermission();toast(permission?'Acceso simulado concedido':'Acceso desactivado')});
$('.permission-banner .dismiss').addEventListener('click',()=>$('#permissionBanner').hidden=true);
$$('[data-close]').forEach(b=>b.addEventListener('click',closeModals));$$('.scrim').forEach(s=>s.addEventListener('click',e=>{if(e.target===s)closeModals()}));
$$('[data-help]').forEach(b=>b.addEventListener('click',()=>{const [title,content]=helpData[b.dataset.help];$('#helpTitle').textContent=title;$('#helpContent').innerHTML=content;openModal('#helpModal')}));
$('#exportRules').addEventListener('click',()=>download('replyflow-reglas.json',JSON.stringify(rules,null,2)));$('#exportReport').addEventListener('click',()=>download('replyflow-informe.csv','Regla,Estado,Respuestas\n'+rules.map(r=>`"${r.name}",${r.active?'Activa':'Pausada'},${r.sent||0}`).join('\n')));
function download(name,content){const a=document.createElement('a');a.href=URL.createObjectURL(new Blob([content],{type:'text/plain'}));a.download=name;a.click();setTimeout(()=>URL.revokeObjectURL(a.href),100);toast('Archivo exportado')}
$('#restoreDemo').addEventListener('click',()=>{if(confirm('¿Restaurar todos los datos de demostración?')){rules=structuredClone(seedRules);persist();renderRules();toast('Datos restaurados')}});
$('#searchBtn').addEventListener('click',()=>{const q=prompt('Buscar respuesta');if(q!==null){currentFilter='all';const value=q.toLowerCase();$$('.rule-card').forEach(c=>c.hidden=!c.textContent.toLowerCase().includes(value));toast(value?'Resultados filtrados':'Mostrando todas')}});
$('#moreBtn').addEventListener('click',()=>toast('Usa Ajustes para importar o exportar tus reglas'));$('#editDefault').addEventListener('click',()=>openEditor(rules[0]));
window.addEventListener('hashchange',()=>{const n=location.hash.slice(1);if(screenNames[n])showScreen(n,false)});

renderChannels();renderRules();updatePermission();
const initial=location.hash.slice(1);showScreen(screenNames[initial]?initial:'home',false);
