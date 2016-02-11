package com.studioidan.skarim.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.studioidan.skarim.App;
import com.studioidan.skarim.R;
import com.studioidan.skarim.activities.SurveyActivity;
import com.studioidan.skarim.adapter.AdapterTrip;
import com.studioidan.skarim.data.DataStore;
import com.studioidan.skarim.entities.Survey;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 21/01/2016.
 */
public class FragmentAddTrips extends BaseFragment implements View.OnClickListener, AdapterTrip.TripCallback {
    AdapterTrip adapter;
    ArrayList<Survey> skarim;
    ListView lv;
    Button btnAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skarim = ds.getSurveys();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trips, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        lv = (ListView) view.findViewById(R.id.listView1);
        adapter = new AdapterTrip(getActivity(), skarim, this);
        lv.setAdapter(adapter);

        btnAdd.setOnClickListener(this);
    }

    public void addNewSeker(String tripId) {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = telephonyManager.getDeviceId();
        Survey s = new Survey(IMEI);
        s.surveyorid = DataStore.surviorId;
        s.tripid_plan = tripId;
        ds.addSurvey(s);
        adapter.notifyDataSetChanged();
    }

    public void callAddDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_trip);
        dialog.setTitle(getString(R.string.AddNewTrip));

        Button btn = (Button) dialog.findViewById(R.id.btn_dialog_add_trip_add);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_dialog_add_trip_cencel);
        final EditText input = (EditText) dialog.findViewById(R.id.et_dialog_add_trip);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Toast.makeText(getActivity(), R.string.NoTripIdWasEntered, Toast.LENGTH_SHORT).show();
                    return;
                }
                addNewSeker(str);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                callAddDialog();
                break;
        }
    }

    @Override
    public void onStartClick(Survey survey) {
        App.current_Survey = survey;
        survey.start();
        getActivity().startActivity(new Intent(getActivity(), SurveyActivity.class));
    }

    @Override
    public void onDeleteClick(Survey survey) {
        ds.getSurveys().remove(survey);
        ds.save();
        adapter.notifyDataSetChanged();
    }
}
