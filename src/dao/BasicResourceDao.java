package dao;

import java.util.List;

import domain.BasicResource;

public interface BasicResourceDao extends BaseDao<BasicResource>{
	/***分页查询基础信息
	 * @param title String 基信标题，为空字符串时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<BasicResource> findPagination(String title, int col, int page);

}
