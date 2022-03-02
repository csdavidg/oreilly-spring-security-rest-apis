package io.jzheaux.springsecurity.goals;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

//@Configuration
public class SecurityBeansConfiguration {

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    /*@Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("user")
                .password("{bcrypt}$2a$10$3njzOWhsz20aimcpMamJhOnX9Pb4Nk3toq8OO0swIy5EPZnb1YyGe")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }*/


}
