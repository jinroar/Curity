package com.example.curity.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.curity.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class MessageChatAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    ArrayList<MessageChatModel> messageChatModels;
    Context context;

    public MessageChatAdapter(Context context,ArrayList<MessageChatModel> messageChatModels){
        this.messageChatModels = messageChatModels;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_layout_inflater, parent, false);
            return new SentMessageHolder(view);
        }else if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receive_layout_inflater, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else if(viewType == VIEW_TYPE_IMAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_image_layout, parent, false);
            return new SentImageHolder(view);
        }
        else if(viewType == VIEW_TYPE_IMAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receive_image_layout, parent, false);
            return new ReceiveImageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageChatModel message = messageChatModels.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SENT :
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_SENT:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                ((ReceiveImageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageChatModels.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        MessageChatModel messageChatModel = messageChatModels.get(position);
        if(messageChatModel.getViewType() == 1){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else if(messageChatModel.getViewType() == 2){
            return  VIEW_TYPE_MESSAGE_RECEIVED;
        }
        else if(messageChatModel.getViewType() == 3){
            return  VIEW_TYPE_IMAGE_SENT;
        }
        else if(messageChatModel.getViewType() == 4){
            return  VIEW_TYPE_IMAGE_RECEIVED;
        }
        return super.getItemViewType(position);
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView tv_text;
        TextView tv_time;
        ImageView sendImage;
        LinearLayout messageLayout;
        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.message);
            tv_time = itemView.findViewById(R.id.time);
            sendImage = itemView.findViewById(R.id.sendImage);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }

        void bind(MessageChatModel messageChatModel){
            tv_time.setText(messageChatModel.getTime());
            tv_text.setText(messageChatModel.getText());
//            if(messageChatModel.getText().equals("")){
//                messageLayout.setVisibility(View.GONE);
//            }
//            if(messageChatModel.hasImage()){
//                sendImage.setVisibility(View.VISIBLE);
//                Picasso.get().load(messageChatModel.imgUrl)
//                            .resize(400,400)
//                            .into(sendImage);
//            }
//            else if(!messageChatModel.hasImage()){
//                sendImage.setVisibility(View.GONE);
//            }

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView tv_text;
        TextView tv_time;
        ImageView receivedImage;
        LinearLayout messageLayout;
        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.message);
            tv_time = itemView.findViewById(R.id.time);
            receivedImage = itemView.findViewById(R.id.receivedImage);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }

        void bind(MessageChatModel messageChatModel){
            tv_time.setText(messageChatModel.getTime());
            tv_text.setText(messageChatModel.getText());
//            if(messageChatModel.getText().equals("")){
//                messageLayout.setVisibility(View.GONE);
//            }
//            if(messageChatModel.hasImage()){
//                receivedImage.setVisibility(View.VISIBLE);
//                Picasso.get().load(messageChatModel.imgUrl)
//                        .resize(400,400)
//                        .into(receivedImage);
//            }
//            else if(!messageChatModel.hasImage()){
//                receivedImage.setVisibility(View.GONE);
//            }
            ;
        }

    }

    public class ReceiveImageHolder extends RecyclerView.ViewHolder{
        ImageView receivedImage;
        public ReceiveImageHolder(@NonNull View itemView) {
            super(itemView);
            receivedImage = itemView.findViewById(R.id.receivedImage);
        }
        void bind(MessageChatModel messageChatModel){
            Picasso.get().load(messageChatModel.imgUrl)
                    .resize(400,400)
                    .into(receivedImage);
        }
    }

    public class SentImageHolder extends RecyclerView.ViewHolder{
        ImageView sendImage;
        public SentImageHolder(@NonNull View itemView) {
            super(itemView);
            sendImage = itemView.findViewById(R.id.sendImage);
        }
        void bind(MessageChatModel messageChatModel){
            Picasso.get().load(messageChatModel.imgUrl)
                    .resize(400,400)
                    .into(sendImage);
        }
    }
}