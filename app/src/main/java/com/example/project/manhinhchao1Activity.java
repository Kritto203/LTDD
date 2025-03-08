package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
public class manhinhchao1Activity extends AppCompatActivity {
    ImageView ivBbkg;
    long startDelayMillis = 2000;
    ImageView ivlogo;
    TextView ivname;
    View view;
    ImageView viewarc;
    TextView viewslogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manhinhchao1);
        ivBbkg = findViewById(R.id.ivBbkg);
        ivlogo = findViewById(R.id.ivlogo);
        ivname = findViewById(R.id.ivname);
        view =   findViewById(R.id.view);
        viewarc= findViewById(R.id.viewarc);
        viewslogan = findViewById(R.id.viewslogan);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Bắt đầu chạy animation của ivBbkg
                ivBbkg.animate().translationY(9000).setDuration(3500);
                ivname.animate().translationY(-900).setDuration(4000);
                ivlogo.animate().translationY(9000).setDuration(3500);
                view.animate().translationY(-900).setDuration(4000);
                viewarc.animate().translationY(9000).setDuration(3500);
                viewslogan.animate().translationY(-900).setDuration(4000);
                // Sau khi animation của ivBbkg kết thúc
                // Chuyển sang màn hình tiếp theo
                ivBbkg.animate().setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(manhinhchao1Activity.this, manhinhchao2Activity.class);
                        startActivity(intent);
                        finish(); // Đóng activity hiện tại để không quay lại khi nhấn nút Back
                    }
                });
            }
        }, startDelayMillis);
    }
}
