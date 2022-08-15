package com.blooddonation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.semojis.Helper.EmojiconTextView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    FirebaseUser firebaseUser;
    List<Chats> messages_chat;
    Context context;
    private static final int MESSAGE_TYPE_LEFT = 0;
    private static final int MESSAGE_TYPE_RIGHT = 1;

    public MessageAdapter(List<Chats> chats, Context context) {
        this.messages_chat = chats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MESSAGE_TYPE_RIGHT) {
            v = LayoutInflater.from(context).inflate(R.layout.right_message_item, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.left_message_item, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chats chats = messages_chat.get(position);
        if (chats.getType().equals("message")) {
            holder.show_message.setText(chats.getMsg());
            holder.is_image_seen.setVisibility(View.GONE);
        } else {
            holder.show_message.setVisibility(View.GONE);
            holder.image_msg_lay.setVisibility(View.VISIBLE);
            holder.seen.setVisibility(View.GONE);
            holder.is_image_seen.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(Uri.parse(chats.getMsg()))
                    .into(holder.image_msg);
        }

        if (position == messages_chat.size() - 1) {
            if (chats.isIs_seen()) {
                holder.seen.setText("seen");
//                holder.is_image_seen.setText("seen");
            } else {
                holder.seen.setText("Delivered");
//                holder.is_image_seen.setText("Delivered");
            }
        } else {
            holder.seen.setVisibility(View.GONE);

        }
//        holder.setIsRecyclable(false);
    }


    @Override
    public int getItemCount() {
        return messages_chat.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EmojiconTextView show_message;
        CircleImageView msg_pro;
        TextView seen, is_image_seen;
        ImageView image_msg;
        RelativeLayout image_msg_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_messsage);
            seen = itemView.findViewById(R.id.seen);
            msg_pro = itemView.findViewById(R.id.msg_pro);
            image_msg = itemView.findViewById(R.id.image_message);
            image_msg_lay = itemView.findViewById(R.id.image_msg_lay);
            is_image_seen = itemView.findViewById(R.id.img_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messages_chat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
