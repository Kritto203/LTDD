package com.example.project.FragmentHoaDon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.Adapter_DaGiaoCuaKhachHang;
import com.example.project.DonMua.HoaDon;
import com.example.project.DonMua.HoaDonDAO;
import com.example.project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DaGiaoKhachHangFragment extends Fragment {

    private RecyclerView recyclerViewDonMua2;
    private FloatingActionButton btnThemDonMua;
    HoaDonDAO qlhd;
    ArrayList<HoaDon> list = new ArrayList<>();

    public DaGiaoKhachHangFragment() {

    }


    public static DaGiaoKhachHangFragment newInstance() {
        DaGiaoKhachHangFragment fragment = new DaGiaoKhachHangFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dagiaocuakhachhang, container, false);
        recyclerViewDonMua2 = view.findViewById(R.id.rcDonMua2);


        loadData();
        return view;
    }


    public void loadData() {
        qlhd = new HoaDonDAO(getContext());
        list = (ArrayList<HoaDon>) qlhd.getTrangThai3();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewDonMua2.setLayoutManager(linearLayoutManager);
        Adapter_DaGiaoCuaKhachHang adapterDonMua = new Adapter_DaGiaoCuaKhachHang(list, getContext(),qlhd);
        recyclerViewDonMua2.setAdapter(adapterDonMua);
    }
}

