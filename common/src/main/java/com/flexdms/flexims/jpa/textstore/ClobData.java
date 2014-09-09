package com.flexdms.flexims.jpa.textstore;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries(value = { @NamedQuery(name = ClobData.PREFIX_QUERY, query = "Select c from  ClobData c where c.name like :name") })
@Entity
@Access(AccessType.FIELD)
public class ClobData {

	public static final String PREFIX_QUERY = "PREFIX_QUERY";

	@Id
	String name;

	@Lob()
	@Column(length = 10 * 10124 * 1024)
	String data;

	public ClobData() {

	}

	public ClobData(String name, String data) {
		super();
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
