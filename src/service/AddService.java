package service;

import java.io.Serializable;

import domain.Area;
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

public interface AddService{
/***区域**/
	public Serializable addArea(Area area);
	
//新增局站(Stations)
	public Serializable addStation(Stations station);

	public Serializable addPermission(Permissions permission);

	public Serializable addRole(Role superAdmin);

	public Serializable addUser(User admin);
	public Serializable addVulnerable(Vulnerable vulnerable);
	
	public Serializable addHourWeather(HourWeather hourWeather);
	public Serializable addLiaison(Liaisons liaison);
	public Serializable addLiaisonUnit(LiaisonUnit liaisonUnit);
	public Serializable addWarnStrategy(WarnStrategy warnStrategy);
	public Serializable addWarn(Warn warn);
	public Serializable addProduct(Product product);
	public Serializable addBasicResource(BasicResource basicResource);
	public Serializable addSystemInfo(SystemInfo systemInfo);
}