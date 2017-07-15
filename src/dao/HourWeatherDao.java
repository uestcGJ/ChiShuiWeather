package dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import domain.HourWeather;
import net.sf.json.JSONArray;

public interface HourWeatherDao extends BaseDao<HourWeather> {
    //
	List<HourWeather>findLastDayData(String date);
	public List<HourWeather> findHourData(String stationCode,int hour);
	public HourWeather findByStationCodeAndDate(String stationCode,Timestamp stamp);
	/***
	 * 通过监测区域号和天气要素查询该要素
	 * 返回格式为：stationCode：局站代号
	 *            value:查询要素的值
	 * **/
	List<Map<String, Object>> findInfosByAreaCodeAndDate(String areaCode, Timestamp stamp, String param);
	/***
	 * 通过监测站代号和天气要素查询该要素
	 * 返回格式为：stationCode：局站代号
	 *            以及各要素组成的键值对
	 * **/
	Map<String, Object> findInfosByStationaCodeAndDate(String stationCode, Timestamp stamp, String param);
//	获取总记录条数
	public long getItemSize(String areaCode,Timestamp stamp);
//分页查询
	List<HourWeather> findPagination(String areaCode, Timestamp stamp,int col,int currentPage);
	List<HourWeather> getVagueStationWeather(String areaCode,String stationName,Timestamp stamp,int col,int page);
	JSONArray findInfoByAreaCodeAndDate(String areaCode, String item);
	long getVagueStationWeatherCount(String areaCode,String stationName,Timestamp stamp);
	/**
	 * 通过局站编号和时间间隔、温度类型查询温度值
	 * @param stationCode String 局站代码
	 * @param intervalHour int 间隔的小时数
	 * @param item String 要素，high、low、ave
	 * @return float 查询的值
	 * */
	public float findLastTimeTempItem(String stationCode,int intervalHour,String item);
	
	/**
	 * 通过局站编号和参数类型获取天气参数，天气参数只有一项，监测站可能为多个，各监测站编号之间通过,分隔
	 * @param stationCode String 局站代码
	 * @param item String 要素
	 * @return List<Map<String,Object>> 每个Map中包含：<br/>
	 *   stationName:监测站名称<br/>
	 *   value：查询的参数值
	 * */
	public List<Map<String,Object>> findStationsLastItem(String stationCode,String item);
}
