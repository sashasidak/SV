package common.helpers

import common.ApplicationManager
import sсreens.Onboarding

open class AuthHelper(manager: ApplicationManager) : ApplicationManager() {
    var manager: ApplicationManager

    init {
        this.manager = manager
    }

    fun fullAuth(): Onboarding {
        manager.getScreen<Onboarding>()
        return manager.getScreen()
    }

}