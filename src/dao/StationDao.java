package dao;

import java.io.Serializable;
import java.util.List;

import domain.Stations;

public interface StationDao extends BaseDao<Stations>{
//根据区域(Areas)ID查询局站
	List<Stations> findAllByAreaId(Serializable id);	
//根据局站号查找局站的区域号
	String findAreaCode(String stationCode);
	String findStationName(String stationCode);
	Stations findByStationCode(String stationCode);
	String[] findNameAndAreaCodeByCode(String stationCode);
	List<Stations> findPaginationOutline(int col,int page,String name);
	List<Stations> findAllStations(String areaCode);
}
