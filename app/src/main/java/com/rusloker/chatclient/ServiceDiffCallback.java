package com.rusloker.chatclient;

import android.net.nsd.NsdServiceInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class ServiceDiffCallback extends DiffUtil.ItemCallback<NsdServiceInfo> {
    @Override
    public boolean areItemsTheSame(@NonNull NsdServiceInfo oldItem, @NonNull NsdServiceInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull NsdServiceInfo oldItem, @NonNull NsdServiceInfo newItem) {
        return false;
    }
}
