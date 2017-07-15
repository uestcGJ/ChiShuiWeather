package dao;

import java.util.List;

import domain.Warn;

public interface WarnDao extends BaseDao<Warn> {
	/***分页查询预警信息
	 * @param status Boolean 预警是否启用，为null时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<Warn> findPaginationWarns(Boolean status,int col,int page);
	
	long getWarnCount(Boolean status);
}
