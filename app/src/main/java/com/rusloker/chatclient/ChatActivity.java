package com.rusloker.chatclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ChatActivity extends AppCompatActivity {
    private Socket clientSocket; //сокет для общения
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    public static List<Message> messages = new LinkedList<>();
    Gson gson = new GsonBuilder().create();
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageAdapter = new MessageAdapter();
        ((RecyclerView)findViewById(R.id.messageRecyclerView)).setAdapter(messageAdapter);
        try {
            Thread creating = new Thread() {
                @Override
                public void run() {
                    try {
                        clientSocket = new Socket(ChatService.host, ChatService.port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            creating.start();// этой строкой мы запрашиваем
            //  у сервера доступ на соединение
            // читать соообщения с сервера
            creating.join();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // писать туда же
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            new Thread() {
                @Override
                public void run() {
                    while (!clientSocket.isClosed() && clientSocket.isConnected()) {
                        try {
                            String stringMessage = in.readLine();
                            Log.i("Client", stringMessage);
                            Message message = gson.fromJson(stringMessage, Message.class);
                            messages.add(message);
                            runOnUiThread(() -> {
                                messageAdapter.notifyDataSetChanged();
                                ((RecyclerView)findViewById(R.id.messageRecyclerView)).scrollToPosition(messages.size()-1);
                            });
                        } catch (IOException e) {
                            Log.e("Client", e.toString());
                        }
                    }
                }
            }.start();

            findViewById(R.id.sendBut).setOnClickListener(v -> {
                if (!clientSocket.isClosed() && clientSocket.isConnected()) {
                    String word = ((TextView) findViewById(R.id.message)).getText().toString();
                    ((TextView) findViewById(R.id.message)).setText("");
                    try {
                        out.write(word + "\n");
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    out.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException | InterruptedException e) {
            Log.e("Client", e.toString());
            finish();
        }
    }
}