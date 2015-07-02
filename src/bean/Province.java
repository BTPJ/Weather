package bean;

/**
 * 省实体类
 * 
 * @author LTP
 *
 */
public class Province {
	/** 省的id号 */
	private int id;
	/** 省名 */
	private String provinceName;
	/** 省的代号 */
	private String provinceCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

}
