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

package org.springframework.ui.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.ThemeSource;

/**
 * Utility class for UI application context implementations.
 * Provides support for a special bean named "themeSource",
 * of type {@link org.springframework.ui.context.ThemeSource}.
 *
 * <p>
 *  UI应用程序上下文实现的实用程序类提供对名为"themeSource"的特殊bean的支持,类型为{@link orgspringframeworkuicontextThemeSource}
 * 
 * 
 * @author Jean-Pierre Pawlak
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public abstract class UiApplicationContextUtils {

	/**
	 * Name of the ThemeSource bean in the factory.
	 * If none is supplied, theme resolution is delegated to the parent.
	 * <p>
	 * 工厂中的ThemeSource bean的名称如果没有提供,则将主题解析委托给父级
	 * 
	 * 
	 * @see org.springframework.ui.context.ThemeSource
	 */
	public static final String THEME_SOURCE_BEAN_NAME = "themeSource";


	private static final Log logger = LogFactory.getLog(UiApplicationContextUtils.class);


	/**
	 * Initialize the ThemeSource for the given application context,
	 * autodetecting a bean with the name "themeSource". If no such
	 * bean is found, a default (empty) ThemeSource will be used.
	 * <p>
	 *  初始化给定应用程序上下文的ThemeSource,自动检测名为"themeSource"的bean如果没有找到这样的bean,将使用默认(空)ThemeSource
	 * 
	 * @param context current application context
	 * @return the initialized theme source (will never be {@code null})
	 * @see #THEME_SOURCE_BEAN_NAME
	 */
	public static ThemeSource initThemeSource(ApplicationContext context) {
		if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
			ThemeSource themeSource = context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
			// Make ThemeSource aware of parent ThemeSource.
			if (context.getParent() instanceof ThemeSource && themeSource instanceof HierarchicalThemeSource) {
				HierarchicalThemeSource hts = (HierarchicalThemeSource) themeSource;
				if (hts.getParentThemeSource() == null) {
					// Only set parent context as parent ThemeSource if no parent ThemeSource
					// registered already.
					hts.setParentThemeSource((ThemeSource) context.getParent());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Using ThemeSource [" + themeSource + "]");
			}
			return themeSource;
		}
		else {
			// Use default ThemeSource to be able to accept getTheme calls, either
			// delegating to parent context's default or to local ResourceBundleThemeSource.
			HierarchicalThemeSource themeSource = null;
			if (context.getParent() instanceof ThemeSource) {
				themeSource = new DelegatingThemeSource();
				themeSource.setParentThemeSource((ThemeSource) context.getParent());
			}
			else {
				themeSource = new ResourceBundleThemeSource();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate ThemeSource with name '" + THEME_SOURCE_BEAN_NAME +
						"': using default [" + themeSource + "]");
			}
			return themeSource;
		}
	}

}
