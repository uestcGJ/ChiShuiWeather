package dao;

import java.util.List;

import domain.Liaisons;

public interface LiaisonsDao extends BaseDao<Liaisons> {
	/**
	 * 分页查询人员信息
	 * @param unitId Long:人员组标识，为null时表示全部
	 * @param name String:人员名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<Liaison>
	 * ***/
	List<Liaisons> findPagination(Long unitId, String name, int col, int page);


	long countLiaisons(Long unitId, String name);

}
