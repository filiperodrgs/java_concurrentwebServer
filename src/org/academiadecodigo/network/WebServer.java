package org.academiadecodigo.network;

import org.omg.CORBA.TRANSACTION_MODE;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Single-threaded simple web server implementation
 */
public class WebServer {

    private static final Logger logger = Logger.getLogger(WebServer.class.getName());

    public static final String DOCUMENT_ROOT = "www/";
    public static final int DEFAULT_PORT = 8080;

    private ServerSocket bindSocket = null;

    public static void main(String[] args) {

        try {

            int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

            WebServer webServer = new WebServer();
            webServer.listen(port);

        } catch (NumberFormatException e) {

            System.err.println("Usage: WebServer [PORT]");
            System.exit(1);

        }
    }

    private void listen(int port) {

        try {

            bindSocket = new ServerSocket(port);
            logger.log(Level.INFO, "server bind to " + getAddress(bindSocket));

            serve(bindSocket);

        } catch (IOException e) {

            logger.log(Level.SEVERE, "could not bind to port " + port);
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);

        }
    }

    private void serve(ServerSocket bindSocket) {

        while (true) {

            try {


                Socket clientSocket = bindSocket.accept();
                ExecutorService cachedPool = Executors.newCachedThreadPool();
                //Thread thread = new Thread(new ServerThread(clientSocket));
                logger.log(Level.INFO, "new connection from " + getAddress(clientSocket));
                cachedPool.submit(new ServerThread(clientSocket));
                System.out.println(Thread.currentThread().getName());
                //thread.start();
                cachedPool.shutdown();



            } catch (IOException e) {

                logger.log(Level.SEVERE, e.getMessage());

            }

        }

    }

    private String getAddress(ServerSocket socket) {

        if (socket == null) {
            return null;
        }

        return socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort();
    }

    private String getAddress(Socket socket) {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort();
    }



}

