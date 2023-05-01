package net.mpopp.fuel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.mpopp.fuel.HttpPostRequest.HttpPostRequestCallback

class ConfirmationActivity : AppCompatActivity(), HttpPostRequestCallback {
    private var date: String? = null
    private var kilometers: String? = null
    private var fuelAmount: String? = null
    private var priceLiter: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        val tvCDate = findViewById<TextView>(R.id.tvCDate)
        val tvCKilometers = findViewById<TextView>(R.id.tvCKilometers)
        val tvCAmountFuel = findViewById<TextView>(R.id.tvCAmountFuel)
        val tvCPriceLiter = findViewById<TextView>(R.id.tvCPriceLiter)
        val bnCSave = findViewById<Button>(R.id.bnCSave)
        val bnCCancel = findViewById<Button>(R.id.bnCCancel)
        val intent = intent
        date = intent.getStringExtra("date")
        kilometers = intent.getStringExtra("kilometers")
        fuelAmount = intent.getStringExtra("fuel_amount")
        priceLiter = intent.getStringExtra("price_liter")
        tvCDate.text = "Date: $date"
        tvCKilometers.text = "Kilometers: $kilometers"
        tvCAmountFuel.text =
            resources.getString(R.string.fuel_amount_in_liters) + ": " + fuelAmount
        tvCPriceLiter.text =
            resources.getString(R.string.price_per_liter_in_eur) + ": " + priceLiter
        bnCSave.setOnClickListener {
            val request = HttpPostRequest(
                this@ConfirmationActivity as HttpPostRequestCallback,
                Config.url
            )
            request.setParameter("key", Config.key)
            request.setParameter("action", "insert_data")
            request.setParameter("date", date!!)
            request.setParameter("kilometers", kilometers!!)
            request.setParameter("fuel_amount", fuelAmount!!)
            request.setParameter("price_liter", priceLiter!!)
            request.execute()
        }
        bnCCancel.setOnClickListener {
            val intent = Intent(this@ConfirmationActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestComplete(result: String?) {
        Log.d(this.javaClass.simpleName, "result: $result")
        if (result == "1") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "There was an error inserting the record in the database.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}