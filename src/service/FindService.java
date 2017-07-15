package service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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
import net.sf.json.JSONArray;

public interface FindService {	
/************/
	public Permissions findPermission(Long id);
	public List<Permissions> findAllPermissions();
    public User findUserByAccount(String account);
    /**
	 * 分页查询用户信息
	 * @param roleId Long:角色标识，为null时表示全部
	 * @param name String:账户名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<User>
	 * ***/
    List<User> getPaginationUser(Long roleId,String name,int col,int page);
    public long countUsers(Long roleId,String name);
    public List<Role> findAllRoles();	
/***角色***/
	public Role findRole(Long id);
	/**
	 * 分页查询角色信息
	 * @param name String:角色名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<Liaison>
	 * ***/
	List<Role> getPaginationRole(String name,int col,int page);
	//统计角色总数量
	public long countRoles(String name);
/**区域***/
 //获取所有的区域
	public List<Area>findAllAreas();
 //通过id查找区域
	public Area findAreaById(Long id);
//通过名称查找区域
	public Area findAreaByName(String name);
//通过编号查找区域
	public Area findAreaByCode(String code);	
/**查询局站**/
//根据局站标识(Stations-ID)查询局站
	public Stations findStationById(Long id);
//通过监测站点代号获取地区代号
	public String findAreaCodeByStationCode(String stationCode);
	
	 String[] findStationNameAndAreaCodeByStationCode(String stationCode);
//查询所有局站
	public List<Stations> findAllStations(String areaCode);
//通过局站号查找局站
	public Stations findStationByStationCode(String stationCode);
//分页获取监测站概要
	public List<Stations> findPaginationStationOutline(int col,int page,String name);
	long getStationItemSize(String name);
//查找全部易受灾点    
    public List<Vulnerable> findAllVulnerable();  
    public Vulnerable findVulnerById(Long id);
    /***
     * 通过标题、表格列数以及当前页码分页查找易受灾点信息
     * @param title String :易受灾点标题，空字符串表示全部
     * @param col int:表格列数
     * @param page int:当前页码
     * @return List<Vulnerable>
     * **/
    List<Vulnerable> findPaginationVulners(String title,int col,int page);
    long getVulnersCount(String title);
    /**根据局站码查找24小时天气情况
	 * @param stationCode String 局站代码
	 * */
    public List<HourWeather> findLastDayWeatherData(String date);
	/***根据局站码和时间间隔查找指定时间前的天气信息
	 * @param stationCode 局站代码
	 * @param hour int 间隔小时数
	 * ***/
    public List<HourWeather> findHourWeatherData(String stationCode,int hour);
    /**通过区域编号、局站名称查询实况
     * @param areaCode 区域标识，当为空字符时查询全部
     * @param stationName 监测站名称 当为空时查询全部
     * @param stamp 时间
     * @param col 表格的行数
     * @param currentPage 当前页码
     * ***/
    public List<HourWeather> getVagueStationWeather(String areaCode,String stationName,Timestamp stamp,int col,int currentPage);
    /***统计模糊查询时的数量***/
    public long getVagueStationWeatherCount(String areaCode,String stationName,Timestamp stamp);
    public HourWeather findHourWeatherByStationCodeAndDate(String stationCode, Timestamp stamp);
    /***
     * 通过区域代号、时间以及参数，获取当前区域下所有站点的天气信息，用于绘制分布图
     * @param areaCode 区域代号
     * @param stamp Timestamp查询的时间
     * @param param String 要查询的参数类型，与数据库中对应参数的字段名相同，如rainfall_12h 表示查询过去12小时雨量
     * 
     * ***/  
    public List<Map<String,Object>> findWeatherInfoByAreaCodeAndDate(String areaCode, Timestamp stamp,String param);
    /***
     * 通过监测站代号、时间以及参数，查找天气要素
     * @param stationCode 监测站代号
     * @param stamp Timestamp 查询的时间
     * @param param String 要查询的参数类型，与数据库中对应参数的字段名相同，多个参数之间用逗号分隔
     * @return Map<String,Object>  为各要素的key-value
     * 
     * ***/  
    public float findLastTimeTempItem(String stationCode,int intervalHour,String item); 
    /**
	 * 通过局站编号和时间间隔、温度类型查询温度值
	 * @param stationCode String 局站代码
	 * @param intervalHour int 间隔的小时数
	 * @param item String 要素，high、low、ave
	 * @return float 查询的值
	 * */
	
