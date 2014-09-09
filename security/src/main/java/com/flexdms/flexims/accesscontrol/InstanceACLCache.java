package com.flexdms.flexims.accesscontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.InstanceACL;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

@RequestScoped
public class InstanceACLCache {
	@Inject
	@FxsecurityEM
	EntityManager em;

	public static class InstanceACLKey {
		String typename;
		long instanceid;

		public String getTypeid() {
			return typename;
		}

		public void setTypeid(String typeid) {
			this.typename = typeid;
		}

		public long getInstanceid() {
			return instanceid;
		}

		public void setInstanceid(long instanceid) {
			this.instanceid = instanceid;
		}

		protected InstanceACLKey(String typeid, long instanceid) {
			super();
			this.typename = typeid;
			this.instanceid = instanceid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (instanceid ^ (instanceid >>> 32));
			result = prime * result + ((typename == null) ? 0 : typename.hashCode());
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
			InstanceACLKey other = (InstanceACLKey) obj;
			if (instanceid != other.instanceid) {
				return false;
			}
			if (typename == null) {
				if (other.typename != null) {
					return false;
				}
			} else if (!typename.equals(other.typename)) {
				return false;
			}
			return true;
		}

	}

	Map<InstanceACLKey, InstanceACL> aclsCache = new HashMap<InstanceACLKey, InstanceACL>();

	public void addCache(InstanceACL acl) {
		InstanceACLKey key = new InstanceACLKey(acl.getType(), acl.getInstanceId());
		aclsCache.put(key, acl);
	}

	public InstanceACL get(FleximsDynamicEntityImpl i) {

		InstanceACL acl = aclsCache.get(new InstanceACLKey(i.getClass().getSimpleName(), i.getId()));
		if (acl != null) {
			return acl;
		}
		return loadInstanceACL(i);

	}

	public InstanceACL loadInstanceACL(FleximsDynamicEntityImpl i) {
		Query query = em.createNamedQuery(InstanceACE.ACLQNAME);
		query.setParameter("typeid", i.getClass().getSimpleName());
		query.setParameter("instanceid", i.getId());

		@SuppressWarnings("unchecked")
		List<InstanceACE> aces = (List<InstanceACE>) query.getResultList();
		InstanceACL acl = new InstanceACL(i, aces);
		addCache(acl);
		return acl;
	}

	public void clear() {
		aclsCache.clear();
	}
}
