package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.DateUtil;
import dataServer.util.NumConv;
import domain.Area;
import domain.BasicResource;
import domain.Stations;
import domain.Vulnerable;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;


@Controller
public class ResourceController {
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    @Resource(name="deleteService") 
   	private DeleteService delService;
    /****
     * 获取所有监测站点的信息用于绘制GIS
	 * @return JSONObject 包括两个参数<br/>
	 *   &nbsp;&nbsp; statusCode： 为boolean型，表明状态<br/>
	 *   &nbsp;&nbsp;stations:JSONArray，各监测站点，为每个元素为一个JSONObject，包含以下字段：<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; name:监测站名称<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; lng：监测站经度<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; lat：监测站纬度<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; rainfall_1h：小时降雨量<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; temp_ave：平均温度<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; humi_re：相对湿度<br/>
     * **/
    @RequestMapping("station/getStationsForGIS")
    public void getStationsForGIS(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	String items=request.getParameter("items");
    	boolean statusCode=false;
    	String areaCode="520381";
    	List<Stations> stations=findService.findAllStations(areaCode);
    	if(stations!=null&&!stations.isEmpty()){
    		statusCode=true;
    		JSONArray stationArray=new JSONArray();
    		for(Stations station:stations){
    			JSONObject stat=new JSONObject();
    			stat.put("name", station.getName());
    			stat.put("lat", station.getLat());
    			stat.put("lng", station.getLng());
    			Timestamp stamp=DateUtil.getDate();
    			Map<String,Object> info=findService.findInfosByStationaCodeAndDate(station.getCode(), stamp, items); 
    			if(info.isEmpty()){
    				//可能数据还未同步，查不到数据时向前推一个小时
    				stamp=new Timestamp(stamp.getTime()-60*60*1000);
    				info=findService.findInfosByStationaCodeAndDate(station.getCode(), stamp, items);
    			}
    			Set<String> it=info.keySet();
    			for(String key:it){
    				stat.put(key, info.get(key));
    			}
    			stationArray.add(stat);
    		}
    		responseData.put("stations", stationArray);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/xml;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /***
	 * 分页获取监测站概要
	 *@return JSONObject 包括以下参数：<br/>
	 *   &nbsp;&nbsp; statusCode： 为boolean型，表明状态<br/>
	 *   &nbsp;&nbsp; err： 为String，当statusCode为false时有效，表明失败原因<br/>
	 *   &nbsp;&nbsp; totalPage： 为int，当statusCode为true时有效，表明总页数<br/>
	 *   &nbsp;&nbsp;stations:JSONArray，当statusCode为true时有效，表示各监测站点信息，每个元素为一个JSONObject，包含以下字段：<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; code：监测站编号<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; name：监测站名称<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; description：监测站描述<br/>
	 * ***/
	@RequestMapping(value="station/getStationsOutline")
	public void getStationsOutline(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		int col=0;   
		int page=0;
		String name=request.getParameter("name");//监测站名称
		name=name==null?"":name;
		try{
			col=Integer.parseInt(request.getParameter("col"));
			page=Integer.parseInt(request.getParameter("page"));
		}catch(Exception e){
		}
		if(col!=0&&page!=0){
			List<Stations> stations=findService.findPaginationStationOutline(col, page,name);
			if(stations!=null&&!stations.isEmpty()){
				long itemCount=findService.getStationItemSize(name);//监测站条目数，用于计算页数
				int pages=(int) Math.ceil(itemCount*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
				statusCode=true;
				JSONArray stationParams=new JSONArray();
				for(Stations station:stations){
					Map<String,Object> para=new LinkedHashMap<String,Object>();
					para.put("code", station.getCode());
					para.put("name", station.getName());
					para.put("description", station.getDescription());//description
					stationParams.add(para);
				}
				responseData.put("stations", stationParams);//packet the station params
			}
		}else{
			err="错误，页码信息无效，请核对后 重发。";
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 获取所有监测站
	 *
	 * ***/
	@RequestMapping(value="station/getAllStation")
	public void getAllStation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		List<Stations> stations=findService.findAllStations("");
		if(stations!=null){
			statusCode=true;
			JSONArray stationParams=new JSONArray();
			for(Stations station:stations){
				Map<String,Object> para=new LinkedHashMap<String,Object>();
				para.put("id", station.getId());
				para.put("area", station.getArea().getName());
				para.put("name", station.getName());
				para.put("latAndLng", station.getLat()+","+station.getLng());//lat and lng
				stationParams.add(para);
			}
			responseData.put("stations", stationParams);//packet the station params
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/**
	 * 查看监测站详情
	 * @param code String 监测站代号
	 * @return JSONObject 包含以下参数<br/>
	 *   &nbsp;&nbsp; statusCode： 为boolean型，表明状态<br/>
	 *   &nbsp;&nbsp; err：String 错误信息<br/>
	 *   &nbsp;&nbsp; station：监测站的详细参数，为JSONObject，包含以下字段：<br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; name:名称 <br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; code:编号 <br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; lat: 纬度<br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; lng: 经度<br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; description:描述<br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; type:类型 <br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; cnty:所在乡镇 <br/> 
	 *   &nbsp;&nbsp;&nbsp;&nbsp; areaName:所在地区 <br/> 
	 *   
	 * */
	@RequestMapping("station/checkStationDetial")
    public void checkStationDetial(HttpServletRequest request,HttpServletResponse response) throws IOException{
		boolean statusCode=false;
		String code=request.getParameter("code");//要修改的监测站的代号
		JSONObject responseData=new JSONObject();
		if(code!=null&&!code.isEmpty()){
			Stations station=findService.findStationByStationCode(code);
			if(station==null){
				responseData.put("err", "根据您下发的监测站代号，未找到对应的监测站点，请核对后重试.");
			}else{
				statusCode=true;
				JSONObject info=new JSONObject();
				info.put("name", station.getName());
				info.put("lat", station.getLat());
				info.put("lng", station.getLng());
				info.put("cnty", station.getCnty());
				info.put("description", station.getDescription());
				info.put("code", station.getCode());
				info.put("type", station.getType());
				info.put("areaName", station.getArea().getName());
				responseData.put("station", info);
		  }
		}else{
			responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 新增监测站
	 * @param station JSONObject 要新增监测站的字段及其value
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="station/addStation")
	public void addStation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		JSONObject params=null;
		try {
			params=JSONObject.fromObject(request.getParameter("station"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(params!=null){
			Stations station=new Stations();
			//read & set parameters for the new station
			try {
				station.setCode(params.getString("code"));
				station.setDescription(params.getString("description"));
				station.setLat(params.getString("lat"));
				station.setLng(params.getString("lng"));
				station.setName(params.getString("name"));
				station.setType(params.getString("type"));
				station.setCnty(params.getString("cnty"));
				String areaCode=params.getString("areaCode");
				Area area=findService.findAreaByCode(areaCode);
				station.setArea(area);
				station.setAreaCode(area.getCode());
				Serializable id=addService.addStation(station);
				if(id!=null){
					statusCode=true;
				}
				else{
					responseData.put("err", "数据库操作异常，请核对您输入的监测站代号是否与其他监测站有冲突。");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
				e.printStackTrace();
			}
		}else{
			responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 修改监测站
	 * @param code 要修改的监测站的代号
	 * @param station JSONObject 要修改的字段及其value
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="station/modifyStation")
	public void modifyStation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String code=request.getParameter("code");//要修改的监测站的代号
		JSONObject responseData=new JSONObject();
		JSONObject params=null;
		try {
			params=JSONObject.fromObject(request.getParameter("station"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(code!=null&&params!=null&&!code.isEmpty()){
			Stations station=findService.findStationByStationCode(code);
			if(station==null){
				responseData.put("err", "根据您下发的监测站代号，未找到对应的监测站点，请核对后重试.");
			}
			else{
				//read & set parameters for the new station
				try {
					station.setDescription(params.getString("description"));
					station.setLat(params.getString("lat"));
					station.setLng(params.getString("lng"));
					station.setName(params.getString("name"));
					station.setType(params.getString("type"));
					statusCode=alterService.alterStation(station);
					if(!statusCode){
						responseData.put("err", "数据库操作异常，请核对您下发的参数是否合法。");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
					e.printStackTrace();
				}
			}
			
		}else{
			responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 删除监测站
	 * @param code 要删除的监测站的代号
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="station/delStation")
	public void delStation(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		String code=request.getParameter("code");//要删除的监测站的代号
		JSONObject responseData=new JSONObject();
		if(code!=null&&!code.isEmpty()){
			Stations station=findService.findStationByStationCode(code);
			if(station==null){
				responseData.put("err", "根据您下发的监测站代号，未找到对应的监测站点，请核对后重试.");
			}else{
				statusCode=delService.delStation(station.getId());
				if(!statusCode){
					responseData.put("err", "数据库操作异常，请稍后重试。");
				}
			}
			
		}else{
			responseData.put("err", "下发检测站参数中缺少必要参数信息，请核对参数后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}	
	/***
	 * 新增易受灾点
	 * @param vulner JSON格式的字符串，包含易受灾点的各字段信息
	 * @return JSONObject  包含statusCode  boolean以及err 失败原因
	 * ***/
	@RequestMapping(value="vulner/addVulner")
	public void addVulner(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String err="";
		JSONObject responseData=new JSONObject();
		JSONObject vulnerJson=JSONObject.fromObject(request.getParameter("vulner"));
		if(vulnerJson!=null){
			Vulnerable vulner=new Vulnerable();
			try{
				vulner.setName(vulnerJson.getString("name"));
				vulner.setLat(vulnerJson.getString("lat"));
				vulner.setLng(vulnerJson.getString("lng"));
				vulner.setCnty(vulnerJson.getString("cnty"));
				vulner.setECPName(vulnerJson.getString("ecpName"));
				vulner.setECPEmail(vulnerJson.getString("ecpEmail"));
                vulner.setECPPhone(vulnerJson.getString("ecpPhone"));
                vulner.setEvaRoute(vulnerJson.getString("evaRoute"));
                vulner.setResPop(vulnerJson.getString("resPop"));
                vulner.setType(vulnerJson.getString("type"));
                vulner.setDescription(vulnerJson.getString("description"));
			}catch(Exception e){
				err="下发参数中存在必要信息的缺失项。";
			}finally{
				try{
					Serializable id=addService.addVulnerable(vulner);
					if(id!=null){
						statusCode=true;
					}
				}catch(Exception e){
					err="数据库操作异常，请稍后重试。";
				}
			}
		}
		else{
			err="下发参数中存在必要信息的缺失项。";
		}
		responseData.put("err", err);
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 修改易受灾点
	 * @param vulner JSON格式的字符串，包含易受灾点的各字段信息
	 * @return JSONObject  包含statusCode  boolean以及err 失败原因
	 * ***/
	@RequestMapping(value="vulner/modifyVulner")
	public void modifyVulner(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String err="";
		JSONObject responseData=new JSONObject();
		JSONObject vulnerJson=JSONObject.fromObject(request.getParameter("vulner"));
		Long id=Long.parseLong(request.getParameter("id"));//易受灾点ID
		if(vulnerJson!=null&&id!=null){
			Vulnerable vulner=findService.findVulnerById(id);
			try{
				vulner.setName(vulnerJson.getString("name"));
				vulner.setLat(vulnerJson.getString("lat"));
				vulner.setLng(vulnerJson.getString("lng"));
				vulner.setCnty(vulnerJson.getString("cnty"));
				vulner.setECPName(vulnerJson.getString("ecpName"));
				vulner.setECPEmail(vulnerJson.getString("ecpEmail"));
                vulner.setECPPhone(vulnerJson.getString("ecpPhone"));
                vulner.setEvaRoute(vulnerJson.getString("evaRoute"));
                vulner.setResPop(vulnerJson.getString("resPop"));
                vulner.setType(vulnerJson.getString("type"));
                vulner.setDescription(vulnerJson.getString("description"));
			}catch(Exception e){
				err="下发参数中存在必要信息的缺失项。";
			}finally{

				statusCode=alterService.alterVulnerable(vulner);
			}
		}
		else{
			err="下发参数中存在必要信息的缺失项。";
		}
		responseData.put("err", err);
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 删除易受灾点
	 * @param id 要删除的易受灾点的id
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="vulner/delVulner")
	public void delVulner(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		Long id=Long.parseLong(request.getParameter("id"));
		JSONObject responseData=new JSONObject();
		statusCode=delService.delVulnarable(id);
		if(!statusCode){
			responseData.put("err", "数据库操作异常，请稍后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 查看易受灾点详情
	 * @param id 要查看详情的易受灾点的id
	 * @return JSONObject 包含三个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:String 失败原因 <br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp;vulner:JSONObject 易受灾点的详细信息
	 * ***/
	@RequestMapping(value="	vulner/checkDetail")
	public void checkDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		Long id=Long.parseLong(request.getParameter("id"));
		JSONObject responseData=new JSONObject();
		Vulnerable vul=findService.findVulnerById(id);
		if(vul!=null){
			statusCode=true;
			JSONObject vulner=new JSONObject();
			/***封装易受灾点信息，具体信息由前端下发***/
			vulner.put("id", vul.getId());//易受灾地ID
			vulner.put("name", vul.getName());//易受灾地名称
			vulner.put("cnty", vul.getCnty());//乡镇
			vulner.put("lat",vul.getLat());//纬度
			vulner.put("lng", vul.getLng());//经度
			vulner.put("ecpName", vul.getECPName());//联系人
			vulner.put("ecpPhone", vul.getECPPhone());//联系人手机号
			vulner.put("ecpEmail",vul.getECPEmail());//紧急联系人邮件
			vulner.put("resPop", vul.getResPop());//常住人口
			vulner.put("type", vul.getType());//易受灾类型
			vulner.put("evaRoute", vul.getEvaRoute());//疏散路线
			vulner.put("description", vul.getDescription());//描述
			responseData.put("vulner",vulner);
		}else{
			responseData.put("err", "数据库操作异常，请稍后重试！");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}

	/***
	 * 获取所有易受灾点
	 * @return  JSONObject 包含 statusCode和vulners
	 * ***/
	@RequestMapping(value="vulner/getAllVulners")
	public void getAllVulners(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		List<Vulnerable> vulnerable=findService.findAllVulnerable();
		if(vulnerable!=null&&(!vulnerable.isEmpty())){//使用短路与进行判定
			statusCode=true;
			JSONArray vulners=new JSONArray();//用于存放易受灾点信息
			for(Vulnerable vul:vulnerable){
				JSONObject vulner=new JSONObject();
				/***封装易受灾点信息，具体信息由前端下发***/
				vulner.put("id", vul.getId());//易受灾地ID
				vulner.put("name", vul.getName());//易受灾地名称
				vulner.put("cnty", vul.getCnty());//乡镇
				vulner.put("lat",vul.getLat());//纬度
				vulner.put("lng", vul.getLng());//经度
				vulner.put("ecpName", vul.getECPName());//联系人
				vulner.put("ecpPhone", vul.getECPPhone());//联系人手机号
				vulner.put("ecpEmail",vul.getECPEmail());//紧急联系人邮件
				vulner.put("resPop", vul.getResPop());//常住人口
				vulner.put("type", vul.getType());//易受灾类型
				vulner.put("evaRoute", vul.getEvaRoute());//疏散路线
				vulner.put("description",vul.getDescription());
				vulners.add(vulner);
			}
			responseData.put("vulners", vulners);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(JSONObject.fromObject(responseData));
		out.flush();
		out.close();
	}
	/***
     * 通过标题、表格列数以及当前页码分页查找易受灾点信息
     * @param title String :易受灾点标题，空字符串表示全部
     * @param col int:表格列数
     * @param page int:当前页码
     * @return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
     *   totalPage:int 当statusCode为true时有效，表示符合条件的易受灾点的页码数<br/>
     *   vulners：JSONArray，当statusCode为true时有效，表示符合条件的基信记录，每一个元素为一个JSONObject，包含以下字段：<br/>
     *   &nbsp;&nbsp;&nbsp;&nbsp; id：long，易受灾点标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,易受灾端标题
     *   &nbsp;&nbsp;&nbsp;&nbsp; description： String易受灾点描述
     * **/
	 @RequestMapping("vulner/getPaginationVulner")
	 public void getPaginationVulner(HttpServletRequest request,HttpServletResponse response) throws IOException{
	    JSONObject responseData=new JSONObject();
	    boolean statusCode=false;
	    String err="";
		try {
			String title=request.getParameter("title");	
			title=(title==null)?"":title;
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<Vulnerable> vulners=findService.findPaginationVulners(title, col, page);
				if(vulners!=null&&!vulners.isEmpty()){
					long totalCount=findService.getVulnersCount(title);
					int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
					pages=(pages==0)?1:pages;
					responseData.put("totalPage", pages);
					statusCode=true;
					JSONArray vulnerInfos=new JSONArray();
					for(Vulnerable vulner:vulners){
						JSONObject vulnerInfo=new JSONObject();
						vulnerInfo.put("title", vulner.getName());
						vulnerInfo.put("description", vulner.getDescription());
						vulnerInfo.put("id", vulner.getId());
						vulnerInfos.add(vulnerInfo);
					}
					responseData.put("vulners", vulnerInfos);
				}
				else{
					err="当前数据库中不存在可用基础信息，您可以新增基础信息条目。";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				err="下发请求中缺少必要参数项，请核对后重试。";
			}
	    	if(!statusCode){
	    		responseData.put("err", err);
	    	}
	    	responseData.put("statusCode", statusCode);
	    	response.setContentType("text/xml;charset=utf-8");
	    	PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
	 }
	/*******基础信息相关********/
	/***
	 * 新增基础信息
	 * @param basis JSON格式的字符串，包含易受灾点的各字段信息，含有以下字段：<br/>
	 *   title String:基信标题<br/>
	 *   context String:基信内容<br/>
	 *   description String:描述<br/>
	 * @return JSONObject  包含以下信息：<br/>:
	 *   statusCode boolean:表示执行结果<br/>
	 *   err String:当statusCode为false时有效，表示失败原因
	 * ***/
	@RequestMapping(value="basis/addBasis")
	public void addBasis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String err="";
		JSONObject responseData=new JSONObject();
		JSONObject basic=JSONObject.fromObject(request.getParameter("basis"));
		if(basic!=null){
			BasicResource basis=new BasicResource();
			try{
				basis.setTitle(basic.getString("title"));
				String context=basic.getString("context");
				context=context.substring(1, context.length()-1);
				String[] byteStr=context.split(",");
				byte[] bytes=NumConv.stringArryTobyteArry(byteStr);
				basis.setContext(new String(bytes));
				basis.setDescription(basic.getString("description"));
			}catch(Exception e){
				err="下发参数中存在必要信息的缺失项。";
			}finally{
				try{
					Serializable id=addService.addBasicResource(basis);
					if(id!=null){
						statusCode=true;
					}
				}catch(Exception e){
					err="数据库操作异常，请稍后重试。";
				}
			}
		}
		else{
			err="下发参数中存在必要信息的缺失项。";
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	 /***
	 * 修改基础信息
	 * @param id long:要修改的基信的标识
	 * @param basis JSON格式的字符串，包含易受灾点的各字段信息，含有以下字段：<br/>
	 *   title String:基信标题<br/>
	 *   context String:基信内容<br/>
	 *   description String:描述<br/>
	 * @return JSONObject  包含以下信息：<br/>:
	 *   statusCode boolean:表示执行结果<br/>
	 *   err String:当statusCode为false时有效，表示失败原因
	 * ***/
	@RequestMapping(value="basis/modifyBasis")
	public void modifyBasis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String err="";
		JSONObject responseData=new JSONObject();
		JSONObject basic=null;
		Long id=null;
		try{
			basic=JSONObject.fromObject(request.getParameter("basis"));
			id=Long.parseLong(request.getParameter("id"));//
		}catch(Exception e){
			err="下发参数中存在必要信息的缺失项\"basis\",请核对后重发。";
		}
		if(basic!=null&&id!=null){
			BasicResource basis=findService.findBasicResourceById(id);
			if(basis!=null){
				try{
					basis.setTitle(basic.getString("title"));
					String context=basic.getString("context");
					context=context.substring(1, context.length()-1);
					String[] byteStr=context.split(",");
					byte[] bytes=NumConv.stringArryTobyteArry(byteStr);
					basis.setContext(new String(bytes));
					basis.setDescription(basic.getString("description"));
				}catch(Exception e){
					e.printStackTrace();
					err="下发参数中存在必要信息的缺失项。";
				}
				finally{
					statusCode=alterService.alterBasicResource(basis);
					if(!statusCode){
						err="数据库操作异常，请稍后重试。";
					}
				}
			}else{
				err="根据下发的标识找不到对应的基础信息记录，请稍后重试。";
			}
		}
		else{
			err="下发参数中存在必要信息的缺失项。";
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 删除基础信息
	 * @param id 要删除的基础信息的id
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="basis/delBasis")
	public void delBasis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		Long id=Long.parseLong(request.getParameter("id"));
		JSONObject responseData=new JSONObject();
		statusCode=delService.delBasicResource(id);
		if(!statusCode){
			responseData.put("err", "数据库操作异常，请稍后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
     * 通过基信标题、表格列数以及当前页码分页查找基础信息
     * @param title String :基信标题，空字符串表示全部
     * @param col int:表格列数
     * @param page int:当前页码
     * @return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
     *   totalPage:int 当statusCode为true时有效，表示符合条件的基信的页码数<br/>
     *   basis：JSONArray，当statusCode为true时有效，表示符合条件的基信记录，每一个元素为一个JSONObject，包含以下字段：<br/>
     *   &nbsp;&nbsp;&nbsp;&nbsp; id：long，基信标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,基信标题
     *   &nbsp;&nbsp;&nbsp;&nbsp; description： String基信描述
     * **/
	 @RequestMapping("basis/getPaginationBasis")
	 public void getPaginationBasis(HttpServletRequest request,HttpServletResponse response) throws IOException{
	    JSONObject responseData=new JSONObject();
	    boolean statusCode=false;
	    String err="";
		try {
			String title=request.getParameter("title");	
			title=(title==null)?"":title;
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<BasicResource> basis=findService.findPaginationBasicResources(title, col, page);
				if(basis!=null&&!basis.isEmpty()){
					long totalCount=findService.getBasicResourceCount(title);
					int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
					pages=(pages==0)?1:pages;
					responseData.put("totalPage", pages);
					statusCode=true;
					JSONArray basisInfos=new JSONArray();
					for(BasicResource basic:basis){
						JSONObject basicInfo=new JSONObject();
						basicInfo.put("title", basic.getTitle());
						basicInfo.put("description", basic.getDescription());
						basicInfo.put("id", basic.getId());
						basisInfos.add(basicInfo);
					}
					responseData.put("basis", basisInfos);
				}
				else{
					err="当前数据库中不存在可用基础信息，您可以新增基础信息条目。";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				err="下发请求中缺少必要参数项，请核对后重试。";
			}
	    	if(!statusCode){
	    		responseData.put("err", err);
	    	}
	    	responseData.put("statusCode", statusCode);
	    	response.setContentType("text/xml;charset=utf-8");
	    	PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
	 }
	 /***
	     * 通过基信标识获取基信详情
	     * @param id long:基信标识
	     * @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	     *   basis JSONObject，当statusCode为true时有效，表示当前基础信息的详细信息，包含以下字段：<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String，基信标题<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; context：String，基信内容</br>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; description：String，基信描述</br>
	     * **/	
	 @RequestMapping("basis/getBasisDetial")
	 public void getBasisDetial(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		String err="";
		Long id=null;
		JSONObject responseData=new JSONObject();
		try{
			id=Long.parseLong(request.getParameter("id"));
		}catch(Exception e){
			id=null;
			err="下发请求中缺少必要的参数项，请核对后重试。";
		} 
		if(id!=null){
			BasicResource basic=findService.findBasicResourceById(id);
			if(basic!=null){
				statusCode=true;
				JSONObject basis=new JSONObject();
				basis.put("title", basic.getTitle());
				basis.put("context",basic.getContext());
				basis.put("description",basic.getDescription());
				responseData.put("basis", basis);
			}else{
				err="根据下发的标识在数据库中找不到对应条目，请核对后重试。";
			}
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
}
