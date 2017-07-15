package dao;

import java.util.List;

import domain.Role;

public interface RoleDao extends BaseDao<Role> {
   public List<Role> getAllRole();
   public Role getByName(String role);
   /**
	 * 分页查询人员信息
	 * @param unitId Long:人员组标识，为null时表示全部
	 * @param name String:人员名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<Liaison>
	 * ***/
   public List<Role> getPaginationRole(String name, int col, int page);
}
