package sn.cperf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import nz.net.ultraq.thymeleaf.LayoutDialect;

public class BeanConfig {

	@Bean
	public LayoutDialect layoutDialect() {
		// TemplateEngine engine = new TemplateEngine();
		return new LayoutDialect();
	}

	@Bean
	public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath("/templates/");
		return bean;
	}
}
