package com.rusloker.chatclient.server;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread {

    private ServerSocket server;

    public ChatServer(ServerSocket socket) {
        this.server = socket;
    }

    @Override
    public void run() {
        try {
            try  {
                while (true) {
                    Socket clientSocket = server.accept();
                    new Thread(){
                        @Override
                        public void run() {
                            new ClientTalker(clientSocket);
                        }
                    }.start();
                }

            } finally {
                Log.i("Server", "Server is closed!");
                server.close();
            }
        } catch (IOException e) {
            Log.e("Server", e.toString());
        }
    }


}
