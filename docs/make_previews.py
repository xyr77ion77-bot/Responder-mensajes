from html import escape
from pathlib import Path

D=Path(__file__).parent

def svg_start(w,h):
 return f'''<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}"><defs><filter id="sh"><feDropShadow dx="0" dy="3" stdDeviation="7" flood-opacity=".08"/></filter></defs><rect width="100%" height="100%" fill="#f5f7f5"/>'''
def rect(x,y,w,h,fill='#fff',r=12,stroke='none'):
 return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{fill}" stroke="{stroke}"/>'
def text(x,y,s,size=14,fill='#17211d',weight=400,anchor='start'):
 return f'<text x="{x}" y="{y}" font-family="Arial, sans-serif" font-size="{size}" font-weight="{weight}" fill="{fill}" text-anchor="{anchor}">{escape(s)}</text>'
def icon_circle(x,y,bg,sym,fg='#168d65',sz=18):
 return rect(x,y,38,38,bg,10)+text(x+19,y+25,sym,sz,fg,700,'middle')
def side(active='Resumen'):
 s=rect(0,0,245,900,'#fff',0,'#e6ebe7')
 s+=rect(24,25,34,34,'#173f31',11)+text(41,49,'↯',20,'#d7ff5a',700,'middle')+text(69,49,'Reply',21,'#17211d',700)+text(118,49,'Flow',21,'#19a973',700)
 s+=text(28,103,'ESPACIO DE TRABAJO',10,'#a0aaa5',700)
 for i,(name,ico) in enumerate([('Resumen','▦'),('Mis reglas','⚡'),('Actividad','◴'),('Contactos','♙')]):
  y=120+i*48
  if name==active:s+=rect(18,y,209,42,'#eaf7f1',10)
  col='#168d65' if name==active else '#69736e'
  s+=text(34,y+27,ico,17,col,700)+text(65,y+27,name,13,col,600)
  if name=='Mis reglas':s+=rect(195,y+11,22,20,'#d7eee4',10)+text(206,y+25,'4',10,'#188762',700,'middle')
 s+=text(28,342,'PREFERENCIAS',10,'#a0aaa5',700)+text(34,389,'⚙',17,'#69736e')+text(65,389,'Configuración',13,'#69736e',600)+text(34,437,'?',17,'#69736e',700)+text(65,437,'Centro de ayuda',13,'#69736e',600)
 s+=rect(18,716,209,112,'#f1f7f3',14,'#e1eee7')+icon_circle(31,730,'#d9f3e7','✦','#148a63',14)+text(79,747,'Plan gratuito',12,'#17211d',700)+text(79,764,'4 de 5 reglas activas',10,'#79837e')+rect(31,783,182,4,'#dce6e1',2)+rect(31,783,145,4,'#20a875',2)+text(31,810,'Mejorar plan',11,'#16875f',700)
 s+=rect(24,844,34,34,'#273d34',10)+text(41,866,'LS',10,'white',700,'middle')+text(69,858,'Laura Studio',11,'#17211d',700)+text(69,874,'laura@studio.com',9,'#929b97')
 return s

def top(title):
 s=rect(245,0,1195,70,'#fff',0,'#e6ebe7')+text(278,42,'ReplyFlow   ›',11,'#9ba39f')+text(348,42,title,11,'#4a5650',700)
 s+=text(1175,41,'●',11,'#20ae77')+text(1192,41,'Autorespuesta activa',11,'#52605a',600)+rect(1328,26,36,20,'#20a875',10)+rect(1347,28,16,16,'white',8)
 return s

