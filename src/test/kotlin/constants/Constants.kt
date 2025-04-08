package constants

class Constants {

    object RunVariables {
        var PLATFORM = Platform.AOS_LOCAL
    }

    enum class Platform {
        IOS, AOS, IOS_LOCAL, AOS_LOCAL
    }

    object VRT {
        const val URL = "http://localhost:4200"
        const val API_KEY = "5GYYT429F0MEC2MNZ7R0BR7QJFHB"
        const val AOS_BRANCH_NAME = "master"
        const val AOS_PROJECT = "Default project"
        const val IOS_BRANCH_NAME = "master"
        const val IOS_PROJECT = "Default project"
    }

    object WireMock {
        const val CUSTOM_URL = "192.168.0.1"
        const val PORT = 8080
    }

    object COREZOID {
        const val COREZOID_API_LOGIN = "99999"
        const val COREZOID_API_SECRET = "d4BpPjGGemr4C6vtVCmY3ga6aoxE2lYekbtIgKcE"
        const val COREZOID_API_URL = "https://test.com.ua/api/2/json"
        const val COREZOID_CONV_MY = 170885 //Мой тестовый конвеер
       }


}
