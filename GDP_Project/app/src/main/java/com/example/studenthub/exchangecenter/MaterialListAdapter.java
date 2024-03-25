package com.example.studenthub.material_exchange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studenthub.R;
import com.example.studenthub.accommodation.Accommodation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.ViewHolder> {

    private Context context;
    private HashMap<String, List<Material>> groupedMaterials;

    public MaterialListAdapter(Context context, HashMap<String, List<Material>> groupedMaterials) {
        this.context = context;
        this.groupedMaterials = groupedMaterials;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_materials, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<String> keysList = new ArrayList<>(groupedMaterials.keySet());
        String key = keysList.get(position);
        holder.headerTextView.setText(key);
        SubcategoryAdapter subcategoryAdapter = new SubcategoryAdapter(context, groupedMaterials.get(key));
        holder.recyclerView.setAdapter(subcategoryAdapter);
    }

    @Override
    public int getItemCount() {
        return groupedMaterials.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.listHeader);
            recyclerView = itemView.findViewById(R.id.subRecyclerView);
        }
    }
}
