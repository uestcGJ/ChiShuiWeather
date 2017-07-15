package daoImpl;

import java.util.List;

import dao.LiaisonUnitDao;
import domain.LiaisonUnit;

public class LiaisonUnitDaoImpl extends BaseDaoImpl<LiaisonUnit> implements LiaisonUnitDao{

	@Override
	public List<LiaisonUnit> findPaginationUnit(String name, int col, int page) {
		// TODO Auto-generated method stub
		String sql="select * from LiaisonUnit where name like '%"+name+"%' order by id desc";
		return findPagination(sql, LiaisonUnit.class, page, col);
	}
	@Override
	public long getUnitCount(String name) {
		// TODO Auto-generated method stub
		String sql="select count(*) from LiaisonUnit where name like '%"+name+"%' order by id desc";
		return countAmounts(sql);
	}
}
