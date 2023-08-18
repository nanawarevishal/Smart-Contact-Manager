package com.smart.smartcontactmanager.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@EnableWebSecurity
@Configuration
public class MyConfig{

    private UserDetailServiceImpl userDetailServiceImpl;

    @Bean
    public UserDetailsService getUSerDetailService(){
        return new UserDetailServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUSerDetailService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    // protected void configure(AuthenticationManagerBuilder auth)throws Exception{
    //     auth.authenticationProvider(authenticationProvider());
        
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    //     http.csrf(AbstractHttpConfigurer :: disable).cors(AbstractHttpConfigurer :: disable).authorizeHttpRequests(auth->{

    //         auth.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**").hasAnyRole("USER").requestMatchers("/**").permitAll();
    //     });
            
    //     return http.build();
    // }

    @Bean 
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
            .csrf(csrf->csrf.disable())
            .authorizeHttpRequests(auth ->{
                auth.requestMatchers("/admin/**").hasRole("ADMIN");
                auth.requestMatchers("/user/**").hasRole("USER");
                auth.requestMatchers("/**").permitAll();
                auth.anyRequest().authenticated();
            })
            // .formLogin(Customizer.withDefaults())    // this is default login form
            .formLogin(form -> form
			.loginPage("/signin"))
            .httpBasic(Customizer.withDefaults())
            .build();
            
    }

    // @Bean                                                      
	// public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
	// 	http
	// 		.securityMatcher("/admin/**")                                   
	// 		.authorizeHttpRequests(authorize -> authorize
	// 			.anyRequest().hasRole("ADMIN")
	// 		)
    //         .securityMatcher("/user/**")
    //         .authorizeHttpRequests(authorize -> authorize
    //         .anyRequest().hasRole("USER"))
    //         .securityMatcher("/**")
    //         .authorizeHttpRequests(authorize -> authorize
    //             .anyRequest().authenticated())

	// 		.httpBasic(Customizer.withDefaults())
    //         .formLogin(Customizer.withDefaults());
	// 	return http.build();
	// }

}
