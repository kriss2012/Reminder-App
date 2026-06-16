package com.vasant.pillpal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasant.pillpal.data.chat.Author
import com.vasant.pillpal.data.chat.ChatUiModel
import com.vasant.pillpal.services.GeminiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val geminiService: GeminiService
) : ViewModel() {

    val conversation: StateFlow<List<ChatUiModel.Message>>
        get() = _conversation

    private val _conversation = MutableStateFlow(
        listOf(ChatUiModel.Message.initConv)
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val myChat = ChatUiModel.Message(
                message,
                author = Author.ME
            )
            _conversation.emit(_conversation.value + myChat)

            _isLoading.emit(true)
            val botResponse = geminiService.generateMedicalResponse(message)
            _isLoading.emit(false)

            _conversation.emit(
                _conversation.value + ChatUiModel.Message(
                    text = botResponse,
                    author = Author.BOT
                )
            )
        }
    }
}

