package com.replyflow.app.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.replyflow.app.data.MatchType
import com.replyflow.app.data.Rule
import com.replyflow.app.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalTime

val LocalOpenDrawer = staticCompositionLocalOf<() -> Unit> { {} }

@Composable private fun Header(title: String, subtitle: String = "ReplyFlow", back: (() -> Unit)? = null) {
    val openDrawer = LocalOpenDrawer.current
    Surface(color = Surface, shadowElevation = 3.dp) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().height(68.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back ?: openDrawer, modifier = Modifier.size(48.dp)) {
                Icon(if (back == null) Icons.Default.Menu else Icons.Default.ArrowBack, contentDescription = if (back == null) "Abrir menú" else "Volver")
            }
            Column(Modifier.weight(1f).padding(start = 4.dp)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(subtitle, color = Muted, fontSize = 15.sp)
            }
            IconButton(onClick = {}, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.MoreVert, contentDescription = "Más opciones") }
        }
    }
}

@Composable private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick, modifier.heightIn(min = 52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Blurple)) { Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
}

@Composable fun HomeScreen(vm: HomeViewModel, onCreate:()->Unit, onRules:()->Unit, onReports:()->Unit, onHelp:()->Unit, onPermission:()->Unit) {
    val state by vm.state.collectAsState()
    val context=LocalContext.current
    val granted=NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    Column(Modifier.fillMaxSize().background(Bg)) {
        Header("Añadir / editar")
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().background(SurfaceHigh).padding(vertical=44.dp,horizontal=24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                    Box(Modifier.size(84.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF3F4270)), contentAlignment=Alignment.Center){ Icon(Icons.Default.Add,contentDescription=null,tint=Color(0xFFC9CDFB),modifier=Modifier.size(52.dp)) }
                    Text("Añadir / editar",fontWeight=FontWeight.ExtraBold,fontSize=24.sp)
                    Text("Crea una respuesta y elige en qué canales funcionará.",color=Muted,fontSize=14.sp,modifier=Modifier.padding(top=7.dp,bottom=18.dp))
                    PrimaryButton("Crear respuesta",onCreate)
                }
            }
            item {
                Section(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){ Overline("RESUMEN");Text("Tu automatización",fontWeight=FontWeight.Bold)};Badge("● En línea",Green)}
                    HorizontalDivider(Modifier.padding(vertical=12.dp),color=Line)
                    Row { Metric(state.rules,"Respuestas",Modifier.weight(1f).clickable(onClick=onRules)); Metric(state.sent,"Enviadas",Modifier.weight(1f).clickable(onClick=onReports)) }
                }
            }
            item { Shortcut("?","¿Necesitas ayuda?","Consulta la guía de configuración",onHelp); Shortcut("♢","Acceso a notificaciones",if(granted)"Permiso concedido" else "Configurar permiso",onPermission) }
        }
    }
}

@Composable private fun Metric(value:Int,label:String,modifier:Modifier){Column(modifier.padding(8.dp)){Text("$value",color=Color(0xFFC9CDFB),fontSize=24.sp,fontWeight=FontWeight.ExtraBold);Text(label,color=Muted,fontSize=13.sp)}}
@Composable private fun Shortcut(glyph:String,title:String,sub:String,onClick:()->Unit){Row(Modifier.padding(horizontal=14.dp,vertical=4.dp).fillMaxWidth().clip(RoundedCornerShape(9.dp)).background(Surface).clickable(onClick=onClick).padding(13.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text(glyph,color=Color(0xFFC9CDFB))};Column(Modifier.padding(start=11.dp).weight(1f)){Text(title,fontWeight=FontWeight.Bold,fontSize=15.sp);Text(sub,color=Muted,fontSize=15.sp)};Text("›",color=Muted)}}
@Composable private fun Section(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){Column(modifier.clip(RoundedCornerShape(10.dp)).background(Surface).padding(14.dp),content=content)}
@Composable private fun Overline(text:String){Text(text,color=Muted,fontSize=11.sp,letterSpacing=1.sp,fontWeight=FontWeight.Bold)}
@Composable private fun Badge(text:String,color:Color){Text(text,color=color,fontSize=11.sp,modifier=Modifier.clip(RoundedCornerShape(20.dp)).background(color.copy(.15f)).padding(horizontal=8.dp,vertical=5.dp))}

