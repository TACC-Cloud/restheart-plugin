package org.restheart.security.impl;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Consumer;

public class AloeIdentityManager extends AbstractAloeSecurityManager implements IdentityManager {
  
  private final Map<String, SimpleAccount> accounts = new HashMap<>();
  
  public AloeIdentityManager(Map<String, Object> arguments) throws FileNotFoundException, UnsupportedEncodingException {
    init(arguments, "users");
  }
  
  @Override
  Consumer<? super Map<String, Object>> consumeConfiguration() {
    return u -> {
      Object _userid = u.get("userid");
      Object _password = u.get("password");
      Object _roles = u.get("roles");
    
      if (_userid == null || !(_userid instanceof String)) {
        throw new IllegalArgumentException("wrong configuration file format. a user entry is missing the userid");
      }
    
      if (_password == null || !(_password instanceof String)) {
        throw new IllegalArgumentException("wrong configuration file format. a user entry is missing the password");
      }
    
      if (_roles == null) {
        throw new IllegalArgumentException("wrong configuration file format. a user entry is missing the roles");
      }
    
      if (!(_roles instanceof List)) {
        throw new IllegalArgumentException("wrong configuration file format. a user entry roles argument is not an array");
      }
    
      String userid = (String) _userid;
      char[] password = ((String) _password).toCharArray();
    
      if (((Collection) _roles).stream().anyMatch(i -> !(i instanceof String))) {
        throw new IllegalArgumentException("wrong configuration file format. a roles entry is wrong. they all must be strings");
      }
    
      Set<String> roles = new HashSet<>((Collection) _roles);
    
      SimpleAccount a = new SimpleAccount(userid, password, roles);
    
      this.accounts.put(userid, a);
    };
  }
  
  @Override
  public Account verify(Account account) {
    return account;
  }
  
  @Override
  public Account verify(String s, Credential credential) {
    return null;
  }
  
  @Override
  public Account verify(Credential credential) {
    return null;
  }
  
}
