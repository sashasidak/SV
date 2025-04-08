package common

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class MyTelegramBot(private val botToken: String) : TelegramLongPollingBot() {

    override fun getBotToken(): String {
        return botToken
    }

    override fun getBotUsername(): String {
        return "WALLE_auto_test_bot"
    }

    override fun onUpdateReceived(update: Update) {
        // Обработка входящих сообщений, если понадобится
    }

    fun sendMessage(chatId: String, message: String) {
        val sendMessage = SendMessage(chatId, message)
        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}
