[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=8625627&assignment_repo_type=AssignmentRepo)
# CS-4333/6333-Project-1

## Overview
For this project you will have to implement a bidirectional Talk program, whose entry point is the Java class **Talk.java**. The program is intended to support two-party communication.

Your program should accept command-line options and run in any of the following modes (as well as report errors for invalid invocations).

```Talk –h [hostname | IPaddress] [–p portnumber]```

The program behaves as a client connecting to ```[hostname | IPaddress]``` on port ```portnumber```. If a server is not available your program should exit with the message **“Client unable to communicate with server”**. Note: portnumber in this case refers to the server and not to the client.

```Talk –s [–p portnumber]```

The program behaves as a server listening for connections on port ```portnumber```. If the port is not available for use, your program should exit with the message **“Server unable to listen on specified port”**.

```Talk –a [hostname | IPaddress] [–p portnumber]```

The program enters *auto* mode. When in auto mode, your program should start as a client attempting to communicate with ```[hostname | IPaddress]``` on port portnumber. If a server is not found, your program should detect this condition and start behaving as a server listening for connections on port ```portnumber```.

```Talk –help```

The program prints your name and instructions on how to use your program.

Once a two-party connection has been established, messages received through the socket should be prepended with **[remote]** when displaying the message on the screen to differentiate it from messages that are typed locally.

In addition, the string ```STATUS``` is considered to be a keyword and will not be transmitted. If a user types STATUS your program should print information about the state of the connection (IP numbers, and remote and local ports).

e.g., ```[STATUS] Client: 127.0.0.1:8080; Server: 127.0.0.1:12987```

Arguments in brackets are optional. In case a ```hostname``` or ```IPaddress``` is not provided their default value should be **localhost**. If a ```portnumber``` is not provided its default value should be **12987**. Once a connection is established you will have to obtain suitable stream objects such as a ```BufferedReader``` and a ```PrintWriter``` (from the socket) as your input and output streams respectively. The unit of data sent/received must be a line of text as returned by the ```readLine()``` method. Users of your program will be using the keyboard to type their messages and their screens to display incoming and outgoing messages.

**Note:** the ```readLine()``` method is a blocking operation and the call does not return until something is read from the input stream. This may result in undesired effects on your program such as not being able to receive and display remote messages on your screen while you are typing. Your program must not suffer from this problem and your solution should be described in your report.

## Assignment
Complete the implementation of **Talk.java**, **TalkClient.java**, and **TalkServer.java** according to the specification above.

[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=9566144&assignment_repo_type=AssignmentRepo)
# CS-43333/6333 Project 2

## Overview
For this project we will explore the challenges associated with providing reliable delivery services over an unreliable network. Three different ARQ approaches have been described in class: stop-and-wait, concurrent logical channels, and sliding window. Your task during this project is to develop a set of Java classes capable of reliably transferring a file between two hosts over UDP (TCP already offers reliable delivery services).

Students will be provided with a *UDPSocket* class that extends the *DatagramSocket* class provided by Java to send a *DatagramPacket* over UDP. Take time to familiarize yourself with the main two classes provided by Java for UDP support and their functionality. The UDPSocket class has the additional capability of allowing programmers to modify the MTU (to model fragmentation), the packet drop rate (to model unreliable networks) and add random time delays to packets (to model out-of-order reception).

### Support material
Parameters regulating message transfers using the UDPSocket class are located in a file named **unet.properties**. This file may be modified to test different scenarios (the file includes comments describing each parameter). Note that you can also use this file to enable and configure logging options (to file or standard output stream).

### Unet.properties
```
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# unet properties file
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#== log file name ================================
#
# log.filename = system (standard output stream)
log.filename = system
log.enable = true
#== packet drop rate =============================
#
# n<0 --- Drop random packets (n*-100) probability
# n=0 --- Do not drop any packets
# n=-1 --- Drop all packets
# n>0 --- Drop every nth packet (assuming no delay)
# n=[d,]*d --- Drop select packets e.g. packet.droprate = 4,6,7
packet.droprate = 0
#== packet delay rate ============================
#
# packet delay in milliseconds
# min==max --- in order delivery
packet.delay.minimum = 10
packet.delay.maximum = 100
#== packet maximum transmission size =============
#
# -1 unlimited (Integer.MAX_VALUE)
packet.mtu = 1500
```

The *setMode* method specifies the algorithm used for reliable delivery where mode is 0 or 1 (to specify stop-and-wait and sliding window respectively). If the method is not called, the mode should default to stop-and-wait. Its companion, *getMode* simply returns an int indicating the mode of operation.

