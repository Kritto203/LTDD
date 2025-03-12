package com.example.project.FragmentHoaDon;

import android.content.Context;
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

import com.example.project.DonMua.HoaDonChiTiet;
import com.example.project.R;
import com.example.project.SanPham.SanPham;
import com.example.project.SanPham.SanPhamDAO;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HoaDonChiTietAdapter extends RecyclerView.Adapter<HoaDonChiTietAdapter.HoaDonChiTietViewHolder> {
    private static final String TAG = "HoaDonChiTietAdapter";
    private Context context;
    private List<HoaDonChiTiet> list;
    private SanPhamDAO sanPhamDAO;

    public HoaDonChiTietAdapter(Context context, List<HoaDonChiTiet> list) {
        this.context = context;
        this.list = list;
        this.sanPhamDAO = new SanPhamDAO(context);
    }

    @NonNull
    @Override
    public HoaDonChiTietViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_hoa_don_chi_tiet, parent, false);
        return new HoaDonChiTietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoaDonChiTietViewHolder holder, int position) {
        HoaDonChiTiet hoaDonChiTiet = list.get(position);
        if (hoaDonChiTiet == null) {
            return;
        }

        // Set order ID
        holder.tvMaHoaDon.setText(String.valueOf(hoaDonChiTiet.getMaHoaDon()));

        // Get product details from the database
        SanPham sanPham = sanPhamDAO.getID(String.valueOf(hoaDonChiTiet.getMaSP()));

        // Set product name from SanPham object
        if (sanPham != null) {
            holder.tvTenSanPham.setText(sanPham.getTensanpham());

            // Load product image
            loadProductImage(holder.imgAvataSanPham, sanPham);
        } else {
            // Default values if product not found
            holder.tvTenSanPham.setText("Unknown Product");
            holder.imgAvataSanPham.setImageResource(R.drawable.laoisp);
        }

        // Set quantity
        holder.tvSoLuong.setText(String.valueOf(hoaDonChiTiet.getSoLuong()));

        // Calculate and set total price
        double tongTien = hoaDonChiTiet.getSoLuong() * hoaDonChiTiet.getDonGia();
        holder.tvTongTien.setText(tongTien + " VNĐ");
    }

    // Method to load the product image
    private void loadProductImage(ImageView imageView, SanPham sanPham) {
        try {
            String linkAnhSanPham = sanPham.getLinkanhsanpham();
            String anhSanPham = sanPham.getAnhsanpham();

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
                imageView.setImageResource(R.drawable.laoisp);
            }
            // If no image sources available, set default image
            else {
                imageView.setImageResource(R.drawable.laoisp);
                Log.d(TAG, "Setting default image (no image data)");
            }
        } catch (Exception e) {
            // If any error occurs, use the default image
            imageView.setImageResource(R.drawable.laoisp);
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
                imageView.setImageResource(R.drawable.laoisp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class HoaDonChiTietViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvataSanPham;
        private TextView tvMaHoaDon, tvTenSanPham, tvSoLuong, tvTongTien;

        public HoaDonChiTietViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvataSanPham = itemView.findViewById(R.id.imgAvataSanPham);
            tvMaHoaDon = itemView.findViewById(R.id.tvMaHoaDon);
            tvTenSanPham = itemView.findViewById(R.id.tvTenSanPham);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvTongTien = itemView.findViewById(R.id.tvTongTien);
        }
    }
}