package com.example.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerRequestAdapter extends RecyclerView.Adapter<RecyclerRequestAdapter.ViewHolder> {
    ArrayList<Request> requests;
    Context applicationContext;

    public RecyclerRequestAdapter(Context applicationContext, ArrayList<Request> requests) {
        this.applicationContext=applicationContext;
        this.requests=requests;
    }

    @NonNull
    @Override
    public RecyclerRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.request_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerRequestAdapter.ViewHolder holder, int position) {
        if(!requests.get(position).helper.equals("null")){
            holder.request.setBackgroundColor(Color.parseColor("#aaaaaa"));
            holder.author.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

        holder.request.setText(requests.get(position).request);

        holder.description.setText(requests.get(position).description);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(applicationContext, "clicked on " +requests.get(position).request, Toast.LENGTH_SHORT).show();
                if(!requests.get(position).id.split("\\.")[1].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(v.getContext(), RequestPreviewActivity.class);
                    intent.putExtra("title", holder.request.getText());
                    intent.putExtra("description", holder.description.getText());
                    intent.putExtra("author", holder.author.getText());
                    intent.putExtra("id", requests.get(position).id);
                    v.getContext().startActivity(intent);
                }else{
                    Intent intent = new Intent(v.getContext(), MyRequestPreviewActivity.class);
                    intent.putExtra("title", holder.request.getText());
                    intent.putExtra("description", holder.description.getText());
                    intent.putExtra("author", holder.author.getText());
                    intent.putExtra("id", requests.get(position).id);
                    v.getContext().startActivity(intent);
                }
            }
        });

        holder.author.setText(requests.get(position).author);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        TextView request, description, author, time;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            request=itemView.findViewById(R.id.itemRequestTv);
            description=itemView.findViewById(R.id.itemDescriptionTv);
            linearLayout=itemView.findViewById(R.id.requestLl);
            author=itemView.findViewById(R.id.itemRequestAuthor);
        }
    }
}
