package sn.cperf.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import sn.cperf.service.HelperService;
import sn.cperf.service.HelperServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter  {
	
	@Autowired
	public void globalConfig(AuthenticationManagerBuilder auth, DataSource dataSource) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select username as principal,password as credentials, valid from users where username=?")
		.authoritiesByUsernameQuery("select users.username as principal ,roles.role as role from users"
				+ " left join users_roles on users.id = users_roles.user_id"
				+ " left join roles on roles.id=users_roles.role_id where users.username=?").rolePrefix("ROLE_").passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/css/**").permitAll()
			.antMatchers("/scss/**").permitAll()
			.antMatchers("/js/**").permitAll()
			.antMatchers("/images/**").permitAll()
			.antMatchers("/fonts/**").permitAll()
			.antMatchers("/dist/**").permitAll()
			.antMatchers("/lib/**").permitAll()
			.antMatchers("/uploads/**").permitAll()
			.antMatchers("/email/**").permitAll()
			.antMatchers("/login").permitAll()
			.antMatchers("/logout").permitAll()
			.antMatchers("/register/**").permitAll()
			.antMatchers("/resetPassword/**").permitAll()
			.antMatchers("/error").permitAll()
			.antMatchers("/403").permitAll()
			.anyRequest().authenticated()
				.and()
			.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/")
				.and()
			.logout().permitAll()
			.and().exceptionHandling().accessDeniedPage("/403");
	}
	
	@Bean(name="beanHelper")
	public HelperService helperService() {
		return new HelperServiceImpl();
	}
}