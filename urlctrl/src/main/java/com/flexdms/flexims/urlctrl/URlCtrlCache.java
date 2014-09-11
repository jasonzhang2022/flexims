package com.flexdms.flexims.urlctrl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import com.flexdms.flexims.AppCache;
import com.flexdms.flexims.AppInitializer;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.EntityContext;
import com.flexdms.flexims.jpa.event.InstanceAction;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;

@ApplicationScoped
public class URlCtrlCache {

	public static final String PATTERN_PROP_STRING = "UrlPattern";
	public static final String ORDER_PROP_STRING = "CheckOrder";
	public static final String ROLES_PROP_STRING = "Roles";
	public static final String TYPE_NAME = "FxUrlCtrl";
	public static final String PATTERN_KEY = "REGEX";

	public static final String CACHE_KEY = "URLCtrlCache";

	@Inject
	AppCache appCache;

	@SuppressWarnings("unchecked")
	public List<FleximsDynamicEntityImpl> getURLs() {
		return (List<FleximsDynamicEntityImpl>) appCache.get(CACHE_KEY);
	}

	public static void sortURLsByOrder(List<FleximsDynamicEntityImpl> urls) {
		Collections.sort(urls, new Comparator<FleximsDynamicEntityImpl>() {
			@Override
			public int compare(FleximsDynamicEntityImpl o1, FleximsDynamicEntityImpl o2) {
				Integer i1 = o1.get(ORDER_PROP_STRING);
				Integer i2 = o2.get(ORDER_PROP_STRING);
				return i1.compareTo(i2);
			}
		});
	}
	
	@PostConstruct
	public void initCache() {
		appCache.put(CACHE_KEY, new LinkedList<>());
	}

	public void init(@Observes AppInitializer.AppInitalizeContext ctx) {
		List<FleximsDynamicEntityImpl> urls = new LinkedList<>();
		urls.clear();
		for (Object obj : ctx.em.createQuery("select u from FxUrlCtrl u order by u.CheckOrder").getResultList()) {
			addUrl(urls, (FleximsDynamicEntityImpl) obj);
		}
		sortURLsByOrder(urls);
		appCache.put(CACHE_KEY, urls);
	}

	public static void addUrl(List<FleximsDynamicEntityImpl> urls, FleximsDynamicEntityImpl url) {
		try {
			url.getMetaAttributes().put(PATTERN_KEY, Pattern.compile((String) url.get(PATTERN_PROP_STRING), Pattern.CASE_INSENSITIVE));
			urls.add(url);
		} catch (Exception e) {
			// ignore bad rexexp syntax
			e.printStackTrace();
		}
	}

	public boolean isUrlCtrl(FleximsDynamicEntityImpl de) {
		return de.getClass().getSimpleName().equals(TYPE_NAME);
	}

	public FleximsDynamicEntityImpl findItem(FleximsDynamicEntityImpl de) {
		for (FleximsDynamicEntityImpl item1 : getURLs()) {
			if (item1.getId() == de.getId()) {
				return item1;
			}
		}
		return null;
	}

	public void deleteItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostRemove) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isUrlCtrl(de)) {
			return;
		}
		FleximsDynamicEntityImpl item = findItem(de);
		if (item != null) {
			getURLs().remove(item);
		}
	}

	public void editItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostUpdate) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isUrlCtrl(de)) {
			return;
		}
		List<FleximsDynamicEntityImpl> urls = getURLs();
		FleximsDynamicEntityImpl item = findItem(de);
		if (item != null) {
			urls.remove(item);
		}
		addUrl(urls, de);
		sortURLsByOrder(urls);
	}

	public void addItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostPersist) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isUrlCtrl(de)) {
			return;
		}
		List<FleximsDynamicEntityImpl> urls = getURLs();
		FleximsDynamicEntityImpl item = findItem(de);
		if (item != null) {
			urls.remove(item);
		}
		addUrl(urls, de);
		sortURLsByOrder(urls);
	}
}
