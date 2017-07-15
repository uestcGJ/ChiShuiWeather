package serviceImpl;

import java.io.Serializable;
import java.util.Map;

import dao.*;
import domain.*;
import service.AlterService;

public class AlterServiceImpl implements AlterService{
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
	
/*************************/
/***修改区域**/	
	public boolean alterArea(Area area){
		try{
			areaDao.addEntity(area);
			return true;
		}catch(Exception e){
			return false;
		}
	}
/**修改局站**/
//修改局站(Stations)
	public boolean alterStation(Stations station){
		try{
			stationDao.alterEntity(station);
			return true;
		}catch(Exception e){
			return false;
		}
	}
//根据字段修改局站(Stations)
	public boolean updateStation(Serializable id,Map<String , Object> parameterMap){
		try{
			stationDao.updateEntity(Stations.class, id, parameterMap);
			return true;
		}catch(Exception e){
			return false;
		}
	}
/*************************/	
//修改角色
	public boolean alterRole(Role role){
		try{
			roleDao.alterEntity(role);
			return true;
		}catch(Exception e){
			return false;
		}
	}
//修改用户
	public boolean alterUser(User user){
		try{
			userDao.alterEntity(user);
			return true;
		}catch(Exception e){
			return false;
		}
	}
//修改权限
	public boolean alterPermission(Permissions permission){
		try{
			permissionDao.alterEntity(permission);
			return true;
		}catch(Exception e){
			return false;
		}
	}
/***易受灾点***/	
	public boolean alterVulnerable(Vulnerable vulnerable){
		try{
			vulnerableDao.alterEntity(vulnerable);
			return true;
		}catch(Exception e){
			return false;
		}
	}
/***修改小时天气**/	
	public boolean alterHourWeather(HourWeather hw){
		try{
			hourWeatherDao.alterEntity(hw);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterLiaisonUnit(LiaisonUnit liaisonUnit) {
		// TODO Auto-generated method stub
		try{
			liaisonUnitDao.alterEntity(liaisonUnit);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterLiaison(Liaisons liaison) {
		// TODO Auto-generated method stub
		try{
			liaisonsDao.alterEntity(liaison);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterWarnStrategy(WarnStrategy warnStrategy) {
		// TODO Auto-generated method stub
		try{
			warnStrategyDao.alterEntity(warnStrategy);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterWarn(Warn warn) {
		// TODO Auto-generated method stub
		try{
			warnDao.alterEntity(warn);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterProduct(Product product) {
		// TODO Auto-generated method stub
		try{
			productDao.alterEntity(product);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean alterBasicResource(BasicResource basicResource) {
		// TODO Auto-generated method stub
		try{
			basicResourceDao.alterEntity(basicResource);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean alterSystemInfo(SystemInfo systemInfo) {
		// TODO Auto-generated method stub
		try{
			systemDao.alterEntity(systemInfo);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}	
}