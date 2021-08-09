/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.util;

import java.io.FileNotFoundException;
import javax.servlet.ServletContext;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Convenience class that performs custom log4j initialization for web environments,
 * allowing for log file paths within the web application, with the option to
 * perform automatic refresh checks (for runtime changes in logging configuration).
 *
 * <p><b>WARNING: Assumes an expanded WAR file</b>, both for loading the configuration
 * file and for writing the log files. If you want to keep your WAR unexpanded or
 * don't need application-specific log files within the WAR directory, don't use
 * log4j setup within the application (thus, don't use Log4jConfigListener or
 * Log4jConfigServlet). Instead, use a global, VM-wide log4j setup (for example,
 * in JBoss) or JDK 1.4's {@code java.util.logging} (which is global too).
 *
 * <p>Supports three init parameters at the servlet context level (that is,
 * context-param entries in web.xml):
 *
 * <ul>
 * <li><i>"log4jConfigLocation":</i><br>
 * Location of the log4j config file; either a "classpath:" location (e.g.
 * "classpath:myLog4j.properties"), an absolute file URL (e.g. "file:C:/log4j.properties),
 * or a plain path relative to the web application root directory (e.g.
 * "/WEB-INF/log4j.properties"). If not specified, default log4j initialization
 * will apply ("log4j.properties" or "log4j.xml" in the class path; see the
 * log4j documentation for details).
 * <li><i>"log4jRefreshInterval":</i><br>
 * Interval between config file refresh checks, in milliseconds. If not specified,
 * no refresh checks will happen, which avoids starting log4j's watchdog thread.
 * <li><i>"log4jExposeWebAppRoot":</i><br>
 * Whether the web app root system property should be exposed, allowing for log
 * file paths relative to the web application root directory. Default is "true";
 * specify "false" to suppress expose of the web app root system property. See
 * below for details on how to use this system property in log file locations.
 * </ul>
 *
 * <p>Note: {@code initLogging} should be called before any other Spring activity
 * (when using log4j), for proper initialization before any Spring logging attempts.
 *
 * <p>Log4j's watchdog thread will asynchronously check whether the timestamp
 * of the config file has changed, using the given interval between checks.
 * A refresh interval of 1000 milliseconds (one second), which allows to
 * do on-demand log level changes with immediate effect, is not unfeasible.

 * <p><b>WARNING:</b> Log4j's watchdog thread does not terminate until VM shutdown;
 * in particular, it does not terminate on LogManager shutdown. Therefore, it is
 * recommended to <i>not</i> use config file refreshing in a production J2EE
 * environment; the watchdog thread would not stop on application shutdown there.
 *
 * <p>By default, this configurer automatically sets the web app root system property,
 * for "${key}" substitutions within log file locations in the log4j config file,
 * allowing for log file paths relative to the web application root directory.
 * The default system property key is "webapp.root", to be used in a log4j config
 * file like as follows:
 *
 * <p>{@code log4j.appender.myfile.File=${webapp.root}/WEB-INF/demo.log}
 *
 * <p>Alternatively, specify a unique context-param "webAppRootKey" per web application.
 * For example, with "webAppRootKey = "demo.root":
 *
 * <p>{@code log4j.appender.myfile.File=${demo.root}/WEB-INF/demo.log}
 *
 * <p><b>WARNING:</b> Some containers (like Tomcat) do <i>not</i> keep system properties
 * separate per web app. You have to use unique "webAppRootKey" context-params per web
 * app then, to avoid clashes. Other containers like Resin do isolate each web app's
 * system properties: Here you can use the default key (i.e. no "webAppRootKey"
 * context-param at all) without worrying.
 *
 * <p>
 * 为Web环境执行自定义log4j初始化的便利类,允许Web应用程序中的日志文件路径,并可以执行自动刷新检查(对于日志记录配置中的运行时更改)
 * 
 *  <p> <b>警告：假设扩展的WAR文件</b>,用于加载配置文件和编写日志文件如果要保留WAR未展开或不需要特定于应用程序的日志文件WAR目录中,不要在应用程序中使用log4j设置(因此,不要使用L
 * og4jConfigListener或Log4jConfigServlet)而是使用全局的,VM范围的log4j设置(例如在JBoss中)或JDK 14的{@code javautillogging}(
 * 这也是全球性的)。
 * 
 * <p>在servlet上下文级别(即webxml中的上下文参数条目)中支持三个init参数：
 * 
 * <ul>
 * <li> <i>"log4jConfigLocation"：</i> <br> log4j配置文件的位置;绝对文件URL(例如"file：C：/ log4jproperties")或相对于Web应用程序
 * 根目录的简单路径(例如"/ WEB-INF /")中的"classpath："位置(例如"classpath：myLog4jproperties" log4jproperties")如果未指定,则将在类
 * 路径中应用默认的log4j初始化("log4jproperties"或"log4jxml");有关详细信息,请参阅log4j文档)<li> <i>"log4jRefreshInterval"：</i> 
 * <br>间隔在配置文件刷新检查之间,以毫秒为单位如果未指定,则不会进行刷新检查,这样可以避免启动log4j的看门狗线程<li> <i>"log4jExposeWebAppRoot"：</i> <br>是
 * 否应该公开Web应用程序根系统属性,允许相对于Web应用程序根目录的日志文件路径默认为"true";指定"false"以禁止暴露Web应用程序根系统属性有关如何在日志文件位置中使用此系统属性的详细信息,
 * 请参阅下文。
 * </ul>
 * 
 * 注意：在任何其他Spring活动(使用log4j之前)应该调用{@code initLogging},以便在任何Spring日志记录尝试之前进行适当的初始化
 * 
 *  <p> Log4j的看门狗线程将异步检查配置文件的时间戳是否已更改,使用检查之间的给定间隔刷新间隔为1000毫秒(1秒),允许立即执行按需日志级别更改,不是不可行的
 * 
 *  <p> <b>警告：</b> Log4j的看门狗线程在VM关机之前不会终止;特别是它不会在LogManager关闭时终止。
 * 因此,建议在生产J2EE环境中<i>不</i>使用配置文件刷新;看门狗线程在应用程序关闭时不会停止。
 * 
 * @author Juergen Hoeller
 * @author Marten Deinum
 * @since 12.08.2003
 * @see org.springframework.util.Log4jConfigurer
 * @see Log4jConfigListener
 * @deprecated as of Spring 4.2.1, in favor of Apache Log4j 2
 * (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
public abstract class Log4jWebConfigurer {

	/** Parameter specifying the location of the log4j config file */
	public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

	/** Parameter specifying the refresh interval for checking the log4j config file */
	public static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";

	/** Parameter specifying whether to expose the web app root system property */
	public static final String EXPOSE_WEB_APP_ROOT_PARAM = "log4jExposeWebAppRoot";


	/**
	 * Initialize log4j, including setting the web app root system property.
	 * <p>
	 * 
	 * <p>默认情况下,此配置程序会自动设置Web应用程序根系统属性,对于log4j配置文件中的日志文件位置中的"$ {key}"替换,允许相对于Web应用程序根目录的日志文件路径默认系统属性键是"webap
	 * proot",用于log4j配置文件,如下所示：。
	 * 
	 *  <p> {@ code log4jappendermyfileFile = $ {webapproot} / WEB-INF / demolog}
	 * 
	 *  <p>或者,为每个Web应用程序指定唯一的上下文参数"webAppRootKey"例如,使用"webAppRootKey ="demoroot"：
	 * 
	 *  <p> {@ code log4jappendermyfileFile = $ {demoroot} / WEB-INF / demolog}
	 * 
	 * <p> <b>警告：</b>某些容器(如Tomcat)不</i>将每个Web应用程序保持系统属性分开您必须为每个Web应用程序使用唯一的"webAppRootKey"上下文参数,避免冲突其他容器如Res
	 * 
	 * @param servletContext the current ServletContext
	 * @see WebUtils#setWebAppRootSystemProperty
	 */
	public static void initLogging(ServletContext servletContext) {
		// Expose the web app root system property.
		if (exposeWebAppRoot(servletContext)) {
			WebUtils.setWebAppRootSystemProperty(servletContext);
		}

		// Only perform custom log4j initialization in case of a config file.
		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location != null) {
			// Perform actual log4j initialization; else rely on log4j's default initialization.
			try {
				// Resolve property placeholders before potentially resolving a real path.
				location = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);

				// Leave a URL (e.g. "classpath:" or "file:") as-is.
				if (!ResourceUtils.isUrl(location)) {
					// Consider a plain file path as relative to the web application root directory.
					location = WebUtils.getRealPath(servletContext, location);
				}

				// Write log message to server log.
				servletContext.log("Initializing log4j from [" + location + "]");

				// Check whether refresh interval was specified.
				String intervalString = servletContext.getInitParameter(REFRESH_INTERVAL_PARAM);
				if (StringUtils.hasText(intervalString)) {
					// Initialize with refresh interval, i.e. with log4j's watchdog thread,
					// checking the file in the background.
					try {
						long refreshInterval = Long.parseLong(intervalString);
						org.springframework.util.Log4jConfigurer.initLogging(location, refreshInterval);
					}
					catch (NumberFormatException ex) {
						throw new IllegalArgumentException("Invalid 'log4jRefreshInterval' parameter: " + ex.getMessage());
					}
				}
				else {
					// Initialize without refresh check, i.e. without log4j's watchdog thread.
					org.springframework.util.Log4jConfigurer.initLogging(location);
				}
			}
			catch (FileNotFoundException ex) {
				throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage());
			}
		}
	}

	/**
	 * Shut down log4j, properly releasing all file locks
	 * and resetting the web app root system property.
	 * <p>
	 * in可以隔离每个Web应用程序的系统属性：在这里您可以使用默认键(即没有"webAppRootKey"的上下文参数),而不用担心。
	 * 
	 * 
	 * @param servletContext the current ServletContext
	 * @see WebUtils#removeWebAppRootSystemProperty
	 */
	public static void shutdownLogging(ServletContext servletContext) {
		servletContext.log("Shutting down log4j");
		try {
			org.springframework.util.Log4jConfigurer.shutdownLogging();
		}
		finally {
			// Remove the web app root system property.
			if (exposeWebAppRoot(servletContext)) {
				WebUtils.removeWebAppRootSystemProperty(servletContext);
			}
		}
	}

	/**
	 * Return whether to expose the web app root system property,
	 * checking the corresponding ServletContext init parameter.
	 * <p>
	 *  初始化log4j,包括设置Web应用程序根系统属性
	 * 
	 * 
	 * @see #EXPOSE_WEB_APP_ROOT_PARAM
	 */
	private static boolean exposeWebAppRoot(ServletContext servletContext) {
		String exposeWebAppRootParam = servletContext.getInitParameter(EXPOSE_WEB_APP_ROOT_PARAM);
		return (exposeWebAppRootParam == null || Boolean.valueOf(exposeWebAppRootParam));
	}

}
