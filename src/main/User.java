package main;

public class User implements Comparable{
	String adress = "";
	String afternetName = "";
	String loginName = "";
	String password = "";
	String name = "";
	boolean horde = false;
	boolean resistance = false;
	boolean admin = false;
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	public int compareTo(User user){
		return adress.compareToIgnoreCase(user.adress);
	}
	public String toString(){
		return "[Adress: "+adress+" ,afternetName: "+afternetName+" ,loginName: "+loginName+" ,password: "+password+" ,name: "+name+" ]";
	}
}
