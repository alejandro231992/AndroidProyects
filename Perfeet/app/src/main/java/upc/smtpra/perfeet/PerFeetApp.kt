package upc.smtpra.perfeet

import android.R
import android.app.Dialog
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.MobileAds
import com.onesignal.OneSignal
import io.github.inflationx.calligraphy3.*
import io.github.inflationx.viewpump.ViewPump
import okhttp3.OkHttpClient
import upc.smtpra.perfeet.network.RestApis
import upc.smtpra.perfeet.utils.Constants
import upc.smtpra.perfeet.utils.Constants.SharedPref.LANGUAGE
import upc.smtpra.perfeet.utils.LocaleManager
import upc.smtpra.perfeet.utils.SharedPrefUtils
import upc.smtpra.perfeet.utils.extensions.getSharedPrefInstance


class PerFeetApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        getSharedPrefInstance().apply {
            appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            language = getStringValue(LANGUAGE,"es")
        }

        // Set Custom Font
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Montserrat-Regular.ttf")
                            .setFontAttrId(R.attr.fontStyle)
                            .build()
                    )
                )
                .build()
        )

        /*CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_regular)).setFontAttrId(
                        R.attr.fontPath
                ).build()
        )*/

        MobileAds.initialize(this) {}

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
        OneSignal.clearOneSignalNotifications()
    }


    fun enableNotification(isEnabled: Boolean) {
        OneSignal.setSubscription(isEnabled)
    }

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var localeManager: LocaleManager
        private lateinit var appInstance: PerFeetApp
        var restApis: RestApis? = null
        var okHttpClient: OkHttpClient? = null
        var sharedPrefUtils: SharedPrefUtils? = null
        var noInternetDialog: Dialog? = null
        lateinit var language: String
        var appTheme: Int = 0

        fun getAppInstance(): PerFeetApp {
            return appInstance
        }

        fun changeAppTheme(isDark: Boolean) {
            getSharedPrefInstance().apply {
                when {
                    isDark -> setValue(Constants.SharedPref.THEME, Constants.THEME.DARK)
                    else -> setValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
                }
                appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            }

        }

        fun changeLanguage(aLanguage: String) {
            getSharedPrefInstance().setValue(LANGUAGE, aLanguage)
            language = aLanguage
        }


    }
}
