/** * $Id: JfinalProperties.java,v 1.0 2019-07-14 00:46 chenmin Exp $ */package com.github.artislong.config;import com.google.common.collect.Maps;import com.jfinal.plugin.activerecord.dialect.Dialect;import com.jfinal.render.ViewType;import lombok.Data;import lombok.experimental.Accessors;import lombok.extern.slf4j.Slf4j;import org.assertj.core.util.Lists;import org.springframework.boot.context.properties.ConfigurationProperties;import org.springframework.util.ObjectUtils;import org.springframework.validation.annotation.Validated;import javax.validation.constraints.NotBlank;import java.util.List;import java.util.Map;/** * @author 陈敏 * @version $Id: JfinalProperties.java,v 1.1 2019-07-14 00:46 chenmin Exp $ * Created on 2019-07-14 00:46 * My blog： https://www.chenmin.info */@Slf4j@Data@Validated@Accessors(chain = true)@ConfigurationProperties(prefix = "jfinal")public class JfinalProperties {    /**     * SQL模板路径     */    private List<String> sqlTemplates = Lists.newArrayList();    /**     * kit类全路径     */    private List<String> kitClasses = Lists.newArrayList();    /**     * 是否打开开发模式     */    private Boolean devMode = false;    /**     * 是否显示查询SQL     */    private Boolean showSql;    /**     * 数据库方言:默认支持MYSQL,ORACLE,SQLSERVER,SQLITE,POSTGRESQL     */    private Class<? extends Dialect> dialect;    private Boolean injectDependency = false;    private String baseUploadPath;    private String baseDownloadPath;    private Integer maxPostSize;    private Integer delayInSeconds;    private String defaultBaseName;    private String defaultLocale;    private Boolean injectSuperClass;    private Boolean reportAfterInvocation;    private String datePattern;    private String urlParaSeparator;    private String viewExtension;    private ViewType viewType;    private Boolean clearAfterMapping;    private Boolean mappingSuperClass;    private Boolean createSession;    private String baseTemplatePath;    private Boolean sessionInView;    private String cacheName;    /**     * 是否允许覆盖Request     */    private Boolean allowRequestOverride;    /**     * 是否允许覆盖Session     */    private Boolean allowSessionOverride;    /**     * 事务级别     */    private Integer transactionLevel;    /**     * 插件加载顺序     */    private Integer configPluginOrder;    /**     * Error View映射     */    private Map<Integer, String> errorViewMapping = Maps.newLinkedHashMap();    /**     * jfinal定制任务配置     */    private Map<String, TaskInfo> taskInfoMap = Maps.newLinkedHashMap();    /**     * 是否打开跨域设置     */    private Boolean origin = false;    @Data    @Accessors(chain = true)    public static class TaskInfo {        /**         * 时间表达式         */        @NotBlank        private String cron;        /**         * 任务类         */        @NotBlank        private String task;        /**         * 是否开启守护进程         */        private Boolean daemon;        /**         * 是否开启任务,默认开启         */        private Boolean enable = true;        public TaskInfo() {        }        public TaskInfo(String cron, String task, boolean daemon, boolean enable) {            if (ObjectUtils.isEmpty(cron)) {                throw new IllegalArgumentException("cron 不能为空.");            }            if (ObjectUtils.isEmpty(task)) {                throw new IllegalArgumentException("task 不能为 null.");            }            this.cron = cron.trim();            this.task = task;            this.daemon = daemon;            this.enable = enable;        }    }}