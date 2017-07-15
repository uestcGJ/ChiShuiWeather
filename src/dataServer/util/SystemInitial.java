package dataServer.util;
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

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import domain.Area;
import domain.Permissions;
import domain.Role;
import domain.User;
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
    private AddService addServiceImpl;
    private FindService findServiceImpl;
	@Override  
    public void init(ServletConfig config) {  
	    try {  
	           super.init();  
	       } catch (ServletException e) {  
	         e.printStackTrace();  
	    } 
	    WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();    
	    addServiceImpl=(AddService)wac.getBean("addService");
	    findServiceImpl=(FindService)wac.getBean("findService");
        List<Permissions> permIn=findServiceImpl.findAllPermissions();
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
            	Serializable permId=addServiceImpl.addPermission(permission);
            	if(permId!=null){
            		String children[][]=(String[][])permss.get(pCount).get("children");
            		String [][][] grandChildren=(String [][][] )permss.get(pCount).get("grandChildren");
            		for(int cCount=0;cCount<children.length;cCount++){
            			Permissions childPerm=new Permissions();
            			childPerm.setName(children[cCount][0]);
            			childPerm.setPermission(children[cCount][1]);
            			childPerm.setDescription(children[cCount][2]);
            			childPerm.setParent(permission);
            			addServiceImpl.addPermission(childPerm);
            			if(grandChildren!=null){
            				for(int gCount=0;gCount<grandChildren[cCount].length;gCount++){
            					    Permissions gChildPerm=new Permissions();
                					gChildPerm.setName(grandChildren[cCount][gCount][0]);
                					gChildPerm.setPermission(grandChildren[cCount][gCount][1]);
                					gChildPerm.setDescription(grandChildren[cCount][gCount][2]);
                					gChildPerm.setParent(childPerm);
                        			addServiceImpl.addPermission(gChildPerm);
            				}
            			}
            		}
            	}
           }
	    	  /**新建超级管理员**/	
	    	Role superAdmin=new Role();
	    	superAdmin.setName("超级管理员");
	    	superAdmin.setDescription("系统初始时自带角色，拥有最高权限，请勿删除");
	    	superAdmin.setPmss(new HashSet<Permissions>(findServiceImpl.findAllPermissions()));
	    	Serializable roleId=addServiceImpl.addRole(superAdmin);
	    	if(roleId!=null){
	    		User admin=new User();
		    	admin.setAccount("admin");
		    	admin.setPassword(NumConv.passwordMD5("123456"));
		    	admin.setDescription("超级管理员");
		    	admin.setEmail("");
		    	admin.setPhone("");
		    	admin.setRole(superAdmin);
		    	addServiceImpl.addUser(admin);
	    	}
        }
	  
    	//判断是否已经存放了区域
    	List<Area> areas=findServiceImpl.findAllAreas();
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
    		addServiceImpl.addArea(chiShui);
    		addServiceImpl.addArea(xiShui);
    		addServiceImpl.addArea(renHuai);
    		System.out.println("================>[Servlet]自动加载建立区域结束.");
    	}
   }  
}
