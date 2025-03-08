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

import com.example.project.DonMua.Adapter_DaGiaoCuaKhachHang;
import com.example.project.DonMua.Adapter_DangGiao;
import com.example.project.DonMua.HoaDon;
import com.example.project.DonMua.HoaDonDAO;
import com.example.project.R;

import java.util.ArrayList;

public class DaGiaoFragment extends Fragment {
    RecyclerView recycleViewDaGiao;
    private HoaDonDAO hoaDonDAO;
    Adapter_DangGiao adapter_donMua;

    RecyclerView recycleViewDangGiao;
    private RecyclerView recyclerViewDonMua;
    HoaDonDAO qlhd;
    ArrayList<HoaDon> list = new ArrayList<>();
    private int trangthai;


    public DaGiaoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData1();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagiao_fragment, container, false);
        recycleViewDaGiao = view.findViewById(R.id.recycleViewDaGiao);
        loadData1();
        return view;
    }

    public void loadData1() {
        ArrayList<HoaDon> list = new ArrayList<>();
        qlhd = new HoaDonDAO(getContext());
        list = (ArrayList<HoaDon>) qlhd.getTrangThai3();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycleViewDaGiao.setLayoutManager(linearLayoutManager);
        Adapter_DaGiaoCuaKhachHang adapterDaGiao = new Adapter_DaGiaoCuaKhachHang(list, getContext(),qlhd);
        recycleViewDaGiao.setAdapter(adapterDaGiao);
    }

}


