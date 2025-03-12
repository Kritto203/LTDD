package com.example.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.QuanLyKhachHang.KhachHang;
import com.example.project.QuanLyKhachHang.KhachHangDAO;
import com.example.project.TaiKhoan.TaiKhoan;

public class manhinhlogin extends AppCompatActivity {
    private static final String TAG = "manhinhlogin";

    private EditText edtUser, edtPass;
    private Button btnDangNhap;
    private CheckBox cbLuuMatKhau;
    TextView tvQuenMatKhau;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manhinhlogin);

        edtUser = findViewById(R.id.edtTenDangNhap);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);
        edtPass = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        progressBar = findViewById(R.id.progressBar);
        cbLuuMatKhau = findViewById(R.id.cbNhoMatKhau);

        TaiKhoan taiKhoanDAO = new TaiKhoan(this);
        KhachHangDAO khachHangDAO = new KhachHangDAO(this);

        // Lưu trạng thái của checkbox "Nhớ mật khẩu"
        cbLuuMatKhau.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberPassword", isChecked);
                editor.apply();
            }
        });

        // Kiểm tra trạng thái "Nhớ mật khẩu" khi mở app
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean set = sharedPreferences.getBoolean("rememberPassword", false);
        if (set) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");

            edtUser.setText(savedUsername);
            edtPass.setText(savedPassword);
            cbLuuMatKhau.setChecked(true);
        }

        tvQuenMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPasswordClick(v);
            }
        });

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();

                if (user.equals("")) {
                    Toast.makeText(manhinhlogin.this, "Tên đăng nhập không được để rỗng", Toast.LENGTH_SHORT).show();
                } else if (pass.equals("")) {
                    Toast.makeText(manhinhlogin.this, "Mật khẩu không được để rỗng", Toast.LENGTH_SHORT).show();
                } else if (taiKhoanDAO.checkDangNhap(user, pass)) {
                    // Normalize username before saving (lowercase and trimmed)
                    String normalizedUser = user.toLowerCase().trim();
                    Log.d(TAG, "Login success with normalized username: " + normalizedUser);

                    // Lưu thông tin đăng nhập vào SharedPreferences nếu người dùng chọn "Nhớ mật khẩu"
                    boolean shouldRememberPassword = cbLuuMatKhau.isChecked();
                    if (shouldRememberPassword) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", normalizedUser);
                        editor.putString("password", pass);
                        editor.apply();
                    } else {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("username");
                        editor.remove("password");
                        editor.apply();
                    }

                    // Save user information to application-wide SharedPreferences for use in other activities
                    String loaitaikhoan = taiKhoanDAO.getLoaiTaiKhoan(normalizedUser);
                    SharedPreferences appPrefs = getSharedPreferences("thongtin", MODE_PRIVATE);
                    SharedPreferences.Editor appEditor = appPrefs.edit();
                    appEditor.putString("username", normalizedUser);
                    appEditor.putString("loaitaikhoan", loaitaikhoan);

                    // Get additional user information if available
                    if (loaitaikhoan.equals("nguoidung")) {
                        // Ensure the KhachHang record exists in the database
                        boolean userExists = khachHangDAO.ensureUserExists(normalizedUser);
                        if (userExists) {
                            KhachHang khachHang = khachHangDAO.getUserName(normalizedUser);
                            if (khachHang != null) {
                                appEditor.putString("hoten", khachHang.getHoten());
                                appEditor.putString("email", khachHang.getEmail());
                                Log.d(TAG, "User exists with ID: " + khachHang.getManguoidung());
                            } else {
                                Log.d(TAG, "User still not found after ensuring existence");
                                appEditor.putString("hoten", "User " + normalizedUser);
                                appEditor.putString("email", "");
                            }
                        }
                    } else {
                        // For admin users
                        appEditor.putString("hoten", "Admin");
                        appEditor.putString("email", "admin@example.com");
                    }

                    appEditor.apply();
                    Log.d(TAG, "Saved user information: username=" + normalizedUser + ", role=" + loaitaikhoan);

                    // Hiển thị progressBar và ẩn nút Đăng nhập
                    progressBar.setVisibility(View.VISIBLE);
                    btnDangNhap.setVisibility(View.GONE);

                    // Chạy một Runnable sau 2 giây
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Log check Intent
                            Log.d(TAG, "Start MainActivity after login");
                            Intent intent = new Intent(manhinhlogin.this, MainActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            btnDangNhap.setVisibility(View.VISIBLE);
                        }
                    }, 2000);

                    Toast.makeText(manhinhlogin.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(manhinhlogin.this, "Tài khoản chưa có trên hệ thống", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onForgotPasswordClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Khôi phục mật khẩu");
        builder.setMessage("Vui lòng nhập số điện thoại của bạn");

        final EditText editTextEmail = new EditText(this);
        editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editTextEmail.setText("Đang phát triển !!!!");
        builder.setView(editTextEmail);

        builder.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = editTextEmail.getText().toString().trim();
                Toast.makeText(manhinhlogin.this, "Đang phát triển !!!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}