package com.example.omdbtest.ui


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.omdbtest.R


class RoundedView: FrameLayout {

    /**
     * The corners than can be changed
     */
    private var topLeftCornerRadius = 0f
    private var topRightCornerRadius = 0f
    private var bottomLeftCornerRadius = 0f
    private var bottomRightCornerRadius = 0f

    constructor(@NonNull context: Context) : super(context) {
        initialize(context, null, 0)
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs, 0)
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs, defStyleAttr)
    }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedView, 0, 0
        )

        //get the default value form the attrs
        topLeftCornerRadius =
            typedArray.getDimension(R.styleable.RoundedView_topLeftCornerRadius, 0f)
        topRightCornerRadius =
            typedArray.getDimension(R.styleable.RoundedView_topRightCornerRadius, 0f)
        bottomLeftCornerRadius =
            typedArray.getDimension(R.styleable.RoundedView_bottomLeftCornerRadius, 0f)
        bottomRightCornerRadius =
            typedArray.getDimension(R.styleable.RoundedView_bottomRightCornerRadius, 0f)
        typedArray.recycle()
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val count: Int = canvas.save()
        val path = Path()
        val cornerDimensions = floatArrayOf(
            topLeftCornerRadius, topLeftCornerRadius,
            topRightCornerRadius, topRightCornerRadius,
            bottomRightCornerRadius, bottomRightCornerRadius,
            bottomLeftCornerRadius, bottomLeftCornerRadius
        )
        path.addRoundRect(
            RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()), cornerDimensions, Path.Direction.CW
        )
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(count)
    }
}