package com.studioidan.skarim.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.studioidan.skarim.data.DataStore;

/**
 * Created by PopApp_laptop on 21/01/2016.
 */
public class BaseFragment extends Fragment {
    protected DataStore ds = DataStore.getInstance();

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException mException) {
            mException.printStackTrace();
        }
    }
}
