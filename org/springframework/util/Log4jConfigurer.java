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

package org.springframework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Convenience class that features simple methods for custom log4j configuration.
 *
 * <p>Only needed for non-default log4j initialization, for example with a custom
 * config location or a refresh interval. By default, log4j will simply read its
 * configuration from a "log4j.properties" or "log4j.xml" file in the root of
 * the classpath.
 *
 * <p>For web environments, the analogous Log4jWebConfigurer class can be found
 * in the web package, reading in its configuration from context-params in
 * {@code web.xml}. In a J2EE web application, log4j is usually set up
 * via Log4jConfigListener, delegating to Log4jWebConfigurer underneath.
 *
 * <p>
 *  便利类,具有自定义log4j配置的简单方法
 * 
 * <p>只需要非默认log4j初始化,例如使用自定义配置位置或刷新间隔默认情况下,log4j将从类路径根目录中的"log4jproperties"或"log4jxml"文件中简单地读取其配置
 * 
 *  对于Web环境,可以在Web包中找到类似的Log4jWebConfigurer类,在{@code webxml}中从context-params中读取其配置。
 * 在J2EE Web应用程序中,log4j通常通过Log4jConfigListener设置,委托给Log4jWebConfigurer下。
 * 
 * 
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see org.springframework.web.util.Log4jWebConfigurer
 * @see org.springframework.web.util.Log4jConfigListener
 * @deprecated as of Spring 4.2.1, in favor of Apache Log4j 2
 * (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
public abstract class Log4jConfigurer {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	/** Extension that indicates a log4j XML config file: ".xml" */
	public static final String XML_FILE_EXTENSION = ".xml";


	/**
	 * Initialize log4j from the given file location, with no config file refreshing.
	 * Assumes an XML file in case of a ".xml" file extension, and a properties file
	 * otherwise.
	 * <p>
	 *  从给定的文件位置初始化log4j,没有配置文件刷新假设"xml"文件扩展名为XML文件,否则为属性文件
	 * 
	 * 
	 * @param location the location of the config file: either a "classpath:" location
	 * (e.g. "classpath:myLog4j.properties"), an absolute file URL
	 * (e.g. "file:C:/log4j.properties), or a plain absolute path in the file system
	 * (e.g. "C:/log4j.properties")
	 * @throws FileNotFoundException if the location specifies an invalid file path
	 */
	public static void initLogging(String location) throws FileNotFoundException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
		URL url = ResourceUtils.getURL(resolvedLocation);
		if (ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol()) && !ResourceUtils.getFile(url).exists()) {
			throw new FileNotFoundException("Log4j config file [" + resolvedLocation + "] not found");
		}

		if (resolvedLocation.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
			DOMConfigurator.configure(url);
		}
		else {
			PropertyConfigurator.configure(url);
		}
	}

	/**
	 * Initialize log4j from the given location, with the given refresh interval
	 * for the config file. Assumes an XML file in case of a ".xml" file extension,
	 * and a properties file otherwise.
	 * <p>Log4j's watchdog thread will asynchronously check whether the timestamp
	 * of the config file has changed, using the given interval between checks.
	 * A refresh interval of 1000 milliseconds (one second), which allows to
	 * do on-demand log level changes with immediate effect, is not unfeasible.
	 * <p><b>WARNING:</b> Log4j's watchdog thread does not terminate until VM shutdown;
	 * in particular, it does not terminate on LogManager shutdown. Therefore, it is
	 * recommended to <i>not</i> use config file refreshing in a production J2EE
	 * environment; the watchdog thread would not stop on application shutdown there.
	 * <p>
	 * 从给定位置初始化log4j,配置文件的给定刷新间隔假设有"xml"文件扩展名的XML文件,否则属性文件,否则Log4j的看门狗线程将异步检查配置的时间戳文件已更改,使用检查之间的给定时间间隔1000毫秒
	 * (1秒)的刷新间隔,允许立即执行按需日志级别更改不可行<p> <b>警告：</b>在VM关机之前,Log4j的看门狗线程不会终止;特别是它不会在LogManager关闭时终止。
	 * 因此,建议在生产J2EE环境中<i>不</i>使用配置文件刷新;看门狗线程在应用程序关闭时不会停止。
	 * 
	 * 
	 * @param location the location of the config file: either a "classpath:" location
	 * (e.g. "classpath:myLog4j.properties"), an absolute file URL
	 * (e.g. "file:C:/log4j.properties), or a plain absolute path in the file system
	 * (e.g. "C:/log4j.properties")
	 * @param refreshInterval interval between config file refresh checks, in milliseconds
	 * @throws FileNotFoundException if the location specifies an invalid file path
	 */
	public static void initLogging(String location, long refreshInterval) throws FileNotFoundException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
		File file = ResourceUtils.getFile(resolvedLocation);
		if (!file.exists()) {
			throw new FileNotFoundException("Log4j config file [" + resolvedLocation + "] not found");
		}

		if (resolvedLocation.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
			DOMConfigurator.configureAndWatch(file.getAbsolutePath(), refreshInterval);
		}
		else {
			PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), refreshInterval);
		}
	}

	/**
	 * Shut down log4j, properly releasing all file locks.
	 * <p>This isn't strictly necessary, but recommended for shutting down
	 * log4j in a scenario where the host VM stays alive (for example, when
	 * shutting down an application in a J2EE environment).
	 * <p>
	 * 关闭log4j,正确释放所有文件锁<p>这不是绝对必要的,但建议在主机VM保持活动的情况下关闭log4j(例如,在J2EE环境中关闭应用程序时)
	 * 
	 */
	public static void shutdownLogging() {
		LogManager.shutdown();
	}

	/**
	 * Set the specified system property to the current working directory.
	 * <p>This can be used e.g. for test environments, for applications that leverage
	 * Log4jWebConfigurer's "webAppRootKey" support in a web environment.
	 * <p>
	 *  将指定的系统属性设置为当前工作目录<p>这可以用于测试环境,适用于在Web环境中利用Log4jWebConfigurer的"webAppRootKey"支持的应用程序
	 * 
	 * @param key system property key to use, as expected in Log4j configuration
	 * (for example: "demo.root", used as "${demo.root}/WEB-INF/demo.log")
	 * @see org.springframework.web.util.Log4jWebConfigurer
	 */
	public static void setWorkingDirSystemProperty(String key) {
		System.setProperty(key, new File("").getAbsolutePath());
	}

}
