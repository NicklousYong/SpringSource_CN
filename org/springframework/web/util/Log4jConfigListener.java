/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener for custom log4j initialization in a web environment.
 * Delegates to {@link Log4jWebConfigurer} (see its javadoc for configuration details).
 *
 * <b>WARNING: Assumes an expanded WAR file</b>, both for loading the configuration
 * file and for writing the log files. If you want to keep your WAR unexpanded or
 * don't need application-specific log files within the WAR directory, don't use
 * log4j setup within the application (thus, don't use Log4jConfigListener or
 * Log4jConfigServlet). Instead, use a global, VM-wide log4j setup (for example,
 * in JBoss) or JDK 1.4's {@code java.util.logging} (which is global too).
 *
 * <p>This listener should be registered before ContextLoaderListener in {@code web.xml}
 * when using custom log4j initialization.
 *
 * <p>
 *  用于在Web环境中定制log4j初始化的引导侦听器委托{@link Log4jWebConfigurer}(有关配置详细信息,请参阅其javadoc)
 * 
 * <b>警告：假设扩展的WAR文件</b>,用于加载配置文件和编写日志文件如果要保留WAR未展开或不需要WAR目录中的特定于应用程序的日志文件,不要在应用程序中使用log4j设置(因此,不要使用Log4j
 * ConfigListener或Log4jConfigServlet)而是使用全局,VM范围的log4j设置(例如在JBoss中)或JDK 14的{@code javautillogging}(这是全局的
 * 
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see Log4jWebConfigurer
 * @see org.springframework.web.context.ContextLoaderListener
 * @see WebAppRootListener
 * @deprecated as of Spring 4.2.1, in favor of Apache Log4j 2
 * (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
public class Log4jConfigListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Log4jWebConfigurer.initLogging(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Log4jWebConfigurer.shutdownLogging(event.getServletContext());
	}

}
