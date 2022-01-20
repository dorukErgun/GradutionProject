package Mainserver;
import com.example.myapplication.ClientInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server {
    public static void main(String args[]) throws Exception {

        boolean newClient;
        DatagramSocket socket = new DatagramSocket(5666);
        ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
        System.out.println(getTimeStamp() + " Server is started and expecting clients...");

        while (true) {
            newClient = true;
            byte[] receivedData = new byte[1024];

            DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
            socket.receive(receivedPacket);

            System.out.println(getTimeStamp() + " Server received a packet!");

            for (int i=0; i < clients.size(); i++) {
                ClientInfo client = clients.get(i);
                if(client.address.equals(receivedPacket.getAddress()) && client.port == receivedPacket.getPort()) {
                    newClient = false;
                    break;
                }
            }

            if(newClient){
                clients.add(new ClientInfo(receivedPacket.getAddress(), receivedPacket.getPort()));
            }

            Thread thread = new Thread(new ServerResponder(socket, receivedPacket, clients));
            thread.start();
        }
    }

    public static String getTimeStamp(){
        return "[" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + "]";
    }

}
