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

package org.springframework.web.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

/**
 * MVC View for a web interaction. Implementations are responsible for rendering
 * content, and exposing the model. A single view exposes multiple model attributes.
 *
 * <p>This class and the MVC approach associated with it is discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 *
 * <p>View implementations may differ widely. An obvious implementation would be
 * JSP-based. Other implementations might be XSLT-based, or use an HTML generation library.
 * This interface is designed to avoid restricting the range of possible implementations.
 *
 * <p>Views should be beans. They are likely to be instantiated as beans by a ViewResolver.
 * As this interface is stateless, view implementations should be thread-safe.
 *
 * <p>
 *  用于Web交互的MVC视图实现方式负责呈现内容,并显示模型单个视图显示多个模型属性
 * 
 * <p>该类和与之相关的MVC方法在<a href=\"http://wwwamazoncom/exec/obidos/tg/detail/-/0764543857/\">专家一对一J2EE的第12章中讨
 * 论设计与开发</a> Rod Johnson(Wrox,2002)。
 * 
 *  <p>查看实现可能会有所不同明显的实现将是基于JSP的其他实现可能是基于XSLT的,或者使用HTML生成库此接口旨在避免限制可能实现的范围
 * 
 *  视图应该是bean他们可能被ViewResolver实例化为bean这个接口是无状态的,视图实现应该是线程安全的
 * 
 * 
 * @author Rod Johnson
 * @author Arjen Poutsma
 * @see org.springframework.web.servlet.view.AbstractView
 * @see org.springframework.web.servlet.view.InternalResourceView
 */
public interface View {

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the response status code.
	 * <p>Note: This attribute is not required to be supported by all View implementations.
	 * <p>
	 * 包含响应状态代码的{@link HttpServletRequest}属性的名称<p>注意：此属性不需要所有View实现支持
	 * 
	 */
	String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains a Map with path variables.
	 * The map consists of String-based URI template variable names as keys and their corresponding
	 * Object-based values -- extracted from segments of the URL and type converted.
	 *
	 * <p>Note: This attribute is not required to be supported by all View implementations.
	 * <p>
	 *  包含具有路径变量的Map的{@link HttpServletRequest}属性的名称该映射包含基于String的URI模板变量名称作为键及其相应的基于对象的值 - 从URL的段和类型转换中提取
	 * 
	 *  注意：所有View实现都不需要该属性
	 * 
	 */
	String PATH_VARIABLES = View.class.getName() + ".pathVariables";

	/**
	 * The {@link MediaType} selected during content negotiation, which may be
	 * more specific than the one the View is configured with. For example:
	 * "application/vnd.example-v1+xml" vs "application/*+xml".
	 * <p>
	 *  在内容协商期间选择的{@link MediaType},可能比视图配置的更具体。
	 * 例如："application / vndexample-v1 + xml"vs"application / * + xml"。
	 * 
	 */
	String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";

	/**
	 * Return the content type of the view, if predetermined.
	 * <p>Can be used to check the content type upfront,
	 * before the actual rendering process.
	 * <p>
	 * 返回视图的内容类型,如果预定<p>可以在实际渲染过程之前先检查内容类型
	 * 
	 * 
	 * @return the content type String (optionally including a character set),
	 * or {@code null} if not predetermined.
	 */
	String getContentType();

	/**
	 * Render the view given the specified model.
	 * <p>The first step will be preparing the request: In the JSP case,
	 * this would mean setting model objects as request attributes.
	 * The second step will be the actual rendering of the view,
	 * for example including the JSP via a RequestDispatcher.
	 * <p>
	 *  给出指定模型的视图<p>第一步将准备请求：在JSP案例中,这意味着将模型对象设置为请求属性第二步将是视图的实际呈现,例如包括JSP通过一个RequestDispatcher
	 * 
	 * @param model Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 * @param request current HTTP request
	 * @param response HTTP response we are building
	 * @throws Exception if rendering failed
	 */
	void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
