package com.example.project;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project.GioHang.GioHangActivity;
import com.example.project.LoaiSanPham.LoaiSanPhamFragment;
import com.example.project.ManHinhChinh.ManHinhChinhFragment;
import com.example.project.QuanLyKhachHang.KhachHang;
import com.example.project.QuanLyKhachHang.KhachHangDAO;
import com.example.project.QuanLyKhachHang.QuanLyKhachHangFragment;
import com.example.project.SanPham.SanPhamFragment;
import com.example.project.TaiKhoan.TaiKhoan;
import com.example.project.ThongKeDoanhThu.ThongKeDoanhThuFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    Toolbar toolbar;
    Bitmap bitmap;

    private KhachHangDAO khachHangDAO;
    private String currentUsername = "";
    private String currentRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DAO
        khachHangDAO = new KhachHangDAO(this);

        // Find views
        toolbar = findViewById(R.id.Toobar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.Nav);
        frameLayout = findViewById(R.id.LayoutConten);
        drawerLayout = findViewById(R.id.Drawer);

        if (savedInstanceState == null) {
            repLaceFragment(ManHinhChinhFragment.newInstance());
            setTitle("Màn hình chính ");
        }

        // Thay đổi avatar
        navigationView.setItemIconTintList(null);
        View header = navigationView.getHeaderView(0);
        TextView txtTen = header.findViewById(R.id.txtTen);
        TextView txtMail = header.findViewById(R.id.txtMail);
        ImageView thaydoianh = header.findViewById(R.id.thaydoianh);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, 0, 0);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Load user information
        loadUserInfo(txtTen, txtMail);

        ActivityResultLauncher<Intent> chooseImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri selectedImageUri = data.getData();
                            if (null != selectedImageUri) {
                                thaydoianh.setImageURI(selectedImageUri);
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) thaydoianh.getDrawable();
                                bitmap = bitmapDrawable.getBitmap();
                            }
                        }
                    }
                });

        thaydoianh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                chooseImage.launch(i);
            }
        });
    }

    private void loadUserInfo(TextView txtTen, TextView txtMail) {
        SharedPreferences sharedPreferences = getSharedPreferences("thongtin", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", "");
        currentRole = sharedPreferences.getString("loaitaikhoan", "");

        Log.d(TAG, "Loading user info: username=" + currentUsername + ", role=" + currentRole);

        // Configure navigation based on user role
        configureNavigation(currentRole);

        // Set user display info
        String hoten = sharedPreferences.getString("hoten", "");
        String email = sharedPreferences.getString("email", "");

        // Check if we have a properly loaded regular user
        if (currentRole.equals("nguoidung") && (hoten.isEmpty() || email.isEmpty())) {
            // Try to get more complete user information
            KhachHang khachHang = khachHangDAO.getUserName(currentUsername);
            if (khachHang != null) {
                // Update SharedPreferences with the complete information
                hoten = khachHang.getHoten();
                email = khachHang.getEmail();

                // Save for future use
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("hoten", hoten);
                editor.putString("email", email);
                editor.apply();

                Log.d(TAG, "Updated user info from database: name=" + hoten + ", email=" + email);
            } else {
                // Ensure user exists in case it was deleted
                boolean created = khachHangDAO.ensureUserExists(currentUsername);
                Log.d(TAG, "User ensured in database: " + created);

                if (created) {
                    khachHang = khachHangDAO.getUserName(currentUsername);
                    if (khachHang != null) {
                        hoten = khachHang.getHoten();
                        email = khachHang.getEmail();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("hoten", hoten);
                        editor.putString("email", email);
                        editor.apply();
                    }
                }
            }
        }

        txtTen.setText(hoten);
        txtMail.setText(email);
    }

    private void configureNavigation(String role) {
        Menu menu = navigationView.getMenu();

        if (!role.equals("admin")) {
            // Regular user - hide admin items
            menu.findItem(R.id.itLoaiSanPham).setVisible(false);
            menu.findItem(R.id.itSanPham).setVisible(false);
            menu.findItem(R.id.itQuanLyHoaDon).setVisible(false);
            menu.findItem(R.id.itQuanLyKhachHang).setVisible(false);
            menu.findItem(R.id.itThongKeDT).setVisible(false);
        } else {
            // Admin - hide user items
            menu.findItem(R.id.itQuanLyDonMua).setVisible(false);
            menu.findItem(R.id.itGioHang).setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itManHinhChinh) {
            toolbar.setTitle(item.getTitle());
            repLaceFragment(ManHinhChinhFragment.newInstance());
            drawerLayout.close();
        } else if (id == R.id.itLoaiSanPham) {
            toolbar.setTitle(item.getTitle());
            repLaceFragment(LoaiSanPhamFragment.newInstance());
            drawerLayout.close();
        } else if (id == R.id.itSanPham) {
            toolbar.setTitle(item.getTitle());
            repLaceFragment(SanPhamFragment.newInstance());
            drawerLayout.close();
        } else if (id == R.id.itQuanLyHoaDon) {
            toolbar.setTitle(item.getTitle());
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = null;
            fragment = new Tablayoutactivity();
            drawerLayout.close();
            fragmentManager.beginTransaction().replace(R.id.LayoutConten, fragment).commit();
        } else if (id == R.id.itQuanLyDonMua) {
            toolbar.setTitle(item.getTitle());
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = null;
            fragment = new TablayoutDonMua();
            drawerLayout.close();
            fragmentManager.beginTransaction().replace(R.id.LayoutConten, fragment).commit();
        } else if (id == R.id.itQuanLyKhachHang) {
            toolbar.setTitle(item.getTitle());
            repLaceFragment(QuanLyKhachHangFragment.newInstance());
            drawerLayout.close();
        } else if (id == R.id.itGioHang) {
            toolbar.setTitle(item.getTitle());
            startActivity(new Intent(MainActivity.this, GioHangActivity.class));
            drawerLayout.close();
        } else if (id == R.id.itThongKeDT) {
            toolbar.setTitle(item.getTitle());
            repLaceFragment(ThongKeDoanhThuFragment.newInstance());
            drawerLayout.close();
        } else if (id == R.id.itDoiMatKhau) {
            showDialogDoiMatKhau();
            drawerLayout.close();
        } else if (id == R.id.itDangXuat) {
            // Clear login state
            clearLoginState();

            Intent intent = new Intent(MainActivity.this, manhinhchao2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            repLaceFragment(ManHinhChinhFragment.newInstance());
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void clearLoginState() {
        // Clear app preferences
        SharedPreferences appPrefs = getSharedPreferences("thongtin", MODE_PRIVATE);
        SharedPreferences.Editor appEditor = appPrefs.edit();
        appEditor.clear();
        appEditor.apply();

        // Clear login preferences if "remember me" isn't checked
        SharedPreferences loginPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean rememberPassword = loginPrefs.getBoolean("rememberPassword", false);
        if (!rememberPassword) {
            SharedPreferences.Editor loginEditor = loginPrefs.edit();
            loginEditor.clear();
            loginEditor.apply();
        }

        Log.d(TAG, "User logged out, login state cleared");
    }

    //Chuyển fragment
    public void repLaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.LayoutConten, fragment);
        fragmentTransaction.commit();
    }

    private void showDialogDoiMatKhau() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.diaalog_doimatkhau, null);
        EditText edtPassCu = view.findViewById(R.id.edtPassCu);
        EditText edtPassMoi = view.findViewById(R.id.edtPassMoi);
        EditText edtPassMoi2 = view.findViewById(R.id.edtPassMoi2);

        builder.setView(view);
        builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Implemented below with setOnClickListener
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passcu = edtPassCu.getText().toString().trim();
                String passmoi = edtPassMoi.getText().toString().trim();
                String passmoi2 = edtPassMoi2.getText().toString().trim();

                if (passmoi.equals(passmoi2)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("thongtin", MODE_PRIVATE);
                    String username = sharedPreferences.getString("username", "");
                    TaiKhoan taiKhoanDAO = new TaiKhoan(MainActivity.this);
                    boolean check = taiKhoanDAO.capnhatmatkhau(username, passcu, passmoi);
                    if (check) {
                        Toast.makeText(MainActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, manhinhlogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Mật khẩu phải trùng với nhau", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if the user information is still valid
        SharedPreferences sharedPreferences = getSharedPreferences("thongtin", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        if (username.isEmpty()) {
            // User information is gone, redirect to login
            Toast.makeText(this, "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, manhinhlogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}