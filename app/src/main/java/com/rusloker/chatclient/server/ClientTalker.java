package com.rusloker.chatclient.server;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rusloker.chatclient.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientTalker {

    private static final List<ClientTalker> talkers = new LinkedList<>();
    private final Socket socket;
    private BufferedReader in;
    private ObjectOutputStream out;
    private String nickname;

    public ClientTalker(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        try {
            Message nickRequest = new Message("Server", System.currentTimeMillis(),
                    "Write your nickname");
            writeMessage(nickRequest);
            nickname = in.readLine();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) { }
            return;
        }
        new Reader(in).start();
        talkers.add(this);

        Message connection = new Message("Server", System.currentTimeMillis(), nickname + " connected");
        Log.i("Server", nickname + " connected");
        messageReceived(connection);
    }

    void messageReceived(Message msg) {
        Log.i("Server", nickname + ": " + msg);
        for (ClientTalker i : talkers) {
            i.writeMessage(msg);
        }
    }

    void writeMessage(Message msg) {
        try {
            out.writeObject(msg);
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
        Message disconnection = new Message(nickname, System.currentTimeMillis(), "Disconnected");
        messageReceived(disconnection);
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
                    ClientTalker.this.messageReceived(new Message(
                            nickname,
                            System.currentTimeMillis(),
                            msg
                    ));
                } catch (Exception e) {
                    break;
                }
            }
            ClientTalker.this.popClient();
        }
    }
}
