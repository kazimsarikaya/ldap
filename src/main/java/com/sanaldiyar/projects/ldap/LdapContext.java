/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanaldiyar.projects.ldap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
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


        Properties env = new Properties();

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
            boolean access;

            for (Field field : declaredFields) {
                LdapAttribute la = field.getAnnotation(LdapAttribute.class);
                if (la != null) {
                    String aname = la.value();
                    Attribute fvalue = attributes.get(aname);

                    access = field.isAccessible();
                    if (!access) {
                        field.setAccessible(true);
                    }


                    if (fvalue == null) {
                        field.set(res, null);
                    } else {
                        Class<?> type = field.getType();
                        if (type.isPrimitive()) {
                            String value = fvalue.get().toString();

                            if (type.isAssignableFrom(boolean.class)) {
                                field.set(res, Boolean.valueOf(value));
                            } else if (type.isAssignableFrom(byte.class)) {
                                field.set(res, Integer.valueOf(value).byteValue());
                            } else if (type.isAssignableFrom(char.class)) {
                                field.set(res, value.charAt(0));
                            } else if (type.isAssignableFrom(double.class)) {
                                field.set(res, Double.valueOf(value).doubleValue());
                            } else if (type.isAssignableFrom(float.class)) {
                                field.set(res, Float.valueOf(value).floatValue());
                            } else if (type.isAssignableFrom(int.class)) {
                                field.set(res, Integer.valueOf(value).intValue());
                            } else if (type.isAssignableFrom(long.class)) {
                                field.set(res, Long.valueOf(value).longValue());
                            } else if (type.isAssignableFrom(short.class)) {
                                field.set(res, Short.valueOf(value).shortValue());
                            }



                        } else {
                            if (fvalue.size() == 1) {
                                if (type.isArray()) {
                                    Class<?> atype = type.getComponentType();
                                    Object itm = null;
                                    Object o = Array.newInstance(atype, 1);

                                    if (atype.isPrimitive()) {
                                        String value = fvalue.get().toString();

                                        if (type.isAssignableFrom(boolean.class)) {
                                            itm = Boolean.valueOf(value);
                                        } else if (type.isAssignableFrom(byte.class)) {
                                            itm = Integer.valueOf(value).byteValue();
                                        } else if (type.isAssignableFrom(char.class)) {
                                            itm = value.charAt(0);
                                        } else if (type.isAssignableFrom(double.class)) {
                                            itm = Double.valueOf(value).doubleValue();
                                        } else if (type.isAssignableFrom(float.class)) {
                                            itm = Float.valueOf(value).floatValue();
                                        } else if (type.isAssignableFrom(int.class)) {
                                            itm = Integer.valueOf(value).intValue();
                                        } else if (type.isAssignableFrom(long.class)) {
                                            itm = Long.valueOf(value).longValue();
                                        } else if (type.isAssignableFrom(short.class)) {
                                            itm = Short.valueOf(value).shortValue();
                                        }

                                    } else {
                                        //FIXME
                                        itm = fvalue.get();
                                    }

                                    Array.set(o, 0, itm);
                                    field.set(res, o);
                                } else {
                                    field.set(res, fvalue.get());
                                }
                            } else if (fvalue.size() > 1) {
                                if (type.isArray()) {
                                    Class<?> atype = type.getComponentType();
                                    Object itm = null;
                                    Object o = Array.newInstance(atype, fvalue.size());
                                    for (int i = 0; i < fvalue.size(); i++) {
                                        if (atype.isPrimitive()) {
                                            String value = fvalue.get(i).toString();

                                            if (type.isAssignableFrom(boolean.class)) {
                                                itm = Boolean.valueOf(value);
                                            } else if (type.isAssignableFrom(byte.class)) {
                                                itm = Integer.valueOf(value).byteValue();
                                            } else if (type.isAssignableFrom(char.class)) {
                                                itm = value.charAt(0);
                                            } else if (type.isAssignableFrom(double.class)) {
                                                itm = Double.valueOf(value).doubleValue();
                                            } else if (type.isAssignableFrom(float.class)) {
                                                itm = Float.valueOf(value).floatValue();
                                            } else if (type.isAssignableFrom(int.class)) {
                                                itm = Integer.valueOf(value).intValue();
                                            } else if (type.isAssignableFrom(long.class)) {
                                                itm = Long.valueOf(value).longValue();
                                            } else if (type.isAssignableFrom(short.class)) {
                                                itm = Short.valueOf(value).shortValue();
                                            }

                                        } else {
                                            //FIXME
                                            itm = fvalue.get(i);
                                        }
                                        Array.set(o, i, itm);
                                    }

                                    field.set(res, o);
                                } else {
                                    field.set(res, fvalue.get());
                                }
                            }
                        }

                        if (!access) {
                            field.setAccessible(false);
                        }

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

    public void setEntity(String identifier, String parentDn, EntityType[] entityTypes, Object o) {
        try {
            String dn = identifier + "," + parentDn;

            Attributes attributes = new BasicAttributes(true);

            Attribute attribute;

            attribute = new BasicAttribute("objectClass");

            for (int i = 0; i < entityTypes.length; i++) {
                attribute.add(entityTypes[i].toString());
            }
            attributes.put(attribute);

            Field[] declaredFields = o.getClass().getDeclaredFields();


            boolean access;

            for (Field field : declaredFields) {
                LdapAttribute la = field.getAnnotation(LdapAttribute.class);
                if (la != null) {
                    String aname = la.value();
                    attribute = new BasicAttribute(aname);


                    access = field.isAccessible();
                    if (!access) {
                        field.setAccessible(true);
                    }
                    Class<?> type = field.getType();
                    if (!type.isArray()) {
                        Object value = field.get(o);
                        //FIXME
                        if (type.isPrimitive()) {
                            attribute.add(value.toString());
                        } else {
                            attribute.add(value);
                        }
                    } else {
                        Object[] values = (Object[]) field.get(o);
                        for (Object value : values) {
                            //FIXME
                            if (value.getClass().isPrimitive()) {
                                attribute.add(value.toString());
                            } else {
                                attribute.add(value);
                            }
                        }
                    }

                    if (!access) {
                        field.setAccessible(false);
                    }

                    attributes.put(attribute);

                }
            }


            this.context.createSubcontext(dn, attributes);


        } catch (NamingException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateEntity(String dn, Object o) {

        try {
            Field[] declaredFields = o.getClass().getDeclaredFields();
            Attribute attribute;
            List<ModificationItem> items = new LinkedList<ModificationItem>();

            boolean access;

            for (Field field : declaredFields) {
                LdapAttribute la = field.getAnnotation(LdapAttribute.class);
                if (la != null) {
                    String aname = la.value();
                    attribute = new BasicAttribute(aname);


                    access = field.isAccessible();
                    if (!access) {
                        field.setAccessible(true);
                    }
                    Class<?> type = field.getType();
                    if (!type.isArray()) {
                        Object value = field.get(o);
                        //FIXME
                        if (type.isPrimitive()) {
                            attribute.add(value.toString());
                        } else {
                            attribute.add(value);
                        }
                    } else {
                        Object[] values = (Object[]) field.get(o);
                        for (Object value : values) {
                            //FIXME
                            if (value.getClass().isPrimitive()) {
                                attribute.add(value.toString());
                            } else {
                                attribute.add(value);
                            }
                        }
                    }

                    if (!access) {
                        field.setAccessible(false);
                    }

                    items.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute));
                }
            }

            ModificationItem[] mitems = new ModificationItem[items.size()];
            mitems = items.toArray(mitems);
            context.modifyAttributes(dn, mitems);
        } catch (NamingException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(LdapContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteEntity(String dn) throws NamingException {
        context.destroySubcontext(dn);
    }
}
