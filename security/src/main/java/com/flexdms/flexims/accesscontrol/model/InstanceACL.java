package com.flexdms.flexims.accesscontrol.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class InstanceACL implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int INITIAL_ACE_NUM = 3;

	List<InstanceACE> aces;

	FleximsDynamicEntityImpl instance;

	public InstanceACL(FleximsDynamicEntityImpl instance) {
		super();
		this.instance = instance;
		this.aces = new ArrayList<InstanceACE>(INITIAL_ACE_NUM);
	}

	public InstanceACL(FleximsDynamicEntityImpl instance, List<InstanceACE> aces) {
		super();
		this.instance = instance;
		this.aces = aces;
	}

	public String getType() {
		return instance.getClass().getSimpleName();
	}

	public long getInstanceId() {
		return instance.getId();
	}

	public List<InstanceACE> getAces() {
		return aces;
	}

	public void setAces(List<InstanceACE> aces) {
		this.aces = aces;
	}

	public FleximsDynamicEntityImpl getInstance() {
		return instance;
	}

	public void setInstance(FleximsDynamicEntityImpl instance) {
		this.instance = instance;
	}

}
