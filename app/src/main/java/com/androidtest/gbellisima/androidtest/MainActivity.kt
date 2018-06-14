package com.androidtest.gbellisima.androidtest

import com.google.android.gms.ads.AdListener
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd


import kotlinx.android.synthetic.main.activity_main.*

const val GAME_LENGTH_MILLISECONDS: Long = 3000

// Remove the line below after defining your own ad unit ID.
private const val TOAST_TEXT = "Test ads are being shown. " +
        "To show live ads, replace the ad unit ID in res/values/strings.xml " +
        "with your own ad unit ID."
private const val START_LEVEL = 1

class MainActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: PublisherInterstitialAd
    private var mCountDownTimer: CountDownTimer? = null
    private var mGameIsInProgress: Boolean = false
    private var mAdIsLoading: Boolean = false
    private var mTimerMilliseconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = PublisherInterstitialAd(this)
        // Replace with your own ad unit id.
        mInterstitialAd.adUnitId = "/6499/example/interstitial"

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                startGame()
            }

            override fun onAdLoaded() {
                mAdIsLoading = false
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                mAdIsLoading = false
            }
        }
        // Create the "retry" button, which tries to show an interstitial between game plays.
        next_level_button.visibility = View.INVISIBLE
        next_level_button.setOnClickListener { showInterstitial() }

        startGame()
    }

    private fun createTimer(milliseconds: Long) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        mCountDownTimer?.cancel()

        mCountDownTimer = object : CountDownTimer(milliseconds, 50) {
            override fun onTick(millisUntilFinished: Long) {
                mTimerMilliseconds = millisUntilFinished
                level.text = getString(R.string.status_bar_notification_info_overflow, (millisUntilFinished/ 1000 + 1))
            }

            override fun onFinish() {
                mGameIsInProgress = false
                level.setText(R.string.abc_action_mode_done)
                next_level_button.visibility = View.VISIBLE
            }
        }
    }

    public override fun onResume() {
        // Start or resume the game.
        super.onResume()

        if (mGameIsInProgress) {
            resumeGame(mTimerMilliseconds)
        }
    }

    public override fun onPause() {
        // Cancel the timer if the game is paused.
        mCountDownTimer?.cancel()
        super.onPause()
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd?.isLoaded == true) {
            mInterstitialAd?.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            startGame()
        }
    }

    private fun startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mAdIsLoading && !mInterstitialAd.isLoaded) {
            mAdIsLoading = true
            val adRequest = PublisherAdRequest.Builder().build()
            mInterstitialAd.loadAd(adRequest)
        }

        next_level_button.visibility = View.INVISIBLE
        resumeGame(GAME_LENGTH_MILLISECONDS)
    }

    private fun resumeGame(milliseconds: Long) {
        // Create a new timer for the correct length and start it.
        mGameIsInProgress = true
        mTimerMilliseconds = milliseconds
        createTimer(milliseconds)
        mCountDownTimer?.start()
    }
}
