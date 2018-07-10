package com.goosen.config.swagger2;

import io.swagger.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Sets;  

import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.web.servlet.config.annotation.EnableWebMvc;  
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;  

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;  
import springfox.documentation.builders.RequestHandlerSelectors;  
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;  
import springfox.documentation.service.Contact;  
import springfox.documentation.spi.DocumentationType;  
import springfox.documentation.spring.web.plugins.Docket;  
import springfox.documentation.swagger2.annotations.EnableSwagger2;  
  

@Configuration  
@EnableSwagger2  
@EnableWebMvc
@ComponentScan(basePackages = "com.goosen")  
public class SwaggerConfig extends WebMvcConfigurationSupport {  
	
	@Bean
	public Docket demo1ApiDocket() {
		//添加head参数start  
//		List<springfox.documentation.service.Parameter> pars = new ArrayList<springfox.documentation.service.Parameter>();
//        ParameterBuilder tokenPar = new ParameterBuilder();  
//        tokenPar.name(HeaderConstants.X_TOKEN).description("用户的登录token").modelRef(new ModelRef("string")).parameterType("header").required(false).build();  
//        pars.add(tokenPar.build());
//        ParameterBuilder apiVersionPar = new ParameterBuilder();
//        apiVersionPar.name(HeaderConstants.API_VERSION).description("api的版本号").modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("2.0").build();
//        pars.add(apiVersionPar.build());
//        ParameterBuilder appVersionPar = new ParameterBuilder();
//        appVersionPar.name(HeaderConstants.APP_VERSION).description("app版本号").modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("v2").build();
//        pars.add(appVersionPar.build());
//        ParameterBuilder callSourcePar = new ParameterBuilder();
//        callSourcePar.name(HeaderConstants.CALL_SOURCE).description("调用来源").modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("PC").build();
//        pars.add(callSourcePar.build());
        //添加head参数end  
		
		return new Docket(DocumentationType.SWAGGER_12)
				.groupName("mgmp-mg-demo1")
				.apiInfo(new ApiInfoBuilder().title("mgmp-mg-demo1").description("mgmp的mg工程demo1").build())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.goosen.controller"))
				.paths(PathSelectors.any())
				//.paths(PathSelectors.none())//如果是线上环境，添加路径过滤，设置为全部都不符合
				.build()
//				.globalOperationParameters(pars)  
				;
	}
    
}  
