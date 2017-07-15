package daoImpl;

import java.util.List;

import dao.WarnStrategyDao;
import domain.WarnStrategy;

public class WarnStrategyDaoImpl extends BaseDaoImpl<WarnStrategy> implements WarnStrategyDao{

	@Override
	public List<WarnStrategy> findPagination(int col,int currentPage,String name) {
		// TODO Auto-generated method stub
		String sql="select *  from warnstrategy where name like '%"+name+"%' order by id desc";
		return  findPagination(sql,WarnStrategy.class,currentPage,col);
	}

	@Override
	public long getAmount(String name) {
		// TODO Auto-generated method stub
		 String sql=" select count(*) from warnstrategy where name like '%"+name+"%' order by id desc";
		 return countAmounts(sql);
	}

	@Override
	public List<WarnStrategy> findByName(String name) {
		//直接利用sql语句进行查询
		String sql="select * from warnstrategy as strategy where strategy.name like '%"+name+"%' order by id desc";
		return findSQL(sql,WarnStrategy.class);
	}
    
}
