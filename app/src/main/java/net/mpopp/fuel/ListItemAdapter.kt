package net.mpopp.fuel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.floor

class ListItemAdapter(
    private val mContext: Context,
    private val mDictionaryList: ArrayList<JSONObject>
) : ArrayAdapter<JSONObject?>(
    mContext, 0, mDictionaryList as List<JSONObject?>
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) {
            listItem =
                LayoutInflater.from(mContext).inflate(R.layout.activity_list_item, parent, false)
        }
        val tvDate = listItem!!.findViewById<TextView>(R.id.tvDate)
        val tvKilometers = listItem.findViewById<TextView>(R.id.tvKilometers)
        val tvFuelLiters = listItem.findViewById<TextView>(R.id.tvFuelLiters)
        val tvPriceEuroLiter = listItem.findViewById<TextView>(R.id.tvPriceEuroLiter)
        val tvTotalPrice = listItem.findViewById<TextView>(R.id.tvTotalPrice)
        val tvConsumption = listItem.findViewById<TextView>(R.id.tvConsumption)
        try {
            val fuel = mDictionaryList[position]
            val dt = fuel.getString("dt")
            tvDate.text = dt
            val kilometers =
                fuel.getString("kilometers") + " / " + fuel.getString("kilometers_total")
            tvKilometers.text = kilometers
            val fuelLiters = fuel.getString("fuel_liters")
            tvFuelLiters.text = fuelLiters
            val priceEuroLiter = fuel.getString("price_euro_liter")
            tvPriceEuroLiter.text = priceEuroLiter
            val totalPrice = fuel.getString("total_price")
            tvTotalPrice.text = totalPrice
            val consumption = fuel.getString("consumption")
            val consumptionFloat =
                (floor((consumption.toFloat() * 1000).toDouble()) / 1000).toFloat()
            tvConsumption.text = consumptionFloat.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return listItem
    }
}