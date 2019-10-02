/** * $Id: SpringJfinalConfiguration.java,v 1.0 2019-07-14 14:32 chenmin Exp $ */package com.github.artislong.config;import com.google.common.collect.Maps;import com.jfinal.captcha.CaptchaCache;import com.jfinal.captcha.ICaptchaCache;import com.jfinal.config.JFinalConfig;import com.jfinal.core.ActionHandler;import com.jfinal.core.ControllerFactory;import com.jfinal.core.JFinalFilter;import com.jfinal.ext.proxy.CglibProxyFactory;import com.jfinal.json.IJsonFactory;import com.jfinal.json.JacksonFactory;import com.jfinal.log.ILogFactory;import com.jfinal.log.JdkLogFactory;import com.jfinal.log.Log4jLogFactory;import com.jfinal.plugin.IPlugin;import com.jfinal.plugin.activerecord.ActiveRecordPlugin;import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;import com.jfinal.plugin.activerecord.IContainerFactory;import com.jfinal.plugin.activerecord.cache.EhCache;import com.jfinal.plugin.activerecord.cache.ICache;import com.jfinal.plugin.activerecord.dialect.*;import com.jfinal.plugin.cron4j.Cron4jPlugin;import com.jfinal.plugin.ehcache.EhCachePlugin;import com.jfinal.proxy.ProxyFactory;import com.jfinal.render.IRenderFactory;import com.jfinal.render.RenderFactory;import com.jfinal.template.ext.spring.JFinalViewResolver;import com.jfinal.template.source.ClassPathSourceFactory;import com.jfinal.template.source.ISourceFactory;import com.jfinal.template.source.StringSource;import com.jfinal.token.ITokenCache;import com.jfinal.token.Token;import it.sauronsoftware.cron4j.Scheduler;import it.sauronsoftware.cron4j.Task;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.autoconfigure.cache.CacheProperties;import org.springframework.boot.autoconfigure.condition.*;import org.springframework.boot.context.properties.ConfigurationProperties;import org.springframework.boot.context.properties.EnableConfigurationProperties;import org.springframework.boot.jdbc.DatabaseDriver;import org.springframework.boot.web.servlet.FilterRegistrationBean;import org.springframework.cache.CacheManager;import org.springframework.cache.interceptor.CacheAspectSupport;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.Primary;import org.springframework.core.io.Resource;import org.springframework.core.io.support.PathMatchingResourcePatternResolver;import org.springframework.core.io.support.ResourcePatternResolver;import org.springframework.util.ObjectUtils;import org.springframework.util.ReflectionUtils;import org.springframework.web.servlet.view.AbstractTemplateViewResolver;import javax.annotation.PostConstruct;import javax.servlet.*;import javax.servlet.http.HttpServletResponse;import javax.sql.DataSource;import java.io.BufferedReader;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.lang.reflect.Method;import java.util.ArrayList;import java.util.Arrays;import java.util.List;import java.util.Map;/** * @author 陈敏 * @version $Id: SpringJfinalConfiguration.java,v 1.1 2019-07-14 14:32 chenmin Exp $ * Created on 2019-07-14 14:32 * My blog： https://www.chenmin.info */@Slf4j@Configuration@EnableConfigurationProperties({JfinalProperties.class, CacheProperties.class})@ConditionalOnClass(name = "com.jfinal.core.JFinal")@ConditionalOnProperty(prefix = "jfinal", name = "enabled", havingValue = "true", matchIfMissing = true)public class SpringJfinalConfiguration {    @Autowired    private JfinalProperties jfinalProperties;    private Map<DatabaseDriver, Dialect> dialectMap = Maps.newHashMap();    @PostConstruct    public void initDialect() {        dialectMap.put(DatabaseDriver.MYSQL, new MysqlDialect());        dialectMap.put(DatabaseDriver.ORACLE, new OracleDialect());        dialectMap.put(DatabaseDriver.SQLSERVER, new SqlServerDialect());        dialectMap.put(DatabaseDriver.SQLITE, new Sqlite3Dialect());        dialectMap.put(DatabaseDriver.POSTGRESQL, new PostgreSqlDialect());    }    @Bean    @ConfigurationProperties("jfinal")    @ConditionalOnMissingBean(AbstractTemplateViewResolver.class)    public AbstractTemplateViewResolver jFinalViewResolver(ISourceFactory sourceFactory) {        JFinalViewResolver jFinalViewResolver = new JFinalViewResolver();        jFinalViewResolver.setSourceFactory(sourceFactory);        return jFinalViewResolver;    }    @Bean    @ConditionalOnMissingBean(JFinalConfig.class)    public JFinalConfig jFinalConfig() {        return new DefaultJFinalConfig();    }    @Bean    @ConditionalOnMissingBean(ISourceFactory.class)    public ISourceFactory sourceFactory() {        return new ClassPathSourceFactory();    }    @Bean    @ConditionalOnMissingBean(IJsonFactory.class)    public IJsonFactory jsonFactory() {        return new JacksonFactory();    }    @Bean    @ConditionalOnMissingBean(IRenderFactory.class)    public IRenderFactory renderFactory() {        return new RenderFactory();    }    @Bean    @ConditionalOnMissingBean(ICaptchaCache.class)    public ICaptchaCache captchaCache() {        return new CaptchaCache();    }    @Bean    @ConditionalOnMissingBean(ILogFactory.class)    @ConditionalOnClass(name = "org.apache.log4j.Level")    public ILogFactory log4jLogFactory() {        return new Log4jLogFactory();    }    @Bean    @ConditionalOnMissingBean(ILogFactory.class)    @ConditionalOnMissingClass(value = "org.apache.log4j.Level")    public ILogFactory jdkLogFactory() {        return new JdkLogFactory();    }    @Bean    @ConditionalOnMissingBean(ProxyFactory.class)    public ProxyFactory proxyFactory() {        return new ProxyFactory();    }    @Bean    @Primary    @ConditionalOnMissingBean(ProxyFactory.class)    @ConditionalOnClass(name = "net.sf.cglib.proxy.Enhancer")    public ProxyFactory cgLibProxyFactory() {        return new CglibProxyFactory();    }    @Bean    @ConditionalOnMissingBean(ControllerFactory.class)    public ControllerFactory controllerFactory() {        return new ControllerFactory();    }    @Bean    @ConditionalOnMissingBean(ActionHandler.class)    public ActionHandler actionHandler() {        return new ActionHandler();    }    @Bean    @ConditionalOnMissingBean(ICache.class)    @ConditionalOnClass(name = "org.springframework.cache.CacheManager")    public ICache cache() {        return new EhCache();    }    @Bean    @ConditionalOnMissingBean(ITokenCache.class)    public ITokenCache tokenCache() {        return new ITokenCache() {            private List<Token> tokens = new ArrayList<Token>();            @Override            public void put(Token token) {                tokens.add(token);            }            @Override            public void remove(Token token) {                tokens.remove(token);            }            @Override            public boolean contains(Token token) {                return tokens.contains(token);            }            @Override            public List<Token> getAll() {                return tokens;            }        };    }    @Bean    @ConditionalOnMissingBean(IContainerFactory.class)    public IContainerFactory containerFactory() {        return new CaseInsensitiveContainerFactory();    }    @Bean    public FilterRegistrationBean filterRegistrationBean(JFinalConfig jFinalConfig) {        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();        filterRegistrationBean.setFilter(jFinalFilter(jFinalConfig));        filterRegistrationBean.addUrlPatterns("/*");        return filterRegistrationBean;    }    @Bean    public Filter jFinalFilter(JFinalConfig jFinalConfig) {        return new JFinalFilter(jFinalConfig) {            @Override            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {                HttpServletResponse httpServletResponse = (HttpServletResponse) res;                if (jfinalProperties.getOrigin()) {                    httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");                    httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET");                    httpServletResponse.setHeader("Access-Control-Max-Age", "3600");                    httpServletResponse.setHeader("Access-Control-Allow-Headers",                            "Content-Type, Content-Length, Authorization, Accept, X-Requested-With, Admin-Token");                }                super.doFilter(req, httpServletResponse, chain);            }        };    }    @Bean    public IPlugin activeRecordPlugin(ICache cache,                                      DataSource dataSource,                                      IContainerFactory containerFactory) {        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(dataSource);        activeRecordPlugin.setCache(cache);        try {            DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(dataSource.getConnection().getMetaData().getURL());            Dialect dialect;            if (!ObjectUtils.isEmpty(jfinalProperties.getDialect())) {                dialect = jfinalProperties.getDialect().newInstance();            } else {                dialect = dialectMap.get(databaseDriver);            }            if (ObjectUtils.isEmpty(dialect)) {                throw new IllegalArgumentException("dialect not be found!");            }            activeRecordPlugin.setDialect(dialect);        } catch (Exception e) {            e.printStackTrace();        }        if (!ObjectUtils.isEmpty(jfinalProperties.getShowSql())) {            activeRecordPlugin.setShowSql(jfinalProperties.getShowSql());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDevMode())) {            activeRecordPlugin.setDevMode(jfinalProperties.getDevMode());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getTransactionLevel())) {            activeRecordPlugin.setTransactionLevel(jfinalProperties.getTransactionLevel());        }        activeRecordPlugin.setContainerFactory(containerFactory);        getSqlTemplates(activeRecordPlugin, jfinalProperties.getSqlTemplates());        getMappingKet(activeRecordPlugin, jfinalProperties.getKitClasses());        return activeRecordPlugin;    }    private void getMappingKet(ActiveRecordPlugin activeRecordPlugin, List<String> kitClasses) {        if (!ObjectUtils.isEmpty(kitClasses)) {            kitClasses.forEach(kitClass -> {                try {                    Class<?> mappingKitClass = Class.forName(kitClass);                    Object mappingKit = mappingKitClass.newInstance();                    Method mappingMethod = ReflectionUtils.findMethod(mappingKitClass, "mapping",                            activeRecordPlugin.getClass());                    ReflectionUtils.invokeMethod(mappingMethod, mappingKit, activeRecordPlugin);                } catch (Exception e) {                    log.warn("{} not found in classpath", kitClass);                }            });        }    }    private void getSqlTemplates(ActiveRecordPlugin arp, List<String> sqlTemplates) {        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();        List<Resource> resources = new ArrayList<Resource>();        if (!ObjectUtils.isEmpty(sqlTemplates)) {            sqlTemplates.forEach(sqlTemplate -> {                if (sqlTemplate != null) {                    try {                        Resource[] templates = resourceResolver.getResources(sqlTemplate);                        resources.addAll(Arrays.asList(templates));                    } catch (IOException e) {                        log.warn("{} path not found in classpath", sqlTemplate);                    }                    resources.forEach(resource -> {                        StringBuilder content = null;                        try {                            content = getContentByStream(resource.getInputStream());                            arp.addSqlTemplate(new StringSource(content, true));                        } catch (IOException e) {                            e.printStackTrace();                        }                    });                }            });        }    }    private StringBuilder getContentByStream(InputStream inputStream) {        StringBuilder stringBuilder = new StringBuilder();        try {            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));            String line;            while ((line = br.readLine()) != null) {                stringBuilder.append(line);            }        } catch (Exception e) {            e.printStackTrace();        }        return stringBuilder;    }    @Bean    @ConditionalOnClass(Scheduler.class)    public IPlugin cron4jPlugin() {        //cron定时器        Map<String, JfinalProperties.TaskInfo> taskInfoMap = this.jfinalProperties.getTaskInfoMap();        Cron4jPlugin cron4jPlugin = new Cron4jPlugin();        taskInfoMap.forEach((key, taskInfo) -> {            try {                String taskClass = taskInfo.getTask();                Object task = Class.forName(taskClass).newInstance();                if (!(task instanceof Runnable) && !(task instanceof Task)) {                    throw new IllegalArgumentException("Task 必须是 Runnable、ITask、ProcessTask 或者 Task 类型");                }                if (task instanceof Runnable) {                    cron4jPlugin.addTask(taskInfo.getCron(), (Runnable) task, taskInfo.getDaemon(), taskInfo.getEnable());                }                if (task instanceof Task) {                    cron4jPlugin.addTask(taskInfo.getCron(), (Task) task, taskInfo.getDaemon(), taskInfo.getEnable());                }            } catch (Exception e) {                log.error("Task [] 加载失败", key);            }        });        return cron4jPlugin;    }    @Bean    @ConditionalOnClass(name = "org.springframework.cache.CacheManager")    @ConditionalOnBean(CacheAspectSupport.class)    @ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")    public IPlugin ehCachePlugin(CacheProperties cacheProperties) {        try {            return new EhCachePlugin(cacheProperties.getEhcache().getConfig().getInputStream());        } catch (IOException e) {            log.warn("{} ehcache not found in classpath", cacheProperties.getEhcache().getConfig().getFilename());        }        return null;    }}