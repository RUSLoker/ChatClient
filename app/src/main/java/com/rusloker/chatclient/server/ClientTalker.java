package com.rusloker.chatclient.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientTalker {

    private static final List<ClientTalker> talkers = new LinkedList<>();
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickname;

    public ClientTalker(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            return;
        }
        try {
            out.write("Write your nickname:\n");
            out.flush();
            nickname = in.readLine();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) { }
            return;
        }
        new Reader(in).start();
        talkers.add(this);
        Log.i("Server", nickname + " connected");
        messageReceived(nickname + " connected");
        try {
            out.write("Connected\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void messageReceived(String msg) {
        Log.i("Server", nickname + ": " + msg);
        for (ClientTalker i : talkers) {
            if(i == this) {
                continue;
            }
            i.writeMessage(nickname + ":  " + msg);
        }
    }

    void writeMessage(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            popClient();
        }
    }

    void popClient() {
        talkers.remove(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageReceived(nickname + " disconnected");
        Log.i("Server", nickname + " disconnected");
    }

    private class Reader extends Thread {
        private final BufferedReader reader;
        public Reader(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            super.run();
            while (!ClientTalker.this.socket.isClosed()) {
                try {
                    String msg = reader.readLine();
                    if (msg == null) {
                        break;
                    }
                    ClientTalker.this.messageReceived(msg);
                } catch (Exception e) {
                    break;
                }
            }
            ClientTalker.this.popClient();
        }
    }
}
