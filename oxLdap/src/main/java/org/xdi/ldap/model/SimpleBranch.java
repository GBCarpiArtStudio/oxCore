/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.ldap.model;

import java.io.Serializable;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

/**
 * Model for simple branch
 * 
 * @author Yuriy Movchan Date: 11.01.2010
 */
@LdapEntry
@LdapObjectClass(values = { "top", "organizationalUnit"})
public class SimpleBranch extends BaseEntry implements Serializable {

	private static final long serialVersionUID = -1311006812730222719L;

	@LdapAttribute(name = "ou")
	private String organizationalUnitName;
	
	public SimpleBranch() {
	}

	public SimpleBranch(String dn) {
		setDn(dn);
	}

	public SimpleBranch(String dn, String organizationalUnitName) {
		this(dn);
		this.organizationalUnitName = organizationalUnitName;
	}

	public String getOrganizationalUnitName() {
		return organizationalUnitName;
	}

	public void setOrganizationalUnitName(String organizationalUnitName) {
		this.organizationalUnitName = organizationalUnitName;
	}

	@Override
	public String toString() {
		return String.format("SimpleBranch [organizationalUnitName=%s, toString()=%s]", organizationalUnitName, super.toString());
	}

}
