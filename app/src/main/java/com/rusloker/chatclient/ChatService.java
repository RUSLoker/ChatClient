package com.rusloker.chatclient;

import android.net.nsd.NsdManager;

import java.net.InetAddress;

public final class ChatService {
    public static final String SERVICE_TYPE = "_nsdchat._tcp.";
    public static String serviceName;
    public static InetAddress host;
    public static int port;

}
