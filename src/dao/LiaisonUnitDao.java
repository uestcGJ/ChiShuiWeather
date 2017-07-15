package dao;

import java.util.List;

import domain.LiaisonUnit;

public interface LiaisonUnitDao extends BaseDao<LiaisonUnit>{
	/**
	 * 分页查询人员组信息
	 * @param name String:人员组名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<LiaisonUnit>
	 * ***/
    List<LiaisonUnit> findPaginationUnit(String name,int col,int page);
    /***
     * 获取人员组总记录数
     * @param name String:人员组名称，为空字符串时查询所有
     * @return long
     * **/
    long getUnitCount(String name);
}
