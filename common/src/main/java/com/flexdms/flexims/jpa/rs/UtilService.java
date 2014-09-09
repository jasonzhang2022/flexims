package com.flexdms.flexims.jpa.rs;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.util.DescribedEnum;

/**
 * Simple Utility service
 * 
 * @author jason.zhang
 * 
 */
@Path("/util")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class UtilService {

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@SuppressWarnings("unchecked")
	@Path("/getenum/{type}")
	@GET
	public NameValueList getEnum(@PathParam("type") String clz) throws Exception {
		Class<Enum<?>> clazz = (Class<Enum<?>>) Class.forName(clz);
		Enum<?>[] xs = clazz.getEnumConstants();
		Map<String, String> map = new LinkedHashMap<>(xs.length);

		if (DescribedEnum.class.isAssignableFrom(clazz)) {
			for (Enum<?> x : xs) {
				map.put(x.name(), ((DescribedEnum) x).getSymbol());
			}
		} else {
			for (Enum<?> x : xs) {
				map.put(x.name(), x.name());
			}
		}

		return NameValueList.fromMap(map);

	}

}
