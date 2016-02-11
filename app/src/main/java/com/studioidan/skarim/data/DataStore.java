package com.studioidan.skarim.data;

import com.google.gson.reflect.TypeToken;
import com.studioidan.skarim.App;
import com.studioidan.skarim.entities.Survey;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 21/01/2016.
 */
public class DataStore implements Serializable {
    private static DataStore instance;
    private ArrayList<Survey> surveys;
    public static String surviorId;

    public static DataStore getInstance() {
        if (instance == null)
            instance = new DataStore();
        return instance;
    }

    private DataStore() {
        surveys = (ArrayList<Survey>) CPM.getArrayObject(Keys.skarim, new TypeToken<ArrayList<Survey>>() {
        }.getType(), App.getContext());
    }

    public ArrayList<Survey> getSurveys() {
        if (surveys == null)
            surveys = new ArrayList<>();
        return surveys;
    }

    public void setSurveys(ArrayList<Survey> surveys) {
        this.surveys = surveys;
        CPM.putObject(Keys.skarim, this.surveys, App.getContext());
    }

    public void addSurvey(Survey survey) {
        getSurveys().add(survey);
        save();
    }

    public void save() {
        setSurveys(getSurveys());
    }
}
