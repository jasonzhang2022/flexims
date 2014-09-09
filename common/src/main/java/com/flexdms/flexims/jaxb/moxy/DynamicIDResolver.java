package com.flexdms.flexims.jaxb.moxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.jaxb.IDResolver;
import org.xml.sax.SAXException;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

//https://wiki.eclipse.org/EclipseLink/Release/2.4.0/JAXB_RI_Extensions/ID_Resolver
public class DynamicIDResolver extends IDResolver {

	public static final int MAP_INITIAL_CAPACITY = 50;
	Map<String, Object> caches = new HashMap<>(MAP_INITIAL_CAPACITY);

	@Override
	public void bind(Object id, Object obj) throws SAXException {
		if (id == null || obj == null) {
			return;
		}

		Class<?> clz = obj.getClass();
		while (clz != FleximsDynamicEntityImpl.class && clz != Object.class) {
			String keyString = getKey(id, clz);
			caches.put(keyString, obj);
			clz = clz.getSuperclass();
		}
	}

	private String getKey(Object id, Class<?> type) {
		return type.getName() + ":" + id.toString();
	}

	public DynamicIDResolver() {
		super();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Callable<?> resolve(final Object id, final Class type) throws SAXException {
		final String keyString = getKey(id, type);
		return new Callable() {
			@Override
			public Object call() throws Exception {
				if (caches.containsKey(keyString)) {
					return caches.get(keyString);
				} else {
					@SuppressWarnings("unchecked")
					Object object = App.getEM().find(type, id);
					if (object != null) {
						caches.put(keyString, object);
					}

					return object;
				}

			}
		};
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Callable<?> resolve(Map<String, Object> id, Class type) throws SAXException {
		// We do not handle composite key, so this is unimplemented
		throw new RuntimeException("Composite keys are not supported. This should not be called.");
	}

	@Override
	public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
		caches.clear();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void bind(Map<String, Object> id, Object obj) throws SAXException {
		// We do not handle composite key, so this is unimplemented
		throw new RuntimeException("Composite keys are not supported. This should not be called.");

	}

}
