package service;


public interface DeleteService {

	boolean delArea(Long id);
	boolean delStation(Long id);
	boolean delRole(Long id);
	boolean delPermission(Long id);
	boolean delUser(Long id);
	public boolean delVulnarable(Long id);
	public boolean delHourWeather(Long id);
	public boolean delLiaisonUnit(Long id);
	public boolean delLiaison(Long id);
	public boolean delWarnStrategy(Long id);
	boolean delWarn(Long id);
	boolean delProduct(Long id);
	boolean delBasicResource(Long id);
}
