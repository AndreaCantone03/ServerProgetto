package org.example;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer
{
    static int portNumber = 1234;
    static ServerSocket serverSocket;

    static boolean startServer() {
        try{
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server started");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void main( String[] args )
    {

        if(!startServer()){
            return;
        }

        while (true){

            Socket clientHandler;
            Socket clientHandler2;

            try{
                clientHandler = serverSocket.accept();
            } catch(IOException e){
                throw new RuntimeException(e);
            }

            try{
                clientHandler2 = serverSocket.accept();
            } catch(IOException e){
                throw new RuntimeException(e);
            }

            Match match = new Match(clientHandler,clientHandler2);
        }
    }

}