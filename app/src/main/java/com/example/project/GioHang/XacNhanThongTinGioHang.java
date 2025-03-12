package com.example.project.GioHang;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.BottomSheetDialogXacNhanHoaDon;
import com.example.project.DonMua.GioHang;
import com.example.project.DonMua.GioHangDAO2;
import com.example.project.DonMua.HoaDonChiTietDAO;
import com.example.project.DonMua.HoaDonDAO;
import com.example.project.DonMua.Order;
import com.example.project.QuanLyKhachHang.KhachHang;
import com.example.project.QuanLyKhachHang.KhachHangDAO;
import com.example.project.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XacNhanThongTinGioHang extends AppCompatActivity {
    private TextView tvKhachHang, tvSoDienThoai, tvDiaChi, tvDiemThuongHoaDon;
    private RecyclerView recyclerViewChiTietHoaDon;
    HoaDonChiTietDAO hoaDonChiTietDAO;
    private Button btnXacNhan, btnXacNhanDiemThuong;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayList<GioHang> list = new ArrayList<>();
    private CartListComfirmAdapter cartListAdapter;
    private HoaDonDAO hoaDonDAO;
    private GioHangDAO2 gioHangDAO2;
    private Toolbar toolbar;
    private static final int MAX = 99999;
    private static final int MIN = 10000;
    private double tax;
    private static final String KEY_PUT = "order";
    private ArrayList<GioHang> listGioHang;
    private KhachHangDAO khachHangDAO;
    private KhachHang khachHang;
    private ArrayList<KhachHang> listKhachHang;
    private List<PhuongThucThanhToan> listThanhToan;
    private Spinner spinnerThanhToan;
    private EditText edDiemThuongHoaDon;
    private String tenKh, quyen, diachi;
    private int maGioHang, maNV, maKH;
    private Order mOrder;
    private Long soDienThoai;
    private String titlePhuongThucThanhToan, userNameKhachHang = null;
    private LinearLayout linearLayoutManHinhXacNhan;
    private int maQuyen;
    private LinearLayout linearLayoutDiaChi, linerDialog;
    private double diemThuongAll = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private int diemThuongSend;
    private int maPhuongThucThanhToan;
    private Boolean checkDiaChi = true;
    private static final String TAG = "XacNhanThongTinGioHang";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_thong_tin_gio_hang);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);

        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        tvKhachHang = findViewById(R.id.tvKhachHang);
        tvSoDienThoai = findViewById(R.id.tvSoDienThoai);
        tvDiaChi = findViewById(R.id.tvDiaChi);


        tvKhachHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Commented code here
            }
        });

        tvSoDienThoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Commented code here
            }
        });
        tvDiaChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        recyclerViewChiTietHoaDon = findViewById(R.id.recyclerViewChiTietHoaDon);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        toolbar = findViewById(R.id.idToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Xác Nhận Thanh Toán");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(XacNhanThongTinGioHang.this, GioHangActivity.class));
            }
        });
        khachHangDAO = new KhachHangDAO(this);
        linearLayoutManHinhXacNhan = findViewById(R.id.linearLayoutManHinhXacNhan);
        linearLayoutDiaChi = findViewById(R.id.linearLayoutDiaChi);


        thongTinHoaDon();
        thongTinNhanHang();
        gioHangDAO2 = new GioHangDAO2(this);
        hoaDonDAO = new HoaDonDAO(this);
        list = (ArrayList<GioHang>) gioHangDAO2.getListCart(maGioHang + "", maQuyen);
        initList();
        tinhTien();
        Log.d("zzzz", "Tong tien" + tinhTien());

        String ten = tvKhachHang.getText().toString();
        String date = sdf.format(new Date());
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetFragment();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Double tongTienSauKhiGiam() {
        Double finalDiemThuong = Double.valueOf(0);
        finalDiemThuong = tinhTien();
        btnXacNhan.setText(finalDiemThuong + " VND");
        return finalDiemThuong;
    }

    private void openBottomSheetFragment() {
        Order order = new Order(list, tenKh, soDienThoai, diachi, tinhTien(), maPhuongThucThanhToan, maKH, maNV, maGioHang, maQuyen);
        BottomSheetDialogXacNhanHoaDon bottomSheetDialogXacNhanHoaDon = BottomSheetDialogXacNhanHoaDon.newInstance(order);
        bottomSheetDialogXacNhanHoaDon.show(getSupportFragmentManager(), bottomSheetDialogXacNhanHoaDon.getTag());
        bottomSheetDialogXacNhanHoaDon.setCancelable(false);
    }

    private void thongTinNhanHang() {
        try {
            SharedPreferences preferences = getSharedPreferences("thongtin", MODE_PRIVATE);
            String userName = preferences.getString("username", "");
            String quyen = preferences.getString("loaitaikhoan", "");

            if (quyen.equals("nguoidung")) {
                maQuyen = 1;
                khachHang = khachHangDAO.getUserName(userName);

                if (khachHang != null) {
                    tvKhachHang.setText(khachHang.getHoten());
                    tvSoDienThoai.setText(khachHang.getSodienthoai() + "");
                    tvDiaChi.setText(khachHang.getDiachi());
                    maGioHang = khachHang.getManguoidung();
                    diachi = khachHang.getDiachi();
                    tenKh = khachHang.getHoten();

                    // Fix for NumberFormatException
                    String phoneStr = khachHang.getSodienthoai();
                    if (phoneStr != null && !phoneStr.isEmpty()) {
                        try {
                            soDienThoai = Long.valueOf(phoneStr);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing phone number: " + e.getMessage());
                            soDienThoai = 0L; // Default phone number
                            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        soDienThoai = 0L; // Default phone number if empty
                    }

                    userNameKhachHang = khachHang.getUsername();
                } else {
                    // Handle case where user data couldn't be loaded
                    Log.e(TAG, "User data not found for: " + userName);
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    // Set default values
                    tvKhachHang.setText("Khách hàng");
                    tvSoDienThoai.setText("0");
                    tvDiaChi.setText("");
                    maGioHang = 1; // Default value
                    tenKh = "Khách hàng";
                    soDienThoai = 0L;
                    diachi = "";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in thongTinNhanHang: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi tải thông tin", Toast.LENGTH_SHORT).show();
            // Set safe default values
            maGioHang = 0;
            tenKh = "Khách hàng";
            soDienThoai = 0L;
            diachi = "";
        }
    }

    private double tinhTien() {
        double TongTien;
        double thue = 0.02;
        double phidichvu = 2000;
        tax = Math.round((gioHangDAO2.getTolalFee(maGioHang + "", maQuyen) * thue) * 100) / 100;
        double total = Math.round(((gioHangDAO2.getTolalFee(maGioHang + "", maQuyen) + tax) * 100) / 100);
        btnXacNhan.setText(total + phidichvu + " VND");
        TongTien = total + phidichvu;

        return TongTien;
    }

    public void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewChiTietHoaDon.setLayoutManager(linearLayoutManager);
        cartListAdapter = new CartListComfirmAdapter(list);
        recyclerViewChiTietHoaDon.setAdapter(cartListAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerViewChiTietHoaDon.addItemDecoration(itemDecoration);
    }

    private void thongTinHoaDon() {
        SharedPreferences preferences = getSharedPreferences("thongtin", MODE_PRIVATE);
        String userName = (preferences.getString("username", ""));
        String quyen = (preferences.getString("loaitaikhoan", ""));
        if (quyen.equals("nguoidung")) {
            khachHang = khachHangDAO.getUserName(userName);
            if (khachHang != null) {
                maKH = khachHang.getManguoidung();
            } else {
                // Default value if khachHang is null
                maKH = 0;
            }
        } else {
            // Default value for non-nguoidung users
            maKH = 0;
        }
    }
}