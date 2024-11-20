package com.common.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.DrawableCompat

/**
 * 让弹窗背景变暗，主要用于{@link android.widget.PopupWindow}
 */
private const val DIM_ANIMATION_DURATION = 500

private const val TAG_ANIMATOR = 1000
private const val TAG_OVERLAY = 1001

fun applyDim(activity: Activity, @FloatRange(from = 0.0, to = 1.0) value: Float, animate: Boolean) {
    applyDim(activity.window.decorView.rootView as ViewGroup, value, animate)
}

fun applyDim(
    rootView: ViewGroup,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    animate: Boolean
) {
    applyDim(rootView, value, animate, false)
}

/**
 * 让弹窗背景变暗
 *
 * @param rootView
 * @param value
 * @param animate 是否使用动画
 * @param clearWhenFinish 动画结束之后是否清空overlay
 */
private fun applyDim(
    rootView: ViewGroup, @FloatRange(from = 0.0, to = 1.0) value: Float,
    animate: Boolean,
    clearWhenFinish: Boolean
) {
    if (animate) {
        if (rootView.getTag(TAG_ANIMATOR) == null) {
            val drawable = rootView.getTag(TAG_OVERLAY) as Drawable?
            val animator = ValueAnimator.ofInt(
                if (drawable == null) 0 else
                    DrawableCompat.getAlpha(drawable),
                (value * 255).toInt()
            )
            animator.duration = DIM_ANIMATION_DURATION.toLong()
            animator.addUpdateListener { animation ->
                applyDim(
                    rootView,
                    animation.animatedValue as Int
                )
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    rootView.setTag(TAG_ANIMATOR, null)
                    if (clearWhenFinish) {
                        clearDim(rootView, false)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }

            })
            animator.start()
        }
    } else {
        applyDim(rootView, (value * 255).toInt())
    }
}

private fun applyDim(@NonNull parent: ViewGroup, alpha: Int) {
    var drawable = parent.getTag(TAG_OVERLAY) as Drawable?
    if (drawable == null) {
        drawable = ColorDrawable(Color.BLACK)
        drawable.setBounds(0, 0, parent.width, parent.height)
        parent.setTag(TAG_OVERLAY, drawable)
        parent.overlay.add(drawable)
    }
    drawable.alpha = alpha
}

fun clearDim(@NonNull activity: Activity, animate: Boolean) {
    clearDim(activity.window.decorView.rootView as ViewGroup, animate)
}

/**
 * 清空变暗背景
 *
 * @param parent
 * @param animate 是否使用动画
 */
fun clearDim(@NonNull parent: ViewGroup, animate: Boolean) {
    if (animate) {
        applyDim(parent, 0f, animate = true, clearWhenFinish = true)
    } else {
        val drawable = parent.getTag(TAG_OVERLAY) as Drawable
        val overlay = parent.overlay
        overlay.remove(drawable)
        parent.setTag(TAG_OVERLAY, null)
        parent.setTag(TAG_ANIMATOR, null)
    }
}