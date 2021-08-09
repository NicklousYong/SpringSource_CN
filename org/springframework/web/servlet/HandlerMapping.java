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

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to be implemented by objects that define a mapping between
 * requests and handler objects.
 *
 * <p>This class can be implemented by application developers, although this is not
 * necessary, as {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping}
 * and {@link org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping}
 * are included in the framework. The former is the default if no
 * HandlerMapping bean is registered in the application context.
 *
 * <p>HandlerMapping implementations can support mapped interceptors but do not
 * have to. A handler will always be wrapped in a {@link HandlerExecutionChain}
 * instance, optionally accompanied by some {@link HandlerInterceptor} instances.
 * The DispatcherServlet will first call each HandlerInterceptor's
 * {@code preHandle} method in the given order, finally invoking the handler
 * itself if all {@code preHandle} methods have returned {@code true}.
 *
 * <p>The ability to parameterize this mapping is a powerful and unusual
 * capability of this MVC framework. For example, it is possible to write
 * a custom mapping based on session state, cookie state or many other
 * variables. No other MVC framework seems to be equally flexible.
 *
 * <p>Note: Implementations can implement the {@link org.springframework.core.Ordered}
 * interface to be able to specify a sorting order and thus a priority for getting
 * applied by DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * <p>
 *  由定义请求和处理程序对象之间映射的对象实现的接口
 * 
 * <p>这个类可以由应用程序开发人员实现,虽然这不是必需的,因为{@link orgspringframeworkwebservlethandlerBeanNameUrlHandlerMapping}和{@link orgspringframeworkwebservletmvcannotationDefaultAnnotationHandlerMapping}
 * 包含在框架中前者是默认的,如果没有HandlerMapping bean在应用程序上下文中注册。
 * 
 * HandlerMapping实现可以支持映射的拦截器,但不需要A处理程序将始终包含在{@link HandlerExecutionChain}实例中,可选地伴随着一些{@link HandlerInterceptor}
 * 实例DispatcherServlet将首先调用每个HandlerInterceptor的{@code preHandle }方法,如果所有{@code preHandle}方法返回{@code true}
 * ,最后调用处理程序本身。
 * 
 *  参数化此映射的功能是此MVC框架的强大而不寻常的功能例如,可以根据会话状态,Cookie状态或许多其他变量编写自定义映射没有其他MVC框架似乎同样灵活
 * 
 * <p>注意：实现可以实现{@link orgspringframeworkcoreOrdered}接口,以便能够指定排序顺序,因此DispatcherServlet Non-Ordered实例应用的优先
 * 级被视为最低优先级。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.core.Ordered
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping
 * @see org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping
 */
public interface HandlerMapping {

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the path
	 * within the handler mapping, in case of a pattern match, or the full
	 * relevant URI (typically within the DispatcherServlet's mapping) else.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 * <p>
	 *  包含处理程序映射中路径的{@link HttpServletRequest}属性的名称,在模式匹配的情况下,或完整的相关URI(通常在DispatcherServlet的映射中)否则<p>注意：此属性
	 * 不是必需的所有HandlerMapping实现支持基于URL的HandlerMappings通常将支持它,但处理程序不一定希望此请求属性存在于所有场景中。
	 * 
	 */
	String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the
	 * best matching pattern within the handler mapping.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 * <p>
	 * 包含处理程序映射中最佳匹配模式的{@link HttpServletRequest}属性的名称<p>注意：所有HandlerMapping实现不需要此属性。
	 * 基于URL的HandlerMappings通常将支持它,但处理程序不一定期望此请求属性存在于所有场景中。
	 * 
	 */
	String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";

	/**
	 * Name of the boolean {@link HttpServletRequest} attribute that indicates
	 * whether type-level mappings should be inspected.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations.
	 * <p>
	 *  指示是否应检查类型级别映射的布尔{@link HttpServletRequest}属性的名称<p>注意：所有HandlerMapping实现不需要该属性
	 * 
	 */
	String INTROSPECT_TYPE_LEVEL_MAPPING = HandlerMapping.class.getName() + ".introspectTypeLevelMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the URI
	 * templates map, mapping variable names to values.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 * <p>
	 * 包含URI模板映射的{@link HttpServletRequest}属性的名称,将变量名称映射到值<p>注意：所有HandlerMapping实现不需要此属性。
	 * 基于URL的HandlerMappings通常将支持它,但处理程序应不一定期望这个请求属性存在于所有场景中。
	 * 
	 */
	String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".uriTemplateVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains a map with
	 * URI matrix variables.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations and may also not be present depending on
	 * whether the HandlerMapping is configured to keep matrix variable content
	 * in the request URI.
	 * <p>
	 *  包含具有URI矩阵变量的映射的{@link HttpServletRequest}属性的名称<p>注意：此属性不需要由所有HandlerMapping实现支持,也可能不存在,具体取决于HandlerM
	 * apping是否配置为保留矩阵请求URI中的变量内容。
	 * 
	 */
	String MATRIX_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".matrixVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the set of
	 * producible MediaTypes applicable to the mapped handler.
	 * <p>Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. Handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 * <p>
	 * 包含适用于映射处理程序的可生产MediaTypes集合的{@link HttpServletRequest}属性的名称注意：所有HandlerMapping实现不需要该属性。
	 * 处理程序不一定要求此请求属性存在于所有场景。
	 * 
	 */
	String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";

	/**
	 * Return a handler and any interceptors for this request. The choice may be made
	 * on request URL, session state, or any factor the implementing class chooses.
	 * <p>The returned HandlerExecutionChain contains a handler Object, rather than
	 * even a tag interface, so that handlers are not constrained in any way.
	 * For example, a HandlerAdapter could be written to allow another framework's
	 * handler objects to be used.
	 * <p>Returns {@code null} if no match was found. This is not an error.
	 * The DispatcherServlet will query all registered HandlerMapping beans to find
	 * a match, and only decide there is an error if none can find a handler.
	 * <p>
	 * 为此请求返回一个处理程序和任何拦截器可以根据请求URL,会话状态或实现类选择的任何因素进行选择<p>返回的HandlerExecutionChain包含一个处理程序Object,而不是甚至一个标记接口,
	 * 以便处理程序不以任何方式约束例如,可以编写一个HandlerAdapter以允许使用另一个框架的处理程序对象<p>如果未找到匹配,则返回{@code null}这不是错误DispatcherServle
	 * 
	 * @param request current HTTP request
	 * @return a HandlerExecutionChain instance containing handler object and
	 * any interceptors, or {@code null} if no mapping found
	 * @throws Exception if there is an internal error
	 */
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}
