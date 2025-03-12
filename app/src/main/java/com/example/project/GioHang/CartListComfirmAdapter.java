package com.example.project.GioHang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DonMua.GioHang;
import com.example.project.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CartListComfirmAdapter extends RecyclerView.Adapter<CartListComfirmAdapter.CartListViewHolder> {
    private static final String TAG = "CartListComfirmAdapter";
    private ArrayList<GioHang> list;

    public CartListComfirmAdapter(ArrayList<GioHang> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cart_list_comfirm_adapter, parent, false);
        return new CartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, int position) {
        final GioHang obj = list.get(position);
        if(obj == null){
            return;
        }

        // Load product image
        loadProductImage(holder.imgSanPham, obj);

        holder.tvTenSP.setText("Sản phẩm: " + obj.getTensanpham());
        holder.tvSoLuong.setText("Số lượng: " + obj.getSoluong() + "");
        holder.tvThanhTien.setText("Thành tiền: " + (Math.round(obj.getSoluong() * obj.getGiasanpham() * 100) / 100) + " VND");
    }

    // Method to load the product image
    private void loadProductImage(ImageView imageView, GioHang gioHang) {
        try {
            String linkAnhSanPham = gioHang.getLinkanhsanpham();
            String anhSanPham = gioHang.getAnhsanpham();

            // First try to load from URL if available
            if (linkAnhSanPham != null && !linkAnhSanPham.isEmpty() && !linkAnhSanPham.equals("NULL")) {
                // Load image from URL using AsyncTask
                new LoadImageFromURLTask(imageView).execute(linkAnhSanPham);
                Log.d(TAG, "Loading image from URL: " + linkAnhSanPham);
            }
            // Then try to load from binary data if available
            else if (anhSanPham != null && !anhSanPham.isEmpty()) {
                try {
                    // Try to convert to byte array and decode
                    byte[] imageBytes = anhSanPham.getBytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Log.d(TAG, "Loading image from binary data");
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error decoding image from binary data", e);
                }

                // If binary data loading failed, set default image
                imageView.setImageResource(R.drawable.logo_color);
            }
            // If no image sources available, set default image
            else {
                imageView.setImageResource(R.drawable.logo_color);
                Log.d(TAG, "Setting default image (no image data)");
            }
        } catch (Exception e) {
            // If any error occurs, use the default image
            imageView.setImageResource(R.drawable.logo_color);
            Log.e(TAG, "Error loading product image", e);
        }
    }

    // AsyncTask to load image from URL
    private class LoadImageFromURLTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageFromURLTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image", e);
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                // If loading failed, set default image
                imageView.setImageResource(R.drawable.logo_color);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class CartListViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSanPham;
        private TextView tvTenSP, tvSoLuong, tvThanhTien;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
            tvTenSP = itemView.findViewById(R.id.tvTenSP);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvThanhTien = itemView.findViewById(R.id.tvThanhTien);
        }
    }
}