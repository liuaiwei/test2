package testt;

public class Logger {
	public static void info(String str){
		System.out.println(str);
	}
	public static void error(String str){
		System.out.println(str);
	}
	public static void error(String str,Exception e){
		System.out.println(str);
		e.printStackTrace();
	}
}
