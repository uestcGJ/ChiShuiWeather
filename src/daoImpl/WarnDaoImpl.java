package daoImpl;

import java.util.List;

import dao.WarnDao;
import domain.Warn;


public class WarnDaoImpl extends BaseDaoImpl<Warn> implements WarnDao{

	@Override
	public List<Warn> findPaginationWarns(Boolean status, int col, int page) {
		String sql="select * from warn as warn ";
		if(status!=null){
			sql+="where warn.status="+(status?1:0);
		}
		sql+=" order by id desc";
		return findPagination(sql,Warn.class,page,col);
	}
	public long getWarnCount(Boolean status){
		String sql="select count(*)from warn as warn ";
		if(status!=null){
			sql+="where warn.status="+(status?1:0);
		}
		sql+=" order by id desc";
		return countAmounts(sql);
	}
}
