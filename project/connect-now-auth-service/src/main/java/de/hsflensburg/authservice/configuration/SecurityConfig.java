package de.hsflensburg.authservice.configuration;

import de.hsflensburg.authservice.populator.CustomLdapAuthoritiesPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static java.lang.String.format;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;
    private AuthenticationProvider authProvider;

    @Value("${auth.service.ldap.url:null}")
    private String ldapUrl;

    @Value("#{'${ldap.role.identifiers.student}'.split(',')}")
    private List<String> studentRoleIdentifier;

    @Value("#{'${ldap.role.identifiers.teacher}'.split(',')}")
    private List<String> teacherRoleIdentifier;

    @Value("#{'${ldap.role.identifiers.admin}'.split(',')}")
    private List<String> adminRoleIdentifier;
    @Value("${auth.service.authentication-method}")
    private String authenticationMethod;
    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;
    @Value("${auth.service.cors.allowed-origin}")
    private String allowedOrigin;
    @Value("${auth.service.cors.allowed-headers}")
    private String allowedHeaders;
    @Value("${auth.service.cors.allowed-methods}")
    private String allowedMethods;

    @Autowired(required = false)
    public void setAuthProvider(@Qualifier("authProvider") AuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }
    public SecurityConfig(JwtFilter jwtFilter) {
        super();
        this.jwtFilter = jwtFilter;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // Register custom auth provider
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        if (authenticationMethod.equalsIgnoreCase("ldap")) {
            auth
                    .ldapAuthentication()
                    .ldapAuthoritiesPopulator(new CustomLdapAuthoritiesPopulator(studentRoleIdentifier, teacherRoleIdentifier, adminRoleIdentifier))
                    .userSearchFilter("(|(uid={0})(mail={0}))")
                    .userDnPatterns("uid={0}")
                    //.userDnPatterns("cn={0},ou=admin,ou=users")
                    //.groupSearchBase("ou=users") //cn=admin,ou=admin,dc=example,dc=org
                    //.groupSearchBase("ou=users")
                    //.groupSearchFilter("(objectClass=groupOfNames)")
                    //.groupSearchBase("ou=groups")
                    .contextSource()
                    .url(ldapUrl);
        } else {
            auth.authenticationProvider(authProvider);
        }
    }

    // Set httpSecurity options
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // Disable cors and csrf
        httpSecurity.cors().and().csrf().disable();

        // Set session management to stateless
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Allow all requests on /auth/, all other routes need authentication
        httpSecurity.authorizeRequests()
                .antMatchers("/role/**").hasRole("ADMIN")
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/token/refresh").permitAll()
                .antMatchers(format("%s/**", restApiDocPath)).permitAll()
                .antMatchers(format("%s/**", swaggerPath)).permitAll()
                .anyRequest().authenticated();

        // Enable the jwt token filter
        httpSecurity.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }

    // Set cors filter options
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(allowedOrigin);
        config.addAllowedHeader(allowedHeaders);
        config.addAllowedMethod(allowedMethods);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Expose authentication manager bean
    @Override @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
