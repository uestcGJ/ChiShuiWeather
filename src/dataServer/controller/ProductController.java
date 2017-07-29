package dataServer.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dataServer.util.DateUtil;
import dataServer.util.NumConv;
import domain.Product;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import service.AddService;
import service.AlterService;
import service.DeleteService;
import service.FindService;

@Controller
/****
 * 气象产品相关controller
 * ***/
public class ProductController {
	@Resource(name="findService")
	private FindService findService;
    @Resource(name="addService") 
	private AddService addService;
    @Resource(name="alterService") 
   	private AlterService alterService;
    @Resource(name="deleteService") 
   	private DeleteService delService;
    
    /***
	 * 产品制作
	 * @param product JSON格式的字符串，包含产品的各字段信息，含有以下字段：<br/>
	 *   type String:产品类型<br/>
	 *   title String:产品标题<br/>
	 *   context String:产品内容<br/>
	 *   author String:拟稿人<br/>
	 *   createDate String:创建时间<br/>
	 * @return JSONObject  包含以下信息：<br/>:
	 *   statusCode boolean:表示执行结果<br/>
	 *   err String:当statusCode为false时有效，表示失败原因
	 * ***/
	@RequestMapping(value="product/makeProduct")
	public void makeProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=false;
		String err="";
		JSONObject responseData=new JSONObject();
		JSONObject productInfo=JSONObject.fromObject(request.getParameter("product"));
		if(productInfo!=null){
			Product product=new Product();
			try{
				product.setTitle(productInfo.getString("title"));
				product.setType(productInfo.getString("type"));
				product.setCreateDate(DateUtil.getCurrentTime());
				product.setAuthor(productInfo.getString("author"));
				product.setReviewer(productInfo.getString("reviewer"));
				product.setImage(productInfo.getString("image"));
				String context=productInfo.getString("context");
				context=context.substring(1, context.length()-1);
				String[] byteStr=context.split(",");
				byte[] bytes=NumConv.stringArryTobyteArry(byteStr);
				product.setContext(new String(bytes));
			}catch(Exception e){
				e.printStackTrace();
				err="下发参数中存在必要信息的缺失项。";
			}finally{
				try{
					Serializable id=addService.addProduct(product);
					if(id!=null){
						statusCode=true;
					}
				}catch(Exception e){
					err="数据库操作异常，请稍后重试。";
				}
			}
		}
		else{
			err="下发参数中存在必要信息的缺失项。";
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	/***
	 * 删除气象产品
	 * @param id 要删除的气象产品的id
	 * @return JSONObject 包含两个字段:<br/>
	 *   &nbsp;&nbsp;&nbsp;&nbsp; statusCode:boolean 执行结果
	 *   &nbsp;&nbsp;&nbsp;&nbsp; err:失败原因 
	 * ***/
	@RequestMapping(value="product/delProduct")
	public void delBasis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		Long id=Long.parseLong(request.getParameter("id"));
		JSONObject responseData=new JSONObject();
		statusCode=delService.delProduct(id);
		if(!statusCode){
			responseData.put("err", "数据库操作异常，请稍后重试。");
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
    
    /***
     * 通过产品类型、表格列数以及当前页码分页查找气象产品信息
     * @param type String:产品类型，空字符串表示全部
     * @param col int:表格列数
     * @param page int:当前页码
     * @return JSONObject 包含以下字段：<br/>
     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
     *   totalPage:int 当statusCode为true时有效，表示符合条件的基信的页码数<br/>
     *   products：JSONArray，当statusCode为true时有效，表示符合条件的产品记录，每一个元素为一个JSONObject，包含以下字段：<br/>
     *   &nbsp;&nbsp;&nbsp;&nbsp; id：long，产品标识
     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String,产品标题
     *   &nbsp;&nbsp;&nbsp;&nbsp; type：String,产品类型
     *   &nbsp;&nbsp;&nbsp;&nbsp; author：String拟稿人
     *   &nbsp;&nbsp;&nbsp;&nbsp; createDate：String创建时间
     * **/
	 @RequestMapping("product/getPaginationProduct")
	 public void getPaginationProduct(HttpServletRequest request,HttpServletResponse response) throws IOException{
	    JSONObject responseData=new JSONObject();
	    boolean statusCode=false;
	    String err="";
		try {
			String type=request.getParameter("type");	
			type=(type==null)?"":type;
			int col=Integer.parseInt(request.getParameter("col"));
			int page=Integer.parseInt(request.getParameter("page"));
			List<Product> products=findService.findPaginationProducts(type, col, page);
				if(products!=null&&!products.isEmpty()){
					long totalCount=findService.getProductCount(type);
					int pages=(int) Math.ceil(totalCount*1.0/(col*1.0));//页码数
					pages=(pages==0)?1:pages;
					responseData.put("totalPage", pages);
					statusCode=true;
					JSONArray pruductInfos=new JSONArray();
					for(Product product:products){
						JSONObject pruductInfo=new JSONObject();
						pruductInfo.put("title", product.getTitle());
						pruductInfo.put("type", product.getType());
						pruductInfo.put("id", product.getId());
						pruductInfo.put("author", product.getAuthor());
						pruductInfo.put("createDate", product.getCreateDate());
						pruductInfos.add(pruductInfo);
					}
					responseData.put("products", pruductInfos);
				}
				else{
					err="当前数据库中不存在可用产品信息，您可以新增产品条目。";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				err="下发请求中缺少必要参数项，请核对后重试。";
			}
	    	if(!statusCode){
	    		responseData.put("err", err);
	    	}
	    	responseData.put("statusCode", statusCode);
	    	response.setContentType("text/html;charset=utf-8");
	    	PrintWriter out=response.getWriter();
			out.println(responseData);
			out.flush();
			out.close();
	 }
	 /***
	     * 通过产品标识获取产品详情
	     * @param id long:产品标识
	     * @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	     *   product JSONObject，当statusCode为true时有效，表示当前产品详细信息，包含以下字段：<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String，产品标题<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; context：String，产品内容</br>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; author：String，拟稿人</br>
	     * **/	
	 @RequestMapping("product/getProductDetial")
	 public void getProductDetial(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		String err="";
		Long id=null;
		JSONObject responseData=new JSONObject();
		try{
			id=Long.parseLong(request.getParameter("id"));
		}catch(Exception e){
			id=null;
			err="下发请求中缺少必要的参数项，请核对后重试。";
		} 
		if(id!=null){
			Product pruduct=findService.findProductById(id);
			if(pruduct!=null){
				statusCode=true;
				JSONObject product=new JSONObject();
				product.put("title", pruduct.getTitle());
				product.put("context",pruduct.getContext());
				product.put("author",pruduct.getAuthor());
				responseData.put("product", product);
			}else{
				err="根据下发的标识在数据库中找不到对应条目，请核对后重试。";
			}
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	 /***
	     * 通过产品标识获取产品详情和相关的三条产品的概要信息
	     * @param id long:产品标识，为0时查询最新一条
	     * @return JSONObject 包含以下字段：<br/>
	     *   statusCode: boolean，false表示查询失败；true表示查找成功<br/>
	     *   err:String 当statusCode为false时有效，表示失败的具体原因<br/>
	     *   product JSONObject，当statusCode为true时有效，表示当前产品详细信息，包含以下字段：<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; title：String，产品标题<br/>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; context：String，产品内容</br>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; author：String，拟稿人</br>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; time：String，发布时间</br>
	     *   &nbsp;&nbsp;&nbsp;&nbsp; reviewer：String，审核人</br>
	     *   relevants JSONArray:相关产品，每个元素为一个JSON对象，包括以下字段</br>:
	     *   	id :产品标识
	     *      title:产品名称
	     *      image:图片url 
	     * **/	
	 @RequestMapping("product/getLastProduct")
	 public void getLastProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean statusCode=true;
		String err="";
		Long id=null;
		JSONObject responseData=new JSONObject();
		try{
			id=Long.parseLong(request.getParameter("id"));
		}catch(Exception e){
			id=null;
			err="下发请求中缺少必要的参数项，请核对后重试。";
		} 
		if(id!=null){
			List<Product> pruducts=findService.findLastProduct(id,request.getParameter("type"));
			if(pruducts!=null&&!pruducts.isEmpty()){
				if(pruducts.get(0)!=null){
					statusCode=true;
					JSONObject product=new JSONObject();
					product.put("title", pruducts.get(0).getTitle());
					product.put("context",pruducts.get(0).getContext());
					product.put("author",pruducts.get(0).getAuthor());
					product.put("time",pruducts.get(0).getCreateDate());
					product.put("reviewer",pruducts.get(0).getReviewer());
					responseData.put("product", product);
					JSONArray relevants=new JSONArray();
					for(int i=1;i<pruducts.size();i++){
						JSONObject relevant=new JSONObject();
						relevant.put("title", pruducts.get(i).getTitle());
						relevant.put("image", pruducts.get(i).getImage());
						relevant.put("id",pruducts.get(i).getId());
						relevants.add(relevant);
					}
					responseData.put("relevants", relevants);
				}
				else{
					err="系统当前不存在可用产品信息。";
				}
				
			}else{
				err="系统当前不存在可用产品信息。";
			}
		}
		if(!statusCode){
			responseData.put("err", err);
		}
		responseData.put("statusCode", statusCode);
		/**send response*/
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println(responseData);
		out.flush();
		out.close();
	}
	 
}
