package com.example.project.ManHinhChinh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.project.R;
import com.example.project.SanPham.SanPham;
import com.example.project.SanPham.SanPhamDAO;
import com.example.project.showchonthongtindonhang;

import java.util.ArrayList;

public class Adapter_SanPhamManHinhChinh extends RecyclerView.Adapter<Adapter_SanPhamManHinhChinh.ViewHolder>{
    private static final String TAG = "SanPhamMainAdapter";

    ArrayList<SanPham> list;
    Context context;
    private SanPhamDAO sanPhamDAO;

    public Adapter_SanPhamManHinhChinh(ArrayList<SanPham> list, Context context, SanPhamDAO sanPhamDAO) {
        this.list = list;
        this.context = context;
        this.sanPhamDAO = sanPhamDAO;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_sanphammanhinhchinh, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SanPham model = list.get(position);

        // Set text views
        holder.tenSanPhamManHinhChinh.setText(model.getTensanpham());
        holder.giaSanPhamManHinhChinh.setText(model.getGiasanpham() + " VNĐ");

        // Load image from URL using Glide
        String imageUrl = model.getLinkanhsanpham();
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("NULL")) {
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(android.R.color.darker_gray)
                        .error(android.R.drawable.ic_menu_gallery)
                        .into(holder.anhSanPhamManHinhChinh);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
                holder.anhSanPhamManHinhChinh.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            // Set a default image
            holder.anhSanPhamManHinhChinh.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Handle admin view
        SharedPreferences preferences = context.getSharedPreferences("thongtin", Context.MODE_PRIVATE);
        String quyen = (preferences.getString("loaitaikhoan", ""));
        if ((quyen.equals("admin"))) {
            holder.imgCartManHinhChinh.setVisibility(View.INVISIBLE);
        }

        // Make entire item clickable for product details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProductDetails(model);
            }
        });

        // Handle cart button click
        holder.imgCartManHinhChinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProductDetails(model);
            }
        });
    }

    private void navigateToProductDetails(SanPham model) {
        Intent intent = new Intent(context, showchonthongtindonhang.class);
        Bundle bundle = new Bundle();
        bundle.putString("linkanhsanpham", model.getLinkanhsanpham());
        bundle.putInt("masanpham", model.getMasanpham());
        bundle.putString("tensanpham", model.getTensanpham());
        bundle.putString("giasanpham", model.getGiasanpham());
        bundle.putInt("soluongtrongkho", model.getSoluongtrongkho());
        bundle.putString("ngaysanxuat", model.getNgaysanxuat());
        bundle.putString("hansudung", model.getHansudung());
        bundle.putString("hello", "hello");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView anhSanPhamManHinhChinh;
        TextView tenSanPhamManHinhChinh, giaSanPhamManHinhChinh;
        ImageView imgCartManHinhChinh;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenSanPhamManHinhChinh = itemView.findViewById(R.id.tenSanPhamManHinhChinh);
            giaSanPhamManHinhChinh = itemView.findViewById(R.id.giaSanPhamManHinhChinh);
            anhSanPhamManHinhChinh = itemView.findViewById(R.id.anhSanPhamManHinhChinh);
            imgCartManHinhChinh = itemView.findViewById(R.id.imgCartManHinhChinh);
        }
    }
}