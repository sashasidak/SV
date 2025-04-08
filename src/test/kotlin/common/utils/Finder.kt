package common.utils

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SelenideElement
import constants.Constants
import org.openqa.selenium.By


class Finder {
    private var iosFinder: By? = null
    private var aosFinder: By? = null

    var findByIos: By? = null
        set(value) {
            field = value
            iosFinder = value
        }

    var findByAos: By? = null
        set(value) {
            field = value
            aosFinder = value
        }


    fun findElement(): SelenideElement {
        var element: SelenideElement
        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> {
                element = Selenide.`$`(iosFinder)
            }
            Constants.Platform.AOS -> {
                element = Selenide.`$`(aosFinder)
            }
            Constants.Platform.IOS_LOCAL -> {
                element = Selenide.`$`(iosFinder)
            }
            Constants.Platform.AOS_LOCAL -> {
                element = Selenide.`$`(aosFinder)
            }
        }
        return element
    }
}