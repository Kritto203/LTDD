package com.example.project.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    static final String DBName = "FoodDelivery";
    static final int DBVersion = 3; // Đảm bảo tăng version nếu có sự thay đổi

    public Database(@Nullable Context context) {
        super(context, DBName, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table Giỏ Hàng
        String ctGioHang = "CREATE TABLE giohang ( masanpham INTEGER  REFERENCES sanpham(masanpham)," +
                "anhsanpham TEXT ," +
                "linkanhsanpham TEXT ," +
                "tensanpham TEXT NOT NULL," +
                "soluong INTEGER NOT NULL," +
                "giasanpham TEXT NOT NULL," +
                "manguoidung INTEGER REFERENCES nguoidung(manguoidung))";
        db.execSQL(ctGioHang);

        // Create table Đánh Giá
        String dbdanhgia = "CREATE TABLE danhgia(id INTEGER PRIMARY KEY AUTOINCREMENT, danhgia REAL, binhluan TEXT)";
        db.execSQL(dbdanhgia);

        // Create table Người Dùng
// In onCreate method, when creating nguoidung table
        String dbnguoidung = "CREATE TABLE nguoidung(manguoidung INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL COLLATE NOCASE," + // Added COLLATE NOCASE
                "password TEXT NOT NULL," +
                "hoten TEXT NOT NULL," +
                "sodienthoai TEXT," + // Allow NULL
                "email TEXT," +
                "diachi TEXT," +
                "loaitaikhoan TEXT)";
        db.execSQL(dbnguoidung);

        // Tạo username và password cho nguoidung
        db.execSQL("INSERT INTO nguoidung VALUES(1,'admin','admin','App ban hang','034562189','bathangban@gmail.com','phan van khoe','admin')," +
                "(2,'thai','12345678','thai','0325896465','thai1@gmail.com','nguyen van cu','nguoidung')");

        // Create table Loại Sản Phẩm
        String ctLoaiSanPham = "CREATE TABLE loaisanpham(maloai INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tenloai TEXT NOT NULL)";
        db.execSQL(ctLoaiSanPham);

        // Tạo loại sản phẩm
        db.execSQL("INSERT INTO loaisanpham VALUES (1, 'Gà'),(2,'Hamburger'),(3, 'HotDog'),(4,'Nước')");

        // Create table Sản Phẩm
        String ctSanPham = "CREATE TABLE sanpham(masanpham INTEGER PRIMARY KEY AUTOINCREMENT," +
                "anhsanpham TEXT," +
                "linkanhsanpham TEXT," +
                "tensanpham TEXT NOT NULL," +
                "giasanpham TEXT NOT NULL," +
                "giamgia TEXT NOT NULL," +
                "soluongtrongkho INTEGER NOT NULL," +
                "maloai INTEGER REFERENCES loaisanpham(maloai)," +
                "ngaysanxuat TEXT NOT NULL," +
                "hansudung TEXT NOT NULL)";
        db.execSQL(ctSanPham);

        // Tạo sản phẩm mẫu
// Update this line in your Database.java file
// Tạo sản phẩm mẫu
        db.execSQL("INSERT INTO sanpham VALUES (1, 'NONE', 'https://vietlotusfoods.com.vn/wp-content/uploads/2020/10/427e7a3136f84a-4mingggin.png', 'Truyền Thống', '90000','1000',20,1,'08/03/2025','8/03/2025')," +
                "(2, 'NONE', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Cheeseburger.jpg/800px-Cheeseburger.jpg', 'Bò phô mai', '70000','1000',20,2,'08/03/2025','08/03/2025'), " +
                "(3, 'NONE', 'https://cdn.uengage.io/uploads/49314/image-276624-1723208111.jpeg', 'Xúc xích heo phô mai', '300000','10',18,3,'08/03/2025','08/03/2025')," +
                "(4, 'NONE', 'https://cdn.tgdd.vn/Files/2017/11/14/1041443/moi-ngay-uong-1-lon-coca-cola-co-sao-khong-nen-uong-bao-nhieu-la-tot-202112300854488863.jpg', 'Coca cola', '15000','10',18,4,'08/03/2025','029/03/2025'),"+
                "(5, 'NONE', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlZUwrwF6QsAo7kSqmoVw2I2D6cWTU2UAKKA&s', 'Tài lộc quá lớn', '500000','10',18,4,'08/03/2025','08/03/2025')");
        // Create table Hóa Đơn
        String ctHoaDon = "CREATE TABLE hoadon (" +
                "id INTEGER," +
                "mahoadon INTEGER PRIMARY KEY," +
                "ngaymua TEXT NOT NULL," +
                "tongtien TEXT NOT NULL," +
                "trangthai INTEGER NOT NULL," +
                "masanpham INTEGER REFERENCES sanpham(masanpham)," +
                "manguoidung INTEGER REFERENCES nguoidung(manguoidung))";
        db.execSQL(ctHoaDon);

        // Create table Chi Tiết Đơn Hàng
        String sql_chitietdonhang = "CREATE TABLE chitietdonhang (" +
                "mahoadon INTEGER REFERENCES hoadon(mahoadon)," +
                "masanpham INTEGER REFERENCES sanpham(masanpham)," +
                "dongia INTEGER NOT NULL," +
                "soluong INTEGER NOT NULL)";
        db.execSQL(sql_chitietdonhang);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            String dropTableNguoiDung = "DROP TABLE IF EXISTS nguoidung";
            db.execSQL(dropTableNguoiDung);
            String dropTableLoaiSanPham = "DROP TABLE IF EXISTS loaisanpham";
            db.execSQL(dropTableLoaiSanPham);
            String dropTableSanPham = "DROP TABLE IF EXISTS sanpham";
            db.execSQL(dropTableSanPham);
            String dropHoaDon = "DROP TABLE IF EXISTS hoadon";
            db.execSQL(dropHoaDon);
            String dropGioHang = "DROP TABLE IF EXISTS giohang";
            db.execSQL(dropGioHang);
            String dropTableChiTietDonHang = "DROP TABLE IF EXISTS chitietdonhang";
            db.execSQL(dropTableChiTietDonHang);
            String dropTableDanhGia = "DROP TABLE IF EXISTS danhgia";
            db.execSQL(dropTableDanhGia);
            onCreate(db);
        }
    }
}
