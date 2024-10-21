package main.java;

import edu.utulsa.unet.RSendUDPI;
import edu.utulsa.unet.UDPSocket;
import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.net.SocketTimeoutException;

public class RSendUDP implements Closeable, RSendUDPI {

  private long timeout = 1000;
  private int mode = 0;
  private int port = 12987;
  private long modeParameter = 256;
  private String fname = "less_important.txt";
  private InetSocketAddress receiver = new InetSocketAddress("localhost", 12987);
  private UDPSocket socket;
  private int MTU;
  private TreeMap<Integer, DatagramPacket> frame;

  /**
   * Returns a byte-buffer representing the ith frame in a sequence of frames to
   * send the input
   * msg. Each frame is assumed to be of size framesize.
   * 
   * @param msg       the total message being sent
   * @param i         the sequence number of this frame
   * @param frameSize the size of eac h frame
   * @return a byte-buffer for the ith frame in the sequence
   */

  public static byte[] formatPacket(String msg, int i, int frameSize) {
    int HLEN = 5;
    int start = i * (frameSize - HLEN);
    int end = Math.min((i + 1) * (frameSize - HLEN), msg.length());
    String pack = msg.substring(start, end);
    ByteBuffer bb = ByteBuffer.allocate(frameSize);
    byte[] barray = pack.getBytes();
    bb.putInt(i);
    bb.put((byte) ((end == msg.length()) ? 0 : 1));
    bb.put(barray);
    return bb.array();
  
  }

 

  public void close() throws IOException {
  }

  /**
   * Returns the name of the file being sent.
   * 
   * @return the name of the file being sent
   */
  public String getFilename() {
    // TODO: implement getFilename()
    return fname;
    // throw new UnsupportedOperationException("getFilename() not yet implemented");
  }

  /**
   * Returns the port number of the receiver.
   * 
   * @return the port number of the receiver
   */
  public int getLocalPort() {
    // TODO: implement getLocalPort()
    return port;
    // throw new UnsupportedOperationException("getLocalPort() not yet
    // implemented");
  }

  /**
   * Returns the selected ARQ algorithm where {@code 0} is stop-and-wait and
   * {@code 1} is
   * sliding-window.
   * 
   * @return the selected ARQ algorithm
   */
  public int getMode() {
    // TODO: implement getMode()
    return mode;
    // throw new UnsupportedOperationException("getMode() not yet implemented");
  }

  /**
   * Returns the size of the window in bytes when using the sliding-window
   * algorithm.
   * 
   * @return the size of the window in bytes for the sliding-window algorithm
   */
  public long getModeParameter() {
    // TODO: implement getModeParameter()
    return modeParameter;
    // throw new UnsupportedOperationException("getModeParameter() not yet
    // implemented");
  }

  /**
   * Returns the address (hostname) of the receiver.
   * 
   * @return the address (hostname) of the receiver
   */
  public InetSocketAddress getReceiver() {
    // TODO: implement getReceiver()
    return receiver;
    // throw new UnsupportedOperationException("getReceiver() not yet implemented");
  }

  /**
   * Returns the ARQ timeout in milliseconds.
   * 
   * @return the ARQ timeout
   */
  public long getTimeout() {
    // TODO: implement getTimeout()
    return timeout;
    // throw new UnsupportedOperationException("getTimeout() not yet implemented");
  }

  /**
   * Sends the pre-selected file to the receiver.
   * 
   * @return {@code true} if the file is successfully sent
   */
  public boolean sendFile() {
    
    try{
      DatagramSocket socket = connect();
      //UDPSocket sendSock = new UDPSocket(localPort);
      socket.setSoTimeout((int)getTimeout());
      MTU = socket.getSendBufferSize();

      BufferedReader readB = new BufferedReader(new FileReader(new File(this.fname)));
      
      if (mode == 0){
        
        stopAndWait(Files.readString(Path.of(fname)), socket, MTU);
        }
      if (mode == 1){
        
        slidingWindow(Files.readString(Path.of(fname)), socket, MTU);
        
        return true;
        }
      
      socket.close();
    }
    catch(FileNotFoundException e){
      e.printStackTrace();
      return false;
    }
    catch(IOException e){
      e.printStackTrace();
      return false;
    }
    return true;

  }

  private void stopAndWait(String content, DatagramSocket socket, int size) throws IOException {
    
    int i = 0;
    socket.setSoTimeout(1000);
    while (i * (size - 5) <content.length()){
      byte[] pack = formatPacket(content, i, size);
      send(socket, pack);
      try{
        byte[] ackB = new byte[8];
        DatagramPacket ackN = new DatagramPacket(ackB, ackB.length);
        
        socket.receive(ackN);
        if(ackN != null && ByteBuffer.wrap(ackN.getData()).getInt(1) == i){
          i++;
        }
        
      } catch (SocketTimeoutException e) {
        //System.out.println("Timeout");
      }
    }
  }
  