private enum class RuleFilter { ALL, ACTIVE, SCHEDULED }
@Composable fun RulesScreen(vm:RulesViewModel,onCreate:()->Unit,onEdit:(Long)->Unit,onPermission:()->Unit){
    val rules by vm.rules.collectAsState();val settings by vm.settings.collectAsState();var filter by remember{mutableStateOf(RuleFilter.ALL)}
    val context=LocalContext.current;val granted=NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    val shown=rules.filter{filter==RuleFilter.ALL||(filter==RuleFilter.ACTIVE&&it.active)||(filter==RuleFilter.SCHEDULED&&it.scheduled)}
    Box(Modifier.fillMaxSize().background(Bg)){
        Column{Header("Respuestas");LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=90.dp)){
            item { Section(Modifier.padding(13.dp)){Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text("●",color=if(settings.serviceEnabled)Green else Red)};Column(Modifier.padding(start=10.dp).weight(1f)){Overline("ESTADO DEL SERVICIO");Text(if(settings.serviceEnabled)"Respuestas activas" else "Servicio pausado",fontWeight=FontWeight.Bold);Text(if(settings.serviceEnabled)"Escuchando mensajes entrantes" else "No se enviarán respuestas",color=Muted,fontSize=15.sp)};Switch(settings.serviceEnabled, vm::serviceEnabled)}}}
            if(!granted)item{Row(Modifier.padding(horizontal=13.dp,vertical=4.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF3B3629)).clickable(onClick=onPermission).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text("!",color=Amber,fontWeight=FontWeight.Bold);Column(Modifier.padding(start=10.dp).weight(1f)){Text("Activa el acceso a notificaciones",fontSize=14.sp,fontWeight=FontWeight.Bold);Text("Necesario para responder desde Android",fontSize=14.sp,color=Muted)};Text("Configurar",color=Amber,fontSize=15.sp)}}
            item{Row(Modifier.padding(13.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).padding(4.dp)){RuleFilter.entries.forEach{f->FilterChip(selected=filter==f,onClick={filter=f},label={Text(when(f){RuleFilter.ALL->"Todas ${rules.size}";RuleFilter.ACTIVE->"Activas";RuleFilter.SCHEDULED->"Programadas"},fontSize=15.sp)},modifier=Modifier.weight(1f))}}}
            rules.firstOrNull()?.let{defaultRule->item{Row(Modifier.padding(horizontal=13.dp,vertical=3.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).padding(11.dp),verticalAlignment=Alignment.CenterVertically){Text("☝",fontSize=18.sp);Column(Modifier.padding(horizontal=10.dp).weight(1f)){Overline("PREDETERMINADA");Text("Perfil ocupado",fontWeight=FontWeight.Bold,fontSize=14.sp)};IconButton({onEdit(defaultRule.id)}){Icon(Icons.Default.Edit,contentDescription="Editar perfil")};Switch(defaultRule.active,{vm.setActive(defaultRule.id,it)})}}}
            items(shown,key={it.id}){rule->RuleRow(rule,{vm.setActive(rule.id,it)},{onEdit(rule.id)})}
        }}
        FloatingActionButton(onClick=onCreate,containerColor=Blurple,contentColor=Color.White,modifier=Modifier.align(Alignment.BottomEnd).padding(20.dp)){Icon(Icons.Default.Add,contentDescription="Crear respuesta")}
    }
}
@Composable private fun RuleRow(rule:Rule,onToggle:(Boolean)->Unit,onEdit:()->Unit){Row(Modifier.padding(horizontal=13.dp,vertical=4.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).clickable(onClick=onEdit).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(39.dp).clip(CircleShape).background(if(rule.active)Color(0xFF3F4270) else Color(0xFF493237)),contentAlignment=Alignment.Center){Text(if(rule.aiEnabled)"✦" else if(rule.scheduled)"◷" else "↩",color=if(rule.active)Color(0xFFC9CDFB) else Red)};Column(Modifier.padding(horizontal=10.dp).weight(1f)){Text(rule.name,fontWeight=FontWeight.Bold,fontSize=15.sp);Text(rule.reply,maxLines=2,overflow=TextOverflow.Ellipsis,color=Muted,fontSize=13.sp,lineHeight=18.sp);Row(Modifier.padding(top=6.dp)){Badge(rule.match.name.lowercase().replaceFirstChar{it.uppercase()},Muted);Spacer(Modifier.width(5.dp));Badge("${rule.channels.size} canales",Muted);if(rule.aiEnabled){Spacer(Modifier.width(5.dp));Badge("✦ IA",Violet)}}};IconButton(onClick=onEdit){Icon(Icons.Default.Edit,contentDescription="Editar")};Switch(rule.active,onToggle)} }

