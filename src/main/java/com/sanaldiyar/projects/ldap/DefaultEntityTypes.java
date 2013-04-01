/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanaldiyar.projects.ldap;

/**
 *
 * @author kazim
 */
public enum DefaultEntityTypes implements EntityType {

    INETORGPERSON,POSIXACCOUNT,ORGANIZATIONALPERSON,PERSON;

    @Override
    public String toString() {
        String res = "";

        switch (this) {
            case INETORGPERSON:
                res = "inetOrgPerson";
                break;
            case POSIXACCOUNT:
                res="posixAccount";
                break;
            case ORGANIZATIONALPERSON:
                res="organizationalPerson";
                break;
            case PERSON:
                res="person";
                break;
        }

        return res;
    }
}
