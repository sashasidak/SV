package tests.base

import com.codeborne.selenide.WebDriverRunner
import common.MyTelegramBot
import common.TestRail.APIClient
import common.TestRail.TestRails
import common.helpers.DataReader.getValue
import io.qameta.allure.Attachment
import org.json.simple.JSONObject
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestResult
import java.lang.reflect.Method

class TestListener : ITestListener {
    private var successCount = 0
    private var failedCount = 0
    private var skipCount = 0

    override fun onTestStart(result: ITestResult) {

    }

    override fun onTestSkipped(result: ITestResult) {
        println("Skipped: " + result.name)
        incrementSkipCount()
    }

    override fun onTestSuccess(result: ITestResult) {
        println("Success: " + result.name)
        incrementSuccessCount()
        if (result.method.method.getAnnotation(TestRails::class.java)?.id != null) {
            sendResultToTestRail(true, result.method.method)
        }
    }

    override fun onTestFailure(result: ITestResult) {
        incrementFailedCount()
        if (result.method.method.getAnnotation(TestRails::class.java)?.id != null) {
            sendResultToTestRail(false, result.method.method)
        }
        saveScreenshotPNG()
        getPageSource()
    }

    override fun onTestFailedButWithinSuccessPercentage(result: ITestResult) {
        println("Broken: " + result.name)
    }

    override fun onStart(result: ITestContext?) {
        println("-------------- START --------------")
    }

    override fun onFinish(context: ITestContext?) {
        println("-------------- FINISH --------------")
        sendResultTelegram()
    }

    private fun sendResultTelegram() {
        val botToken = getValue("botToken")
        val chatId = getValue("chatId")
        val results =
            "=======================================================\nРезультаты тестов: Success $successCount, Failed $failedCount, Skip $skipCount\n======================================================="

        val bot = MyTelegramBot(botToken)
        bot.sendMessage(chatId, results)
    }

    private fun sendResultToTestRail(isSuccess: Boolean, testMethod: Method) {
        val annotation = testMethod.getAnnotation(TestRails::class.java)
        val client = APIClient(getValue("APIClient"))
        client.setUser(getValue("setUser"))
        client.setPassword(getValue("setPassword"))
        val apiKey = getValue("apiKey") // Ваш API ключ TestRail
        client.setApiKey(apiKey)
        val caseId = annotation?.id
        val suiteId = "2" // ID набора тестов в TestRail
        val data = JSONObject()
        data["status_id"] = if (isSuccess) 1 else 5 // 1 - passed, 5 - failed
        data["comment"] = "Тест выполнен успешно" // Комментарий к результату (необязательно)
        client.sendPost("add_result_for_case/$suiteId/$caseId", data)
    }
    fun incrementSuccessCount() {
        successCount++
    }
    fun incrementFailedCount() {
        failedCount++
    }
    fun incrementSkipCount() {
        skipCount++
    }

    @Attachment(value = "Screenshot", type = "image/png")
    private fun saveScreenshotPNG(): ByteArray? {
        return (WebDriverRunner.getWebDriver() as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }
    @Attachment(value = "Screen source", type = "file/xml")
    private fun getPageSource(): String? {
        return (WebDriverRunner.getWebDriver().pageSource)
    }
}
