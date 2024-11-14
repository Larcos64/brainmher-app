package com.uan.brainmher.application.ui.adapters.patient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uan.brainmher.application.ui.fragments.motor_exercises_assignment.MotorChildFragment;
import com.uan.brainmher.application.ui.fragments.patients.HomePatientFragment;
import com.uan.brainmher.application.ui.fragments.patients.MemorizamePatientFragment;
import com.uan.brainmher.application.ui.fragments.patients.NotificationsPatientFragment;

public class PatientFragmentPageAdapter extends FragmentStateAdapter {

    MotorChildFragment.MotorChildFragmentI motorChildFragmentI;

    public void setMotorChildFragmentI(MotorChildFragment.MotorChildFragmentI motorChildFragmentI) {
        this.motorChildFragmentI = motorChildFragmentI;
    }

    public PatientFragmentPageAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return HomePatientFragment.newInstance(motorChildFragmentI);
            case 1:
                return MemorizamePatientFragment.newInstance();
            case 2:
                return NotificationsPatientFragment.newInstance();
            default:
                return new Fragment(); // Fragment por defecto si el índice está fuera de rango
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Número de fragmentos
    }
}

