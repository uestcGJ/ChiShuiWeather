package dataServer.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/***
	 * get date  contains hours
	 * ***/
	public static Timestamp getDate(){
		Date date=new Date();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH");//hh为12小时制 HH为24小时制
		String format=sf.format(date);
		try {
			date=sf.parse(format.concat(":00:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return  new Timestamp(date.getTime());
	}
	/***将时间转换为14位的字符串格式，如20170324140000，用于CIMISS查询用
	 * @param Timestamp stamp 时间
	 * @return String
	 * **/
	public static String getStringDate(Timestamp stamp){
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHH");
		String date=sf.format(stamp)+"0000";
		return date;
	}
	/***
	 * 获取当前时间，形如2017-03-30 
	 * **/
	public static String getCurrentDate(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		String date=sf.format(new Date());
		return date;
	}
	/***
	 * 获取当前时间，形如2017年05月12日 12点 08分
	 * **/
	public static String getCurrentTime(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日 HH点 mm分");
		String date=sf.format(new Date()).replaceAll(" ", "");
		return date;
	}

	/***
	 * 获取当前整点时间的14位字符串格式
	 * **/
	public static String getCurrentStringDate(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHH");
		String date=sf.format(new Date())+"0000";
		return date;
	}
}
