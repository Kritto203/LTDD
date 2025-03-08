package com.example.project.FragmentHoaDon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.Adapter_DangGiao;
import com.example.project.DonMua.HoaDon;
import com.example.project.DonMua.HoaDonDAO;
import com.example.project.R;

import java.util.ArrayList;

public class DangGiaoFragment extends Fragment {
    private RecyclerView recycleViewDangGiao;
    HoaDonDAO qlhd;
    HoaDonDAO hoaDonDAO;
    ArrayList<HoaDon> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.danggiao_fragment, container, false);
        recycleViewDangGiao = view.findViewById(R.id.recycleViewDangGiao);
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        ArrayList<HoaDon> list = new ArrayList<>();
        qlhd = new HoaDonDAO(getContext());
        list = (ArrayList<HoaDon>) qlhd.getTrangThai2();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycleViewDangGiao.setLayoutManager(linearLayoutManager);
        Adapter_DangGiao adapter_daXacNhan = new Adapter_DangGiao(list, getContext(),qlhd);
        recycleViewDangGiao.setAdapter(adapter_daXacNhan);
    }

}