package tests.base

import com.codeborne.selenide.WebDriverRunner
import common.ApplicationManager
import common.helpers.VRTHelper
import common.helpers.WireMockHelper
import constants.Constants
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Parameters
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.testng.annotations.Optional

var vrtHelper: VRTHelper = VRTHelper()
var wireMockHelper: WireMockHelper = WireMockHelper()
var applicationManager: ApplicationManager = ApplicationManager()

open class BaseTest {
    @BeforeMethod
    @Parameters(value = ["deviceIndex", "platform"])
    fun createDriver(@Optional deviceIndex: String?, @Optional platform: String?) {
        val resolvedDeviceIndex = deviceIndex ?: "0"
        val resolvedPlatform = platform ?: Constants.RunVariables.PLATFORM.name

        Constants.RunVariables.PLATFORM = Constants.Platform.valueOf(resolvedPlatform)

        WebDriverRunner.setWebDriver(applicationManager.createDriver(resolvedDeviceIndex))
    }

    @AfterMethod
    fun closeApplication(result: ITestResult) {
        updateTestStatus(result)
        WebDriverRunner.closeWebDriver()
    }

    private fun updateTestStatus(result: ITestResult) {
        val driver = WebDriverRunner.getWebDriver() as RemoteWebDriver
        val sessionId: SessionId = driver.sessionId
        val status = if (result.isSuccess) "passed" else "failed"
        val reason = if (!result.isSuccess) "Element not found on the login page" else ""

        val browserstackUsername = "bsuser_WNCwvM"
        val browserstackAccessToken = "jRZwEynszwBuVsAmiqwb"

        val url = URL("https://api-cloud.browserstack.com/app-automate/sessions/$sessionId.json")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Basic " + encodeCredentials(browserstackUsername, browserstackAccessToken))
        connection.doOutput = true

        val data = "{\"status\":\"$status\", \"reason\":\"$reason\"}".toByteArray()
        connection.outputStream.write(data)

        val responseCode = connection.responseCode

        val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()

        // Выводим ответ от сервера
        println(response.toString())

        connection.disconnect()
    }

    private fun encodeCredentials(username: String, accessToken: String): String {
        val credentials = "$username:$accessToken"
        return String(Base64.getEncoder().encode(credentials.toByteArray()))
    }
}
