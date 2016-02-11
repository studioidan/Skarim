package com.studioidan.skarim.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.studioidan.skarim.App;
import com.studioidan.skarim.LocationService;
import com.studioidan.skarim.R;
import com.studioidan.skarim.data.CPM;
import com.studioidan.skarim.data.DataStore;
import com.studioidan.skarim.data.Keys;
import com.studioidan.skarim.entities.LocationTrackItem;
import com.studioidan.skarim.entities.Stop;
import com.studioidan.skarim.entities.Survey;

import java.util.Date;

public class SurveyActivity extends ActionBarActivity implements OnClickListener {
    enum DoorMode {OPEN, CLOSE}

    //Data
    Survey survey;
    Stop CurrentStop;
    int CurrentStopId;
    DoorMode doorMode;
    Location lastLocation;
    Handler handler;
    int continuingBySystem;

    //Views
    Button btnDoorClosed, btnDoorOpened, btn_finish;
    TextView tvUp_plusOne, tvUp_plusFive, tvUp_minusOne, tvDown_plusOne, tvDown_plusFive, tvDown_minusOne;
    Button btnUp_plusOne, btnUp_plusFive, btnUp_minusOne, btnDown_plusOne, btnDown_plusFive, btnDown_minusOne;
    TextView tvTotalUp, tvTotalDown;
    Animation animBlink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);

        startService(new Intent(SurveyActivity.this, LocationService.class));
        init();
        boolean isInMiddle = getIntent().getBooleanExtra("isInmiddle", false);
        initSurvey(isInMiddle);
        doorOpen();

        //we are in the middle of survey
        CPM.putBoolean(Keys.isInMiddle, true, SurveyActivity.this);
    }

    protected void onStart() {
        IntentFilter filter = new IntentFilter("NEW_LOCATION");
        registerReceiver(LocationReceiver, filter);
        Log.i("SurveyActivity", "ReceiverRegistered");
        super.onStart();
    }


    BroadcastReceiver LocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SurveyActivity", "gotLocation");
            Location l = (Location) intent.getExtras().get("loc");
            float speed = intent.getExtras().getFloat("speed", 0);
            lastLocation = l;
            if (lastLocation != null) {
                String doorM = "Open";
                if (doorMode == DoorMode.CLOSE)
                    doorM = "Close";
                LocationTrackItem item = new LocationTrackItem(lastLocation.getLatitude(), lastLocation.getLongitude(), doorM, survey.tripid_plan, DataStore.surviorId);
                item.speed = speed;
                survey.addLocationToChain(item);
            }
        }
    };

    private void init() {
        //reference
        btnDoorOpened = (Button) findViewById(R.id.btn_counter_door_opened);
        btnDoorClosed = (Button) findViewById(R.id.btn_counter_door_closed);
        btn_finish = (Button) findViewById(R.id.btn_counter_finish);

        handler = new Handler();
        tvTotalUp = (TextView) findViewById(R.id.tv_counter_up_total);
        tvTotalDown = (TextView) findViewById(R.id.tv_counter_down_total);

        //other
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_blink);

    }

    private void initSurvey(boolean isInMiddle) {
        survey = App.current_Survey;
        doorMode = DoorMode.OPEN;
        CurrentStopId = survey.stops.size() + 1;
    }

    public void doorOpen() {
        if (CurrentStop == null) {
            CurrentStop = new Stop(survey.stops.size() + 1);
            btnDoorOpened.startAnimation(animBlink);
            btnDoorClosed.clearAnimation();

            // save current survey
            CPM.putObject(Keys.LastSeker, survey, SurveyActivity.this);
            return;
        }

        survey.stops.add(CurrentStop);
        CurrentStop.continue_by_system = survey.getContinueCount() ;
        CurrentStopId += 1;
        CurrentStop = new Stop(CurrentStopId);

        animBlink.cancel();
        btnDoorOpened.startAnimation(animBlink);
        btnDoorClosed.clearAnimation();
        updateView();

        // save current survey
        CPM.putObject(Keys.LastSeker, survey, SurveyActivity.this);
    }

    public void doorClose() {
        // init stop time
        CurrentStop.timeDoorClose = new Date();
        if (lastLocation != null) {
            CurrentStop.lat = (float) lastLocation.getLatitude();
            CurrentStop.lon = (float) lastLocation.getLongitude();
        }
        btnDoorClosed.startAnimation(animBlink);
        btnDoorOpened.clearAnimation();
        updateView();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(LocationReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_counter_door_opened:
                if (doorMode == DoorMode.OPEN)
                    return;
                doorMode = DoorMode.OPEN;
                doorOpen();
                break;
            case R.id.btn_counter_door_closed:
                if (doorMode == DoorMode.CLOSE)
                    return;
                if (((survey.stops.size() + 1) % 5 == 0) && (survey.stops.size() != 0)) {
                    handler.removeCallbacks(run_continueDialog);
                    handler.postDelayed(run_continueDialog, 1000 * 10);
                    Toast.makeText(getApplicationContext(), R.string.WaitForContinueDialog, Toast.LENGTH_LONG).show();
                }
                doorMode = DoorMode.CLOSE;
                doorClose();
                break;
            case R.id.btn_counter_finish:
                survey.BusKind = "";
                showEndDialog();
                break;
            case R.id.btn_counter_up_plus_one:
                CurrentStop.On += 1;
                updateView();
                break;
            case R.id.btn_counter_up_plus_five:
                CurrentStop.On += 5;
                updateView();
                break;
            case R.id.btn_counter_up_minus_one:
                CurrentStop.On = Math.max(0, CurrentStop.On - 1);
                updateView();
                break;
            case R.id.btn_counter_up_reset:
                CurrentStop.On = 0;
                updateView();
                break;
            case R.id.btn_counter_down_plus_one:
                CurrentStop.Off += 1;
                updateView();
                break;
            case R.id.btn_counter_down_plus_five:
                CurrentStop.Off += 5;
                updateView();
                break;
            case R.id.btn_counter_down_minus_one:
                CurrentStop.Off = Math.max(0, CurrentStop.Off - 1);
                updateView();
                break;
            case R.id.btn_counter_down_reset:
                CurrentStop.Off = 0;
                updateView();
                break;
            default:
                break;
        }
    }

     Runnable run_continueDialog = new Runnable() {

        @Override
        public void run() {
            showContinueDialog();
        }
    };

    private void showEndDialog() {
        final Dialog dialog = new Dialog(SurveyActivity.this);
        dialog.setContentView(R.layout.dialog_end_trip);
        dialog.setTitle(R.string.AreYouSure);

        // set the custom dialog components - text, image and button
        Button yes = (Button) dialog.findViewById(R.id.btn_dialog_exit_yes);
        Button no = (Button) dialog.findViewById(R.id.btn_dialog_exit_no);
        yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(run_continueDialog);
                dialog.dismiss();

                // save last stop data
                if (doorMode == DoorMode.CLOSE)
                    doorOpen();

                survey.date_end = new Date();

                //turn location chain into json
                Intent intent = new Intent(SurveyActivity.this, CompleteSeker.class);

                //String locJson = new Gson().toJson(survey.locationChain);
                //intent.putExtra("locs", locJson);

                // kill location service
                stopService(new Intent(SurveyActivity.this, LocationService.class));
                SurveyActivity.this.finish();
                startActivity(intent);
            }
        });
        no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showContinueDialog() {
        final Dialog dialog = new Dialog(SurveyActivity.this);
        dialog.setContentView(R.layout.dialog_continue_number);
        dialog.setTitle(getString(R.string.Continue));

        // set the custom dialog components - text, image and button
        Button btn = (Button) dialog.findViewById(R.id.btn_dialog_continue_ok);
        final EditText input = (EditText) dialog.findViewById(R.id.et_dialog__continue);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String str = input.getText().toString().trim();
                    int num = Integer.parseInt(str);
                    CurrentStop.continue_by_survior = num;

                    dialog.dismiss();
                    //doorMode = DoorMode.CLOSE;
                    //doorClose();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Input Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void updateView() {
        tvTotalUp.setText("" + CurrentStop.On);
        tvTotalDown.setText("" + CurrentStop.Off);
    }

    @Override

    public void onBackPressed() {
        showExitDialog();
        //super.onBackPressed();
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(SurveyActivity.this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setTitle(getString(R.string.AreYouSure));

        // set the custom dialog components - text, image and button
        Button yes = (Button) dialog.findViewById(R.id.btn_dialog_exit_yes);
        Button no = (Button) dialog.findViewById(R.id.btn_dialog_exit_no);
        yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DataStore.getInstance().getSurveys().remove(survey);
                DataStore.getInstance().save();
                App.current_Survey = null;

                MainActivity.start(SurveyActivity.this, true);
                //Intent intent = new Intent(SurveyActivity.this, AddTrips.class);
                // kill location service
                stopService(new Intent(SurveyActivity.this, LocationService.class));
                SurveyActivity.this.finish();
                //startActivity(intent);
            }
        });
        no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
