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

package org.springframework.ui.context;

import org.springframework.context.MessageSource;

/**
 * A Theme can resolve theme-specific messages, codes, file paths, etcetera
 * (e&#46;g&#46; CSS and image files in a web environment).
 * The exposed {@link org.springframework.context.MessageSource} supports
 * theme-specific parameterization and internationalization.
 *
 * <p>
 * 主题可以解决主题特定的消息,代码,文件路径等(例如Web环境中的CSS和图像文件)。
 * 已展示的{@link orgspringframeworkcontextMessageSource}支持主题特定的参数化和国际化。
 * 
 * 
 * @author Juergen Hoeller
 * @since 17.06.2003
 * @see ThemeSource
 * @see org.springframework.web.servlet.ThemeResolver
 */
public interface Theme {

	/**
	 * Return the name of the theme.
	 * <p>
	 *  返回主题的名称
	 * 
	 * 
	 * @return the name of the theme (never {@code null})
	 */
	String getName();

	/**
	 * Return the specific MessageSource that resolves messages
	 * with respect to this theme.
	 * <p>
	 *  返回解决与此主题相关的消息的特定MessageSource
	 * 
	 * @return the theme-specific MessageSource (never {@code null})
	 */
	MessageSource getMessageSource();

}
