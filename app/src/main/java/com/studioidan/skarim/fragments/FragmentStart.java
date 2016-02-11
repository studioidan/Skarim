package com.studioidan.skarim.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.studioidan.skarim.R;
import com.studioidan.skarim.activities.BaseActivity;
import com.studioidan.skarim.data.CPM;
import com.studioidan.skarim.data.DataStore;
import com.studioidan.skarim.data.Keys;

import java.util.Locale;

/**
 * Created by PopApp_laptop on 21/01/2016.
 */
public class FragmentStart extends BaseFragment implements View.OnClickListener {
    private EditText et_login;
    private Button btnOk;
    private ImageView imgLanRu, imgLanEn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_login = (EditText) view.findViewById(R.id.et_main_login);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        imgLanRu = (ImageView) view.findViewById(R.id.imgLanRu);
        imgLanEn = (ImageView) view.findViewById(R.id.imgLanEn);
        imgLanRu.setOnClickListener(this);
        imgLanEn.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                String str = et_login.getText().toString().trim();
                if (str.length() == 0) return;
                DataStore.surviorId = str;
                CPM.putString(Keys.surviorId, str, getActivity());
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragmentContainer, new FragmentAddTrips(), "", false, "", -1, -1, -1, -1);
                break;
            case R.id.imgLanEn:
                setLocal("en_US");
                break;
            case R.id.imgLanRu:
                setLocal("ru");
                break;
        }

    }

    private void setLocal(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getApplicationContext().getResources().updateConfiguration(config, null);

        getActivity().onConfigurationChanged(config);
    }
}
