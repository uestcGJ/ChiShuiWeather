package dataServer.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
/**
 * 格式化工具
 * ***/
public class FormatUtil {
  /**
   * 返回XML格式的数据
   * **/
  public String getRstXml(String xml){
      String formatXml = null;  
      SAXReader reader = new SAXReader();  
      try{
	      Document document=reader.read(new StringReader(xml));
	      XMLWriter writer=null;  
	      if(document!=null){
	        StringWriter stringWriter = new StringWriter();  
	        OutputFormat format = new OutputFormat("", true);  
	        writer = new XMLWriter(stringWriter,format);  
	        writer.write(document);  
	        writer.flush();  
	        formatXml = stringWriter.getBuffer().toString();
	      }      
      } catch( IOException|DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      } 
      return formatXml;
  }
  /***
   * 将JSON格式的字符串转换为JSON对象
   * @param json String 字符型形式的JSON数据
   * @return JSONObject JSON对象
   * ***/
  public JSONObject getRstJson(String json ) {
	 JSONObject jsonFormat=null;
	 try {
	    jsonFormat=JSONObject.fromObject(json);
	 } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 }
     return jsonFormat;
  } 
  /***
   *获取HTML格式的数据
   * **/
  public String getRstHtml(String html){
    return this.getRstXml(html);
  }  
}
