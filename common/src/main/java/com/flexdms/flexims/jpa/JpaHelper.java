package com.flexdms.flexims.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.util.Utils;

public final class JpaHelper {
	private JpaHelper() {
		
	}
	public static Class<? extends DynamicEntity> getEntityClass(EntityManager em, String name) {
		name = Utils.upperFirstLetter(name);
		DynamicHelper dhelper = new JPADynamicHelper(em);
		return dhelper.getType(name).getJavaClass();
	}

	public static FleximsDynamicEntityImpl createNewEntity(EntityManager em, String name) {
		Class<? extends DynamicEntity> clz = getEntityClass(em, name);
		try {
			return (FleximsDynamicEntityImpl) clz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	private static Subgraph populateSubgraph(Subgraph subgraph, List<String> props) {
		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				subgraph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}
		for (String key : subkeysMap.keySet()) {
			Subgraph subgraph1 = subgraph.addSubgraph(key);
			populateSubgraph(subgraph1, subkeysMap.get(key));
		}
		return subgraph;

	}

	/**
	 * Not used right now. Using FetchGroup for property filtering
	 * 
	 * @param em
	 * @param typename
	 * @param props
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static EntityGraph buildEntityGraph(EntityManager em, String typename, List<String> props) {
		EntityGraph graph = em.createEntityGraph(getEntityClass(em, typename));
		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				graph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}

		for (String key : subkeysMap.keySet()) {
			Subgraph subgraph = graph.addSubgraph(key);
			populateSubgraph(subgraph, subkeysMap.get(key));
		}
		return graph;
	}

}
