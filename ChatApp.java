package cs402_assignment4;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatApp {
	public static void main(String[] args) {
	    receiver.start();
	    sender.start();
	}
		  
		  static Thread receiver = new Thread() {
			  public void run() {
				  DatagramPacket inPacket = null;
				    byte[] inBuf = new byte[256];
				    try {
						// A multicast group is specified by a class D IP address and by a standard UDP port number. 
						// Class D IP addresses are in the range 224.0.0.0 to 239.255.255.255, inclusive.
						InetAddress mcastaddr = InetAddress.getByName("224.2.2.3");
						int multicastport = 8888;
						NetworkInterface netif = NetworkInterface.getByInetAddress(mcastaddr);
						//Prepare to join multicast group
						InetSocketAddress groupaddr = new InetSocketAddress(mcastaddr, multicastport);
				        MulticastSocket socket = new MulticastSocket(multicastport); // create a multicast socket
						socket.joinGroup(groupaddr, netif); // Tell the router to associate the multicast socket 
															// with the chosen interface and multicast address
				      while (true) {
				        inPacket = new DatagramPacket(inBuf, inBuf.length);
				        socket.receive(inPacket);  // receive a multicast
				        String msg = new String(inBuf, 0, inPacket.getLength());
				        System.out.println(msg);
				      }
				    } catch (IOException ioe) {
				      System.out.println(ioe);
				    }
			  }
		  };
		  
		  static Thread sender = new Thread() {
			  public void run() {
				  Scanner sc = new Scanner(System.in);
				  System.out.println("Enter your username:");
				  String user = sc.nextLine();
				  System.out.println("Enter Your Message");
				  	DatagramSocket socket = null;
				    DatagramPacket outPacket = null;
				    byte[] outBuf;
				    final int PORT = 8888;
				 
				    try {
				      socket = new DatagramSocket();
				      long counter = 0;
				      
				      while (true) {
				        String usermsg = sc.nextLine();
				        counter++;
				        String msg;
				        msg = user + ": "+usermsg;
				        outBuf = msg.getBytes();
				        //Send to multicast IP address and port
				        InetAddress address = InetAddress.getByName("224.2.2.3");
				        outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
				        socket.send(outPacket);
				      }
				    } catch (IOException ioe) {
				      System.out.println(ioe);
				    }
			  }
		  };
}

 