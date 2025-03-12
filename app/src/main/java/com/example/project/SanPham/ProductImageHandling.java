package com.example.project.SanPham;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.project.SanPham.ImageHandlerHelper;
import com.example.project.R;

/**
 * Helper class for handling product image functionality in the add/edit dialog
 */
public class ProductImageHandling {

    /**
     * Setup image preview functionality for the add product dialog
     */
    public static void setupAddProductImagePreview(
            Context context,
            Dialog dialog,
            final EditText edtLinkAnhSanPham,
            final ImageView imgXemTruocSanPham,
            final Button btnXemAnhSanPham) {

        btnXemAnhSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = edtLinkAnhSanPham.getText().toString().trim();

                if (imageUrl.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập link ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ImageHandlerHelper.isValidImageUrl(imageUrl)) {
                    Toast.makeText(context, "Link ảnh không hợp lệ. Hãy đảm bảo link bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Try to load the image
                ImageHandlerHelper.loadImage(
                        context,
                        imageUrl,
                        imgXemTruocSanPham,
                        () -> Toast.makeText(context, "Tải ảnh thành công", Toast.LENGTH_SHORT).show(),
                        null
                );
            }
        });
    }

    /**
     * Setup image preview functionality for the edit product dialog
     */
    public static void setupEditProductImagePreview(
            Context context,
            Dialog dialog,
            final EditText edtSuaLinkAnhSanPham,
            final ImageView imgXemTruocSuaSanPham,
            final Button btnXemAnhSuaSanPham) {

        // Load existing image if available
        String initialImageUrl = edtSuaLinkAnhSanPham.getText().toString().trim();
        if (!initialImageUrl.isEmpty() && !initialImageUrl.equals("NULL")) {
            ImageHandlerHelper.loadImage(
                    context,
                    initialImageUrl,
                    imgXemTruocSuaSanPham,
                    null,
                    null
            );
        }

        btnXemAnhSuaSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = edtSuaLinkAnhSanPham.getText().toString().trim();

                if (imageUrl.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập link ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!ImageHandlerHelper.isValidImageUrl(imageUrl)) {
                    Toast.makeText(context, "Link ảnh không hợp lệ. Hãy đảm bảo link bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Try to load the image
                ImageHandlerHelper.loadImage(
                        context,
                        imageUrl,
                        imgXemTruocSuaSanPham,
                        () -> Toast.makeText(context, "Tải ảnh thành công", Toast.LENGTH_SHORT).show(),
                        null
                );
            }
        });
    }
}