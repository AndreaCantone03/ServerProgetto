package org.example;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class ClientHandler extends Thread{
    private Socket clientSocket;
    BufferedReader in;
    PrintWriter out = null; // allocate to write answer to client.
    private Player thisPlayer = null;
    private Player otherPlayer = null;
    Match m;
    public ClientHandler(Socket clientSocket, PrintWriter o, double x, double y, double x2, double y2, Match m) {
        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("In error");
        }
        out = o;
        thisPlayer = new Player(3,(int) x, (int) y);
        otherPlayer = new Player(3, (int) x2, (int) y2);
        this.m = m;
        InetAddress inetAddress = this.clientSocket.getInetAddress();
        System.out.println("Connected from: " + inetAddress);
    }

    boolean manage(){
        Gson g = new Gson();
        out.println(g.toJson(thisPlayer));
        out.println(g.toJson(otherPlayer));

        while (true) {
            String s;
            try {
                if ((s = in.readLine()) != null) {
                    System.out.println(s);
                    setThisPlayer(g.fromJson(s, Player.class));
                    if (thisPlayer.isShot() == false) {
                        m.sharePlayersCoordinates(this);
                    } else {
                        Date d = new Date();
                        Bullet b = new Bullet(thisPlayer.getX(), thisPlayer.getY(), d.getTime());
                        m.shareBulletsCoordinates(this, b);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected");
                break;
            }
        }
        return true;
    }

    @Override
    public void run() {
        manage();
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public void setThisPlayer(Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public Player getOtherPlayer() {
        return otherPlayer;
    }

    public void setOtherPlayer(Player otherPlayer) {
        this.otherPlayer = otherPlayer;
    }

}