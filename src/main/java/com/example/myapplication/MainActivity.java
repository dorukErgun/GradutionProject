package com.example.myapplication;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BufferedReader inFromUser;
    DatagramSocket clientSocket;
    InetAddress IPAddress; //192.168.2.158
    int portNumber = 5666;
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    String sentence = "";
    DatagramPacket sendPacket = null;
    DatagramPacket receivePacket = null;
    ArrayList<ClientInfo> clients = null;
    ClientInfo client = null;

    EditText simpleEditText;
    Button simpleButton;

    EditText messageEditText;
    Button messageSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleEditText = (EditText) findViewById(R.id.getFirstInput);
        simpleButton = (Button) findViewById(R.id.button);

        new ServerHandshaker().execute();
        simpleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Connectioner().execute();
            }
        });

    }

    class ServerHandshaker extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            try {
                handshakeTheServer();
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

    class Connectioner extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            try {
                System.out.println("Now i will need the readline.");
                String editTextValue = simpleEditText.getText().toString();
                tryConnection(editTextValue);
            }catch(Exception e) {
                System.out.println("Error happened connecting to the server." + e);
            }
            return "This.";
        }

        @Override
        protected void onPostExecute(String result)
        {
            Constants.setAll(clientSocket, client.address, client.port, portNumber, receiveData);

            Intent i = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(i);

            //new MessageSender().execute();
            super.onPostExecute(result);

        }
    }

    protected void handshakeTheServer()  throws Exception {
        System.out.println("Handshaking the server.");
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        clientSocket = new DatagramSocket(9876);
        clientSocket.setBroadcast(true);
        IPAddress = InetAddress.getByName("16.170.246.167"); //192.168.2.158
        System.out.println("Client is started! Port: " + clientSocket.getLocalPort());

        sentence = "GTUP2PCHATAPPLICATON";
        sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
        clientSocket.send(sendPacket);

        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("Server says " + modifiedSentence);
        ArrayList<ClientInfo> clients = null;
        ClientInfo client = null;
        System.out.println("Handshaking is over.");
    }

    protected void tryConnection(String _sentence)  throws Exception {
        System.out.println("Connecting to the server.");
        do{
            String sentence = _sentence;
            sendData = sentence.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNumber);
            clientSocket.send(sendPacket);

            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            ByteArrayInputStream bis = new ByteArrayInputStream(receiveData);
            ObjectInput in = new ObjectInputStream(bis);
            clients = (ArrayList<ClientInfo>) in.readObject();

            for (int i=0; i < clients.size(); i++) {
                client = clients.get(i);
                System.out.println("Your partner: " + client.address + ":" + client.port);
            }

            bis.close();
            in.close();

        } while(clients.size() < 1);

        System.out.println("Connection is done.");

    }

}