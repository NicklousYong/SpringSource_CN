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

package org.springframework.web.servlet.view;

/**
 * Abstract base class for template view resolvers,
 * in particular for Velocity and FreeMarker views.
 *
 * <p>Provides a convenient way to specify {@link AbstractTemplateView}'s
 * exposure flags for request attributes, session attributes,
 * and Spring's macro helpers.
 *
 * <p>
 *  用于模板视图解析器的抽象基类,特别适用于Velocity和FreeMarker视图
 * 
 * <p>提供了一种方便的方法来指定{@link AbstractTemplateView}的请求属性,会话属性和Spring的宏助手的曝光标志
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see AbstractTemplateView
 * @see org.springframework.web.servlet.view.velocity.VelocityViewResolver
 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver
 */
public class AbstractTemplateViewResolver extends UrlBasedViewResolver {

	private boolean exposeRequestAttributes = false;

	private boolean allowRequestOverride = false;

	private boolean exposeSessionAttributes = false;

	private boolean allowSessionOverride = false;

	private boolean exposeSpringMacroHelpers = true;


	@Override
	protected Class<?> requiredViewClass() {
		return AbstractTemplateView.class;
	}

	/**
	 * Set whether all request attributes should be added to the
	 * model prior to merging with the template. Default is "false".
	 * <p>
	 *  设置在与模板合并之前是否应将所有请求属性添加到模型中默认为"false"
	 * 
	 * 
	 * @see AbstractTemplateView#setExposeRequestAttributes
	 */
	public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
		this.exposeRequestAttributes = exposeRequestAttributes;
	}

	/**
	 * Set whether HttpServletRequest attributes are allowed to override (hide)
	 * controller generated model attributes of the same name. Default is "false",
	 * which causes an exception to be thrown if request attributes of the same
	 * name as model attributes are found.
	 * <p>
	 *  设置HttpServletRequest属性是否允许覆盖(隐藏)控制器生成的相同名称的模型属性默认值为"false",如果发现与模型属性名称相同的请求属性,则引发异常
	 * 
	 * 
	 * @see AbstractTemplateView#setAllowRequestOverride
	 */
	public void setAllowRequestOverride(boolean allowRequestOverride) {
		this.allowRequestOverride = allowRequestOverride;
	}

	/**
	 * Set whether all HttpSession attributes should be added to the
	 * model prior to merging with the template. Default is "false".
	 * <p>
	 *  设置在与模板合并之前是否应将所有HttpSession属性添加到模型中Default是"false"
	 * 
	 * 
	 * @see AbstractTemplateView#setExposeSessionAttributes
	 */
	public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
		this.exposeSessionAttributes = exposeSessionAttributes;
	}

	/**
	 * Set whether HttpSession attributes are allowed to override (hide)
	 * controller generated model attributes of the same name. Default is "false",
	 * which causes an exception to be thrown if session attributes of the same
	 * name as model attributes are found.
	 * <p>
	 * 设置是否允许HttpSession属性重写(隐藏)控制器生成的相同名称的模型属性默认值为"false",如果找到与模型属性相同名称的会话属性,则引发异常。
	 * 
	 * 
	 * @see AbstractTemplateView#setAllowSessionOverride
	 */
	public void setAllowSessionOverride(boolean allowSessionOverride) {
		this.allowSessionOverride = allowSessionOverride;
	}

	/**
	 * Set whether to expose a RequestContext for use by Spring's macro library,
	 * under the name "springMacroRequestContext". Default is "true".
	 * <p>
	 *  设置是否公开一个RequestContext供Spring Spring宏使用,名称为"springMacroRequestContext",默认为"true"
	 * 
	 * @see AbstractTemplateView#setExposeSpringMacroHelpers
	 */
	public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
		this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
	}


	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		AbstractTemplateView view = (AbstractTemplateView) super.buildView(viewName);
		view.setExposeRequestAttributes(this.exposeRequestAttributes);
		view.setAllowRequestOverride(this.allowRequestOverride);
		view.setExposeSessionAttributes(this.exposeSessionAttributes);
		view.setAllowSessionOverride(this.allowSessionOverride);
		view.setExposeSpringMacroHelpers(this.exposeSpringMacroHelpers);
		return view;
	}

}
