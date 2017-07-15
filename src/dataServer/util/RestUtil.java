package dataServer.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONObject;
/**
 * 采用rest方式访问数据库接口
 * ***/
public class RestUtil {
  //贵州省MUSIC系统服务器地址
  private final String host="10.203.89.55";
  //连接超时时间
  private final int timeoutInMilliSeconds=1000*60*2 ;//2 MINUTE
  /**
   *采用rest方式连接数据库获取数据
   *@param params String 包含请求参数的url
   *@return String 数据库服务器返回的数据，字符串形式
   */
  public JSONObject getRestData(String params) {
	JSONObject response=new JSONObject();
    try {
      URI uri = new URI("http",this.host,"/cimiss-web/api",params,"");
      URL url=uri.toURL();
      URLConnection con = url.openConnection();
      con.setConnectTimeout( this.timeoutInMilliSeconds ); 
      StringBuilder retStr=new StringBuilder();
      BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream()));
      String line = reader.readLine();
      while(line!=null) {
        retStr.append(line).append("\r\n");
        line = reader.readLine();
      }
      reader.close();
      response=JSONObject.fromObject(retStr.toString());
     } catch (Exception e) {
       // e.printStackTrace();
        response.clear();
        response.put("returnCode", "1");
    }
 
    return response;
  }

  //测试用
  public static JSONObject testJson(String file){
	  JSONObject json=new JSONObject();
		try {
			FileInputStream fs=new FileInputStream(file);
			String response="";
			byte[] bbf=new byte[1024]; 
			int has=0;
			while((has=fs.read(bbf))>0){
				response+=new String(bbf,0,has);
			}
			fs.close();
			String err=response.replaceAll("\"", "'");
			json=JSONObject.fromObject(err);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
  }
  /**
   存储数据 
   */
  public String setRestData(String params,String inString) {
    StringBuilder retStr = new StringBuilder();
    URI uri = null;
    URL url = null;
    java.io.BufferedReader reader = null;
    URLConnection con;
    params=params+"&instring="+inString;
    try {
      uri = new URI("http", this.host, "/cimiss-web/write", params, "");
      url = uri.toURL();
      con = url.openConnection();
      con.setConnectTimeout( this.timeoutInMilliSeconds ); 
      reader = new BufferedReader(  new InputStreamReader(con.getInputStream()));
      String line = reader.readLine();
      while (line != null) {
        retStr.append(line).append("\r\n");
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception ex1) {
      ex1.printStackTrace();
    }
    return retStr.toString();
  }
  
  /** 
   * 从网络Url中下载文件 
   * @param urlStr 
   * @param fileName 
   * @param savePath 
   * @throws IOException 
   */  
  public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{  
      URL url = new URL(urlStr);    
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
              //设置超时间为3秒  
      conn.setConnectTimeout(3*1000);  
      //防止屏蔽程序抓取而返回403错误  
      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  

      //得到输入流  
      InputStream inputStream = conn.getInputStream();    
      //获取自己数组  
      byte[] getData = readInputStream(inputStream);      

      //文件保存位置  
      File saveDir = new File(savePath);  
      if(!saveDir.exists()){  
          saveDir.mkdir();  
      }  
      File file = new File(saveDir+File.separator+fileName);      
      FileOutputStream fos = new FileOutputStream(file);       
      fos.write(getData);   
      if(fos!=null){  
          fos.close();    
      }  
      if(inputStream!=null){  
          inputStream.close();  
      }  


      System.out.println("info:"+url+" download success");   

  }  



  /** 
   * 从输入流中获取字节数组 
   * @param inputStream 
   * @return 
   * @throws IOException 
   */  
  public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
      byte[] buffer = new byte[1024];    
      int len = 0;    
      ByteArrayOutputStream bos = new ByteArrayOutputStream();    
      while((len = inputStream.read(buffer)) != -1) {    
          bos.write(buffer, 0, len);    
      }    
      bos.close();    
      return bos.toByteArray();    
  }    
 
  
  
  public static void main(String[] args){
	  
  }
}
