package org.estore.e_store_order_service.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansProvider {

    @Bean
    public ModelMapper getModelMapper(){
        return  new ModelMapper();
    }
}
