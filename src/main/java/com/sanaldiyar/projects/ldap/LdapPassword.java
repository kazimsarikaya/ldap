/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanaldiyar.projects.ldap;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author kazim
 */
public class LdapPassword {

    public enum PasswordType {

        SHA, SSHA, MD5, SMD5, CRYPT, PLAINTEXT;

        @Override
        public String toString() {
            String res = "";
            switch (this) {
                case SHA:
                    res = "{SHA}";
                    break;
                case SSHA:
                    res = "{SSHA}";
                    break;
                case MD5:
                    res = "{MD5}";
                    break;
                case SMD5:
                    res = "{SMD5}";
                    break;
                default:
                    res = "";
                    break;
            }
            return res;
        }
    }

    public static byte[] generatePassword(String password, PasswordType passwordType) {
        try {
            byte[] res = null;
            int seedChars = 0;
            byte[] seed = null;
            switch (passwordType) {
                case SMD5:
                case SSHA:
                    seedChars = 8;
                    break;
                case CRYPT:
                    seedChars = 2;
                    break;
                default:
                    seedChars = 0;
                    break;
            }

            if (seedChars > 0) {
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(Calendar.getInstance(TimeZone.getTimeZone("Europe/Istanbul")).getTimeInMillis());
                seed = new byte[seedChars];
                secureRandom.nextBytes(seed);
            }
            byte[] passwordBytes = password.getBytes("UTF-8");

            byte hashinput[] = null;

            switch (passwordType) {
                case SMD5:
                case SSHA:
                case CRYPT:
                    hashinput = new byte[passwordBytes.length + seedChars];
                    System.arraycopy(passwordBytes, 0, hashinput, 0, passwordBytes.length);
                    System.arraycopy(seed, 0, hashinput, passwordBytes.length, seedChars);
                    break;
                case MD5:
                case SHA:
                case PLAINTEXT:
                    hashinput = new byte[passwordBytes.length];
                    System.arraycopy(passwordBytes, 0, hashinput, 0, passwordBytes.length);
                    break;
            }

            byte[] hash = null;
            switch (passwordType) {
                case SHA:
                case SSHA:
                    hash = DigestUtils.sha(hashinput);
                    break;
                case MD5:
                case SMD5:
                    hash = DigestUtils.md5(hashinput);
                    break;
                case CRYPT:
                    break;
                case PLAINTEXT:
                    hash = new byte[hashinput.length];
                    System.arraycopy(hashinput, 0, hash, 0, hash.length);
                    break;
            }

            byte[] encinput = null;
            switch(passwordType){
                case SMD5:
                case SSHA:
                case CRYPT:
                    encinput = new byte[hash.length + seedChars];
                    System.arraycopy(hash, 0, encinput, 0, hash.length);
                    System.arraycopy(seed, 0, encinput, hash.length, seedChars); 
                    break;
                default:
                    encinput=new byte[hashinput.length];
                    System.arraycopy(hashinput, 0, encinput, 0, encinput.length);
                    break;
            }

            if (passwordType == PasswordType.PLAINTEXT) {
                res=new byte[encinput.length];
                System.arraycopy(encinput, 0, res, 0, res.length);
            } else {
                byte[] tmp = Base64.encodeBase64(encinput);
                byte[] label=passwordType.toString().getBytes("UTF-8");
                res=new byte[label.length+tmp.length];
                System.arraycopy(label, 0, res, 0, label.length);
                System.arraycopy(tmp, 0, res, label.length, tmp.length);
            }
            return res;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LdapPassword.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LdapPassword.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
