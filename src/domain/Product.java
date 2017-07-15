package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="PRODUCT")
public class Product {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//产品标识
	@Column(name="TITLE")
	private String title;	   //产品标题
	@Column(name="AUTHOR")
	private String author;	   //拟稿人
	@Column(name="REVIEWER")
	private String reviewer;   //审核
	@Column(name="TYPE")
	private String type;	   //产品类别
	@Column(name="IMAGE")
	private String image;	   //图片地址
	@Column(name="CREATE_DATE")
	private String createDate; //制作时间
	@Lob
	@Column(name="CONTEXT",columnDefinition="LONGTEXT")
	private String context;	//产品内容

	//id
	public Long getId(){
		return this.id;
	}
	//title
	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	//author
	public String getAuthor(){
		return this.author;
	}
	public void setAuthor(String author){
		this.author=author;
	}
	//reviewer
	public String getReviewer(){
		return this.reviewer;
	}
	public void setReviewer(String reviewer){
		this.reviewer=reviewer;
	}	
	//context
	public String getContext(){
		return this.context;
	}
	public void setContext(String context){
		this.context=context;
	}
	//type
	public String getType(){
		return this.type;
	}
	public void setType(String type){
		this.type=type;
	}
	//image
	public String getImage(){
		return this.image;
	}
	public void setImage(String image){
		this.image=image;
	}
   //createDate	
	public String getCreateDate(){
		return this.createDate;
	}
	public void setCreateDate(String createDate){
		this.createDate=createDate;
	}	
}
