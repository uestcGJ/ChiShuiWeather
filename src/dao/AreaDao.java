package dao;

import domain.Area;

public interface AreaDao extends BaseDao<Area>{
  //通过名称获取区域
   public Area findAreaByName(String name);
   public Area findAreaByCode(String code);
}