def desktop_dashboard():
 s=svg_start(1440,900)+side()+top('Resumen')
 s+=text(280,122,'Buenos días, Laura 👋',25,'#17211d',700)+text(280,148,'Aquí tienes un resumen de lo que ReplyFlow hizo por ti.',13,'#7d8882')
 s+=rect(1257,103,145,40,'#168d65',9)+text(1278,128,'＋',18,'white',700)+text(1303,128,'Nueva regla',12,'white',700)
 cards=[('✉','#e3f5ed','#178c66','Respuestas enviadas','342','↗ 12% vs. semana pasada'),('♙','#f0eafa','#7f55b9','Contactos atendidos','218','27 nuevos esta semana'),('◷','#fbeee1','#cf7634','Tiempo ahorrado','8.4 h','≈ 1.2 h por día'),('⚡','#e6f1f8','#397fa5','Reglas activas','3 / 4','Límite del plan: 5')]
 for i,(ic,bg,fg,l,v,d) in enumerate(cards):
  x=280+i*276;s+=rect(x,178,260,124,'#fff',13,'#e4e9e6')+icon_circle(x+17,194,bg,ic,fg,16)+text(x+68,216,l,11,'#7c8781',600)+text(x+18,261,v,25,'#17211d',700)+text(x+18,282,d,10,'#1a9b70' if i==0 else '#87908b')
 s+=rect(280,319,1088,78,'#183d31',13)+icon_circle(297,333,'#2e6c56','◉','white',17)+text(355,349,'Haz que ReplyFlow trabaje por ti',14,'white',700)+text(355,370,'Crea respuestas automáticas para las preguntas que recibes todos los días.',11,'#c5d7cf')+rect(1240,340,107,35,'white',8)+text(1293,362,'Crear regla  ›',11,'#174331',700,'middle')
 s+=rect(280,414,704,426,'#fff',13,'#e4e9e6')+text(300,445,'Actividad reciente',14,'#17211d',700)+text(300,464,'Últimas respuestas enviadas automáticamente',10,'#8d9691')+text(922,451,'Ver toda  ›',11,'#218b66',700)+f'<line x1="280" y1="483" x2="984" y2="483" stroke="#edf0ee"/>'
 rows=[('MG','#9367D3','María González','“Hola”','Saludo inicial','Hace 2 min'),('CM','#E8994E','Carlos Méndez','“¿Me compartes el horario?”','Horario de atención','Hace 18 min'),('TM','#43A68D','Tienda Magnolia','“Quiero conocer sus precios”','Catálogo y precios','Hace 43 min'),('?','#7690AA','Número desconocido','“Buenas tardes”','Saludo inicial','Hace 1 h')]
 for i,(ini,col,n,msg,rule,tm) in enumerate(rows):
  y=500+i*82;s+=rect(299,y,36,36,col,10)+text(317,y+23,ini,10,'white',700,'middle')+text(348,y+14,n,11,'#17211d',700)+text(348,y+31,tm,9,'#9da5a1')+text(348,y+52,'Mensaje: '+msg,10,'#68736d')+rect(796,y+8,132,25,'#edf7f2',13)+text(862,y+25,'⚡ '+rule,9,'#288967',700,'middle')
  if i<3:s+=f'<line x1="299" y1="{y+68}" x2="965" y2="{y+68}" stroke="#f0f2f1"/>'
 s+=rect(1001,414,367,426,'#fff',13,'#e4e9e6')+text(1021,445,'Rendimiento',14,'#17211d',700)+text(1021,464,'Últimos 7 días',10,'#8d9691')+text(1021,520,'342',24,'#17211d',700)+text(1069,520,'respuestas',10,'#8f9893')
 hs=[90,130,106,164,140,190,146]
 for i,h in enumerate(hs):
  x=1028+i*43;s+=rect(x,760-h,23,h,'#2aa477' if i==5 else '#dceee7',5)+text(x+11,786,['L','M','M','J','V','S','D'][i],9,'#8f9994',400,'middle')
 s+='</svg>'
 (D/'dashboard-desktop.svg').write_text(s)

def mobile_rules():
 w,h=390,844;s=svg_start(w,h)
 s+=rect(0,0,w,62,'#fff',0,'#e6ebe7')+rect(16,15,31,31,'#173f31',10)+text(31,37,'↯',18,'#d7ff5a',700,'middle')+text(57,37,'Reply',18,'#17211d',700)+text(103,37,'Flow',18,'#19a973',700)+text(326,35,'●',10,'#20ae77')+rect(344,21,32,18,'#20a875',9)+rect(359,23,14,14,'white',7)
 s+=text(16,101,'Mis reglas',22,'#17211d',700)+text(16,123,'Define qué responder y cuándo hacerlo.',11,'#7d8882')+rect(334,79,40,40,'#168d65',9)+text(354,106,'＋',21,'white',700,'middle')
 s+=rect(16,145,275,38,'#fff',9,'#dfe5e1')+text(31,169,'⌕',18,'#909a95')+text(56,169,'Buscar una regla…',11,'#909a95')+rect(300,145,74,38,'#fff',9,'#dfe5e1')+text(337,169,'⚙  Filtros',10,'#647069',600,'middle')+text(16,205,'4 reglas',10,'#8a948e')
 rules=[('Saludo inicial','Coincidencia exacta','“Hola”','¡Hola! 👋 Gracias por escribirnos.','#24a679','#e1f3eb'),('Horario de atención','Contiene','“*horario*”','Nuestro horario es de lunes a viernes.','#4b91b8','#e6f1f7'),('Catálogo y precios','Patrón','“*precio* | *catálogo*”','Claro, puedes ver nuestro catálogo.','#8c67c0','#f0eafb')]
 for i,(n,m,tr,re,col,bg) in enumerate(rules):
  y=220+i*174;s+=rect(16,y,358,158,'#fff',13,'#e2e7e4')+rect(16,y,4,158,col,2)+rect(30,y+14,33,33,bg,9)+text(46,y+36,'⚡',14,col,700,'middle')+text(75,y+29,n,13,'#17211d',700)+text(75,y+44,m,9,'#8b9590')+rect(326,y+17,36,20,'#20a875',10)+rect(345,y+19,16,16,'white',8)
  s+=rect(30,y+60,330,67,'#f8faf9',9)+text(42,y+78,'CUANDO RECIBES',8,'#9da6a1',700)+text(42,y+96,tr,11,'#455149')+text(42,y+112,'RESPONDER CON',8,'#9da6a1',700)+text(42,y+124,re,10,'#455149')+text(31,y+146,'♙  Todos',9,'#89938e')+text(306,y+146,'▶  Activa',9,'#23936c',700)
 s+=rect(0,779,w,65,'#fff',0,'#dfe6e2')
 for i,(ic,n) in enumerate([('▦','Resumen'),('⚡','Reglas'),('◴','Actividad'),('⚙','Ajustes')]):
  x=49+i*97;col='#168d65' if i==1 else '#88928d';s+=text(x,807,ic,18,col,700,'middle')+text(x,826,n,9,col,600,'middle')
 s+='</svg>'
 (D/'reglas-mobile.svg').write_text(s)

desktop_dashboard();mobile_rules()
