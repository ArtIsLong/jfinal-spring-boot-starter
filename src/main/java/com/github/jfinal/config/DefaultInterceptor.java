/** * $Id: DefaultInterceptor.java,v 1.0 2019-07-27 15:27 chenmin Exp $ */package com.github.jfinal.config;import com.jfinal.aop.Interceptor;import com.jfinal.aop.Invocation;import org.springframework.stereotype.Component;/** * @author 陈敏 * @version $Id: DefaultInterceptor.java,v 1.1 2019-07-27 15:27 chenmin Exp $ * Created on 2019-07-27 15:27 * My blog： https://www.chenmin.info */public class DefaultInterceptor implements Interceptor {    @Override    public void intercept(Invocation invocation) {        invocation.invoke();    }}