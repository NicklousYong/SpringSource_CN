/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling exceptions in specific handler classes and/or
 * handler methods. Provides consistent style between Servlet and Portlet
 * environments, with the semantics adapting to the concrete environment.
 *
 * <p>Handler methods which are annotated with this annotation are allowed to
 * have very flexible signatures. They may have parameters of the following
 * types, in arbitrary order:
 * <ul>
 * <li>An exception argument: declared as a general Exception or as a more
 * specific exception. This also serves as a mapping hint if the annotation
 * itself does not narrow the exception types through its {@link #value()}.
 * <li>Request and/or response objects (Servlet API or Portlet API).
 * You may choose any specific request/response type, e.g.
 * {@link javax.servlet.ServletRequest} / {@link javax.servlet.http.HttpServletRequest}
 * or {@link javax.portlet.PortletRequest} / {@link javax.portlet.ActionRequest} /
 * {@link javax.portlet.RenderRequest}. Note that in the Portlet case,
 * an explicitly declared action/render argument is also used for mapping
 * specific request types onto a handler method (in case of no other
 * information given that differentiates between action and render requests).
 * <li>Session object (Servlet API or Portlet API): either
 * {@link javax.servlet.http.HttpSession} or {@link javax.portlet.PortletSession}.
 * An argument of this type will enforce the presence of a corresponding session.
 * As a consequence, such an argument will never be {@code null}.
 * <i>Note that session access may not be thread-safe, in particular in a
 * Servlet environment: Consider switching the
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#setSynchronizeOnSession
 * "synchronizeOnSession"} flag to "true" if multiple requests are allowed to
 * access a session concurrently.</i>
 * <li>{@link org.springframework.web.context.request.WebRequest} or
 * {@link org.springframework.web.context.request.NativeWebRequest}.
 * Allows for generic request parameter access as well as request/session
 * attribute access, without ties to the native Servlet/Portlet API.
 * <li>{@link java.util.Locale} for the current request locale
 * (determined by the most specific locale resolver available,
 * i.e. the configured {@link org.springframework.web.servlet.LocaleResolver}
 * in a Servlet environment and the portal locale in a Portlet environment).
 * <li>{@link java.io.InputStream} / {@link java.io.Reader} for access
 * to the request's content. This will be the raw InputStream/Reader as
 * exposed by the Servlet/Portlet API.
 * <li>{@link java.io.OutputStream} / {@link java.io.Writer} for generating
 * the response's content. This will be the raw OutputStream/Writer as
 * exposed by the Servlet/Portlet API.
 * <li>{@link org.springframework.ui.Model} as an alternative to returning
 * a model map from the handler method. Note that the provided model is not
 * pre-populated with regular model attributes and therefore always empty,
 * as a convenience for preparing the model for an exception-specific view.
 * </ul>
 *
 * <p>The following return types are supported for handler methods:
 * <ul>
 * <li>A {@code ModelAndView} object (Servlet MVC or Portlet MVC).
 * <li>A {@link org.springframework.ui.Model} object, with the view name implicitly
 * determined through a {@link org.springframework.web.servlet.RequestToViewNameTranslator}.
 * <li>A {@link java.util.Map} object for exposing a model,
 * with the view name implicitly determined through a
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator}.
 * <li>A {@link org.springframework.web.servlet.View} object.
 * <li>A {@link String} value which is interpreted as view name.
 * <li>{@link ResponseBody @ResponseBody} annotated methods (Servlet-only)
 * to set the response content. The return value will be converted to the
 * response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.
 * <li>An {@link org.springframework.http.HttpEntity HttpEntity&lt;?&gt;} or
 * {@link org.springframework.http.ResponseEntity ResponseEntity&lt;?&gt;} object
 * (Servlet-only) to set response headers and content. The ResponseEntity body
 * will be converted and written to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}.
 * <li>{@code void} if the method handles the response itself (by
 * writing the response content directly, declaring an argument of type
 * {@link javax.servlet.ServletResponse} / {@link javax.servlet.http.HttpServletResponse}
 * / {@link javax.portlet.RenderResponse} for that purpose)
 * or if the view name is supposed to be implicitly determined through a
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator}
 * (not declaring a response argument in the handler method signature;
 * only applicable in a Servlet environment).
 * </ul>
 *
 * <p>In Servlet environments, you can combine the {@code ExceptionHandler} annotation
 * with {@link ResponseStatus @ResponseStatus}, to define the response status
 * for the HTTP response.
 *
 * <p><b>Note:</b> In Portlet environments, {@code ExceptionHandler} annotated methods
 * will only be called during the render and resource phases - just like
 * {@link org.springframework.web.portlet.HandlerExceptionResolver} beans would.
 * Exceptions carried over from the action and event phases will be invoked during
 * the render phase as well, with exception handler methods having to be present
 * on the controller class that defines the applicable <i>render</i> method.
 *
 * <p>
 *  在特定处理程序类和/或处理程序方法中处理异常的注释在Servlet和Portlet环境之间提供一致的样式,语义适应具体环境
 * 
 * 使用此注释注释的处理程序方法允许具有非常灵活的签名他们可以按任意顺序具有以下类型的参数：
 * <ul>
 * 一个异常参数：被声明为一般异常或更具体的异常如果注释本身没有通过其{@link #value()} <li>请求来缩小异常类型,那么也可以用作映射提示, /或响应对象(Servlet API或Portl
 * et API)您可以选择任何特定的请求/响应类型,例如{@link javaxservletServletRequest} / {@link javaxservlethttpHttpServletRequest}
 * 或{@link javaxportletPortletRequest} / {@link javaxportletActionRequest} / {@link javaxportletRenderRequest }
 * 请注意,在Portlet的情况下,明确声明的action / render参数也用于将特定的请求类型映射到处理程序方法(在没有给出区分动作和呈现请求之间的其他信息的情况下)<li>会话对象(Servle
 * t API或Portlet API)：{@link javaxservlethttpHttpSession}或{@link javaxportletPortletSession}此类型的参数将强制存在相
 * 应的会话因此,此类参数将永远不会是{@代码null} <i>请注意,会话访问可能不是线程安全的,特别是在Servlet环境中：如果允许多个请求访问,请将{@link orgspringframeworkwebservletmvcmethodannotationRequestMappingHandlerAdapter#setSynchronizeOnSession"synchronizeOnSession"}
 * 标志切换为"true"会话同时</i> <li> {@ link orgspringframeworkwebcontextrequestWebRequest}或{@link orgspringframeworkwebcontextrequestNativeWebRequest}
 * 允许通用请求参数访问以及请求/会话属性访问,而不涉及当前请求区域设置的本地Servlet / Portlet API <li> {@ link javautilLocale}(由最具体的区域设置解析器确
 * 定,即已配置Servlet环境中的{@link orgspringframeworkwebservletLocaleResolver}和Portlet环境中的门户区域设置)<li> {@ link javaioInputStream}
 *  / {@link javaioReader}访问请求的内容这将是原始的InputStream / Reader,由Servlet / Portlet API <li> {@ link javaioOutputStream}
 *  / {@link javaioWriter}用于生成响应的内容这将是由Servlet / Portlet API公开的原始OutputStream / Writer <li> {@ link orgspringframeworkuiModel}
 * 作为从处理程序方法返回模型映射的替代方法请注意,提供的模型未预先填充常规模型属性,因此始终为空,以便为异常特定视图准备模型。
 * </ul>
 * 
 * <p>处理程序方法支持以下返回类型：
 * <ul>
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.web.context.request.WebRequest
 * @see org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {

	/**
	 * Exceptions handled by the annotated method. If empty, will default to any
	 * exceptions listed in the method argument list.
	 * <p>
	 * <li>一个{@code ModelAndView}对象(Servlet MVC或Portlet MVC)<li>一个{@link orgspringframeworkuiModel}对象,其视图名称通
	 * 过{@link orgspringframeworkwebservletRequestToViewNameTranslator}隐式确定<li> A {@link javautilMap}通过{@link orgspringframeworkwebservletRequestToViewNameTranslator}
	 * 隐式确定视图名称的对象,用于展示模型的对象<li>一个{@link orgspringframeworkwebservletView}对象<li>解释为视图名称的{@link String}值<li> 
	 * {@链接ResponseBody @ResponseBody}注释方法(仅限Servlet)设置响应内容返回值将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
	 * 转换为响应流<li> {@link orgspringframeworkhttpHttpEntity HttpEntity&lt;?&gt;}或{@link orgspringframeworkhttpResponseEntity ResponseEntity&lt;?&gt;}
	 * 对象(仅Servlet)设置响应头和内容使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}将ResponseE
	 * ntity体转换并写入响应流,如果方法处理响应本身(通过直接编写响应内容,为此目的声明类型为{@link javaxservletServletResponse} / {@link javaxservlethttpHttpServletResponse}
	 *  / {@link javaxportletRenderResponse})的参数,或者如果视图名称应该通过{@link orgspringframeworkwebservlet隐式确定)RequestToViewNameTranslator}
	 * (不在处理程序方法签名中声明响应参数;仅适用于Servlet环境)。
	 * </ul>
	 */
	Class<? extends Throwable>[] value() default {};

}
