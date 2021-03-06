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

package org.springframework.web.servlet.view.tiles2;

import org.apache.tiles.TilesException;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.PreparerFactory;
import org.apache.tiles.preparer.ViewPreparer;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Abstract implementation of the Tiles {@link org.apache.tiles.preparer.PreparerFactory}
 * interface, obtaining the current Spring WebApplicationContext and delegating to
 * {@link #getPreparer(String, org.springframework.web.context.WebApplicationContext)}.
 *
 * <p><b>NOTE: Tiles 2 support is deprecated in favor of Tiles 3 and will be removed
 * as of Spring Framework 5.0.</b>.
 *
 * <p>
 * 抽象实现Tiles {@link orgapachetilespreparerPreparerFactory}接口,获取当前的Spring WebApplicationContext并委托给{@link #getPreparer(String,orgspringframeworkwebcontextWebApplicationContext)}
 * 。
 * 
 *  <p> <b>注意：Tiles 2支持已被弃用,有利于Tiles 3,并将从Spring Framework 50中删除</b>
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see #getPreparer(String, org.springframework.web.context.WebApplicationContext)
 * @see SimpleSpringPreparerFactory
 * @see SpringBeanPreparerFactory
 * @deprecated as of Spring 4.2, in favor of Tiles 3
 */
@Deprecated
public abstract class AbstractSpringPreparerFactory implements PreparerFactory {

	@Override
	public ViewPreparer getPreparer(String name, TilesRequestContext context) throws TilesException {
		WebApplicationContext webApplicationContext = (WebApplicationContext) context.getRequestScope().get(
				DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (webApplicationContext == null) {
			webApplicationContext = (WebApplicationContext) context.getApplicationContext().getApplicationScope().get(
					WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			if (webApplicationContext == null) {
				throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
			}
		}
		return getPreparer(name, webApplicationContext);
	}

	/**
	 * Obtain a preparer instance for the given preparer name,
	 * based on the given Spring WebApplicationContext.
	 * <p>
	 * 
	 * 
	 * @param name the name of the preparer
	 * @param context the current Spring WebApplicationContext
	 * @return the preparer instance
	 * @throws TilesException in case of failure
	 */
	protected abstract ViewPreparer getPreparer(String name, WebApplicationContext context) throws TilesException;

}
