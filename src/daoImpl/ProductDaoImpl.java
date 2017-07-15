package daoImpl;

import java.util.ArrayList;
import java.util.List;

import dao.ProductDao;
import domain.Product;

public class ProductDaoImpl extends BaseDaoImpl<Product> implements ProductDao {

	@Override
	public List<Product> findPagination(String type, int col, int page) {
		String sql="select * from Product where  type like '%"+type+"%' order by id desc";
		return super.findPagination(sql, Product.class, page, col);
	}

	@Override
	public List<Product> findLastProduct(Long id, String type) {
		// TODO Auto-generated method stub
		List<Product> products=new ArrayList<>();
		String sql="select * from Product ";
		if(id!=0){
			sql+="where id="+id;
		}else{
			sql+="order by id desc limit 1";
		}
		Product product=super.findOneInSQL(sql, Product.class);
		products.add(product);
		if(product!=null){
			sql="select * from Product where id!="+product.getId()+" order by id desc limit 3";
			List<Product> relevant=super.findSQL(sql, Product.class);
			products.addAll(relevant);
		}
		return products;
	}

}
