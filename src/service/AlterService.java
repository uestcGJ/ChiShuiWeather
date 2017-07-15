package service;

import java.io.Serializable;
import java.util.Map;

import domain.BasicResource;
import domain.HourWeather;
import domain.LiaisonUnit;
import domain.Liaisons;
import domain.Permissions;
import domain.Product;
import domain.Role;
import domain.Stations;
import domain.SystemInfo;
import domain.User;
import domain.Vulnerable;
import domain.Warn;
import domain.WarnStrategy;

public interface AlterService {
	
/**修改局站**/
//修改局站(Stations)
	boolean alterStation(Stations station);
//根据字段修改局站
	boolean updateStation(Serializable id,Map<String , Object> parameterMap);	
/***********/
	boolean alterRole(Role role);
	boolean alterUser(User user);
	boolean alterPermission(Permissions permission);
	public boolean alterVulnerable(Vulnerable vulnerable);
	public boolean alterHourWeather(HourWeather hourWeather);
	public boolean alterLiaisonUnit(LiaisonUnit liaisonUnit);
	public boolean alterLiaison(Liaisons liaison);
	public boolean alterWarnStrategy(WarnStrategy warnStrategy);
	boolean alterWarn(Warn warn);
	boolean alterProduct(Product product);
	boolean alterBasicResource(BasicResource basicResource);
	boolean alterSystemInfo(SystemInfo systemInfo);
}
