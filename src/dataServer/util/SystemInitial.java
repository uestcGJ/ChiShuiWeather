package dataServer.util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import domain.Area;
import domain.Permissions;
import domain.Role;
import domain.Stations;
import domain.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import service.AddService;
import service.FindService;
/***
 * 
 * 初始化系统
 * ***/
public class SystemInitial extends HttpServlet {  
	/**系统初始化时间***/
	public final static Timestamp initialTime=DateUtil.getDate();
    private static final long serialVersionUID = 1L;  
    private AddService addService;
    private FindService findService;
	@Override  
    public void init(ServletConfig config) {  
	    try {  
	           super.init();  
	       } catch (ServletException e) {  
	         e.printStackTrace();  
	    } 
	    WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
	    addService=(AddService)wac.getBean("addService");
	    findService=(FindService)wac.getBean("findService");
        List<Permissions> permIn=findService.findAllPermissions();
        System.out.println("================>[Servlet]系统初始化自动加载建立角色权限开始.");  
        List<Map<String,Object>>permss=new ArrayList<Map<String,Object>>();//所有的权限
        /**
         * 账户管理
         * **/
        LinkedHashMap <String,Object>account=new LinkedHashMap<String,Object>();
        account.put("name", "账户管理");
        account.put("permission","account");
        account.put("description","账户管理");
        account.put("parent",null);
	    String accountChilds [][]={	
	    							{"修改密码","modifyPword","修改密码"},
	    							{"修改个人信息","modifyInfo","修改个人信息"},				
	    						  };
	    account.put("children",accountChilds);
	    account.put("grandChildren",null);
    	permss.add(account);
    	/**
    	 * 角色管理模块
    	 * **/
    	LinkedHashMap <String,Object>roles=new LinkedHashMap<String,Object>();
    	roles.put("name", "角色管理");
    	roles.put("permission","role");
    	roles.put("description","角色管理");
    	roles.put("parent",null);
	    String roleChilds [][]={	
	    							{"增加角色","addRole","增加角色"},
	    							{"删除角色","delRole","删除角色"},				
	    							{"修改角色","modifyRole","修改角色"},
	    						};
    	roles.put("children",roleChilds);
     	roles.put("grandChildren",null);
    	permss.add(roles);
    	/**
    	 * 用户管理模块
    	 * **/
    	LinkedHashMap <String,Object>users=new LinkedHashMap<String,Object>();//用户管理部分
    	users.put("name", "用户管理");
    	users.put("permission","user");
    	users.put("description", "用户管理");
    	users.put("parent", null);
	    String userChilds [][]={   
	    							{"增加用户","addUser","增加用户"},
									{"删除用户","delUser","删除用户"},				
									{"修改用户","modifyUser","修改用户"},
								};
	    users.put("children",userChilds);
   	    users.put("grandChildren",null);
	    permss.add(users);
        if(permIn.size()<1){//只有数据库未建立权限记录时才新建权限
	    	for(int pCount=0;pCount<permss.size();pCount++){
        		Permissions permission=new Permissions();
            	permission.setName((String) permss.get(pCount).get("name"));
            	permission.setDescription((String) permss.get(pCount).get("description"));
            	permission.setParent((Permissions) permss.get(pCount).get("parent"));
            	permission.setPermission((String) permss.get(pCount).get("permission"));
            	Serializable permId=addService.addPermission(permission);
            	if(permId!=null){
            		String children[][]=(String[][])permss.get(pCount).get("children");
            		String [][][] grandChildren=(String [][][] )permss.get(pCount).get("grandChildren");
            		for(int cCount=0;cCount<children.length;cCount++){
            			Permissions childPerm=new Permissions();
            			childPerm.setName(children[cCount][0]);
            			childPerm.setPermission(children[cCount][1]);
            			childPerm.setDescription(children[cCount][2]);
            			childPerm.setParent(permission);
            			addService.addPermission(childPerm);
            			if(grandChildren!=null){
            				for(int gCount=0;gCount<grandChildren[cCount].length;gCount++){
            					    Permissions gChildPerm=new Permissions();
                					gChildPerm.setName(grandChildren[cCount][gCount][0]);
                					gChildPerm.setPermission(grandChildren[cCount][gCount][1]);
                					gChildPerm.setDescription(grandChildren[cCount][gCount][2]);
                					gChildPerm.setParent(childPerm);
                        			addService.addPermission(gChildPerm);
            				}
            			}
            		}
            	}
           }
	    	  /**新建超级管理员**/	
	    	Role superAdmin=new Role();
	    	superAdmin.setName("超级管理员");
	    	superAdmin.setDescription("系统初始时自带角色，拥有最高权限，请勿删除");
	    	superAdmin.setPmss(new HashSet<Permissions>(findService.findAllPermissions()));
	    	Serializable roleId=addService.addRole(superAdmin);
	    	if(roleId!=null){
	    		User admin=new User();
		    	admin.setAccount("admin");
		    	admin.setPassword(NumConv.passwordMD5("123456"));
		    	admin.setDescription("超级管理员");
		    	admin.setEmail("");
		    	admin.setPhone("");
		    	admin.setRole(superAdmin);
		    	addService.addUser(admin);
	    	}
        }
	  
    	//判断是否已经存放了区域
    	List<Area> areas=findService.findAllAreas();
    	if(areas.size()<1){
    		Area renHuai=new Area();
    		renHuai.setName("仁怀");
    		renHuai.setCode("520382");
    		Area chiShui=new Area();
    		chiShui.setName("赤水");
    		chiShui.setCode("520381");
    		Area xiShui=new Area();
    		xiShui.setName("习水");
    		xiShui.setCode("520330");
    		addService.addArea(chiShui);
    		addService.addArea(xiShui);
    		addService.addArea(renHuai);
    		System.out.println("================>[Servlet]自动加载建立监测区域结束.");
    	}
    	/*
    	 * 读取监测站信息，新建监测站
    	 */
    	if(findService.findStationById(1L)==null){
        	createStations();
    		System.out.println("================>[Servlet]自动加载建立监测站结束.");
    	}
   }
	/***
	 * 读取文本文件，获取监测站信息，批量建立监测站
	 * ***/
	final private  void createStations(){
		BufferedReader reader=null;
		JSONObject info=null;
		try {
			String file=SystemInitial.class.getResource("/properties/stationInfo.txt").getPath().replaceAll("%20", " ");
			reader=new BufferedReader(new FileReader(file));
			String line=null;
			StringBuilder sb=new StringBuilder();
			while((line=reader.readLine())!=null){
				sb.append(line);
				sb.append("\n");
			}
			info= JSONObject.fromObject(sb.toString().replaceAll("\"", "'"));
			if(info!=null){
				 WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
				 SessionFactory sessionFactory=(SessionFactory)wac.getBean("sessionFactory");
				 Session session=sessionFactory.openSession();
				 Transaction tx=session.beginTransaction();
				 JSONArray stations=info.getJSONArray("stations");
				 for(int i=0;i<stations.size();i++){
					JSONObject stationInfo=stations.getJSONObject(i);
					Stations station=new Stations();
					String areaCode=stationInfo.getString("areaCode");
					Area area=findService.findAreaByCode(areaCode);
					station.setAreaCode(areaCode);
					station.setArea(area);
					String stationName=stationInfo.getString("stationName");
					station.setCnty(stationName.split("站")[0]);
					station.setName(stationName);
					station.setType("mix");
					station.setCode(stationInfo.getString("stationCode"));
					station.setLat(stationInfo.getString("lat"));
					station.setLng(stationInfo.getString("lng"));
					station.setDescription("系统自动同步监测站点");
					 /**批量操作**/
			         session.save(station);
			         if(i%20==0){//到20个后存储一次
			            session.flush();
			        	session.clear();
			         }
				}
				//提交事务
				tx.commit();
				session.close();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
