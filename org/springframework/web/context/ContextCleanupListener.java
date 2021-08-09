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

package org.springframework.web.context;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.DisposableBean;

/**
 * Web application listener that cleans up remaining disposable attributes
 * in the ServletContext, i.e. attributes which implement {@link DisposableBean}
 * and haven't been removed before. This is typically used for destroying objects
 * in "application" scope, for which the lifecycle implies destruction at the
 * very end of the web application's shutdown phase.
 *
 * <p>
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.support.ServletContextScope
 * @see ContextLoaderListener
 */
public class ContextCleanupListener implements ServletContextListener {

	private static final Log logger = LogFactory.getLog(ContextCleanupListener.class);


	@Override
	public void contextInitialized(ServletContextEvent event) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		cleanupAttributes(event.getServletContext());
	}


	/**
	 * Find all ServletContext attributes which implement {@link DisposableBean}
	 * and destroy them, removing all affected ServletContext attributes eventually.
	 * <p>
	 * 清理ServletContext中剩余一次性属性的Web应用程序侦听器,即实现{@link DisposableBean}并且之前未被删除的属性。
	 * 此通常用于销毁"应用程序"范围内的对象,生命周期意味着在Web应用程序关闭阶段结束。
	 * 
	 * 
	 * @param sc the ServletContext to check
	 */
	static void cleanupAttributes(ServletContext sc) {
		Enumeration<String> attrNames = sc.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			if (attrName.startsWith("org.springframework.")) {
				Object attrValue = sc.getAttribute(attrName);
				if (attrValue instanceof DisposableBean) {
					try {
						((DisposableBean) attrValue).destroy();
					}
					catch (Throwable ex) {
						logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
					}
				}
			}
		}
	}

}
