package com.axin.axinkeyboardwizard;

import com.axin.axinkeyboardwizard.services.fx.FxService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AxinKeyboardWizardApplication {

  public static void main(String[] args) {


    SpringApplication application = new SpringApplication(AxinKeyboardWizardApplication.class);

    application.setWebApplicationType(WebApplicationType.NONE);
    application.setHeadless(false);
    ConfigurableApplicationContext context = application.run(args);
    FxService fxService = context.getBean(FxService.class);
    fxService.start();
  }

}