@Composable fun RuleEditorScreen(vm:EditorViewModel,id:Long,onBack:()->Unit,onAi:()->Unit){
    val state by vm.state.collectAsState();val ai by vm.aiConfig.collectAsState();var listTarget by remember{mutableStateOf<String?>(null)}
    LaunchedEffect(id){vm.load(id)}
    Column(Modifier.fillMaxSize().background(Bg)){
        Header(if(id<0)"Añadir respuesta" else "Editar respuesta",back=onBack)
        LazyColumn(Modifier.weight(1f),contentPadding=PaddingValues(13.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            item{Section{Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text(if(state.active)"✓" else "×")};Column(Modifier.padding(start=10.dp).weight(1f)){Text("Respuesta activa",fontWeight=FontWeight.Bold);Text("Se aplicará en los canales elegidos",fontSize=15.sp,color=Muted)};Switch(state.active,{vm.update{copy(active=it)}})}}}
            item{DiscordField("Nombre",state.name,{vm.update{copy(name=it)}},"Ej. Ocupado")}
            item{Row(verticalAlignment=Alignment.Bottom){DiscordField("Cuando el mensaje contenga",state.trigger,{vm.update{copy(trigger=it)}},"hola, precio o *",Modifier.weight(1f));Spacer(Modifier.width(7.dp));FilledTonalButton(onClick={vm.update{copy(trigger=trigger+"*")}},shape=RoundedCornerShape(5.dp)){Text("*")}}}
            item{Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){MatchType.entries.forEach{type->FilterChip(selected=state.match==type,onClick={vm.update{copy(match=type)}},label={Text(when(type){MatchType.EXACT->"Exacto";MatchType.CONTAINS->"Contiene";MatchType.PATTERN->"Patrón";MatchType.ANY->"Cualquiera"},fontSize=14.sp)},modifier=Modifier.weight(1f))}}}
            item{DiscordField("Mensaje de respuesta (${state.reply.length}/1000)",state.reply,{if(it.length<=1000)vm.update{copy(reply=it)}},"Escribe la respuesta de respaldo",singleLine=false)}
            item{Column{Overline("INTELIGENCIA ARTIFICIAL");Row(Modifier.padding(top=6.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(if(state.aiEnabled)Color(0xFF36385C) else Surface).clickable(onClick=onAi).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text("✦",color=Color(0xFFC9CDFB))};Column(Modifier.padding(horizontal=10.dp).weight(1f)){Text("Configurar IA",fontWeight=FontWeight.Bold,fontSize=15.sp);Text(ai?.let{"${it.platform} · ${it.model}"}?:"Selecciona proveedor y modelo",color=Muted,fontSize=14.sp)};Text("›",color=Muted)};Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Responder usando IA",fontSize=13.sp,modifier=Modifier.weight(1f));Switch(state.aiEnabled,{vm.update{copy(aiEnabled=it)}})}}}
            item{Column{Overline("CANALES");ChannelGrid(state.channels){ch,checked->vm.update{copy(channels=if(checked)channels+ch else channels-ch)}}}}
            item{Column{Overline("DESTINATARIOS");ListButton("✓","Lista permitida",if(state.allow.isEmpty())"Todos los contactos" else "${state.allow.size} añadidos"){listTarget="allow"};ListButton("⊘","Lista de no responder",if(state.deny.isEmpty())"Ninguno" else "${state.deny.size} añadidos"){listTarget="deny"}}}
            item{Column{Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Overline("HORARIO");Text("Disponibilidad",fontWeight=FontWeight.Bold)};Switch(state.scheduled,{vm.update{copy(scheduled=it)}})};if(state.scheduled)Row(Modifier.padding(top=8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){TimeButton("Desde",state.start){vm.update{copy(start=it)}};TimeButton("Hasta",state.end){vm.update{copy(end=it)}}}}}
        }
        Row(Modifier.fillMaxWidth().background(Surface).navigationBarsPadding().padding(10.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){if(id>=0)OutlinedButton({vm.delete(onBack)},colors=ButtonDefaults.outlinedButtonColors(contentColor=Red)){Text("Eliminar")};OutlinedButton(onBack,Modifier.weight(.32f)){Text("Cancelar")};PrimaryButton("✓ Guardar",{vm.save(onBack)},Modifier.weight(.68f))}
    }
    listTarget?.let{target->TextEntryDialog(if(target=="allow")"Añadir a permitidos" else "No responder a",onDismiss={listTarget=null}){value->vm.update{if(target=="allow")copy(allow=allow+value)else copy(deny=deny+value)};listTarget=null}}
}

