package com.thoughtworks.android_test_project

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {

    private var bookingRequest: BookingRequest = BookingRequest()
    lateinit var checkInDate: Calendar
    lateinit var checkOutDate: Calendar

    private val hotelList: List<Hotel> = mutableListOf(
            Hotel("Parque das flores", 3, "R$110", "R$80", "R$90", "R$80"),
            Hotel("Jardim Botânico", 4, "R$160", "R$110", "R$60", "R$50"),
            Hotel("Mar Atlântico", 5, "R$220", "R$100", "R$150", "R$40")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkInDate = getInstance()
        checkOutDate = getInstance()

        checkInCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            checkInDate.set(year, month, dayOfMonth)
            checkInCalendarView.date = checkInDate.timeInMillis
        }

        checkOutCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            checkOutDate.set(year, month, dayOfMonth)
            checkOutCalendarView.date = checkOutDate.timeInMillis
        }

        bestPricesButton.setOnClickListener {
            bookingRequest = BookingRequest(rewardSwitch.isChecked, 0, 0)
            val hotel: Hotel = getBestHotel()
            showBest(hotel.name)
        }
    }

    private fun getBestHotel(): Hotel {
        checkInDate.timeInMillis = checkInCalendarView.date
        checkOutDate.timeInMillis = checkOutCalendarView.date

        var dateAux = checkInDate
        var firstDay: Int = checkInDate.get(DAY_OF_MONTH)
        var lastDay: Int = checkOutDate.get(DAY_OF_MONTH)

        for (i in firstDay..lastDay) {
            dateAux.set(DAY_OF_MONTH, i)

            when (dateAux.get(DAY_OF_WEEK)) {
                1 -> bookingRequest.totalWeekends = bookingRequest.totalWeekends + 1 // SUNDAY
                2 -> bookingRequest.totalWeekdays = bookingRequest.totalWeekdays + 1 // MONDAY
                3 -> bookingRequest.totalWeekdays = bookingRequest.totalWeekdays + 1 // TUESDAY
                4 -> bookingRequest.totalWeekdays = bookingRequest.totalWeekdays + 1 // WEDNESDAY
                5 -> bookingRequest.totalWeekdays = bookingRequest.totalWeekdays + 1 // THURSDAY
                6 -> bookingRequest.totalWeekdays = bookingRequest.totalWeekdays + 1 // FRIDAY
                7 -> bookingRequest.totalWeekends = bookingRequest.totalWeekends + 1 // SATURDAY
            }
        }

        var totalValueCurrentHotel = 0
        var minValue = 0
        var minValueHotel = 0

        for (i in 0..2) {
            totalValueCurrentHotel = calculateTotalValue(i)

            when {
                minValue == 0 && i == 0-> {
                    minValue = totalValueCurrentHotel
                    minValueHotel = i
                }
                minValue == totalValueCurrentHotel -> {
                    if (hotelList[minValueHotel].classification < hotelList[i].classification)
                        minValueHotel = i
                }
                minValue > totalValueCurrentHotel -> {
                    minValue = totalValueCurrentHotel
                    minValueHotel = i
                }
            }
        }
        return hotelList[minValueHotel]
    }

    private fun calculateTotalValue(position: Int): Int {
        var totalWeekday = 0
        var totalWeekend = 0

        if (bookingRequest.loyalty) {
            totalWeekday = hotelList[position].loyaltyWeekday.removePrefix("R$").toInt() * bookingRequest.totalWeekdays
            totalWeekend = hotelList[position].loyaltyWeekend.removePrefix("R$").toInt() * bookingRequest.totalWeekends
        } else {
            totalWeekday = hotelList[position].regularWeekday.removePrefix("R$").toInt() * bookingRequest.totalWeekdays
            totalWeekend = hotelList[position].regularWeekend.removePrefix("R$").toInt() * bookingRequest.totalWeekends
        }
        return totalWeekday + totalWeekend
    }

    private fun getDateFrom(calendarView: CalendarView): Calendar {
        val calendar = getInstance()
        calendar.timeInMillis = calendarView.date
        return calendar
    }

    private fun showBest(name: String) = Toast.makeText(this, name, Toast.LENGTH_LONG).show()
}

data class Hotel(
        var name: String = "",
        var classification: Int = 0,
        var regularWeekday: String = "",
        var loyaltyWeekday: String = "",
        var regularWeekend: String = "",
        var loyaltyWeekend: String = ""
)

data class BookingRequest(
        var loyalty: Boolean = false,
        var totalWeekdays: Int = 0,
        var totalWeekends: Int = 0,
)
