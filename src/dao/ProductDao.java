package dao;

import java.util.List;

import domain.Product;

public interface ProductDao extends BaseDao<Product>{
	/***分页查询产品信息
	 * @param type String 产品类型，为空字符串时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<Product> findPagination(String type, int col, int page);

	List<Product> findLastProduct(Long id, String type);


}
