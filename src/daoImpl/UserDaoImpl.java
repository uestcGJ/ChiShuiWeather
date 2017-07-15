package daoImpl;

import java.util.List;

import dao.UserDao;
import domain.User;

public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {
	//通过角色查找用户	
	   public List<User> findUsersByRoleId(Long id){
		   String sql=" select user from User as user where user.role.id = ?0 ";
		   return findMulti(sql,id);
	   }
	 //通过用户名查找用户
	   public User getUserByAccount(String account){
		   String sql="select user from User as user where role_id !=1 and user.account = ?0 ";
		   return findOne(sql,account);
	   }
	   public User getUserForLogin(String account){
		   String sql="select user from User as user where user.account = ?0 ";
		   return findOne(sql,account);
	   }
	  @Override
	  public List<User> getPaginationUser(Long roleId, String name, int col, int page) {
		  String sql="select * from User where role_id !=1 and account like '%"+name+"%'";
			 if(roleId!=null){
				 sql+=" and role_id="+roleId;
			 } 
		 return super.findPagination(sql, User.class, page, col);
	 }
}
