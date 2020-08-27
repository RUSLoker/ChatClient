package com.rusloker.chatclient;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rusloker.chatclient.databinding.MessageHolderBinding;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemViewHolder> {

    @NonNull
    @Override
    public MessageAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageAdapter.ItemViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ItemViewHolder holder, int position) {
        if (DiscoverServices.services != null) {
            Message service = ChatActivity.messages.get(position);
            holder.bind(service);
        }
    }

    @Override
    public int getItemCount() {
        return ChatActivity.messages.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private MessageHolderBinding binding;
        private ItemViewHolder(@NonNull MessageHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static MessageAdapter.ItemViewHolder from(@NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            MessageHolderBinding binding = MessageHolderBinding.inflate(layoutInflater, parent, false);
            return new MessageAdapter.ItemViewHolder(binding);
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
        }

    }
}
