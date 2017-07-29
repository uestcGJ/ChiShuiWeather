package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.DateUtil;
import dataServer.util.MessageUtil;
import dataServer.util.NumConv;
import domain.Permissions;
import domain.Role;
import domain.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;


/***
 * 响应账户有关的数据请求
 * **/
@Controller
public class UserController {
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    @Resource(name="deleteService") 
   	private DeleteService delService;
	
	/**
	 *登录验证
	 * ***/
    @RequestMapping("account/getLoginInfo")//
    public void getLoginInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String account=request.getParameter("account");//用户帐号
		User user=findService.findUserForLogin(account);
		JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		if(user!=null){
			statusCode=true;
			responseData.put("account", user.getAccount());
			responseData.put("psw", user.getPassword());
		}	
		responseData.put("statusCode", statusCode);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();	
    }  
	/**
	 *根据账号获取当期用户的角色和权限
	 * ***/
    @RequestMapping("account/getPermission")//
    public void getPermission(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String account=request.getParameter("account");//用户帐号
		User user=findService.findUserForLogin(account);
		JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		if(user!=null){
			List<String> perms=new ArrayList<String>();
			Role role=user.getRole();
			if(role.getPmss()!=null){
				statusCode=true;
				Set<Permissions> pmss=role.getPmss();
				for(Permissions perm:pmss){
					perms.add(perm.getPermission());
				}
				responseData.put("perms", perms);
				responseData.put("role", role.getName());
			}
		}	
		responseData.put("statusCode", statusCode);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    	
    } 
	/**
	 *根据账号获取当期用户基本信息
	 * ***/
    @RequestMapping("account/getAccountInfo")//
    public void getAccountInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String account=request.getParameter("account");//用户帐号
		User user=findService.findUserForLogin(account);
		JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		if(user!=null){
			statusCode=true;
			responseData.put("account", user.getAccount());
			responseData.put("email", user.getEmail());
			responseData.put("phone", user.getPhone());
		}	
		responseData.put("statusCode", statusCode);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    	
    }
	
	/**修改密码**/
	@RequestMapping("account/modifyPword")
	public void modifyPword(HttpServletRequest request,HttpServletResponse response) throws IOException{
		   String account=request.getParameter("account");
		   JSONObject responseData=new JSONObject();
		   boolean statusCode=false;
		   String err="";
		   User user=findService.findUserForLogin(account);
		   if(user!=null){
			   String oldPword=request.getParameter("oldPword");
			   String pword=request.getParameter("newPword");
			   oldPword=NumConv.passwordMD5(oldPword);
			   pword=NumConv.passwordMD5(pword);
			   if(oldPword.equals(user.getPassword())){//验证输入的用户密码是否与数据库的密码一致
				   user.setPassword(pword);
				   statusCode=alterService.alterUser(user);
				   if(!statusCode){
					   err="存储故障";
				   }
			   }
			   else{
				   err="验证失败，您输入的密码有误。";
			   }
		   }
		   else{
			   err="验证失败，请重试。";
		   }
		   responseData.put("statusCode", statusCode);
		   if(!statusCode){
			   responseData.put("err", err);
		   }
	       response.setContentType("text/html");
		   response.setCharacterEncoding("utf-8");
		   PrintWriter out=response.getWriter();
		   out.println(responseData);
		   out.flush();
		   out.close();
	}
