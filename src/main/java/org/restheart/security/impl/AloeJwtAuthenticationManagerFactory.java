package org.restheart.security.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.Jwt;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import org.restheart.security.AuthenticationMechanismFactory;
import org.restheart.security.exceptions.AloeSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class extracts the wso2 generated jwt token and the tenant value
 * from header name and follows the sequence
 *     if token is verified
 *        create Account wht the <user> <roles> <tenant> info
 *     if everything checks out return Authenticated
 *     else return NOT Authenticated
 *
 * @return
 * @author sterry1 modified from    @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class AloeJwtAuthenticationManagerFactory implements AuthenticationMechanismFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationManagerFactory.class);

    public static final String JWT_AUTH_HEADER_PREFIX = "X-JWT-Assertion";
    
    @Override
    public AuthenticationMechanism build(Map<String, Object> args, IdentityManager idm) {
        
        return new AuthenticationMechanism() {
            @Override
            public AuthenticationMechanismOutcome
                    authenticate(HttpServerExchange hse, SecurityContext sc) {
                try {
                    Map<String,String>tenantAndToken = getTenanatAndToken(hse);
                    boolean readyToProcess = tenantAndToken != null;
                    
                    if ( readyToProcess ) {
                        String token = tenantAndToken.get("jwtToken");
                        AloeJWTProcessor aloeJWTProcessor = new AloeJWTProcessor(token);
                        Jwt verifiedJwt = aloeJWTProcessor.decodeAndVerifyJwt(token);
                        
                        if(verifiedJwt == null){
                            // get out with not authenticated
                            LOGGER.debug("Jwt token validation failed ");
                            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                        }
                        
                        // if we verified the token
                        // we need the roles, tenant and userid
                        // the roles need to be in a Set<String>
                        String _roles = aloeJWTProcessor.roles;
                        Set<String> roles = new LinkedHashSet<>();
                        boolean rolesInitialized = false;
                        
                        
                        if (_roles != null && !_roles.isEmpty()) {
                            try {
                                String[] values = _roles.split(",");
                                roles = new LinkedHashSet<String>(Arrays.asList(values));

                                if (roles != null) {
                                    rolesInitialized = true;
                                }
                            } catch (Exception ex ) {
                                LOGGER.warn("Jwt cannot get roles from claim {}, "
                                        + "extepected an array of strings: {}",
                                        _roles.toString());
                            }
                        }

                        AloeAccount account = new AloeAccount(aloeJWTProcessor.userName,roles,tenantAndToken.get("tenant"));
                        if (idm != null) {
                            idm.verify(account);
                        }

                        sc.authenticationComplete(account,
                                "AloeJwtAuthenticationManager", false);
                        return AuthenticationMechanismOutcome.AUTHENTICATED;
                    }

                } catch (JWTVerificationException | AloeSecurityException ex) {
                    LOGGER.debug("Jwt not verified "+ ex.getMessage(), ex);
                    return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
                }
    
                // by returning NOT_ATTEMPTED, in case the provided credentials
                // don't match any user of the IdentityManager, the authentication
                // will fallback to the default authentication manager (BasicAuthenticationManager)
                // to make it failing, return NOT_AUTHENTICATED
                return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
           }

            @Override
            public ChallengeResult sendChallenge(HttpServerExchange hse, SecurityContext sc
            ) {
                return new ChallengeResult(true, 200);
            }
    
            /* ---------------------------------------------------------------------- */
            /* getTenanatAndToken:                                                    */
            /* ---------------------------------------------------------------------- */
            /**
             * Gets the jwt header and value out of the request headers
             *   parses the header name for the tenant value and the jwt token string
             * @param hse
             * @return Map of two values "tenant" string name and "jwtToken" string
             *         for processing
             */
            private Map getTenanatAndToken(HttpServerExchange hse) {
                HeaderMap headers = hse.getRequestHeaders();
                Collection<HttpString> headerNames = headers.getHeaderNames();
                for (HttpString name : headerNames){
                    if(name.toString().startsWith(JWT_AUTH_HEADER_PREFIX)){
                        Map<String,String>jwtMap = new HashMap<>();
                        jwtMap.put("tenant",name.toString().replace(JWT_AUTH_HEADER_PREFIX,""));
                        HeaderValues values = headers.get(name);
                        jwtMap.put("jwtToken",values.getFirst());
                        LOGGER.debug("jwt : "+values.getFirst());
                        return jwtMap;
                    }
                }
                return null;
            }
        };
    }
}
