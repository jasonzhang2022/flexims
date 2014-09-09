package com.flexdms.flexims.accesscontrol.rs;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.flexdms.flexims.accesscontrol.model.InstanceACE;

@XmlRootElement
public class InstanceACES {
	List<InstanceACE> aces;

	public List<InstanceACE> getAces() {
		return aces;
	}

	public void setAces(List<InstanceACE> aces) {
		this.aces = aces;
	}

}
