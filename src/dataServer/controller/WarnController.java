package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.DateUtil;
import domain.LiaisonUnit;
import domain.Liaisons;
import domain.Stations;
import domain.Warn;
import domain.WarnStrategy;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;

/***
 * 预警相关controller
 * **/
@Controller
public class WarnController {
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    @Resource(name="deleteService") 
   	private DeleteService delService;
    /***
	   * 新增预警策略时获取所有监测站信息
	   * @return JSONObject包含:<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   statusCode： 为boolean型，表明状态<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   err：String 当statusCode为false时有效，表明失败原因<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   stations:当statusCode为true时有效，表示各监测站的信息。为JSONArray，每个元素为一个JSONObject，包含以下字段：<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; code:站点编号<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; name:站点名称<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; areaCode:区域编号  
	   * ***/
    @RequestMapping(value="warn/getStationInfo")
    public void getStationInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		List<Stations>stations=findService.findAllStations("");
		if(stations!=null&&!stations.isEmpty()){
			statusCode=true;
			JSONArray stationInfos=new JSONArray();
			for(Stations station:stations){
				JSONObject info=new JSONObject();
				info.put("code", station.getCode());
				info.put("name", station.getName());
				info.put("areaCode", station.getAreaCode());
				stationInfos.add(info);
			}
			responseData.put("stations", stationInfos);
		}else{
			responseData.put("err", "数据库操作异常，请稍后重试。");
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
	   * 新增预警策略时获取所有人员和人员组信息
	   * @return JSONObject包含:<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   statusCode： 为boolean型，表明状态<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   err：String 当statusCode为false时有效，表明失败原因<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   liaisonUnits:当statusCode为true时有效，表示各人员组的信息。为JSONArray，每个元素为一个JSONObject，包含以下字段：<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; name:组别名称<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; id:组别标识  
	   *   &nbsp;&nbsp;&nbsp;&nbsp;   liaisons:当statusCode为true时有效，表示人员的信息。为JSONArray，每个元素为一个JSONObject，包含以下字段：<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; name:人员名称<br/>
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; id:人员标识  
	   *   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; unitId:组别标识 
	   * ***/
    @RequestMapping(value="warn/getLiaisonsInfo")
    public void getLiaisonsInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		List<LiaisonUnit> liaisonUnits=findService.findAllLiaisonUnits();
		if(liaisonUnits!=null&&!liaisonUnits.isEmpty()){
			statusCode=true;
			JSONArray liaisonUnitInfos=new JSONArray();
			JSONArray liaisonInfos=new JSONArray();
			for(LiaisonUnit unit:liaisonUnits){
				JSONObject liaisonUnit=new JSONObject();
				liaisonUnit.put("id", unit.getId());
				liaisonUnit.put("name", unit.getName());
				liaisonUnitInfos.add(liaisonUnit);
				Set<Liaisons> lisisons=unit.getLiaisons();
				if(lisisons!=null&&!lisisons.isEmpty()){
					for(Liaisons liaison:lisisons){
						JSONObject liaisonInfo=new JSONObject();
						liaisonInfo.put("id", liaison.getId());
						liaisonInfo.put("name", liaison.getName());
						liaisonInfo.put("unitId", unit.getId());
						liaisonInfos.add(liaisonInfo);
					}	
				}
			}
			responseData.put("liaisonUnits", liaisonUnitInfos);
			responseData.put("liaisons", liaisonInfos);
		}else{
			responseData.put("err", "数据库操作异常，请稍后重试。");
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
     * 增加预警策略
     * @param warnStrategy: JSONObject 包含预警策略的各字段
     * **/
    @RequestMapping(value="warn/addWarnStrategy")
    public void addWarnStrategy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		JSONObject strategy=new JSONObject();
		String err="";
		try{
			strategy=JSONObject.fromObject(request.getParameter("warnStrategy"));
		}catch(Exception e){
			err="下发参数中缺少必要项\"warnStrategy\"，请核对后重试";
			e.printStackTrace();
		}
		if(!strategy.isEmpty()){
			WarnStrategy warnStrategy=new WarnStrategy();
			warnStrategy.setCreateDate(DateUtil.getCurrentDate());
			try{
				warnStrategy.setCreateUser(strategy.getString("createUser"));
				warnStrategy.setItem(strategy.getString("item"));
				warnStrategy.setInfoWay(strategy.getString("infoWay"));
				warnStrategy.setName(strategy.getString("name"));
				warnStrategy.setParam(strategy.getString("param"));
				warnStrategy.setThreshold(Float.parseFloat(strategy.getString("threshold")));
				warnStrategy.setStatus(strategy.getBoolean("status"));
				JSONArray liaisonIds=strategy.getJSONArray("liaisons");
				JSONArray stationIds=strategy.getJSONArray("stations");
				Set<Liaisons>liaisons=new HashSet<>();
				Set<Stations>stations=new HashSet<>();
				for(int i=0;i<liaisonIds.size();i++){
					Liaisons liaison=findService.findLiaisonById(liaisonIds.getLong(i));
					if(liaison!=null){
						liaisons.add(liaison);
					}
				}
				for(int i=0;i<stationIds.size();i++){
					Stations station=findService.findStationByStationCode(stationIds.getString(i));
					if(station!=null){
						stations.add(station);
					}
				}
				warnStrategy.setLiaisons(liaisons);
				warnStrategy.setStations(stations);
				Serializable id=addService.addWarnStrategy(warnStrategy);
				if(id!=null){
					statusCode=true;
				}else{
					err="数据库操作异常，请稍后重试。";
				}
			}catch(Exception e){
				e.printStackTrace();
				err="下发参数中缺少必要项，请核对后重发.";
			}
		}
		responseData.put("err", err);
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***
     * 修改预警策略
     * @param id:要修改的预警策略的ID
     * @param warnStrategy: JSONObject 包含预警策略的各字段
     * **/
    @RequestMapping(value="warn/alterWarnStrategy")
    public void alterWarnStrategy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		Long id=null;
		try{
			id=Long.parseLong(request.getParameter("id"));
		}catch(Exception e){
			err="下发参数中缺少必要项\"id\"，请核对后重试";
		}
		JSONObject strategy=new JSONObject();
		if(id!=null){
			WarnStrategy warnStrategy=findService.findWarnStrategyById(id);
			if(warnStrategy!=null){
				try{
					strategy=JSONObject.fromObject(request.getParameter("warnStrategy"));
				}catch(Exception e){
					err="下发参数中缺少必要项\"warnStrategy\"，请核对后重试";
					e.printStackTrace();
				}
				if(!strategy.isEmpty()){
					try{
						warnStrategy.setItem(strategy.getString("item"));
						warnStrategy.setName(strategy.getString("name"));
						warnStrategy.setInfoWay(strategy.getString("infoWay"));
						warnStrategy.setParam(strategy.getString("param"));
						warnStrategy.setThreshold(Float.parseFloat(strategy.getString("threshold")));
						warnStrategy.setStatus(strategy.getBoolean("status"));
						JSONArray liaisonIds=strategy.getJSONArray("liaisons");
						JSONArray stationIds=strategy.getJSONArray("stations");
						Set<Liaisons>liaisons=new HashSet<>();
						Set<Stations>stations=new HashSet<>();
						for(int i=0;i<liaisonIds.size();i++){
							Liaisons liaison=findService.findLiaisonById(liaisonIds.getLong(i));
							if(liaison!=null){
								liaisons.add(liaison);
							}
						}
						for(int i=0;i<stationIds.size();i++){
							Stations station=findService.findStationByStationCode(stationIds.getString(i));
							if(station!=null){
								stations.add(station);
							}
						}
						warnStrategy.setLiaisons(liaisons);
						warnStrategy.setStations(stations);
						statusCode=alterService.alterWarnStrategy(warnStrategy);
						if(!statusCode){
							err="数据库操作异常，请稍后重试。";
						}
					}catch(Exception e){
						e.printStackTrace();
						err="下发参数中缺少必要项，请核对后重发。";
					}
				}
			}
		}
		responseData.put("err", err);
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***
     * 删除预警策略
     * @param id:要删除的预警策略的ID
     * **/
    @RequestMapping(value="warn/delWarnStrategy")
    public void delWarnStrategy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		Long id=null;
		try{
			id=Long.parseLong(request.getParameter("id"));
		}catch(Exception e){
			err="下发参数中缺少必要项\"id\"，请核对后重试";
		}
		if(id!=null){
			statusCode=delService.delWarnStrategy(id);
			if(!statusCode){
				err="数据库操作异常";
			}
		}
		responseData.put("err", err);
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***
	 *分页获取预警策略
	 *@param page :int 当前页数
	 *@param col :int 表格的行数 
	 * @return JSONObject 包括
	 *     pages:总页数
	 *     statusCode:boolean 标识查新状态
	 *     warnStrategy：JSONArray 每个元素为一条预警策略的各参数信息 
	 * ***/
	@RequestMapping(value="warn/getPaginationWarnStrategy")
	public void getPaginationWarnStrategy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		int currentPage=Integer.parseInt(request.getParameter("page"));//获取当前页码数
		int col=Integer.parseInt(request.getParameter("col"));//表格列数
		String name=request.getParameter("name");//策略名称
   		name=(name==null)?"":name;
		long totalCount=findService.getWarnStrategyAmount(name);
		int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
		pages=(pages==0)?1:pages;
		List<WarnStrategy>WarnStrategy=findService.findPaginationWarnStrategy(col,currentPage,name);
		if(WarnStrategy!=null&&(!WarnStrategy.isEmpty())){
			statusCode=true;
			responseData.put("pages", pages);
			JSONArray warnStrategy=new JSONArray();
			for(WarnStrategy strategy:WarnStrategy){
				JSONObject para=new JSONObject();
				para.put("id", strategy.getId());
				para.put("infoWay",strategy.getInfoWay());
				para.put("name",strategy.getName());
				String item=strategy.getItem();
				String param=strategy.getParam();
				switch(item+"_"+param){
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
				item=item.equals("rainfall")?"降雨量":"温度";
				para.put("item",item);
				para.put("param",param);
				para.put("threshold",strategy.getThreshold());
				para.put("createUser", strategy.getCreateUser());
				para.put("createDate", strategy.getCreateDate());
				String status=strategy.getStatus()?"已启用":"未启用";
				para.put("status", status);
				warnStrategy.add(para);
			}
			responseData.put("warnStrategy", warnStrategy);//packet the station params
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
   	 *查看预警策略详情
   	 * ***/
   	@RequestMapping(value="warn/getWarnStrategyDetial")
   	public void getWarnStrategyDetial(HttpServletRequest request, HttpServletResponse response) throws IOException {
   		boolean statusCode=false;
   		JSONObject responseData=new JSONObject();
   		Long id=null;
   		try{
   			id=Long.parseLong(request.getParameter("id"));
   			WarnStrategy strategy=findService.findWarnStrategyById(id);
   			if(strategy!=null){
   				statusCode=true;
   				JSONObject para=new JSONObject();
				para.put("id", strategy.getId());
				para.put("name",strategy.getName());
				para.put("item",strategy.getItem());
				para.put("param",strategy.getParam());
				para.put("infoWay",strategy.getInfoWay());
				para.put("threshold",strategy.getThreshold());
				para.put("createUser", strategy.getCreateUser());
				para.put("createDate", strategy.getCreateDate());
				String status=strategy.getStatus()?"已启用":"未启用";
				para.put("status", status);
				List<Long> liaisonIds=new ArrayList<>();
				List<String> stationCodes=new ArrayList<>();
				Set<Liaisons> liaisons=strategy.getLiaisons();
				Set<Stations> stations=strategy.getStations();
				if(liaisons!=null){
					for(Liaisons liaison:liaisons){
						liaisonIds.add(liaison.getId());
					}
				}
				if(stations!=null){
					for(Stations station:stations){
						stationCodes.add(station.getCode());
					}
				}
				para.put("liaisons", liaisonIds);
				para.put("stations", stationCodes);
				responseData.put("warnStrategy", para);
   			}
   		}catch(Exception e){
   			e.printStackTrace();
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
     * 通过名称模糊查询预警策略
     * @param name:策略名称
     * **/
    @RequestMapping(value="warn/getWarnStrategyByName")
    public void getWarnStrategyByName(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String name=request.getParameter("name");
		List<WarnStrategy>WarnStrategy=findService.findWarnStrategyByName(name);
		if(WarnStrategy!=null&&(!WarnStrategy.isEmpty())){
			statusCode=true;
			JSONArray warnStrategy=new JSONArray();
			for(WarnStrategy strategy:WarnStrategy){
				JSONObject para=new JSONObject();
				para.put("id", strategy.getId());
				para.put("name",strategy.getName());
				String item=strategy.getItem();
				String param=strategy.getParam();
				switch(item+"_"+param){
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
				item=item.equals("rainfall")?"降雨量":"温度";
				para.put("item",item);
				para.put("param",param);
				para.put("threshold",strategy.getThreshold());
				para.put("createUser", strategy.getCreateUser());
				para.put("createDate", strategy.getCreateDate());
				String status=strategy.getStatus()?"已启用":"未启用";
				para.put("status", status);
				warnStrategy.add(para);
			}
			responseData.put("warnStrategy", warnStrategy);//packet the station params
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    /******预警相关********/
    
    /***
     * 通过告警处理状态、表格列数已经当前页码分页查找预警信息
     * @param status Boolean :预警状态,为true表示已解除，false表示未解除，null表示全部
     * @param col int:表格列数
     * @param page int:当前页码
     * @return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
     *   totalPage:int 当statusCode为true时有效，表示符合条件的告警信息的页码数<br/>
     *   warns：JSONArray，当statusCode为true时有效，表示符合条件的告警记录，每一个元素为一个JSONObject，包含以下字段：<br/>
     *   &nbsp;&nbsp;&nbsp;&nbsp; id：long，告警标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,告警标题
     *   &nbsp;&nbsp;&nbsp;&nbsp; status：String，告警状态，已解除或未解除
     *   &nbsp;&nbsp;&nbsp;&nbsp; source：String，告警来源
     *   &nbsp;&nbsp;&nbsp;&nbsp; time：String，告警时间
     * **/
    @RequestMapping(value="warn/getPaginationWarns")
    public void getPaginationWarns(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		try{
			Boolean status=null;
			if(!request.getParameter("status").equals("null")){
				status=Boolean.parseBoolean(request.getParameter("status"));
			}
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<Warn> warns=findService.findPaginationWarns(status, col, page);
			if(warns!=null&&(!warns.isEmpty())){
				statusCode=true;
				long count=findService.getWarnCount(status);
				int pages=(int) Math.ceil(count*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
	            JSONArray warnInfos=new JSONArray();
				for(Warn warn:warns){
					JSONObject warnInfo=new JSONObject();
					warnInfo.put("id", warn.getId());
					warnInfo.put("title", warn.getTitle());
					String isHandle=warn.getStatus()?"已解除":"未解除";
					warnInfo.put("status", isHandle);
					warnInfo.put("source", warn.getSource());
					warnInfo.put("time", warn.getTime());
					warnInfos.add(warnInfo);
				}
				responseData.put("warns", warnInfos);
			}
			else{
				err="未找到符合条件的条目。";
			}
		}catch(Exception e){
			err="缺少必要参数项，请确认后重试。";
		}
		if(!statusCode){
			responseData.put("err", err);
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
     * 通过告警标识获取告警详情
     * @param id long:告警标识
     * @return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
     *   totalPage:int 当statusCode为true时有效，表示符合条件的告警信息的页码数<br/>
     *   JSONObject，当statusCode为true时有效，表示当前告警的详细信息，包含以下字段：<br/>
     *   &nbsp;&nbsp;&nbsp;&nbsp; time：String，告警时间
     *   &nbsp;&nbsp;&nbsp;&nbsp; type：String，告警类别，rainfall或temp
     *   &nbsp;&nbsp;&nbsp;&nbsp; param：String，告警参数 1h、3h、6h、12h、24h、low、ave、high
     *   &nbsp;&nbsp;&nbsp;&nbsp; infoWay：String,告警方式  由0、1组成的长度为3的字符串，分别表示短信、电话、邮件，为1表示采用，0表示不采用
     *   &nbsp;&nbsp;&nbsp;&nbsp; context：String,告警详细内容 
     *   &nbsp;&nbsp;&nbsp;&nbsp; stations：Long[],发生告警的各监测站的标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; liaisons：Long[],发送告警通知的各人员标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,告警标题  
     *   &nbsp;&nbsp;&nbsp;&nbsp; status：String，告警状态，已解除或未解除
     *   &nbsp;&nbsp;&nbsp;&nbsp; source：String，告警来源
     * **/
    @RequestMapping(value="warn/getWarnDetial")
    public void getWarnDetial(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		try{
			Long id=Long.parseLong(request.getParameter("id"));
			Warn warn=findService.findWarnById(id);
			if(warn!=null){
				statusCode=true;
				JSONObject warnInfo=new JSONObject();
				warnInfo.put("title", warn.getTitle());
				warnInfo.put("type", warn.getItem());
				warnInfo.put("param", warn.getParam());
				String infoWay=warn.getInfoWay().equals(null)?"000":warn.getInfoWay();
				warnInfo.put("infoWay", infoWay);
				warnInfo.put("time", warn.getTime());
				warnInfo.put("context", warn.getContext());
				Set<Stations>stations=warn.getStations();
				List<String>stationCodes=new ArrayList<String>();
				if(stations!=null&&!stations.isEmpty()){
					for(Stations station:stations){
						stationCodes.add(station.getCode());
					}
				}
				Set<Liaisons>liaisons=warn.getLiaisons();
				List<Long>liaisonIds=new ArrayList<Long>();
				if(liaisons!=null&&!liaisons.isEmpty()){
					for(Liaisons liaison:liaisons){
						liaisonIds.add(liaison.getId());
					}
				}
				warnInfo.put("stations",stationCodes);
				warnInfo.put("liaisons",liaisonIds);
				String isHandle=warn.getStatus()?"已解除":"未解除";
				warnInfo.put("status", isHandle);
				warnInfo.put("source", warn.getSource());
				responseData.put("warn", warnInfo);
			}
			else{
				err="未找到符合条件的条目。";
			}
		}catch(Exception e){
			err="缺少必要参数项，请确认后重试。";
		}
		if(!statusCode){
			responseData.put("err", err);
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
     * 删除告警
     *@param id long:告警标识
     *@return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示删除失败；true表示删除成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/> 
     * ***/
    @RequestMapping(value="warn/delWarn")
    public void delWarn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		try{
			Long id=Long.parseLong(request.getParameter("id"));
			statusCode=delService.delWarn(id);
			if(!statusCode){
				err="数据库操作失败，请重试。";
			}
		}catch(Exception e){
			err="缺少必要参数项，请确认后重试。";
			e.printStackTrace();
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	  /**
	   * 发布预警信息
	   * @param warn JSONObject:包含预警详细信息的JSONObject，必须含有以下内容：<br/>
	   *  &nbsp;&nbsp;&nbsp;&nbsp; time：String，告警时间
       *   &nbsp;&nbsp;&nbsp;&nbsp; item：String，告警类别，rainfall或temp
       *   &nbsp;&nbsp;&nbsp;&nbsp; param：String，告警参数 1h、3h、6h、12h、24h、low、ave、high
       *   &nbsp;&nbsp;&nbsp;&nbsp; infoWay：String,告警方式  由0、1组成的长度为3的字符串，分别表示短信、电话、邮件，为1表示采用，0表示不采用
       *   &nbsp;&nbsp;&nbsp;&nbsp; context：String,告警详细内容 
       *   &nbsp;&nbsp;&nbsp;&nbsp; stations：String[],发生告警的各监测站的code
       *   &nbsp;&nbsp;&nbsp;&nbsp; liaisons：Long[],发送告警通知的各人员标识
       *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,告警标题  
       *   &nbsp;&nbsp;&nbsp;&nbsp; status：String，告警状态，已解除或未解除
       *   &nbsp;&nbsp;&nbsp;&nbsp; source：String，告警来源，发布告警的人员
	   * @return JSONObject 包含以下字段：<br/>
	   *  statusCode: boolean，false表示发布失败；true表示发布成功<br/>
	   *  err:String 当statusCode为false时有效，表示失败的具体原因<br/> 
     * ***/
    @RequestMapping(value="warn/releaseWarn")
    public void releaseWarn(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		JSONObject warnParams=null;
		try {
			warnParams=JSONObject.fromObject(request.getParameter("warn"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(warnParams!=null){
			Warn warn=new Warn();
			//read & set parameters for the new station
			try {
				warn.setTime(DateUtil.getCurrentTime());
				warn.setItem(warnParams.getString("item"));
				warn.setParam(warnParams.getString("param"));
				warn.setInfoWay(warnParams.getString("infoWay"));
				warn.setContext(warnParams.getString("context"));
				warn.setTitle(warnParams.getString("title"));
				warn.setSource(warnParams.getString("source"));
				warn.setStatus(warnParams.getBoolean("status"));
				JSONArray stationCodes=warnParams.getJSONArray("stations");
				JSONArray liaisonIds=warnParams.getJSONArray("liaisons");
				Set<Stations> stations=new HashSet<Stations>();
				Set<Liaisons> liaisons=new HashSet<Liaisons>();
				for(int i=0;i<stationCodes.size();i++){
					Stations station=findService.findStationByStationCode(stationCodes.getString(i));
					if(station!=null){
						stations.add(station);
					}
				}
				for(int i=0;i<liaisonIds.size();i++){
					Liaisons liaison=findService.findLiaisonById(liaisonIds.getLong(i));
					if(liaison!=null){
						liaisons.add(liaison);
					}
				}
				warn.setLiaisons(liaisons);
				warn.setStations(stations);
				Serializable id=addService.addWarn(warn);
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
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }
    /***
     * 解除告警
     *@param id long:告警标识
     *@return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示解除失败；true表示解除成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/> 
     * ***/
    @RequestMapping(value="warn/relieveWarn")
    public void relieveWarn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		try{
			Long id=Long.parseLong(request.getParameter("id"));
			Warn warn=findService.findWarnById(id);
			if(warn!=null){
				warn.setStatus(true);
				statusCode=alterService.alterWarn(warn);
				if(!statusCode){
					err="数据库操作失败，请重试。";
				}
			}else{
				err="查询指定对象失败，请重试。";
			}
		}catch(Exception e){
			err="缺少必要参数项，请确认后重试。";
			e.printStackTrace();
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    /***通过文字获取语音数据，通过请求转发的方式调用百度语音API***/
    @RequestMapping(value="warn/getWarnAudio")
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException{
    	String context=request.getParameter("context");
    	//接收到的中文参数已经经过utf-8编码
    	String params="idx=1&tex="+context+"&cuid=baidu_speech_demo&cod=2&lan=zh&ctp=1&pdt=1&spd=5&per=0&vol=10&pit=5"; 
  		try {
  			URI uri = new URI("http","tts.baidu.com","/text2audio",params,"");
			URL url=uri.toURL();
			response.sendRedirect(url.toString());
		  	     
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
