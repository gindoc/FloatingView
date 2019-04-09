package com.gindoc.floatingview

import android.content.Context
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView

/**
 * Author: GIndoc on 2019/4/8.
 * FOR   :
 */
class FloatingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    private var startX = 0f                     // 手指按下的点的横坐标
    private var startY = 0f
    private var tanslateX = 0f                  // 最终停留的位置的横坐标
    private var tanslateY = 0f

    private var leftBoundary: Int = 0           // 控件移动的边界
    private var topBoundary: Int
    private var rightBoundary: Int
    private var bottomBoundary: Int

    @ORIENTATION
    var orientation = HORIZONTAL                // 控件悬停的行为(水平靠边、垂直靠边，总是在左边、右边、上边、下边悬停)
    private var isPositive = true               // 用于orientation为VERTICAL、HORIZONTAL时，是正向移动还是反向移动

    var hasAnim = true                          // 是否使用动画
    var duration: Long = 500                    // 动画时间
    private var isAnimPlaying = false           // 动画是否正在进行中

    init {
        val screenSize = WindowUtils.getScreenWidthAndHeight(context)
        rightBoundary = screenSize[0]
        bottomBoundary = screenSize[1]
        topBoundary = WindowUtils.getStatusHeight(context)
    }

    /**
     * 设置边界
     */
    fun setBoundary(left: Int, top: Int, right: Int, bottom: Int) {
        leftBoundary = left
        topBoundary = top
        rightBoundary = right
        bottomBoundary = bottom
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                if (isAnimPlaying) return super.onTouchEvent(event)
                startX = event.x
                startY = event.y
            } else if (it.action == MotionEvent.ACTION_MOVE) {
                val offsetX = event.x - startX
                val offsetY = event.y - startY
                var l = left + offsetX
                var t = top + offsetY
                var r = right + offsetX
                var b = bottom + offsetY

                if (l < leftBoundary) {
                    l = leftBoundary.toFloat()
                    r = l + width
                }
                if (r > rightBoundary) {
                    r = rightBoundary.toFloat()
                    l = r - width
                }
                if (t < topBoundary) {
                    t = topBoundary.toFloat()
                    b = t + height
                }
                if (b > bottomBoundary) {
                    b = bottomBoundary.toFloat()
                    t = b - height
                }
                layout(l.toInt(), t.toInt(), r.toInt(), b.toInt())
            } else if (event.action == MotionEvent.ACTION_UP) {
                when (orientation) {
                    HORIZONTAL -> {
                        tanslateX = if (right - width / 2 < (rightBoundary + leftBoundary) / 2) {
                            isPositive = false
                            leftBoundary.toFloat() - left
                        } else {
                            isPositive = true
                            rightBoundary.toFloat() - right
                        }
                    }
                    VERTICAL -> {
                        tanslateY = if (bottom - height / 2 < (bottomBoundary + topBoundary) / 2) {
                            isPositive = false
                            topBoundary.toFloat() - top
                        } else {
                            isPositive = true
                            bottomBoundary.toFloat() - bottom
                        }
                    }
                    LEFT -> {
                        tanslateX = leftBoundary.toFloat() - left
                    }
                    RIGHT -> {
                        tanslateX = rightBoundary.toFloat() - right
                    }
                    TOP -> {
                        tanslateY = topBoundary.toFloat() - top
                    }
                    BOTTOM -> {
                        tanslateY = bottomBoundary.toFloat() - bottom
                    }
                }
                if (hasAnim) {
                    startAnim()
                } else {
                    layoutAfterAnim()
                }
            }
        }
        return true
    }

    /**
     * 在view动画结束后更新控件位置
     */
    private fun layoutAfterAnim() {
        when (orientation) {
            HORIZONTAL -> {
                if (isPositive) {
                    layout(rightBoundary - width, top, rightBoundary, bottom)
                } else {
                    layout(leftBoundary, top, leftBoundary + width, bottom)
                }
            }
            VERTICAL -> {
                if (isPositive) {
                    layout(left, bottomBoundary - height, right, bottomBoundary)
                } else {
                    layout(left, topBoundary, right, topBoundary + height)
                }
            }
            LEFT -> {
                layout(leftBoundary, top, leftBoundary + width, bottom)
            }
            RIGHT -> {
                layout(rightBoundary - width, top, rightBoundary, bottom)
            }
            TOP -> {
                layout(left, topBoundary, right, topBoundary + height)
            }
            BOTTOM -> {
                layout(left, bottomBoundary - height, right, bottomBoundary)
            }
        }
    }

    private fun startAnim() {
        if (tanslateX == 0f && tanslateY == 0f) return
        val animation = TranslateAnimation(0f, tanslateX, 0f, tanslateY)
        animation.duration = duration
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                clearAnimation()
                layoutAfterAnim()
                isAnimPlaying = false
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        startAnimation(animation)
        isAnimPlaying = true
    }

    @IntDef(VERTICAL, HORIZONTAL, LEFT, RIGHT, TOP, BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    @MustBeDocumented
    annotation class ORIENTATION

    companion object {
        const val VERTICAL = 0
        const val HORIZONTAL = 1
        const val LEFT = 2
        const val RIGHT = 3
        const val TOP = 4
        const val BOTTOM = 5
    }


}