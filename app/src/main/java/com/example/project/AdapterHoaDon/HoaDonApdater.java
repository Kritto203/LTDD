package com.example.project.AdapterHoaDon;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project.FragmentHoaDon.ChoXacNhanFragment;
import com.example.project.FragmentHoaDon.DaGiaoFragment;
import com.example.project.FragmentHoaDon.DaXacNhanFragment;
import com.example.project.FragmentHoaDon.DangGiaoFragment;

public class HoaDonApdater extends FragmentStateAdapter {

    public HoaDonApdater(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChoXacNhanFragment();
            case 1:
                return new DaXacNhanFragment();
            case 2:
                return new DangGiaoFragment();
            case 3:
                return new DaGiaoFragment();
        }
        return new DaGiaoFragment();
    }


    @Override
    public int getItemCount() {
        return 4;
    }
}
