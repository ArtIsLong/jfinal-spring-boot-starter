/** * $Id: JfinalClassPathBeanDefinitionScanner.java,v 1.0 2019-07-27 14:48 chenmin Exp $ */package com.github.jfinal.config;import lombok.Data;import lombok.experimental.Accessors;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;import org.springframework.beans.factory.config.BeanDefinitionHolder;import org.springframework.beans.factory.support.BeanDefinitionRegistry;import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;import org.springframework.core.type.filter.AnnotationTypeFilter;import org.springframework.core.type.filter.AssignableTypeFilter;import java.lang.annotation.Annotation;import java.util.Arrays;import java.util.Set;/** * @author 陈敏 * @version $Id: JfinalClassPathBeanDefinitionScanner.java,v 1.1 2019-07-27 14:48 chenmin Exp $ * Created on 2019-07-27 14:48 * My blog： https://www.chenmin.info */@Slf4j@Data@Accessors(chain = true)public class ClassPathJfinalControlScanner extends ClassPathBeanDefinitionScanner {    private Class<? extends Annotation> annotationClass;    private Class<?>[] markerInterfaces;    public ClassPathJfinalControlScanner(BeanDefinitionRegistry registry) {        super(registry, false);    }    /**     * 配置扫描接口     * 扫描添加了markerInterfaces标志类的类或标注了annotationClass注解的类,     * 或者扫描所有类     */    public void registerFilters() {        // if specified, use the given annotation and / or marker interface        if (this.annotationClass != null) {            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));        }        // override AssignableTypeFilter to ignore matches on the actual marker interface        if (this.markerInterfaces != null) {            for (Class<?> markerInterface : markerInterfaces) {                addIncludeFilter(new AssignableTypeFilter(markerInterface));            }        }    }    /**     * 重写ClassPathBeanDefinitionScanner的doScan方法,以便在我们自己的逻辑中调用     * @param basePackages     * @return     */    @Override    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);        if (beanDefinitions.isEmpty()) {            log.warn("No Jfinal Controller was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");        }        return beanDefinitions;    }    /**     * 判断bean是否满足条件,可以被加载到Spring中,markerInterfaces标志类功能再此处实现     * @param beanDefinition     * @return true: 可以被加载到Spring中     */    @Override    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {        Boolean flag = false;        for (Class<?> markerInterface : markerInterfaces) {            flag = markerInterface.getName().equals(beanDefinition.getMetadata().getSuperClassName());            if (!flag) {                String[] interfaceNames = beanDefinition.getMetadata().getInterfaceNames();                for (String interfaceName : interfaceNames) {                    flag = markerInterface.getName().equals(interfaceName);                    if (flag) {                        return flag;                    }                }            }            if (flag) {                return flag;            }        }        return flag;    }}