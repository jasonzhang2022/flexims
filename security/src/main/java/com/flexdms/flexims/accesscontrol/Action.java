package com.flexdms.flexims.accesscontrol;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(ActionXmlAdapter.class)
public abstract class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract String getName();

	public abstract String getDescription();

	public String[] getAliases() {
		return new String[0];
	}

	public boolean contain(Action action) {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		String name = getName();
		String[] aliases = getAliases();
		result = prime * result + ((aliases == null) ? 0 : aliases.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Action other = (Action) obj;
		String name = getName();
		if (name == null && other.getName() != null) {
			return false;
		}
		if (name != null && other.getName() == null) {
			return false;
		}

		if (name.equalsIgnoreCase(other.getName())) {
			return true;
		}
		for (String oalias : other.getAliases()) {
			if (name.equalsIgnoreCase(oalias)) {
				return true;
			}
		}
		for (String alias : getAliases()) {
			for (String oalias : other.getAliases()) {
				if (alias.equalsIgnoreCase(oalias)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean inAction(List<Action> actions) {
		if (actions == null || actions.isEmpty()) {
			return false;
		}
		for (Action a : actions) {
			if (equals(a) || a.contain(this)) {
				return true;
			}
		}
		return false;
	}
}
