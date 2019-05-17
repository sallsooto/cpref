package sn.cperf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import sn.cperf.service.CperfService;

@SpringBootApplication
public class CperfApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(CperfApplication.class, args);
		try {
			CperfService cperfService = ctx.getBean(CperfService.class);
			cperfService.doAllNecessaryOperationsAfterLunchApplication();
		} catch (Exception e) {
		}
	}

}
