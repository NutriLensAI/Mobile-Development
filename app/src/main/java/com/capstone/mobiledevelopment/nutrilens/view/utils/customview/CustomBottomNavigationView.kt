package com.capstone.mobiledevelopment.nutrilens.view.utils.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.capstone.mobiledevelopment.nutrilens.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private var mPath: Path = Path()
    private var mPaint: Paint = Paint()

    // The CURVE_CIRCLE_RADIUS represents the radius of the fab button
    private val CURVE_CIRCLE_RADIUS = 128 / 2

    // The coordinates of the first curve
    private val mFirstCurveStartPoint = Point()
    private val mFirstCurveEndPoint = Point()
    private val mFirstCurveControlPoint1 = Point()
    private val mFirstCurveControlPoint2 = Point()

    // The coordinates of the second curve
    private val mSecondCurveStartPoint = Point()
    private val mSecondCurveEndPoint = Point()
    private val mSecondCurveControlPoint1 = Point()
    private val mSecondCurveControlPoint2 = Point()
    private var mNavigationBarWidth = 0
    private var mNavigationBarHeight = 0

    init {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = ContextCompat.getColor(context, R.color.white)
        setBackgroundColor(Color.TRANSPARENT)

//        // Set the shadow layer for the paint
//        mPaint.setShadowLayer(10f, 0f, -5f, Color.GRAY) // Adjust the values as needed
//        setLayerType(LAYER_TYPE_SOFTWARE, mPaint) // Enable software rendering to allow shadow layer
    }

    override fun getMaxItemCount(): Int {
        return 5 // Allow 5 items including the FAB placeholder
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Get width and height of navigation bar
        mNavigationBarWidth = width
        mNavigationBarHeight = height
        // The coordinates (x,y) of the start point before curve
        mFirstCurveStartPoint.set(
            (mNavigationBarWidth / 2) - (CURVE_CIRCLE_RADIUS * 2) - (CURVE_CIRCLE_RADIUS / 3),
            0
        )
        // The coordinates (x,y) of the end point after curve
        mFirstCurveEndPoint.set(
            mNavigationBarWidth / 2,
            CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4)
        )
        // Same thing for the second curve
        mSecondCurveStartPoint.set(mFirstCurveEndPoint.x, mFirstCurveEndPoint.y)
        mSecondCurveEndPoint.set(
            (mNavigationBarWidth / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3),
            0
        )

        // The coordinates (x,y) of the 1st control point on a cubic curve
        mFirstCurveControlPoint1.set(
            mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4),
            mFirstCurveStartPoint.y
        )
        // The coordinates (x,y) of the 2nd control point on a cubic curve
        mFirstCurveControlPoint2.set(
            mFirstCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2) + CURVE_CIRCLE_RADIUS,
            mFirstCurveEndPoint.y
        )

        mSecondCurveControlPoint1.set(
            mSecondCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2) - CURVE_CIRCLE_RADIUS,
            mSecondCurveStartPoint.y
        )
        mSecondCurveControlPoint2.set(
            mSecondCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 4)),
            mSecondCurveEndPoint.y
        )

        mPath.reset()
        mPath.moveTo(0f, 0f)
        mPath.lineTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat())

        mPath.cubicTo(
            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
        )

        mPath.cubicTo(
            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
        )

        mPath.lineTo(mNavigationBarWidth.toFloat(), 0f)
        mPath.lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
        mPath.lineTo(0f, mNavigationBarHeight.toFloat())
        mPath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPath, mPaint)

        // Additional logic to enlarge and raise the selected item
        val selectedItem = menu.findItem(selectedItemId)
        val icon = selectedItem.icon

        if (icon != null) {
            // Change color to teal_200 if the item is selected
            icon.setTint(ContextCompat.getColor(context, R.color.teal_200))

            val bounds = icon.bounds

            // Draw the selected item larger and raised
            val selectedIconSize = (bounds.width() * 1.5).toInt()
            val selectedIconHeight = (bounds.height() * 1.5).toInt()
            val left = bounds.left - (selectedIconSize - bounds.width()) / 2
            val top =
                bounds.top - (selectedIconHeight - bounds.height()) / 2 - 20 // raised by 20 pixels
            val right = bounds.right + (selectedIconSize - bounds.width()) / 2
            val bottom = bounds.bottom + (selectedIconHeight - bounds.height()) / 2 - 20

            icon.setBounds(left, top, right, bottom)
            icon.draw(canvas)
        }
    }
}
