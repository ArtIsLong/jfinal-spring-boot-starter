/** * $Id: SpringJfinalConfiguration.java,v 1.0 2019-07-14 14:32 chenmin Exp $ */package com.github.jfinal.config;import com.github.jfinal.annotation.RouterPath;import com.google.common.collect.Maps;import com.jfinal.captcha.CaptchaCache;import com.jfinal.captcha.ICaptchaCache;import com.jfinal.config.JFinalConfig;import com.jfinal.core.ActionHandler;import com.jfinal.core.ControllerFactory;import com.jfinal.core.JFinalFilter;import com.jfinal.json.IJsonFactory;import com.jfinal.json.JacksonFactory;import com.jfinal.log.ILogFactory;import com.jfinal.log.JdkLogFactory;import com.jfinal.log.Log4jLogFactory;import com.jfinal.plugin.IPlugin;import com.jfinal.plugin.activerecord.ActiveRecordPlugin;import com.jfinal.plugin.activerecord.cache.EhCache;import com.jfinal.plugin.activerecord.cache.ICache;import com.jfinal.plugin.activerecord.dialect.*;import com.jfinal.plugin.cron4j.Cron4jPlugin;import com.jfinal.plugin.ehcache.EhCachePlugin;import com.jfinal.proxy.ProxyFactory;import com.jfinal.render.IRenderFactory;import com.jfinal.render.RenderFactory;import com.jfinal.template.ext.spring.JFinalViewResolver;import com.jfinal.template.source.ClassPathSourceFactory;import com.jfinal.template.source.ISourceFactory;import com.jfinal.template.source.StringSource;import com.jfinal.token.ITokenCache;import com.jfinal.token.Token;import it.sauronsoftware.cron4j.Scheduler;import it.sauronsoftware.cron4j.Task;import lombok.Data;import lombok.experimental.Accessors;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.BeansException;import org.springframework.beans.factory.BeanFactory;import org.springframework.beans.factory.BeanFactoryAware;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.support.BeanDefinitionRegistry;import org.springframework.boot.autoconfigure.AutoConfigurationPackages;import org.springframework.boot.autoconfigure.AutoConfigureAfter;import org.springframework.boot.autoconfigure.cache.CacheProperties;import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;import org.springframework.boot.context.properties.ConfigurationProperties;import org.springframework.boot.context.properties.EnableConfigurationProperties;import org.springframework.boot.jdbc.DatabaseDriver;import org.springframework.boot.web.servlet.FilterRegistrationBean;import org.springframework.cache.CacheManager;import org.springframework.cache.interceptor.CacheAspectSupport;import org.springframework.context.ApplicationContext;import org.springframework.context.ApplicationContextAware;import org.springframework.context.EnvironmentAware;import org.springframework.context.ResourceLoaderAware;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.Import;import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;import org.springframework.core.env.Environment;import org.springframework.core.io.Resource;import org.springframework.core.io.ResourceLoader;import org.springframework.core.io.support.PathMatchingResourcePatternResolver;import org.springframework.core.io.support.ResourcePatternResolver;import org.springframework.core.type.AnnotationMetadata;import org.springframework.util.ObjectUtils;import org.springframework.util.ReflectionUtils;import org.springframework.util.StringUtils;import org.springframework.web.servlet.view.AbstractTemplateViewResolver;import javax.sql.DataSource;import java.io.BufferedReader;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.lang.reflect.Method;import java.util.*;/** * @author 陈敏 * @version $Id: SpringJfinalConfiguration.java,v 1.1 2019-07-14 14:32 chenmin Exp $ * Created on 2019-07-14 14:32 * My blog： https://www.chenmin.info */@Slf4j@Configuration@EnableConfigurationProperties({JfinalProperties.class,CacheProperties.class})@ConditionalOnClass(name = "com.jfinal.core.JFinal")@AutoConfigureAfter(DataSourceAutoConfiguration.class)public class SpringJfinalConfiguration implements ApplicationContextAware {    @Autowired    private JfinalProperties jfinalProperties;    private Map<DatabaseDriver, Dialect> dialectMap = Maps.newHashMap();    private ApplicationContext applicationContext;    public SpringJfinalConfiguration() {        dialectMap.put(DatabaseDriver.MYSQL, new MysqlDialect());        dialectMap.put(DatabaseDriver.ORACLE, new OracleDialect());        dialectMap.put(DatabaseDriver.SQLSERVER, new SqlServerDialect());        dialectMap.put(DatabaseDriver.SQLITE, new Sqlite3Dialect());        dialectMap.put(DatabaseDriver.POSTGRESQL, new PostgreSqlDialect());    }    @Override    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {        this.applicationContext = applicationContext;    }    @Bean    @ConfigurationProperties("jfinal")    @ConditionalOnMissingBean(AbstractTemplateViewResolver.class)    public AbstractTemplateViewResolver jFinalViewResolver(ISourceFactory sourceFactory) {        JFinalViewResolver jFinalViewResolver = new JFinalViewResolver();        jFinalViewResolver.setSourceFactory(sourceFactory);        return jFinalViewResolver;    }    @Bean    @ConditionalOnMissingBean    public JFinalConfig jFinalConfig() {        return new DefaultJFinalConfig();    }    @Bean    @ConditionalOnMissingBean(ISourceFactory.class)    public ISourceFactory sourceFactory() {        return new ClassPathSourceFactory();    }    @Bean    @ConditionalOnMissingBean(IJsonFactory.class)    public IJsonFactory jsonFactory(){        return new JacksonFactory();    }    @Bean    @ConditionalOnMissingBean(IRenderFactory.class)    public IRenderFactory renderFactory() {        return new RenderFactory();    }    @Bean    @ConditionalOnMissingBean(ICaptchaCache.class)    public ICaptchaCache captchaCache() {        return new CaptchaCache();    }    @Bean    @ConditionalOnMissingBean(ILogFactory.class)    @ConditionalOnClass(name = "org.apache.log4j.Level")    public ILogFactory log4jLogFactory() {        return new Log4jLogFactory();    }    @Bean    @ConditionalOnMissingBean(ILogFactory.class)    @ConditionalOnMissingClass(value = "org.apache.log4j.Level")    public ILogFactory jdkLogFactory() {        return new JdkLogFactory();    }    @Bean    @ConditionalOnMissingBean(ProxyFactory.class)    public ProxyFactory proxyFactory() {        return new ProxyFactory();    }    @Bean    @ConditionalOnMissingBean(ControllerFactory.class)    public ControllerFactory controllerFactory() {        return new ControllerFactory();    }    @Bean    @ConditionalOnMissingBean(ActionHandler.class)    public ActionHandler actionHandler() {        return new ActionHandler();    }    @Bean    @ConditionalOnMissingBean(ICache.class)    @ConditionalOnClass(name = "org.springframework.cache.CacheManager")    public ICache cache() {        return new EhCache();    }    @Bean    @ConditionalOnMissingBean(ITokenCache.class)    public ITokenCache tokenCache() {        return new ITokenCache() {            private List<Token> tokens = new ArrayList<Token>();            @Override            public void put(Token token) {                tokens.add(token);            }            @Override            public void remove(Token token) {                tokens.remove(token);            }            @Override            public boolean contains(Token token) {                return tokens.contains(token);            }            @Override            public List<Token> getAll() {                return tokens;            }        };    }    @Bean    public FilterRegistrationBean filterRegistrationBean(JFinalConfig jFinalConfig) {        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();        filterRegistrationBean.setFilter(new JFinalFilter(jFinalConfig));        HashMap<Object, Object> params = Maps.newHashMap();        params.put("configClass", "com.github.jfinal.config.DefaultJFinalConfig");        filterRegistrationBean.setInitParameters(params);        filterRegistrationBean.addUrlPatterns("/*");        return filterRegistrationBean;    }    @Bean    public IPlugin activeRecordPlugin(ICache cache, DataSource dataSource) {        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(dataSource);        activeRecordPlugin.setCache(cache);        try {            DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(dataSource.getConnection().getMetaData().getURL());            Dialect dialect;            if (!ObjectUtils.isEmpty(jfinalProperties.getDialect())) {                dialect = jfinalProperties.getDialect().newInstance();            } else {                dialect = dialectMap.get(databaseDriver);            }            if (ObjectUtils.isEmpty(dialect)) {                throw new IllegalArgumentException("dialect not be found!");            }            activeRecordPlugin.setDialect(dialect);        } catch (Exception e) {            e.printStackTrace();        }        if (!ObjectUtils.isEmpty(jfinalProperties.getShowSql())) {            activeRecordPlugin.setShowSql(jfinalProperties.getShowSql());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getSqlTemplate())) {            activeRecordPlugin.setBaseSqlTemplatePath(jfinalProperties.getSqlTemplate());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDevMode())) {            activeRecordPlugin.setDevMode(jfinalProperties.getDevMode());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getTransactionLevel())) {            activeRecordPlugin.setTransactionLevel(jfinalProperties.getTransactionLevel());        }        getSqlTemplates(activeRecordPlugin, jfinalProperties.getSqlTemplate());        getMappingKet(activeRecordPlugin, jfinalProperties.getKitClasses());        return activeRecordPlugin;    }    private void getMappingKet(ActiveRecordPlugin activeRecordPlugin, List<String> kitClasses) {        if (!ObjectUtils.isEmpty(kitClasses)) {            kitClasses.forEach(kitClass -> {                try {                    Class<?> mappingKitClass = Class.forName(kitClass);                    Object mappingKit = mappingKitClass.newInstance();                    Method mappingMethod = ReflectionUtils.findMethod(mappingKitClass, "mapping",                            activeRecordPlugin.getClass());                    ReflectionUtils.invokeMethod(mappingMethod, mappingKit, activeRecordPlugin);                } catch (Exception e) {                    log.warn("{} not found in classpath", kitClass);                }            });        }    }    private void getSqlTemplates(ActiveRecordPlugin arp, String sqlTemplate) {        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();        List<Resource> resources = new ArrayList<Resource>();        if (sqlTemplate != null) {            try {                Resource[] sqlTemplates = resourceResolver.getResources(sqlTemplate);                resources.addAll(Arrays.asList(sqlTemplates));            } catch (IOException e) {                log.warn("{} path not found in classpath", sqlTemplate);            }            resources.forEach(resource -> {                StringBuilder content = null;                try {                    content = getContentByStream(resource.getInputStream());                    arp.addSqlTemplate(new StringSource(content, true));                } catch (IOException e) {                    e.printStackTrace();                }            });        }    }    private StringBuilder getContentByStream(InputStream inputStream) {        StringBuilder stringBuilder = new StringBuilder();        try {            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));            String line;            while ((line = br.readLine()) != null) {                stringBuilder.append(line);            }        } catch (Exception e) {            e.printStackTrace();        }        return stringBuilder;    }    @Bean    @ConditionalOnClass(Scheduler.class)    public IPlugin cron4jPlugin() {        //cron定时器        Map<String, JfinalProperties.TaskInfo> taskInfoMap = this.jfinalProperties.getTaskInfoMap();        Cron4jPlugin cron4jPlugin = new Cron4jPlugin();        taskInfoMap.forEach((key, taskInfo) -> {            try {                String taskClass = taskInfo.getTask();                Object task = Class.forName(taskClass).newInstance();                if ( !(task instanceof Runnable) && !(task instanceof Task)) {                    throw new IllegalArgumentException("Task 必须是 Runnable、ITask、ProcessTask 或者 Task 类型");                }                if (task instanceof Runnable) {                    cron4jPlugin.addTask(taskInfo.getCron(), (Runnable) task, taskInfo.getDaemon(), taskInfo.getEnable());                }                if (task instanceof Task) {                    cron4jPlugin.addTask(taskInfo.getCron(), (Task) task, taskInfo.getDaemon(), taskInfo.getEnable());                }            } catch (Exception e) {                log.error("Task [] 加载失败", key);            }        });        return cron4jPlugin;    }    @Bean    @ConditionalOnClass(name = "org.springframework.cache.CacheManager")    @ConditionalOnBean(CacheAspectSupport.class)    @ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")    public IPlugin ehCachePlugin(CacheProperties cacheProperties) {        try {            return new EhCachePlugin(cacheProperties.getEhcache().getConfig().getInputStream());        } catch (IOException e) {            log.warn("{} ehcache not found in classpath", cacheProperties.getEhcache().getConfig().getFilename());        }        return null;    }    @Data    @Accessors(chain = true)    public static class AutoConfiguredJfinalControlScannerRegistrar            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {        private BeanFactory beanFactory;        private ResourceLoader resourceLoader;        private Environment environment;        @Override        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {            log.debug("Searching for router annotated with @RouterPath");            ClassPathJfinalControlScanner scanner = new ClassPathJfinalControlScanner(registry);            try {                if (this.resourceLoader != null) {                    scanner.setResourceLoader(this.resourceLoader);                }                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);                if (log.isDebugEnabled()) {                    for (String pkg : packages) {                        log.debug("Using auto-configuration base package '{}'", pkg);                    }                }                scanner.setAnnotationClass(RouterPath.class);                scanner.registerFilters();                scanner.doScan(StringUtils.toStringArray(packages));            } catch (IllegalStateException ex) {                log.debug("Could not determine auto-configuration package, automatic router scanning disabled.", ex);            }        }        @Override        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {            this.beanFactory = beanFactory;        }        @Override        public void setEnvironment(Environment environment) {            this.environment = environment;        }        @Override        public void setResourceLoader(ResourceLoader resourceLoader) {            this.resourceLoader = resourceLoader;        }    }    @Configuration    @Import({AutoConfiguredJfinalControlScannerRegistrar.class})    public static class JfinalControlRegistrarNotFoundConfiguration {    }}