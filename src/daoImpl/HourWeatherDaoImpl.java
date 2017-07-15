package daoImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.HourWeatherDao;
import dataServer.util.DateUtil;
import domain.HourWeather;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HourWeatherDaoImpl extends BaseDaoImpl<HourWeather> implements HourWeatherDao {
	/**根据局站码查找24小时天气情况
	 * @param stationCode String 局站代码
	 * */
	public List<HourWeather> findLastDayData(String stationCode) {
		//直接利用sql语句进行查询，主要是源于利用其时间函数
		String sql="select * from Hour_Weather as weather where weather.station_code= '"+stationCode+
				"' and weather.date between "+ "date_sub(date_format(now(),'%Y-%m-%d %H:00:00'),interval 1 day) and date_format(now(),'%Y-%m-%d %H:00:00')" ;
		return findSQL(sql, HourWeather.class);
	}
	/**
	 * @param stationCode String 局站代码
	 * */
	public float findLastTimeTempItem(String stationCode,int intervalHour,String item) {
		String sql="";
		String param="temp_"+item;
		switch (item){
		    case "high":
		    	sql="select max("+param+") from Hour_Weather  where";
		    	break;
		    case"low":
		    	sql="select min("+param+") from Hour_Weather  where "+param+"!='-99' and";
		    	break;
		    case"ave":
		    	sql="select avg("+param+") from Hour_Weather  where "+param+"!='-99' and";
		    	break;
		    default:
		    	break;
		}
		//直接利用sql语句进行查询，主要是源于利用其时间函数
		sql+="  station_code= '"+stationCode+"' and  date between "+ "date_sub(date_format(now(),'%Y-%m-%d %HH:00:00'),interval  "+
		      intervalHour+" hour) and date_format(now(),'%Y-%m-%d %HH:00:00')";
		return findFloatInSQL(sql);
	}
	/***根据局站码和时间间隔查找指定时间前的天气信息
	 * @param stationCode 局站代码
	 * @param hour int 间隔小时数
	 * ***/
	public List<HourWeather> findHourData(String stationCode,int hour) {
		//直接利用sql语句进行查询，主要是源于利用其时间函数
		String sql="select * from Hour_Weather as weather where weather.station_code= '"+stationCode+
				"' and weather.date between "+ "date_sub(date_format(now(),'%Y-%m-%d %H:00:00'),interval "+hour+" hour) and date_format(now(),'%Y-%m-%d %H:00:00')" ;
		 return findSQL(sql, HourWeather.class);
	}
	/**
	 * 根据局站代码和时间进行查找
	 * 
	 * ***/
	public HourWeather findByStationCodeAndDate(String stationCode, Timestamp stamp) {
		// TODO Auto-generated method stub
		 String sql="select * from Hour_Weather as weather where weather.station_code= '"+stationCode+
				"' and weather.date= '"+stamp+"'";
		 return findOneInSQL(sql,HourWeather.class);
	}
	/*** 
	 * 
	 * ***/
	public List<Map<String,Object>> findInfosByAreaCodeAndDate(String areaCode, Timestamp stamp, String param) {
		// TODO Auto-generated method stub
		 String sql="select station_code," +param+ " from Hour_Weather  where area_code= '"+areaCode+
				"' and date= '"+stamp+"'";
		 List<Object[]> rainfalls=findFieldsInSQL(sql);
		 List<Map<String,Object>> rains=new ArrayList<>();
		 for(Object[] rainfall:rainfalls){
			 Map<String,Object> rain=new HashMap<String,Object>();
			 rain.put("stationCode", rainfall[0]);
			 rain.put("value", rainfall[1]);
			 rains.add(rain);
		 }
		 return rains;
	}
	public Map<String, Object> findInfosByStationaCodeAndDate(String stationCode, Timestamp stamp, String params){
		 String sql="select " +params+ " from Hour_Weather  where station_code= '"+stationCode+
					"' and date= '"+stamp+"'";
			 Object[] result=findFieldInSQL(sql);
			 String[] items=params.split(","); 
			 Map<String, Object> infos =new LinkedHashMap<>();
             if(result!=null){
            	 for(int i=0;i<result.length;i++){
            		 infos.put(items[i], result[i]);
            	 }
             }
			 return infos;
	}
//获取总记录条数
	public long getItemSize(String areaCode,Timestamp stamp){
		 String sql=" select count(*) from Hour_Weather where area_code= '"+areaCode+"' and date= '"+stamp+"'";
		 return countAmounts(sql);
	}	
//分页查询
	public List<HourWeather> findPagination(String areaCode,Timestamp stamp,int col,int currentPage){
		 String sql="select * from Hour_Weather as hw  where area_code= '"+areaCode+"' and date= '"+stamp+"' order by id desc";
		 return findPagination(sql,HourWeather.class,currentPage,col);
	}
	@Override
	public JSONArray findInfoByAreaCodeAndDate(String areaCode, String item) {
		 String sql="select station_code," +item+ ",station_name from Hour_Weather  where area_code= '"+areaCode+
					"' and date= '"+DateUtil.getDate()+"'";
			 List<Object[]> rainfalls=findFieldsInSQL(sql);
			 JSONArray rains=new JSONArray();
			 for(Object[] rainfall:rainfalls){
				 JSONObject rain=new JSONObject();
				 rain.put("stationCode", rainfall[0]);
				 String value=String.valueOf(rainfall[1]);
				 value=value.contains("-99")?"0":value;
				 rain.put("value",value);
				 rain.put("name", rainfall[2]);
				 rains.add(rain);
			 }
			 return rains;
	}
//模糊查询站点气象信息
	public List<HourWeather> getVagueStationWeather(String areaCode,String stationName,Timestamp stamp,int col,int page){
		String sql="select * from Hour_Weather as hw  where area_code like'%"+areaCode+"%' and station_name like'%"+stationName+"%' and date='"+ stamp+"'order by id desc";
		return findPagination(sql,HourWeather.class,page,col);
	}
//模糊查询站点气象信息时统计条目数
	public long getVagueStationWeatherCount(String areaCode,String stationName,Timestamp stamp){
		String sql="select count(*) from Hour_Weather as hw  where area_code like'% "+areaCode+"%' and station_name like'% "+stationName+"%' and date='"+ stamp+"'";
		return countAmounts(sql);
	}
	@Override
	public List<Map<String, Object>> findStationsLastItem(String stationCode, String item) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> infos=new ArrayList<Map<String, Object>>();
		int limit=stationCode.split(",").length;//判断要查询的监测站的数量
		String sql="select station_name,"+item+",station_code from Hour_Weather where station_code in ("+stationCode+") order by id desc limit "+limit;
		List<Object[]> results=findFieldsInSQL(sql);
		if(results!=null){
			//由于数据库中可能存在无效的监测站，此时查询所得的数据中存在某些监测站前一个小时的数据项，将其移除
			int size=results.size()-1;
			for(int i=0;i<size/2;i++){
				if(results.get(i)[0].equals(results.get(size-i)[0])){
					results.remove(size-i);
				}
			}
			for(Object[] result:results){
				Map<String, Object> info=new HashMap<String,Object>();
				info.put("stationName",result[0]);
				info.put("value",result[1]);
				info.put("stationCode",result[2]);
				infos.add(info);
			}
		}
		return infos;
	}
}
