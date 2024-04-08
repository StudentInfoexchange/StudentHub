package com.example.studenthub.material_exchange;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studenthub.R;
import com.example.studenthub.chat.ChatUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.MaterialViewHolder> {

    private Context context;
    private List<Material> materials;

    public SubcategoryAdapter(Context context, List<Material> materials) {
        this.context = context;
        this.materials = materials;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Context ct = holder.tvSubCategory.getContext();
        Material material = materials.get(position);
        holder.tvTitle.setText(material.getTitle());
        holder.tvSubCategory.setText(material.getSubCategory());
        holder.tvPrice.setText("$ " + material.getPrice());
        holder.tvContact.setText("Contact: " + material.getContact());
        Glide.with(context).load(material.getImageUrl()).into(holder.iv);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Boolean isAddedByUser = TextUtils.equals(material.getStudentId(), uid);

        if (isAddedByUser) {
            holder.btChat.setText("Edit");
        } else {
            holder.btChat.setText("Chat with Seller");
        }
        holder.btChat.setOnClickListener(v -> {
            if (!isAddedByUser) {
                ChatUtils.openChat(ct, material.getId(), "Material Exchange", material.getStudentId());
            } else {
                Intent i = new Intent(ct, AddMaterialActivity.class);
                i.putExtra("Material", material);
                ct.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvContact, tvSubCategory;
        ImageView iv;
        Button btChat;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubCategory = itemView.findViewById(R.id.tvSubCategory);
            tvContact = itemView.findViewById(R.id.tvContact);
            iv = itemView.findViewById(R.id.iv);
            btChat = itemView.findViewById(R.id.btChat);
        }
    }
}
