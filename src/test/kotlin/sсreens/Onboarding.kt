package sсreens

import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideElement
import common.helpers.WaitsHelper.implicitlyWait
import io.appium.java_client.pagefactory.AndroidFindBy
import io.appium.java_client.pagefactory.iOSXCUITFindBy
import sсreens.base.AnyScreen
import tests.base.applicationManager
import java.time.Duration

class Onboarding : AnyScreen() {

    @AndroidFindBy(id = "ua.com.test:id/tvText")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name=\"No\"]")
    private val dontHaveAmmerCard: SelenideElement? = null

    @AndroidFindBy(id = "ua.com.test:id/etPhoneNumber")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeApplication[@name=\"test\"]/XCUIElementTypeWindow[1]/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[1]/XCUIElementTypeOther[3]/XCUIElementTypeTextField")
    private val dontHaveAmmer: SelenideElement? = null

    @iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name=\"Далі\"]")
    private val dontHmmer: SelenideElement? = null

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Код для входу')]")
    @iOSXCUITFindBy(accessibility = "Код для входу")
    private val doner: SelenideElement? = null

    fun clickOnDontHaveAmmerCard(): Onboarding {
        implicitlyWait(2)
        dontHaveAmmerCard?.shouldBe(Condition.visible, Duration.ofSeconds(5))
        return applicationManager.getScreen()
    }

    fun clickOn(): Onboarding {
//        dontHaveAmmer?.shouldBe(Condition.visible, Duration.ofSeconds(15))
//        dontHaveAmmer?.click()
//        dontHaveAmmer?.sendKeys("967023805")
//        when (constants.Constants.RunVariables.PLATFORM) {
//            constants.Constants.Platform.IOS -> {
//                dontHmmer?.click()
//            }
//            constants.Constants.Platform.AOS -> {
//            }
//            constants.Constants.Platform.IOS_LOCAL -> {
//                dontHmmer?.click()
//            }
//            constants.Constants.Platform.AOS_LOCAL -> {
//            }
//        }
//        doner?.shouldBe(Condition.visible, Duration.ofSeconds(10))
        return applicationManager.getScreen()
    }


}