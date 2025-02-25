package main.java;

import java.io.IOException;
import java.util.HashMap;

/** Driver class for the Talk socket application. */
public class Talk {

  /**
   * The program behaves as a client with the specified client connection.
   *
   * @param client the client interface to use for socket communication
   * @return {@code false} if the host is not available
   * @throws never should return {@code false} rather than throwing an exception
   */
  protected boolean clientMode(BasicTalkInterface client) {
    // TODO: complete clientMode
    try {
      while (true)
        client.asyncIO();
    } catch (Exception e) {
      return false;
    }
    // return true;
  }

  /**
   * The program behaves as a server with the specified server.
   *
   * @param server the server interface to use for socket communication
   * @return {@code false} if the port is not available
   * @throws never should return {@code false} rather than throwing an exception
   */
  protected boolean serverMode(BasicTalkInterface server) {
    try {
      while (true)
        server.asyncIO();
    } catch (Exception e) {
      return false;
    }
    // return true;
  }

  /**
   * The program enters auto mode and behaves as a client attempting to connect to
   * the specified
   * host on the specified port. If the host is not available, the program should
   * behave as a
   * server listening for connections on the specified port.
   *
   * @param hostname   the host to connect to
   * @param portnumber the port to connect to or listen on
   * @return {@code false} if the host and port are both unavailable
   * @throws never should return {@code false} rather than throwing an exception
   */
  public boolean autoMode(String hostname, int portnumber) {
    try {
      if (!this.clientMode(hostname, portnumber)) {
        return this.serverMode(portnumber);
      }
      return true;
    } catch (Exception e) {
      return this.serverMode(portnumber);
    }
  }

  /**
   * The program behaves as a client connecting to the specified host on the
   * specified port.
   *
   * @param hostname   the host to connect to
   * @param portnumber the port to connect to
   * @return {@code false} if the host is not available
   * @throws never should return {@code false} rather than throwing an exception
   */
  public boolean clientMode(String hostname, int portnumber) {
    try {
      return this.clientMode(new TalkClient(hostname, portnumber));
    } catch (IOException e) {
      return false;
    }
  }

  public void helpMode() {
    System.out.println("Eliga Franks");
    System.out.println("Talk [-a | -h | -s] [hostname | IPaddress] [-p portnumber]\n");
  }

  /**
   * The program behaves as a server listening for connections on the specified
   * port.
   *
   * @param portnumber the port to listen for connections on
   * @return {@code false} if the port is unavailable
   * @throws never should return {@code false} rather than throwing an exception
   */
  public boolean serverMode(int portnumber) {
    try {
      return this.serverMode(new TalkServer(portnumber));
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Parses the specified args and executes the talk program in its intended mode.
   *
   * @param args the CLI args
   * @throws never should return {@code false} rather than throwing an exception
   */
  public boolean start(String[] args) {

    CLI cli = new CLI(args);

    // boolean h = this.clientMode(((cli.has("-h")) ? cli.get("-h") : "localhost"),
    // ((cli.has("-p"))
    // ? cli.getInt("-p") : 12987));
    //
    // boolean s = this.serverMode((cli.has("-p")) ? cli.getInt("-p") : 12987);

    try {
      if (cli.has("-help")) {
        this.helpMode();
        return true;
      }
      if (cli.has("-s")) {
        boolean s = this.serverMode((cli.has("-p")) ? cli.getInt("-p") : 12987);
        if (!s) {
          System.out.println("Server is unable to listen on specified port");
        }
        return s;
      }
      if (cli.has("-h")) {
        boolean h = this.clientMode(
            (cli.has("-h")) ? cli.get("-h") : "localhost",
            (cli.has("-p")) ? cli.getInt("-p") : 12987);
        if (!h) {
          System.out.println("Client unable to communicate with server");
        }
        return h;
      }
      if (cli.has("-a")) {
        return this.autoMode(
            ((cli.has("-a")) ? cli.get("-a") : "localhost"),
            ((cli.has("-p")) ? cli.getInt("-p") : 12987));
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public static void main(String[] args) {
    System.exit(new Talk().start(args) ? 0 : 1);
  }

  private static class CLI {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "12987";
    HashMap<String, String> opts;

    public CLI(String[] args) {
      this.opts = new HashMap<>();
      {
        for (int i = 0; i < args.length; i++) {
          if (args[i].equals("-h") || args[i].equals("-a")) {
            if (i + 1 < args.length && !args[i + 1].equals("-p")) {
              this.opts.put(args[i], args[i + 1]);
              i++;
            } else {
              this.opts.put(args[i], CLI.DEFAULT_HOST);
            }
            continue;
          }
          if (args[i].equals("-s") || (args[i].equals("-help"))) {
            this.opts.put(args[i], args[i]);
            continue;
          }
          if (args[i].equals("-p")) {
            if (i + 1 < args.length) {
              this.opts.put(args[i], args[i + 1]);
              i++;
            } else {
              this.opts.put(args[i], CLI.DEFAULT_PORT);
            }
            continue;
          }
          // everything else
        }
      }
    }

    public String get(String flag) {
      return this.opts.get(flag);
    }

    public int getInt(String flag) throws NumberFormatException {
      return Integer.parseInt(this.opts.get(flag));
    }

    public boolean has(String flag) {
      return this.opts.containsKey(flag);
    }
  }
}
