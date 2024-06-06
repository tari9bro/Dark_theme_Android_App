package com.phstudio.darktheme

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.phstudio.darktheme.R.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    private val retryAttempt = 0
    private var adView: MaxAdView? = null
    var weakActivity: WeakReference<MainActivity>? = null
    private var interstitialAd: MaxInterstitialAd? = null
    private var retry = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        val nav = findViewById<BottomNavigationView>(id.bnMain)
        val navController = findNavController(this, id.fcMain)
        nav.setupWithNavController(navController)
        // noInternetDialog();
        weakActivity = WeakReference<MainActivity>(this@MainActivity)

        //The key argument here must match that used in the other activity
        LoadBannerAd()
        LoadInterstitialAd()
    }
    fun playInterstitial() {
        if (interstitialAd!!.isReady) {
            interstitialAd!!.showAd()
        }
    }

    private fun LoadInterstitialAd() {
        interstitialAd = MaxInterstitialAd(resources.getString(string.interstitial), this)
        val adListener: MaxAdListener = object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {}
            override fun onAdDisplayed(ad: MaxAd) {}
            override fun onAdHidden(ad: MaxAd) {}
            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                retry++
                val delay =
                    TimeUnit.SECONDS.toMillis(Math.pow(2.0, Math.min(6, retry).toDouble()).toLong())
                Handler().postDelayed({ interstitialAd!!.loadAd() }, delay)
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        }
        interstitialAd!!.setListener(adListener)
        interstitialAd!!.loadAd()
    }

    private fun LoadBannerAd() {
        adView = MaxAdView(resources.getString(string.banner), this)
        adView!!.setListener(object : MaxAdViewAdListener {
            override fun onAdExpanded(ad: MaxAd) {}
            override fun onAdCollapsed(ad: MaxAd) {}
            override fun onAdLoaded(ad: MaxAd) {}
            override fun onAdDisplayed(ad: MaxAd) {}
            override fun onAdHidden(ad: MaxAd) {}
            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
        })
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = resources.getDimensionPixelSize(dimen.banner_height)
        adView!!.layoutParams = FrameLayout.LayoutParams(width, height, Gravity.BOTTOM)
        adView!!.setBackgroundColor(Color.WHITE)
        val layout: LinearLayout = findViewById(id.adLayout)
        layout.addView(adView)
        adView!!.loadAd()
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setIcon(mipmap.ic_launcher_round)
        dialogBuilder.setMessage(resources.getString(string.close_app))
            .setCancelable(false)
            .setPositiveButton(
                resources.getString(string.Yes)
            ) { _, _ ->
                finishAffinity()
            }
            .setNegativeButton(
                resources.getString(string.No)
            ) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(string.Exit_app))
        alert.show()
    }
}