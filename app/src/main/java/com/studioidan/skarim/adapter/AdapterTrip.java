package com.studioidan.skarim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.studioidan.skarim.R;
import com.studioidan.skarim.entities.Survey;

import java.util.ArrayList;
import java.util.List;

public class AdapterTrip extends BaseAdapter {

    public List<Survey> data;
    Context con;
    private LayoutInflater inflater = null;
    private TripCallback callback;

    public AdapterTrip(Context c, ArrayList<Survey> d, TripCallback callback) {
        this.con = c;
        this.data = d;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Survey item = data.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.row_trip, null);

        final TextView tv_name = (TextView) vi.findViewById(R.id.tv_row_trip_id);
        tv_name.setText("Trip id: " + item.tripid_plan);
        Button btn_start = (Button) vi.findViewById(R.id.btn_add_trip_start_trip);
        Button btn_delete = (Button) vi.findViewById(R.id.btn_add_trip_delete_trip);
        btn_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onStartClick(item);
            }
        });


        btn_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null)
                    callback.onDeleteClick(item);
            }
        });
        return vi;
    }

    public interface TripCallback {
        void onStartClick(Survey survey);

        void onDeleteClick(Survey survey);
    }
}
