package com.xwbing.config.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/**
 * ClassPathBeanDefinitionScanner
 *
 * @author xiangwb
 */
@Configuration
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    public static <T> T getProperty(String key, Class<T> clazz) {
        return applicationContext.getEnvironment().getProperty(key, clazz);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotation) {
        return applicationContext.getBeansWithAnnotation(annotation);
    }

    public static void publishEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    public static Resource[] getResourcesByPackage(String scanPackage) throws IOException {
        String locationPattern =
                ResourceUtils.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage)
                        + "/**/*.class";
        return applicationContext.getResources(locationPattern);
    }

    public static void resolveResource(Resource resource, Class clazz) throws IOException {
        if (!resource.isReadable()) {
            return;
        }
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        ClassMetadata classMetadata = metadataReader.getClassMetadata();

        String annotationName = clazz.getName();
        if (annotationMetadata.isAnnotated(annotationName)) {
            String value = (String)annotationMetadata.getAnnotationAttributes(annotationName).get("value");
            String className = classMetadata.getClassName();
        }

    }
}
