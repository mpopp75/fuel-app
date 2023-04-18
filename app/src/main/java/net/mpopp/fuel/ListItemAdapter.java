package net.mpopp.fuel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListItemAdapter extends ArrayAdapter<JSONObject> {
    private Context mContext;
    private ArrayList<JSONObject> mDictionaryList;

    public ListItemAdapter(Context context, ArrayList<JSONObject> dictionaryList) {
        super(context, 0, dictionaryList);
        mContext = context;
        mDictionaryList = dictionaryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.activity_list_item, parent, false);
        }

        TextView tvDate = listItem.findViewById(R.id.tvDate);
        TextView tvKilometers = listItem.findViewById(R.id.tvKilometers);
        TextView tvFuelLiters = listItem.findViewById(R.id.tvFuelLiters);
        TextView tvPriceEuroLiter = listItem.findViewById(R.id.tvPriceEuroLiter);
        TextView tvTotalPrice = listItem.findViewById(R.id.tvTotalPrice);
        TextView tvConsumption = listItem.findViewById(R.id.tvConsumption);

        try {
            JSONObject fuel = mDictionaryList.get(position);
            String dt = fuel.getString("dt");
            tvDate.setText(dt);

            String kilometers = fuel.getString("kilometers") + " / " + fuel.getString("kilometers_total");
            tvKilometers.setText(kilometers);

            String fuel_liters = fuel.getString("fuel_liters");
            tvFuelLiters.setText(fuel_liters);

            String price_euro_liter = fuel.getString("price_euro_liter");
            tvPriceEuroLiter.setText(price_euro_liter);

            String total_price = fuel.getString("total_price");
            tvTotalPrice.setText(total_price);

            String consumption = fuel.getString("consumption");
            float consumption_float = (float) (Math.floor(Float.parseFloat(consumption) * 1000) / 1000);
            tvConsumption.setText(String.valueOf(consumption_float));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listItem;
    }
}