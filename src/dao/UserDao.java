package dao;

import java.util.List;

import domain.User;

public interface UserDao extends BaseDao<User> {
//通过角色查找用户	
   public List<User> findUsersByRoleId(Long id);
//通过用户名查找用户
   public User getUserByAccount(String account);
   /**
	 * 分页查询用户信息
	 * @param roleId Long:角色标识，为null时表示全部
	 * @param name String:账户名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<User>
	 * ***/
   public List<User> getPaginationUser(Long roleId, String name, int col, int page);
   public User getUserForLogin(String account);
}