@Composable private fun DiscordField(label:String,value:String,onChange:(String)->Unit,placeholder:String,modifier:Modifier=Modifier,singleLine:Boolean=true){Column(modifier){Text(label,color=Muted,fontSize=15.sp,modifier=Modifier.padding(start=2.dp,bottom=5.dp));OutlinedTextField(value,onChange,modifier=Modifier.fillMaxWidth(),placeholder={Text(placeholder,fontSize=14.sp)},singleLine=singleLine,minLines=if(singleLine)1 else 4,shape=RoundedCornerShape(5.dp),colors=OutlinedTextFieldDefaults.colors(focusedContainerColor=Nav,unfocusedContainerColor=Nav))}}
private val channelItems=listOf("sms" to "SMS","wa" to "WA","business" to "Business","messenger" to "Messenger","instagram" to "Instagram","telegram" to "Telegram","linkedin" to "LinkedIn","chat" to "Chat")
@Composable private fun ChannelGrid(selected:Set<String>,onChange:(String,Boolean)->Unit){Column(Modifier.padding(top=8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){channelItems.chunked(4).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){row.forEach{(id,label)->Column(Modifier.width(72.dp).clickable{onChange(id,id !in selected)},horizontalAlignment=Alignment.CenterHorizontally){Box(Modifier.size(42.dp).clip(CircleShape).background(if(id in selected)Blurple else SurfaceHigh),contentAlignment=Alignment.Center){Text(label.take(2).uppercase(),fontSize=13.sp,fontWeight=FontWeight.Bold)};Text(label,color=Muted,fontSize=14.sp,maxLines=1)}}}}}}
@Composable private fun ListButton(icon:String,title:String,sub:String,onClick:()->Unit){Row(Modifier.fillMaxWidth().clickable(onClick=onClick).padding(vertical=11.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(32.dp).clip(CircleShape).background(SurfaceHigh),contentAlignment=Alignment.Center){Text(icon,color=Color(0xFFC9CDFB))};Column(Modifier.padding(start=10.dp).weight(1f)){Text(title,fontSize=14.sp,fontWeight=FontWeight.Bold);Text(sub,fontSize=14.sp,color=Muted)};Text("✎",color=Muted)}}
@Composable private fun TimeButton(label:String,value:String,onValue:(String)->Unit){val context=LocalContext.current;val parsed=runCatching{LocalTime.parse(value)}.getOrDefault(LocalTime.NOON);OutlinedButton(onClick={TimePickerDialog(context,{_,h,m->onValue("%02d:%02d".format(h,m))},parsed.hour,parsed.minute,true).show()},modifier=Modifier.width(145.dp)){Column{Text(label,fontSize=14.sp,color=Muted);Text(value)}}}
@Composable private fun TextEntryDialog(title:String,onDismiss:()->Unit,onAdd:(String)->Unit){var value by remember{mutableStateOf("")};AlertDialog(onDismissRequest=onDismiss,title={Text(title)},text={OutlinedTextField(value,{value=it},label={Text("Nombre o número")})},confirmButton={TextButton({if(value.isNotBlank())onAdd(value.trim())}){Text("Añadir")}},dismissButton={TextButton(onDismiss){Text("Cancelar")}})}

