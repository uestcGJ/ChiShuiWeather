package daoImpl;

import dao.AreaDao;
import domain.Area;

public class AreaDaoImpl  extends BaseDaoImpl<Area> implements AreaDao {

	@Override
	public Area findAreaByName(String name) {
		String sql="select area from Area as area  where area.name=?0";
		return findOne(sql,name);
	}

	@Override
	public Area findAreaByCode(String code) {
		String sql="select area  from  Area as area where area.code=?0";
		return findOne(sql,code);
	}

}
