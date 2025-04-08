package common.extensions

import com.codeborne.selenide.SelenideElement
import common.ApplicationManager
import constants.Constants
import io.appium.java_client.MobileDriver
import io.appium.java_client.touch.WaitOptions
import io.appium.java_client.touch.offset.PointOption
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.interactions.Actions
import java.time.Duration

var applicationManager: ApplicationManager = ApplicationManager()

fun SelenideElement?.type(value: String) {
    this?.click()
    Thread.sleep(2000)
    this?.clear()
    this?.sendKeys(value)
    var text = this?.text!!.filter { !it.isWhitespace() }
    if (!text.contains(value)) {
        this.clear()
        this.sendKeys(value)
    }
}

fun SelenideElement?.longTap() {
    when (Constants.RunVariables.PLATFORM) {
        Constants.Platform.IOS -> {
            val point: Point = this?.location!!
            val dimension: Dimension = this.size!!
            var x = point.x + dimension.width / 2
            var y = point.y + dimension.height / 2
            PlatformTouchAction(applicationManager.getDriver() as MobileDriver<*>)
                .press(PointOption.point(x, y))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(10)))
                .release()
                .perform()
        }

        Constants.Platform.AOS -> {
            val a = Actions(applicationManager.getDriver())
            a.clickAndHold(this)
            a.perform()
        }
        Constants.Platform.IOS_LOCAL -> {
            val point: Point = this?.location!!
            val dimension: Dimension = this.size!!
            var x = point.x + dimension.width / 2
            var y = point.y + dimension.height / 2
            PlatformTouchAction(applicationManager.getDriver() as MobileDriver<*>)
                .press(PointOption.point(x, y))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(10)))
                .release()
                .perform()
        }

        Constants.Platform.AOS_LOCAL -> {
            val a = Actions(applicationManager.getDriver())
            a.clickAndHold(this)
            a.perform()
        }
    }

}



