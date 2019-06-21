package com.ahsanali.varticalcaledarview

import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import android.widget.Toast
import com.ahsanali.varticalcalendarview.VerticalCalendarView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calendarView.setOnDayClickListener(object : VerticalCalendarView.OnDayClickListener {
            override fun onClick(day: Int, month: Int, year: Int, hasEvent: Boolean) {
                Toast.makeText(
                    this@MainActivity,
                    "$day/$month/$year hasEvent= $hasEvent",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        add_btn.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.setCalendarView(calendarView)
            newFragment.show(supportFragmentManager, "datePicker")
        }
    }

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        private var calendarView: VerticalCalendarView? = null

        fun setCalendarView(calendarView: VerticalCalendarView) {
            this.calendarView = calendarView
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(activity!!, this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            calendarView?.addEvent(day, month + 1, year)
        }
    }
}
