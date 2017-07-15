package daoImpl;
import java.io.Serializable;
import java.util.List;
import dao.StationDao;
import domain.Stations;

public class StationDaoImpl extends BaseDaoImpl<Stations> implements StationDao{
//根据区域(Areas)ID查询局站
	public List<Stations> findAllByAreaId(Serializable id){
		String sql = " select stations from Stations as stations where stations.area.id = ?0 ";
		return findMulti(sql,id);
	}

	@Override
	public String findAreaCode(String stationCode) {
		// TODO Auto-generated method stub
		 String sql="select area_code from Stations  where code= '"+stationCode+"'";
			
		return (String) findFieldInSQL(sql)[0];
	}
	public String findStationName(String stationCode){
		String sql="select station_name from Stations  where code= '"+stationCode+"'";
		
		return (String) findFieldInSQL(sql)[0];
	}
	public String[] findNameAndAreaCodeByCode(String stationCode){
       String sql="select name,area_code from Stations  where code= '"+stationCode+"'";
		Object[] result=findFieldInSQL(sql);
		String[] nameAndCode=new String[2];
		if(result!=null){
			nameAndCode[0]=(String)result[0];
			nameAndCode[1]=(String)result[1];
		}
		return nameAndCode;
	}
	public Stations findByStationCode(String stationCode){
		String sql="select * from Stations where code='"+stationCode+"'";
		return findOneInSQL(sql,Stations.class);
	}
	/***分页查询监测站**/
	public List<Stations> findPaginationOutline(int col,int page,String name){
		String sql="select * from Stations where name like '%"+name+"%' order by id desc";
		return findPagination(sql,Stations.class,page,col);
	}
	@Override
	public long countAmounts(String name) {
		// TODO Auto-generated method stub
		 String sql=" select count(*) from Stations where name like '%"+name+"%'";
		 return super.countAmounts(sql);
	}

	@Override
	public List<Stations> findAllStations(String areaCode) {
		// TODO Auto-generated method stub
		String sql="select * from Stations where area_code like '%"+areaCode+"%' order by id desc";
		return super.findSQL(sql, Stations.class);
	}
}
