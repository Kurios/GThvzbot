package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class IRC {
	
	Socket sock;
	PrintWriter out;
	BufferedReader in;
	LinkedList<String> filteredIn = new LinkedList<String>();
	PingListener listener;
	
	public IRC(String name) throws UnknownHostException, IOException{
		sock = new Socket("irc.afternet.org",6667);
	     out = new PrintWriter(sock.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
         
         out.print("PASS \r\n");
         out.print("NICK "+name+"\r\n");
         out.print("USER hvzbot hvzbot bla :HVZ Auth Bot\r\n");
         out.flush();
         listener = new PingListener();
         listener.start();
         
	}
	public void send(String s){
		out.print(s+"\r\n");
		Logger.print("-> "+s);
		out.flush();
	}
	public boolean hasNext(){
		return !filteredIn.isEmpty();
	}
	public String getNext(){
		return filteredIn.poll();
	}
	public void close(){
		try {
		sock.close();
		in.close();
		out.close();
		listener.close();
		} catch(IOException e) {
		}
	}
	
	private class PingListener extends Thread {
		
		boolean alive = true;
		
		public void close(){
			alive = false;
		}
		public void run(){
			String parse;
			while(alive){
				try {
					while(in.ready()){
						
						parse = in.readLine();
						Logger.print(parse);
						if(parse.startsWith("PING")){
							send("PONG "+parse.substring(5));
						}
						else{
							
							if(parse.endsWith("HVZbot :Welcome to the AfterNET IRC Network, HVZbot")){
								send("JOIN #hvz,#hvz-horde,#hvz-resistance");
							}else filteredIn.add(parse);
						}
					}
					sleep(400);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
