package com.romy.notification.common.config;

import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.jdbc.support.JdbcUtils;

public class ResultMap extends ListOrderedMap {

	private static final long serialVersionUID = -7700790403928325865L;
	
	public Object put(Object key, Object value) {
		if(value == null) value = "";
		return super.put(JdbcUtils.convertUnderscoreNameToPropertyName((String) key), value);
	}
	
}

