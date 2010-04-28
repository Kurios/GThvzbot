package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.TreeMap;

import mian.Message.Type;

public class IRC_AI extends Thread{

	IRC connection;
	boolean running = true;
	TreeMap<String, User> users = new TreeMap<String, User>();
	TreeMap<String, WUser> whois = new TreeMap<String, WUser>();
	TreeMap<String, User> afternet = new TreeMap<String, User>();
	public IRC_AI(IRC connection){
		super();
		this.connection = connection;
	}
	public void run() {
		int verify = 0;
		Message mes;
		User user;
		user = new User();
		user.admin = true;
		user.adress = "hvzmaster.Users.AfterNET.Org";
		user.afternetName = "hvzmaster";
		user.name = "HVZ master Bot Codename \" Alpha Wrench \" ";
		afternet.put(user.afternetName,user);
		while(running){
			try {
				sleep(400);
				while(connection.hasNext()){
					user = null;
					mes = Message.parseMessage(connection.getNext());
					if(mes.type == Type.JOIN){
						user = users.get(mes.adress);
						if(!(user != null) && !mes.afternet.isEmpty()){
							Logger.print("Afternet Login is: "+mes.afternet);
							user = afternet.get(mes.afternet);
						}
						if(user != null){
							Logger.print("User Found, using "+user.name);
							if(mes.channel.matches("#hvz-horde") && !(user.horde || user.admin)){
								connection.send("PRIVMSG "+mes.channel+" :.k "+mes.user+" no spying!");
							}else if(mes.channel.matches("#hvz-resistance") && !(user.resistance || user.admin)){
								connection.send("PRIVMSG "+mes.channel+" :.k "+mes.user+" no spying!");
							}else if(mes.channel.matches("#hvz")){
								connection.send("WHOIS "+mes.user);
							}
						}else{
							Logger.print("User Not Found "+ mes.adress);
							if(mes.channel.matches("#hvz")){
								connection.send("WHOIS "+mes.user);
								connection.send("PRIVMSG "+mes.user+" :please log in. the format is /msg HVZbot auth <username> <password> (anything in <> is a parameter)");
							}else if(mes.channel.matches("#hvz-horde") || mes.channel.matches("#hvz-resistance")){
								connection.send("PRIVMSG "+mes.channel+" :.k "+mes.user+" no spying!");
							}
						}
					}else if(mes.type == Type.PRIVMSG){
						Logger.print(mes.channel);
						if(mes.channel.matches("hvzbot")){
							Logger.print("message to me: " + mes.message);
							if(mes.message.startsWith(" :auth")){
								String auth[] = mes.message.split(" "); 
								if(auth.length < 3 || !newUser(mes.adress,auth)){
									connection.send("PRIVMSG "+mes.user+" :auth failed, please retry");
								}else{
									connection.send("PRIVMSG "+mes.user+" :welcome "+mes.user);
								}
							}
						}
						if(mes.message.startsWith(" :?verify")){
							String auth[] = mes.message.split(" ");
							if(auth.length < 2){
								connection.send("PRIVMSG "+mes.channel+" :this is not enough arguments for ?verify.");
							}else{
								verify = 1;
								connection.send("WHOIS "+auth[2]);
								
							}
						}
					}else if(mes.type == Type.WHOIS_MAIN){
						WUser wser = new WUser();
						wser.name = mes.user;
						wser.adress = mes.adress;
						whois.put(mes.user, wser);
					}else if(mes.type == Type.WHOIS_CHANNELS){
						WUser wser = whois.get(mes.user);
						wser.channels = mes.message.toLowerCase();
					}else if(mes.type == Type.WHOIS_AFTERNET){
						WUser wser = whois.get(mes.user);
						wser.afternetName = mes.message;
						user = afternet.get(wser.afternetName);
						if(user != null){
							users.remove(wser.adress);
							users.put(wser.adress, user);
						}
					}else if(mes.type == Type.WHOIS_END){
						WUser wser = whois.get(mes.user);
						Logger.print(users.toString());
						if(!(wser != null)) wser = new WUser();
						if(!(wser.adress != null))wser.adress = "";
						user = users.get(wser.adress);
						if(user != null){
							String auth[] = {"","",user.loginName,user.password};
							newUser(wser.adress,auth);
							user = users.get(wser.adress);
							if(!wser.afternetName.isEmpty()){
								user.afternetName = wser.afternetName;
								afternet.remove(wser.afternetName);
								afternet.put(wser.afternetName, user);
							}
							whois.remove(wser.name);
							connection.send("PRIVMSG #hvz : "+wser.name+" is "+user.name);
						}else{
							connection.send("PRIVMSG #hvz : "+wser.name+" is not authed");
						}
						Logger.print("Channels: "+wser.channels);
						String[] stringx = wser.channels.split("[ :]");
						Logger.print("ChannelList: "+stringx.toString());
						for(int x = 0; x < stringx.length;x++){
							if(user != null){
								Logger.print("User Found, using "+user.name);
								if(stringx[x].matches("#hvz-horde") && !(user.horde || user.admin)){
									connection.send("PRIVMSG #hvz-horde :.k "+mes.user+" no spying!");
								}else if(stringx[x].matches("#hvz-resistance") && !(user.resistance || user.admin)){
									connection.send("PRIVMSG #hvz-resistance :.k "+mes.user+" no spying!");
								}else if(mes.channel.matches("#hvz")){
									sleep(1);
								}
							}else{
								Logger.print("User Not Found "+ mes.adress);
								if(stringx[x].matches("#hvz")){
									sleep(1);
								}
								if(stringx[x].matches("#hvz-horde")){
									connection.send("PRIVMSG #hvz-horde :.k "+mes.user+" no spying!");
								}
								if(stringx[x].matches("#hvz-resistance")){
									connection.send("PRIVMSG #hvz-resistance :.k "+mes.user+" no spying!");
								}
							}
						}
					}else{
						//Logger.print("UNPARSED from: "+mes.adress+" to: "+mes.channel+" data: "+mes.message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	private boolean newUser(String adress, String[] auth) {
		Logger.print(adress);
		if(!adress.matches("hvzmaster.Users.AfterNET.Org")){
			users.remove(adress);
			User user = new User();
			user.name = "No Body";
			try {
			    // Construct data
			    String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(auth[2].trim(), "UTF-8");
			    data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(auth[3].trim(), "UTF-8");
			    Logger.print(data);
			    // Send data
			    URL url = new URL("http://hvzgatech.com:80/auth.php");
			    URLConnection conn = url.openConnection();
			    conn.setDoOutput(true);
			    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			    wr.write(data);
			    wr.flush();
	
			    // Get the response
			    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    String line;
			    
			    while ((line = rd.readLine()) != null) {
			    	Logger.print("webreport "+line);
			        String lines[] = line.split(" ");
			        if(lines.length > 3){
				        if(lines[0].matches("1")){
				        	user.admin = true;
				        }
				        if(lines[1].matches("-3") || lines[1].matches("1") ){
				        	user.resistance = true;
				        }
				        if(Integer.parseInt(lines[1]) < 0){
				        	user.horde = true;
				        }
				        user.name = lines[2] +  " " + lines[3];
			        }
			    }
			    wr.close();
			    rd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			user.loginName = auth[2];
			user.password = auth[3];
			user.adress = adress;
			user.afternetName = "<>";
			users.put(adress, user);
			Logger.print(users.toString());
			if(user.name.matches("No Body")){
				return false;
			}else{
				return true;
			}
		}
		return true;
	}
	
	

}
