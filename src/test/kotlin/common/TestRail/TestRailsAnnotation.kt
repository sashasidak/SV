package common.TestRail


import kotlin.annotation.Retention
import kotlin.annotation.Target

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION) // Применяется к функциям
annotation class TestRails(val id: String = "none")


