/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io;

/**
 * Extended interface for a resource that is loaded from an enclosing
 * 'context', e.g. from a {@link javax.servlet.ServletContext} or a
 * {@link javax.portlet.PortletContext} but also from plain classpath paths
 * or relative file system paths (specified without an explicit prefix,
 * hence applying relative to the local {@link ResourceLoader}'s context).
 *
 * <p>
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 * @see org.springframework.web.portlet.context.PortletContextResource
 */
public interface ContextResource extends Resource {

	/**
	 * Return the path within the enclosing 'context'.
	 * <p>This is typically path relative to a context-specific root directory,
	 * e.g. a ServletContext root or a PortletContext root.
	 * <p>
	 * 从封闭的"上下文"加载的资源的扩展接口,例如来自{@link javaxservletServletContext}或{@link javaxportletPortletContext},也可以是纯类路
	 * 径路径或相对文件系统路径(没有明确的前缀指定),因此应用相对于本地{@link ResourceLoader}的上下文)。
	 * 
	 */
	String getPathWithinContext();

}
