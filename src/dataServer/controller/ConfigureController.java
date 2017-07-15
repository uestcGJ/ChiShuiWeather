package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.EncryptUtils;
import dataServer.util.MessageUtil;
import domain.SystemInfo;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;

@Controller
/****基本主控配置控制器***/
public class ConfigureController {
	@Resource(name="findService")
	private FindService findService;
	@Resource(name="addService") 
	private AddService addService;
	@Resource(name="alterService") 
	private AlterService alterService;
	@Resource(name="deleteService") 
	private DeleteService delService;
	  /**获取邮件服务器信息
	   * 
	   @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示当前配置成功；true表示当前配置失败<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	     *   emailService JSONObject:当statusCode为true时有效，表示邮件服务器的基本信息，包含以下字段：<br/>
	     *    address String:服务器地址<br/>
	     *    port    int:服务器端口<br/>
	     *    account String:用户账号<br/> 
	     * **/
	@RequestMapping("configure/emailServer/getServer")
	public void getEmailServer(HttpServletRequest request,HttpServletResponse response){
		 JSONObject responseData=new JSONObject();
		 boolean statusCode=false;
		 String err="";
		 SystemInfo systemInfo=findService.findSystemInfoById(1L);
		 if(systemInfo!=null&&systemInfo.getEmailAccount()!=null){
			 statusCode=true;
			 JSONObject emailServer=new JSONObject();
			 emailServer.put("address", systemInfo.getEmailAddr());
			 emailServer.put("port", systemInfo.getEmailPort());
			 String account="*******";
			 try {
				 account=EncryptUtils.aesDecrypt(systemInfo.getEmailAccount());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 emailServer.put("account", account);
			 responseData.put("emailServer", emailServer);
		 }else{
			 err="当前系统尚未配置邮件服务器";
		 }
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		response.setCharacterEncoding("utf-8");
		response.setContentType("json/text");
		try {
			PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	  /**
	   * 测试邮件服务器
	   * @param emailServer JSONObject:邮件服务器的相关参数，包含以下信息:<br/>
	   * 	address String:服务器地址<br/>
	   *    port    int:服务器端口<br/>
	   *    account String:用户账号<br/>
	   *    pword String:邮箱密码<br/>
	   @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示当前配置可用；true表示当前配置不可用<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	   * **/
	@RequestMapping("configure/emailServer/checkServer")
	public void checkServer(HttpServletRequest request,HttpServletResponse response){
		 JSONObject responseData=new JSONObject();
		 boolean statusCode=false;
		 String err="";
		 JSONObject emailServer=null;
		 try {
			 emailServer=JSONObject.fromObject(request.getParameter("emailServer"));	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				err="下发请求中缺少必要的参数项\"emailServer\"，请核对后重试。";
		 }
		 if(emailServer!=null){
			 String address=emailServer.getString("address").trim();
			 int port=emailServer.getInt("port");
			 String account=emailServer.getString("account").trim();
			 String pword=emailServer.getString("pword").trim();
			 Map<String,Object>emailPara=new LinkedHashMap<String,Object>();
			 emailPara.put("address", address);
			 emailPara.put("port", port);
			 emailPara.put("account", account);
			 emailPara.put("pword", pword);
			 statusCode=MessageUtil.checkServer(emailPara);
			 if(!statusCode){
				 err="当前邮件服务器不可用，请确认服务器地址、端口以及账号密码是否匹配。";
			 }
		 }
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		response.setCharacterEncoding("utf-8");
		response.setContentType("json/text");
		try {
			PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	 /**
	   * 配置邮件服务器
	   * @param emailServer JSONObject:邮件服务器的相关参数，包含以下信息:<br/>
	   * 	address String:服务器地址<br/>
	   *    port    int:服务器端口<br/>
	   *    account String:用户账号<br/>
	   *    pword String:邮箱密码<br/>
	   @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示当前配置成功；true表示当前配置失败<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	   * **/
	@RequestMapping("configure/emailServer/setServer")
	public void setServer(HttpServletRequest request,HttpServletResponse response){
		 JSONObject responseData=new JSONObject();
		 boolean statusCode=false;
		 String err="";
		 JSONObject emailServer=null;
		 try {
			 emailServer=JSONObject.fromObject(request.getParameter("emailServer"));	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				err="下发请求中缺少必要的参数项\"emailServer\"，请核对后重试。";
		 }
		 if(emailServer!=null){
			 String address=emailServer.getString("address").trim();
			 int port=emailServer.getInt("port");
			 String account=emailServer.getString("account").trim();
			 String pword=emailServer.getString("pword").trim();
			 Map<String,Object>emailPara=new LinkedHashMap<String,Object>();
			 emailPara.put("address", address);
			 emailPara.put("port", port);
			 emailPara.put("account", account);
			 emailPara.put("pword", pword);
			 statusCode=MessageUtil.checkServer(emailPara);
			 if(statusCode){//邮件服务器可用，存储在数据库中
				SystemInfo systemInfo=new SystemInfo();
				systemInfo.setEmailAddr(address);
				systemInfo.setEmailPort(port);
				try{
					account=EncryptUtils.aesEncrypt(account);
					pword=EncryptUtils.aesEncrypt(pword);
				}catch(Exception e){
					
				}
				systemInfo.setEmailAccount(account);
				systemInfo.setEmailPword(pword);
				Serializable id=addService.addSystemInfo(systemInfo);
				if(id==null){
					statusCode=false;
					err="数据库存储异常，请稍后重试。";
				}
			}else{
				  err="系统检查发现当前邮件服务器不可用，请确认服务器地址、端口以及账号密码是否匹配。";
			}
		 }
		
		if(!statusCode){
				responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		response.setCharacterEncoding("utf-8");
		response.setContentType("json/text");
		try {
			PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
