package com.uan.brainmher.application.ui.fragments.carer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.interfaces.IMainCarer;

public class HomeFragment extends Fragment {

    CardView heart, manage, diary;

    private IMainCarer mIMainCarer;

    public HomeFragment() {
    }

    public HomeFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cu_home,container,false);

        heart = view.findViewById(R.id.heart);
        manage = view.findViewById(R.id.manage);
        diary = view.findViewById(R.id.diary);

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMainCarer.inflateFragment(getString(R.string.my_care));
            }
        });

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMainCarer.inflateFragment(getString(R.string.manage));
            }
        });

        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMainCarer.inflateFragment(getString(R.string.diary));
            }
        });

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIMainCarer = (IMainCarer) getActivity();
    }
}
