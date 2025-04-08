package common.helpers

import org.json.JSONObject
import org.json.JSONTokener
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.util.*

object DataReader {

    fun getValue(key: String): String {
        val fileName = FileInputStream("src/test/kotlin/constants/devices.props")
        val props = Properties()
        val `is`: InputStream?

        try {
            `is` = fileName
            props.load(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var data = props.getProperty(key)

        try {
            data = String(data.toByteArray(charset("ISO8859-1")))

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return data
    }
    fun getValueAsInt(key: String): Int {

        val fileName = FileInputStream("src/test/kotlin/constants/devices.props")
        val props = Properties()
        val `is`: InputStream?

        try {
            `is` = fileName
            props.load(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var data = props.getProperty(key)

        try {
            data = String(data.toByteArray(charset("ISO8859-1")))

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return data.toInt()
    }

    fun getJsonFromFile(file: String): JSONObject {
        val fileName = FileInputStream("src/test/resources/stubs/$file")
        return JSONObject(JSONTokener(fileName))
    }

}