@Composable fun AiWizardScreen(vm:AiViewModel,onBack:()->Unit,onFinished:()->Unit){
    val state by vm.state.collectAsState();val saved by vm.saved.collectAsState();var editing by remember{mutableStateOf(saved==null)}
    LaunchedEffect(Unit){vm.load()}
    if(!editing&&saved!=null){Column(Modifier.fillMaxSize().background(Bg)){Header("Configurar IA",back=onBack);Column(Modifier.fillMaxSize().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Box(Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(Blurple),contentAlignment=Alignment.Center){Text("✦",fontSize=32.sp)};Badge("● CONFIGURADA",Green);Text("Tu asistente está listo",fontSize=23.sp,fontWeight=FontWeight.ExtraBold,modifier=Modifier.padding(top=14.dp));Text("ReplyFlow puede usar este modelo para generar respuestas.",color=Muted,fontSize=13.sp,modifier=Modifier.padding(8.dp));Section(Modifier.fillMaxWidth().padding(vertical=16.dp)){SummaryLine("Proveedor",saved!!.platform);SummaryLine("Modelo",saved!!.model);SummaryLine("Clave","••••••••••••")};PrimaryButton("Editar configuración",{editing=true},Modifier.fillMaxWidth());TextButton({vm.clear();editing=true}){Text("Eliminar configuración",color=Red)}}};return}
    Column(Modifier.fillMaxSize().background(Bg)){Header("Configurar IA","Asistente automático",{if(vm.back())onBack()});Text("Paso ${state.step} de 3",fontSize=15.sp,color=Muted,modifier=Modifier.padding(start=20.dp,top=15.dp));StepBar(state.step);Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)){when(state.step){
        1->AiBasics(state,vm)
        2->AiKey(state,vm)
        else->AiModel(state,vm)
    }};Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(18.dp)){PrimaryButton(if(state.step==3)"Finalizar" else "Siguiente",{if(vm.next())editing=false},Modifier.fillMaxWidth())}}
}
@Composable private fun StepBar(step:Int){Row(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=8.dp)){listOf("Datos básicos","Clave API","Modelo").forEachIndexed{i,label->Column(Modifier.weight(1f)){Box(Modifier.fillMaxWidth().height(3.dp).background(if(i<step)Blurple else Line));Text((if(i<step-1)"✓ " else "")+label,color=if(i<step)Color(0xFFC9CDFB)else Muted,fontSize=14.sp,modifier=Modifier.padding(top=7.dp))}}}}
@Composable private fun AiBasics(s:AiWizardState,vm:AiViewModel){Text("Datos básicos",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Asigna un nombre a la plataforma y verifica la URL de su API.",color=Muted,fontSize=13.sp,modifier=Modifier.padding(top=7.dp,bottom=20.dp));Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("openrouter" to "OpenRouter","google" to "Google Gemini","custom" to "Personalizada").forEach{(id,label)->FilterChip(s.provider==id,{vm.provider(id)},{Text(label,fontSize=14.sp)})}};DiscordField("Nombre de la plataforma",s.platform,{vm.update{copy(platform=it)}},"OpenRouter",Modifier.padding(top=15.dp));DiscordField("URL de la API",s.url,{vm.update{copy(url=it)}},"https://api.ejemplo.com/v1/",Modifier.padding(top=15.dp))}
@Composable private fun AiKey(s:AiWizardState,vm:AiViewModel){Text("Clave API",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Se guardará cifrada únicamente en este dispositivo.",color=Muted,fontSize=13.sp,modifier=Modifier.padding(top=7.dp,bottom=20.dp));Text("Clave API",color=Muted,fontSize=15.sp);OutlinedTextField(s.apiKey,{vm.update{copy(apiKey=it)}},Modifier.fillMaxWidth(),visualTransformation=if(s.showKey)VisualTransformation.None else PasswordVisualTransformation(),trailingIcon={Text(if(s.showKey)"⊘" else "◉",Modifier.clickable{vm.update{copy(showKey=!showKey)}})},singleLine=true,colors=OutlinedTextFieldDefaults.colors(focusedContainerColor=Nav,unfocusedContainerColor=Nav));HorizontalDivider(Modifier.padding(vertical=20.dp),color=Line);Text("¿Necesitas ayuda?",fontWeight=FontWeight.Bold,fontSize=14.sp);Text(if(s.provider=="google")"aistudio.google.com/app/apikey" else "openrouter.ai/keys",color=Color(0xFFC9CDFB),fontSize=13.sp)}
@Composable private fun AiModel(s:AiWizardState,vm:AiViewModel){Text("Modelo",fontSize=22.sp,fontWeight=FontWeight.Bold);Text("Introduce el identificador exacto del modelo.",color=Muted,fontSize=13.sp,modifier=Modifier.padding(top=7.dp,bottom=20.dp));DiscordField("Modelo",s.model,{vm.update{copy(model=it)}},"openai/gpt-4.1-mini");Text("Ejemplos: gpt-4.1-mini · gemini-2.5-flash · claude-3.5-haiku",color=Muted,fontSize=14.sp,modifier=Modifier.padding(vertical=12.dp));DiscordField("Instrucción del asistente",s.prompt,{vm.update{copy(prompt=it)}},"Responde de forma amable",singleLine=false)}
@Composable private fun SummaryLine(label:String,value:String){Text(label,color=Muted,fontSize=14.sp);Text(value,fontWeight=FontWeight.Bold,fontSize=14.sp);HorizontalDivider(Modifier.padding(vertical=9.dp),color=Line)}

@Composable fun ReportsScreen(vm:ReportsViewModel){
    val rules by vm.rules.collectAsState();val total by vm.total.collectAsState();val context=LocalContext.current;var range by remember{mutableIntStateOf(7)}
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){uri->uri?.let{context.contentResolver.openOutputStream(it)?.use{out->out.write(("Regla,Estado,Respuestas\n"+rules.joinToString("\n"){r->"\"${r.name}\",${if(r.active)"Activa" else "Pausada"},${r.sent}"}).toByteArray())}}}
    Column(Modifier.fillMaxSize().background(Bg)){Header("Informes","Actividad");LazyColumn(contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
        item{Overline("ACTIVIDAD");Text("Informes",fontSize=26.sp,fontWeight=FontWeight.ExtraBold);Text("Consulta qué reglas están trabajando por ti.",color=Muted,fontSize=13.sp)}
        item{Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf(1 to "Hoy",7 to "7 días",30 to "30 días").forEach{(days,label)->FilterChip(selected=range==days,onClick={range=days},label={Text(label,fontSize=15.sp)})}}}
        item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){StatCard("↗",total,"Respuestas enviadas",Modifier.weight(1f));StatCard("◷",(total*3/60),"Minutos ahorrados",Modifier.weight(1f))}}
        item{Section{Text("Actividad semanal",fontWeight=FontWeight.Bold);Text("Respuestas por día",color=Muted,fontSize=14.sp);WeeklyChart(listOf(3,5,2,7,4,8,6))}}
        item{Overline("RENDIMIENTO POR REGLA")}
        items(rules){r->Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(9.dp).clip(CircleShape).background(if(r.active)Blurple else Red));Column(Modifier.padding(start=10.dp).weight(1f)){Text(r.name,fontWeight=FontWeight.Bold,fontSize=14.sp);Text(if(r.active)"Activa" else "Pausada",color=Muted,fontSize=14.sp)};Text("${r.sent}",fontWeight=FontWeight.Bold)}}
        item{PrimaryButton("⇅ Exportar informe",{export.launch("replyflow-informe.csv")},Modifier.fillMaxWidth())}
    }}
}
@Composable private fun StatCard(icon:String,value:Int,label:String,modifier:Modifier){Section(modifier){Text(icon,color=Color(0xFFC9CDFB));Text("$value",fontSize=23.sp,fontWeight=FontWeight.ExtraBold,modifier=Modifier.padding(top=8.dp));Text(label,color=Muted,fontSize=14.sp)}}
@Composable private fun WeeklyChart(values:List<Int>){val max=(values.maxOrNull()?:1).toFloat();Canvas(Modifier.fillMaxWidth().height(130.dp).padding(top=18.dp)){val slot=size.width/values.size;values.forEachIndexed{i,v->val h=size.height*(v/max);drawLine(color=if(v==values.maxOrNull())Blurple else Color(0xFF4E5058),start=androidx.compose.ui.geometry.Offset(slot*i+slot/2,size.height),end=androidx.compose.ui.geometry.Offset(slot*i+slot/2,size.height-h),strokeWidth=slot*.45f,cap=StrokeCap.Round)}}}

