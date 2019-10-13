package com.example.studentportalfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPEOne =1;
    private static final int TYPETwo = 2;

    private ArrayList<UserModel> myData;
    Context context;

    public MyAdapter(ArrayList<UserModel> myData,Context context) {
        this.myData = myData;
        this.context=context;
    }

    @Override
    public int getItemViewType(int position)
    {
        UserModel obj=myData.get(position);
        if(obj.getType() == TYPEOne)
        {
            return TYPEOne;
        }
        else if(obj.getType() == TYPETwo)
        {
            return TYPETwo;
        }
        else return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPEOne)
        {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.rowdesign_recycler,parent,false);
            return new ConfirmedHolder(v);
        }
        else {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.rowdesign_recycler2,parent,false);
            return new UnConfirmedHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPEOne:
                confirmedDetail((ConfirmedHolder)holder,position);
                break;
            case TYPETwo:
                unconfirmedDetail((UnConfirmedHolder)holder,position);
                break;

        }
    }

    private void confirmedDetail(ConfirmedHolder holder, final int pos)
    {
        holder.textView.setText(myData.get(pos).getEmail());
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount(myData.get(pos).getEmail());
            }
        });
    }

    private void deleteAccount(String email) {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("User");
        Query query=db.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    snapshot.getRef().removeValue();
                    Toast.makeText(context,"Account Removed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Error! Try Again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unconfirmedDetail(UnConfirmedHolder holder, final int pos)
    {
        holder.textView1.setText(myData.get(pos).getEmail());
        holder.btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(myData.get(pos).getEmail());
            }
        });
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeData(myData.get(pos).getEmail());
            }
        });
    }

    private void removeData(String email) {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("User");
        Query query=db.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    snapshot.getRef().removeValue();
                    Toast.makeText(context,"Request Removed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Error! Try Again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData(String email) {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("User");
        Query query=db.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    snapshot.getRef().child("type").setValue(1);
                    Toast.makeText(context,"Request Approved",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context,"Error! Try Again",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public static class ConfirmedHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button btn_remove;
        public ConfirmedHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_emailId_row);
            btn_remove = itemView.findViewById(R.id.btn_delete_row1);
        }
    }
    public static class UnConfirmedHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        Button btn_approve,btn_remove;
        public UnConfirmedHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.txt_emailId_row2);
            btn_approve = itemView.findViewById(R.id.btn_approve_row2);
            btn_remove = itemView.findViewById(R.id.btn_delete_row2);
        }
    }
}
