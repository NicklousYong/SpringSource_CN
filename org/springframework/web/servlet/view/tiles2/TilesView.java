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

package org.springframework.web.servlet.view.tiles2;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletTilesApplicationContext;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;

import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * {@link org.springframework.web.servlet.View} implementation that retrieves a
 * Tiles definition. The "url" property is interpreted as name of a Tiles definition.
 *
 * <p>This class builds on Tiles, which requires JSP 2.0.
 * JSTL support is integrated out of the box due to JSTL's inclusion in JSP 2.0.
 * <b>Note: Spring 4.0 requires Tiles 2.2.2.</b>
 *
 * <p>Depends on a TilesContainer which must be available in
 * the ServletContext. This container is typically set up via a
 * {@link TilesConfigurer} bean definition in the application context.
 *
 * <p><b>NOTE: Tiles 2 support is deprecated in favor of Tiles 3 and will be removed
 * as of Spring Framework 5.0.</b>.
 *
 * <p>
 *  检索Tiles定义的{@link orgspringframeworkwebservletView}实现"url"属性被解释为Tiles定义的名称
 * 
 * <p>这个类基于Tiles,这需要JSP 20 JSTL支持集成在一起,因为JSTL被包含在JSP 20中。注意：Spring 40需要Tiles 222 </b>
 * 
 *  <p>取决于ServletContext中必须可用的TilesContainer此容器通常通过应用程序上下文中的{@link TilesConfigurer} bean定义设置
 * 
 * 
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 2.5
 * @see #setUrl
 * @see TilesConfigurer
 * @deprecated as of Spring 4.2, in favor of Tiles 3
 */
@Deprecated
public class TilesView extends AbstractUrlBasedView {

	private boolean alwaysInclude = false;


	/**
	 * Specify whether to always include the view rather than forward to it.
	 * <p>Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * <p>
	 *  <p> <b>注意：Tiles 2支持已被弃用,有利于Tiles 3,并将从Spring Framework 50中删除</b>
	 * 
	 * 
	 * @since 4.1.2
	 * @see TilesViewResolver#setAlwaysInclude
	 */
	public void setAlwaysInclude(boolean alwaysInclude) {
		this.alwaysInclude = alwaysInclude;
	}


	@Override
	public boolean checkResource(final Locale locale) throws Exception {
		TilesContainer container = ServletUtil.getContainer(getServletContext());
		if (!(container instanceof BasicTilesContainer)) {
			// Cannot check properly - let's assume it's there.
			return true;
		}
		BasicTilesContainer basicContainer = (BasicTilesContainer) container;
		TilesApplicationContext appContext = new ServletTilesApplicationContext(getServletContext());
		TilesRequestContext requestContext = new ServletTilesRequestContext(appContext, null, null) {
			@Override
			public Locale getRequestLocale() {
				return locale;
			}
		};
		return (basicContainer.getDefinitionsFactory().getDefinition(getUrl(), requestContext) != null);
	}

	@Override
	protected void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		ServletContext servletContext = getServletContext();
		TilesContainer container = ServletUtil.getContainer(servletContext);
		if (container == null) {
			throw new ServletException("Tiles container is not initialized. " +
					"Have you added a TilesConfigurer to your web application context?");
		}

		exposeModelAsRequestAttributes(model, request);
		JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));
		if (this.alwaysInclude) {
			ServletUtil.setForceInclude(request, true);
		}
		container.render(getUrl(), request, response);
	}

}