@Composable fun HelpScreen(onBack:(()->Unit)?=null){var topic by remember{mutableStateOf<Pair<String,String>?>(null)};val topics=listOf(
    Triple("◇","Primeros pasos","Concede acceso a notificaciones, crea una respuesta, selecciona los canales y activa la regla."),
    Triple("▤","Manual de usuario","Las reglas se evalúan de arriba hacia abajo. La primera coincidencia válida responde."),
    Triple("?","Preguntas frecuentes","ReplyFlow procesa las notificaciones en el dispositivo. Solo la IA utiliza la red."),
    Triple("⌁","Solución de problemas","Comprueba el permiso de notificaciones, el canal, horario y que exista una acción Responder.")
);Column(Modifier.fillMaxSize().background(Bg)){Header("Centro de ayuda",back=onBack);Column(Modifier.padding(16.dp)){Overline("RECURSOS");Text("¿Cómo podemos ayudarte?",fontSize=23.sp,fontWeight=FontWeight.ExtraBold,modifier=Modifier.padding(vertical=8.dp));topics.forEach{(icon,title,text)->Row(Modifier.padding(vertical=4.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).clickable{topic=title to text}.padding(13.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(35.dp).clip(RoundedCornerShape(9.dp)).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text(icon)};Column(Modifier.padding(start=10.dp).weight(1f)){Text(title,fontWeight=FontWeight.Bold,fontSize=14.sp);Text("Abrir artículo",color=Muted,fontSize=14.sp)};Text("›")}}}}
    topic?.let{(title,text)->AlertDialog(onDismissRequest={topic=null},title={Text(title)},text={Text(text)},confirmButton={TextButton({topic=null}){Text("Entendido")}})}
}

