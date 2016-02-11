package com.studioidan.skarim.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.studioidan.skarim.R;
import com.studioidan.skarim.fragments.FragmentAddTrips;
import com.studioidan.skarim.fragments.FragmentStart;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_GO_TO_ADD_SURVEY = "extra.go.to.add.survey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //check if need to go to add trips
        if (getIntent() != null && getIntent().hasExtra(EXTRA_GO_TO_ADD_SURVEY))
            replaceFragment(R.id.fragmentContainer, new FragmentAddTrips(), "");
        else
            replaceFragment(R.id.fragmentContainer, new FragmentStart(), "");

        /*
        boolean b = CPM.getBoolean(Keys.isInMiddle, false, getApplicationContext());
        if (b == true) {
            Survey s = CPM.getObject(Keys.LastSeker, Survey.class, getApplicationContext());
            if (s.stops.size() == 0) {
                // the session was just started...no reason to recover
                CPM.putBoolean(Keys.isInMiddle, false, getApplicationContext());
                return;
            }
            App.current_Survey = s;
            DataStore.surviorId = s.surveyorid;
            Intent i = new Intent(getApplicationContext(), SurveyActivity.class);
            i.putExtra("isInmiddle", true);
            this.finish();
            startActivity(i);
        }*/

    }

    public static void start(Context context, boolean goToAddSurvey) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        starter.putExtra(EXTRA_GO_TO_ADD_SURVEY, goToAddSurvey);
        context.startActivity(starter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        replaceFragment(R.id.fragmentContainer, new FragmentStart(), "");
        super.onConfigurationChanged(newConfig);
    }
}
