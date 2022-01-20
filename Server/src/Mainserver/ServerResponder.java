package Mainserver;
import com.example.myapplication.ClientInfo;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerResponder implements Runnable {

    DatagramSocket socket = null;
    DatagramPacket packet = null;
    byte[] sentData = new byte[1024];
    ArrayList<ClientInfo> clients = null;

    public ServerResponder(DatagramSocket socket, DatagramPacket packet, ArrayList<ClientInfo> clients) {
        this.socket = socket;
        this.packet = packet;
        this.clients = (ArrayList<ClientInfo>) clients.clone();
    }

    public void run() {
        DatagramPacket response = null;

        String message = new String(packet.getData(), 0 , packet.getLength());
        System.out.println(getTimeStamp() + " Server recieved the below message:\n" + message);
        ByteArrayOutputStream bos = null;
        ObjectOutput out = null;
        try {
            if(message.startsWith("GTUP2PCHATAPPLICATON"))
            {
                String capitalizedSentence = message.toUpperCase();
                sentData = capitalizedSentence.getBytes();

                response = new DatagramPacket(sentData, sentData.length, packet.getAddress(), packet.getPort());
                socket.send(response);
            }
            else
            {
                for (int i=0; i < clients.size(); i++) {
                    ClientInfo client = clients.get(i);
                    if(client.address.equals(packet.getAddress()) && client.port == packet.getPort())
                    {
                        clients.remove(i);
                        break;
                    }
                }

                bos = new ByteArrayOutputStream();
                out = new ObjectOutputStream(bos);
                out.writeObject(clients);

                sentData = bos.toByteArray();

                response = new DatagramPacket(sentData, sentData.length, packet.getAddress(), packet.getPort());
                socket.send(response);
                System.out.println(getTimeStamp() + " Server responds with the below string:\n" + response.getData().toString());

                out.close();
                bos.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static String getTimeStamp(){
        return "[" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + "]";
    }
}