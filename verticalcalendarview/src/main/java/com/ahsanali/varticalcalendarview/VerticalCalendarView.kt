package com.ahsanali.varticalcalendarview

import android.app.Service
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ahsanali.varticalcalendarview.adapters.VerticalCalendarAdapter

class VerticalCalendarView : FrameLayout {

    private var rl_calendar: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mCalendarAdapter: VerticalCalendarAdapter? = null
    private var mOnDayClickListener: OnDayClickListener? = null
    private var calendarAttrs = Attributes()

    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 1
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        getAttrs(attrs, defStyle)

        val layoutInflater = context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val content = layoutInflater.inflate(R.layout.calendar_view, null, false)
        addView(content)

        rl_calendar = findViewById(R.id.rl_calendar)
        mLayoutManager = LinearLayoutManager(context)
        rl_calendar?.layoutManager = mLayoutManager

        setAdapter()

        mLayoutManager?.scrollToPosition(3)

        rl_calendar?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = recyclerView.getChildCount()
                totalItemCount = mCalendarAdapter!!.getItemCount()
                firstVisibleItem = (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }

                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold
                    && mCalendarAdapter!!.shouldLoadNextMonths()
                ) {
                    // End has been reached
                    mCalendarAdapter!!.getNextMonths()
                    loading = true
                }

                if (!loading && firstVisibleItem <= 1 + visibleThreshold
                    && mCalendarAdapter!!.shouldLoadPreviousMonths()
                ) {
                    // Start has been reached
                    mCalendarAdapter!!.getPreviousMonth()
                    loading = true
                }
            }
        })

        invalidate()
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.VerticalCalendarView, defStyle, 0
        )
        val displayMetrics = resources.displayMetrics

        calendarAttrs.weekdayHeight = a.getDimension(
            R.styleable.VerticalCalendarView_weekdayNameHeight,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24.toFloat(), displayMetrics)
        ).toInt()

        val typedValue = TypedValue()

        a.getValue(R.styleable.VerticalCalendarView_weekdayNameLabelColor, typedValue)

        if (typedValue.type == TypedValue.TYPE_REFERENCE) {
            calendarAttrs.weekdayLabelColor = ContextCompat.getColor(
                context,
                a.getResourceId(
                    R.styleable.VerticalCalendarView_weekdayNameLabelColor,
                    R.color.default_LabelColor
                )
            )
        } else {
            calendarAttrs.weekdayLabelColor = a.getColor(
                R.styleable.VerticalCalendarView_weekdayNameLabelColor,
                ContextCompat.getColor(context, R.color.default_LabelColor)
            )
        }

        calendarAttrs.dayHeight = a.getDimension(
            R.styleable.VerticalCalendarView_dayHeight,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48.toFloat(), displayMetrics)
        ).toInt()

        calendarAttrs.dayWidth = a.getDimension(
            R.styleable.VerticalCalendarView_dayWidth,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48.toFloat(), displayMetrics)
        ).toInt()

        calendarAttrs.todayCircleSize = a.getDimension(
            R.styleable.VerticalCalendarView_todayCircleSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30.toFloat(), displayMetrics)
        ).toInt()

        a.getValue(R.styleable.VerticalCalendarView_todayCircleColor, typedValue)

        if (typedValue.type == TypedValue.TYPE_REFERENCE) {
            calendarAttrs.todayCircleColor = ContextCompat.getColor(
                context,
                a.getResourceId(
                    R.styleable.VerticalCalendarView_todayCircleColor,
                    R.color.default_todayCircleColor
                )
            )

        } else {
            calendarAttrs.todayCircleColor = a.getColor(
                R.styleable.VerticalCalendarView_todayCircleColor,
                ContextCompat.getColor(context, R.color.default_todayCircleColor)
            )
        }

        a.getValue(R.styleable.VerticalCalendarView_eventCircleColor, typedValue)

        if (typedValue.type == TypedValue.TYPE_REFERENCE) {
            calendarAttrs.eventCircleColor = ContextCompat.getColor(
                context,
                a.getResourceId(R.styleable.VerticalCalendarView_eventCircleColor, R.color.default_eventCircleColor)
            )

        } else {
            calendarAttrs.eventCircleColor = a.getColor(
                R.styleable.VerticalCalendarView_eventCircleColor,
                ContextCompat.getColor(context, R.color.default_eventCircleColor)
            )
        }

        calendarAttrs.monthDividerSize = a.getDimension(
            R.styleable.VerticalCalendarView_monthDividerSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, displayMetrics)
        ).toInt()

        calendarAttrs.monthLabelSize = a.getDimension(
            R.styleable.VerticalCalendarView_monthLabelSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, displayMetrics)
        )

        calendarAttrs.monthLabelHeight = a.getDimension(
            R.styleable.VerticalCalendarView_monthLabelHeight,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, displayMetrics)
        ).toInt()

        a.getValue(R.styleable.VerticalCalendarView_monthLabelColor, typedValue)

        if (typedValue.type == TypedValue.TYPE_REFERENCE) {
            calendarAttrs.monthLabelColor = ContextCompat.getColor(
                context,
                a.getResourceId(R.styleable.VerticalCalendarView_monthLabelColor, R.color.default_LabelColor)
            )

        } else {
            calendarAttrs.monthLabelColor = a.getColor(
                R.styleable.VerticalCalendarView_monthLabelColor,
                ContextCompat.getColor(context, R.color.default_LabelColor)
            )
        }

        a.recycle()
    }

    private fun setAdapter() {
        mCalendarAdapter = VerticalCalendarAdapter(context, calendarAttrs)
        rl_calendar!!.adapter = mCalendarAdapter

        mCalendarAdapter!!.setOnDayClickListener(object : OnDayClickListener {

            override fun onClick(day: Int, month: Int, year: Int, hasEvent: Boolean) {

                if (mOnDayClickListener != null) {
                    mOnDayClickListener!!.onClick(day, month, year, hasEvent)
                }
            }
        })
    }

    fun setOnDayClickListener(onDayClickListener: OnDayClickListener) {
        mOnDayClickListener = onDayClickListener
    }

    fun addEvent(day: Int, month: Int, year: Int) {
        mCalendarAdapter!!.addEvent(day, month, year)
    }

    fun deleteEvent(day: Int, month: Int, year: Int) {
        mCalendarAdapter!!.deleteEvent(day, month, year)
    }

    /* Classes & Interfaces*/

    interface OnDayClickListener {
        fun onClick(day: Int, month: Int, year: Int, hasEvent: Boolean)
    }

    inner class Attributes {
        var weekdayHeight: Int = 0
        var weekdayLabelColor: Int = 0

        var dayWidth: Int = 0
        var dayHeight: Int = 0

        var todayCircleColor: Int = 0
        var todayCircleSize: Int = 0

        var monthLabelSize: Float = 0.toFloat()
        var monthLabelHeight: Int = 0
        var monthLabelColor: Int = 0

        var monthDividerSize: Int = 0

        var eventCircleColor: Int = 0
    }
}