/**修改邮箱**/
	@RequestMapping("account/modifyInfo/modifyEmail")
	public void modifyEmail(HttpServletRequest request,HttpServletResponse response) throws IOException{
		   String account=request.getParameter("account");
		   JSONObject responseData=new JSONObject();
		   User user=findService.findUserForLogin(account);
		   boolean statusCode=false;
		   String err="";
		   if(user!=null){
			   String word=request.getParameter("pword");
			   String pword=NumConv.passwordMD5(word);
			   String newEmail=request.getParameter("email");
			   if(pword.equals(user.getPassword())){//验证输入的用户密码是否与数据库的密码一致
				   user.setEmail(newEmail);
				   statusCode=alterService.alterUser(user);
				   if(!statusCode){
					   err="存储故障。";
				   }
			   }
			   else{
				   err="验证失败，您输入的密码有误";
			   }
		   }else{
			  err="账户验证失败，请重试。"; 
		   }
		  responseData.put("statusCode", statusCode);
		  if(!statusCode){
			   responseData.put("err", err);
		  }
		  response.setContentType("text/html");
		  response.setCharacterEncoding("utf-8");
		  PrintWriter out=response.getWriter();
		  out.println(responseData);
		  out.flush();
		  out.close();
	}
	/**修改手机号**/
	 @RequestMapping("account/modifyInfo/modifyPhone")
	 public void modifyPhone(HttpServletRequest request,HttpServletResponse response) throws IOException{
		   String account=request.getParameter("account");
		   JSONObject responseData=new JSONObject();
		   User user=findService.findUserForLogin(account);
		   boolean statusCode=false;
		   String err="";
		   if(user!=null){
			   String word=request.getParameter("pword");
			   String pword=NumConv.passwordMD5(word);
			   String newPhone=request.getParameter("phone");
			   if(pword.equals(user.getPassword())){//验证输入的用户密码是否与数据库的密码一致
				   user.setPhone(newPhone);
				   statusCode=alterService.alterUser(user);
				   if(!statusCode){
					   err="存储故障";
				   }
			   }
			   else{
				   err="验证失败，您输入的密码有误";
			   }
		   }
		   else{
				  err="账户验证失败，请重试。"; 
		   }
		   responseData.put("statusCode", statusCode);
		   if(!statusCode){
			   responseData.put("err", err);
		   }
	       response.setContentType("text/html");
		   response.setCharacterEncoding("utf-8");
		   PrintWriter out=response.getWriter();
		   out.println(responseData);
		   out.flush();
		   out.close();
	 }
 /******角色相关操作******/  
    /**
	  * 获取权限信息，用于查看、编辑、新增角色时的权限列表
	  *@return JSONObject 包含:<br/>
	  *     statusCode boolean：表明状态,true表示查询到权限信息，false表明查找失败<br/>
	  *     err String：statusCode为false时有效，表明失败的具体原因<br/>
	  *     perms JSONObject：statusCode为true时有效,表明具体的角色信息，为key:JSONArray的格式，key表示权限组名称，JSONArray表示当前权限组的各子权限,
	  *     每个元素为一个JSONObject，以key-value的形式呈现权限信息，包含：<br/>
	  *     &nbsp;&nbsp;name String:权限名称
	  *     &nbsp;&nbsp;id long:权限标识   
	  * ***/
    @RequestMapping("role/getPermsInfo")//
    public void getPermsInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	List<Permissions> perms=findService.findAllPermissions();
    	JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		String err="";
    	if(perms!=null&&!perms.isEmpty()){
    		Iterator<Permissions> it=perms.iterator();
    		while(it.hasNext()){
    			Permissions perm=it.next();
    			if(perm.getChildren()==null||perm.getChildren().isEmpty()){
    				it.remove();
    			}
    		}
    		if(!perms.isEmpty()){
    			statusCode=true;
    			JSONObject pmss=new JSONObject();
    			for(Permissions perm:perms){
    				JSONArray childPmss=new JSONArray();
    				Set<Permissions> childPerms=perm.getChildren();
    				for(Permissions childPerm:childPerms){
    					JSONObject pms=new JSONObject();
    					pms.put("id", childPerm.getId());
    					pms.put("name", childPerm.getName());
    					childPmss.add(pms);
    				}
    				pmss.put(perm.getName(), childPmss);
    			}
    			responseData.put("perms", pmss);
    		}
    	}
		responseData.put("statusCode", statusCode);
		if(!statusCode){
			err="由于异常原因，数据库中未查找到权限信息，请稍后重试，若一直出现该故障，请联系维护人员重新部署系统。";
			responseData.put("err", err);
		}
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close(); 	
    }
    /**
     * 增加角色
     * ***/
    @RequestMapping("role/addRole")//
    public void addRole(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject role =JSONObject.fromObject(request.getParameter("role"));//读取新增角色的信息
		JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		String err="";
		if(role!=null){
			statusCode=true;
			Role rol=new Role();
			rol.setAddTime(DateUtil.getCurrentDate());
			rol.setAddUser(role.getString("account"));
			rol.setDescription(role.getString("description"));
			rol.setName(role.getString("name"));
			JSONArray permIds=new JSONArray();
			try{
				permIds=(JSONArray) role.get("perms");
			}catch(Exception e){
				 err="下发参数中缺少必要参数项\"perms\",请核对重发。";	
				e.printStackTrace();
			}
			Set<Permissions> perms=new HashSet<>();
			for(int i=0;i<permIds.size();i++){
				Permissions perm=findService.findPermission(permIds.getLong(i));
				if(perm!=null){
					perms.add(perm);
				}
			}
			rol.setPmss(perms);
			Serializable id=addService.addRole(rol);
			if(id!=null){
				statusCode=true;
			}else{
				err="数据库操作异常，请稍后重试。";
			}
		}else{
		   err="下发参数中缺少必要参数项\"role\",请核对重发。";	
		}
		responseData.put("statusCode", statusCode);
		responseData.put("err", err);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close(); 	
    }
    /***
	   * 分页获取角色列表
	   * @param name String:角色名称，为空字符串时表示全部
	   * @param col int:表格行数
	   * @param page int:当前页码
	   * @return JSONObject 包括以下参数<br/>
		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
		*   &nbsp;&nbsp; totalPage int:statusCode为true时有效，总页码数<br/>
		*   &nbsp;&nbsp; roles JSONArray:statusCode为true时有效，表示当前页码的人员信息，每个元素为一个JSONArray，包括以下参数：<br/>
		*   &nbsp;&nbsp;&nbsp;&nbsp; id long:角色标识
		*   &nbsp;&nbsp;&nbsp;&nbsp; name String：角色名称 
		*   &nbsp;&nbsp;&nbsp;&nbsp; createDate String：创建时间 
		*   &nbsp;&nbsp;&nbsp;&nbsp; createUser String：创建时用户
		*   &nbsp;&nbsp;&nbsp;&nbsp; description String：描述 
	   * **/
    @RequestMapping("role/getPaginationRole")
    public void getPaginationRole(HttpServletRequest request,HttpServletResponse response) throws IOException{
	  	JSONObject responseData=new JSONObject();
	  	boolean statusCode=false;
	  	String err="";
		try {
			String name=request.getParameter("name");	
			name=(name==null)?"":name;
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<Role> roles=findService.getPaginationRole(name, col, page);
			if(roles!=null&&!roles.isEmpty()){
				long totalCount=findService.countRoles(name);
				int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
				statusCode=true;
				JSONArray roleInfos=new JSONArray();
				for(Role role:roles){
					JSONObject roleInfo=new JSONObject();
					roleInfo.put("name", role.getName());
					roleInfo.put("description", role.getDescription());
					roleInfo.put("id", role.getId());
					roleInfo.put("createDate", role.getAddTime());
					roleInfo.put("createUser", role.getAddUser());
					roleInfos.add(roleInfo);
				}
				responseData.put("roles", roleInfos);
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
	* 查看角色详情
	*@param id long:角色标识
	*@return JSONObject 包括以下参数<br/>
	*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
	*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
	*   &nbsp;&nbsp; role JSONObject:statusCode为true时有效，表示角色的详细信息，包括以下参数：<br/>
	*   &nbsp;&nbsp;&nbsp;&nbsp; perms long[]:权限标识
	*   &nbsp;&nbsp;&nbsp;&nbsp; name String：角色名称 
	*   &nbsp;&nbsp;&nbsp;&nbsp; createDate String：创建时间 
	*   &nbsp;&nbsp;&nbsp;&nbsp; createUser String：创建时用户
	*   &nbsp;&nbsp;&nbsp;&nbsp; description String：描述 
    * **/   
    @RequestMapping("role/getRoleDetial")
    public void getRoleDetial(HttpServletRequest request,HttpServletResponse response) throws IOException{
	  	JSONObject responseData=new JSONObject();
	  	boolean statusCode=false;
	  	String err="";
		try {
			Long id=Long.parseLong(request.getParameter("id"));	
			Role role=findService.findRole(id);
			if(role!=null){
				statusCode=true;
				JSONObject roleInfo=new JSONObject();
				roleInfo.put("name", role.getName());
				Set<Permissions> pmss=role.getPmss();
				Set<Long> perms=new TreeSet<Long>(); 
				if(perms!=null){
					for(Permissions perm:pmss){
						perms.add(perm.getId());
					}
				}
				roleInfo.put("perms", perms);
				roleInfo.put("description", role.getDescription());
				roleInfo.put("createDate", role.getAddTime());
				roleInfo.put("createUser", role.getAddUser());
				responseData.put("role", roleInfo);
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
    /***修改角色**/   
    @RequestMapping("role/alterRole")//
    public void alterRole(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	Long id=Long.parseLong(request.getParameter("id"));
    	Role role=findService.findRole(id);
    	JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		String err="";
		if(role!=null){
    		JSONObject rolePara =JSONObject.fromObject(request.getParameter("role"));//读取新增角色的信息
    		if(rolePara!=null){
    			statusCode=true;
    			role.setAlterTime(DateUtil.getCurrentDate());
    			role.setAlterUser(rolePara.getString("account"));
    			role.setDescription(rolePara.getString("description"));
    			role.setName(rolePara.getString("name"));
    			JSONArray permIds=new JSONArray();
    			try{
    				permIds=(JSONArray) rolePara.get("perms");
    			}catch(Exception e){
    				 err="下发参数中缺少必要参数项\"perms\",请核对重发。";	
    				e.printStackTrace();
    			}
    			List<Permissions> perms=new ArrayList<>();
    			for(int i=0;i<permIds.size();i++){
    				Permissions perm=findService.findPermission(permIds.getLong(i));
    				if(perm!=null){
    					perms.add(perm);
    				}
    			}
    			role.setPmss(new HashSet<Permissions>(perms));
    			statusCode=alterService.alterRole(role);
    			if(!statusCode){
    				err="数据库操作异常，请稍后重试";
    			}
    			
    		}else{
    		   err="下发参数中缺少必要参数项\"role\",请核对重发。";	
    		}
    	}
		responseData.put("status", statusCode);
		responseData.put("err", err);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }
    /**
     * 删除角色
     * ***/
    @RequestMapping("role/delRole")
    public void delRole(HttpServletRequest request,HttpServletResponse response) throws IOException{
  	    Long id=Long.parseLong(request.getParameter("id"));
  		JSONObject responseData=new JSONObject();
  		boolean statusCode=delService.delRole(id);
  		if(!statusCode){
  			responseData.put("err","数据库中不存在指定对象。");
  		}
  		responseData.put("statusCode", statusCode);
  		response.setContentType("text/html;charset=utf-8");
  		PrintWriter out=response.getWriter();
  		out.println(responseData);
  		out.flush();
  		out.close();
    } 
    /*********用户相关*********/
    /**
     * 新增用户时判断账号的唯一性
     @param name String:用户输入的账号
	   * @return JSONObject 包含:<br/>
	   *     statusCode boolean:表示执行结果，true表示执行成功，false表示失败<br/>
	   *     err String: 表明失败原因，当statusCode为false时有效<br/>
	   *     isAvailable boolean:表明当前账号的可用性，当statusCode为true时有效,false表示账户不可用，true表示账号可用
	   * **/
    @RequestMapping("user/isAccountAvailable")
    public void isAccountAvailable(HttpServletRequest request,HttpServletResponse response) throws IOException{
  	    String name=request.getParameter("name");
  	    JSONObject responseData=new JSONObject();
  	    boolean statusCode=false;
  	    String err="";
  	    if(name==null){
  	    	err="请求中缺少参数项，请核对后重发。";
  	    }else{
  	    	User user=findService.findUserByAccount(name);
  	    	boolean isAvailable=(user==null)?true:false;
  	    	responseData.put("isAvailable", isAvailable);
  	    }
  		if(!statusCode){
  			responseData.put("err",err);
  		}
  		responseData.put("statusCode", statusCode);
  		response.setContentType("text/html;charset=utf-8");
  		PrintWriter out=response.getWriter();
  		out.println(responseData);
  		out.flush();
  		out.close();
    }
    /**
	   * 新增用户时获取角色信息
	   * @return JSONObject 包含<br/>
	   *     statusCode boolean：表明状态<br/>
	   *     err String ：statusCode为false时有效，表明失败原因<br/>
	   *     roles JSONArray:statusCode为true时有效，表明所有可用角色信息，每个元素为一个JSONObject，包含以下参数：<br/>
	   *     &nbsp;&nbsp;&nbsp;&nbsp; name String：角色名称<br/>
	   *     &nbsp;&nbsp;&nbsp;&nbsp; id long :角色标识
	   * **/
    @RequestMapping("user/getRoleInfo")
    public void getRoleInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
    	boolean statusCode=false;
    	String err="";
    	List<Role> roles=findService.findAllRoles();
    	if(roles!=null&&!roles.isEmpty()){
    		statusCode=true;
    		JSONArray units=new JSONArray();
    		for(Role role:roles){
    			JSONObject unit=new JSONObject();
    			unit.put("id", role.getId());
    			unit.put("name", role.getName());
    			units.add(unit);
    		}
    		responseData.put("roles", units);
    	}
    	else{
    		err="当前数据库不存在除超级管理员外的可用角色，您可以在\"用户角色\"功能块新增角色。";
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
    /**
	   * 新增用户
	   * @param user JSONObject 包含<br/>
	   *    roleId long：角色标识<br/>
	   *    name  String：账户名称<br/>
	   *    pword String：账户密码<br/>
	   *    email String：账户邮箱<br/>
	   *    phone String：账户手机号<br/> 
	   *    description String：用户描述<br/>
	   * @return JSONObject 包含<br/>
	   *     statusCode boolean：表明状态<br/>
	   *     err String ：statusCode为false时有效，表明失败原因
	   * **/
    @RequestMapping("user/addUser")
    public void addUser(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject userInfo =JSONObject.fromObject(request.getParameter("user"));//读取新增角色的信息
		JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		String err="";
		if(userInfo!=null){
			try{
				User user=new User();
				Long roleId=userInfo.getLong("roleId");
				Role role=findService.findRole(roleId);
				user.setEmail(userInfo.getString("email"));
				user.setPhone(userInfo.getString("phone"));
				user.setPassword(NumConv.passwordMD5(userInfo.getString("pword")));
				user.setDescription(userInfo.getString("description"));
				user.setAccount(userInfo.getString("name"));
				if(role!=null){//角色不为空时才新增用户，否则暂停当前操作
					user.setRole(role);
					Serializable id=addService.addUser(user);
					if(id!=null){
						statusCode=true;
					}else{
						err="数据库操作异常，请稍后重试。";
					}
				}else{
					err="下发参数中缺少必要参数项\"roleId\"或根据角色标识未查询到对应的角色记录,请核对重发。";	
				}
				
			}catch(Exception e){
				 err="下发参数中缺少必要参数项,请核对重发。";	
				e.printStackTrace();
			}
		}else{
		   err="下发参数中缺少必要参数项\"user\",请核对重发。";	
		}
		responseData.put("status", statusCode);
		responseData.put("err", err);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close(); 	
    }  
    /***
  		* 分页获取用户列表
  		*@param name String:用户名称，为空字符串时表示全部
  		*@param Long roleId:为null时表示全部
  		*@param col int:表格行数
  		*@param page int:当前页码
  		*@return JSONObject 包括以下参数<br/>
  		*   &nbsp;&nbsp; statusCode boolean:表明状态<br/>
  		*   &nbsp;&nbsp; err String:statusCode为false时有效，表示执行失败的详细原因<br/>
  		*   &nbsp;&nbsp; totalPage int:statusCode为true时有效，总页码数<br/>
  		*   &nbsp;&nbsp; users JSONArray:statusCode为true时有效，表示当前页码的用户信息，每个元素为一个JSONObject，包括以下参数：<br/>
  		*   &nbsp;&nbsp;&nbsp;&nbsp; id long:用户标识
  		*   &nbsp;&nbsp;&nbsp;&nbsp; name String：账户名称 
  		*   &nbsp;&nbsp;&nbsp;&nbsp; roleName String：角色名称
  		*   &nbsp;&nbsp;&nbsp;&nbsp; phone String：手机号
  		*   &nbsp;&nbsp;&nbsp;&nbsp; email String：邮箱
  		*   &nbsp;&nbsp;&nbsp;&nbsp; description String：描述 
  	  * **/
    @RequestMapping("user/getPaginationUser")
    public void getPaginationUser(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	Long roleId=null;
    	boolean statusCode=false;
    	String err="";
    	JSONObject responseData=new JSONObject();
    	if(!request.getParameter("roleId").equals("null")){
    		roleId=Long.parseLong(request.getParameter("roleId"));
    	}
  	    String name=request.getParameter("name");
  	    name=(name==null)?"":name;
  	    try{
  	        int page=Integer.parseInt(request.getParameter("page"));
  	        int col=Integer.parseInt(request.getParameter("col"));
  	        List<User> users=findService.getPaginationUser(roleId,name,col,page);
  	        if(users!=null&&!users.isEmpty()){
  	        	long totalCount=findService.countUsers(roleId,name);
				int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
				pages=(pages==0)?1:pages;
				responseData.put("totalPage", pages);
  	        	statusCode=true;
  	        	JSONArray userInfos=new JSONArray();
  	        	for(User user:users){
  	        		JSONObject userInfo=new JSONObject();
  	        		userInfo.put("id",user.getId());
  	        		userInfo.put("name",user.getAccount());
  	        		userInfo.put("roleName",user.getRole().getName());
  	        		userInfo.put("phone",user.getPhone());
  	        		userInfo.put("email",user.getEmail());
  	        		userInfo.put("description",user.getDescription());
  	        		userInfos.add(userInfo);
  	        	}
  	        	responseData.put("users", userInfos);
  	        }
  	        else{
  	        	err="当前数据库中不存在除超级管理员外的其他用户记录。";
  	        }
  	    }
  	    catch(Exception e){
  	    	err="下发请求中缺少必要的参数项，请核对后重发。";
  	    	e.printStackTrace();
  	    }
  		if(!statusCode){
  			responseData.put("err",err);
  		}
  		responseData.put("statusCode", statusCode);
  		response.setContentType("text/html;charset=utf-8");
  		PrintWriter out=response.getWriter();
  		out.println(responseData);
  		out.flush();
  		out.close();
    } 
    /**
	   * 修改用户
	   * @param name String 用户账号
	   * @param user JSONObject 包含
	   *    roleId long：角色标识<br/>
	   *    email String：账户邮箱<br/>
	   *    phone String：账户手机号<br/> 
	   *    description String：用户描述<br/>
	    @return JSONObject 包含<br/>
	   *     statusCode： 为boolean型，表明状态<br/>
	   *     err：String 表明失败原因<br/>
	   * **/
    @RequestMapping("user/alterUser")
    public void alterUser(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
		boolean statusCode=false;
		String err="";
		String account=request.getParameter("name");
    	if(account==null){
    		err="下发请求中缺少必要参数项\"name\"，请核对后重试。";
    	}else{
    		JSONObject userPara =JSONObject.fromObject(request.getParameter("user"));//读取信息
    		if(userPara==null){
    			err="下发请求中缺少必要参数项\"user\"，请核对后重试。";
    		}else{
    			User user=findService.findUserByAccount(account);
    			if(user!=null){
    				try{
    					user.setDescription(userPara.getString("description"));
    					user.setPhone(userPara.getString("phone"));
    					user.setEmail(userPara.getString("email"));
    					Role role=findService.findRole(userPara.getLong("roleId"));
    					if(role!=null){
    						user.setRole(role);
    					}
    					statusCode=alterService.alterUser(user);
    					if(!statusCode){
    	    				err="数据库操作异常，请稍后重试";
    	    			}
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    			}
    		}
    	}
		responseData.put("status", statusCode);
		if(!statusCode){
			responseData.put("err", err);
		}
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
    }  
    /**
	   * 删除用户
	   *@param id Long 用户标识
	   *@return JSONObject 包含
	   *     statusCode： 为boolean型，表明状态
	   *     err：String 表明失败原因
	   * **/
    @RequestMapping("user/delUser")
    public void delUser(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	Long id=null;
    	try{
    		id=Long.parseLong(request.getParameter("id"));
    	}catch(Exception e){
    		id=null;
    	} 
  	    boolean statusCode=false;
  	    String err="";
  	    JSONObject responseData=new JSONObject();
  	    if(id!=null){
  	    	 statusCode=delService.delUser(id);
  	  		if(!statusCode){
  	  			err="数据库操作异常，请稍后重试。";
  	  		}
  	    }else{
  	    	err="下发请求中缺少必要参数项\"id\"，请核对后重试。";
  	    }
  	    if(!statusCode){
  	    	responseData.put("err",err);
  	    }
  		responseData.put("statusCode", statusCode);
  		response.setContentType("text/html;charset=utf-8");
  		PrintWriter out=response.getWriter();
  		out.println(responseData);
  		out.flush();
  		out.close();
    }  
    /**找回密码-发送验证码**/ 
    @RequestMapping("account/getVerifyCode")
    public void getVerifyCode(HttpServletRequest request,HttpServletResponse response){
    	JSONObject responseData=new JSONObject();
    	boolean status=false;
    	String verifyWay=request.getParameter("verifyWay");
    	String desAddress="";
    	String account=request.getParameter("account");
    	User user=findService.findUserForLogin(account);
    	String code=NumConv.createVerifyCode(4);
    	String context="您正在尝试找回账号"+account+"的密码，本次验证码为："+code+"<br/>\n"+
    	               "请勿泄露验证码，如果不是您本人操作请忽略该信息";
    	System.out.println(context);
    	String info="";
    	if(verifyWay.equals("email")){//发送验证码至邮箱
    		desAddress=user.getEmail();
    		List<String> emails=new ArrayList<String>();
    		emails.add(desAddress);
    		status=MessageUtil.sendEmail(emails, "邮箱验证找回账号密码",context);
    		if(!status){//发送失败重发
    			status=MessageUtil.sendEmail(emails, "邮箱验证找回账号密码",context);
    			if(status){
    				info="验证消息已成功发送到您指定邮箱，请注意查收";
    			}
    			else{
    				info="发送失败，请确认您指定邮箱是否开启垃圾邮箱拦截";
    			}
    		}
    		else{
    			info="验证消息已成功发送到您指定邮箱，请注意查收";
    		}
    	}
    	else{//短信找回
    		List<String> phones=new ArrayList<String>();
    		phones.add(desAddress);
    		status=MessageUtil.sendMessage(phones, context);
    		desAddress=user.getPhone();
    		if(!status){//发送失败重发
    			status=MessageUtil.sendMessage(phones, context);
    			if(status){
    				info="验证消息已成功发送到您手机，请注意查收";
    			}
    			else{
    				info="发送失败，请确认短信猫或您账户是否正常";
    			}
    		}
    		else{
    			info="验证消息已成功发送到您手机，请注意查收";
    		}
    	}
    	if(status){
    			user.setVerifyCode(code);
    			status=alterService.alterUser(user);	
    	}
    	responseData.put("statusCode", status);
    	responseData.put("info", info);
    	response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
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
    /**找回密码-发送验证码
     * 核对验证码是否正确
     * **/ 
    @RequestMapping("account/checkVerifyCode")
    public void checkVerifyCode(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	JSONObject responseData=new JSONObject();
		boolean status=false;
    	String account=request.getParameter("account");
    	String code=request.getParameter("verifyCode");
    	User user=findService.findUserForLogin(account);
    	//System.out.println("account:"+account+"\t code:"+code);
    	String verifyCode=null;
    	if(user!=null&&(verifyCode=user.getVerifyCode())!=null){
    		if(verifyCode.equals(code)){
    			status=true;
    		}
    	}
    	responseData.put("statusCode", status);
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		//System.out.println("response:"+responseJson);
		out.flush();
		out.close();
	}  
    /**验证重设密码**/
    @RequestMapping("account/reSetPword")
    public void reSetPword(HttpServletRequest request,HttpServletResponse response) throws IOException{
   	   User user=findService.findUserForLogin(request.getParameter("account"));
   	   String pword=NumConv.passwordMD5(request.getParameter("pword"));
   	   boolean status=false;
   	   String err="";
   	   user.setPassword(pword);
       status=alterService.alterUser(user);
   		   if(!status){
   			   err="存储故障";
   		   }
   	   JSONObject responseData=new JSONObject();
   	   responseData.put("statusCode", status);
   	   responseData.put("err", err);
       response.setContentType("text/html");
   	   response.setCharacterEncoding("utf-8");
   	   PrintWriter out=response.getWriter();
   	   out.println(responseData);
   	   out.flush();
   	   out.close();
    }
}
