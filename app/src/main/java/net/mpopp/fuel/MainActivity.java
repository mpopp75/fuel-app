package net.mpopp.fuel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements HttpPostRequest.HttpPostRequestCallback {

    private EditText etDate;
    private EditText etKilometers;
    private EditText etAmountFuel;
    private EditText etPriceLiter;

    private Button bnSave;
    private Button bnReset;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDate = findViewById(R.id.etDate);
        etKilometers = findViewById(R.id.etKilometers);
        etAmountFuel = findViewById(R.id.etFuelAmount);
        etPriceLiter = findViewById(R.id.etPriceLiter);
        bnSave = findViewById(R.id.bnCSave);
        bnReset = findViewById(R.id.bnReset);

        LocalDate dt = LocalDate.now();
        DateTimeFormatter ft = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = dt.format(ft);

        Log.d(this.getClass().getSimpleName(), "currentDate: " + currentDate);
        etDate.setText(currentDate);

        etDate.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDatePickerDialog();
                return true;
            }
        });

        etDate.addTextChangedListener(textWatcher);
        etKilometers.addTextChangedListener(textWatcher);
        etAmountFuel.addTextChangedListener(textWatcher);
        etPriceLiter.addTextChangedListener(textWatcher);

        bnSave.setEnabled(false);

        bnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getCanonicalName(), "Date: " + etDate.getText().toString());
                Log.d(this.getClass().getCanonicalName(), "Kilometers: " + etKilometers.getText().toString());
                Log.d(this.getClass().getCanonicalName(), "Fuel Amount: " + etAmountFuel.getText().toString());
                Log.d(this.getClass().getCanonicalName(), "Price/Liter: " + etPriceLiter.getText().toString());

                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra("date", etDate.getText().toString());
                intent.putExtra("kilometers", etKilometers.getText().toString());
                intent.putExtra("fuel_amount", etAmountFuel.getText().toString());
                intent.putExtra("price_liter", etPriceLiter.getText().toString());
                startActivity(intent);
            }
        });

        bnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate dt = LocalDate.now();
                DateTimeFormatter ft = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String currentDate = dt.format(ft);
                etDate.setText(currentDate);
                etKilometers.setText("");
                etAmountFuel.setText("");
                etPriceLiter.setText("");

                updateListView();
            }
        });

        updateListView();
    }

    private void updateListView() {
        HttpPostRequest request = new HttpPostRequest(this, Config.getUrl());
        request.setParameter("key", Config.getKey());
        request.setParameter("action", "get_data");
        request.execute();
    }

    private void showDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText(String.format("%04d", year) + "-" +
                                       String.format("%02d", (month + 1)) + "-" +
                                       String.format("%02d", dayOfMonth));

                        MainActivity.this.datePickerDialog.dismiss();
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    public void onRequestComplete(String result) {
        Log.d(this.getClass().getSimpleName(), "result: " + result);
        try {
            JSONArray json = new JSONArray(result);
            createListView(json);
        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), "JSONException: " + e);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Exception: " + e);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing before text is changed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing as text is changed
        }

        @Override
        public void afterTextChanged(Editable s) {
            // do this after text is changed

            bnSave.setEnabled(etDate.getText().toString().matches("\\d{4}-\\d{2}-\\d{2}$") &&
                    etKilometers.getText().toString().matches("^\\d+(\\.\\d{0,1})?$") &&
                    etAmountFuel.getText().toString().matches("^\\d+(\\.\\d{0,2})?$") &&
                    etPriceLiter.getText().toString().matches("^\\d+(\\.\\d{0,3})?$"));
        }
    };


    private void createListView(JSONArray json) throws JSONException {
        ListView lvOutput = findViewById(R.id.lvOutput);

        ArrayList<JSONObject> outputList = new ArrayList<JSONObject>();
        for (int i = 0; i < json.length(); i++) {
            outputList.add(json.getJSONObject(i));
        }

        ListItemAdapter adapter = new ListItemAdapter(this, outputList);
        lvOutput.setAdapter(adapter);
    }
}