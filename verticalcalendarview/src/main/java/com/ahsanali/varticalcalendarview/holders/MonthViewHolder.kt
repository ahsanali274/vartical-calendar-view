package com.ahsanali.varticalcalendarview.holders

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.ahsanali.varticalcalendarview.R
import com.ahsanali.varticalcalendarview.VerticalCalendarView
import java.util.*

class MonthViewHolder(
    itemView: View, var weekRowsCount: Int,
    private val attrs: VerticalCalendarView.Attributes,
    private val mOnDayClickListener: VerticalCalendarView.OnDayClickListener
) : RecyclerView.ViewHolder(itemView) {

    private val mContext: Context
    private val weeks_container: LinearLayout
    var label_month: TextView
    var weeksColumns: ArrayList<Array<WeekDayView?>>
    var mMonth: Int = 0
    var mYear: Int = 0

    init {
        (itemView.layoutParams as RecyclerView.LayoutParams).setMargins(
            0, 0, 0,
            attrs.monthDividerSize
        )

        mContext = itemView.context
        label_month = itemView.findViewById(R.id.label_month)
        label_month.layoutParams.height = attrs.monthLabelHeight
        label_month.setTextAppearance(mContext, attrs.monthTextAppearanceId)

        weeks_container = itemView.findViewById(R.id.weeks_container)
        weeksColumns = ArrayList()
        val weekDayNames: LinearLayout = itemView.findViewById(R.id.label_days)
        weekDayNames.layoutParams.height = attrs.weekdayHeight

        for (i in 0 until weekDayNames.childCount) {
            weekDayNames.getChildAt(i).layoutParams.width = attrs.dayWidth

            (weekDayNames.getChildAt(i) as TextView).setTextAppearance(mContext, attrs.weekDayTextAppearanceId)
        }
    }

    fun generateWeekRows() {
        var linearLayout: LinearLayout

        var layoutParams: LinearLayout.LayoutParams
        for (i in 0 until weekRowsCount) {
            linearLayout = LinearLayout(mContext)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                attrs.dayHeight
            )

            linearLayout.layoutParams = layoutParams
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            generateWeekColumns(linearLayout)

            weeks_container.addView(linearLayout)
        }
    }

    private fun generateWeekColumns(linearLayout: LinearLayout) {
        val columns = arrayOfNulls<WeekDayView>(7)

        val inflater = LayoutInflater.from(mContext)

        var tvDay: TextView
        var container: View
        for (i in 0..6) {
            container = inflater.inflate(R.layout.day_view, linearLayout, false)
            container.tag = i
            container.layoutParams.width = attrs.dayWidth

            val eventCircleView: View = container.findViewById(R.id.circle)
            val todayCircleView: View = container.findViewById(R.id.today_circle)

            (eventCircleView.background as GradientDrawable).setColor(attrs.eventCircleColor)
            (todayCircleView.background as GradientDrawable).setColor(attrs.todayCircleColor)

            todayCircleView.layoutParams.width = attrs.todayCircleSize
            todayCircleView.layoutParams.height = attrs.todayCircleSize

            tvDay = container.findViewById(R.id.tv_day)
            tvDay.setTextAppearance(mContext, attrs.dateTextAppearanceId)

            tvDay.layoutParams.width = attrs.todayCircleSize
            tvDay.layoutParams.height = attrs.todayCircleSize

            container.setOnClickListener { view ->
                val day = view.tag as Int
                if (day > 0) {
                    mOnDayClickListener.onClick(day, mMonth, mYear, false)
                }
            }

            linearLayout.addView(container)

            columns[i] = WeekDayView(container, tvDay, eventCircleView, todayCircleView)
        }
        weeksColumns.add(columns)
    }

    inner class WeekDayView internal constructor(
        var container: View,
        var tv_value: TextView,
        var v_event_circle: View,
        var v_today_circle: View
    ) {
        init {
            this.v_event_circle.visibility = View.INVISIBLE
            this.v_today_circle.visibility = View.INVISIBLE
        }
    }
}
