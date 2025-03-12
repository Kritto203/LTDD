package com.example.project.ManHinhChinh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project.LoaiSanPham.LoaiSanPham;
import com.example.project.LoaiSanPham.LoaiSanPhamDAO;
import com.example.project.R;
import com.example.project.SanPham.SanPham;
import com.example.project.SanPham.SanPhamDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManHinhChinhFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManHinhChinhFragment extends Fragment {

    ArrayList<LoaiSanPham> listLoaiSanPham;
    ArrayList<SanPham> listSanPham = new ArrayList<>();
    Spinner spSXTheoLoai;
    int maLoai, maLoaiCheckSpiner = 0;
    LoaiSanPhamDAO loaiSanPhamDAO;
    SapXepSpinnerSanPhamAdapter sapXepSpinnerSanPhamAdapter;
    RecyclerView recycle_sanphammanhinhchinh;
    private ViewPager2 viewPager2;
    private InputMethodManager inputMethodManager;

    public ManHinhChinhFragment() { }

    public static ManHinhChinhFragment newInstance() {
        return new ManHinhChinhFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_man_hinh_chinh, container, false);

        // Khởi tạo các view
        viewPager2 = view.findViewById(R.id.slider);
        EditText timkiemsanpham = view.findViewById(R.id.timkiemsanpham);
        ImageView btntimkiem = view.findViewById(R.id.btntimkiem);
        recycle_sanphammanhinhchinh = view.findViewById(R.id.recycle_sanphammanhinhchinh);
        spSXTheoLoai = view.findViewById(R.id.spnSXLoai);
        inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Danh sách URL ảnh cho ViewPager2
        List<String> listSlide = new ArrayList<>();
        listSlide.add("https://media.baoquangninh.vn/dataimages/201306/original/images679639_54b3e28loikhuyenchonhungnguoit.jpg");
        listSlide.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQB63WlfUr5jhHnAWQ34SCNIwcpTHRzhYw0kw&s");
        listSlide.add("https://cdn.baohatinh.vn/images/757c121c82e2f1709cfa102275ab673b4c8fb15ef10b5c635cd316588728174f6c510c7c52f6c18d1f541e87978c5a5b2694fb951e97cd71a0766be40a887ea7/108d1084140t5916l9-1473149955-thuc-an-n.jpg");
        listSlide.add("https://mqflavor.com/wp-content/uploads/2024/06/ga-ran-kfc-thu-loi-nhuan-thi-truong-thuc-an-nhanh-viet-nam.jpg");

        // Cài đặt adapter cho ViewPager2
        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), listSlide);
        viewPager2.setAdapter(adapter);

        // Cuộn tự động
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem == listSlide.size() - 1) ? 0 : currentItem + 1;
                viewPager2.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // Đổi trang mỗi 3 giây
            }
        };
        handler.postDelayed(runnable, 3000); // Bắt đầu cuộn tự động sau 3 giây

        // Cài đặt RecyclerView cho danh sách sản phẩm
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recycle_sanphammanhinhchinh.setLayoutManager(gridLayoutManager);

        SanPhamDAO sanPhamDAO = new SanPhamDAO(getContext());
        listSanPham = (ArrayList<SanPham>) sanPhamDAO.getAll();
        Adapter_SanPhamManHinhChinh adapter_sanPhamManHinhChinh = new Adapter_SanPhamManHinhChinh(listSanPham, getContext(), sanPhamDAO);
        recycle_sanphammanhinhchinh.setAdapter(adapter_sanPhamManHinhChinh);

        // Cài đặt Spinner để phân loại sản phẩm
        loaiSanPhamDAO = new LoaiSanPhamDAO(getContext());
        listLoaiSanPham = (ArrayList<LoaiSanPham>) loaiSanPhamDAO.getAll();
        LoaiSanPham loaiSanPhamNull = new LoaiSanPham();
        loaiSanPhamNull.setTenLoaiSanPham("Tất cả các sản phẩm");
        listLoaiSanPham.add(0, loaiSanPhamNull);
        sapXepSpinnerSanPhamAdapter = new SapXepSpinnerSanPhamAdapter(getContext(), listLoaiSanPham);
        spSXTheoLoai.setAdapter(sapXepSpinnerSanPhamAdapter);

        spSXTheoLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    listSanPham = (ArrayList<SanPham>) sanPhamDAO.getAll();
                } else {
                    maLoai = listLoaiSanPham.get(position).getMaLoaiSanPham();
                    listSanPham = (ArrayList<SanPham>) sanPhamDAO.LocSanPham(maLoai);
                }
                Adapter_SanPhamManHinhChinh adapter = new Adapter_SanPhamManHinhChinh(listSanPham, getContext(), sanPhamDAO);
                recycle_sanphammanhinhchinh.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                maLoaiCheckSpiner = 1;
            }
        });
        // Tìm kiếm sản phẩm
        timkiemsanpham.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    listSanPham.clear();
                    String ten = timkiemsanpham.getText().toString().trim();
                    if (!ten.isEmpty()) {
                        listSanPham = sanPhamDAO.TimKiemSanPham(ten);
                    } else {
                        listSanPham = (ArrayList<SanPham>) sanPhamDAO.getAll();
                    }
                    Adapter_SanPhamManHinhChinh adapter = new Adapter_SanPhamManHinhChinh(listSanPham, getContext(), sanPhamDAO);
                    recycle_sanphammanhinhchinh.setAdapter(adapter);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSanPham.clear();
                String ten = timkiemsanpham.getText().toString().trim();
                if (ten.isEmpty()) {
                    listSanPham = (ArrayList<SanPham>) sanPhamDAO.getAll();
                } else {
                    listSanPham = sanPhamDAO.TimKiemSanPham(ten);
                }
                Adapter_SanPhamManHinhChinh adapter = new Adapter_SanPhamManHinhChinh(listSanPham, getContext(), sanPhamDAO);
                recycle_sanphammanhinhchinh.setAdapter(adapter);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        return view;
    }
}
