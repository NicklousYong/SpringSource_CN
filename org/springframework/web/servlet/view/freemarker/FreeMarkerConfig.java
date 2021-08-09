/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.web.servlet.view.freemarker;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;

/**
 * Interface to be implemented by objects that configure and manage a
 * FreeMarker Configuration object in a web environment. Detected and
 * used by {@link FreeMarkerView}.
 *
 * <p>
 *  通过配置和管理Web环境中的FreeMarker Configuration对象的对象实现的接口由{@link FreeMarkerView}检测并使用
 * 
 * 
 * @author Darren Davison
 * @author Rob Harrop
 * @since 03.03.2004
 * @see FreeMarkerConfigurer
 * @see FreeMarkerView
 */
public interface FreeMarkerConfig {

	/**
	 * Return the FreeMarker Configuration object for the current
	 * web application context.
	 * <p>A FreeMarker Configuration object may be used to set FreeMarker
	 * properties and shared objects, and allows to retrieve templates.
	 * <p>
	 * 返回当前Web应用程序上下文的FreeMarker配置对象<p> FreeMarker配置对象可用于设置FreeMarker属性和共享对象,并允许检索模板
	 * 
	 * 
	 * @return the FreeMarker Configuration
	 */
	Configuration getConfiguration();

	/**
	 * Returns the {@link TaglibFactory} used to enable JSP tags to be
	 * accessed from FreeMarker templates.
	 * <p>
	 *  返回用于启用从FreeMarker模板访问JSP标签的{@link TaglibFactory}
	 */
	TaglibFactory getTaglibFactory();

}
