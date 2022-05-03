package com.example.restez.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.restez.MainActivity;
import com.example.restez.Model.AppModel;
import com.example.restez.R;
import com.example.restez.UpdateSelection;
import com.example.restez.Utils.DatabaseHandler;

import java.util.List;

public class AppModelAdapter extends RecyclerView.Adapter<AppModelAdapter.ViewHolder> {

    private List<AppModel> appList;
    private Activity activity;
    private DatabaseHandler db;

    public static final int RESULT_OK = 1000;

    public static final int SELECTION_ACT = 56789;


    public AppModelAdapter(DatabaseHandler db, Activity activity) {
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.tracker_item, parent, false);
        return new ViewHolder(itemView);

    }

    public int getItemCount() {
        return appList.size();
    }

    public void setTasks(List<AppModel> appList) {
        this.appList = appList;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        AppModel item = appList.get(position);
        holder.app.setText(item.getName() + " " + item.getSearch());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(holder.getAdapterPosition());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UpdateSelection.class);
                intent.putExtra("ID", item.getId());
                notifyItemChanged(position);
                ((Activity) view.getContext()).startActivity(intent);

            }
        });


    }

    public void deleteItem(int position) {
        AppModel item = appList.get(position);
        db.deleteApp(item.getId());
        appList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateMonitor(int position, String query, int type) {
        AppModel item = appList.get(position);
        db.updateMonitorType(item.getId(), query, type);
        item.setSearch(query);
        notifyItemChanged(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app;
        ImageButton edit, delete;
        Context context;
        ViewHolder(View view) {
            super(view);
            app = view.findViewById(R.id.appCheckBox);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.remove);
            context = view.getContext();
        }
    }




}
