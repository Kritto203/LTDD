package com.example.project.TaiKhoan;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project.Database.Database;

public class TaiKhoan {
    SharedPreferences sharedPreferences;
    Database database;

    public TaiKhoan(Context context) {
        database = new Database(context);
        sharedPreferences = context.getSharedPreferences("thongtin",MODE_PRIVATE);
    }
    public boolean checkDangNhap(String username, String password) {
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        // mathuthu, hoten, matkhau, loaitaikhoan
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM nguoidung WHERE username = ? AND password = ?", new String[]{username, password});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username",cursor.getString(0));
            editor.putString("loaitaikhoan",cursor.getString(7));
            editor.putString("hoten",cursor.getString(3));
            editor.putString("email",cursor.getString(5));
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public boolean capnhatmatkhau(String username, String passmoi, String passmoi2) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM nguoidung WHERE manguoidung = ? AND password = ?", new String[]{username, passmoi});
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("password", passmoi2);
            long check = sqLiteDatabase.update("nguoidung", values, "manguoidung = ?", new String[]{username});
            if (check == -1)

                return false;
            return true;

        }
        return false;

    }
}
