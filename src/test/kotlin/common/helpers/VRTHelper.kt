package common.helpers

import com.codeborne.selenide.WebDriverRunner
import common.helpers.DataReader.getValue
import constants.Constants
import io.visual_regression_tracker.sdk_java.TestRunOptions
import io.visual_regression_tracker.sdk_java.VisualRegressionTracker
import io.visual_regression_tracker.sdk_java.VisualRegressionTrackerConfig
import org.openqa.selenium.Dimension
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import tests.base.applicationManager
import java.io.IOException

class VRTHelper() {

    var isStarted: Boolean = false

    private var config =
            when (Constants.RunVariables.PLATFORM) {
                Constants.Platform.IOS -> {
                    VisualRegressionTrackerConfig(
                            Constants.VRT.URL,
                            Constants.VRT.IOS_PROJECT,
                            Constants.VRT.API_KEY,
                            Constants.VRT.IOS_BRANCH_NAME,
                            false,
                            System.currentTimeMillis().toString(),
                            15
                    )
             }
             Constants.Platform.AOS -> {
                 VisualRegressionTrackerConfig(
                         Constants.VRT.URL,
                         Constants.VRT.AOS_PROJECT,
                         Constants.VRT.API_KEY,
                         Constants.VRT.AOS_BRANCH_NAME,
                         false,
                         System.currentTimeMillis().toString(),
                         15
                 )
             }
                Constants.Platform.IOS_LOCAL -> {
                    VisualRegressionTrackerConfig(
                        Constants.VRT.URL,
                        Constants.VRT.IOS_PROJECT,
                        Constants.VRT.API_KEY,
                        Constants.VRT.IOS_BRANCH_NAME,
                        false,
                        System.currentTimeMillis().toString(),
                        15
                    )
                }
                Constants.Platform.AOS_LOCAL -> {
                    VisualRegressionTrackerConfig(
                        Constants.VRT.URL,
                        Constants.VRT.AOS_PROJECT,
                        Constants.VRT.API_KEY,
                        Constants.VRT.AOS_BRANCH_NAME,
                        false,
                        System.currentTimeMillis().toString(),
                        15
                    )
                }
            }

    var visualRegressionTracker = VisualRegressionTracker(config)

    fun start() {
        visualRegressionTracker.start()
        isStarted = true
    }

    fun stop() {
        if (isStarted) {
            visualRegressionTracker.stop()
        }
    }

    fun track(name: String) {
        if (!isStarted) {
            start()
        }
        val dimensions: Dimension = applicationManager.getDriver().manage().window().getSize()
        val screenWidth: Int = dimensions.getWidth()
        val screenHeight: Int = dimensions.getHeight()
        val demensionString =screenWidth.toString() + "x" + screenHeight.toString()
        var deviceName = ""
        var osVersion = ""
        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> deviceName = getValue("IOS_DEVICE_NAME")
            Constants.Platform.AOS -> deviceName = getValue("AOS_DEVICE_NAME")
            Constants.Platform.IOS_LOCAL -> deviceName = getValue("IOS_DEVICE_NAME")
            Constants.Platform.AOS_LOCAL -> deviceName = getValue("AOS_DEVICE_NAME")
        }

        when (Constants.RunVariables.PLATFORM) {
            Constants.Platform.IOS -> osVersion = getValue("IOS_PLATFORM_VERSION")
            Constants.Platform.AOS -> osVersion = getValue("AOS_PLATFORM_VERSION")
            Constants.Platform.IOS_LOCAL -> osVersion = getValue("IOS_PLATFORM_VERSION")
            Constants.Platform.AOS_LOCAL -> osVersion = getValue("AOS_PLATFORM_VERSION")
        }
        try {
            visualRegressionTracker.track(name, (WebDriverRunner.getWebDriver() as TakesScreenshot).getScreenshotAs(OutputType.BASE64),
                TestRunOptions.builder()
                    .os(Constants.RunVariables.PLATFORM.toString() + " " +osVersion)
                    .device(deviceName)
                    .viewport(demensionString)
                    .diffTollerancePercent(3.0f)
                    .build()
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}