@Composable fun SettingsScreen(vm:SettingsViewModel,onPermission:()->Unit){
    val settings by vm.settings.collectAsState();val context=LocalContext.current;val granted=NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->uri?.let{context.contentResolver.openOutputStream(it)?.use{out->out.write(vm.exportJson().toByteArray())}}}
    val importRules=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->uri?.let{context.contentResolver.openInputStream(it)?.bufferedReader()?.use{reader->vm.importJson(reader.readText())}}}
    val notificationPermission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){}
    Column(Modifier.fillMaxSize().background(Bg)){Header("Ajustes","Preferencias");LazyColumn(contentPadding=PaddingValues(14.dp),verticalArrangement=Arrangement.spacedBy(15.dp)){
        item{Overline("PERMISOS");SettingRow("♢","Acceso a notificaciones",if(granted)"Acceso concedido" else "Pendiente de configurar",onPermission)}
        item{Column{Overline("COMPORTAMIENTO");SettingSwitch("↩","Servicio de respuestas","Motor principal",settings.serviceEnabled,vm::serviceEnabled);SettingRow("◷","Retraso de respuesta",if(settings.replyDelayMs==0L)"Sin retraso" else "${settings.replyDelayMs/1000} segundos",onClick={vm.delay(if(settings.replyDelayMs>=5_000)0 else settings.replyDelayMs+1_000)});SettingSwitch("↻","Evitar duplicados","Ventana de 5 minutos",settings.avoidDuplicates,vm::duplicates);SettingSwitch("☾","Tema oscuro","Estética Discord-like",settings.themeDark,vm::themeDark);SettingSwitch("◉","Modo estricto","Notificación persistente opcional",settings.strictMode){value->if(value&&Build.VERSION.SDK_INT>=33)notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);vm.strict(value)}}}
        item{Column{Overline("DATOS");SettingRow("⇧","Exportar reglas","Guardar rules.json",onClick={export.launch("rules.json")});SettingRow("⇩","Importar reglas","Restaurar desde un archivo",onClick={importRules.launch(arrayOf("application/json","text/plain"))});SettingRow("↺","Restaurar demostración","Reponer reglas iniciales",{vm.restore()},Red)}}
        item{Text("ReplyFlow 0.1.0\nTodo se guarda en el dispositivo. ReplyFlow no está afiliada a WhatsApp, Telegram, Meta ni otras plataformas.",color=Muted,fontSize=14.sp,lineHeight=13.sp,modifier=Modifier.padding(10.dp))}
    }}
}
@Composable private fun SettingRow(icon:String,title:String,sub:String,onClick:()->Unit,color:Color=Color(0xFFC9CDFB)){Row(Modifier.padding(top=7.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).clickable(onClick=onClick).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(35.dp).clip(RoundedCornerShape(9.dp)).background(Color(0xFF3F4270)),contentAlignment=Alignment.Center){Text(icon,color=color)};Column(Modifier.padding(start=10.dp).weight(1f)){Text(title,fontWeight=FontWeight.Bold,fontSize=14.sp);Text(sub,color=Muted,fontSize=14.sp)};Text("›",color=Muted)}}
@Composable private fun SettingSwitch(icon:String,title:String,sub:String,value:Boolean,onChange:(Boolean)->Unit){Row(Modifier.padding(top=7.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Surface).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text(icon,color=Color(0xFFC9CDFB));Column(Modifier.padding(start=12.dp).weight(1f)){Text(title,fontWeight=FontWeight.Bold,fontSize=14.sp);Text(sub,color=Muted,fontSize=14.sp)};Switch(value,onChange)}}