The *setModeParameter* method is used to indicate the size of the window in bytes for the sliding window mode. A call to this method when using stop-and-wait should have no effect. The default value should be 256 for the sliding window algorithm. Hint: your program will have to use this value and the MTU (max payload size) value to calculate the maximum number of outstanding frames you can send if using the Sliding Window algorithm. For instance, if the window size is 2400 and the MTU is 20 you could have up to 120 outstanding frames on the network.

The *setFilename* method is used to indicate the name of the file that should be sent (when used by the sender) or the name to give to a received file (when used by the receiver).

The *setTimeout* method specifies the timeout value in milliseconds. Its default value should be one second.

A sender uses *setReceiver* to specify IP address (or fully qualified name) of the receiver and the remote port number. Similarly, *setLocalPort* is used to indicate the local port number used by the host. The sender will send data to the specified IP and port number. The default local port number is 12987 and if an IP address is not specified then the localhost is used.

The methods *sendFile* and *receiveFile* initiate file transmission and reception respectively. Methods returning a boolean should return true if the operation succeeded and false otherwise.

### Operation Requirements
RReceiveUDP
- Should print an initial message indicating the local IP, the ARQ algorithm (indicating the value of n if appropriate) in use and the UDP port where the connection is expected.
- Upon successful initial connection from a sender, a line should be printed indicating IP address and port used by the sender.
- For each received message print its sequence number and number of data bytes
- For each ACK sent to the sender, print a message indicating which sequence number is being acknowledged
- Upon successful file reception, print a line indicating how many messages/bytes were received and how long it took.

RSendUDP
- Should print an initial message indicating the local IP, the ARQ algorithm (indicating the value of n if appropriate) in use and the local source UDP port used by the sender.
- Upon successful initial connection, a line should be printed indicating address and port used by the receiver.
- For each sent message print the message sequence number and number of data bytes in the message
- For each received ACK print a message indicating which sequence number is being acknowledged
- If a timeout occurs, i.e. an ACK has been delayed or lost, and a message needs to be resent, print a message indicating this condition
- Upon successful file transmission, print a line indicating how many bytes were sent and how long it took.
  
### Usage
Program usage will be illustrated by the following example. Assume Host A has IP address 192.168.1.23 and Host B has IP address 172.17.34.56 (note that none of these IP addresses are routable over the Internet). Host A wants to send a file named important.txt to Host B. Host A wants to use local port 23456 and Host B wants to use local port 32456 during the file transfer. Host B does not consider the file so important and it wants to save the received file as less_important.txt. Both Host A and B have agreed to use the sliding-window protocol with a window size of 512 bytes and Host A will use a timeout value of 10 seconds (the path between A and B has a rather large delay).

### Sample Code
The following code running on Host A should accomplish the task of reliably sending the file:
```Java
RSendUDP sender = new RSendUDP();
sender.setMode(1);
sender.setModeParameter(512);
sender.setTimeout(10000);
sender.setFilename("important.txt");
sender.setLocalPort(23456);
sender.setReceiver(new InetSocketAddress("172.17.34.56", 32456));
sender.sendFile();
```

The following code running on Host B should accomplish the task of receiving the file:
```Java
RReceiveUDP receiver = new RReceiveUDP();
receiver.setMode(1);
receiver.setModeParameter(512);
receiver.setFilename("less_important.txt");
receiver.setLocalPort(32456);
receiver.receiveFile();
```

### Sample Output
Assume our solution uses 10 bytes of header for every message, the network cannot deliver messages larger than 200 bytes and the file we are transferring is 800 bytes long. The output of your program (on the sender side) when running the stop-and-wait algorithm could look like this:
```
Sending important.txt from 192.168.1.23:23456 to 172.17.34.56:32456 with 800 bytes
Using stop-and-wait
Message 1 sent with 190 bytes of actual data
Message 1 acknowledged
Message 2 sent with 190 bytes of actual data
Message 2 acknowledged
Message 3 sent with 190 bytes of actual data
Message 3 timed-out
Message 3 sent with 190 bytes of actual data
Message 3 acknowledged
Message 4 sent with 190 bytes of actual data
Message 4 acknowledged
Message 5 sent with 40 bytes of actual data
Message 5 acknowledged
Successfully transferred important.txt (800 bytes) in 1.2 seconds
```

A similar output should be seen on the receiver side.

## Compiling and Testing Code
Your IDE should provide tools to compile your code. If you're unfamiliar with that process, you can research it online or ask. Most developers compile their code from command line using a shell script, such as a **Makefile** or build script (**build.sh**). I've provided build scripts for you in both *Powershell* and *Bash*. Refer to the following directions on how to use these scripts based on the terminal that you're using. If you're on Windows, please use Windows Subsystem for Linux (WSL), Git Bash, or Powershell, not Command Prompt.

