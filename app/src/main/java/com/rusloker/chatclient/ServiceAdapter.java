package com.rusloker.chatclient;

import android.net.nsd.NsdServiceInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.rusloker.chatclient.databinding.NsdServiceHolderBinding;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ItemViewHolder> {

    ServiceClickListener clickListener;

    public ServiceAdapter(ServiceClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ItemViewHolder.from(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (DiscoverServices.services != null) {
            NsdServiceInfo service = DiscoverServices.services.get(position);
            holder.bind(service, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return DiscoverServices.services.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private NsdServiceHolderBinding binding;
        private ItemViewHolder(@NonNull NsdServiceHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static ItemViewHolder from(@NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            NsdServiceHolderBinding binding = NsdServiceHolderBinding.inflate(layoutInflater, parent, false);
            return new ItemViewHolder(binding);
        }

        public void bind(NsdServiceInfo task, ServiceClickListener clickListener) {
            binding.setClickListener(clickListener);
            binding.setService(task);
            binding.executePendingBindings();
        }

    }
}
