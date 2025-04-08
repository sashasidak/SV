package tests.onBoarding

import common.TestRail.TestRails
import common.helpers.AuthHelper
import common.helpers.CorezoidSendBlock
import org.testng.annotations.Test
import tests.base.BaseTest
import tests.base.applicationManager
import tests.base.vrtHelper
import tests.base.wireMockHelper

class OnboardingTest : BaseTest() {

    @Test
    @TestRails(id = "3")
    @CorezoidSendBlock(Number = "111", type = "222", amount = "333")
    fun sendAssetToAnotherWallet() {
        wireMockHelper.stubAction(200,"create","/start/success.json")
        applicationManager.getHelper<AuthHelper>()
            .fullAuth()
            .clickOn()
        vrtHelper.track("Первый скрин")
    }
}

