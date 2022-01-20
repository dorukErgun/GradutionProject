package com.example.myapplication;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Constants {

    public static DatagramSocket clientSocket;
    public static InetAddress adress;
    public static int port;
    public static int localPort;
    public static byte[] receiveData;

    public static void setAll(DatagramSocket _clientSocket, InetAddress _adress, int _port, int _localPort, byte[] _receiveData){
        clientSocket = _clientSocket;
        adress = _adress;
        port = _port;
        localPort = _localPort;
        receiveData = _receiveData;
    }

}
