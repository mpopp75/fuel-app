package net.mpopp.fuel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmationActivity extends AppCompatActivity implements HttpPostRequest.HttpPostRequestCallback {

    private String date;
    private String kilometers;
    private String fuel_amount;
    private String price_liter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        TextView tvCDate = findViewById(R.id.tvCDate);
        TextView tvCKilometers = findViewById(R.id.tvCKilometers);
        TextView tvCAmountFuel = findViewById(R.id.tvCAmountFuel);
        TextView tvCPriceLiter = findViewById(R.id.tvCPriceLiter);
        Button bnCSave = findViewById(R.id.bnCSave);
        Button bnCCancel = findViewById(R.id.bnCCancel);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        kilometers = intent.getStringExtra("kilometers");
        fuel_amount = intent.getStringExtra("fuel_amount");
        price_liter = intent.getStringExtra("price_liter");

        tvCDate.setText("Date: " + date);
        tvCKilometers.setText("Kilometers: " + kilometers);
        tvCAmountFuel.setText(getResources().getString(R.string.fuel_amount_in_liters) + ": " + fuel_amount);
        tvCPriceLiter.setText(getResources().getString(R.string.price_per_liter_in_eur) + ": " + price_liter);

        bnCSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpPostRequest request = new HttpPostRequest((HttpPostRequest.HttpPostRequestCallback) ConfirmationActivity.this, Config.getUrl());
                request.setParameter("key", Config.getKey());
                request.setParameter("action", "insert_data");
                request.setParameter("date", date);
                request.setParameter("kilometers", kilometers);
                request.setParameter("fuel_amount", fuel_amount);
                request.setParameter("price_liter", price_liter);

                request.execute();
            }
        });

        bnCCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestComplete(String result) {
        Log.d(this.getClass().getSimpleName(), "result: " + result);
        if (result.equals("1")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "There was an error inserting the record in the database.", Toast.LENGTH_LONG).show();
        }
    }
}