- [![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=9566149&assignment_repo_type=AssignmentRepo)

# CS-4333/6333-Project-3

## Overview
HTTP 1.1 [RFC 2616] defines the following methods: OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE and CONNECT. The goal of this project is to implement a minimal HTTP 1.1 server supporting and implementing only the GET and HEAD methods. This protocol typically runs on top of TCP on port 80.

The HTTP protocol is a request/response protocol:
1. A client sends a request to the server in the form of a request method, URI, and protocol version, followed by a MIME-like message containing request modifiers, client information, and possibly body content over a connection with a server.
2. The server responds with a status line, including the message's protocol version and a success or error code, followed by a MIME-like message containing server information, entity  metainformation, and possibly entity-body content.

Where a URI (Uniform Resource Identifier) is either a URL (Uniform Resource Location) or a URN (Uniform Resource Name). Throughout this document the following notation is used: SP is a blank space and CRLF is a carriage return followed by a line feed character. URIs have been known by many names: WWW addresses, Universal Document Identifiers, Universal Resource Identifiers , and finally the combination of Uniform Resource Locators (URL) and Names (URN). As far as HTTP is concerned, Uniform Resource Identifiers are simply formatted strings which identify a resource via a name, location, or any other characteristic.

### Client Request
The general form for an HTTP/1.1 request is:

```
Method SP Request-URI SP HTTP/1.1 CRLF
([general-header line | request-header line | entity-header line] CRLF)*
CRLF
[message body]
```

Any HTTP/1.1 compliant client must include a Host request-header field identifying the internet host (the server) they are connecting to and the port number:

```
Host: hostname[:port]
```

The port number must be included unless the server is using the default port 80. For the header lines, the notation `(…)*` means zero or more of the strings enclosed in parenthesis. If the machine where the server is running does not have a DNS name, you may use the IP address for hostname.

Your server must check for the Host request header field before serving the request. If there is no Host request-header, the server must return an error code as specified in the following section.

In summary, a minimal and valid GET request on a server named neo.mcs.utulsa.edu running on port 16405 for a file or resource named index.html will look like:

```
GET /index.html HTTP/1.1 CRLF
Host: neo.mcs.utulsa.edu:16405 CRLF
CRLF
```

The same request as sent by a Chrome browser using Mac OS X may look like:

```
GET /index.html HTTP/1.1 CRLF
Host: neo.mcs.utulsa.edu:16405 CRLF
Connection: keep-alive CRLF
Cache-Control: max-age=0 CRLF
Upgrade-Insecure-Requests: 1 CRLF
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like
Gecko) Chrome/86.0.4240.80 Safari/537.36 CRLF
Accept:
text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,a
pplication/signed-exchange;v=b3;q=0.9 CRLF
Accept-Encoding: gzip, deflate CRLF
Accept-Language: en-US,en;q=0.9 CRLF
CRLF
```

> Note in this case that the client is sending lots of information to the server. It basically notifies the server what kinds of files the browser accepts (Accept lines) and a User-Agent line that notifies the server that the client Chrome.

Remember that there could be more lines following the GET method request. Your server must look among those lines for a Host request-header field before it is served (and return an error code if the line is missing). Your server can safely ignore the lines that are not strictly required by the server in order to process a client’s request.

### Server Response
After receiving and interpreting a request message, a server responds with an HTTP response message. The general form of a response message consists of several lines: (i) a status line followed by a CRLF, (ii) zero or more header lines followed by a CRLF (also called entity headers), (iii) a CRLF indicating the end of the header lines and (iv) a message body if necessary containing the requested data. The general form of the response message then is:

```
HTTP/1.1 SP StatusCode SP ReasonPhrase CRLF
([( general-header line | response-header line | entity-header line)] CRLF) *
CRLF
[ message-body ]
```

> Note: the only difference between a server response to a GET and a server response to a HEAD is that when the server responds to a HEAD request, the message body is empty (only the header lines associated with the request are sent back to the client).

### Status Line
```
HTTP/1.1 SP StatusCode SP ReasonPhrase CRLF
```

The StatusCode element is a 3-digit integer result code of the attempt to understand and satisfy the request. These codes are fully defined in section 10 of RFC 2616. The ReasonPhrase is intended to give a short textual description of the StatusCode. The StatusCode is intended for use by automata and the ReasonPhrase is intended for the human user. The client is not required to examine or display the ReasonPhrase.

The first digit of the StatusCode defines the class of response. The last two digits do not have any categorization role. There are 5 values for the first digit:
- 1xx: Informational - Request received, continuing process.
- 2xx: Success - The action was successfully received, understood, and accepted.
- 3xx: Redirection - Further action must be taken in order to complete the request.
- 4xx: Client Error - The request contains bad syntax or cannot be fulfilled.
- 5xx: Server Error - The server failed to fulfill an apparently valid request.

The following table summarizes the status codes and reason phrases that your server MUST implement:

|  StatusCode  |  ReasonPhrase   |
| ------------ | --------------- |
|     200      |        OK       |
|     400      |    Bad Request  |
|     404      |     Not Found   |
|     501      | Not Implemented |

A 200 OK response indicates the request has succeeded and the information returned by the server with the response is dependent on the method used in the request. If the message is in response to a GET method, then the message body contains the data associated with the request resource. If the message is in response to a HEAD method, then only entity header lines are sent without any message body.

A 400 Bad Request message indicates that a request could not be understood by the server due to malformed syntax (the client should not repeat the request without modification).

A 404 Not Found message indicates the server has not found anything matching the requested resource (this is one of the most common responses).

A 501 Not Implemented message indicates that the server does not support the functionality required to fulfill the request. Your server should respond with this message when the request method corresponds to one of the following: OPTIONS, POST, PUT, DELETE, TRACE and CONNECT (the methods you are NOT implementing in this project).

### Entity Headers
There are several types of header lines. The intention of these lines is to provide information to the Client when responding to a request. Your server MUST implement (and send back to the client) at least the following three header lines when the request is valid. Otherwise, just the server entity header is fine.

```
Server: ServerName/ServerVersion
Content-Length: lengthOfResource
Content-Type: typeOfResource
```

The Server response-header field contains information about the software used by the origin server (the server you are implementing for this project) to handle the request. An example is:

```
Server: cs4333httpserver/1.0.2
```

The Content-Length entity-header field indicates the size of the entity-body, in decimal number of OCTETs, sent to the recipient or, in the case of the HEAD method, the size of the entity-body that would have been sent had the request been a GET. An example for `lengthOfResource=3495` is:

```
Content-Length: 3495
```

The Content-Type entity-header field indicates the media type of the message-body sent to the recipient or, in the case of the HEAD method, the media type that would have been sent had the request been a GET. Your server must be able to report to the client the following media types:

|   File Name   | File Type |  Content Type   |
| ------------- | --------- | --------------- |
| *.html, *.htm | html      | text/html       |
| *.gif         | gif       | image/gif       |
| *.jpg, *.jpeg | jpeg      | image/jpeg      |
| *.pdf         | pdf       | application/pdf |

An example for typeOfResource when responding to a GET request method for a file named index.html is:

```
Content-Type: text/html
```

As illustrated above, the end of the entity header lines is indicated by sending an additional CRLF.

### Message Body
The message-body (if any) of an HTTP message is used to carry the entity-body associated with the request or response (typically the file being requested). I suggest you use the FileInputStream Java class to read the contents of a requested file.



### Windows Users (Powershell)
- To compile your code: `./build.ps1`
- To compile and run your code: `./build.ps1 run` (forwards clargs to program)
- To compile and test your code: `./build.ps1 test` (forwards clargs to TUGrader)
- To format your code: `./build.ps1 fmt`
- To sync your code: `./build.ps1 sync`
- To submit your code: `./build.ps1 submit`
- To remove class files: `./build.ps1 clean`

### Windows Users (WSL, Git Bash), Mac and Linux Users
- To compile your code: `./build.sh`
- To compile and run your code: `./build.sh run` (forwards clargs to program)
- To compile and test your code: `./build.sh test` (forwards clargs to TUGrader)
- To format your code: `./build.sh fmt`
- To sync your code: `./build.sh sync`
- To submit your code: `./build.sh submit`
- To remove class files: `./build.sh clean`

These scripts use the following commands. Note that Windows users need to replace the colon with a semicolon in the Java classpath.
- To compile a Java file: `javac -d target -cp lib/*:src <filepath>.java`
- To execute a Java file: `java -cp lib/*:target <package-path>.<filename>`
- To format a Java file: `java -jar lib/google-java-format.jar --replace --skip-javadoc-formatting <filepath>.java`
- To remove class files: `rm -r target/*`

If you're working from command-line, [google-java-format](https://github.com/google/google-java-format) is an open-source formatting tool that you can use to format your files. You can use the following commands to format your code depending on your terminal.
- `./build.ps1 fmt`
- `./build.sh fmt`

To sync changes made from another device, use the following command.
- `git fetch origin main`
- `git pull origin main`

To push commits from command line, use the following commands.
- `git add -A`
- `git commit -m "<your message goes here>"`
- `git push origin main`

You can also sync all changes and submit with the following commands depending on your terminal.
- `./build.ps1 submit`
- `./build.sh submit`
