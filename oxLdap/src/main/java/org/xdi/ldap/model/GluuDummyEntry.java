/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.ldap.model;

import java.io.Serializable;

import org.gluu.site.ldap.persistence.annotation.LdapEntry;

/**
 * Person with custom attributes
 * 
 * @author Yuriy Movchan Date: 07.13.2011
 */
@LdapEntry
public class GluuDummyEntry extends Entry implements Serializable {

	private static final long serialVersionUID = -1111582184398161100L;

}
