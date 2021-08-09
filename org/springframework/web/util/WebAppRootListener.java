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
 * Listener that sets a system property to the web application root directory.
 * The key of the system property can be defined with the "webAppRootKey" init
 * parameter at the servlet context level (i.e. context-param in web.xml),
 * the default key is "webapp.root".
 *
 * <p>Can be used for toolkits that support substitution with system properties
 * (i.e. System.getProperty values), like log4j's "${key}" syntax within log
 * file locations.
 *
 * <p>Note: This listener should be placed before ContextLoaderListener in {@code web.xml},
 * at least when used for log4j. Log4jConfigListener sets the system property
 * implicitly, so there's no need for this listener in addition to it.
 *
 * <p><b>WARNING</b>: Some containers, e.g. Tomcat, do NOT keep system properties separate
 * per web app. You have to use unique "webAppRootKey" context-params per web app
 * then, to avoid clashes. Other containers like Resin do isolate each web app's
 * system properties: Here you can use the default key (i.e. no "webAppRootKey"
 * context-param at all) without worrying.
 *
 * <p><b>WARNING</b>: The WAR file containing the web application needs to be expanded
 * to allow for setting the web app root system property. This is by default not
 * the case when a WAR file gets deployed to WebLogic, for example. Do not use
 * this listener in such an environment!
 *
 * <p>
 * 将系统属性设置为Web应用程序根目录的侦听器可以使用servlet上下文级别(即webxml中的context-param)中的"webAppRootKey"init参数来定义系统属性的关键字,默认键为
 * "webapproot"。
 * 
 *  <p>可以用于支持使用系统属性(即SystemgetProperty值)替换的工具包,如日志文件位置中的log4j的"$ {key}"语法
 * 
 *  注意：此侦听器应放置在{@code webxml}中的ContextLoaderListener之前,至少在用于log4j时Log4jConfigListener隐式设置系统属性,因此除此之外不需要此
 * 侦听器。
 * 
 * @author Juergen Hoeller
 * @since 18.04.2003
 * @see WebUtils#setWebAppRootSystemProperty
 * @see Log4jConfigListener
 * @see System#getProperty
 */
public class WebAppRootListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		WebUtils.setWebAppRootSystemProperty(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		WebUtils.removeWebAppRootSystemProperty(event.getServletContext());
	}

}
