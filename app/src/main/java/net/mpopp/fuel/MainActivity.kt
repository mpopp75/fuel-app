package net.mpopp.fuel

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private var etDate: EditText? = null
    private var etKilometers: EditText? = null
    private var etAmountFuel: EditText? = null
    private var etPriceLiter: EditText? = null
    private var bnSave: Button? = null
    private var datePickerDialog: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etDate = findViewById(R.id.etDate)
        etKilometers = findViewById(R.id.etKilometers)
        etAmountFuel = findViewById(R.id.etFuelAmount)
        etPriceLiter = findViewById(R.id.etPriceLiter)
        bnSave = findViewById(R.id.bnCSave)
        bnSave!!.isEnabled = false

        val bnReset = findViewById<Button>(R.id.bnReset)
        val dt = LocalDate.now()
        val ft = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = dt.format(ft)
        Log.d(this.javaClass.simpleName, "currentDate: $currentDate")

        etDate!!.setText(currentDate)
        etDate!!.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var day = c.get(Calendar.DAY_OF_MONTH)
            var month = c.get(Calendar.MONTH)
            var year = c.get(Calendar.YEAR)

            val etDateText: String = etDate!!.text.toString()

            if (etDateText != "") {
                day = etDateText.substring(8, 10).toInt()
                month = etDateText.substring(5, 7).toInt() - 1
                year = etDateText.substring(0, 4).toInt()
            }

            datePickerDialog = DatePickerDialog(
                this@MainActivity,
                { _, pickedYear, pickedMonth, pickedDayOfMonth ->
                    etDate!!.setText(
                        buildString {
                            append(String.format("%02d", pickedYear))
                            append("-")
                            append(String.format("%02d", pickedMonth + 1))
                            append("-")
                            append(String.format("%02d", pickedDayOfMonth))
                        })
                },
                year,
                month,
                day
            )
            datePickerDialog!!.show()
        }

        etDate!!.addTextChangedListener(textWatcher)
        etKilometers!!.addTextChangedListener(textWatcher)
        etAmountFuel!!.addTextChangedListener(textWatcher)
        etPriceLiter!!.addTextChangedListener(textWatcher)

        bnSave!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ConfirmationActivity::class.java)
            intent.putExtra("date", etDate!!.text.toString())
            intent.putExtra("kilometers", etKilometers!!.text.toString())
            intent.putExtra("fuel_amount", etAmountFuel!!.text.toString())
            intent.putExtra("price_liter", etPriceLiter!!.text.toString())
            startActivity(intent)
        }

        bnReset!!.setOnClickListener {
            val dt1 = LocalDate.now()
            val ft1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate1 = dt1.format(ft1)

            etDate!!.setText(currentDate1)
            etKilometers!!.setText("")
            etAmountFuel!!.setText("")
            etPriceLiter!!.setText("")
            updateListView()
        }
        updateListView()
    }

    private fun updateListView() {
        val client = HttpClient("https://var.mpopp.net/fuel/app_fuel.php")
        val params = mapOf(
            "key" to Config.key,
            "action" to "get_data"
        )

        client.post(params, object : HttpClient.Callback {
            override fun onSuccess(response: String) {
                processListViewData(response)
            }

            override fun onError(e: Exception) {
                Log.e(this.javaClass.simpleName, "Exception: $e")
                Toast.makeText(this@MainActivity, "Connection error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun processListViewData(result: String?) {
        Log.d(this.javaClass.simpleName, "result: $result")
        try {
            val json = JSONArray(result)
            createListView(json)
        } catch (e: JSONException) {
            Log.e(this.javaClass.simpleName, "JSONException: $e")
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Exception: $e")
        }
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do nothing before text is changed
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do nothing as text is changed
        }

        override fun afterTextChanged(s: Editable) {
            // do this after text is changed
            bnSave!!.isEnabled =
                etDate!!.text.toString().matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) &&
                        etKilometers!!.text.toString()
                            .matches(Regex("^\\d+(\\.\\d?)?$")) &&
                        etAmountFuel!!.text.toString()
                            .matches(Regex("^\\d+(\\.\\d{0,2})?$")) &&
                        etPriceLiter!!.text.toString()
                            .matches(Regex("^\\d+(\\.\\d{0,3})?$"))
            Log.i("afterTextChanged", "bnSave.isEnabled: " + bnSave!!.isEnabled.toString())
        }
    }

    @Throws(JSONException::class)
    private fun createListView(json: JSONArray) {
        val lvOutput = findViewById<ListView>(R.id.lvOutput)
        val outputList = ArrayList<JSONObject>()
        for (i in 0 until json.length()) {
            outputList.add(json.getJSONObject(i))
        }
        val adapter = ListItemAdapter(this, outputList)
        lvOutput.adapter = adapter
    }
}
