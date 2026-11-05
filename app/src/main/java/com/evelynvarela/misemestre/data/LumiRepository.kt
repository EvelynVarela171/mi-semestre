package com.evelynvarela.misemestre.data

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

private const val MODELO = "gemini-3.5-flash-lite"

private const val INSTRUCCIONES = """
Eres Lumi, una medusita amable que acompaña a estudiantes universitarios en la app Mi Semestre.
Responde siempre en español, con tono cercano y en máximo 120 palabras.
Escribe en texto plano: no uses Markdown, asteriscos ni encabezados.
Usa solo los datos del estudiante que recibes en cada mensaje. Si falta un dato, dilo sin inventarlo.
Para priorizar, ten en cuenta las fechas de entrega, el peso de las notas y lo que falta por evaluar.
Explica los temas con ejemplos sencillos y ayuda a entender, sin hacer la tarea completa.
"""

private const val LARGO_MAXIMO_FRASE = 120

private const val INSTRUCCIONES_FRASE = """
Recibes el nombre de una entrega universitaria.
Responde en español con una sola frase de máximo 12 palabras: el primer paso concreto para empezarla.
No saludes, no te presentes, no uses Markdown y no menciones otras tareas.
"""

class LumiRepository {

    private val ia = Firebase.ai(backend = GenerativeBackend.googleAI())
    private val chat = ia
        .generativeModel(MODELO, systemInstruction = content { text(INSTRUCCIONES) })
        .startChat()
    private val modeloFrases = ia
        .generativeModel(MODELO, systemInstruction = content { text(INSTRUCCIONES_FRASE) })

    suspend fun fraseDelDia(prioridad: String): String? =
        modeloFrases.generateContent("Entrega: $prioridad")
            .text?.trim()?.takeIf { it.isNotEmpty() && it.length <= LARGO_MAXIMO_FRASE }

    fun responder(pregunta: String, contexto: String): Flow<String> =
        chat.sendMessageStream("Datos del estudiante:\n$contexto\n\nPregunta: $pregunta")
            .mapNotNull { it.text }
}
