package com.rusloker.chatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.rusloker.chatclient.server.ChatServer;

import java.io.IOException;
import java.net.ServerSocket;

public class MainActivity extends AppCompatActivity {
    ServerSocket serverSocket;
    int localPort;
    NsdManager.RegistrationListener registrationListener;
    private NsdManager nsdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.create).setOnClickListener(v -> {
            ((Button)findViewById(R.id.create)).setEnabled(false);
            ((Button)findViewById(R.id.discover)).setEnabled(false);
            initializeRegistrationListener();
            try {
                initializeServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            registerService(localPort);
        });
        findViewById(R.id.discover).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DiscoverServices.class));
        });
    }

    public void registerService(int port) {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("NsdChat");
        serviceInfo.setServiceType(ChatService.SERVICE_TYPE);
        serviceInfo.setPort(port);

        nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        nsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);

    }

    public void initializeServerSocket() throws IOException {
        // Initialize a server socket on the next available port.
        serverSocket = new ServerSocket(0);

        // Store the chosen port.
        localPort = serverSocket.getLocalPort();
    }

    public void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                ChatService.serviceName = NsdServiceInfo.getServiceName();
                Toast.makeText(MainActivity.this,
                        "Registered successfully " + ChatService.serviceName,
                        Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> {
                    new ChatServer(serverSocket).start();
                    ChatService.host = serverSocket.getInetAddress();
                    ChatService.port = serverSocket.getLocalPort();
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                });
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
                Toast.makeText(MainActivity.this,
                        "Registration failed code: " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
            }
        };
    }

}