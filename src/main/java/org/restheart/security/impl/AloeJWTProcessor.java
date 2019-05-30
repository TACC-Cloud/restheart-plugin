package org.restheart.security.impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.restheart.security.exceptions.AloeSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Processes the JWT Header value and verifies it against out Aloe keys
 *
 * @author sterry1
 */public class AloeJWTProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AloeJWTProcessor.class);
  /* ********************************************************************** */
  /*                                Fields                                  */
  /* ********************************************************************** */
  // The public key used to check the JWT signature.  This cached copy is
  // used by all instances of this class.
  private static PublicKey _jwtPublicKey;
  // The JWT key alias.  This is the key pair used to sign JWTs
  // and verify them.
  public static final String DEFAULT_KEY_ALIAS = "wso2";
  
  public boolean isVerified = false;
  public String userName = null;
  public String roles = null;
  public String jwtToken;
  
  
  /* ---------------------------------------------------------------------- */
  /* default contructor:                                                    */
  /* ---------------------------------------------------------------------- */
  /**
   * @param  token from the request header
   * @return new AloeJWTProcessor
   */
  public AloeJWTProcessor(String token){
    this.jwtToken = token;
  }
  
  public Jwt get_jwt() {
    return _jwt;
  }
  
  // the decoded and verified jwt that can produce a json version of claims body.
  private Jwt _jwt = null;
  
  
  /* ---------------------------------------------------------------------- */
  /* decodeAndVerifyJwt:                                                    */
  /* ---------------------------------------------------------------------- */
  /** Decode the jwt and use the JWT signature to validate that the header
   * and payload have not changed.
   *
   * @param encodedJWT the JWT from the request header
   * @return the decoded and verified jwt
   */
  public Jwt decodeAndVerifyJwt(String encodedJWT) throws AloeSecurityException {
    // Some defensive programming.
    if (encodedJWT == null) return null;
    
    // Get the public part of the signing key.
    PublicKey publicKey = getJwtPublicKey();
    
    // Verify and import the jwt data.
    Jwt jwt = null;
    try { jwt = Jwts.parser().setSigningKey(publicKey).parse(encodedJWT);}
    catch (Exception e) {
      String msg = "ALOE_SECURITY_JWT_PARSE_ERROR";
      LOGGER.error(msg, e);
      throw new AloeSecurityException(msg, e);
    }
    
    if (jwt !=null){
      this._jwt = jwt;
      this.isVerified = true;
      // we need to unpack the username and roles from claims
      // Retrieve the user name from the claims section.
      Claims claims = (Claims) jwt.getBody();
      if (claims != null) {
        userName = this.getUser(claims);
        roles = this.getRoles(claims);
      }
  
    }
    
    // We have a validated jwt.
    return jwt;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getJwtPublicKey:                                                       */
  /* ---------------------------------------------------------------------- */
  /** Return the cached public key if it exists.  If it doesn't exist, load it
   * from the keystore, cache it, and then return it.
   *
   * @return jwt Public Key
   */
  private PublicKey getJwtPublicKey() throws AloeSecurityException {
    // Use the cached copy if it has already been loaded.
    if (_jwtPublicKey != null) return _jwtPublicKey;
    
    // Serialize access to this code.
    synchronized (AloeJWTProcessor.class)
    {
      // Maybe another thread loaded the key in the intervening time.
      if (_jwtPublicKey != null) return _jwtPublicKey;
      
      // We need to load the key from the keystore.
      // Get our own instance of the key manager
      // to avoid possible multithreading issues.
      KeyManager km = null;
      try {km = new KeyManager();}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_NO_KEYSTORE";
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Get the keystore's password.
      String password = "Umg&FR7VwWL*04lj8vd+";  // AloeEnv.get(EnvVar.ALOE_ENVONLY_KEYSTORE_PASSWORD);
      if (StringUtils.isBlank(password)) {
        String msg = "ALOE_SECURITY_NO_KEYSTORE_PASSWORD";
        LOGGER.error(msg);
        throw new AloeSecurityException(msg);
      }
      
      // Load the complete store.
      try {km.load(password);}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_KEYSTORE_LOAD_ERROR";
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Get the certificate containing the public key.
      Certificate cert = null;
      try {cert = km.getCertificate(DEFAULT_KEY_ALIAS);}
      catch (KeyStoreException e) {
        String msg = "ALOE_SECURITY_GET_CERTIFICATE "+ DEFAULT_KEY_ALIAS+" "+e.getMessage();
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Make sure we got a certificate.
      if (cert == null) {
        String msg = "ALOE_SECURITY_CERTIFICATE_NOT_FOUND "+DEFAULT_KEY_ALIAS+" "+km.getStorePath();
        LOGGER.error(msg);
        throw new AloeSecurityException(msg);
      }
      
      // Get the public key from the certificate and verify the certificate.
      PublicKey publicKey = cert.getPublicKey();
      try {cert.verify(publicKey);}
      catch (Exception e) {
        String msg = "ALOE_SECURITY_CERTIFICATE_VERIFY "+DEFAULT_KEY_ALIAS+" "+ e.getMessage();
        LOGGER.error(msg, e);
        throw new AloeSecurityException(msg, e);
      }
      
      // Success!
      _jwtPublicKey = publicKey;
    }
    
    return _jwtPublicKey;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getUser:                                                               */
  /* ---------------------------------------------------------------------- */
  /** Get the user name or return null.
   *
   * @param claims the JWT claims object
   * @return the simple user name or null
   */
  public String getUser(Claims claims)
  {
    // The enduser name may have extraneous information around it.
    String s = (String)claims.get("http://wso2.org/claims/enduser");
    if (StringUtils.isBlank(s)) return null;
    else if (s.contains("@")) return StringUtils.substringBefore(s, "@");
    else if (s.contains("/")) return StringUtils.substringAfter(s, "/");
    else return s;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getRoles:                                                              */
  /* ---------------------------------------------------------------------- */
  /** Get the set of roles or return null.
   *
   * @param claims the JWT claims object
   * @return the user's roles or null
   */
  public String getRoles(Claims claims)
  {
    String s =  (String)claims.get("http://wso2.org/claims/role");
    if (StringUtils.isBlank(s)) return null;
    else return s;
  }
  
}
