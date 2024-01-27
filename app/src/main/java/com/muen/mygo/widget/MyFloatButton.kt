package com.muen.mygo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.getbase.floatingactionbutton.FloatingActionButton
import kotlin.math.abs


class MyFloatButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
)  : FloatingActionButton(context, attrs, defStyleAttr),View.OnTouchListener {

    private val CLICK_DRAG_TOLERANCE = 10f
    private var downRawX = 0f
    private  var downRawY = 0f
    private var dX = 0f
    private  var dY = 0f
    init {
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val layoutParams = layoutParams as (ViewGroup.MarginLayoutParams)
        val action: Int = event?.action!!
        return if (action == MotionEvent.ACTION_DOWN) {
            downRawX = event.rawX
            downRawY = event.rawY
            dX = x - downRawX
            dY = y - downRawY
            true
        } else if (action == MotionEvent.ACTION_MOVE) {
            val viewWidth: Int = width
            val viewHeight: Int = height
            val viewParent = parent as View
            val parentWidth = viewParent.width
            val parentHeight = viewParent.height
            var newX: Float = event.rawX + dX
            newX = layoutParams.leftMargin.toFloat().coerceAtLeast(newX)
            newX = (parentWidth - viewWidth - layoutParams.rightMargin).toFloat().coerceAtMost(newX)

            var newY: Float = event.rawY + dY
            newY = layoutParams.topMargin.toFloat().coerceAtLeast(newY)
            newY = (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat().coerceAtMost(newY)
            animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()
            true
        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX: Float = event.rawX
            val upRawY: Float = event.rawY
            val upDX: Float = upRawX - downRawX
            val upDY: Float = upRawY - downRawY
            if (abs(upDX) < CLICK_DRAG_TOLERANCE && abs(upDY) < CLICK_DRAG_TOLERANCE) {
                performClick()
            } else {
                true
            }
        } else {
            super.onTouchEvent(event)
        }
    }
}