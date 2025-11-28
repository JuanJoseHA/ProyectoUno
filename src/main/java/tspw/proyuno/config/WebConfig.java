package tspw.proyuno.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imagenes/**")
                // CAMBIO DE ORDEN: Prioriza el Classpath (archivos estáticos) 
                // para asegurar que las imágenes estáticas se carguen correctamente.
                .addResourceLocations("classpath:/static/imagenes/", "file:C:/Producto/","file:/app/imagenes_subidas/");
    }
}