package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Match extends Thread {
    //Il server distribuirà i giocatori sempre nello stesso modo in base all'ordine d'ingresso.
    //Il primo sarà sempre quello di sinistra mentre l'altro quello di destra.
    private ClientHandler leftPlayer; //left player
    private ClientHandler rightPlayer; //right player
    private static int width = 1220;
    private static int height = 720;
    private PrintWriter outLeft = null;
    private PrintWriter outRight = null;
    List<Bullet> bullets = new ArrayList<Bullet>();
    Gson g = new Gson();
    public Match(Socket clientHandler1, Socket clientHandler2) {
        try {
            outLeft = new PrintWriter(clientHandler1.getOutputStream(), true);
            outRight = new PrintWriter(clientHandler2.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error Match");
        }
        this.leftPlayer = new ClientHandler(clientHandler1, outLeft,0, height/2, width - 100, height/2, this);
        this.rightPlayer = new ClientHandler(clientHandler2, outRight, width - 100, height/2, 0, height/2, this);
        leftPlayer.start();
        rightPlayer.start();
        this.start();
    }
    public void sharePlayersCoordinates(ClientHandler p) {
        Command cmd;
        if (p == leftPlayer) {
            cmd = new Command("otherPlayer", g.toJson(leftPlayer.getThisPlayer()));
            outRight.println(g.toJson(cmd));
        } else {
            cmd = new Command("otherPlayer", g.toJson(rightPlayer.getThisPlayer()));
            outLeft.println(g.toJson(cmd));
        }
    }

    public void shareBulletsCoordinates(ClientHandler p, Bullet b) {
        Command cmd;
        if (p == leftPlayer) {
            b.setOwner("leftPlayer");
            b.setX(leftPlayer.getThisPlayer().getX() + 130);
        } else {
            b.setOwner("rightPlayer");
            b.setX(rightPlayer.getThisPlayer().getX() - 130);
        }
        cmd = new Command("bullet", g.toJson(b));
        outRight.println(g.toJson(cmd));
        outLeft.println(g.toJson(cmd));
        bullets.add(b);
        System.out.println(b);
    }

    @Override
    public void run() {
        Command cmd;
        while(true) {
            if (bullets.size() > 0) {
                for (Bullet bullet: bullets) {
                    if (bullet.getX() >= 0 && bullet.getX() <= width) {
                        if (bullet.getOwner().equals("leftPlayer")) {
                            bullet.setX(bullet.getX() + 80);
                        } else {
                            bullet.setX(bullet.getX() - 80);
                        }
                    }
                    System.out.println(g.toJson(bullet));
                    cmd = new Command("bullet", g.toJson(bullet));
                    outLeft.println(g.toJson(cmd));
                    outRight.println(g.toJson(cmd));
                    if (bullet.getX() < -10 || bullet.getX() > width + 10) {
                        bullets.remove(bullet);
                        break;
                    }
                    try{
                        if( bullet.getX() >= rightPlayer.getThisPlayer().getX() && ((bullet.getY() >= rightPlayer.getThisPlayer().getY()) && (bullet.getY() <= (rightPlayer.getThisPlayer().getY()+100)) )){
                            int decHeart = rightPlayer.getThisPlayer().getnHeart() - 1;
                            rightPlayer.getThisPlayer().setnHeart( decHeart );
                            cmd = new Command("thisPlayer", g.toJson(rightPlayer.getThisPlayer()));
                            outRight.println(g.toJson(cmd));
                            cmd.command = "otherPlayer";
                            outLeft.println(g.toJson(cmd));
                            System.out.println("Right player shotted");
                            if(rightPlayer.getThisPlayer().getnHeart() == 0){
                                System.out.println("Game finished");
                            }
                        } else if( bullet.getX() <= leftPlayer.getThisPlayer().getX() && ((bullet.getY() >= leftPlayer.getThisPlayer().getY()) && (bullet.getY() <= (leftPlayer.getThisPlayer().getY()+100)) )){
                            int decHeart = leftPlayer.getThisPlayer().getnHeart() - 1;
                            leftPlayer.getThisPlayer().setnHeart( decHeart );
                            cmd = new Command("otherPlayer", g.toJson(leftPlayer.getThisPlayer()));
                            outRight.println(g.toJson(cmd));
                            cmd.command = "thisPlayer";
                            outLeft.println(g.toJson(cmd));
                            System.out.println("Left player shotted");
                            if(leftPlayer.getThisPlayer().getnHeart() == 0){
                                System.out.println("Game finished");
                            }
                        }
                    }catch (Exception e){
                        System.out.println("No right player at the moment...");
                    }
                }
            }
            try{
                Thread.sleep(750);
            }catch (Exception e){}
        }
    }

}