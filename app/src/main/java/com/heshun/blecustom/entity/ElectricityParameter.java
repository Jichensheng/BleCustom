package com.heshun.blecustom.entity;

/**
 * 电力参数实体
 * author：Jics
 * 2017/3/24 12:15
 */
public class ElectricityParameter {
	private String name;
	private String value;
	private String unit;

	public ElectricityParameter(String name, String value, String unit){
		this.name=name;
		this.value=value;
		this.unit=unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
