package com.rusloker.chatclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DiscoverServices extends AppCompatActivity {
    private static final String TAG = "Discovery";
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener resolveListener;
    public static List<NsdServiceInfo> services = new ArrayList<>();
    ServiceAdapter serviceAdapter;
    private NsdManager nsdManager;
    private NsdServiceInfo mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_services);

        nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        initializeDiscoveryListener();
        initializeResolveListener();

/*        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("Test");
        serviceInfo.setServiceType(ChatService.SERVICE_TYPE);

        services.add(serviceInfo);*/

        serviceAdapter = new ServiceAdapter(service -> {
            nsdManager.resolveService(service, resolveListener);
        });
        ((RecyclerView) findViewById(R.id.recyclerView)).setAdapter(serviceAdapter);

        nsdManager.discoverServices(
                ChatService.SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(ChatService.SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(ChatService.serviceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + ChatService.serviceName);
                }

                services.add(service);
                runOnUiThread(() -> {
                    serviceAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
                for (int i = 0; i < services.size(); i++) {
                    NsdServiceInfo cur = services.get(i);
                    if (cur.getServiceName().equals(service.getServiceName())
                            && cur.getServiceType().equals(service.getServiceType())){
                        services.remove(i);
                        i--;
                    }
                }
                runOnUiThread(() -> {
                    serviceAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(ChatService.serviceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
            }
        };
    }

}