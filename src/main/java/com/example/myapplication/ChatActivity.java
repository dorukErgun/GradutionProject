package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Message> list;
    private RecyclerAdapter adapter;

    EditText enteredMessage;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        enteredMessage = (EditText) findViewById(R.id.typedMessage);
        sendButton = (Button) findViewById(R.id.sendMessageButton);

        //SET UP
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        adapter = new RecyclerAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        new MessageSender().execute();

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(i4<i7){
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = recyclerView.getAdapter().getItemCount() == 0 ? 0 : recyclerView.getAdapter().getItemCount()-1;
                            recyclerView.smoothScrollToPosition(pos);
                        }
                    }, 100);
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Message newlyTypedMessage = new Message(enteredMessage.getText().toString(),true);
                int insertIndex = list.size();
                list.add(insertIndex, newlyTypedMessage);
                adapter.notifyItemInserted(insertIndex);
            }
        });

    }

    class MessageSender extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            try {
                initChat();
            }catch(Exception e) {
                System.out.println("Error happened while handshaking the server.");
            }
            return "This.";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

        }
    }

    protected void initChat() throws Exception {
        DatagramSocket clientSocket = Constants.clientSocket;
        InetAddress adress = Constants.adress;
        int port = Constants.port;
        int localPort = Constants.localPort;
        byte[] receiveData = Constants.receiveData;

        System.out.println(clientSocket);
        System.out.println(adress);
        System.out.println(port);
        System.out.println(localPort);
        System.out.println(receiveData);


        ClientSender sender = new ClientSender(clientSocket, adress, port, localPort);
        Thread threadSend = new Thread(sender);
        threadSend.start();

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        while(true){
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            if(receivePacket.getPort() != clientSocket.getLocalPort())
            {
                sender.address = receivePacket.getAddress();
                sender.port = receivePacket.getPort();
                String reply = new String(receivePacket.getData(), 0 ,receivePacket.getLength());
                System.out.println(receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort() + ": " + reply);
                Message newlyTypedMessage = new Message(reply,false);;
                int insertIndex = list.size();
                list.add(insertIndex, newlyTypedMessage);
                adapter.notifyItemInserted(insertIndex);
            }
        }

    }

    public class ClientReceiver implements Runnable {

        DatagramSocket clientSocket;
        ClientSender sender;
        DatagramPacket receivePacket;
        byte[] receiveData;

        public ClientReceiver(DatagramSocket _clientSocket, ClientSender _sender, byte[] _receiveData){
            clientSocket = _clientSocket;
            sender = _sender;
            receiveData = _receiveData;
        }

        @Override
        public void run() {
            while(true){
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                try{
                    clientSocket.receive(receivePacket);
                }catch (Throwable e){
                    System.out.println("Error:" + e.toString());
                }


                sender.address = receivePacket.getAddress();
                sender.port = receivePacket.getPort();
                String reply = new String(receivePacket.getData(), 0 ,receivePacket.getLength());
                Message newlyTypedMessage = new Message(reply,false);
                int insertIndex = list.size();
                list.add(insertIndex, newlyTypedMessage);
                adapter.notifyItemInserted(insertIndex);
                System.out.println(receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort() + ": " + reply);

            }
        }
    }

    public class ClientSender implements Runnable {

        DatagramSocket clientSocket = null;
        byte[] sendData = new byte[1024];
        DatagramPacket sendPacket = null;
        String sentence = "";
        public InetAddress address;
        public int port;
        int localPort;
        public ClientSender(DatagramSocket clientSocket, InetAddress address, int port, int localPort) throws SocketException {
            this.address = address;
            this.port = port;
            this.localPort = localPort;
            this.clientSocket = clientSocket;
        }

        public void run() {
            do{
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Message newlyTypedMessage = new Message(enteredMessage.getText().toString(),true);
                        int insertIndex = list.size();
                        list.add(insertIndex, newlyTypedMessage);
                        adapter.notifyItemInserted(insertIndex);

                        try {
                            sentence = newlyTypedMessage.text;
                            sendData = sentence.getBytes();
                            sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                            clientSocket.send(sendPacket);
                            System.out.println("Data sent to " + address.getHostAddress() + ":" + port);

                            if(!address.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                                sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), localPort);
                                clientSocket.send(sendPacket);
                                System.out.println("Data sent to " + InetAddress.getLocalHost().getHostAddress() + ":" + localPort);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } while(!sentence.equals("quit"));
            clientSocket.close();
        }


    }

}
