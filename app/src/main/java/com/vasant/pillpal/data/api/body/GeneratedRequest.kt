package com.vasant.pillpal.data.api.body

import com.vasant.pillpal.data.chat.GenerationParameters

data class ChatRequest(
    val inputs: String,
    val parameters: GenerationParameters = GenerationParameters()
)