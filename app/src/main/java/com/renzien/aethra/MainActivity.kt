package com.renzien.aethra

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.renzien.aethra.ui.theme.AethraTheme

class MainActivity : ComponentActivity() {

    private var appReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Splash menunggu sampai halaman pertama siap.
        splashScreen.setKeepOnScreenCondition {
            !appReady
        }

        splashScreen.setOnExitAnimationListener { provider ->
            if (!ValueAnimator.areAnimatorsEnabled()) {
                provider.remove()
            } else {
                val icon = provider.iconView

                val lift = 8f * resources.displayMetrics.density

                var removed = false

                fun removeSplash() {
                    if (!removed) {
                        removed = true
                        provider.remove()
                    }
                }

                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            provider.view,
                            View.ALPHA,
                            provider.view.alpha,
                            0f
                        ),

                        ObjectAnimator.ofFloat(
                            icon,
                            View.SCALE_X,
                            icon.scaleX,
                            icon.scaleX * 1.12f
                        ),

                        ObjectAnimator.ofFloat(
                            icon,
                            View.SCALE_Y,
                            icon.scaleY,
                            icon.scaleY * 1.12f
                        ),

                        ObjectAnimator.ofFloat(
                            icon,
                            View.TRANSLATION_Y,
                            icon.translationY,
                            icon.translationY - lift
                        )
                    )

                    duration = 280L

                    interpolator = DecelerateInterpolator()

                    addListener(
                        object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(
                                animation: Animator
                            ) {
                                removeSplash()
                            }

                            override fun onAnimationCancel(
                                animation: Animator
                            ) {
                                removeSplash()
                            }
                        }
                    )

                    start()
                }
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            )
        )

        setContent {
            AethraTheme {
                AethraApp(
                    onReadyToDraw = {
                        appReady = true
                    }
                )
            }
        }
    }
}