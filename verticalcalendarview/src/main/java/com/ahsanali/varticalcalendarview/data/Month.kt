package com.ahsanali.varticalcalendarview.data

class Month(var value: Int, var year: Int) {
    var weeks: ArrayList<Week> = WeekManager.getWeeks(value, year)
    var lastDay: Int = 0

    init {
        lastDay = weeks[weeks.size - 1].getLastDay()
    }
}