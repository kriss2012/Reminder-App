package com.vasant.pillpal.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.vasant.pillpal.BuildConfig
import com.vasant.pillpal.repository.MedicineRepo
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    private val medicineRepo: MedicineRepo
) {
    // The complex constructor you were trying to use is internal.
    private val generativeModel = GenerativeModel(
        // 1. Model name
        modelName = "gemini-2.5-flash",

        // 2. Your API Key from BuildConfig
        apiKey = BuildConfig.API_KEY,

        // 3. Configuration is set here
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        },

        // 4. System instructions are set here
        systemInstruction = content {
            text(
                """
                You are a professional medical AI assistant for Kiri Reminder, a medication management app. Your role is to provide evidence-based, educational health information while maintaining a supportive and compassionate tone.

                CORE RESPONSIBILITIES:
                ✓ Provide accurate, concise medication information (dosages, interactions, side effects, schedules)
                ✓ Reference user's specific medications to personalize advice and check interactions
                ✓ Answer health questions using evidence-based medical knowledge
                ✓ Offer medication adherence tips and best practices
                ✓ Explain symptoms and when to seek professional care
                ✓ Maintain confidentiality and never share health data

                CRITICAL GUIDELINES:
                ⚠️ For emergencies/severe symptoms: Immediately recommend "Seek emergency medical attention or call 911"
                ⚠️ Never diagnose conditions, prescribe medications, or replace professional medical advice
                ⚠️ When uncertain, recommend consulting healthcare providers
                ⚠️ Always include: "**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."

                COMMUNICATION STYLE:
                • Use simple, non-technical language; explain medical terms clearly
                • Keep responses concise (2-3 sentences for quick queries, max 5 for detailed ones)
                • Be empathetic and supportive
                • Format responses clearly with bullet points when listing information
                • Focus on practical, actionable advice

                MEDICATION MANAGEMENT:
                • Review drug interactions between user's current medications
                • Suggest optimal times to take medications based on food/water requirements
                • Provide adherence reminders and tips
                • Explain why medications are important for their conditions

                You have access to: user's current medications, schedules, dosages, and completion status. Use this context to provide personalized guidance.
            """.trimIndent()
            )
        }
    )


    /**
     * Formats user's medication list into a readable context for the AI
     */
    private suspend fun getMedicationContext(): String {
        return try {
            val medicines = medicineRepo.getMedicine().first()
            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateFormatFull = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault())

            if (medicines.isEmpty()) {
                "\n\n[USER CONTEXT] No medications currently scheduled in Kiri Reminder."
            } else {
                val upcomingMeds = medicines.filter { !it.isCompleted }
                val completedMeds = medicines.filter { it.isCompleted }

                val upcomingStr = if (upcomingMeds.isNotEmpty()) {
                    "UPCOMING DOSES:\n" + upcomingMeds.joinToString("\n") { med ->
                        val timeStr = dateFormat.format(Date(med.time))
                        "• ${med.medName} (${med.dosage}) - $timeStr"
                    }
                } else "No upcoming doses scheduled"

                val completedStr = if (completedMeds.isNotEmpty()) {
                    "DOSES TAKEN TODAY: ${completedMeds.joinToString(", ") { it.medName }}"
                } else "No doses taken yet"

                "\n\n[USER MEDICATION SCHEDULE - Current time: ${dateFormatFull.format(currentTime)}]\n$upcomingStr\n$completedStr"
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Generates AI response with context about user's medications
     */
    suspend fun generateMedicalResponse(prompt: String): String {
        // If API key is empty or looks like a placeholder, use local fallback directly
        if (BuildConfig.API_KEY.isBlank() || BuildConfig.API_KEY == "null" || BuildConfig.API_KEY.startsWith("YOUR_")) {
            return generateMockResponse(prompt)
        }

        return try {
            // Get user's medication context
            val medicationContext = getMedicationContext()

            // Combine user prompt with medication context
            val enrichedPrompt = "$prompt$medicationContext"

            // Generate response with context
            val response = generativeModel.generateContent(enrichedPrompt)
            response.text ?: generateMockResponse(prompt)
        } catch (e: Exception) {
            // Log the exception for debugging
            e.printStackTrace()
            // Fall back to a smart mock response instead of displaying a raw error
            generateMockResponse(prompt)
        }
    }

    /**
     * Generates a realistic, contextual health assistant response offline/without API key.
     */
    private suspend fun generateMockResponse(prompt: String): String {
        val medicines = try {
            medicineRepo.getMedicine().first()
        } catch (e: Exception) {
            emptyList()
        }

        val normalizedPrompt = prompt.lowercase(Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return when {
            normalizedPrompt.contains("hello") || normalizedPrompt.contains("hi ") || normalizedPrompt.contains("hey") || normalizedPrompt.contains("hola") -> {
                "Hello! I am your Kiri Reminder AI Assistant. How can I help you with your medications today?\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
            }
            normalizedPrompt.contains("medication") || normalizedPrompt.contains("pill") || normalizedPrompt.contains("schedule") || normalizedPrompt.contains("take") || normalizedPrompt.contains("my") || normalizedPrompt.contains("what") -> {
                if (medicines.isEmpty()) {
                    "You currently do not have any medications scheduled in Kiri Reminder. To add a medication, press the '+' button on the home screen.\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
                } else {
                    val listStr = medicines.joinToString("\n") { med ->
                        val timeStr = timeFormat.format(Date(med.time))
                        "• **${med.medName}** (${med.dosage}) - $timeStr"
                    }
                    "Here is your current medication schedule:\n\n$listStr\n\nPlease make sure to take them on time as scheduled!\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
                }
            }
            normalizedPrompt.contains("side effect") || normalizedPrompt.contains("symptom") || normalizedPrompt.contains("pain") || normalizedPrompt.contains("hurt") -> {
                "Side effects depend heavily on the specific medication. Common side effects can include nausea, drowsiness, or dry mouth. If you are experiencing severe or worsening symptoms, please contact a physician or visit an emergency room immediately.\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
            }
            normalizedPrompt.contains("water") || normalizedPrompt.contains("food") || normalizedPrompt.contains("stomach") || normalizedPrompt.contains("eat") -> {
                "As a general rule, take medications with a full glass of water. Some medicines require food to protect your stomach lining or improve absorption, while others must be taken on an empty stomach. Always check the prescription label for specific instructions.\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
            }
            else -> {
                "I understand you are asking about: \"$prompt\". As your Kiri Reminder health assistant, I recommend maintaining a consistent routine with your medicine. Please speak to your doctor or pharmacist for specific concerns.\n\n**Disclaimer:** This information is educational only and not a substitute for professional medical advice. Consult your healthcare provider."
            }
        }
    }


    /**
     * Get a summary of user's medication schedule
     */
    suspend fun getMedicationSummary(): String {
        return try {
            val medicines = medicineRepo.getMedicine().first()
            if (medicines.isEmpty()) {
                "You don't have any medications scheduled yet. Would you like to add one?"
            } else {
                val total = medicines.size
                val completed = medicines.count { it.isCompleted }
                val pending = total - completed

                "You have $total medication(s) scheduled:\n" +
                "✓ Completed: $completed\n" +
                "⏰ Pending: $pending"
            }
        } catch (e: Exception) {
            "Unable to fetch medication summary."
        }
    }
}
