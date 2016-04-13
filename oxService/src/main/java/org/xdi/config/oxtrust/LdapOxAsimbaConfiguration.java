/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2016, Gluu
 */
package org.xdi.config.oxtrust;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapJsonObject;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;
import org.xdi.ldap.model.Entry;

/**
 * Asimba LDAP settings configuration entry. 
 * 
 * @author Dmitry Ognyannikov
 */
@LdapEntry
@LdapObjectClass(values = {"top", "oxAsimbaConfiguration"})
public class LdapOxAsimbaConfiguration extends Entry {

    private static final long serialVersionUID = -12489347651302948L;
    
    @LdapJsonObject
    @LdapAttribute(name = "oxConfApplication")
    private AsimbaConfiguration applicationConfiguration;

    @LdapAttribute(name = "oxRevision")
    private long revision;
    
    public LdapOxAsimbaConfiguration() {}

    /**
     * @return the applicationConfiguration
     */
    public AsimbaConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    /**
     * @param applicationConfiguration the applicationConfiguration to set
     */
    public void setApplicationConfiguration(AsimbaConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    /**
     * @return the revision
     */
    public long getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(long revision) {
        this.revision = revision;
    }
    
}
