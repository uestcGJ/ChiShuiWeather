package dataServer.util;

import java.util.List;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import domain.SystemInfo;
import net.sf.json.JSONObject;
import service.FindService;

/**
 * 发送消息工具类接口
 * 提供发送短信和邮件的方法
 * **/

public class MessageUtil{
	private static FindService findService;
	public void setFindService(FindService findService){
		MessageUtil.findService = findService;
	}
	/**
	 * 发送短信
	 * @param  phone (String) 收件人手机号
	 * @param  context (String)  短信内容
	 * @return boolean 短信发送状态 
	 * **/
	public synchronized static boolean sendMessage(List<String> phone,String context){
	        String GSMPort=null;
	        SerialPortUtil serial=new SerialPortUtil(9600);
	 	    GSMPort=serial.GetGsmPort();
	 	    if(GSMPort!=null){   
	   	      	MessageHandler smsHandler = new MessageHandler(GSMPort,9600);
			    boolean status=false;
			    for(String ph:phone){
			    	status=smsHandler.sendSMS(ph, context);
			    	if(!status){
			    		break;
			    	}
			    }
			    //关闭服务器
			    smsHandler.destroy();
	        	return status;
	        }
	        else{
	        	return false;
	        }
	 }
	/**
	 * 邮件发送
	 * @param recAddress 收件人地址
	 * @param mailHead 邮件主题
	 * @param context 邮件内容
	 * @return boolean 发送状态
	 * **/    
	  public static boolean sendEmail(List<String> recAddress,String mailHead,String context){
		  WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
	      findService=(FindService)wac.getBean("findService");
	      System.out.println("--------sendEmail----------");
		  SystemInfo systemInfo=findService.findSystemInfoById((long)1);
		  if(systemInfo.getEmailAddr()!=null){
			  String url="message/sendEmail";
			  CallWebService webService=new CallWebService(url);
			  JSONObject emailPara=new JSONObject();
			  emailPara.put("host", systemInfo.getEmailAddr());
			  emailPara.put("port",systemInfo.getEmailPort());
			  emailPara.put("account", systemInfo.getEmailAccount());
			  emailPara.put("pword", systemInfo.getEmailPword());
			  JSONObject infoPara=new JSONObject();
			  infoPara.put("recAddress", recAddress);
			  infoPara.put("mailHead", mailHead);
			  infoPara.put("context", context);
			  String param="emailPara="+emailPara.toString()+"&infoPara="+infoPara;
			  JSONObject responseData= webService.callWebService(param);
			  System.out.println("--------回传信息----------"+responseData);
			  return responseData.getBoolean("statusCode");
		  }
		  return false;
	  } 
		/**
		 * 利用语音平台发送短信
		 * @param phone 收件人手机号
		 * @param context 内容
		 * @return boolean 发送状态
		 * **/    
		  public static boolean sendSMS(String phone,String context){
			  WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
		      findService=(FindService)wac.getBean("findService");
		      System.out.println("--------sendSMS----------");
			  SystemInfo systemInfo=findService.findSystemInfoById((long)1);
			  String accountId="";
			  String appId="";
			  String authcToken="";
			  boolean useDefault=true;
			  if(systemInfo.getAccountSid()!=null){
				  accountId=systemInfo.getAccountSid();
				  appId=systemInfo.getAppId();
				  authcToken=systemInfo.getAuthToken();
				  useDefault=false;
			  }
			  String url="message/sendSMS";
			  CallWebService webService=new CallWebService(url);
			  JSONObject smsPara=new JSONObject();
			  smsPara.put("accountId", accountId);
			  smsPara.put("appId",appId);
			  smsPara.put("useDefault",useDefault);
			  smsPara.put("authcToken",authcToken);
			  JSONObject infoPara=new JSONObject();
			  infoPara.put("phone", phone);
			  infoPara.put("context", context);
			  String param="smsPara="+smsPara.toString()+"&infoPara="+infoPara;
			  JSONObject responseData= webService.callWebService(param);
			  System.out.println("--------回传信息----------"+responseData);
			  return responseData.getBoolean("statusCode");
		  }
		  /**
			 * 拨打电话，发送语音消息
			 * @param phone String 收件人手机号
			 * @param context String 内容
			 * @param playTimes int 播放次数 
			 * @return boolean 发送状态
			 * **/    
		 public static boolean sendVoiceMessage(String phone,String context,int playTimes){
				WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
			    findService=(FindService)wac.getBean("findService");
			    System.out.println("--------sendSMS----------");
				SystemInfo systemInfo=findService.findSystemInfoById((long)1);
				String accountId="";
				String appId="";
				String authcToken="";
				boolean useDefault=true;
				//如果数据库中已经存放了语音平台的信息，则读取信息，否则发送空字符串，让服务器端读取配置信息
				if(systemInfo.getAccountSid()!=null){
					  accountId=systemInfo.getAccountSid();
					  appId=systemInfo.getAppId();
					  authcToken=systemInfo.getAuthToken();
					  useDefault=false;
				}
				String url="message/sendVoiceMessage";
				CallWebService webService=new CallWebService(url);
				JSONObject voicePara=new JSONObject();
				voicePara.put("accountId", accountId);
				voicePara.put("appId",appId);
				voicePara.put("useDefault",useDefault);
				voicePara.put("authcToken",authcToken);
				JSONObject infoPara=new JSONObject();
				infoPara.put("phone", phone);
				infoPara.put("context", context);
				infoPara.put("playTimes", playTimes);
				String param="voicePara="+voicePara.toString()+"&infoPara="+infoPara;
				JSONObject responseData= webService.callWebService(param);
				System.out.println("--------回传信息----------"+responseData);
				return responseData.getBoolean("statusCode");
	  }  	  
}
