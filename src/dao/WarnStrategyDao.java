package dao;

import java.util.List;

import domain.WarnStrategy;

public interface WarnStrategyDao extends BaseDao<WarnStrategy>{
	List<WarnStrategy> findPagination(int col,int currentPage,String name);
	public long getAmount(String name);
	List<WarnStrategy> findByName(String name);
}
