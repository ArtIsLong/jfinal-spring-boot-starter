/** * $Id: DefaultJFinalConfig.java,v 1.0 2019-08-08 21:57 chenmin Exp $ */package com.github.artislong.config;import com.github.artislong.annotation.RouterPath;import com.jfinal.aop.Interceptor;import com.jfinal.captcha.ICaptchaCache;import com.jfinal.config.*;import com.jfinal.core.ActionHandler;import com.jfinal.core.Controller;import com.jfinal.core.ControllerFactory;import com.jfinal.handler.Handler;import com.jfinal.json.IJsonFactory;import com.jfinal.log.ILogFactory;import com.jfinal.plugin.IPlugin;import com.jfinal.proxy.ProxyFactory;import com.jfinal.render.IRenderFactory;import com.jfinal.template.Directive;import com.jfinal.template.Engine;import com.jfinal.token.ITokenCache;import lombok.Data;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.autoconfigure.web.ServerProperties;import org.springframework.context.ApplicationContext;import org.springframework.context.ApplicationContextAware;import org.springframework.util.ObjectUtils;import java.util.Map;/** * JFinalConfig默认配置类 * @author 陈敏 * @version $Id: DefaultJFinalConfig.java,v 1.1 2019-08-08 21:57 chenmin Exp $ * Created on 2019-08-08 21:57 * My blog： https://www.chenmin.info */@Slf4j@Datapublic class DefaultJFinalConfig extends JFinalConfig implements ApplicationContextAware {    @Autowired    private JfinalProperties jfinalProperties;    @Autowired    private ServerProperties serverProperties;    @Autowired    private IJsonFactory jsonFactory;    @Autowired    private IRenderFactory renderFactory;    @Autowired    private ICaptchaCache captchaCache;    @Autowired    private ILogFactory logFactory;    @Autowired    private ProxyFactory proxyFactory;    @Autowired    private ControllerFactory controllerFactory;    @Autowired    private ITokenCache tokenCache;    @Autowired    private ActionHandler actionHandler;    private ApplicationContext applicationContext;    @Override    public void configConstant(Constants me) {        if (!ObjectUtils.isEmpty(jfinalProperties.getDevMode())) {            me.setDevMode(jfinalProperties.getDevMode());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getInjectDependency())) {            me.setInjectDependency(jfinalProperties.getInjectDependency());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getBaseUploadPath())) {            me.setBaseUploadPath(jfinalProperties.getBaseUploadPath());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getBaseDownloadPath())) {            me.setBaseDownloadPath(jfinalProperties.getBaseDownloadPath());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getConfigPluginOrder())) {            me.setConfigPluginOrder(jfinalProperties.getConfigPluginOrder());        }        me.setEncoding("UTF-8");        Map<Integer, String> errorViewMapping = jfinalProperties.getErrorViewMapping();        if (!ObjectUtils.isEmpty(errorViewMapping)) {            errorViewMapping.forEach((errorCode, errorView) -> me.setErrorView(errorCode, errorView));        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDelayInSeconds())) {            me.setFreeMarkerTemplateUpdateDelay(jfinalProperties.getDelayInSeconds());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDefaultBaseName())) {            me.setI18nDefaultBaseName(jfinalProperties.getDefaultBaseName());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDefaultLocale())) {            me.setI18nDefaultLocale(jfinalProperties.getDefaultLocale());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getInjectDependency())) {            me.setInjectDependency(jfinalProperties.getInjectDependency());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getInjectSuperClass())) {            me.setInjectSuperClass(jfinalProperties.getInjectSuperClass());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getReportAfterInvocation())) {            me.setReportAfterInvocation(jfinalProperties.getReportAfterInvocation());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getDatePattern())) {            me.setJsonDatePattern(jfinalProperties.getDatePattern());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getUrlParaSeparator())) {            me.setUrlParaSeparator(jfinalProperties.getUrlParaSeparator());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getViewExtension())) {            me.setViewExtension(jfinalProperties.getViewExtension());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getViewType())) {            me.setViewType(jfinalProperties.getViewType());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getMaxPostSize())) {            me.setMaxPostSize(jfinalProperties.getMaxPostSize());        }        me.setJsonFactory(jsonFactory);        me.setRenderFactory(renderFactory);        me.setCaptchaCache(captchaCache);        me.setLogFactory(logFactory);        me.setProxyFactory(proxyFactory);        me.setControllerFactory(controllerFactory);        me.setTokenCache(tokenCache);    }    @Override    public void configRoute(Routes me) {        loadController(me);        loadInterceptor(me);        loadRoutes(me);        if (!ObjectUtils.isEmpty(serverProperties.getServlet().getContextPath())) {            me.setBaseViewPath(serverProperties.getServlet().getContextPath());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getClearAfterMapping())) {            me.setClearAfterMapping(jfinalProperties.getClearAfterMapping());        }        if (!ObjectUtils.isEmpty(jfinalProperties.getMappingSuperClass())) {            me.setMappingSuperClass(jfinalProperties.getMappingSuperClass());        }    }    private void loadController(Routes me) {        Map<String, Controller> controllerMap = applicationContext.getBeansOfType(Controller.class);        if (!ObjectUtils.isEmpty(controllerMap)) {            controllerMap.values().forEach(controller -> {                String value = "";                RouterPath annotation = controller.getClass().getAnnotation(RouterPath.class);                if (!ObjectUtils.isEmpty(annotation)) {                    value = annotation.value();                }                if (ObjectUtils.isEmpty(value)) {                    String simpleName = controller.getClass().getSimpleName();                    value = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);                }                me.add(value, controller.getClass());            });        }    }    private void loadInterceptor(Routes me) {        Map<String, Interceptor> interceptors = applicationContext.getBeansOfType(Interceptor.class);        if (!ObjectUtils.isEmpty(interceptors)) {            interceptors.values().forEach(interceptor -> me.addInterceptor(interceptor));        }    }    private void loadRoutes(Routes me) {        Map<String, Routes> routesMap = applicationContext.getBeansOfType(Routes.class);        if (!ObjectUtils.isEmpty(routesMap)) {            routesMap.values().forEach(route -> me.add(route));        }    }    @Override    public void configEngine(Engine me) {        Map<String, Directive> directiveMap = applicationContext.getBeansOfType(Directive.class);        directiveMap.forEach((directiveName, directive) -> me.addDirective(directiveName, directive.getClass()));    }    @Override    public void configPlugin(Plugins me) {        Map<String, IPlugin> pluginMap = applicationContext.getBeansOfType(IPlugin.class);        if (!ObjectUtils.isEmpty(pluginMap)) {            pluginMap.values().forEach(plugin -> me.add(plugin));        }    }    @Override    public void configInterceptor(Interceptors me) {        Map<String, Interceptor> interceptorMap = applicationContext.getBeansOfType(Interceptor.class);        if (!ObjectUtils.isEmpty(interceptorMap)) {            interceptorMap.values().forEach(interceptor -> me.add(interceptor));        }    }    @Override    public void configHandler(Handlers me) {        Map<String, Handler> handlerMap = applicationContext.getBeansOfType(Handler.class);        if (!ObjectUtils.isEmpty(handlerMap)) {            handlerMap.values().forEach(handler -> me.add(handler));        }        me.setActionHandler(actionHandler);    }}