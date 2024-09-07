package com.example.asignment.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asignment.CarDetail;
import com.example.asignment.R;
import com.example.asignment.models.carModels;

import java.util.List;

public class marketCarAdapter extends RecyclerView.Adapter<marketCarAdapter.CarViewHolder> {
    private List<carModels> cars;
    private Long userId;

    public marketCarAdapter(List<carModels> cars, Long userId) {
        this.cars = cars;
        this.userId = userId;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car,
                parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        carModels car = cars.get(position);
        holder.carModel.setText(car.getModel());
        holder.carCompany.setText(car.getCompany());
        holder.carPrice.setText(String.format("$%d", car.getPrice()));

        if (car.getImagePath() != null && !car.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(car.getImagePath())
                    .into(holder.carImage);
        } else {
            holder.carImage.setImageDrawable(null);
            holder.carImage.setBackgroundColor(Color.LTGRAY);
        }

        holder.btnViewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CarDetail.class);

            intent.putExtra("carId", car.getId());
            intent.putExtra("model", car.getModel());
            intent.putExtra("company", car.getCompany());
            intent.putExtra("description", car.getDescription());
            intent.putExtra("price", String.valueOf(car.getPrice()));
            intent.putExtra("imagePath", car.getImagePath());
            intent.putExtra("status", car.getStatus());
            intent.putExtra("userId", userId);

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carModel;
        TextView carCompany;
        TextView carPrice;
        Button btnViewDetail;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.carImage);
            carModel = itemView.findViewById(R.id.carModel);
            carCompany = itemView.findViewById(R.id.carCompany);
            carPrice = itemView.findViewById(R.id.carPrice);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }
    }
}
