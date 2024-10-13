package com.uan.brainmher.application.ui.fragments.patients;

import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.tools.Constants;

public class PetsChildFragment extends BaseMemorizameFragment {
    @Override
    protected String getCollectionName() {
        return Constants.Pets;
    }

    @Override
    protected String getTabTitle() {
        return getString(R.string.tab_pets);
    }

    @Override
    protected String getTabDescription() {
        return getString(R.string.lbl_description_pets);
    }

    @Override
    protected int getTabImage() {
        return R.drawable.img_pets_card;
    }
}