package com.thoughtworks.android_test_project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {

    //Super Fast Solution, para 1 hr de entrevista.
    // Antes de aplicar a lógica, padrões, SOLID e a arquitetura!

    private var checkInDate: Calendar = getInstance()
    private var checkOutDate: Calendar = getInstance()
    private lateinit var bookingRequest: BookingRequest
    private val hotelList: List<Hotel> = mutableListOf(
        Hotel("Parque das flores", 3, 110.0, 80.0, 90.0, 80.0),
        Hotel("Jardim Botânico", 4, 160.0, 110.0, 60.0, 50.0),
        Hotel("Mar Atlântico", 5, 220.0, 100.0, 150.0, 40.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkInCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            checkInDate.set(year, month, dayOfMonth)
        }

        checkOutCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            checkOutDate.set(year, month, dayOfMonth)
        }

        bestPricesButton.setOnClickListener {
            bookingRequest = BookingRequest(rewardSwitch.isChecked, 0, 0)
            showBestPrice(bestHotel().name)
        }
    }

    private fun bestHotel(): Hotel{
        var currentDate: Calendar = checkInDate
        var currentHotelValue: Double

        var minHotelValue = calcHotelValue(0)
        var minHotel = 0

        currentDate.timeInMillis = checkInDate.timeInMillis

        for (i in checkInDate.get(DAY_OF_YEAR)..checkOutDate.get(DAY_OF_YEAR)){
            currentDate.set(DAY_OF_YEAR, i)

            when (currentDate.get(DAY_OF_WEEK)){
                SUNDAY -> bookingRequest.totalWeekends++
                SATURDAY -> bookingRequest.totalWeekends++
                else -> bookingRequest.totalWeekdays++
            }
        }

        for(i in hotelList.indices){
            currentHotelValue = calcHotelValue(i)

            if (currentHotelValue < minHotelValue) {
                minHotelValue = currentHotelValue
                minHotel = i
            }else if (currentHotelValue == minHotelValue)
                if (hotelList[i].classification > hotelList[minHotel].classification){
                    minHotelValue = currentHotelValue
                    minHotel = i
                }
        }
        return hotelList[minHotel]
    }

    private fun calcHotelValue(position: Int): Double{
        var weekends = 0.0
        var weekdays = 0.0

        if(bookingRequest.loyalty){
            weekdays = hotelList[position].loyaltyWeekday * bookingRequest.totalWeekdays
            weekends = hotelList[position].loyaltyWeekend * bookingRequest.totalWeekends
        } else{
            weekdays = hotelList[position].regularWeekday * bookingRequest.totalWeekdays
            weekends = hotelList[position].regularWeekend * bookingRequest.totalWeekends
        }
        return weekdays + weekends
    }

    private fun showBestPrice(name: String) = Toast.makeText(this, name, Toast.LENGTH_LONG).show()
}

data class Hotel(
    val name: String,
    val classification: Int,
    val regularWeekday: Double,
    val loyaltyWeekday: Double,
    val regularWeekend: Double,
    val loyaltyWeekend: Double
)

data class BookingRequest(
    val loyalty: Boolean = false,
    var totalWeekdays: Int = 0,
    var totalWeekends: Int = 0
)