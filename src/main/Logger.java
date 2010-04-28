package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class Logger {
	
	static File log = new File("irc.log");
	static FileOutputStream stream;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss :");
	static File data = new File("irc.data");
	static FileOutputStream dstream;
	static FileInputStream istream;
	public static void startup(){
		try {
			log.createNewFile();
			log.setWritable(true);
			data.createNewFile();
			data.setReadable(true);
			data.setWritable(true);
			stream = new FileOutputStream(log);
			istream = new FileInputStream(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void print(String string) {
		string = dateFormat.format(new Date()) + string + "\n";
		System.out.print(string);
		try {
			stream.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void backup(TreeMap<String,User> users) {
		try {
			dstream = new FileOutputStream(data);
			for(Iterator<User> c = users.values().iterator();!c.hasNext();dstream.write(c.next().toString().getBytes()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void close(){
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
