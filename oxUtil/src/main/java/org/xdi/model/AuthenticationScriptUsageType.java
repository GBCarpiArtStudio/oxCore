/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.model;

import java.util.HashMap;
import java.util.Map;

import org.gluu.site.ldap.persistence.annotation.LdapEnum;

/**
 * Authentication script type
 * 
 * @author Yuriy Movchan Date: 05/06/2013
 */
public enum AuthenticationScriptUsageType implements LdapEnum {

	INTERACTIVE("interactive", "Interactive"), SERVICE("service", "Services"), BOTH("both", "Both methods"), LOGOUT("logout", "Logout");

	private String value;
	private String displayName;

	private static Map<String, AuthenticationScriptUsageType> mapByValues = new HashMap<String, AuthenticationScriptUsageType>();

	static {
		for (AuthenticationScriptUsageType enumType : values()) {
			mapByValues.put(enumType.getValue(), enumType);
		}
	}

	private AuthenticationScriptUsageType(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static AuthenticationScriptUsageType getByValue(String value) {
		return mapByValues.get(value);
	}

	public Enum<? extends LdapEnum> resolveByValue(String value) {
		return getByValue(value);
	}

	@Override
	public String toString() {
		return value;
	}

}
