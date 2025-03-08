package com.example.project.SanPham;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;

public class DanhGiaSanPham extends AppCompatActivity {
    private RatingBar ratingBar;
    SanPhamDAO sanPhamDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_gia_san_pham);
        sanPhamDAO = new SanPhamDAO(this);

        ratingBar = findViewById(R.id.ratingBar);

        // Đặt sự kiện khi người dùng thay đổi giá trị đánh giá
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Xử lý giá trị đánh giá từ người dùng
                // Ở đây, bạn có thể lưu giá trị rating vào cơ sở dữ liệu hoặc nơi lưu trữ khác
                // Ví dụ:
                long id = sanPhamDAO.addRating(rating);
                saveRatingToDatabase(rating);
                if (id != -1) {
                    // Nếu lưu thành công, hiển thị thông báo cho người dùng
                    Toast.makeText(DanhGiaSanPham.this, "Bạn đã đánh giá " + rating + " sao.", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu lưu không thành công, hiển thị thông báo lỗi
                    Toast.makeText(DanhGiaSanPham.this, "Lưu đánh giá thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Cursor cursor = sanPhamDAO.getAllRatings();
        if (cursor != null) {
            // Xử lý dữ liệu từ cursor (nếu cần)
            cursor.close();
        }
    }

    private void saveRatingToDatabase(float rating) {
        // Đưa giá trị rating vào cơ sở dữ liệu hoặc nơi lưu trữ khác
        // Code xử lý lưu rating vào cơ sở dữ liệu ở đây
    }
}
