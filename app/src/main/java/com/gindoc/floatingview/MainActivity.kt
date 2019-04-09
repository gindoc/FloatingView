package com.gindoc.floatingview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addRedPacket()
    }

    private fun addRedPacket() {
        val windowSize = WindowUtils.getScreenWidthAndHeight(this)

        val redPacketView = FloatingView(this)
        redPacketView.setImageResource(R.drawable.red_packet)
        // 边界自己根据实际情况设定，这里的bottom设为屏幕高度减去状态栏高度
        redPacketView.setBoundary(0, 0, windowSize[0], windowSize[1]-WindowUtils.getStatusHeight(this))

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = windowSize[1] / 2
        params.gravity = Gravity.END
        window.decorView.findViewById<ViewGroup>(android.R.id.content).addView(redPacketView, params)
    }
}
