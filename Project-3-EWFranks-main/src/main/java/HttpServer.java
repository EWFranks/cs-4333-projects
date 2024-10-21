package main.java;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An implementation of an HTTP server. */
public class HttpServer implements Closeable {

  int port;
  String head;
  String domain;
  String contentType;
  Socket sock;
  ServerSocket serverSocket;
  String text = "this is filler";
  /**
   * Returns the content type associated with the given filename.
   * @param uri the path identifier for the requested resource
   * @return the content type associated with the given URI,
   *   or {@code null} if the file type is not supported.
   */
  public static String getContentType(String uri) {
    // TO-DO: implement getContentType(String)

    int index = uri.lastIndexOf('.');
    String identify;
    if (index > 0) {
      identify = uri.substring(index + 1);
      if (identify.equals("pdf")) {
        return "application/pdf";
      }
      if (identify.equals("gif")) {
        return "image/gif";
      }
      if (identify.equals("html")) {
        return "text/html";
      }
      if (identify.equals("htm")) {
        return "text/html";
      }
      if (identify.equals("jpg")) {
        return "image/jpeg";
      }
      if (identify.equals("jpeg")) {
        return "image/jpeg";
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Returns the HTTP server response entity headers.
   * @param serverName the name of the HTTP server
   * @param version the version of the HTTP server
   * @return the HTTP server response entity headers
   */
  public static String[] getEntityHeaders(String serverName, String version) {
    return HttpServer.getEntityHeaders(serverName, version, 0, null);
  }

  /**
   * Returns the HTTP server response entity headers.
   * @param serverName the name of the HTTP server
   * @param version the version of the HTTP server
   * @param length the content length of the requested resource,
   *   or {@code -1} if the response is not a 200 OK.
   * @param contentType the content type of the requested file,
   *   or {@code null} if the response is not a 200 OK.
   * @return the HTTP server response entity headers
   */
  public static String[] getEntityHeaders(
      String serverName, String version, int length, String contentType) {
    // TO-DO: implement getEntityHeaders(String, String, String)


    
    String[] Head = (length >= 0) ? new String[3] : new String[1];
    Head[0] = "Server: " + serverName + "/" + version;
    if (length >= 0) {
      Head[1] = "Content-Length: " + Long.toString(length);
      Head[2] = "Content-Type: " + contentType;
    }
    return Head;
  }

  /**
   * Returns the reason phrase associated with the input status code.
   * @param statusCode the status code of an HTTP request
   * @return the reason phrase associated with the input status code
   */
  public static String getReasonPhrase(int statusCode) {
    // TO-DO: implement getReasonPhrase(int)
    if (statusCode == 200) {
      return "OK";
    }
    if (statusCode == 404) {
      return "Not Found";
    }
    if (statusCode == 501) {
      return "Not Implemented";
    }
    return "Bad Request";
    // throw new UnsupportedOperationException("getReasonPhrase(int) not yet implemented");
  }

  /**
   * Returns the HTTP server response header.
   * @param serverName the name of the HTTP server
   * @param version the version of the HTTP server
   * @param statusCode the status code of the request
   * @return the HTTP server response header
   */
  public static String getResponseHeader(String serverName, String version, int statusCode) {
    return HttpServer.getResponseHeader(serverName, version, statusCode, null);
  }
  /**
   * Returns the HTTP server response header.
   * @param serverName the name of the HTTP server
   * @param version the version of the HTTP server
   * @param statusCode the status code of the request
   * @param uri the path identifier for the requested resource,
   *   or {@code null} if the URI was not provided
   * @return the HTTP server response header
   */
  public static String getResponseHeader(
      String serverName, String version, int statusCode, String uri) {
    // TO-DO: implement getResponseHeader(String, String, int, String)
    String Head = getStatusLine(statusCode) + getEntityHeaders(serverName, version)[0] + "\r\n";
    if (statusCode == 200) {
      File f = new File(new File("public_html"), uri);
      int length = (int) (f.length());
      String[] enthead = getEntityHeaders(serverName, version, length, getContentType(uri));
      Head += enthead[1] + "\r\n" + enthead[2] + "\r\n";
    }
    return Head + "\r\n";
  }

  /**
   * Returns the status code for the input HTTP request. The request must include a HOST field
   * in its header. Valid hosts are 'localhost', '127.0.0.1', and the machine's domain name,
   * and it must be appended with the HTTP server's designated port unless the port is 80.
   * @param domain the domain name of the HTTP server
   * @param port the port of the HTTP server
   * @param request the HTTP request
   * @return the status code for the input HTTP request
   */
  public static int getStatusCode(String domain, int port, String request) {

    Pattern is400 =
        Pattern.compile(
            "^[a-zA-Z]+ ((/[\\w|-]+)+(\\.[\\w]+))? HTTP/[\\d|.]+\r\n([\\w|-]+:.+\r\n)*\r\n");
    Matcher m = is400.matcher(request);
    Pattern hostPat =
        Pattern.compile(
            String.format(
                "Host:\\s*(%s|localhost|127.0.0.1)%s\\s*\r\n",
                domain, (port != 80) ? "(:" + port + ")" : "(:80)?"));

    Matcher startMat = hostPat.matcher(request);

    if (!m.find()) {
      return 400;
    }
    if (!startMat.find()) {
      return 400;
    }
    Pattern is501 =
        Pattern.compile("^(GET|HEAD) (/[\\w|-]+)+(.html|.htm|.gif|.jpg|.jpeg|.pdf) HTTP/1\\.1");
    m = is501.matcher(request);
    if (!m.find()) {
      return 501;
    }

    String fname = "public_html" + request.split(" ")[1].trim();

    if (new File(fname).exists()) {
      return 200;
    }
    return 404;

    // throw new UnsupportedOperationException("getStatusCode(String, int, String) not yet
    // implemented");
  }

  /**
   * Returns the HTTP server response status line.
   * @param statusCode the status code of the request
   * @return the HTTP server response status line
   */
  public static String getStatusLine(int statusCode) {
    // TO-DO: implement getStatusLine(int)

    return "HTTP/1.1 " + Integer.toString(statusCode) + " " + getReasonPhrase(statusCode) + "\r\n";

    // throw new UnsupportedOperationException("getStatusLine(int) not yet implemented");
  }
  /**
   * Constructs an HTTP server that listens for HTTP requests
   * on the input port.
   * @param port the port to listen for HTTP requests
   */
  public HttpServer(int port) throws IOException {
    // TODO: implement HttpServer(int)
    this.port = port;
    serverSocket = new ServerSocket(port);
    HttpHandler.spawn(this);
  }

  public void close() throws IOException {}

  /**
   * Returns the port acquired by this HTTP server.
   * @return the port acquired by this HTTP server
   */
  public int getPort() {
    // TODO: implement getPort()
    // throw new UnsupportedOperationException("getPort() not yet implemented");
    return this.port;
  }

  /**
   * Returns the HTTP response for the input HTTP request.
   * @param request the HTTP request being responded to
   * @return teh HTTP response for the input HTTP request
   * @throws IOException
   */
  public String getResponse(String request) throws IOException {
    // TODO: implement getResponse(String)
    // throw new UnsupportedOperationException("getResponse(String) not yet implemented");

    int statusCode = getStatusCode(this.domain, this.port, request);

    if (statusCode != 200) {
      return getResponseHeader(domain, text, statusCode);
    }

    String[] div = request.split(" ");

    if (div[0].equals("GET")) {
      return getResponseHeader(this.domain, text, statusCode, div[1])
          + Files.readString(Path.of("public_html", div[1]));
    } else if (div[0].equals("HEAD")) {
      return getResponseHeader(this.domain, text, statusCode, div[1]);
    } else {
      return getResponseHeader(this.domain, text, statusCode);
    }
  }

  public static void main(String[] args) {
    try {
      HttpServer server = new HttpServer(80);
      server.getResponse("GET / HTTP/1.1");
      server.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
