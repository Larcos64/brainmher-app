package com.uan.brainmher.application.ui.fragments.patients;

import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.tools.Constants;

public class HomeChildFragment extends BaseMemorizameFragment {
    @Override
    protected String getCollectionName() {
        return Constants.Home;
    }

    @Override
    protected String getTabTitle() {
        return getString(R.string.tab_home);
    }

    @Override
    protected String getTabDescription() {
        return getString(R.string.lbl_description_home);
    }

    @Override
    protected int getTabImage() {
        return R.drawable.img_home_card;
    }
}