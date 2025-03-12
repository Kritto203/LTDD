package com.example.project.QuanLyKhachHang;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.project.Database.Database;

import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    Database database;
    SQLiteDatabase db;
    private static final String TAG = "KhachHangDAO"; // For logging

    public KhachHangDAO(Context context) {
        database = new Database(context);
        db = database.getWritableDatabase();
    }

    @SuppressLint("Range")
    public ArrayList<KhachHang> getDSKhachHang() {
        ArrayList<KhachHang> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM nguoidung", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                KhachHang khachHang = new KhachHang();
                khachHang.setManguoidung(cursor.getInt(cursor.getColumnIndex("manguoidung")));
                khachHang.setHoten(cursor.getString(cursor.getColumnIndex("hoten")));
                khachHang.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                khachHang.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                khachHang.setSodienthoai(cursor.getString(cursor.getColumnIndex("sodienthoai")));
                khachHang.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                khachHang.setDiachi(cursor.getString(cursor.getColumnIndex("diachi")));
                khachHang.setLoaitaikhoan(cursor.getString(cursor.getColumnIndex("loaitaikhoan")));
                list.add(khachHang);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean themkhachhang(String hoten, String username, String password, String sodienthoai, String email, String diachi, String loaitaikhoan) {
        Log.d(TAG, "Adding new user: username=" + username + ", hoten=" + hoten + ", loaitaikhoan=" + loaitaikhoan);

        // First check if user already exists to avoid duplicates
        if (checkUserName(username) == -1) {
            Log.d(TAG, "User already exists, not adding duplicate");
            return false;
        }

        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoten", hoten);
        values.put("username", username);
        values.put("password", password);
        values.put("sodienthoai", sodienthoai);
        values.put("email", email);
        values.put("diachi", diachi);
        values.put("loaitaikhoan", loaitaikhoan);
        long check = sqLiteDatabase.insert("nguoidung", null, values);

        // Get and log the ID of the newly created user
        if (check != -1) {
            KhachHang newUser = getUserName(username);
            if (newUser != null) {
                Log.d(TAG, "Created new user with ID: " + newUser.getManguoidung());
            }
        }

        return check != -1;
    }

    public KhachHang getID(String id) {
        String sql = "SELECT * FROM nguoidung WHERE manguoidung=?";
        List<KhachHang> list = getData(sql, id);
        if (!list.isEmpty()) {
            return list.get(0);  // Lấy phần tử đầu tiên trong danh sách
        }
        return null;  // Không tìm thấy kết quả
    }

    public int update(KhachHang obj) {
        Log.d(TAG, "Updating user: ID=" + obj.getManguoidung() + ", username=" + obj.getUsername());

        ContentValues values = new ContentValues();
        values.put("hoten", obj.getHoten());
        values.put("username", obj.getUsername());
        values.put("password", obj.getPassword());
        values.put("sodienthoai", obj.getSodienthoai());
        values.put("email", obj.getEmail());
        values.put("diachi", obj.getDiachi());
        values.put("loaitaikhoan", obj.getLoaitaikhoan());

        int result = db.update("nguoidung", values, "manguoidung=?", new String[]{String.valueOf(obj.getManguoidung())});
        Log.d(TAG, "Update result: " + (result > 0 ? "success" : "failed"));
        return result;
    }

    @SuppressLint("Range")
    public List<KhachHang> getData(String sql, String... selectionArgs) {
        ArrayList<KhachHang> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();

        try {
            Cursor cursor = sqLiteDatabase.rawQuery(sql, selectionArgs); // Chạy truy vấn với các tham số
            Log.d(TAG, "Query executed: " + sql + " with args: " + (selectionArgs != null && selectionArgs.length > 0 ? selectionArgs[0] : "null"));
            Log.d(TAG, "Cursor count: " + cursor.getCount());

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                do {
                    KhachHang khachHang = new KhachHang();
                    khachHang.setManguoidung(cursor.getInt(cursor.getColumnIndex("manguoidung")));
                    khachHang.setHoten(cursor.getString(cursor.getColumnIndex("hoten")));
                    khachHang.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                    khachHang.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                    khachHang.setSodienthoai(cursor.getString(cursor.getColumnIndex("sodienthoai")));
                    khachHang.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                    khachHang.setDiachi(cursor.getString(cursor.getColumnIndex("diachi")));
                    khachHang.setLoaitaikhoan(cursor.getString(cursor.getColumnIndex("loaitaikhoan")));
                    list.add(khachHang);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in getData: " + e.getMessage());
        }

        return list;
    }

    // Modified getUserName method
    public KhachHang getUserName(String username) {
        if (username == null || username.isEmpty()) {
            Log.e(TAG, "getUserName called with null or empty username");
            return null;
        }

        try {
            // Normalize the username by trimming whitespace and converting to lowercase
            username = username.trim().toLowerCase();

            // Use case-insensitive query for better matching
            String sql = "SELECT * FROM nguoidung WHERE LOWER(username)=?";
            Log.d(TAG, "Looking for user with normalized username: '" + username + "'");

            // Checking if the user exists first
            Cursor cursor = db.rawQuery(sql, new String[]{username});
            int count = cursor.getCount();
            cursor.close();

            Log.d(TAG, "Found " + count + " users with username: " + username);

            if (count == 0) {
                return null;
            }

            List<KhachHang> list = getData(sql, username);
            if (!list.isEmpty()) {
                KhachHang user = list.get(0);
                Log.d(TAG, "Found user: manguoidung=" + user.getManguoidung() +
                        ", username='" + user.getUsername() + "'");
                return user;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getUserName: " + e.getMessage());
        }

        return null;
    }

    // Add ensureUserExists method if it doesn't exist
    public boolean ensureUserExists(String username) {
        if (username == null || username.isEmpty()) {
            Log.e(TAG, "Cannot ensure existence of user with empty username");
            return false;
        }

        // Normalize the username
        username = username.trim().toLowerCase();

        // Try to get the user
        KhachHang user = getUserName(username);

        if (user == null) {
            Log.d(TAG, "User '" + username + "' not found, creating new record");

            // Create a basic user record
            boolean result = themkhachhang(
                    "User " + username,  // hoten
                    username,            // username
                    "default",           // password
                    "",                  // sodienthoai
                    "",                  // email
                    "",                  // diachi
                    "nguoidung"          // loaitaikhoan
            );

            // Verify the user was created successfully
            if (result) {
                user = getUserName(username);
                if (user != null) {
                    Log.d(TAG, "Successfully created user with ID: " + user.getManguoidung());
                    return true;
                } else {
                    Log.e(TAG, "Failed to retrieve newly created user");
                    return false;
                }
            } else {
                Log.e(TAG, "Failed to create new user record");
                return false;
            }
        } else {
            Log.d(TAG, "User already exists with ID: " + user.getManguoidung());
            return true;
        }
    }

    public boolean capNhapThongTin(int manguoidung, String hoten, String username, String email, String diachi) {
        Log.d(TAG, "Updating user info: ID=" + manguoidung + ", username=" + username);

        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hoten", hoten);
        values.put("username", username);
        values.put("email", email);
        values.put("diachi", diachi);
        long check = sqLiteDatabase.update("nguoidung", values, "manguoidung = ?", new String[]{String.valueOf(manguoidung)});

        Log.d(TAG, "Update result: " + (check != -1 ? "success" : "failed"));
        return check != -1;
    }

    public int xoaThongTinThanhVien(int manguoidung) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM hoadon WHERE manguoidung = ?", new String[]{String.valueOf(manguoidung)});
        if (cursor.getCount() != 0) {
            return -1;  // Không thể xóa nếu có hóa đơn liên quan
        }
        long check = sqLiteDatabase.delete("nguoidung", "manguoidung = ?", new String[]{String.valueOf(manguoidung)});
        return check == -1 ? 0 : 1;  // 0 nếu xóa thất bại, 1 nếu thành công
    }

    public int checkSoDienThoai(String soDienThoai) {
        Cursor cursor = db.rawQuery("SELECT * FROM nguoidung WHERE sodienthoai = ?", new String[]{soDienThoai});
        return cursor.getCount() != 0 ? -1 : 1;  // Trả về -1 nếu số điện thoại đã tồn tại
    }

    public int checkUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            return 1; // Consider empty username as not existing
        }

        // Normalize username for consistent checking
        userName = userName.trim().toLowerCase();

        Cursor cursor = db.rawQuery("SELECT * FROM nguoidung WHERE LOWER(username) = ?", new String[]{userName});
        int result = cursor.getCount() != 0 ? -1 : 1;
        cursor.close();
        return result;  // Trả về -1 nếu username đã tồn tại
    }

    public int checkGmail(String gmail) {
        Cursor cursor = db.rawQuery("SELECT * FROM nguoidung WHERE email = ?", new String[]{gmail});
        return cursor.getCount() != 0 ? -1 : 1;  // Trả về -1 nếu email đã tồn tại
    }

 /*   // Helper method to check if user exists and create if not
    public boolean ensureUserExists(String username) {
        if (username == null || username.isEmpty()) {
            Log.e(TAG, "Cannot ensure existence of user with empty username");
            return false;
        }

        // First normalize the username
        username = username.trim().toLowerCase();

        // Try to get the user
        KhachHang user = getUserName(username);

        if (user == null) {
            Log.d(TAG, "User '" + username + "' not found, creating new record");

            // Create a basic user record
            boolean result = themkhachhang(
                    "User " + username,  // hoten
                    username,            // username
                    "default",           // password (should be secure in production)
                    "",                  // sodienthoai
                    "",                  // email
                    "",                  // diachi
                    "nguoidung"          // loaitaikhoan
            );

            // Verify the user was created successfully
            if (result) {
                user = getUserName(username);
                if (user != null) {
                    Log.d(TAG, "Successfully created user with ID: " + user.getManguoidung());
                    return true;
                } else {
                    Log.e(TAG, "Failed to retrieve newly created user");
                    return false;
                }
            } else {
                Log.e(TAG, "Failed to create new user record");
                return false;
            }
        } else {
            Log.d(TAG, "User already exists with ID: " + user.getManguoidung());
            return true;
        }
    }
*/
    // Helper method to debug the database
    public void debugDatabase() {
        Log.d(TAG, "==== DATABASE DEBUG ====");

        // Count total users
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM nguoidung", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        Log.d(TAG, "Total users in database: " + count);

        // List all users
        cursor = db.rawQuery("SELECT manguoidung, username, hoten, loaitaikhoan FROM nguoidung", null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("manguoidung"));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("hoten"));
                @SuppressLint("Range") String role = cursor.getString(cursor.getColumnIndex("loaitaikhoan"));

                Log.d(TAG, "User: ID=" + id + ", username='" + username + "', name='" + name + "', role=" + role);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d(TAG, "==== END DATABASE DEBUG ====");
    }
}