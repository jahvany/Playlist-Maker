package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    private var isPlaying = false

    private val iconRect = RectF()

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView
        )

        playDrawable = typedArray.getDrawable(
            R.styleable.PlaybackButtonView_playIcon
        )

        pauseDrawable = typedArray.getDrawable(
            R.styleable.PlaybackButtonView_pauseIcon
        )

        typedArray.recycle()

        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        iconRect.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (w - paddingRight).toFloat(),
            (h - paddingBottom).toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val drawable = if (isPlaying) pauseDrawable else playDrawable

        drawable?.let {
            it.bounds = Rect(
                iconRect.left.toInt(),
                iconRect.top.toInt(),
                iconRect.right.toInt(),
                iconRect.bottom.toInt()
            )
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                toggleState()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        invalidate()
    }

    private fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }
}