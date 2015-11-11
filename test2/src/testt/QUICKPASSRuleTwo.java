package testt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 活动开始时间：8月17日－11月17日 活动-->活动规则： 1
 * 每个机器每月200个名额，每卡每天可享受一次1分购机会，不与9折冲突，每月1分名额用完之后，用户参与9折活动，商品不限制价格，只可以买饮料。 2
 * 每卡每天除了一分名额外还可以享受一次9折机会，9折不限制月总量，每卡每天1次机会。
 */

public class QUICKPASSRuleTwo {
	private static String path = "D:/Card" + File.separator;
	public static boolean isrecord = false;
	private static int ruleType;// 1=1分活动 2=9折活动
	private static int nb, NB;

	public int rule(String id, int money) {
		int Money = money;
		String Id = id;
		int date1 = 20151001;
		int date2 = 20151101;
		int date3 = 20151201;
		int date4 = 20151231;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date nowTime = new Date();
			int nowDate = Integer.parseInt(sdf.format(nowTime));

			if (nowDate < date1 || nowDate > date4) {// 判断不在8、17-11、17日期内
				Logger.info("<<<< date is error :" + nowDate);
				return Money;
			}
			// 今日刷卡1分卡号名单
			Map<String, Object> TodayCardMapYi = FileUtil.fileToMap(path,nowDate + "Yi.txt");
			// 今日刷卡9折卡号名单
			Map<String, Object> TodayCardMapJiu = FileUtil.fileToMap(path,nowDate + "Jiu.txt");
			
			if (TodayCardMapJiu.containsKey(Id)) {// 卡今日已经刷过
				Logger.info("<<<< card is used today :" + Id);
				return Money;
			}
			
			// 活动数量
			if(date1<=nowDate && nowDate<date2){
				Map<String, Object> CNBOMap = FileUtil.fileToMap1(path, "201510.txt");
				int one;
				if(CNBOMap.get("count") == null){
					one = 0;
				}else {
					one =Integer.parseInt((String) CNBOMap.get("count"));
				}
				nb = one;
				NB = 200 - one;
			}else if(date2<=nowDate && nowDate<date3){
				Map<String, Object> CNBOMap = FileUtil.fileToMap1(path, "201510.txt");
				Map<String, Object> CNBTMap = FileUtil.fileToMap1(path, "201511.txt");
				int one , two;
				if(CNBOMap.get("count") == null){
					one = 0;
				}else {
					one =Integer.parseInt((String) CNBOMap.get("count"));
				}
				if(CNBTMap.get("count") == null){
					two = 0;
				}else {
					two = Integer.parseInt((String) CNBTMap.get("count"));
				}
				nb = two;
				NB = 400 - one;
			}else {
				Map<String, Object> CNBOMap = FileUtil.fileToMap1(path, "201510.txt");
				Map<String, Object> CNBTMap = FileUtil.fileToMap1(path, "201511.txt");
				Map<String, Object> CNBTHMap = FileUtil.fileToMap1(path, "201512.txt");
				int one , two , thr;
				if(CNBOMap.get("count") == null){
					one = 0;
				}else {
					one =Integer.parseInt((String) CNBOMap.get("count"));
				}
				if(CNBTMap.get("count") == null){
					two = 0;
				}else {
					two = Integer.parseInt((String) CNBTMap.get("count"));
				}
				if(CNBTHMap.get("count") == null){
					thr = 0;
				}else {
					thr = Integer.parseInt((String) CNBTHMap.get("count"));
				}
				nb = thr;
				NB = 600 - one - two;
			}
			
			if ( nb <= NB) {
				if (TodayCardMapYi.containsKey(Id)) {// 已经参加1分活动
					Logger.info("TodayCardMapYi.txt contain this id so it is 9 zhe. "+ Id);
					Money = money * 90 / 100;
					isrecord = true;
					ruleType = 2;
					return Money;
				}
				Logger.info(Id + " is 1fen ");
				Money = 1;
				isrecord = true;
				ruleType = 1;
				Logger.info("old number is : " + nb);
				nb++;
				Logger.info("new number is : " + nb);
				return Money;
			} else {
				Logger.info("1 fen is finash so it is 9 zhe. " + Id);
				Money = money * 90 / 100;
				isrecord = true;
				ruleType = 2;
				return Money;
			}

		} catch (Exception e) {
			Logger.error("<<<< Exception e :" + e.getMessage());
		}
		return Money;
	}

	/**
	 * 记录做过营销活动的卡号等数据
	 */
	public boolean record(String data) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowDate = sdf.format(new Date());
			if (ruleType == 1) {
				FileUtil.write(path + nowDate + "Yi.txt", data, true);
				FileUtil.write(path + nowDate.substring(0, 6) + ".txt", nb+"", false);
			} else if (ruleType == 2) {
				FileUtil.write(path + nowDate + "Jiu.txt", data, true);
			} else {
				Logger.error("ruleType(" + ruleType + ") is error, record fail");
				return false;
			}
			
			ruleType = 0;
			Logger.info(">>>>> record:" + data + " is sucussful.");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
}
