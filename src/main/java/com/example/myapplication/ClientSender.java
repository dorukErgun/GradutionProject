package com.example.myapplication;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ClientSender extends AppCompatActivity implements Runnable {
	
	DatagramSocket clientSocket = null;
	BufferedReader inFromUser = null;
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
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run() {

        do{
			try {
				System.out.print("Please print something clientSender:");
				sentence = inFromUser.readLine();
				sendData = sentence.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
				clientSocket.send(sendPacket);
				System.out.println("Data sent to " + address.getHostAddress() + ":" + port);

				if(!address.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) // Send a local packet if peers behind the same NAT
				{
					sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), localPort);
					clientSocket.send(sendPacket);
					System.out.println("Data sent to " + InetAddress.getLocalHost().getHostAddress() + ":" + localPort);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } while(!sentence.equals("quit"));
        	clientSocket.close();
	}
}
