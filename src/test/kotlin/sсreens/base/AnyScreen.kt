package sсreens.base

import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.SelenideElement
import common.extensions.PlatformTouchAction
import common.utils.Finder
import constants.Constants
import io.appium.java_client.MobileBy
import io.appium.java_client.TouchAction
import io.appium.java_client.touch.WaitOptions.waitOptions
import io.appium.java_client.touch.offset.PointOption
import io.appium.java_client.touch.offset.PointOption.point
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.Rectangle
import org.openqa.selenium.WebElement
import tests.base.applicationManager
import java.time.Duration


open class AnyScreen {
    var finder = Finder()

    fun passAlert(buttonName: String?) {
        var alertName: SelenideElement

        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> {
                alertName = `$`(By.id(buttonName))
                alertName.shouldBe(visible)
                alertName.shouldBe(visible, Duration.ofSeconds(2))
            }

            Constants.Platform.AOS -> {
                alertName = `$`(By.xpath("//android.widget.Button[contains(@text, '$buttonName')]"))
                alertName.shouldBe(exist)
            }
            Constants.Platform.IOS_LOCAL -> {
                alertName = `$`(By.id(buttonName))
                alertName.shouldBe(visible)
                alertName.shouldBe(visible, Duration.ofSeconds(2))
            }

            Constants.Platform.AOS_LOCAL -> {
                alertName = `$`(By.xpath("//android.widget.Button[contains(@text, '$buttonName')]"))
                alertName.shouldBe(exist)
            }
        }

