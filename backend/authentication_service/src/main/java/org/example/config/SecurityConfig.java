package org.example.config;

import org.example.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//WebSecurityConfigurerAdapter needs to extended to override some of its methods
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
    private JwtFilter jwtFilter;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }


    // authentication provider uses the userDetailsService by calling the loadUserByUsername()
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    //This method is used to configure the security of the application
    //Since we are attaching jwt to a request header manually, we don't need to worry about csrf
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors().and().
             http.csrf().disable()
                     .authorizeRequests()
                     .antMatchers("/authentication-service/login").permitAll()
                     .antMatchers("/authentication-service/register").permitAll()
                     .anyRequest().authenticated()
//                     .anyRequest().permitAll()
                     .and()
                     .authenticationProvider(daoAuthenticationProvider())
                     .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
