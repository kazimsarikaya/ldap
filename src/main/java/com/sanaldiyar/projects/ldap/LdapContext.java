/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanaldiyar.projects.ldap;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

/**
 *
 * @author kazim
 */
public class LdapContext {

    private String username;
    private String password;
    private String server;
    private int port;
    DirContext context = null;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void connect() throws NamingException {


        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + this.server + ":" + this.port);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, this.username);
        env.put(Context.SECURITY_CREDENTIALS, this.password);

        context = new InitialLdapContext(env, new Control[]{});

    }

    public void close() throws NamingException {
        context.close();
    }

    public <T> T getEntity(String dn, Class<T> clazz) throws NamingException {
        try {
            Attributes attributes = context.getAttributes(dn);
            T res = clazz.newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {
                LdapAttribute la = field.getAnnotation(LdapAttribute.class);
                if (la != null) {
                    String aname = la.value();
                    Attribute fvalue = attributes.get(aname);
                    if (fvalue == null) {
                        field.set(res, null);
                    } else {
                        field.set(res, fvalue.get());
                    }
                }
            }

            return res;
        } catch (InstantiationException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
