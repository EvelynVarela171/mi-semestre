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

class LumiRepository {

    private val chat = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(MODELO, systemInstruction = content { text(INSTRUCCIONES) })
        .startChat()

    fun responder(pregunta: String, contexto: String): Flow<String> =
        chat.sendMessageStream("Datos del estudiante:\n$contexto\n\nPregunta: $pregunta")
            .mapNotNull { it.text }
}
