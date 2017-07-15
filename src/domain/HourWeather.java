package domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/****
 * 小时天气情况
 * 包括当前整点的降水量、湿度、温度情况
 * ***/
@Entity
@Table(name="HOUR_WEATHER")
public class HourWeather {
	@Id
	@GenericGenerator(name="key_increment",strategy="increment")
	@GeneratedValue(generator="key_increment")
	@Column(name="ID",nullable=false,unique=true,updatable=false)
	private Long id;			//天气标识
	@Column(name="DATE",nullable=false)
	private Timestamp date;		//date format likes 2017-03-12 03
	
	@Column(name="AREA_CODE",nullable=false,length=15)
	private String area_code;	  //code of area
	
	@Column(name="STATION_CODE",nullable=false,length=15)
	private String station_code;	  //code of station
	
	@Column(name="STATION_NAME",nullable=true,length=30)
	private String station_name;	  //code of station
	
	@Column(name="RAINFALL_1h",columnDefinition="float default 0.0",scale=1)
	private float rainfall_1h;		//rainfall_1h 
	
	@Column(name="RAINFALL_3h",columnDefinition="float default 0.0",scale=1)
	private float rainfall_3h;		//rainfall_3h
	
	@Column(name="RAINFALL_6h",columnDefinition="float default 0.0",scale=1)
	private float rainfall_6h;		//rainfall_6h
	
	@Column(name="RAINFALL_12h",columnDefinition="float default 0.0",scale=1)
	private float rainfall_12h;		//rainfall_12h
	
	@Column(name="RAINFALL_24h",columnDefinition="float default 0.0",scale=1)
	private float rainfall_24h;		//rainfall_24h
	
	@Column(name="HUMI_RE_MIN",columnDefinition="int default 0",length=2)
	private int humi_re_min;		//humi_re_min  最小相对湿度
	
	@Column(name="HUMI_RE",columnDefinition="int default 0")
	private int humi_re;		//humi_re   相对湿度
	
	@Column(name="TEMP_DEW",columnDefinition="float default 0.0" ,scale=1)
	private float temp_dew;		//露点温度
	
	@Column(name="TEMP_LOW",columnDefinition="float default 0.0",scale=1)
	private float temp_low;		//temp_low
	
	@Column(name="TEMP_AVE",columnDefinition="float default 0.0",scale=2)
	private float temp_ave;		//temp_ave
	
	@Column(name="TEMP_HIGH",columnDefinition="float default 0.0",scale=1)
	private float temp_high;		//temp_high
	
	@Column(name="TEMP_6h_HIGH",columnDefinition="float default 0.0",scale=1)
	private float temp_6h_high;		//temp_6h_high
	
	@Column(name="TEMP_6h_AVE",columnDefinition="float default 0.0",scale=1)
	private float temp_6h_ave;		//temp_6h_ave
	
	@Column(name="TEMP_6h_LOW",columnDefinition="float default 0.0",scale=1)
	private float temp_6h_low;		//temp_6h_low
	
	@Column(name="TEMP_12h_HIGH",columnDefinition="float default 0.0",scale=1)
	private float temp_12h_high;		//temp_12h_high
	
	@Column(name="TEMP_12h_AVE",columnDefinition="float default 0.0",scale=1)
	private float temp_12h_ave;		//temp_12h_ave
	
	@Column(name="TEMP_12h_LOW",columnDefinition="float default 0.0",scale=1)
	private float temp_12h_low;		//temp_12h_low
	
	@Column(name="TEMP_24h_HIGH",columnDefinition="float default 0.0",scale=1)
	private float temp_24h_high;		//temp_24h_high
	
	@Column(name="TEMP_24h_AVE",columnDefinition="float default 0.0",scale=1)
	private float temp_24h_ave;		//temp_24h_ave
	
	@Column(name="TEMP_24h_LOW",columnDefinition="float default 0.0",scale=1)
	private float temp_24h_low;		//temp_24h_low
	
	@Column(name="WIND_SPEED",columnDefinition="int default 0",scale=2)
	private float wind_speed;		//wind_speed number[2]
	
