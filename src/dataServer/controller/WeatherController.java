package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.CallCimissApi;
import dataServer.util.DateUtil;
import domain.Area;
import domain.HourWeather;
import domain.Stations;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.FindService;
/***
 * 与天气相关的controller
 * 
 * ***/
@Controller
public class WeatherController {
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    
    /***
     * 分页获取区域的实时监测信息
     * @param areaCode 区域代码
     * @param page 当前页码
     * @param col 表格的行数
     * **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value="weather/getPageRealTimeWeather")
	public void getPageRealTimeWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 String areaCode=request.getParameter("areaCode");
		 int currentPage=Integer.parseInt(request.getParameter("page"));//获取当前页码数
		 int col=Integer.parseInt(request.getParameter("col"));//表格列数
		 long totalCount=0;//总记录数
		 List<Area>areas=new ArrayList<>();
		 if(areaCode.isEmpty()){//为空表示查询所有区域的站点
			 areas=findService.findAllAreas();
		 }else{
			 Area area=findService.findAreaByCode(areaCode);
			 if(area!=null){
				 areas.add(area);
			 }
		 }
		 List<HourWeather> weathers=new ArrayList<>();
		 Timestamp stamp=DateUtil.getDate();
		 //分页获取指定区域各站点实况
		 for(Area area:areas){
			 List<HourWeather> weather=findService.findPaginationWeatherInfo(area.getCode(),stamp,col,currentPage); 
			 if(weather.isEmpty()){//可能数据还未同步，查不到数据时向前推一个小时
				 stamp=new Timestamp(stamp.getTime()-60*60*1000);
				 weather=findService.findPaginationWeatherInfo(area.getCode(),stamp,col,currentPage); 
			 }
			 totalCount+=findService.getWeatherItemSize(areaCode, stamp);//保存记录数目
			 weathers.addAll(weather);
		 }
		 boolean statusCode=false;
		 JSONObject responseData=new JSONObject();
		 if(!weathers.isEmpty()){
			 statusCode=true;
			 int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
			 pages=(pages==0)?1:pages;
			 responseData.put("totalPage", pages);
			 JSONArray staInfos=new JSONArray();
			 for(HourWeather hw:weathers ){
				 JSONObject staWeather=new JSONObject();
				 JSONObject staInfo=new JSONObject();
				 Stations station=findService.findStationByStationCode(hw.getStationCode());
				 staWeather.put("rainfall_1h", hw.getRainfall_1h());
				 staWeather.put("rainfall_3h", hw.getRainfall_3h());
				 staWeather.put("rainfall_6h", hw.getRainfall_6h());
				 staWeather.put("rainfall_12h", hw.getRainfall_12h());
				 staWeather.put("rainfall_24h", hw.getRainfall_24h());
				 staWeather.put("temp_ave", hw.getTempAve());
				 staWeather.put("temp_high", hw.getTempHigh());
				 staWeather.put("temp_low", hw.getTempLow());
				 Iterator<String> it=staWeather.keys();
				 //将缺失数据换为"缺失"
				 while(it.hasNext()){
					 String key=(String)it.next();
					 if(staWeather.getString(key).contains("-99")){
						 staWeather.replace(key, "缺失");
					 }
				 }
				 staInfo.put("area", station.getArea().getName());
				 staInfo.put("cnty", station.getCnty());
				 staInfo.put("name", station.getName());
				 staInfo.put("code", station.getCode());
				 staInfo.put("weather", staWeather);
				 staInfos.add(staInfo);
			}
			 responseData.put("stations", staInfos);//封装
		}else{
				responseData.put("err", "未查到满足条件的数据，可能是数据库尚未同步，请稍后重试。");
			}
		responseData.put("statusCode", statusCode);
		
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		//return as JSON
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***
     * 分页模糊查询监测站的实时监测信息
     * @param areaCode 区域代码
     * @param stationName 监测站名称 
     * @param page 当前页码
     * @param col 表格的行数
     * **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value="weather/getVagueStationWeather")
	public void getVagueStationWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 String areaCode=request.getParameter("areaCode");
		 String stationName=request.getParameter("stationName");
		 int currentPage=Integer.parseInt(request.getParameter("page"));//获取当前页码数
		 int col=Integer.parseInt(request.getParameter("col"));//表格列数
		 long totalCount=0;//总记录数
		 Timestamp stamp=DateUtil.getDate();
		 List<HourWeather> weathers=findService.getVagueStationWeather(areaCode,stationName,stamp,col,currentPage);
		 totalCount=findService.getVagueStationWeatherCount(areaCode, stationName, stamp);
		 //分页获取指定区域各站点实况
		 if(weathers.isEmpty()){//可能数据还未同步，查不到数据时向前推一个小时
			stamp=new Timestamp(stamp.getTime()-60*60*1000);
			weathers=findService.getVagueStationWeather(areaCode,stationName,stamp,col,currentPage);
			 totalCount=findService.getVagueStationWeatherCount(areaCode, stationName, stamp);
		 }
		 boolean statusCode=false;
		 JSONObject responseData=new JSONObject();
		 if(!weathers.isEmpty()){
			 statusCode=true;
			 int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
			 pages=(pages==0)?1:pages;
			 responseData.put("totalPage", pages);
			 JSONArray staInfos=new JSONArray();
			 for(HourWeather hw:weathers ){
				 JSONObject staWeather=new JSONObject();
				 JSONObject staInfo=new JSONObject();
				 Stations station=findService.findStationByStationCode(hw.getStationCode());
				 staWeather.put("rainfall_1h", hw.getRainfall_1h());
				 staWeather.put("rainfall_3h", hw.getRainfall_3h());
				 staWeather.put("rainfall_6h", hw.getRainfall_6h());
				 staWeather.put("rainfall_12h", hw.getRainfall_12h());
				 staWeather.put("rainfall_24h", hw.getRainfall_24h());
				 staWeather.put("temp_ave", hw.getTempAve());
				 staWeather.put("temp_high", hw.getTempHigh());
				 staWeather.put("temp_low", hw.getTempLow());
				 Iterator<String> it=staWeather.keys();
				 //将缺失数据换为"缺失"
				 while(it.hasNext()){
					 String key=(String)it.next();
					 if(staWeather.getString(key).contains("-99")){
						 staWeather.replace(key, "缺失");
					 }
				 }
				 staInfo.put("area", station.getArea().getName());
				 staInfo.put("cnty", station.getCnty());
				 staInfo.put("name", station.getName());
				 staInfo.put("code", station.getCode());
				 staInfo.put("weather", staWeather);
				 staInfos.add(staInfo);
			}
			 responseData.put("stations", staInfos);//封装
		}else{
				responseData.put("err", "未查到满足条件的数据，可能是数据库尚未同步，请稍后重试。");
			}
		responseData.put("statusCode", statusCode);
		
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		//return as JSON
		out.println(responseData);
		out.flush();
		out.close();
	}	
	 /***
     * 获取某个地区的特定天气参数分布数据
     * @param areaCode 地区代码
     * **/
	@RequestMapping(value="weather/getDistribution")
	public void getDistribution(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String areaCode=request.getParameter("areaCode");
		String item=request.getParameter("item");
		if(areaCode!=null&&item!=null){
			JSONArray weathers= findService.findInfoByAreaCodeAndDate(areaCode,item);
			if(!weathers.isEmpty()){
				responseData.put("weathers",weathers);
				statusCode=true;
			}else{
				responseData.put("err", "获取天气信息失败，数据库中不存在当前条目，可能是还未同步，请稍后重试。");
			}
		}else{
			responseData.put("err", "获取天气信息失败，缺少必要数据，请核对后重试。");
		}
	    responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		//return as JSON
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***
     * 获取具体站点的详细小时天气信息
     * @param stationCode 局站代码
     * **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value="weather/getDetialHourWeather")
	public void getDetialHourWeather(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String stationCode=request.getParameter("stationCode");
		JSONObject responseData=new JSONObject();
		Timestamp stamp=DateUtil.getDate();
		HourWeather weather=findService.findHourWeatherByStationCodeAndDate(stationCode, stamp);
		if(weather==null){
			//可能数据还未同步，查不到数据时向前推一个小时
			stamp=new Timestamp(stamp.getTime()-60*60*1000);
			weather=findService.findHourWeatherByStationCodeAndDate(stationCode, stamp);
		}
		if(weather!=null){
			 statusCode=true;
			 JSONObject staWeather=new JSONObject();
			 JSONObject staInfo=new JSONObject();
			 Stations station=findService.findStationByStationCode(weather.getStationCode());
			 staWeather.put("rainfall_1h", weather.getRainfall_1h());
			 staWeather.put("rainfall_3h", weather.getRainfall_3h());
			 staWeather.put("rainfall_6h", weather.getRainfall_6h());
			 staWeather.put("rainfall_12h", weather.getRainfall_12h());
			 staWeather.put("rainfall_24h", weather.getRainfall_24h());
			 staWeather.put("humi_re", weather.getHumiRe());
			 staWeather.put("humi_re_min", weather.getHumiReMin());
			 staWeather.put("temp_dew", weather.getTempDew());
			 staWeather.put("temp_ave", weather.getTempAve());
			 staWeather.put("temp_high", weather.getTempHigh());
			 staWeather.put("temp_low", weather.getTempLow());
			 staWeather.put("temp_24_max", weather.getTemp24hHigh());
			 staWeather.put("temp_24_min", weather.getTemp24hLow());
			 Iterator<String> it=staWeather.keys();
			 //将缺失数据换为"缺失"
			 while(it.hasNext()){
				 String key=(String)it.next();
				 if(staWeather.getString(key).contains("-99")){
					 staWeather.replace(key, "缺失");
				 }
			 }
			 staInfo.put("area", station.getArea().getName());
			 staInfo.put("cnty", station.getCnty());
			 staInfo.put("name", station.getName());
			 staInfo.put("code", station.getCode());
			 staInfo.put("lat", station.getLat());
			 staInfo.put("lng", station.getLng());
			 staInfo.put("weather", staWeather);
			 responseData.put("station", staInfo);
		
		}
		else{
			responseData.put("err", "未查到满足条件的数据，可能是数据库尚未同步，请稍后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		//return as JSON
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 获取各站点整点天气，用于画等值图
	 * 下发的参数包含两个字段 areaCode与time
	 * @param areaCode为地区编号，一般为赤水
	 * @param item 气象要素 rainfall、temp、humi
	 *@param time为时间范围取值可能为1h 3h 6h 12h 24h分别表示过去1h、3h、6h、12h、24h 
	 * **/
	@RequestMapping(value="graph/getHourItem")
	public void getHourItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String areaCode=request.getParameter("areaCode");
		String time=request.getParameter("time");
		String item=request.getParameter("item");//天气要素  rainfall、temp、humi
		Timestamp stamp=DateUtil.getDate();
		String param=item+"_"+time;//要查询的参数，形如rainfall_3h
		JSONObject responseData=new JSONObject();
		List<Map<String,Object>> rainfalls=findService.findWeatherInfoByAreaCodeAndDate(areaCode, stamp, param);
		if(rainfalls.isEmpty()){
			//可能数据还未同步，查不到数据时向前推一个小时
			stamp=new Timestamp(stamp.getTime()-60*60*1000);
			rainfalls=findService.findWeatherInfoByAreaCodeAndDate(areaCode, stamp, param);
		}
		if(!rainfalls.isEmpty()){
			 statusCode=true;
			 for(int i=0;i<rainfalls.size();i++){
				 Map<String,Object> rainfall=rainfalls.get(i);
				 String stationCode=(String) rainfall.get("stationCode");
				 Stations station=findService.findStationByStationCode(stationCode);
				 rainfall.put("lat", station.getLat());
				 rainfall.put("lng", station.getLng());
				 rainfall.put("name", station.getName());
			 }
			 responseData.put("rainfall", rainfalls);
		}
		else{
			responseData.put("err", "未查到满足条件的数据，可能是数据库尚未同步，请稍后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 根据指定的开始时间、截止时间和天气要素查询天气信息用于绘制历史天气分布图
	 * @param startTime 开始时间 形如20170310020000
	 * @param endTime 截止时间  形如20170310020000
	 * @param item 气象要素,rainfall、temp_ave、temp_high、temp_low
	 * @return 返回的是一个JSONObject。
	* 		statusCode true表示成功  false表示失败；
	*       infos JSONArray，各监测站的天气信息和经纬度信息，每个元素为一个JSONObject，包含以下字段：
	*       	value:天气要素值
	*           lat：经度
	*           lng:纬度 
	 * **/
	@RequestMapping(value="graph/getHisHourItem")
	public void getHisHourItem(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject responseData=CallCimissApi.getHistoryStationWeatherInfo(request.getParameter("startTime"),
								request.getParameter("endTime"),request.getParameter("item"));
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
}
