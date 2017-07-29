package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.DateUtil;
import domain.LiaisonUnit;
import domain.Liaisons;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;

@Controller
public class LiaisonController {
	@Resource(name="findService")
	private FindService findService;
	@Resource(name="addService") 
	private AddService addService;
	@Resource(name="alterService") 
	private AlterService alterService;
	@Resource(name="deleteService") 
	private DeleteService delService;
	
	/****
     * 新增人员组
     * @param unit JSONObject:新增人员组的详细信息，包含以下两个字段：<br/>
     * 	&nbsp;&nbsp; name String:人员组名称<br/>
     *  &nbsp;&nbsp; description String:人员组描述<br/>
     *  &nbsp;&nbsp; createUser String:操作人员<br/>
	 * @return JSONObject 包括两个参数<br/>
	 *   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
	 *   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
     * **/
    @RequestMapping("liaisonUnit/addLiaisonUnit")
    public void addLiaisonUnit(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	JSONObject params=null;
		try {
			params=JSONObject.fromObject(request.getParameter("unit"));	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			err="下发请求中缺少必要的参数项\"unit\"，请核对后重试。";
		}
    	if(params!=null){
    		try{
    			LiaisonUnit unit=new LiaisonUnit();
    			unit.setName(params.getString("name"));
    			unit.setDescription(params.getString("description"));
    			unit.setCreateUser(params.getString("createUser"));
    			unit.setCreateDate(DateUtil.getCurrentTime());
    			Serializable id=addService.addLiaisonUnit(unit);
    			if(id!=null){
    				statusCode=true;
    			}
    			else{
    				err="数据库操作异常，请稍后重试。";
    			}
    		}catch(Exception e){
    			err="下发请求中缺少必要的参数项，请核对后重试。";
    		}
    	}
    	else{
    		err="下发请求中缺少必要的参数项\"unit\"，请核对后重试。";
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /****
     * 分页获取人员组信息
     * @param name String:组别名称，为空字符串时表示所有
     * @param col  int:前端表格列数
     * @param page int:当前页码数
     * @return JSONObject,包含以下字段：<br/>
	 *   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
	 *   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
	 *   &nbsp;&nbsp; totalPage int:总页数<br/>
	 *   &nbsp;&nbsp; units JSONArray:当前页码各人员组条目的详细信息，每个元素为一个JSONObject包括以下字段：<br/>
	 *   &nbsp;&nbsp; &nbsp;&nbsp;name String:组别名称
	 *   &nbsp;&nbsp; &nbsp;&nbsp;id long:组别标识
	 *   &nbsp;&nbsp; &nbsp;&nbsp;description String:组别描述
	 *   &nbsp;&nbsp; &nbsp;&nbsp;createUser String:创建人员
	 *   &nbsp;&nbsp; &nbsp;&nbsp;createDate String:创建时间
     * **/
    @RequestMapping("liaisonUnit/getPaginationUnit")
    public void getPaginationUnit(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
		try {
			String name=request.getParameter("name");	
			name=(name==null)?"":name;
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<LiaisonUnit> liaisonUnits=findService.findPaginationLiaisonUnit(name, col, page);
			if(liaisonUnits!=null&&!liaisonUnits.isEmpty()){
				long totalCount=findService.countLiaisonUnit(name);
				int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
				statusCode=true;
				JSONArray units=new JSONArray();
				for(LiaisonUnit liaisonUnit:liaisonUnits){
					JSONObject unit=new JSONObject();
					unit.put("name", liaisonUnit.getName());
					unit.put("description", liaisonUnit.getDescription());
					unit.put("createUser", liaisonUnit.getCreateUser());
					unit.put("id", liaisonUnit.getId());
					units.add(unit);
				}
				responseData.put("units", units);
			}
			else{
				err="当前数据库中不存在可用人员组，您可以新增条目。";
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			err="下发请求中缺少必要参数项，请核对后重试。";
		}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /***
     * 删除人员组
     *@param id long:人员组标识
     *@return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示删除失败；true表示删除成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/> 
     * ***/
    @RequestMapping(value="liaisonUnit/delLiaisonUnit")
    public void delWarn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		JSONObject responseData=new JSONObject();
		String err="";
		try{
			Long id=Long.parseLong(request.getParameter("id"));
			statusCode=delService.delLiaisonUnit(id);
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
    /****
     * 修改人员组
     * @param unit JSONObject:新增人员组的详细信息，包含以下两个字段：<br/>
     * 	&nbsp;&nbsp; name String:人员组名称<br/>
     *  &nbsp;&nbsp; description String:人员组描述<br/>
     *  &nbsp;&nbsp; createUser String:操作人员<br/>
	 * @return JSONObject 包括两个参数<br/>
	 *   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
	 *   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
     * **/
    @RequestMapping("liaisonUnit/alterLiaisonUnit")
    public void alterLiaisonUnit(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	JSONObject params=null;
    	Long id=null;
		try {
			params=JSONObject.fromObject(request.getParameter("unit"));	
			id=Long.parseLong(request.getParameter("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			err="下发请求中缺少必要的参数项\"unit\"，请核对后重试。";
		}
    	if(params!=null&&id!=null){
    		try{
    			LiaisonUnit unit=findService.findLiaisonUnitById(id);
    			unit.setName(params.getString("name"));
    			unit.setDescription(params.getString("description"));
    			statusCode=alterService.alterLiaisonUnit(unit);
    			if(!statusCode){
    				err="数据库操作异常，请稍后重试。";
    			}
    		}catch(Exception e){
    			err="下发请求中缺少必要的参数项，请核对后重试。";
    		}
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }
    /***
	   * 新增人员时获取各人员组的标识和名称键值对
	   * @return JSONObject 包括两个参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
		*   &nbsp;&nbsp; units JSONArray:statusCode为true时有效，表示当前系统中存在的人员组信息，每个元素为JSONObject，包含以下字段<br/>:
		*   &nbsp;&nbsp; &nbsp;&nbsp; name String:人员组名称 
		*   &nbsp;&nbsp; &nbsp;&nbsp; id long：人员组标识
	   * **/
    @RequestMapping("liaison/getUnitInfo")
    public void getUnitInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	List<LiaisonUnit> liaisonUnits=findService.findAllLiaisonUnits();
    	if(liaisonUnits!=null&&!liaisonUnits.isEmpty()){
    		statusCode=true;
    		JSONArray units=new JSONArray();
    		for(LiaisonUnit liaisonUnit:liaisonUnits){
    			JSONObject unit=new JSONObject();
    			unit.put("id", liaisonUnit.getId());
    			unit.put("name", liaisonUnit.getName());
    			units.add(unit);
    		}
    		responseData.put("units", units);
    	}
    	else{
    		err="当前数据库不存在可用人员组，您可以在\"人员组\"功能块新增人员组。";
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /***
	   * 新增人员
	   * @param liaison JSONObject:新建人员的参数信息，包含以下字段：<br/>
	   * &nbsp;&nbsp; unitId long:人员组标识
	   * &nbsp;&nbsp; name String:人员姓名
	   * &nbsp;&nbsp; phone String:手机号
	   * &nbsp;&nbsp; email String:邮箱
	   * &nbsp;&nbsp; description String:人员描述 
	   * @return JSONObject 包括两个参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
	   * **/
    @RequestMapping("liaison/addLiaison")
    public void addLiaison(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	JSONObject params=null;
		try {
			params=JSONObject.fromObject(request.getParameter("liaison"));	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			err="下发请求中缺少必要的参数项\"liaison\"，请核对后重试。";
		}
    	if(params!=null){
    		try{
    			Liaisons liaison=new Liaisons();
    			liaison.setName(params.getString("name"));
    			liaison.setDescription(params.getString("description"));
    			liaison.setPhone(params.getString("phone"));
    			liaison.setEmail(params.getString("email"));
    			Long unitId=params.getLong("unitId");
    			if(unitId!=null){
    				LiaisonUnit unit=findService.findLiaisonUnitById(unitId);
    				liaison.setLiaisonUnit(unit);
    			}
    			Serializable id=addService.addLiaison(liaison);
    			if(id!=null){
    				statusCode=true;
    			}
    			else{
    				err="数据库操作异常，请稍后重试。";
    			}
    		}catch(Exception e){
    			err="下发请求中缺少必要的参数项，请核对后重试。";
    		}
    	}else{
    		err="下发请求中缺少必要的参数项\"liaison\"，请核对后重试。";
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /***
	   * 修改人员
	   * @param id long:人员标识
	   * @param liaison JSONObject:人员的参数信息，包含以下字段：<br/>
	   * &nbsp;&nbsp; unitId long:人员组标识
	   * &nbsp;&nbsp; name String:人员姓名
	   * &nbsp;&nbsp; phone String:手机号
	   * &nbsp;&nbsp; email String:邮箱
	   * &nbsp;&nbsp; description String:人员描述 
	   * @return JSONObject 包括两个参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
	 * **/
    @RequestMapping("liaison/alterLiaison")
    public void alterLiaison(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	JSONObject params=null;
    	Long id=null;
		try {
			params=JSONObject.fromObject(request.getParameter("liaison"));	
			id=Long.parseLong(request.getParameter("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			err="下发请求中缺少必要的参数项，请核对后重试。";
		}
    	if(id!=null&&params!=null){
    		try{
    			Liaisons liaison=findService.findLiaisonById(id);
    			if(liaison!=null){
    				liaison.setName(params.getString("name"));
        			liaison.setDescription(params.getString("description"));
        			liaison.setPhone(params.getString("phone"));
        			liaison.setEmail(params.getString("email"));
        			Long unitId=params.getLong("unitId");
        			if(unitId!=null){
        				LiaisonUnit unit=findService.findLiaisonUnitById(unitId);
        				liaison.setLiaisonUnit(unit);
        			}
    			}
    			statusCode=alterService.alterLiaison(liaison);
                if(!statusCode){
    				err="数据库操作异常，请稍后重试。";
    			}
    		}catch(Exception e){
    			err="下发请求中缺少必要的参数项，请核对后重试。";
    		}
    	}
    	else{
    		err="下发请求中缺少必要的参数项，请核对后重试。";
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }
    /***
	   * 分页获取人员列表
	   * @param unitId long:人员组标识,为null时表示全部
	   * @param name String:人员名称，为空字符串时表示全部
	   * @param col int:表格行数
	   * @param page int:当前页码
	   * @return JSONObject 包括以下参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
		*   &nbsp;&nbsp; totalPage int:statusCode为true时有效，总页码数<br/>
		*   &nbsp;&nbsp; liaisons JSONArray:statusCode为true时有效，表示当前页码的人员信息，每个元素为一个JSONArray，包括以下参数：<br/>
		*   &nbsp;&nbsp;&nbsp;&nbsp; id long:人员标识
		*   &nbsp;&nbsp;&nbsp;&nbsp; name String：人员姓名 
		*   &nbsp;&nbsp;&nbsp;&nbsp; unitName String：人员组名称
		*   &nbsp;&nbsp;&nbsp;&nbsp; phone String：手机号 
		*   &nbsp;&nbsp;&nbsp;&nbsp; email String：邮箱 
		*   &nbsp;&nbsp;&nbsp;&nbsp; description String：描述 
	   * **/
    @RequestMapping("liaison/getPaginationLiaison")
    public void getPaginationLiaison(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
		try {
			String name=request.getParameter("name");	
			name=(name==null)?"":name;
			Long unitId=null;
			if(!request.getParameter("unitId").equals("null")){
				unitId=Long.parseLong(request.getParameter("unitId"));
			}
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<Liaisons> liaisons=findService.findPaginationLiaison(unitId,name, col, page);
			if(liaisons!=null&&!liaisons.isEmpty()){
				long totalCount=findService.countLiaisons(unitId,name);
				int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
				statusCode=true;
				JSONArray liaisonInfos=new JSONArray();
				for(Liaisons liaison:liaisons){
					JSONObject liaisonInfo=new JSONObject();
					liaisonInfo.put("name", liaison.getName());
					liaisonInfo.put("description", liaison.getDescription());
					liaisonInfo.put("phone", liaison.getPhone());
					liaisonInfo.put("id", liaison.getId());
					liaisonInfo.put("email", liaison.getEmail());
					liaisonInfo.put("unitName", liaison.getLiaisonUnit().getName());
					liaisonInfos.add(liaisonInfo);
				}
				responseData.put("liaisons", liaisonInfos);
			}
			else{
				err="当前数据库中不存在可用人员，您可以新增条目。";
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
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    } 
    /***
	   * 删除人员
	   * @param id long:人员标识
	   * @return JSONObject 包括两个参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
	   * **/
    @RequestMapping("liaison/delLiaison")
    public void delLiaison(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	Long id=null;
		try {
			id=Long.parseLong(request.getParameter("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			err="下发请求中缺少必要的参数项，请核对后重试。";
		}
    	if(id!=null){
    		statusCode=delService.delLiaison(id);
            if(!statusCode){
    			err="数据库操作异常，请稍后重试。";
    	    }
    	}
    	else{
    		err="下发请求中缺少必要的参数项，请核对后重试。";
    	}
    	if(!statusCode){
    		responseData.put("err", err);
    	}
    	responseData.put("statusCode", statusCode);
    	response.setContentType("text/html;charset=utf-8");
    	PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }     
}
