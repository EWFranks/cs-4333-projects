package main.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/** A thread-runnable http handler for http requests. */
public class HttpHandler implements Runnable {

  /** Spawns a new thread to run a handler. */
  public static void spawn(HttpServer server) {
    Thread thread = new Thread(new HttpHandler(server));
    thread.start();
  }

  private HttpServer server;

  /** Constructs a handler for http requests to the server. */
  public HttpHandler(HttpServer server) {
    this.server = server;
  }

  /** The work to run in a thread. */
  public void run() {
    // TODO: implement run()
    // throw new UnsupportedOperationException("run() not yet implemented");
    try {

      Socket handler = this.server.serverSocket.accept();
      HttpHandler.spawn(server);
      BufferedReader sockin = new BufferedReader(new InputStreamReader(handler.getInputStream()));
      PrintWriter sockout = new PrintWriter(handler.getOutputStream(), true);
      while (!sockin.ready()) {}
      StringBuilder PosBuild = new StringBuilder();
      while (sockin.ready()) {
        PosBuild.append((char) sockin.read());
      }
      sockout.println(server.getResponse(PosBuild.toString())); // sockin.readLine()
      sockout.close();
      sockin.close();
      handler.close();

    } catch (Exception e) {
    }
  }
}
