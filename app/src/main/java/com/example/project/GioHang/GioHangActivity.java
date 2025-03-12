package com.example.project.GioHang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.GioHang;
import com.example.project.DonMua.GioHangDAO2;
import com.example.project.Interface.ChangeNumberItemCartList;
import com.example.project.MainActivity;
import com.example.project.QuanLyKhachHang.KhachHang;
import com.example.project.QuanLyKhachHang.KhachHangDAO;
import com.example.project.R;

import java.util.ArrayList;

public class GioHangActivity extends AppCompatActivity implements ChangeNumberItemCartList {
    private RecyclerView rclHoaHon;
    private TextView tvThanhTienMN, tvThueVATMN, tvPhiKhacMN, tvTongTienMN;
    private Button btnThanhToan, btnContinueShopping;
    private ImageView imgNotThing;
    private CartListAdapter cartListAdapter;
    private double tongTien = 0;
    private GioHangDAO2 gioHangDAO2;
    private Toolbar toolbar;
    private LinearLayout linerGioHang, layoutEmptyCart;
    private FrameLayout cartContentContainer;
    private KhachHangDAO khachHangDAO;
    private KhachHang khachHang;
    private ArrayList<GioHang> listGioHang;
    private double tax;
    private int maQuyen, quyenNguoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        // Xử lý giao diện trạng thái
        setupStatusBar();

        // Khởi tạo các view
        initializeViews();

        // Thiết lập Toolbar
        setupToolbar();

        // Lấy thông tin người dùng từ SharedPreferences
        setupUserData();

        // Xử lý nút thanh toán
        setupButtons();
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void initializeViews() {
        try {
            rclHoaHon = findViewById(R.id.rclHoaHon);
            tvThanhTienMN = findViewById(R.id.tvThanhTienMN);
            tvThueVATMN = findViewById(R.id.tvThueVATMN);
            tvPhiKhacMN = findViewById(R.id.tvPhiKhacMN);
            tvTongTienMN = findViewById(R.id.tvTongTienMN);
            btnThanhToan = findViewById(R.id.btnThanhToan);
            toolbar = findViewById(R.id.idToolBar);
            linerGioHang = findViewById(R.id.linerGioHang);

            // Các view mới thêm vào
            layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
            cartContentContainer = findViewById(R.id.cartContentContainer);
            btnContinueShopping = findViewById(R.id.btnContinueShopping);
            imgNotThing = findViewById(R.id.imgNotThing);
        } catch (Exception e) {
            Log.e("GioHangActivity", "Error initializing views", e);
            Toast.makeText(this, "Đã xảy ra lỗi khi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(GioHangActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setupUserData() {
        SharedPreferences preferences = getSharedPreferences("thongtin", MODE_PRIVATE);
        String userName = preferences.getString("username", "");

        // Khởi tạo KhachHangDAO và lấy thông tin người dùng hiện tại
        khachHangDAO = new KhachHangDAO(this);
        khachHang = khachHangDAO.getUserName(userName);

        if (khachHang != null) {
            maQuyen = khachHang.getManguoidung();
            quyenNguoiDung = 1;

            // Lấy dữ liệu giỏ hàng của người dùng
            gioHangDAO2 = new GioHangDAO2(this);
            loadCartData();
        } else {
            // Nếu không tìm thấy người dùng, chuyển hướng về màn hình đăng nhập
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GioHangActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupButtons() {
        // Xử lý nút thanh toán
        btnThanhToan.setOnClickListener(v -> {
            if (listGioHang != null && !listGioHang.isEmpty()) {
                Intent intent = new Intent(GioHangActivity.this, XacNhanThongTinGioHang.class);
                intent.putExtra("totalMoney", tvTongTienMN.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng trống. Vui lòng thêm sản phẩm!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút tiếp tục mua sắm
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(GioHangActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadCartData() {
        try {
            listGioHang = (ArrayList<GioHang>) gioHangDAO2.getAll(String.valueOf(maQuyen), quyenNguoiDung);
            Log.d("GioHangActivity", "Cart items loaded: " + (listGioHang != null ? listGioHang.size() : "null"));

            if (listGioHang != null && !listGioHang.isEmpty()) {
                showCartWithProducts();
            } else {
                showEmptyCartMessage();
            }
        } catch (Exception e) {
            Log.e("GioHangActivity", "Error loading cart data", e);
            Toast.makeText(this, "Không thể tải dữ liệu giỏ hàng", Toast.LENGTH_SHORT).show();
            showEmptyCartMessage();
        }
    }

    private void showEmptyCartMessage() {
        try {
            // Hiển thị layout giỏ hàng trống
            layoutEmptyCart.setVisibility(View.VISIBLE);
            cartContentContainer.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("GioHangActivity", "Error showing empty cart message", e);
            Toast.makeText(this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCartWithProducts() {
        try {
            // Hiển thị layout giỏ hàng có sản phẩm
            layoutEmptyCart.setVisibility(View.GONE);
            cartContentContainer.setVisibility(View.VISIBLE);

            // Thiết lập RecyclerView
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rclHoaHon.setLayoutManager(linearLayoutManager);

            // Create adapter with non-empty list
            cartListAdapter = new CartListAdapter(listGioHang, this, this);
            rclHoaHon.setAdapter(cartListAdapter);

            // Tính toán và cập nhật tổng tiền
            tinhTien();
        } catch (Exception e) {
            Log.e("GioHangActivity", "Error showing cart with products", e);
            Toast.makeText(this, "Đã xảy ra lỗi khi hiển thị giỏ hàng", Toast.LENGTH_SHORT).show();
            showEmptyCartMessage();
        }
    }

    @Override
    public void changed() {
        // Implement the ChangeNumberItemCartList interface method
        tinhTien();

        // Update the adapter if needed
        if (cartListAdapter != null) {
            cartListAdapter.notifyDataSetChanged();
        }

        // Check if cart is now empty after changes
        if (listGioHang != null && listGioHang.isEmpty()) {
            showEmptyCartMessage();
        }
    }

    private void tinhTien() {
        try {
            double thue = 0.02;
            double phidichvu = 2000;
            double totalFee = gioHangDAO2.getTolalFee(String.valueOf(maQuyen), quyenNguoiDung);
            tax = Math.round((totalFee * thue) * 100) / 100.0;
            double itemTotal = Math.round(totalFee * 100) / 100.0;
            double total = Math.round(((itemTotal + tax) * 100) / 100.0);

            // Cập nhật UI
            if (itemTotal > 0) {
                tvThueVATMN.setText(tax + " VND");
                tvPhiKhacMN.setText(phidichvu + " VND");
                tvThanhTienMN.setText(itemTotal + " VND");
                tvTongTienMN.setText((total + phidichvu) + " VND");
            } else {
                // Reset the cart display if total is 0
                showEmptyCartMessage();
            }
        } catch (Exception e) {
            Log.e("GioHangActivity", "Error calculating total", e);
        }
    }

    // Override onResume to refresh cart data each time the activity comes to foreground
    @Override
    protected void onResume() {
        super.onResume();
        if (khachHang != null && gioHangDAO2 != null) {
            // Refresh cart data
            loadCartData();
        }
    }
}