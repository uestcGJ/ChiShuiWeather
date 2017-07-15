

/**业务逻辑层：添加实体**/


package serviceImpl;

import java.io.Serializable;
import dao.*;
import domain.*;
import service.AddService;

public class AddServiceImpl implements AddService{
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
	
/***区域**/	
	public Serializable addArea(Area area){
		return areaDao.addEntity(area);
	}
//新增局站(Stations)
	public Serializable addStation(Stations station){
		return stationDao.addEntity(station);
	}
//新增角色
	public Serializable addRole(Role role){
		return roleDao.addEntity(role);
	}
//新增权限
	public Serializable addPermission(Permissions permission){
		return permissionDao.addEntity(permission);
	}
//新增用户
	public Serializable addUser(User user){
		return userDao.addEntity(user);
	}
//新增易受灾点
	public Serializable addVulnerable(Vulnerable vulnerable){
		return vulnerableDao.addEntity(vulnerable);
	}
//	
	@Override
	public synchronized Serializable addHourWeather(HourWeather hourWeather) {
		// TODO Auto-generated method stub
		return hourWeatherDao.addEntity(hourWeather);
	}
	@Override
	public Serializable addLiaison(Liaisons liaison) {
		// TODO Auto-generated method stub
		return liaisonsDao.addEntity(liaison);
	}
	@Override
	public Serializable addLiaisonUnit(LiaisonUnit liaisonUnit) {
		// TODO Auto-generated method stub
		return liaisonUnitDao.addEntity(liaisonUnit);
	}
	@Override
	public Serializable addWarnStrategy(WarnStrategy warnStrategy) {
		// TODO Auto-generated method stub
		return warnStrategyDao.addEntity(warnStrategy);
	}
	@Override
	public  Serializable addWarn(Warn warn) {
		synchronized(this){
			// TODO Auto-generated method stub
			return warnDao.addEntity(warn);
		}	
	}
	@Override
	public Serializable addProduct(Product product) {
		// TODO Auto-generated method stub
		return productDao.addEntity(product);
	}
	@Override
	public Serializable addBasicResource(BasicResource basicResource) {
		// TODO Auto-generated method stub
		return basicResourceDao.addEntity(basicResource);
	}
	@Override
	public Serializable addSystemInfo(SystemInfo systemInfo) {
		// TODO Auto-generated method stub
		return systemDao.addEntity(systemInfo);
	}
}
