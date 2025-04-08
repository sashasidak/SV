package common.helpers

import common.ApplicationManager
import io.appium.java_client.AppiumDriver
import java.util.concurrent.TimeUnit

private var driver: AppiumDriver<*>? = null

object WaitsHelper : ApplicationManager() {

    fun implicitlyWait (seconds:Long = 7){
        driver?.manage()?.timeouts()?.implicitlyWait(seconds, TimeUnit.SECONDS)
    }

}