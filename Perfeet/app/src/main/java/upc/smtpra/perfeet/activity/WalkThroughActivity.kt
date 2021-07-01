package upc.smtpra.perfeet.activity

import android.os.Bundle
import upc.smtpra.perfeet.AppBaseActivity
import upc.smtpra.perfeet.R
import upc.smtpra.perfeet.adapter.WalkAdapter
import upc.smtpra.perfeet.utils.CarouselEffectTransformer
import upc.smtpra.perfeet.utils.Constants.SharedPref.SHOW_SWIPE
import upc.smtpra.perfeet.utils.extensions.*
import kotlinx.android.synthetic.main.activity_walk_through.*

class WalkThroughActivity : AppBaseActivity() {
    private var mCount: Int? = null
    private var mHeading = arrayOf("Hola, somos PerFeet!", "Los mejores estilos!", "Compra hasta el cansancio!")
    private val mSubHeading = arrayOf("Hacemos que recorrer tu ciudad sea fácil\n y cómodo.", "Compra las mejores zapatillas en esta aplicación pensado en ti.", "Hazte con las zapatillas más vendidas a precios de oferta.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)

        init()
        val adapter = WalkAdapter()

        ViewPager.adapter = adapter

        dots.attachViewPager(ViewPager)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        mCount = adapter.count

        btnStatShopping.onClick {
            getSharedPrefInstance().setValue(SHOW_SWIPE, true)
            //launchActivity<SignInUpActivity>()
            launchActivityWithNewTask<DashBoardActivity>()
        }
        llSignIn.onClick {
            getSharedPrefInstance().setValue(SHOW_SWIPE, true)
            launchActivity<SignInUpActivity>()
        }
    }

    private fun init() {
        ViewPager.apply {
            clipChildren = false
            pageMargin = resources.getDimensionPixelOffset(R.dimen.spacing_small)
            offscreenPageLimit = 3
            setPageTransformer(false, CarouselEffectTransformer(this@WalkThroughActivity))
            offscreenPageLimit = 0

            onPageSelected { position: Int ->
                val animFadeIn = android.view.animation.AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                tvHeading.startAnimation(animFadeIn)
                tvSubHeading.startAnimation(animFadeIn)
                tvHeading.text = mHeading[position]
                tvSubHeading.text = mSubHeading[position]
            }
        }
    }
}