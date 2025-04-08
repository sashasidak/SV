package common.helpers

import com.github.tomakehurst.wiremock.client.WireMock.*
import constants.Constants
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class WireMockHelper {

    init {
        configureFor(Constants.WireMock.CUSTOM_URL, Constants.WireMock.PORT)
    }

    fun setupDefaultProxy() {
        stubFor(any(urlMatching(".*"))
            .willReturn(aResponse()
                .withStatus(200)
                .proxiedFrom("https://test.com.ua"))
            .withId(UUID.fromString("281f033a-7085-4fd1-9166-b692266f7848")))
    }

    fun stubAction(status: Int, action: String, filePath: String) {
        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("stubs/$filePath")
                ?: throw IOException("Файл не найден: stubs/$filePath")

            val responseBody = inputStream.readBytes().toString(StandardCharsets.UTF_8)
            stubFor(any(urlMatching(".*"))
                .withRequestBody(containing("\"action\":\"$action\""))
                .willReturn(aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody))
                .withId(UUID.randomUUID()))
        } catch (e: IOException) {
            e.printStackTrace()
            System.err.println("Ошибка при чтении файла: stubs/$filePath")
        }
    }

    fun stubScenarioAction(vararg actionsAndFiles: String) {
        require(actionsAndFiles.size % 2 == 0) { "Количество параметров должно быть четным (action и filePath должны идти парами)." }

        for (i in actionsAndFiles.indices step 2) {
            stubAction(200, actionsAndFiles[i], actionsAndFiles[i + 1])
        }
    }

    fun stubURL(status: Int, endpoint: String, filePath: String) {
        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("stubs/$filePath")
                ?: throw IOException("Файл не найден: stubs/$filePath")

            val responseBody = inputStream.readBytes().toString(StandardCharsets.UTF_8)
            stubFor(any(urlMatching("$endpoint\\?.*"))
                .willReturn(aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(responseBody))
                .withId(UUID.randomUUID()))
        } catch (e: IOException) {
            e.printStackTrace()
            System.err.println("Ошибка при чтении файла: stubs/$filePath")
        }
    }

    fun checkRequest(url: String) {
        try {
            verify(postRequestedFor(urlMatching("$url.*")))
        } catch (e: Exception) {
            error("\n Тест упал: Не было такого POST запроса")
        }
    }

    fun checkNoRequest(url: String) {
        try {
            verify(0, postRequestedFor(urlMatching("$url.*")))
        } catch (e: AssertionError) {
            error("\n Тест упал: Был отправлен запрос, хотя его не должно быть")
        }
    }

    fun resetToDefaultProxy() {
        listAllStubMappings().mappings.forEach { stub ->
            if (stub.id != UUID.fromString("281f033a-7085-4fd1-9166-b692266f7848")) {
                removeStub(stub)
            }
        }
    }

    fun clearAllMappingsAndRequests() {
        resetAllRequests()
        resetAllScenarios()
    }
}
