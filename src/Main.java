

import main.IRC;
import main.IRC_AI;
import main.Logger;

public class Main {
	public static void main(String args[]){
		IRC connection;
		Logger.startup();
		try {
		connection = new IRC("anonbot");
		Logger.print("Starting a new Session");
		IRC_AI ai = new IRC_AI(connection);
		ai.start();
		while(ai.isAlive());
		}catch(Exception e){
			Logger.print(e.getMessage());
			e.printStackTrace();
		}
	}
}
