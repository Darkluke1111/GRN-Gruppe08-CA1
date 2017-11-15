package de.uulm.in.vs.grn.a2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NumberGuessingGameThreadedServer implements Runnable{
  private final ServerSocket serverSocket;
  private final ExecutorService service;

  /*
  Constructor sets up the Server Socket at the specified port and creates a Pool with the specified size.
   */
  public NumberGuessingGameThreadedServer(int port, int poolSize) throws IOException {
      serverSocket = new ServerSocket(port);
      service = Executors.newFixedThreadPool(poolSize);
  }

  @Override
  public void run() {
    while(true) {
      try {
        Socket socket = serverSocket.accept();
        service.execute(new NumberGuessingGameRequestHandler(socket));
      } catch (IOException e1) {
        service.shutdown();
        e1.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    try {
      //Create a Server and run it in the current thread
      new NumberGuessingGameThreadedServer(5555,4).run();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
