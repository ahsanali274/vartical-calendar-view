package com.ahsanali.varticalcalendarview.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import com.ahsanali.varticalcalendarview.R
import com.ahsanali.varticalcalendarview.VerticalCalendarView
import com.ahsanali.varticalcalendarview.data.Day
import com.ahsanali.varticalcalendarview.data.Month
import com.ahsanali.varticalcalendarview.holders.MonthViewHolder
import java.util.*

class VerticalCalendarAdapter(
    private val mContext: Context,
    private val attrs: VerticalCalendarView.Attributes
) :
    RecyclerView.Adapter<MonthViewHolder>() {

    private val mMonthLabels: List<String> = listOf(*mContext.resources.getStringArray(R.array.months))
    private val mMonths: ArrayList<Month>

    private val startYear: Int
    private val startMonth: Int
    private val today: Int

    private var earlyMonthLoaded: Int = 0
    private var earlyYearLoaded: Int = 0

    private var laterMonthLoaded: Int = 0
    private var laterYearLoaded: Int = 0

    private val minYearLimit: Int
    private val maxYearLimit: Int

    private val PAYLOAD = 3
    private var onDayClickListener: VerticalCalendarView.OnDayClickListener? = null
    private val mEvents: HashMap<String, Boolean>

    override fun getItemCount(): Int {
        return mMonths.size
    }

    init {
        val calendar = Calendar.getInstance()
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH) + 1
        today = calendar.get(Calendar.DAY_OF_MONTH)

        mMonths = ArrayList()
        mEvents = HashMap()

        earlyMonthLoaded = startMonth
        earlyYearLoaded = startYear
        laterYearLoaded = startYear
        laterMonthLoaded = startMonth

        minYearLimit = startYear - 100
        maxYearLimit = startYear + 100

        mMonths.add(Month(startMonth, startYear))
        getPreviousMonth()
        getNextMonths()
    }

    override fun getItemViewType(position: Int): Int {
        return mMonths[position].weeks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.month_view, parent, false)

        val mh = MonthViewHolder(v, viewType, attrs, object : VerticalCalendarView.OnDayClickListener {
            override fun onClick(day: Int, month: Int, year: Int, hasEvent: Boolean) {
                if (onDayClickListener != null) {
                    onDayClickListener!!.onClick(day, month, year, hasEvent(day, month, year))
                }
            }
        })
        mh.generateWeekRows()
        return mh
    }


    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val m = mMonths[position]
        setLabel(holder, m)
        setWeeks(holder, m)

        holder.mYear = m.year
        holder.mMonth = m.value
    }

    private fun setLabel(holder: MonthViewHolder, m: Month) {
        holder.tvMonth.text = mMonthLabels[m.value - 1] + " " + m.year
    }

    private fun setWeeks(holder: MonthViewHolder, m: Month) {
        var weekColumns: Array<MonthViewHolder.WeekDayView?>
        var days: Array<Day>
        var container: View?
        var tvDay: TextView?
        var viewCircle: View?

        for (i in 0 until holder.weekRowsCount) {
            weekColumns = holder.weeksColumns.get(i)
            days = m.weeks[i].days
            for (j in 0..6) {
                viewCircle = weekColumns[j]?.viewEventCircle
                container = weekColumns[j]?.container
                tvDay = weekColumns[j]?.tvvalue
                tvDay?.text = "" + days[j].value

                container?.tag = days[j].value
                container?.isClickable = days[j].value != 0

                viewCircle?.visibility = if (hasEvent(days[j].value, m.value, m.year)) VISIBLE else INVISIBLE

                if (m.year == startYear && m.value == startMonth && days[j].value == today) {
                    tvDay?.setTextColor(Color.WHITE)
                    weekColumns[j]?.viewTodayCircle?.visibility = VISIBLE
                } else {
                    tvDay?.setTextColor(if (days[j].value == 0) Color.TRANSPARENT else Color.BLACK)
                    weekColumns[j]?.viewTodayCircle?.visibility = GONE
                }
            }
        }
    }

    private fun hasEvent(day: Int, month: Int, year: Int): Boolean {
        val key = String.format("%d%d%d", day, month, year)

        return mEvents.containsKey(key)
    }

    fun getPreviousMonth() {
        if (earlyMonthLoaded <= PAYLOAD) {
            var count = 0
            for (i in earlyMonthLoaded - 1 downTo 1) {
                mMonths.add(0, Month(i, earlyYearLoaded))
                count++
            }

            earlyMonthLoaded = 12 - (PAYLOAD - earlyMonthLoaded)
            earlyYearLoaded--

            if (earlyYearLoaded < minYearLimit) {
                notifyItemRangeInserted(0, count)
                return
            }

            for (i in 12 downTo earlyMonthLoaded) {
                mMonths.add(0, Month(i, earlyYearLoaded))
            }

        } else {
            for (i in earlyMonthLoaded - 1 downTo earlyMonthLoaded - PAYLOAD) {
                mMonths.add(0, Month(i, earlyYearLoaded))
            }
            earlyMonthLoaded -= PAYLOAD
        }

        notifyItemRangeInserted(0, PAYLOAD)
    }

    fun getNextMonths() {
        val positionStart = mMonths.size - 1
        if (laterMonthLoaded > 12 - PAYLOAD) {
            var count = 0
            for (i in laterMonthLoaded + 1..12) {
                mMonths.add(Month(i, laterYearLoaded))
                count++
            }

            laterMonthLoaded = laterMonthLoaded + PAYLOAD - 12
            laterYearLoaded++

            if (laterYearLoaded > maxYearLimit) {
                notifyItemRangeInserted(positionStart, count)
                return
            }

            for (i in 1..laterMonthLoaded) {
                mMonths.add(Month(i, laterYearLoaded))
            }

        } else {
            for (i in laterMonthLoaded + 1..laterMonthLoaded + PAYLOAD) {
                mMonths.add(Month(i, laterYearLoaded))
            }
            laterMonthLoaded += PAYLOAD
        }
        notifyItemRangeInserted(positionStart, PAYLOAD)
    }

    fun shouldLoadPreviousMonths(): Boolean {
        return earlyYearLoaded >= minYearLimit
    }

    fun shouldLoadNextMonths(): Boolean {
        return laterYearLoaded <= maxYearLimit
    }

    fun setOnDayClickListener(onDayClickListener: VerticalCalendarView.OnDayClickListener) {
        this.onDayClickListener = onDayClickListener
    }

    fun addEvent(day: Int, month: Int, year: Int) {
        val key = String.format("%d%d%d", day, month, year)
        mEvents[key] = true
        notifyDataSetChanged()
    }

    fun deleteEvent(day: Int, month: Int, year: Int) {
        val key = String.format("%d%d%d", day, month, year)
        mEvents.remove(key)
        notifyDataSetChanged()
    }
}
