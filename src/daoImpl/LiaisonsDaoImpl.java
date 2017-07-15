package daoImpl;

import java.util.List;

import dao.LiaisonsDao;
import domain.Liaisons;

public class LiaisonsDaoImpl extends BaseDaoImpl<Liaisons> implements LiaisonsDao{

	/**
	 * 分页查询人员信息
	 * @param unitId Long:人员组标识，为null时表示全部
	 * @param name String:人员名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<Liaison>
	 * ***/
	public List<Liaisons> findPagination(Long unitId, String name, int col, int page) {
		String sql="select * from Liaisons as liaison where liaison.name like '%"+name+"%'";
		if(unitId!=null){
			sql+=" and liaison.unit_id="+unitId+" order by id desc";
		}
		return findPagination(sql, Liaisons.class, page, col);
	}

	@Override
	public long countLiaisons(Long unitId, String name) {
		// TODO Auto-generated method stub
		String sql="select count(*) from Liaisons as liaison where liaison.name like '%"+name+"%'";
		if(unitId!=null){
			sql+=" and liaison.unit_id="+unitId+" order by id desc";
		}
		return countAmounts(sql);
	}

}
