package com.thoughtworks.android_test_project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {

    private var bookingRequest: BookingRequest = BookingRequest()
    private val checkInDate: Calendar = getInstance()
    private val checkOutDate: Calendar = getInstance()

    private val hotelList: List<Hotel> = mutableListOf(
        Hotel("Parque das flores", 3, 110, 80, 90, 80),
        Hotel("Jardim Botânico", 4, 160, 110, 60, 50),
        Hotel("Mar Atlântico", 5, 220, 100, 150, 40)
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
            bookingRequest = BookingRequest(rewardSwitch.isChecked)
            showBest(getBestHotel().name)
        }
    }

    private fun getBestHotel(): Hotel {
        val dateAux = checkInDate

        for (i in checkInDate.get(DAY_OF_MONTH)..checkOutDate.get(DAY_OF_MONTH)) {
            dateAux.set(DAY_OF_MONTH, i)

            when (dateAux.get(DAY_OF_WEEK)) {
                1 -> bookingRequest.totalWeekends++ // SUNDAY
                7 -> bookingRequest.totalWeekends++ // SATURDAY
                else -> bookingRequest.totalWeekdays++ //WEEKDAY
            }
        }

        var totalValueCurrentHotel: Int
        var minValue = calculateTotalValue(0)
        var minValueHotelPosition = 0

        for (i in 1 until hotelList.size) {
            totalValueCurrentHotel = calculateTotalValue(i)

            when {
                minValue == totalValueCurrentHotel -> {
                    if (hotelList[minValueHotelPosition].classification < hotelList[i].classification)
                        minValueHotelPosition = i
                }
                minValue > totalValueCurrentHotel -> {
                    minValue = totalValueCurrentHotel
                    minValueHotelPosition = i
                }
            }
        }
        return hotelList[minValueHotelPosition]
    }

    private fun calculateTotalValue(position: Int): Int {
        val totalWeekday: Int
        val totalWeekend: Int

        if (bookingRequest.loyalty) {
            totalWeekday = hotelList[position].loyaltyWeekday * bookingRequest.totalWeekdays
            totalWeekend = hotelList[position].loyaltyWeekend * bookingRequest.totalWeekends
        } else {
            totalWeekday = hotelList[position].regularWeekday * bookingRequest.totalWeekdays
            totalWeekend = hotelList[position].regularWeekend * bookingRequest.totalWeekends
        }
        return totalWeekday + totalWeekend
    }

    private fun showBest(name: String) = Toast.makeText(this, name, Toast.LENGTH_LONG).show()
}

data class Hotel(
    val name: String,
    val classification: Int,
    val regularWeekday: Int,
    val loyaltyWeekday: Int,
    val regularWeekend: Int,
    val loyaltyWeekend: Int
)

data class BookingRequest(
    var loyalty: Boolean = false,
    var totalWeekdays: Int = 0,
    var totalWeekends: Int = 0,
)

// Implementar as data classes
// Mocar as instancias de hotel
// Criar os listeners (dos calendars e do botão)
// Fazer função com o algoritmo de calculo
// Fazer função para o Toast do hotel
// Testar