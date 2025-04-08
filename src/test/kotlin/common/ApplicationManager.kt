package common

import browserstack.shaded.org.json.JSONArray
import browserstack.shaded.org.json.JSONObject
import browserstack.shaded.org.json.simple.parser.JSONParser
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.appium.ScreenObject.screen
import common.helpers.DataReader.getValue
import constants.Constants
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.AndroidMobileCapabilityType.*
import io.appium.java_client.remote.MobileCapabilityType
import io.appium.java_client.remote.MobileCapabilityType.*
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.FileReader
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


open class ApplicationManager {

    private var driver: AppiumDriver<*>? = null
    private val clipboard = Selenide.clipboard()
    var formater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var date: Date = Date()

    @JvmName("getDriver1")
    fun createDriver(deviceIndex: String): AppiumDriver<*>? {
        var driver: AppiumDriver<*>? = null

        when (Constants.RunVariables.PLATFORM) {
                Constants.Platform.IOS -> {
        val capabilitiesIOS = DesiredCapabilities()
        val parser = JSONParser()
        val config =
            parser.parse(FileReader("src/test/resources/iOS_parallel.conf.json")) as JSONObject
        val envs = config["environments"] as JSONArray

        val envCapabilities = envs[deviceIndex.toInt()] as Map<String, String>
        var it: Iterator<*> = envCapabilities.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next() as Map.Entry<*, *>
            capabilitiesIOS.setCapability(key.toString(), value.toString())
        }
        val commonCapabilities = config["capabilities"] as Map<String, String>
        it = commonCapabilities.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next() as Map.Entry<*, *>
            if (capabilitiesIOS.getCapability(key.toString()) == null) {
                capabilitiesIOS.setCapability(key.toString(), value)
            }
        }
        val commonCapabilitiesNext = config["capabilities"] as Map<String, String>
        it = commonCapabilitiesNext.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it.next() as Map.Entry<*, *>
            if (capabilitiesIOS.getCapability(key.toString()) == "browserstack-build-1") {
                capabilitiesIOS.setCapability(key.toString(),  formater.format(date).toString())
            }
        }
        var username = System.getenv("BROWSERSTACK_USERNAME")
        if (username == null) {
            username = config["username"] as String?
        }
        var accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY")
        if (accessKey == null) {
            accessKey = config["access_key"] as String?
        }
        val app = System.getenv("BROWSERSTACK_APP_ID")
        if (app != null && !app.isEmpty()) {
            capabilitiesIOS.setCapability("app", app)
        }
        val url = URL("http://" + username + ":" + accessKey + "@" + config["server"] + "/wd/hub")
                    if (driver == null) {
                        driver = IOSDriver<SelenideElement>(url, capabilitiesIOS)
                    }
                    driver
                }



            Constants.Platform.AOS -> {
                val capabilitiesAOS = DesiredCapabilities()
                val parser = JSONParser()
                val config =
                    parser.parse(FileReader("src/test/resources/AOS_parallel.conf.json")) as JSONObject
                val envs = config["environments"] as JSONArray

                val envCapabilities = envs[deviceIndex.toInt()] as Map<String, String>
                var it: Iterator<*> = envCapabilities.entries.iterator()
                while (it.hasNext()) {
                    val (key, value) = it.next() as Map.Entry<*, *>
                    capabilitiesAOS.setCapability(key.toString(), value.toString())
                }
                val commonCapabilities = config["capabilities"] as Map<String, String>
                it = commonCapabilities.entries.iterator()
                while (it.hasNext()) {
                    val (key, value) = it.next() as Map.Entry<*, *>
                    if (capabilitiesAOS.getCapability(key.toString()) == null) {
                        capabilitiesAOS.setCapability(key.toString(), value)
                    }
                }
                val commonCapabilitiesNext = config["capabilities"] as Map<String, String>
                it = commonCapabilitiesNext.entries.iterator()
                while (it.hasNext()) {
                    val (key, value) = it.next() as Map.Entry<*, *>
                    if (capabilitiesAOS.getCapability(key.toString()) == "browserstack-build-1") {
                        capabilitiesAOS.setCapability(key.toString(),  formater.format(date).toString())
                    }
                }
                var username = System.getenv("BROWSERSTACK_USERNAME")
                if (username == null) {
                    username = config["username"] as String?
                }
                var accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY")
                if (accessKey == null) {
                    accessKey = config["access_key"] as String?
                }
                val app = System.getenv("BROWSERSTACK_APP_ID")
                if (app != null && !app.isEmpty()) {
                    capabilitiesAOS.setCapability("app", app)
                }
                val url = URL("http://" + username + ":" + accessKey + "@" + config["server"] + "/wd/hub")

                if (driver == null) {
                    driver = AndroidDriver<SelenideElement>(url, capabilitiesAOS)
                }
                driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS)
                driver
            }
            Constants.Platform.AOS_LOCAL -> {
                val capabilitiesAOS_LOCAL = DesiredCapabilities()

                capabilitiesAOS_LOCAL.setCapability("avd", getValue("AOS_DEVICE_NAME"))
                capabilitiesAOS_LOCAL.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
                capabilitiesAOS_LOCAL.setCapability(PLATFORM_VERSION, getValue("AOS_PLATFORM_VERSION"))
                capabilitiesAOS_LOCAL.setCapability("autoGrantPermissions", "true")
                capabilitiesAOS_LOCAL.setCapability("appPackage", "ua.com.test")
                capabilitiesAOS_LOCAL.setCapability("locale", "UA")
                capabilitiesAOS_LOCAL.setCapability("language", "uk")
                capabilitiesAOS_LOCAL.setCapability("appActivity", ".core.modules.splash.SplashScreenActivity")
                capabilitiesAOS_LOCAL.setCapability("appWaitActivity", ".core.modules.login.LoginActivity")
                capabilitiesAOS_LOCAL.setCapability("newCommandTimeout", 10000)
                driver = AndroidDriver<SelenideElement>(URL("http://127.0.0.1:4723/wd/hub"), capabilitiesAOS_LOCAL)
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
            }
            Constants.Platform.IOS_LOCAL -> {
                val capabilitiesIOS_LOCAL = DesiredCapabilities()

                capabilitiesIOS_LOCAL.setCapability(DEVICE_NAME, getValue("IOS_DEVICE_NAME"))
                capabilitiesIOS_LOCAL.setCapability("automationName", "XCUITest")
                capabilitiesIOS_LOCAL.setCapability(PLATFORM_VERSION, getValue("IOS_PLATFORM_VERSION"))
                capabilitiesIOS_LOCAL.setCapability(CapabilityType.PLATFORM_NAME, "iOS")
                capabilitiesIOS_LOCAL.setCapability(UDID, getValue("IOS_UDID"))
                capabilitiesIOS_LOCAL.setCapability("autoAcceptAlerts", true)
                capabilitiesIOS_LOCAL.setCapability("autoDismissAlerts", true)
                capabilitiesIOS_LOCAL.setCapability("bundleId", "com.test.mobapplication")
                driver = IOSDriver<SelenideElement>(URL("http://127.0.0.1:4723/wd/hub"), capabilitiesIOS_LOCAL)
            }

            }
        return driver
    }


    fun getDriver(): AppiumDriver<*> {
        return WebDriverRunner.getWebDriver() as AppiumDriver<*>
    }

    inline fun <reified T> getHelper(): T {
        val clazz: Class<*>?
        val constructor: Constructor<*>?
        var `object`: Any? = null
        try {
            clazz = T::class.java
            constructor = clazz.getConstructor(ApplicationManager::class.java)
            `object` = constructor.newInstance(this)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return `object` as T
    }

    inline fun <reified T> getScreen(): T {
        return screen(T::class.java)
    }

}