package com.example.project.SanPham;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.project.R;

/**
 * Helper class for handling image loading and validation
 */
public class ImageHandlerHelper {
    private static final String TAG = "ImageHandlerHelper";

    /**
     * Load image from URL into ImageView
     *
     * @param context Context for loading
     * @param imageUrl URL of the image
     * @param imageView ImageView to display the image
     * @param successCallback Callback when image loads successfully
     * @param errorCallback Callback when image fails to load
     */
    public static void loadImage(
            Context context,
            String imageUrl,
            ImageView imageView,
            Runnable successCallback,
            Runnable errorCallback) {

        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("NULL")) {
            // Load default image
            imageView.setImageResource(R.drawable.placeholder_image);
            if (errorCallback != null) {
                errorCallback.run();
            }
            return;
        }

        try {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Image load failed: " + e.getMessage());
                            if (errorCallback != null) {
                                errorCallback.run();
                            }
                            Toast.makeText(context, "Không thể tải ảnh từ đường dẫn này", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (successCallback != null) {
                                successCallback.run();
                            }
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage());
            imageView.setImageResource(R.drawable.error_image);
            if (errorCallback != null) {
                errorCallback.run();
            }
            Toast.makeText(context, "Lỗi khi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate if a URL is a valid image URL
     *
     * @param url URL to validate
     * @return true if URL appears to be a valid image URL
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Basic URL validation
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }

        // Check for common image extensions
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") ||
                lowerUrl.endsWith(".jpeg") ||
                lowerUrl.endsWith(".png") ||
                lowerUrl.endsWith(".gif") ||
                lowerUrl.endsWith(".webp") ||
                lowerUrl.contains("image");
    }
}