	//id 
	public long getId(){
		return this.id;
	}
	//date
	public java.sql.Timestamp getDate(){
		return this.date;
	}
	public void setDate(java.sql.Timestamp date){
		this.date=date;
	}
	//station_code
	public String getStationCode(){
		return this.station_code;
	}
	public void setStationCode(String stationCode){
		this.station_code=stationCode;
	}
	//station_name
	public String getStationName(){
		return this.station_name;
	}
	public void setStationName(String stationName){
		this.station_name=stationName;
	}
	//area_code
	public String getAreaCode(){
		return this.area_code;
	}
	public void setAreaCode(String area_code){
		this.area_code=area_code;
	}
	//rainfall_3h
	public float getRainfall_3h(){
		return this.rainfall_3h;
	}
	public void setRainfall_3h(float rainfall_3h){
		this.rainfall_3h=rainfall_3h;
	}
	//rainfall_6h
	public float getRainfall_6h(){
		return this.rainfall_6h;
	}
	public void setRainfall_6h(float rainfall_6h){
		this.rainfall_6h=rainfall_6h;
	}
	//rainfall_12h
	public float getRainfall_12h(){
		return this.rainfall_12h;
	}
	public void setRainfall_12h(float rainfall_12h){
		this.rainfall_12h=rainfall_12h;
	}
	//rainfall_24h
	public float getRainfall_24h(){
		return this.rainfall_24h;
	}
	public void setRainfall_24h(float rainfall_24h){
		this.rainfall_24h=rainfall_24h;
	}
	//rainfall_1h
	public float getRainfall_1h(){
		return this.rainfall_1h;
	}
	public void setRainfall_1h(float rainfall_1h){
		this.rainfall_1h=rainfall_1h;
	}
	//temp_dew
	public float getTempDew(){
		return this.temp_dew;
	}
	public void setTempDew(float temp_dew){
		this.temp_dew=temp_dew;
	}
	//temp_low
	public float getTempLow(){
		return this.temp_low;
	}
	public void setTempLow(float temp_low){
		this.temp_low=temp_low;
	}
	//temp_high
	public float getTempHigh(){
		return this.temp_high;
	}
	public void setTempHigh(float temp_high){
		this.temp_high=temp_high;
	}
	//temp_ave
	public float getTempAve(){
		return this.temp_ave;
	}
	public void setTempAve(float temp_ave){
		this.temp_ave=temp_ave;
	}
	//temp_6h_low
	public float getTemp6hLow(){
		return this.temp_6h_low;
	}
	public void setTemp6hLow(float temp_6h_low){
		this.temp_6h_low=temp_6h_low;
	}
	//temp_6h_ave
	public float getTemp6hAve(){
		return this.temp_6h_ave;
	}
	public void setTemp6hAve(float temp_ave){
		this.temp_6h_ave=temp_ave;
	}	
	//temp_6h_high
	public float getTemp6hHigh(){
		return this.temp_6h_high;
	}
	public void setTemp6hHigh(float temp_24h_max){
		this.temp_6h_high=temp_24h_max;
	}	
	//temp_12h_low
	public float getTemp12hLow(){
		return this.temp_12h_low;
	}
	public void setTemp12hLow(float temp_12h_low){
		this.temp_12h_low=temp_12h_low;
	}
	//temp_12h_ave
	public float getTemp12hAve(){
		return this.temp_12h_ave;
	}
	public void setTemp12hAve(float temp_ave){
		this.temp_12h_ave=temp_ave;
	}	
	//temp_12h_high
	public float getTemp12hHigh(){
		return this.temp_12h_high;
	}
	public void setTemp12hHigh(float temp_12h_max){
		this.temp_12h_high=temp_12h_max;
	}
	//temp_24h_low
	public float getTemp24hLow(){
		return this.temp_24h_low;
	}
	public void setTemp24hLow(float temp_24h_low){
		this.temp_24h_low=temp_24h_low;
	}
	//temp_24h_ave
	public float getTemp24hAve(){
		return this.temp_24h_ave;
	}
	public void setTemp24hAve(float temp_ave){
		this.temp_24h_ave=temp_ave;
	}	
	//temp_24h_high
	public float getTemp24hHigh(){
		return this.temp_24h_high;
	}
	public void setTemp24hHigh(float temp_24h_max){
		this.temp_24h_high=temp_24h_max;
	}	
	//humidity
	public int getHumiRe(){
		return this.humi_re;
	}
	public void setHumiRe(int humidity){
		this.humi_re=humidity;
	}
	//humi_min
	public int getHumiReMin(){
		return this.humi_re_min;
	}
	public void setHumiReMin(int humi_min){
		this.humi_re_min=humi_min;
	}
	//wind_speed
	public float getWindSpeed(){
		return this.wind_speed;
	}
	public void setWindSpeed(float windSpeed){
		this.wind_speed=windSpeed;
	}
}
