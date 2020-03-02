/** * $Id: IndexController.java,v 1.0 2019-08-09 09:56 chenmin Exp $ */package com.github.artislong.controller;import com.github.artislong.annotation.RouterPath;import com.jfinal.aop.Before;import com.jfinal.core.Controller;import com.jfinal.core.paragetter.Para;import com.jfinal.kit.Kv;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;import com.jfinal.plugin.activerecord.tx.Tx;import com.jfinal.plugin.activerecord.tx.TxConfig;import model.Theme;import java.util.List;/** * @author 陈敏 * @version $Id: IndexController.java,v 1.1 2019-08-09 09:56 chenmin Exp $ * Created on 2019-08-09 09:56 * My blog： https://www.chenmin.info */@RouterPath("/index")public class IndexController extends Controller {    public void index(@Para(value = "title", defaultValue = "") String title) {        List<Record> records = Db.find(Db.getSqlPara("theme.queryTheme", Kv.by("title", title)));        renderJson(records);    }    @Before(Tx.class)    public void insert(@Para Theme theme) {        theme.save();    }}