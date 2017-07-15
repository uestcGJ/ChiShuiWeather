package serviceImpl;

import dao.*;
import domain.*;
import service.DeleteService;

public class DeleteServiceImpl implements DeleteService{
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
    @SuppressWarnings("unused")
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
	
	public boolean delArea(Long id){
		try{
			areaDao.deleteEntity(Area.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除局站(Stations)
	public boolean delStation(Long id) {
		try{
			stationDao.deleteEntity(Stations.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除角色
	public boolean delRole(Long id) {
		try{
			roleDao.deleteEntity(Role.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除权限
	public boolean delPermission(Long id) {
		try{
			permissionDao.deleteEntity(Permissions.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除用户	
	public boolean delUser(Long id) {
		try{
			userDao.deleteEntity(User.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除易受灾点	
	public boolean delVulnarable(Long id) {
		try{
			vulnerableDao.deleteEntity(Vulnerable.class, id);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
//删除小时天气信息
	public boolean delHourWeather(Long id) {
		try{
			hourWeatherDao.deleteEntity(HourWeather.class, id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean delLiaisonUnit(Long id) {
		// TODO Auto-generated method stub
		try{
			liaisonUnitDao.deleteEntity(LiaisonUnit.class, id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean delLiaison(Long id) {
		// TODO Auto-generated method stub
		try{
			liaisonsDao.deleteEntity(Liaisons.class, id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean delWarnStrategy(Long id) {
		// TODO Auto-generated method stub
		try{
			warnStrategyDao.deleteEntity(WarnStrategy.class, id);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	@Override
	public boolean delWarn(Long id) {
		// TODO Auto-generated method stub
		try{
			warnDao.deleteEntity(Warn.class, id);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean delProduct(Long id) {
		// TODO Auto-generated method stub
		try{
			productDao.deleteEntity(Product.class, id);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean delBasicResource(Long id) {
		try{
			basicResourceDao.deleteEntity(BasicResource.class, id);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
