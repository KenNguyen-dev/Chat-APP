package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessagingActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.Chats;
import com.example.chatapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context context;
    private List<Chats> chatsList;
    private String imageURL;

    FirebaseUser firebaseUser;

    public  MessageAdapter(Context context, List<Chats> chatsList,String imageURL)
    {
        this.context= context;
        this.chatsList=chatsList;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chats chats = chatsList.get(position);


        if(chats.getType().equals("text"))
        {
            holder.show_message.setText(chats.getMessage());
        }else if(chats.getType().equals("image"))
        {
            Glide.with(context).load(chats.getMessage()).into(holder.image);
        }


        if(imageURL.equals("default"))
        {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else
        {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }

        if(position==chatsList.size()-1)
        {
            if(chats.isIsseen())
            {
                holder.txt_seen.setText("Seen");
            }else
            {
                holder.txt_seen.setText("Delivered");
            }
        }else
        {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_message;
        public ImageView profile_image;
        public ImageView image;

        public TextView txt_seen;


        public ViewHolder( View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            image=itemView.findViewById(R.id.image);


        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }else
            return MSG_TYPE_LEFT;
    }
}

