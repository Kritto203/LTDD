package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.DonMua.GioHang;
import com.example.project.DonMua.GioHangDAO2;
import com.example.project.QuanLyKhachHang.KhachHang;
import com.example.project.QuanLyKhachHang.KhachHangDAO;
import com.example.project.SanPham.SanPham;
import com.example.project.SanPham.SanPhamDAO;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class showchonthongtindonhang extends AppCompatActivity {
    private static final String TAG = "showchonthongtindonhang";

    Toolbar toolbar;
    SanPham sanPham;
    ArrayList<SanPham> list;
    int numberOrder = 1, tienSanPham = 0;
    TextView tvTenSanPhamChonThongTin, tvGiaSanPhamChonThongTin, tvSoLuongShowDetal, tvNoiDungChonThongTinDonHang;
    ImageView imgAnhSanPhamChonThongTin, imgMinus, imgPlus;
    Button btnThemVaoGioHang;
    private KhachHang khachHang = null;
    private SanPhamDAO sanPhamDAO;
    private GioHangDAO2 gioHangDAO2;
    private KhachHangDAO khachHangDAO;
    private String userName;
    private String quyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showchonthongtindonhang);

        // Initialize DAOs first
        sanPhamDAO = new SanPhamDAO(this);
        gioHangDAO2 = new GioHangDAO2(this);
        khachHangDAO = new KhachHangDAO(this);

        // Load the current user information
        loadUserInfo();

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        list = (ArrayList<SanPham>) sanPhamDAO.getAll();

        // Get product details from intent
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        final int masanpham = bundle.getInt("masanpham");
        final String tensanpham = bundle.getString("tensanpham", "NULL");
        final String giasanpham = bundle.getString("giasanpham", "NULL");
        final int soluongtrongkho = bundle.getInt("soluongtrongkho", -1);
        final String ngaysanxuat = bundle.getString("ngaysanxuat", "NULL");
        final String hansudung = bundle.getString("hansudung", "NULL");
        final String linkanhsanpham = bundle.getString("linkanhsanpham", "NULL");
        final byte[] anhsanpham = bundle.getByteArray("anhsanpham"); // For binary image data

        // Display product info
        displayProductInfo(tensanpham, giasanpham, soluongtrongkho, ngaysanxuat, hansudung);

        // Load product image
        loadProductImage(linkanhsanpham, anhsanpham, masanpham);

        // Setup quantity controls
        setupQuantityControls(soluongtrongkho);

        // Setup add to cart button
        btnThemVaoGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soluongtrongkho == 0) {
                    Toast.makeText(showchonthongtindonhang.this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verify user information is loaded
                if (quyen.equals("nguoidung") && khachHang == null) {
                    // Try to reload user information if it's null
                    khachHang = khachHangDAO.getUserName(userName);

                    if (khachHang == null) {
                        // If still null, try to ensure the user exists
                        boolean created = khachHangDAO.ensureUserExists(userName);
                        if (created) {
                            khachHang = khachHangDAO.getUserName(userName);
                            Log.d(TAG, "Created and loaded user: " + (khachHang != null ? khachHang.getManguoidung() : "failed"));
                        }

                        if (khachHang == null) {
                            Toast.makeText(showchonthongtindonhang.this,
                                    "Không thể tải thông tin người dùng. Vui lòng đăng nhập lại.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Log.d(TAG, "Using user ID: " + khachHang.getManguoidung() + " for cart item");
                }

                // Create cart item
                GioHang objGioHang = new GioHang();
                objGioHang.setMasanpham(masanpham);
                objGioHang.setTensanpham(tensanpham);

                // Set user ID for the cart item
                if (quyen.equals("nguoidung") && khachHang != null) {
                    objGioHang.setManguoidung(khachHang.getManguoidung());

                    // Check if product is already in cart
                    if (gioHangDAO2.checkValue(masanpham + "", khachHang.getManguoidung() + "") > 0) {
                        Toast.makeText(showchonthongtindonhang.this, "Sản phẩm đã có trong giỏ hàng", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    // Default value for non-user roles
                    objGioHang.setManguoidung(0);
                }

                if (linkanhsanpham != null && !linkanhsanpham.equals("NULL")) {
                    objGioHang.setLinkanhsanpham(linkanhsanpham);
                }

                if (anhsanpham != null) {
                    objGioHang.setAnhsanpham(anhsanpham.toString());
                }

                objGioHang.setSoluong(numberOrder);

                try {
                    objGioHang.setGiasanpham(Integer.parseInt(giasanpham));
                } catch (NumberFormatException e) {
                    Toast.makeText(showchonthongtindonhang.this, "Lỗi định dạng giá sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                long kq = gioHangDAO2.insertGioHang(objGioHang);
                if (kq > 0) {
                    startActivity(new Intent(showchonthongtindonhang.this, MainActivity.class));
                    Toast.makeText(showchonthongtindonhang.this, "Thêm giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(showchonthongtindonhang.this, "Thêm giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadProductImage(String linkanhsanpham, byte[] anhsanpham, int masanpham) {
        try {
            if (linkanhsanpham != null && !linkanhsanpham.equals("NULL")) {
                // Load image from URL using AsyncTask
                new LoadImageFromURLTask().execute(linkanhsanpham);
                Log.d(TAG, "Loading image from URL: " + linkanhsanpham);
            } else if (anhsanpham != null) {
                // Load image from byte array
                Bitmap bitmap = BitmapFactory.decodeByteArray(anhsanpham, 0, anhsanpham.length);
                imgAnhSanPhamChonThongTin.setImageBitmap(bitmap);
                Log.d(TAG, "Loading image from byte array");
            } else {
                // Try to load from SanPhamDAO if not provided in intent
                SanPham product = sanPhamDAO.getID(String.valueOf(masanpham));
                if (product != null) {
                    String productLink = product.getLinkanhsanpham();
                    if (productLink != null && !productLink.isEmpty() && !productLink.equals("NULL")) {
                        new LoadImageFromURLTask().execute(productLink);
                        Log.d(TAG, "Loading image from DAO URL: " + productLink);
                    } else {
                        // Check if product has binary image data in the database
                        String anhSanPhamString = product.getAnhsanpham();
                        if (anhSanPhamString != null && !anhSanPhamString.isEmpty()) {
                            try {
                                // Try to convert to byte array and decode
                                byte[] imageBytes = anhSanPhamString.getBytes();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                if (bitmap != null) {
                                    imgAnhSanPhamChonThongTin.setImageBitmap(bitmap);
                                    Log.d(TAG, "Loading image from binary data in DAO");
                                    return;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error decoding image from DAO binary data", e);
                            }
                        }

                        // If all attempts failed, use default image
                        imgAnhSanPhamChonThongTin.setImageResource(R.drawable.ham);
                        Log.d(TAG, "Setting default image (no image found)");
                    }
                } else {
                    imgAnhSanPhamChonThongTin.setImageResource(R.drawable.ham);
                    Log.d(TAG, "Setting default image (product not found)");
                }
            }
        } catch (Exception e) {
            // If any error occurs, use the default image
            imgAnhSanPhamChonThongTin.setImageResource(R.drawable.ham);
            Log.e(TAG, "Error loading product image", e);
        }
    }

    // AsyncTask to load image from URL without using Picasso
    private class LoadImageFromURLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image", e);
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imgAnhSanPhamChonThongTin.setImageBitmap(result);
            } else {
                // If loading failed, set default image
                imgAnhSanPhamChonThongTin.setImageResource(R.drawable.ham);
            }
        }
    }

    private void loadUserInfo() {
        SharedPreferences preferences = getSharedPreferences("thongtin", MODE_PRIVATE);
        userName = preferences.getString("username", "");
        quyen = preferences.getString("loaitaikhoan", "");

        Log.d(TAG, "Loading user info for: '" + userName + "', role: " + quyen);

        if (quyen.equals("nguoidung") && userName != null && !userName.isEmpty()) {
            // Always use the existing user from the database
            khachHang = khachHangDAO.getUserName(userName);

            if (khachHang != null) {
                Log.d(TAG, "Found user: ID=" + khachHang.getManguoidung() +
                        ", name=" + khachHang.getHoten() +
                        ", username=" + khachHang.getUsername());
            } else {
                Log.d(TAG, "User not found for username: " + userName);
                // Don't create a new user here - we'll handle this when needed
            }
        } else {
            Log.d(TAG, "Not a regular user or empty username");
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.idToolBarGioHang);
        tvTenSanPhamChonThongTin = findViewById(R.id.tvTenSanPhamChonThongTin);
        tvGiaSanPhamChonThongTin = findViewById(R.id.tvGiaSanPhamChonThongTin);
        tvSoLuongShowDetal = findViewById(R.id.tvSoLuongShowDetal);
        tvNoiDungChonThongTinDonHang = findViewById(R.id.tvNoiDungChonThongTinDonHang);
        imgAnhSanPhamChonThongTin = findViewById(R.id.imgAnhSanPhamChonThongTin);
        imgMinus = findViewById(R.id.imgMinus);
        imgPlus = findViewById(R.id.imgPlus);
        btnThemVaoGioHang = findViewById(R.id.btnThemVaoGioHang);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sản phẩm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void displayProductInfo(String tensanpham, String giasanpham, int soluongtrongkho,
                                    String ngaysanxuat, String hansudung) {
        tvTenSanPhamChonThongTin.setText(tensanpham);
        tvGiaSanPhamChonThongTin.setText(giasanpham + " VNĐ");
        tvSoLuongShowDetal.setText(String.valueOf(numberOrder));

        tvNoiDungChonThongTinDonHang.setText(
                "Tên sản phẩm : " + tensanpham
                        + "\n" + "Giá : " + giasanpham + " VNĐ" + "\n"
                        + "Số lượng trong kho: " + soluongtrongkho + "\n"
                        + "Ngày sản xuất : " + ngaysanxuat
                        + "\n" + "Hạn sử dụng : " + hansudung
                        + "\n" + "Thuế : " + 2 + "%"
                        + "\n" + "Phí dịch vụ : " + 2000 + " VNĐ");
    }

    private void setupQuantityControls(final int soluongtrongkho) {
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder < soluongtrongkho) {
                    numberOrder = numberOrder + 1;
                }
                tvSoLuongShowDetal.setText(numberOrder + "");
                if (soluongtrongkho == 0) {
                    Toast.makeText(showchonthongtindonhang.this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder > 1) {
                    numberOrder = numberOrder - 1;
                }
                tvSoLuongShowDetal.setText(numberOrder + "");
            }
        });
    }
}