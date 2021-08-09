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

package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.request.render.Renderer;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Convenience subclass of {@link UrlBasedViewResolver} that supports
 * {@link TilesView} (i.e. Tiles definitions) and custom subclasses of it.
 *
 * <p>
 *  支持{@link TilesView}(即Tiles定义)和自定义子类的{@link UrlBasedViewResolver}的便利子类
 * 
 * 
 * @author Nicolas Le Bas
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.2
 */
public class TilesViewResolver extends UrlBasedViewResolver {

	private Renderer renderer;

	private Boolean alwaysInclude;


	public TilesViewResolver() {
		setViewClass(requiredViewClass());
	}


	/**
	 * This resolver requires {@link TilesView}.
	 * <p>
	 *  此解析器需要{@link TilesView}
	 * 
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return TilesView.class;
	}

	/**
	 * Set the {@link Renderer} to use. If not specified, a default
	 * {@link org.apache.tiles.renderer.DefinitionRenderer} will be used.
	 * <p>
	 * 将{@link Renderer}设置为使用如果未指定,将使用默认的{@link orgapachetilesrendererDefinitionRenderer}
	 * 
	 * 
	 * @see TilesView#setRenderer(Renderer)
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Specify whether to always include the view rather than forward to it.
	 * <p>Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * <p>
	 *  指定是否始终包含视图而不是转发到视图<p>默认值为"false"将此标志打开以强制使用Servlet包含,即使可以进行转发
	 * 
	 * @since 4.1.2
	 * @see TilesView#setAlwaysInclude
	 */
	public void setAlwaysInclude(Boolean alwaysInclude) {
		this.alwaysInclude = alwaysInclude;
	}


	@Override
	protected TilesView buildView(String viewName) throws Exception {
		TilesView view = (TilesView) super.buildView(viewName);
		if (this.renderer != null) {
			view.setRenderer(this.renderer);
		}
		if (this.alwaysInclude != null) {
			view.setAlwaysInclude(this.alwaysInclude);
		}
		return view;
	}

}