    Map<String,Object> findInfosByStationaCodeAndDate(String stationCode, Timestamp stamp,String param);
	/**
	 * 通过局站编号和参数类型获取天气参数，天气参数只有一项，监测站可能为多个，各监测站编号之间通过|分隔
	 * @param stationCode String 局站代码
	 * @param item String 要素
	 * @return List<Map<String,Object>> 每个Map中包含：<br/>
	 *   stationName:监测站名称<br/>
	 *   value：查询的参数值
	 * */
	public List<Map<String,Object>> findStationsLastWeatherInfos(String stationCode,String item);
 //通过地区编号、时间以及列数和当前页数分页查询实况信息   
    public List<HourWeather> findPaginationWeatherInfo(String areaCode, Timestamp stamp,int col,int currentPage);
 //获取总记录条数
	public long getWeatherItemSize(String areaCode,Timestamp stamp);
	
    /******人员相关*******/
	public LiaisonUnit findLiaisonUnitById(Long id);
	public Liaisons findLiaisonById(Long id);
	/**
	 * 分页查询人员信息
	 * @param unitId Long:人员组标识，为null时表示全部
	 * @param name String:人员名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<Liaison>
	 * ***/
	public List<Liaisons>findPaginationLiaison(Long unitId,String name,int col,int page);
	
	public long countLiaisons(Long unitId,String name);
	public List<LiaisonUnit> findAllLiaisonUnits();
	/**
	 * 分页查询人员组信息
	 * @param name String:人员组名称，为空字符串时查询所有
	 * @param col int:表格列数
	 * @param page int:当前页码
	 * @return List<LiaisonUnit>
	 * ***/
	public List<LiaisonUnit> findPaginationLiaisonUnit(String name,int col,int page);
	public long countLiaisonUnit(String name);
    /*****预警策略相关*******/
	public List<WarnStrategy> findAllWarnStrategy();
	public WarnStrategy findWarnStrategyById(Long id);
	/***
	 * 分页查询预警策略
	 * @param col:int 每页的记录数
	 * @param currentPage:当前页码数
	***/
	public List<WarnStrategy> findPaginationWarnStrategy(int col,int currentPage,String name);
	//获取预警策略的总条目数
	public long getWarnStrategyAmount(String name);
	public List<WarnStrategy> findWarnStrategyByName(String name);
	public JSONArray findInfoByAreaCodeAndDate(String areaCode, String item);
	
	/***分页查询预警信息
	 * @param status Boolean 预警是否启用，为null时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<Warn> findPaginationWarns(Boolean status,int col,int page);
	/***统计告警数量**/
	long getWarnCount(Boolean status);
	/**通过告警标识查询告警*/
	Warn findWarnById(long id);
	/***分页查询产品信息
	 * @param type String 产品类型，为空字符串时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<Product> findPaginationProducts(String type,int col,int page);
	/***统计产品数量**/
	long getProductCount(String type);
	/**通过标识查询产品*/
	Product findProductById(long id);	
	/***分页查询基础信息
	 * @param title String 基信标题，为空字符串时表示全部
	 * @param col int 前端页面的列数
	 * @param page int 当前页码
	 * **/
	List<BasicResource> findPaginationBasicResources(String title,int col,int page);
	/***统计产品数量**/
	long getBasicResourceCount(String title);
	/**通过标识查询产品*/
	BasicResource findBasicResourceById(long id);
	public List<Product> findLastProduct(Long id, String parameter);
	public User findUserForLogin(String account);
	public SystemInfo findSystemInfoById(long l);
}