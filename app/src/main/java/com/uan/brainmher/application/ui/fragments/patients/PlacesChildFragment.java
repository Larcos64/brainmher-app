package com.uan.brainmher.application.ui.fragments.patients;

import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.tools.Constants;

public class PlacesChildFragment extends BaseMemorizameFragment {
    @Override
    protected String getCollectionName() {
        return Constants.Places;
    }

    @Override
    protected String getTabTitle() {
        return getString(R.string.tab_places);
    }

    @Override
    protected String getTabDescription() {
        return getString(R.string.lbl_description_places);
    }

    @Override
    protected int getTabImage() {
        return R.drawable.img_places_card;
    }
}