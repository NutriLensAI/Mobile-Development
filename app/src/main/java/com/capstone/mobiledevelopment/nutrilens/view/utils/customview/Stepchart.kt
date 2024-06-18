package com.capstone.mobiledevelopment.nutrilens.view.utils.customview

import android.content.Context
import com.capstone.mobiledevelopment.nutrilens.data.database.step.StepCount
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.chart
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.line
import io.data2viz.charts.chart.quantitative
import io.data2viz.geom.Size
import io.data2viz.viz.VizContainerView

class StepChart(context: Context, stepData: List<StepCount>) : VizContainerView(context) {

    private val chart: Chart<StepCount> = chart(stepData) {
        size = Size(800.0, 800.0)
        title = "Weekly Step Count"

        // Create a discrete dimension for the formatted month
        val month = discrete({ domain.formattedMonth })

        // Create a continuous numeric dimension for the step count
        val steps = quantitative({ domain.stepCount.toDouble() }) {
            name = "Steps Taken"
        }

        // Using a discrete dimension for the X-axis and a continuous one for the Y-axis
        line(month, steps) {
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        chart.size = Size(800.0, 800.0 * h / w)
    }
}
