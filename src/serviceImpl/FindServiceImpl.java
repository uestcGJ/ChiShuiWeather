

/**业务逻辑层：查询实体**/


package serviceImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import dao.*;
import domain.*;
import net.sf.json.JSONArray;
import service.FindService;

public class FindServiceImpl implements FindService{
	//注入bean
	private     AreaDao                 areaDao;                //00.注入对区域的操作
	private 	StationDao              stationDao;				//01.注入对局站的操作
	private     RoleDao           		roleDao;          		//02.注入对角色的操作 
	private     PermissionDao           permissionDao;          //03.注入对权限的操作   
	private     UserDao           		userDao;          		//04.注入对用户的操作 
    private     VulnerableDao           vulnerableDao;          //05.注入对易受灾点的操作 
    private     HourWeatherDao          hourWeatherDao;         //06.注入对天气的操作 
    private     LiaisonsDao             liaisonsDao;            //07.注入对联络人的操作 
    private     LiaisonUnitDao          liaisonUnitDao;         //08.注入对联络人组的操作 
    private     WarnStrategyDao         warnStrategyDao;        //09.注入对告警策略的操作
    private     WarnDao                 warnDao;                //10.注入对告警信息的操作 
    private     ProductDao              productDao;             //11.注入对产品的操作
    private     BasicResourceDao        basicResourceDao;       //12.注入对基础资源的操作
    private     SystemDao           	systemDao;           	//13.注入对系统信息资源的操作
	public void setAreaDao(AreaDao areaDao){
		this.areaDao=areaDao;
	}
	public void setStationDao(StationDao stationDao){
		this.stationDao = stationDao;
	}
	public void setRoleDao(RoleDao roleDao){
		this.roleDao=roleDao;
	} 
	public void setPermissionDao(PermissionDao permissionDao){
		this.permissionDao=permissionDao;
	}
	public void setUserDao(UserDao userDao){
		this.userDao=userDao;
	}
	public void setVulnerableDao(VulnerableDao vulnerableDao){
		this.vulnerableDao=vulnerableDao;
	}
	public void setHourWeatherDao(HourWeatherDao hourWeatherDao){
		this.hourWeatherDao=hourWeatherDao;
	}
	public void setLiaisonUnitDao(LiaisonUnitDao liaisonUnitDao){
		this.liaisonUnitDao=liaisonUnitDao;
	}
	public void setLiaisonsDao(LiaisonsDao liaisonsDao){
		this.liaisonsDao=liaisonsDao;
	}
	public void setWarnStrategyDao(WarnStrategyDao warnStrategyDao){
		this.warnStrategyDao=warnStrategyDao;
	}
	public void setWarnDao(WarnDao warnDao){
		this.warnDao=warnDao;
	}
	public void setProductDao(ProductDao productDao){
		this.productDao=productDao;
	}
	public void setBasicResourceDao(BasicResourceDao basicResourceDao){
		this.basicResourceDao=basicResourceDao;
	}
	public void setSystemDao(SystemDao systemDao){
		this.systemDao=systemDao;
	}
	
/***权限**/
    //查找全部权限
	public List<Permissions> findAllPermissions() {
		// TODO Auto-generated method stub
		return permissionDao.findAllEntities(Permissions.class);
	}
	public Permissions findPermission(Long id){
		return permissionDao.findEntity(Permissions.class, id);
	}
 /****用户***/
	//通过用户名查找用户
	public User findUserByAccount(String account) {
		// TODO Auto-generated method stub
		return userDao.getUserByAccount(account);
	}
	public User findUserForLogin(String account) {
		// TODO Auto-generated method stub
		return userDao.getUserForLogin(account);
	}
	public List<User> getPaginationUser(Long roleId,String name,int col,int page) {
		return userDao.getPaginationUser(roleId,name,col,page);
	}
	public long countUsers(Long roleId,String name) {
		 String sql="select count(*) from User where role_id !=1 and account like '%"+name+"%'";
		 if(roleId!=null){
			 sql+=" and role_id="+roleId;
		 }
		return userDao.countAmounts(sql);
   }
/***角色***/
	//查找所有角色
	public List<Role> findAllRoles(){
			// TODO Auto-generated method stub
		return roleDao.getAllRole();
	}
	public Role findRole(Long id){
		return roleDao.findEntity(Role.class, id);
	}
	public List<Role> getPaginationRole(String name,int col,int page){
		return roleDao.getPaginationRole(name,col,page);
	}
	//统计角色总数量
	public long countRoles(String name){
		return roleDao.countAmounts("select count(*) from Role where id!=1 and name !='超级管理员' and name like '%"+name+"%'");
	}
/******区域******/
//获取所有的区域
	public List<Area>findAllAreas(){
		return areaDao.findAllEntities(Area.class);
	}
 //通过id查找区域
	public Area findAreaById(Long id){
		return areaDao.findEntity(Area.class, id);
	}
//通过名称查找区域
	public Area findAreaByName(String name){
		return areaDao.findAreaByName(name);
	}
//通过编号查找区域
	public Area findAreaByCode(String code){
		return areaDao.findAreaByCode(code);
	}	
/**查询监测站**/
//根据局站标识(Stations-ID)查询局站
	public Stations findStationById(Long id){
		return stationDao.findEntity(Stations.class, id);
	}
//通过监测站点代号获取地区代号
    public String findAreaCodeByStationCode(String stationCode) {
		// TODO Auto-generated method stub
	 return stationDao.findAreaCode(stationCode);
   }
   public  String[] findStationNameAndAreaCodeByStationCode(String stationCode){
	   return stationDao.findNameAndAreaCodeByCode(stationCode);
   } 
//通过局站号查找局站
	public Stations findStationByStationCode(String stationCode){
		return stationDao.findByStationCode(stationCode);
	}    
 //查找所有监测站
  public List<Stations> findAllStations(String areaCode) {
	// TODO Auto-generated method stub
	return stationDao.findAllStations(areaCode);
  }
//分页获取监测站概要
  public List<Stations> findPaginationStationOutline(int col,int page,String name){
	  return stationDao.findPaginationOutline(col, page,name);
  }
public //获取监测站条目数量
  long getStationItemSize(String name){
	  return stationDao.countAmounts(name);
  }
/****易受灾站点****/ 
//查找全部易受灾站点
  public List<Vulnerable> findAllVulnerable(){
	  return vulnerableDao.findAllEntities(Vulnerable.class);
  }
  public List<Vulnerable> findPaginationVulners(String title,int col,int page){
	  return vulnerableDao.findPaginationVulners(title,col,page);
  }
  public long getVulnersCount(String title){
	  String sql="select count(*) from Vulnerable where name like '%"+title+"%'";
	  return vulnerableDao.countAmounts(sql);
  }
  @Override
  public Vulnerable findVulnerById(Long id) {
		// TODO Auto-generated method stub
		return vulnerableDao.findEntity(Vulnerable.class, id);
  }
 // 查找过去24小时的天气信息
  public List<HourWeather> findLastDayWeatherData(String date){
	  return hourWeatherDao.findLastDayData(date);
  }
 @Override
  public List<HourWeather> findHourWeatherData(String stationCode, int hour) {
	// TODO Auto-generated method stub
	return hourWeatherDao.findHourData(stationCode, hour);
  }
  @Override
  public HourWeather findHourWeatherByStationCodeAndDate(String stationCode, Timestamp stamp) {
	 // TODO Auto-generated method stub
	 return hourWeatherDao.findByStationCodeAndDate(stationCode, stamp);
  }
  @Override
  /***
	 * 通过监测站代号和天气要素查询该要素<br/>
	 * 返回格式为：stationCode：局站代号 value:List,按查询要素的先后顺序排列
	 * **/
  public List<Map<String,Object>> findWeatherInfoByAreaCodeAndDate(String areaCode, Timestamp stamp, String param) {
	 // TODO Auto-generated method stub
	 return hourWeatherDao.findInfosByAreaCodeAndDate(areaCode, stamp, param);
  }
  public Map<String,Object> findInfosByStationaCodeAndDate(String stationCode, Timestamp stamp,String param){
	  return hourWeatherDao.findInfosByStationaCodeAndDate(stationCode, stamp, param);
  }
  public List<HourWeather> getVagueStationWeather(String areaCode,String stationName,Timestamp stamp,int col,int currentPage){
	  return hourWeatherDao.getVagueStationWeather(areaCode, stationName, stamp, col, currentPage);
  }
  public long getVagueStationWeatherCount(String areaCode,String stationName,Timestamp stamp){
	  return hourWeatherDao.getVagueStationWeatherCount(areaCode, stationName, stamp);
  }
  @Override
  public JSONArray findInfoByAreaCodeAndDate(String areaCode, String item) {
  	// TODO Auto-generated method stub
  	return hourWeatherDao.findInfoByAreaCodeAndDate(areaCode,item);
  }
  //通过地区编号、时间以及列数和当前页数分页查询实况信息   
  public List<HourWeather> findPaginationWeatherInfo(String areaCode, Timestamp stamp,int col,int currentPage){
	 return hourWeatherDao.findPagination(areaCode, stamp,col,currentPage);
  }
  public List<Map<String,Object>> findStationsLastWeatherInfos(String stationCode,String item){
	  return hourWeatherDao.findStationsLastItem(stationCode, item);
  }
 // 获取总记录条数
  public long getWeatherItemSize(String areaCode,Timestamp stamp) {
	return hourWeatherDao.getItemSize(areaCode, stamp);
  }
  public float findLastTimeTempItem(String stationCode,int intervalHour,String item){
	  return hourWeatherDao.findLastTimeTempItem(stationCode, intervalHour, item);
  }
  @Override
  public LiaisonUnit findLiaisonUnitById(Long id) {
	  // TODO Auto-generated method stub
	  return liaisonUnitDao.findEntity(LiaisonUnit.class, id);
  }
  @Override
  public Liaisons findLiaisonById(Long id) {
	  // TODO Auto-generated method stub
	  return liaisonsDao.findEntity(Liaisons.class, id);
  }
  public List<Liaisons>findPaginationLiaison(Long unitId,String name,int col,int page){
	  return liaisonsDao.findPagination( unitId, name, col, page);
  }
  public long countLiaisons(Long unitId,String name){
	  return liaisonsDao.countLiaisons(unitId,name);
  }
  public List<LiaisonUnit> findPaginationLiaisonUnit(String name,int col,int page){
	  return liaisonUnitDao.findPaginationUnit(name, col, page);
  }
  public long countLiaisonUnit(String name){
	  return liaisonUnitDao.getUnitCount(name);
  }
  @Override
  public WarnStrategy findWarnStrategyById(Long id) {
	  // TODO Auto-generated method stub
	  return warnStrategyDao.findEntity(WarnStrategy.class, id);
  }
  @Override
  public List<LiaisonUnit> findAllLiaisonUnits() {
	  // TODO Auto-generated method stub
	  return liaisonUnitDao.findAllEntities(LiaisonUnit.class);
  }
  @Override
  public List<WarnStrategy> findAllWarnStrategy() {
	  // TODO Auto-generated method stub
	  return warnStrategyDao.findAllEntities(WarnStrategy.class);
  }
  public List<WarnStrategy> findPaginationWarnStrategy(int col,int currentPage,String name){
	  return warnStrategyDao.findPagination(col, currentPage,name);
  }
  @Override
  public long getWarnStrategyAmount(String name) {
	// TODO Auto-generated method stub
	 return warnStrategyDao.getAmount(name);
  }
  @Override
  public List<WarnStrategy> findWarnStrategyByName(String name) {
	 // TODO Auto-generated method stub
	 return warnStrategyDao.findByName(name);
  }
 @Override
 public List<Warn> findPaginationWarns(Boolean status, int col, int page) {
	// TODO Auto-generated method stub
	return warnDao.findPaginationWarns(status, col, page);
 }
 @Override
 public long getWarnCount(Boolean status) {
		// TODO Auto-generated method stub
	return warnDao.getWarnCount(status);
 }
 @Override
 public Warn findWarnById(long id) {
	// TODO Auto-generated method stub
	return warnDao.findEntity(Warn.class, id);
 }
 @Override
 public List<Product> findPaginationProducts(String type, int col, int page) {
	// TODO Auto-generated method stub
	return productDao.findPagination(type,col,page);
 }
 @Override
 public long getProductCount(String type) {
	// TODO Auto-generated method stub
	 String sql="select count(*) from Product where  type like '%"+type+"%'";
	return productDao.countAmounts(sql);
 }
 @Override
 public Product findProductById(long id) {
	// TODO Auto-generated method stub
	 return productDao.findEntity(Product.class, id);
 }
@Override
public List<BasicResource> findPaginationBasicResources(String title, int col, int page) {
	// TODO Auto-generated method stub
	return basicResourceDao.findPagination(title,col,page);
}
@Override
public long getBasicResourceCount(String title) {
	// TODO Auto-generated method stub
	 String sql="select count(*) from Basic_Resource where title like '%"+title+"%'";
	return basicResourceDao.countAmounts(sql);
}
@Override
public BasicResource findBasicResourceById(long id) {
	// TODO Auto-generated method stub
	return basicResourceDao.findEntity(BasicResource.class, id);
}
@Override
public List<Product> findLastProduct(Long id, String type) {
	// TODO Auto-generated method stub
	return productDao.findLastProduct(id,type);
}
@Override
public SystemInfo findSystemInfoById(long id) {
	// TODO Auto-generated method stub
	return systemDao.findEntity(SystemInfo.class, id);
}

	
}
