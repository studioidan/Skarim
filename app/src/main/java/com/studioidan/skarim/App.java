package com.studioidan.skarim;

import android.app.Application;
import android.content.Context;

import com.studioidan.skarim.data.CPM;
import com.studioidan.skarim.data.DataStore;
import com.studioidan.skarim.data.Keys;
import com.studioidan.skarim.entities.Survey;


public class App extends Application {
    private static Context context;
    public static Survey current_Survey;
    public static boolean isInMiddle;

    public static Context getContext() {
        return App.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
        isInMiddle = CPM.getBoolean(Keys.isInMiddle, false, context);
    }

    private void initComponents() {
        App.context = getApplicationContext();
        DataStore.getInstance();
    }

}
