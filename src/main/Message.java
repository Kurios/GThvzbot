package main;

import java.util.Scanner;

public class Message {
	public enum Type {
		PING, JOIN, PRIVMSG, NOTICE, WHOIS_MAIN, WHOIS_END, WHOIS_CHANNELS, WHOIS_AFTERNET, WHOIS_QWEB, OTHER
		}

	public String user;
	public Type type;
	public String adress;
	public String channel;
	public String message;
	public String afternet;

	public static Message parseMessage(String message){
		Message ret = new Message();
		Scanner scan = new Scanner(message);
		String next = scan.next();
		if(next.matches(("NOTICE"))){
			ret.type = Type.NOTICE;
			ret.message = scan.nextLine();
		}else{
			String[] intro = next.split("[!:@]");
			ret.user = intro[1];
			if(ret.user.endsWith(".Org")){
				ret.adress = ret.user;
			}else{
				ret.adress = intro[3];
				if (ret.adress.endsWith(".Users.AfterNET.Org")){
					ret.afternet = ret.adress.split(".Users.AfterNET.Org")[0];
				}
			}
			next = scan.next();
			if(next.matches("JOIN")){
				ret.type = Type.JOIN;
			}else if(next.matches("PRIVMSG")){
				ret.type = Type.PRIVMSG;
			}else if(next.matches("311")){
				ret.type = Type.WHOIS_MAIN;
			}else if(next.matches("319")){
				ret.type = Type.WHOIS_CHANNELS;
			}else if(next.matches("330")){
				ret.type = Type.WHOIS_AFTERNET;
			}else if(next.matches("318")){
				ret.type = Type.WHOIS_END;
			}else{
				ret.type = Type.OTHER;
			}
			if(ret.type == Type.JOIN || ret.type == Type.PRIVMSG){
				ret.channel = scan.next().toLowerCase();
			}
			if(ret.type == Type.PRIVMSG){
				ret.message = scan.nextLine();
			}
			if(ret.type == Type.WHOIS_MAIN){
				scan.next();
				ret.user = scan.next();
				scan.next();
				ret.adress = scan.next();
			}
			if(ret.type == Type.WHOIS_CHANNELS){
				scan.next();
				ret.user = scan.next();
				ret.message = scan.nextLine();
			}
			if(ret.type == Type.WHOIS_AFTERNET){
				scan.next();
				ret.user = scan.next();
				ret.message = scan.next();
			}
			if(ret.type == Type.WHOIS_END){
				scan.next();
				ret.user = scan.next();
			}
		}
		return ret;
	}
}
