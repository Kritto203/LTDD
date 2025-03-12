package com.example.project.TaiKhoan;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.project.Database.Database;

public class TaiKhoan {
    private static final String TAG = "TaiKhoan";

    SharedPreferences sharedPreferences;
    Database database;

    public TaiKhoan(Context context) {
        database = new Database(context);
        sharedPreferences = context.getSharedPreferences("thongtin", MODE_PRIVATE);
    }

    public boolean checkDangNhap(String username, String password) {
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();

        // Normalize username to lowercase for consistent lookup
        String normalizedUsername = username.trim().toLowerCase();

        try {
            // Use case-insensitive comparison for username
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM nguoidung WHERE LOWER(username) = ? AND password = ?",
                    new String[]{normalizedUsername, password}
            );

            Log.d(TAG, "Login attempt for: " + normalizedUsername + ", found matches: " + cursor.getCount());

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Store user information
                editor.putString("username", normalizedUsername);
                editor.putString("loaitaikhoan", cursor.getString(cursor.getColumnIndex("loaitaikhoan")));
                editor.putString("hoten", cursor.getString(cursor.getColumnIndex("hoten")));
                editor.putString("email", cursor.getString(cursor.getColumnIndex("email")));
                editor.apply();

                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean capnhatmatkhau(String username, String passCu, String passMoi) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

        try {
            // Normalize username to lowercase for consistent lookup
            String normalizedUsername = username.trim().toLowerCase();

            // First check if the old password is correct
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM nguoidung WHERE LOWER(username) = ? AND password = ?",
                    new String[]{normalizedUsername, passCu}
            );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int userId = cursor.getInt(cursor.getColumnIndex("manguoidung"));
                cursor.close();

                // Update the password
                ContentValues values = new ContentValues();
                values.put("password", passMoi);

                long check = sqLiteDatabase.update(
                        "nguoidung",
                        values,
                        "manguoidung = ?",
                        new String[]{String.valueOf(userId)}
                );

                return check != -1;
            } else {
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating password: " + e.getMessage());
            return false;
        }
    }

    public String getLoaiTaiKhoan(String username) {
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        String normalizedUsername = username.trim().toLowerCase();
        String loaiTaiKhoan = "";

        try {
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT loaitaikhoan FROM nguoidung WHERE LOWER(username) = ?",
                    new String[]{normalizedUsername}
            );

            if (cursor.moveToFirst()) {
                loaiTaiKhoan = cursor.getString(cursor.getColumnIndex("loaitaikhoan"));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting user role: " + e.getMessage());
        }

        return loaiTaiKhoan;
    }
}