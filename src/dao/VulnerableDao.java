package dao;

import java.util.List;

import domain.Vulnerable;

public interface VulnerableDao extends BaseDao<Vulnerable>{

	List<Vulnerable> findPaginationVulners(String title, int col, int page);

}