  private void slidingWindow(String content, DatagramSocket socket, int size) throws IOException {
    
    int i = 0;
    socket.setSoTimeout(1000);
    while (i * (size - 5) <content.length()){
    
      byte[] pack = formatPacket(content, i, size);
      send(socket, pack);
      try {
        byte[] ackB = new byte[8];
        DatagramPacket ackN = new DatagramPacket(ackB, ackB.length);
          
        socket.receive(ackN);
        if(ackN != null && ByteBuffer.wrap(ackN.getData()).getInt(1) == i){
          i++;
        }
        
      } catch (SocketTimeoutException e) {}
    }
  }
  


  /**
   * Sets the name of the file being sent.
   * 
   * @param fname the name of the file being sent
   */
  public void setFilename(String fname) {
    // TODO: implement setFilename(String)
    this.fname = fname;
    // throw new UnsupportedOperationException("setFilename(String) not yet
    // implemented");
  }

  /**
   * Sets the port number of the receiver.
   * 
   * @param port the port number of the receiver
   * @return {@code true} if the intended port of the receiver is set to the input
   *         port
   */
  public boolean setLocalPort(int port) {
    // TODO: implement setLocalPort(int)
    if (port > 65535 || port < 0) {
      return false;
    }
    this.port = port;
    return true;
    // throw new UnsupportedOperationException("setLocalPort(int) not yet
    // implemented");
  }

  /**
   * Sets the selected ARQ algorithm where {@code 0} is stop-and-wait and
   * {@code 1} is
   * sliding-window.
   * 
   * @param mode the selected ARQ algorithm
   * @return {@code true} if the ARQ algorithm is set to the input mode
   */
  public boolean setMode(int mode) {
    // TODO: implement setMode(int)
    if (mode != 1 && mode != 0) {
      return false;
    }
    this.mode = mode;
    return true;
    // throw new UnsupportedOperationException("setMode(int) not yet implemented");
  }

  /**
   * Sets the size of the window in bytes when using the sliding-window algorithm.
   * 
   * @param n the size of the window in bytes for the sliding-window algorithm
   * @return {@code true} if the window size is set to the input n
   */
  public boolean setModeParameter(long n) {
    // TODO: implemenet setModeParameter(long)
    if (n <= 0) {
      return false;
    }
    // throw new UnsupportedOperationException("setModeParameter(long) not yet
    // implemented");
    modeParameter = n;
    return true;
  }

  /**
   * Sets the address (hostname) of the receiver.
   * 
   * @param receiver the address (hostname) of the receiver
   * @return {@code true} if the intended address of the receiver is set to the
   *         input receiver
   */
  public boolean setReceiver(InetSocketAddress receiver) {
    // TODO: implemenet setReceiver(InetSocketAddress)

    if (receiver == null) {
      return false;
    }
    this.receiver = receiver;
    return true;
    // throw new UnsupportedOperationException("setReceiver(InetSocketAddress) not
    // yet implemented");
  }

  /**
   * Sets the ARQ timeout in milliseconds.
   * 
   * @param timeout the ARQ timeout
   * @return {@code true} if the ARQ timeout is set to the input timeout
   */
  public boolean setTimeout(long timeout) {
    // TODO: implemenet setTimeout(long)
    if (timeout > 0) {
      this.timeout = timeout;
      return true;
    }
    return false;
    // throw new UnsupportedOperationException("setTimeout(long) not yet
    // implemented");
  }

  /**
   * Returns an established socket connection.
   * 
   * @return an established DatagramSocket connection
   */
  private DatagramSocket connect() throws IOException {
    return new UDPSocket(this.getLocalPort());
  }

  /**
   * Sends buffer over socket connection.
   * 
   * @param socket an established DatagramSocket connection
   * @param buffer the buffer to send over the socket
   * @return {@code true} if the buffer is successfully sent over the socket
   */
  private boolean send(DatagramSocket socket, byte[] buffer) {
    try {
      DatagramPacket packet = new DatagramPacket(
          buffer, buffer.length, this.getReceiver().getAddress(), this.getReceiver().getPort());
      socket.send(packet);
      Thread.sleep(250);
      return true;
    } catch (InterruptedException | IOException e) {
      System.out.println(e);
      return false;
    }
  }

  private DatagramPacket receive(DatagramSocket socket, byte[] buffer) {
    try {
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      socket.setSoTimeout((int) timeout);
      socket.receive(packet);
      return packet;
    } catch (IOException e) {
      // a TIMEOUT is an IOException
      System.out.println(e);
      return null;
    }
  }
}
