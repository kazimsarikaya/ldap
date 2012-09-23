/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanaldiyar.projects.ldap;

/**
 *
 * @author kazim
 */
public enum EntityType {

    INETORGPERSON;

    @Override
    public String toString() {
        String res = "";

        switch (this) {
            case INETORGPERSON:
                res = "inetOrgPerson";
                break;
        }

        return res;
    }
}
