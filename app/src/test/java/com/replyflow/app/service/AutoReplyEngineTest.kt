package com.replyflow.app.service

import com.replyflow.app.data.IncomingMessage
import com.replyflow.app.data.MatchType
import com.replyflow.app.data.Rule
import org.junit.Assert.*
import org.junit.Test

class AutoReplyEngineTest {
    private val engine = AutoReplyEngine()
    private fun rule(id:Long, trigger:String, match:MatchType, allow:List<String> = emptyList(), deny:List<String> = emptyList()) = Rule(
        id,"r$id",trigger,match,"respuesta",listOf("wa"),true,false,false,"09:00","18:00",allow,deny,0
    )
    private fun incoming(text:String,sender:String="Ana")=IncomingMessage("com.whatsapp","wa",sender,text)

    @Test fun firstMatchingRuleWins(){
        val result=engine.evaluate(listOf(rule(1,"hola",MatchType.CONTAINS),rule(2,"*",MatchType.ANY)),incoming("Hola equipo"))
        assertEquals(1L,result?.rule?.id)
    }
    @Test fun exactIsTrimmedAndCaseInsensitive(){
        assertNotNull(engine.evaluate(listOf(rule(1," HOLA ",MatchType.EXACT)),incoming("hola")))
    }
    @Test fun patternSupportsAlternativesAndWildcard(){
        assertNotNull(engine.evaluate(listOf(rule(1,"precio*|catálogo",MatchType.PATTERN)),incoming("precio del producto")))
    }
    @Test fun denyHasPriorityOverAllow(){
        assertNull(engine.evaluate(listOf(rule(1,"*",MatchType.ANY,listOf("Ana"),listOf("Ana"))),incoming("x")))
    }
    @Test fun channelMustMatch(){
        assertNull(engine.evaluate(listOf(rule(1,"*",MatchType.ANY)),incoming("x").copy(channel="telegram")))
    }
}
