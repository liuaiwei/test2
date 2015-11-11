package testt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static String serialNo;
	private static String cardNo;
	private static String data;
	private static String newStr;
	private static int name;
	private static String fileName = "vcard_2015-09-20.log";
//	private static String fileName = "vcard_2015-09-30.log";
//	private static String fileName = "vcard_2015-10-00.log";
//	private static String fileName = "vcard_2015-10-10.log";
	
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		try {
			for (int i = 0; i < hexString.length(); i++) {
				tmp = "0000"
						+ Integer.toBinaryString(Integer.parseInt(
								hexString.substring(i, i + 1), 16));
				bString += tmp.substring(tmp.length() - 4);
			}
		} catch (Exception e) {
			Logger.error("Exception : " + e.getMessage());
		}
		return bString;
	}

	public static void main(String[] args) {
		
//		int number = 10000;
//		
		String a = hexString2binaryString("04");
		Logger.info("start.  "+a);
		
		
//		String pathName = "D:/ubox/";
//
//		try {
//			for (int i = 0; i < 10; i++) {
//				name = i + Integer.parseInt(fileName.substring(15, 16));
//				Logger.info("start.  " + fileName.substring(0, 15) + name+ ".log");
//				file(pathName, fileName.substring(0, 15) + name + ".log");
//			}
//		} catch (Exception e) {
//			Logger.error("Exception " + e.getMessage());
//		}
	}

	public synchronized static void file(String pathName, String fileName)
			throws Exception {

		InputStream is = null;
		BufferedReader reader = null;
		try {
			File filePath = new File(pathName);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			File file = new File(pathName + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			is = new FileInputStream(file);

			String line; // 用来保存每行读取的内容
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) { // 如果 line 为空说明读完了
				if (line.length() > 12) {
					if (line.substring(1, 12).equals("from socket")) {
						Pattern p = Pattern.compile("serialNo(.+?),\"");
						Matcher m = p.matcher(line);
						while (m.find()) {
							serialNo = m.group().substring(11, 29);
							Logger.info("---------------------serialNo1 : "+ serialNo);
						}
					}
					if (line.substring(1, 12).equals("jni.GetCard")) {
						cardNo = line.substring(32, 39);
						Logger.info("---------------------cardNo : " + cardNo);
					}
					if (line.substring(1, 12).equals(">>>>send:ip")) {
						data = line.substring(39, line.length());
						Logger.info("---------------------data : " + data);
					}
					if (line.substring(1, 10).equals("to socket")) {
						Logger.info("---------------------to socket");
						Pattern a = Pattern.compile("resultCode\":200");
						Matcher b = a.matcher(line);
						if (b.find()) {
							String code = b.group().substring(12, 15);
							Logger.info("---------------------code : " + code);
							if (code.equals("200")) {
								Pattern p = Pattern.compile("serialNo(.+?),\"");
								Matcher m = p.matcher(line);
								while (m.find()) {
									String serialno = m.group().substring(11,29);
									Logger.info("---------------------serialno2 : "+ serialno);
									if (serialno.equals(serialNo)) {
										Pattern c = Pattern.compile("cardDesc\":\"扣费成功");
										Matcher d = c.matcher(line);
										if (d.find()) {
											String adddata = "cardDesc\":\"扣费成功 "+ data;
											newStr = line.replace("cardDesc\":\"扣费成功",adddata);
											Logger.info("---------------------newStr1 : "+ newStr);
										} else {
											String adddata = "cardDesc\":\"扣费成功 "+ data;
											newStr = line.replace("cardDesc\":\"", adddata);
											Logger.info("---------------------newStr2 : "+ newStr);
										}
										Pattern p1 = Pattern
												.compile("cardNo\":\"\"");
										Matcher m1 = p1.matcher(line);
										if (m1.find()) {
											String addid = "\"cardNo\":\""+ cardNo;
											String s2 = newStr.replaceAll("\"cardNo\":\"", addid);
											Logger.info("---------------------msg1 : "+ s2);
											write(s2, true);
										} else {
											Logger.info("---------------------msg2 : "+ newStr);
											write(newStr, true);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (is != null)
					is.close();
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public synchronized static void write(String data, boolean append)
			throws Exception {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			String path = "D:/ubox/";
			File newDir = new File(path);
			if (!newDir.exists()) {// 检查父目录是否存在，不存在就创建
				newDir.mkdirs();
			}
			fw = new FileWriter("D:/ubox/" + fileName.substring(6, 15) + name+ ".txt", append);
			bw = new BufferedWriter(fw);
			bw.write(new String(data.getBytes(), "UTF-8"));
			bw.newLine();

			bw.flush();
			fw.flush();
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (null != bw) {
					bw.close();
				}
				if (null != fw) {
					fw.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
