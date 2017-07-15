package daoImpl;

import java.util.List;

import dao.VulnerableDao;
import domain.Vulnerable;

public class VulnerableDaoImpl extends BaseDaoImpl<Vulnerable> implements VulnerableDao {

	@Override
	public List<Vulnerable> findPaginationVulners(String title, int col, int page) {
		 String sql="select * from Vulnerable where name like '%"+title+"%' order by id desc";
		return super.findPagination(sql, Vulnerable.class, page, col);
	}
  
}
