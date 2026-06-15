package com.vasant.pillpal.data.chat


data class GenerationParameters(
    val max_new_tokens: Int = 256,
    val temperature: Float = 0.7f,
    val top_p: Float = 0.95f,
    val top_k: Int = 50,
    val do_sample: Boolean = true,
    val return_full_text: Boolean = false
)
