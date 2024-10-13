package com.uan.brainmher.application.ui.fragments.patients;

import com.uan.brainmher.R;
import com.uan.brainmher.infraestructure.tools.Constants;

public class FamilyChildFragment extends BaseMemorizameFragment {
    @Override
    protected String getCollectionName() {
        return Constants.Family;
    }

    @Override
    protected String getTabTitle() {
        return getString(R.string.tab_family);
    }

    @Override
    protected String getTabDescription() {
        return getString(R.string.lbl_description_family);
    }

    @Override
    protected int getTabImage() {
        return R.drawable.img_family_card;
    }
}