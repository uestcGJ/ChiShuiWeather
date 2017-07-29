package dataServer.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import domain.HourWeather;
import domain.Liaisons;
import domain.Stations;
import domain.Warn;
import domain.WarnStrategy;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.FindService;

/***
 * 用于周期同步CIMISS系统中的气象数据
 * 
 * ***/
public class PeriodSynchroData extends TimerTask{
	//注入数据库操作
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    /**
     * 未完成数据同步的站点，包含三个字段：
     *    站点代号：stationCode String
     *    时间信息：timeStamp   Timestamp
     *    重查次数：repeatTime  int
     * **
     */
    private static List<Map<String,Object>> unSynchroList; 
    private static final int repeatTimes=6*24*2;//每隔十分钟同步一次，两天还未补齐则放弃
    //静态变量，只初始化一次
    static{
    	unSynchroList=new ArrayList<>();
    }
    /**---------执行方法--------*/
    public void run() {
    	
    	System.out.println("============同步CIMISS数据库============");
    	//查找缺失项
    	getMissingData();
    	//同步整点数据
    	synchroData();
		System.out.println("============同步完成，当前缺失的数据项："+unSynchroList.size());
		System.out.println("============对比预警策略，判断是否产生预警信息===============");
		checkWarnStrategy();//对比预警策略
	}
    /***
     * 同步数据库
     * **/
    private void synchroData(){
    	Timestamp currentDate=DateUtil.getDate();
    	//当期整点时间
    	String time=DateUtil.getStringDate(currentDate);
    	String areaCode="520381,520330,520382";//赤水、习水、仁怀三个地区
    	String element="PRE_1h,PRE_3h,PRE_6h,PRE_12h,PRE_24h,TEM,TEM_Max,TEM_Min,TEM_Max_24h,TEM_Min_24h,DPT,RHU,RHU_Min";
    	//查询CMISS系统，获取气象参数
    	JSONObject json=CallCimissApi.getAreaWeatherInfo(time,areaCode,element);
    	if(json.getBoolean("statusCode")){//查找成功，存放数据
    		JSONArray weatherArray=json.getJSONArray("DS");//读取天气数值
    		for(int i=0;i<weatherArray.size();i++){
    			JSONObject weather=weatherArray.getJSONObject(i);
    			String stationCode=weather.getString("Station_ID_C");
    			if(weather.getBoolean("isMissing")){//存在缺失数据，将当前的置为
    				Map<String,Object> unSynchro=new LinkedHashMap<>();
        			unSynchro.put("timeStamp",currentDate);
        			unSynchro.put("repeatTime",1);
        			unSynchro.put("stationCode",stationCode);
        			if(!contains(stationCode,currentDate)){
        				unSynchroList.add(unSynchro);
        			}
    			}
    			//由于同步的时间间隔较小，为了减少对数据库写入操作次数，只在未同步当前项时才处理存储
    			HourWeather hw=findService.findHourWeatherByStationCodeAndDate(stationCode,currentDate);
                if(hw==null){
                	 this.handleResponse(weather,currentDate);
                	 continue;
                }
          }
    	}else{//查找失败，将所有站点都放入失败队列
    		List<Stations> stations=findService.findAllStations("");
    		for(Stations station:stations){
    			Map<String,Object> unSynchro=new LinkedHashMap<>();
    			unSynchro.put("timeStamp",currentDate);
    			unSynchro.put("repeatTime",1);
    			String stationCode=station.getCode();
    			unSynchro.put("stationCode",stationCode);
    			if(!contains(stationCode,currentDate)){
    				unSynchroList.add(unSynchro);
    			}
        	}
    	}	
    }
    /**
     * 判断队列中是否已经存在该项
     * ***/
    private boolean contains(String stationCode,Timestamp date){
    	for(int i=0;i<unSynchroList.size();i++){
    		Map<String,Object> item=unSynchroList.get(i);
    		if(item.get("stationCode").equals(stationCode)&&item.get("timeStamp").equals(date)){
    			return true;
    		}
    	}
    	return false;
    }
    /***
     * 查找缺失数据
     * **/
    private void getMissingData(){
    	/***
    	 * 用于查找缺失数据的子线程
    	 * **/
    	class GetDefaultThread extends Thread{
    		private Map<String,Object> unSynch;
    		private boolean status=false;
    		public GetDefaultThread(Map<String,Object> unSynch){
    			this.unSynch=unSynch;
    		}
    		public Map<String,Object> getUnSynch(){
    			return this.unSynch;
    		}
    		public boolean getStatus(){
    			return this.status;
    		}
    		public void run(){
        		Timestamp date=(Timestamp)unSynch.get("timeStamp");
        		String time=DateUtil.getStringDate(date);
        		String stationCode=(String)unSynch.get("stationCode");
            	String element="PRE_1h,PRE_3h,PRE_6h,PRE_12h,PRE_24h,TEM,TEM_Max,TEM_Min,TEM_Max_24h,TEM_Min_24h,DPT,RHU,RHU_Min";
        		//访问CIMISS，获取气象数据
        		JSONObject json=CallCimissApi.getStationWeatherInfo(time,stationCode,element);
        		if(json.getBoolean("statusCode")){//查找成功，存放数据
        			JSONArray weatherArray=json.getJSONArray("DS");//读取天气数值
            		for(int index=0;index<weatherArray.size();index++){
            			JSONObject weather=weatherArray.getJSONObject(index);
    	        		if(!weather.getBoolean("isMissing")){//数据补全
    	        			handleResponse(weather,date);
    	        			status=true;
    	        		}
    	        	}
            	}
        	}
    	}
    	/***线程队列**/
    	List<GetDefaultThread> getDefaultThreads=new ArrayList<GetDefaultThread>();
    	for(int i=0;i<unSynchroList.size();i++){
    		Map<String,Object> unSynchro=unSynchroList.get(i);
    		GetDefaultThread defaultThread=new GetDefaultThread(unSynchro);
    		defaultThread.start();
    		getDefaultThreads.add(defaultThread);
    	}
    	/***线程join**/
    	for(GetDefaultThread thread:getDefaultThreads){
    		try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	/**读取结果***/
    	for(GetDefaultThread thread:getDefaultThreads){
    		boolean status=thread.getStatus();
    		Map<String,Object> unAsyno=thread.getUnSynch();
    		if(status){
    			unSynchroList.remove(unAsyno);
    		}else{
    			int repeatTime=(int)unAsyno.get("repeatTime");
    			repeatTime++;
    			if(repeatTime>=repeatTimes){//已经超过24小时，仍未获取到完整数据，放弃当前记录数据的获取
            		System.out.println("============超过24小时仍未获得有效数据，移除项："+unAsyno.get("timeStamp")+",stationCode:"+unAsyno.get("stationCode"));
            		unSynchroList.remove(unAsyno);
            	}else{//未超过24小时，次数加1
            		repeatTime++;
            		unAsyno.put("repeatTime", repeatTime);
            		unSynchroList.set(unSynchroList.indexOf(unAsyno), unAsyno);
            	}
    		}
    	}
    }
    /****
     * 处理回传数据
     * ***/
    public  void handleResponse(JSONObject weather,Timestamp currentDate){
    	String stationCode=weather.getString("Station_ID_C");
		//新建天气信息，存入数据库
		HourWeather hw=findService.findHourWeatherByStationCodeAndDate(stationCode,currentDate);
		boolean isExit=true;
		if(hw==null){
			hw=new HourWeather();
			isExit=false;
		}
		hw.setDate(currentDate);
		hw.setTempDew(Float.valueOf(weather.getString("DPT")));//temp_dew
		hw.setTempAve(Float.valueOf(weather.getString("TEM")));//temp_ave
		hw.setTempHigh(Float.valueOf(weather.getString("TEM_Max")));//temp_high
		hw.setTempLow(Float.valueOf(weather.getString("TEM_Min")));//temp_low
		hw.setTemp24hHigh(Float.valueOf(weather.getString("TEM_Max_24h")));
		hw.setTemp24hLow(Float.valueOf(weather.getString("TEM_Min_24h")));
		hw.setTemp24hAve(findService.findLastTimeTempItem(stationCode, 24, "ave"));
		hw.setTemp6hHigh(findService.findLastTimeTempItem(stationCode, 6, "high"));
		hw.setTemp6hAve(findService.findLastTimeTempItem(stationCode, 6, "ave"));
		hw.setTemp6hLow(findService.findLastTimeTempItem(stationCode, 6, "low"));
		hw.setTemp12hHigh(findService.findLastTimeTempItem(stationCode,12, "high"));
		hw.setTemp12hAve(findService.findLastTimeTempItem(stationCode, 12, "ave"));
		hw.setTemp12hLow(findService.findLastTimeTempItem(stationCode, 12, "low"));
		hw.setHumiRe(weather.getInt("RHU"));//humidity  	
		hw.setHumiReMin(weather.getInt("RHU_Min"));//RHU_Min
		hw.setRainfall_1h(Float.parseFloat(weather.getString("PRE_1h")));//Rainfall_1h
		hw.setRainfall_3h(Float.parseFloat(weather.getString("PRE_3h")));//
		hw.setRainfall_6h(Float.parseFloat(weather.getString("PRE_6h")));//
		hw.setRainfall_12h(Float.parseFloat(weather.getString("PRE_12h")));//
		hw.setRainfall_24h(Float.parseFloat(weather.getString("PRE_24h")));//
		hw.setStationCode(stationCode);
		String[] nameAndAreaCode=findService.findStationNameAndAreaCodeByStationCode(stationCode);
		hw.setStationName(nameAndAreaCode[0]);
		hw.setAreaCode(nameAndAreaCode[1]);
		if(nameAndAreaCode[1]!=null){
			if(isExit){//存在则修改，不存在则新建 
				try{
				   alterService.alterHourWeather(hw);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				try{
				  addService.addHourWeather(hw);
				}catch(Exception e){
					//e.printStackTrace();
				}
			}      
		}	
    }
    /***对比预警策略，产生预警信息***/
    private void checkWarnStrategy(){
    	class CheckStrategy extends Thread{
    		private WarnStrategy strategy;
    		public CheckStrategy(WarnStrategy strategy){
    			this.strategy=strategy;
    		}
    		public void run(){
    			Set<Stations> stations=strategy.getStations();
    			String codes="";
    			for(Stations station:stations){
    				codes+="'"+station.getCode()+"',";
    			}
    			if(codes.endsWith(",")){
    				codes=codes.substring(0, codes.length()-1);
    			}

    			//预警策略有可用监测站时才进行对比
    			if(!codes.isEmpty()){
        			String item=strategy.getItem()+"_"+strategy.getParam();//预警参数
                    List<Map<String,Object>> infos=findService.findStationsLastWeatherInfos(codes, item);//查询预警策略中各监测站的对应天气要素
                    float threshold=strategy.getThreshold();//预警门限值
                    Set<String> warnStationCodes=new HashSet<String>();
                    if(strategy.getItem().equals("low")){//预警参数为最低值，此时如果实测值小于阈值则产生预警信息
                    	for(Map<String,Object> info:infos){
                    		if(Float.parseFloat((String)info.get("value"))<threshold){
                    			warnStationCodes.add((String)info.get("stationCode"));
                    		}
                    	}
                    }
                    else{//预警参数为均值或最大值，或雨量等信息，超过阈值时产生报警
                    	for(Map<String,Object> info:infos){
                    		if((Float) info.get("value")>threshold){
                    			warnStationCodes.add((String)info.get("stationCode"));
                    		}
                    	}
                    }
                    //存在告警且当前小时和当前预警策略尚未同步的情况下才进行预警处理，避免因为同步而造成重复告警
                    if(!warnStationCodes.isEmpty()&&findService.findWarnByDateAndStrategyId(DateUtil.getCurrentHourDate(),strategy.getId())==null){//产生告警信息
                    	StringBuffer warnInfo=new StringBuffer(DateUtil.getCurrentTime());
                    	warnInfo.append(",");
                    	Set<Stations> warnStations=new HashSet<Stations>();
                    	for(String stationCode:warnStationCodes){
                    		Stations station=findService.findStationByStationCode(stationCode);
                    		warnInfo.append(station.getName()+"、");
                        	warnStations.add(station);
                    	}
                    	if(warnInfo.toString().endsWith("、")){
                    		warnInfo=new StringBuffer(warnInfo.substring(0, warnInfo.length()-1));
                    	}
                    	String itm=strategy.getItem();
        				String param=strategy.getParam();
        				switch(itm+"_"+param){
        					case"rainfall_1h":
        						param="一小时降雨量";
        					break;
        					case"rainfall_3h":
        						param="三小时降雨量";
        					break;
        					case"rainfall_6h":
        						param="六小时降雨量";
        					break;
        					case"rainfall_12h":
        						param="十二小时降雨量";
        					break;
        					case"rainfall_24h":
        						param="二十四小时降雨量";
        					break;
        					case"temp_low":
        						param="最低温度";
        					break;
        					case"temp_ave":
        						param="平均温度";
        					break;
        					case"temp_high":
        						param="最高温度";
        					break;
        					default:
        						param="其他";
        					break;	
        				}
                    	warnInfo.append(param);
                    	warnInfo.append("已超过阈值");
                    	warnInfo.append(strategy.getThreshold());
                    	warnInfo.append(itm.equals("rainfall")?"mm":"℃");
                    	warnInfo.append(",请密切关注，及时有效处理。");
                		Warn warn=new Warn();
                    	warn.setStrategyId(strategy.getId());
                    	warn.setStations(warnStations);
                    	warn.setSource(strategy.getName());
                    	warn.setTime(DateUtil.getCurrentTime());
                    	warn.setLiaisons(strategy.getLiaisons());
                    	warn.setStatus(false);
                    	warn.setItem(strategy.getItem());
                    	warn.setParam(strategy.getParam());
                    	warn.setTitle(param+"预警");
                    	warn.setInfoWay(strategy.getInfoWay());
                    	warn.setContext(warnInfo.toString());
                    	warn.setDate(DateUtil.getCurrentHourDate());
                    	warnHandle(warn);
                    }
    			}
    		}
    		/****处理告警
    		 * 包括生成告警信息存放到数据库中、发送告警短信、邮件和系统消息
    		 * ***/
    		 private  void warnHandle(Warn warn){
    			 System.out.println("--------------处理告警--------------");
    			try{
        			addService.addWarn(warn);
    			}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				Set<Liaisons>liaisons=warn.getLiaisons();//需要通知的人员
    				String infoWay=warn.getInfoWay();
    				/****
    				 * 发送短信和拨打电话
    				 * **/
    				class SendInfo extends Thread{
    					private Liaisons liaison;
    					private String info;//预警通知方式三个字符分别表示短信、电话、邮件，为1表示采用，0表示不采用
    					public SendInfo(Liaisons liaison,String info){
    						this.liaison=liaison;
    						this.info=info;
    					}
    					public void run(){
    						if(info.charAt(0)=='1'){//是否发送短信
    							MessageUtil.sendSMS(liaison.getPhone(), warn.getContext());
    						}
    						if(info.charAt(1)=='1'){//是否拨打电话
    							MessageUtil.sendVoiceMessage(liaison.getPhone(), warn.getContext(), 2);
    						}
    					}
    				}
    				List<String> emailAddress=new ArrayList<>();
    				ExecutorService exec=Executors.newCachedThreadPool();
       			    System.out.println("--------------拨打电话--------------");
    				for(Liaisons liaison:liaisons){
    					emailAddress.add(liaison.getEmail());
    					exec.execute(new SendInfo(liaison,infoWay));
    				}
    				exec.shutdown();
    				//发送邮件
    				String title=DateUtil.getCurrentTime()+"赤水市气象局预警信息";
    				if(infoWay.charAt(2)=='1'&&!emailAddress.isEmpty()){//是否发送邮件
           			    System.out.println("--------------发送邮件--------------");
						MessageUtil.sendEmail(emailAddress, title, warn.getContext());
					}
    			}
    		}
    	}
    	//获取全部的预警策略
    	List<WarnStrategy> strategies=findService.findAllWarnStrategy();
    	ExecutorService exec=Executors.newCachedThreadPool();//创建线程池
    	for(WarnStrategy strategy:strategies){
    		if(strategy.getStatus()){//如果预警策略已经启动，根据预警策略判定天气信息
        		exec.execute(new CheckStrategy(strategy));//创建执行多个线程
    		}
    	}
    	exec.shutdown();
    }
}
