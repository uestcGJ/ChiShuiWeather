package daoImpl;

import java.util.List;

import dao.RoleDao;
import domain.Role;

public class RoleDaoImpl extends BaseDaoImpl<Role> implements RoleDao{
	 public Role getByName(String role){
		String sql =" select role from Role as role where role.name = ?0 ";
		return findOne(sql,role);
	  }
	 public List<Role> getAllRole(){
			String sql =" select * from Role  where  id!=1 and name !='超级管理员'";
			return super.findSQL(sql, Role.class);
     }
	 /**
		 * 分页查询人员信息
		 * @param unitId Long:人员组标识，为null时表示全部
		 * @param name String:人员名称，为空字符串时查询所有
		 * @param col int:表格列数
		 * @param page int:当前页码
		 * @return List<Liaison>
		 * ***/
	public List<Role> getPaginationRole(String name, int col, int page) {
		// TODO Auto-generated method stub
		String sql =" select * from Role  where id!=1 and name !='超级管理员' and name like '%"+name+"%'";
		return findPagination(sql, Role.class, page, col);
	}
}
