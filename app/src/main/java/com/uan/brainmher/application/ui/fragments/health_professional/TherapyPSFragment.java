package com.uan.brainmher.application.ui.fragments.health_professional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.fragments.memorizame.MemorizameParentFragment;

public class TherapyPSFragment extends Fragment {

    private Bundle args;

    public TherapyPSFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getActivity().getIntent().getExtras();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ps_therapy, container, false);

        TabLayout tabs = view.findViewById(R.id.ps_tabs_therapy);
        ViewPager2 viewPager = view.findViewById(R.id.containerPageTherapyPS);

        SetUpViewPager(viewPager, tabs);
        return view;
    }

    private void SetUpViewPager(ViewPager2 viewPager, TabLayout tabs) {
        // Configura el adaptador del ViewPager2
        Adapter adapter = new Adapter(this);
        viewPager.setAdapter(adapter);

        // Vincula el TabLayout con el ViewPager2 usando TabLayoutMediator
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_lightbulb_outline_black);
                    tab.setText(R.string.cognitive);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_accessibility_black);
                    tab.setText(R.string.motor);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_brain_black);
                    tab.setText(R.string.memorizame);
                    break;
            }
        }).attach();
    }

    public class Adapter extends FragmentStateAdapter {

        public Adapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new CognitiveTherapyPSFragment();
                case 1:
                    return new MotorTherapyPSFragment(args);
                case 2:
                    return new MemorizameParentFragment();
                default:
                    return new CognitiveTherapyPSFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Número de pestañas
        }
    }
}