        alertName.click()
    }

    fun scrollToText(text: String, direction: Direction = Direction.DOWN) {
        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> {
                var counter = 0
                while (!`$`(By.name(text)).isDisplayed && counter != 11) {
                    iosScroll(direction)
                    counter++
                }
            }

            Constants.Platform.AOS -> {
                aosScroll(ScrollBy.toText(text))
            }
            Constants.Platform.IOS_LOCAL -> {
                var counter = 0
                while (!`$`(By.name(text)).isDisplayed && counter != 11) {
                    iosScroll(direction)
                    counter++
                }
            }

            Constants.Platform.AOS_LOCAL -> {
                aosScroll(ScrollBy.toText(text))
            }
        }
    }

    fun scrollToContentDesk(content: String, direction: Direction = Direction.DOWN) {
        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> {
                var counter = 0
                while (!`$`(By.xpath("//*[contains(@name,'$content')]")).isDisplayed && counter != 3) {
                    iosScroll(direction)
                    counter++
                }
            }

            Constants.Platform.AOS -> {
                aosScroll(ScrollBy.toContentDesk(content))
            }
            Constants.Platform.IOS_LOCAL -> {
                var counter = 0
                while (!`$`(By.xpath("//*[contains(@name,'$content')]")).isDisplayed && counter != 3) {
                    iosScroll(direction)
                    counter++
                }
            }

            Constants.Platform.AOS_LOCAL -> {
                aosScroll(ScrollBy.toContentDesk(content))
            }
        }
    }

    data class ScrollBy(val uiSelector: String) {
        companion object {
            fun toId(id: String) = ScrollBy("new UiSelector().resourceIdMatches(\".*id/$id\")")
            fun toText(text: String) = ScrollBy("new UiSelector().text(\"${text}\")")
            fun toContentDesk(content: String) = ScrollBy("new UiSelector().description(\"$content\")")
        }
    }

    fun aosScroll(selector: ScrollBy): WebElement? {
        val container = "new UiSelector().scrollable(true)"
        val script = "new UiScrollable($container).setMaxSearchSwipes(8).scrollIntoView(${selector.uiSelector})"
        return applicationManager.getDriver()?.findElement(MobileBy.AndroidUIAutomator(script))
    }

    open fun iosScroll(direction: Direction) {
        //The viewing size of the device
        val size: Dimension = applicationManager.getDriver().manage().window().size

        //x position set to mid-screen horizontally
        val width = size.width / 2

        //Starting y location set to 80% of the height (near bottom)
        val startPoint = (size.getHeight() * 0.80).toInt()

        //Ending y location set to 20% of the height (near top)
        val endPoint = (size.getHeight() * 0.20).toInt()

        when (direction) {
            Direction.DOWN -> PlatformTouchAction(applicationManager.getDriver()).press(point(width, startPoint))
                .waitAction(waitOptions(Duration.ofMillis(3000))).moveTo(point(width, endPoint)).release().perform()

            Direction.UP -> PlatformTouchAction(applicationManager.getDriver()).press(point(width, endPoint))
                .waitAction(waitOptions(Duration.ofMillis(3000))).moveTo(point(width, startPoint)).release().perform()

            else -> {}
        }
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    fun hideKeyboard(textForIos: String) {
        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> {
                applicationManager.getDriver().findElement(By.name(textForIos)).click()
            }

            Constants.Platform.AOS -> {
                applicationManager.getDriver().hideKeyboard()
            }
            Constants.Platform.IOS_LOCAL -> {
                applicationManager.getDriver().findElement(By.name(textForIos)).click()
            }

            Constants.Platform.AOS_LOCAL -> {
                applicationManager.getDriver().hideKeyboard()
            }
        }
    }

    fun findElement(builder: Finder.() -> Unit): SelenideElement {
        finder.builder()
        return finder.findElement()
    }

    open fun swipeToElementInElement(el: SelenideElement, dir: Direction, range: IntRange) {
        for (i in range) {

            val ANIMATION_TIME = 200 // ms
            val PRESS_TIME = 500 // ms
            val driver = applicationManager.getDriver()
            val dims: Dimension = driver.manage().window().size
            val leftBorder: Int
            val rightBorder: Int
            leftBorder = 10
            rightBorder = 10
            var edgeBorder = 10
            val pointOptionStart: PointOption<*>
            val pointOptionEnd: PointOption<*>
            val rect = el.rect

            when (Constants.RunVariables.PLATFORM) {
                Constants.Platform.IOS -> {
                    // find rect that overlap screen
                    if (rect.x < 0) {
                        rect.width = rect.width + rect.x
                        rect.x = 0
                    }
                    if (rect.y < 0) {
                        rect.height = rect.height + rect.y
                        rect.y = 0
                    }
                    if (rect.width > dims.width) rect.width = dims.width
                    if (rect.height > dims.height) rect.height = dims.height

                    when (dir) {
                        Direction.LEFT -> {
                            pointOptionStart = point(
                                rect.x + rect.width - rightBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + leftBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        Direction.RIGHT -> {
                            pointOptionStart = point(
                                rect.x + leftBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + rect.width - rightBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        else -> throw IllegalArgumentException("swipeElementIOS(): dir: '$dir' NOT supported")
                    }
                    // execute swipe using TouchAction
                    try {
                        TouchAction(driver)
                            .press(pointOptionStart) // a bit more reliable when we add small wait
                            .waitAction(waitOptions(Duration.ofMillis(PRESS_TIME.toLong())))
                            .moveTo(pointOptionEnd)
                            .release().perform()
                    } catch (e: java.lang.Exception) {
                        System.err.println("""swipeElementIOS(): TouchAction FAILED ${e.message} """.trimIndent())
                        return
                    }
                    // always allow swipe action to complete
                    try {
                        Thread.sleep(ANIMATION_TIME.toLong())
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }

                Constants.Platform.AOS -> {
                    val rect: Rectangle = el.rect
                    when (dir) {
                        Direction.LEFT -> {
                            pointOptionStart = point(
                                rect.x + rect.width - edgeBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + edgeBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        Direction.RIGHT -> {
                            pointOptionStart = point(
                                rect.x + edgeBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + rect.width - edgeBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        else -> throw IllegalArgumentException("swipeElementAndroid(): dir: '$dir' NOT supported")
                    }
                    // execute swipe using TouchAction
                    try {
                        TouchAction(driver)
                            .press(pointOptionStart) // a bit more reliable when we add small wait
                            .waitAction(waitOptions(Duration.ofMillis(PRESS_TIME.toLong())))
                            .moveTo(pointOptionEnd)
                            .release().perform()
                    } catch (e: Exception) {
                        System.err.println("""swipeElementAndroid(): TouchAction FAILED ${e.message} """.trimIndent())
                        return
                    }
                    // always allow swipe action to complete
                    try {
                        Thread.sleep(ANIMATION_TIME.toLong())
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
                Constants.Platform.IOS_LOCAL -> {
                    // find rect that overlap screen
                    if (rect.x < 0) {
                        rect.width = rect.width + rect.x
                        rect.x = 0
                    }
                    if (rect.y < 0) {
                        rect.height = rect.height + rect.y
                        rect.y = 0
                    }
                    if (rect.width > dims.width) rect.width = dims.width
                    if (rect.height > dims.height) rect.height = dims.height

                    when (dir) {
                        Direction.LEFT -> {
                            pointOptionStart = point(
                                rect.x + rect.width - rightBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + leftBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        Direction.RIGHT -> {
                            pointOptionStart = point(
                                rect.x + leftBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + rect.width - rightBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        else -> throw IllegalArgumentException("swipeElementIOS(): dir: '$dir' NOT supported")
                    }
                    // execute swipe using TouchAction
                    try {
                        TouchAction(driver)
                            .press(pointOptionStart) // a bit more reliable when we add small wait
                            .waitAction(waitOptions(Duration.ofMillis(PRESS_TIME.toLong())))
                            .moveTo(pointOptionEnd)
                            .release().perform()
                    } catch (e: java.lang.Exception) {
                        System.err.println("""swipeElementIOS(): TouchAction FAILED ${e.message} """.trimIndent())
                        return
                    }
                    // always allow swipe action to complete
                    try {
                        Thread.sleep(ANIMATION_TIME.toLong())
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }

                Constants.Platform.AOS_LOCAL -> {
                    val rect: Rectangle = el.rect
                    when (dir) {
                        Direction.LEFT -> {
                            pointOptionStart = point(
                                rect.x + rect.width - edgeBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + edgeBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        Direction.RIGHT -> {
                            pointOptionStart = point(
                                rect.x + edgeBorder,
                                rect.y + rect.height / 2
                            )
                            pointOptionEnd = point(
                                rect.x + rect.width - edgeBorder,
                                rect.y + rect.height / 2
                            )
                        }

                        else -> throw IllegalArgumentException("swipeElementAndroid(): dir: '$dir' NOT supported")
                    }
                    // execute swipe using TouchAction
                    try {
                        TouchAction(driver)
                            .press(pointOptionStart) // a bit more reliable when we add small wait
                            .waitAction(waitOptions(Duration.ofMillis(PRESS_TIME.toLong())))
                            .moveTo(pointOptionEnd)
                            .release().perform()
                    } catch (e: Exception) {
                        System.err.println("""swipeElementAndroid(): TouchAction FAILED ${e.message} """.trimIndent())
                        return
                    }
                    // always allow swipe action to complete
                    try {
                        Thread.sleep(ANIMATION_TIME.toLong())
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
            }
        }
    }

    fun scrollPo() {
//        applicationManager.getDriver().findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"WebView\").instance(0))").click();
        applicationManager.getDriver()
            .findElement(MobileBy.AndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"WebView\").instance(0))"))
    }

}
