package daoImpl;

import java.util.List;

import dao.BasicResourceDao;
import domain.BasicResource;

public class BasicResourceDaoImpl extends BaseDaoImpl<BasicResource> implements BasicResourceDao{

	@Override
	public List<BasicResource> findPagination(String title, int col, int page) {
		// TODO Auto-generated method stub
		String sql="select * from Basic_Resource where title like '%"+title+"%' order by id desc";
		return super.findPagination(sql, BasicResource.class, page, col);
	}

}
