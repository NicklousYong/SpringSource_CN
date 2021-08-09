/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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
import java.util.concurrent.Callable;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation for mapping web requests onto specific handler classes and/or
 * handler methods. Provides a consistent style between Servlet and Portlet
 * environments, with the semantics adapting to the concrete environment.
 *
 * <p><b>NOTE:</b> The set of features supported for Servlets is a superset
 * of the set of features supported for Portlets. The places where this applies
 * are marked with the label "Servlet-only" in this source file. For Servlet
 * environments there are some further distinctions depending on whether an
 * application is configured with {@literal "@MVC 3.0"} or
 * {@literal "@MVC 3.1"} support classes. The places where this applies are
 * marked with {@literal "@MVC 3.1-only"} in this source file. For more
 * details see the note on the new support classes added in Spring MVC 3.1
 * further below.
 *
 * <p>Handler methods which are annotated with this annotation are allowed to
 * have very flexible signatures. They may have parameters of the following
 * types, in arbitrary order (except for validation results, which need to
 * follow right after the corresponding command object, if desired):
 * <ul>
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
 * <li>{@link org.springframework.http.HttpMethod} for the HTTP request method</li>
 * <li>{@link PathVariable @PathVariable} annotated parameters (Servlet-only)
 * for access to URI template values (i.e. /hotels/{hotel}). Variable values will be
 * converted to the declared method argument type. By default, the URI template
 * will match against the regular expression {@code [^\.]*} (i.e. any character
 * other than period), but this can be changed by specifying another regular
 * expression, like so: /hotels/{hotel:\d+}.
 * Additionally, {@code @PathVariable} can be used on a
 * {@link java.util.Map Map&lt;String, String&gt;} to gain access to all
 * URI template variables.
 * <li>{@link MatrixVariable @MatrixVariable} annotated parameters (Servlet-only)
 * for access to name-value pairs located in URI path segments. Matrix variables
 * must be represented with a URI template variable. For example /hotels/{hotel}
 * where the incoming URL may be "/hotels/42;q=1".
 * Additionally, {@code @MatrixVariable} can be used on a
 * {@link java.util.Map Map&lt;String, String&gt;} to gain access to all
 * matrix variables in the URL or to those in a specific path variable.
 * <li>{@link RequestParam @RequestParam} annotated parameters for access to
 * specific Servlet/Portlet request parameters. Parameter values will be
 * converted to the declared method argument type. Additionally,
 * {@code @RequestParam} can be used on a {@link java.util.Map Map&lt;String, String&gt;} or
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}
 * method parameter to gain access to all request parameters.
 * <li>{@link RequestHeader @RequestHeader} annotated parameters for access to
 * specific Servlet/Portlet request HTTP headers. Parameter values will be
 * converted to the declared method argument type. Additionally,
 * {@code @RequestHeader} can be used on a {@link java.util.Map Map&lt;String, String&gt;},
 * {@link org.springframework.util.MultiValueMap MultiValueMap&lt;String, String&gt;}, or
 * {@link org.springframework.http.HttpHeaders HttpHeaders} method parameter to
 * gain access to all request headers.
 * <li>{@link RequestBody @RequestBody} annotated parameters (Servlet-only)
 * for access to the Servlet request HTTP contents. The request stream will be
 * converted to the declared method argument type using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message
 * converters}. Such parameters may optionally be annotated with {@code @Valid}
 * and also support access to validation results through an
 * {@link org.springframework.validation.Errors} argument.
 * Instead a {@link org.springframework.web.bind.MethodArgumentNotValidException}
 * exception is raised.
 * <li>{@link RequestPart @RequestPart} annotated parameters
 * (Servlet-only, {@literal @MVC 3.1-only})
 * for access to the content
 * of a part of "multipart/form-data" request. The request part stream will be
 * converted to the declared method argument type using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message
 * converters}. Such parameters may optionally be annotated with {@code @Valid}
 * and support access to validation results through a
 * {@link org.springframework.validation.Errors} argument.
 * Instead a {@link org.springframework.web.bind.MethodArgumentNotValidException}
 * exception is raised.
 * <li>{@link SessionAttribute @SessionAttribute} annotated parameters for access
 * to existing, permanent session attributes (e.g. user authentication object)
 * as opposed to model attributes temporarily stored in the session as part of
 * a controller workflow via {@link SessionAttributes}.
 * <li>{@link RequestAttribute @RequestAttribute} annotated parameters for access
 * to request attributes.
 * <li>{@link org.springframework.http.HttpEntity HttpEntity&lt;?&gt;} parameters
 * (Servlet-only) for access to the Servlet request HTTP headers and contents.
 * The request stream will be converted to the entity body using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message
 * converters}.
 * <li>{@link java.util.Map} / {@link org.springframework.ui.Model} /
 * {@link org.springframework.ui.ModelMap} for enriching the implicit model
 * that will be exposed to the web view.
 * <li>{@link org.springframework.web.servlet.mvc.support.RedirectAttributes}
 * (Servlet-only, {@literal @MVC 3.1-only}) to specify the exact set of attributes
 * to use in case of a redirect and also to add flash attributes (attributes
 * stored temporarily on the server-side to make them available to the request
 * after the redirect). {@code RedirectAttributes} is used instead of the
 * implicit model if the method returns a "redirect:" prefixed view name or
 * {@code RedirectView}.
 * <li>Command/form objects to bind parameters to: as bean properties or fields,
 * with customizable type conversion, depending on {@link InitBinder} methods
 * and/or the HandlerAdapter configuration - see the "webBindingInitializer"
 * property on RequestMappingHandlerMethodAdapter.
 * Such command objects along with their validation results will be exposed
 * as model attributes, by default using the non-qualified command class name
 * in property notation (e.g. "orderAddress" for type "mypackage.OrderAddress").
 * Specify a parameter-level {@link ModelAttribute @ModelAttribute} annotation for
 * declaring a specific model attribute name.
 * <li>{@link org.springframework.validation.Errors} /
 * {@link org.springframework.validation.BindingResult} validation results
 * for a preceding command/form object (the immediate preceding argument).
 * <li>{@link org.springframework.web.bind.support.SessionStatus} status handle
 * for marking form processing as complete (triggering the cleanup of session
 * attributes that have been indicated by the {@link SessionAttributes @SessionAttributes}
 * annotation at the handler type level).
 * <li>{@link org.springframework.web.util.UriComponentsBuilder}
 * (Servlet-only, {@literal @MVC 3.1-only})
 * for preparing a URL relative to the current request's host, port, scheme,
 * context path, and the literal part of the servlet mapping.
 * </ul>
 *
 * <p><strong>Note:</strong> Java 8's {@code java.util.Optional} is supported
 * as a method parameter type with annotations that provide a {@code required}
 * attribute (e.g. {@code @RequestParam}, {@code @RequestHeader}, etc.). The use
 * of {@code java.util.Optional} in those cases is equivalent to having
 * {@code required=false}.
 *
 * <p>The following return types are supported for handler methods:
 * <ul>
 * <li>A {@code ModelAndView} object (Servlet MVC or Portlet MVC),
 * with the model implicitly enriched with command objects and the results
 * of {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * <li>A {@link org.springframework.ui.Model Model} object, with the view name implicitly
 * determined through a {@link org.springframework.web.servlet.RequestToViewNameTranslator}
 * and the model implicitly enriched with command objects and the results
 * of {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * <li>A {@link java.util.Map} object for exposing a model,
 * with the view name implicitly determined through a
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator}
 * and the model implicitly enriched with command objects and the results
 * of {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * <li>A {@link org.springframework.web.servlet.View} object, with the
 * model implicitly determined through command objects and
 * {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * The handler method may also programmatically enrich the model by
 * declaring a {@link org.springframework.ui.Model} argument (see above).
 * <li>A {@link String} value which is interpreted as view name,
 * with the model implicitly determined through command objects and
 * {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * The handler method may also programmatically enrich the model by
 * declaring a {@link org.springframework.ui.ModelMap} argument
 * (see above).
 * <li>{@link ResponseBody @ResponseBody} annotated methods (Servlet-only)
 * for access to the Servlet response HTTP contents. The return value will
 * be converted to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message
 * converters}.
 * <li>An {@link org.springframework.http.HttpEntity HttpEntity&lt;?&gt;} or
 * {@link org.springframework.http.ResponseEntity ResponseEntity&lt;?&gt;} object
 * (Servlet-only) to access to the Servlet response HTTP headers and contents.
 * The entity body will be converted to the response stream using
 * {@linkplain org.springframework.http.converter.HttpMessageConverter message
 * converters}.
 * <li>An {@link org.springframework.http.HttpHeaders HttpHeaders} object to
 * return a response with no body.</li>
 * <li>A {@link Callable} which is used by Spring MVC to obtain the return
 * value asynchronously in a separate thread transparently managed by Spring MVC
 * on behalf of the application.
 * <li>A {@link org.springframework.web.context.request.async.DeferredResult}
 * which the application uses to produce a return value in a separate
 * thread of its own choosing, as an alternative to returning a Callable.
 * <li>A {@link org.springframework.util.concurrent.ListenableFuture}
 * which the application uses to produce a return value in a separate
 * thread of its own choosing, as an alternative to returning a Callable.
 * <li>A {@link java.util.concurrent.CompletionStage} (implemented by
 * {@link java.util.concurrent.CompletableFuture} for example)
 * which the application uses to produce a return value in a separate
 * thread of its own choosing, as an alternative to returning a Callable.
 * <li>A {@link org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter}
 * can be used to write multiple objects to the response asynchronously;
 * also supported as the body within {@code ResponseEntity}.</li>
 * <li>An {@link org.springframework.web.servlet.mvc.method.annotation.SseEmitter}
 * can be used to write Server-Sent Events to the response asynchronously;
 * also supported as the body within {@code ResponseEntity}.</li>
 * <li>A {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody}
 * can be used to write to the response asynchronously;
 * also supported as the body within {@code ResponseEntity}.</li>
 * <li>{@code void} if the method handles the response itself (by
 * writing the response content directly, declaring an argument of type
 * {@link javax.servlet.ServletResponse} / {@link javax.servlet.http.HttpServletResponse}
 * / {@link javax.portlet.RenderResponse} for that purpose)
 * or if the view name is supposed to be implicitly determined through a
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator}
 * (not declaring a response argument in the handler method signature;
 * only applicable in a Servlet environment).
 * <li>Any other return type will be considered as single model attribute
 * to be exposed to the view, using the attribute name specified through
 * {@link ModelAttribute @ModelAttribute} at the method level (or the default attribute
 * name based on the return type's class name otherwise). The model will be
 * implicitly enriched with command objects and the results of
 * {@link ModelAttribute @ModelAttribute} annotated reference data accessor methods.
 * </ul>
 *
 * <p><b>NOTE:</b> {@code @RequestMapping} will only be processed if an
 * an appropriate {@code HandlerMapping}-{@code HandlerAdapter} pair
 * is configured. This is the case by default in both the
 * {@code DispatcherServlet} and the {@code DispatcherPortlet}.
 * However, if you are defining custom {@code HandlerMappings} or
 * {@code HandlerAdapters}, then you need to add
 * {@code DefaultAnnotationHandlerMapping} and
 * {@code AnnotationMethodHandlerAdapter} to your configuration.</code>.
 *
 * <p><b>NOTE:</b> Spring 3.1 introduced a new set of support classes for
 * {@code @RequestMapping} methods in Servlet environments called
 * {@code RequestMappingHandlerMapping} and
 * {@code RequestMappingHandlerAdapter}. They are recommended for use and
 * even required to take advantage of new features in Spring MVC 3.1 (search
 * {@literal "@MVC 3.1-only"} in this source file) and going forward.
 * The new support classes are enabled by default from the MVC namespace and
 * with use of the MVC Java config ({@code @EnableWebMvc}) but must be
 * configured explicitly if using neither.
 *
 * <p><b>NOTE:</b> When using controller interfaces (e.g. for AOP proxying),
 * make sure to consistently put <i>all</i> your mapping annotations - such as
 * {@code @RequestMapping} and {@code @SessionAttributes} - on
 * the controller <i>interface</i> rather than on the implementation class.
 *
 * <p>
 *  将Web请求映射到特定处理程序类和/或处理程序方法的注释在Servlet和Portlet环境之间提供一致的样式,其语义适应具体环境
 * 
 * <p> <b>注意：</b> Servlet支持的一组功能是Portlet支持的一组功能的超集本适用的地方在此源文件中标有"仅Servlet"标签用于Servlet环境有一些进一步的区别取决于应用程序是
 * 否配置了{@literal"@MVC 30"}或{@literal"@MVC 31"}支持类应用的地方标有{@literal"@MVC 31-only"}有关更多详细信息,请参阅下面进一步介绍在Spri
 * ng MVC 31中添加的新支持类。
 * 
 * 使用此注释注释的处理程序方法允许具有非常灵活的签名。他们可以按任意顺序具有以下类型的参数(验证结果除外,如果需要,需要在相应的命令对象之后) ：
 * <ul>
 * <li>请求和/或响应对象(Servlet API或Portlet API)您可以选择任何特定的请求/响应类型,例如{@link javaxservletServletRequest} / {@link javaxservlethttpHttpServletRequest}
 * 或{@link javaxportletPortletRequest} / {@link javaxportletActionRequest} / {@link javaxportletRenderRequest}
 * 请注意,在Portlet的情况下,明确声明的action / render参数也用于将特定的请求类型映射到处理程序方法(在没有其他信息给出区分动作和呈现请求的情况下)<li > Session对象(Se
 * rvlet API或Portlet API)：{@link javaxservlethttpHttpSession}或{@link javaxportletPortletSession}此类型的参数将强
 * 制存在相应的会话因此,这样的参数永远不会是{@code null} <i>注意,会话访问可能不是线程安全的,特别是在Servlet环境中：考虑将{@link orgspringframeworkwebservletmvcmethodannotationRequestMappingHandlerAdapter#setSynchronizeOnSession"synchronizeOnSession"}
 * 标志如果允许多个请求同时访问会话,则为"true"</i> <li> {@ link orgspringframeworkwebcontextrequestWebRequest}或{@link orgspringframeworkwebcontextrequestNativeWebRequest}
 * 允许通用请求参数访问以及请求/会话属性访问,而无需绑定针对当前请求区域设置的本机Servlet / Portlet API <li> {@ link javautilLocale}(由可用的最具体的区域
 * 设置解析器确定,即配置的{@link org一个Servlet环境中的springframeworkwebservletLocaleResolver)以及一个Portlet环境中的门户区域设置)<li> {@ link javaioInputStream}
 *  / {@link javaioReader}访问请求的内容这将是由Servlet / Portlet公开的原始InputStream / Reader用于生成响应内容的API <li> {@ link javaioOutputStream}
 *  / {@link javaioWriter}这将是由HTTP请求方法的Servlet / Portlet API <li> {@ link orgspringframeworkhttpHttpMethod}
 * 公开的原始OutputStream / Writer < / li> <li> {@ link PathVariable @PathVariable}用于访问URI模板值的注释参数(仅限Servlet)
 * (即/ hotels / {hotel})变量值将转换为声明的方法参数类型默认情况下,URI模板将与正则表达式{@code [^ \\] *}(即除句点之外的任何字符)匹配,但可以通过指定其他正则表达式
 * 来更改,如：/ hotels / {hotel： \\ d +}另外,{@code @PathVariable}可以在{@link javautilMap Map&lt; String,String&gt;}
 * 上使用,以访问所有URI模板变量<li> {@ link MatrixVariable @MatrixVariable}注释参数(Servlet-仅用于访问位于URI路径段中的名称 - 值对矩阵变量必须
 * 用URI模板变量表示。
 * 例如/ hotels / {hotel},其中传入URL可能是"/ hotels / 42; q = 1"另外, {@code @MatrixVariable}可以在{@link javautil上使用映射映射&lt; String,String&gt;}
 * 以访问URL或特定路径变量中的所有矩阵变量<li> {@ link RequestParam @RequestParam}用于访问特定Servlet / Portlet请求参数的注释参数参数值将被转换为
 * 声明的方法参数类型另外,{@code @RequestParam}可以在{@link javautilMap Map&lt; String,String&gt;}或{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt所有请求参数<li> {@ link RequestHeader @RequestHeader}
 * 用于访问特定Servlet / Portlet请求HTTP头的注释参数参数值将转换为声明的方法参数类型此外,{@code @RequestHeader}可以在{@link javautilMap Map&lt; String,String&gt;}
 * ,{@link orgspringframeworkutilMultiValueMap MultiValueMap&lt; String,String&gt;}或{@link orgspringframeworkhttpHttpHeaders HttpHeaders}
 * 方法参数中使用,以访问所有请求标头<li> {@ link RequestBody @RequestBody}用于访问Servlet请求的注释参数(仅Servlet)HTTP内容请求流将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
 * 转换为声明的方法参数类型此类参数可以选择使用{@code @Valid}进行注释,还可以通过{@link orgspringframeworkvalidationErrors}参数来访问验证结果,而不是
 * {@link orgspringframeworkwebbindMethodArgumentNotValidException}异常引发<li> {@ link RequestPart @RequestPart}
 * 注释参数(仅Servlet-only {@literal @MVC 31-only}),用于访问"multipart / form-data"请求的一部分内容请求部分流将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
 * 转换为声明的方法参数类型。
 * 这些参数可以可选地使用{@code @Valid}进行注释,并通过{@link orgspringframeworkvalidationErrors}参数支持对验证结果的访问,而不是{ @link orgspringframeworkwebbindMethodArgumentNotValidException}
 * 异常引发<li> {@ link SessionAttribute @SessionAttribute}用于访问现有永久会话属性的注释参数(例如用户认证对象),而不是通过{@link SessionAttributes}
 *  <li> {@ link RequestAttribute @RequestAttribute}临时存储在会话中的模型属性作为控制器工作流的一部分,用于访问请求属性的注释参数<li> {@ link orgspringframeworkhttpHttpEntity HttpEntity&lt;?&gt;}
 * 参数(仅Servlet)用于访问Servlet请求HTTP头和内容请求流将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
 * 转换为实体主体<li> {@ link javautilMap} / { @link orgspringframeworkuiModel} / {@link orgspringframeworkuiModelMap}
 * ,用于丰富将暴露给Web视图的隐式模型<li> {@ link orgspringframeworkwebservletmvcsupportRedirectAttributes}(仅限Servlet,{@literal @MVC 31-only}
 * )来指定在重定向情况下使用的精确属性集,还可以添加Flash属性(临时存储在服务器端以使其可用)如果该方法返回"redirect："前缀视图名称或{@code RedirectView} <li>命令/
 * 表单对象以绑定参数,则使用{@code RedirectAttributes}而不是隐式模型bean属性或字段,可根据{@link InitBinder}方法和/或HandlerAdapter配置进行可
 * 定制的类型转换 - 请参阅RequestMappingHandlerMethodAdapter上的"webBindingInitializer"属性默认情况下,这些命令对象及其验证结果将作为模型属性公开
 * ,默认情况下,使用属性符号中的非限定命令类名(例如,"orderAddress"作为类型"mypackageOrderAddress")指定参数级{@link ModelAttribute @ModelAttribute }
 * 用于声明特定模型属性名称的注释<li> {@ link orgspringframeworkvalidationErrors} / {@link orgspringframeworkvalidationBindingResult}
 * 上一个命令/表单对象的验证结果(上一个参数)<li> {@ link orgspringframeworkwebbindsupportSessionStatus}状态句柄,用于将表单处理标记为完整(触发
 * 清理由处理器类型级别的{@link SessionAttributes @SessionAttributes}注释指示的会话属性)<li> {@ link orgspringframeworkwebutilUriComponentsBuilder}
 * (仅Servlet, {@literal @MVC 31-only}),用于准备相对于当前请求的主机,端口,方案,上下文路径和servlet映射的字面部分的URL。
 * </ul>
 * 
 * <p> <strong>注意：</strong>支持Java 8的{@code javautilOptional}作为方法参数类型,其注释提供{@code required}属性(例如{@code @RequestParam}
 * ,{@code @ RequestHeader}等)在这些情况下使用{@code javautilOptional}相当于{@code required = false}。
 * 
 *  <p>处理程序方法支持以下返回类型：
 * <ul>
 * <li>一个{@code ModelAndView}对象(Servlet MVC或Portlet MVC),其中模型隐含地丰富了命令对象和{@link ModelAttribute @ModelAttribute}
 * 注释引用数据访问器方法的结果<li> A {@link orgspringframeworkuiModel模型}对象,视图名称通过{@link orgspringframeworkwebservletRequestToViewNameTranslator}
 * 隐式确定,隐式丰富了命令对象的模型以及{@link ModelAttribute @ModelAttribute}注释引用数据访问器方法的结果<li> A {@link javautilMap}通过{@link orgspringframeworkwebservlet隐式确定视图名称来暴露模型的对象RequestToViewNameTranslator}
 * ,隐式丰富了命令对象的模型以及{@link ModelAttribute @ModelAttribute}注释引用数据访问器方法的结果<li>一个{@link orgspringframeworkwebservletView}
 * 对象,模型通过命令对象隐含地确定,并且{@link ModelAttribute @ModelAttribute}注释引用数据访问器方法处理程序方法还可以通过声明{@link orgspringframeworkuiModel}
 * 参数(见上文)以编程方式丰富模型。
 * <li>将解释为视图名称的{@link String}值与模型通过命令对象隐含确定和{@link ModelAttribute @ModelAttribute}注释引用数据访问器方法处理程序方法也可以通
 * 过声明一个{@link orgspringframeworkuiModelMap}参数(见上文)来以编程方式丰富模型。
 * <li> {@ link ResponseBody @ResponseBody}注释方法(仅Servlet)用于访问Servlet响应HTTP内容返回值将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
 * 转换为响应流<li>一个{@link orgspringframeworkhttpHttpEntity HttpEntity&lt;?&gt;}或{@link orgspringframeworkhttpResponseEntity ResponseEntity&lt;?&gt;}
 * 对象(仅限Servlet)访问Servlet响应HTTP头和内容实体主体将使用{@linkplain orgspringframeworkhttpconverterHttpMessageConverter消息转换器}
 * 转换为响应流<li> {@link orgSpringFrameworkhttpHttpHeaders HttpHeaders}对象返回一个没有正文的响应</li> <li>一个{@link Callable}
 * ,由Spring MVC用于在由Spring MVC透明管理的单独的线程中异步获取返回值,代表应用程序<li>应用程序用于在自己选择的单独线程中生成返回值的{@link orgspringframeworkwebcontextrequestasyncDeferredResult}
 * ,作为返回Callable <li> A {@link orgspringframeworkutilconcurrentListenableFuture}的替代方法,该应用程序用于生成在自己选择的单独的
 * 线程中返回值,作为返回Callable <li>的替代方法{@link javautilconcurrentCompletionStage}(由{@link javautilconcurrent例如,CompletableFuture}
 * ),该应用程序用于在自己选择的单独线程中生成返回值,作为返回Callable <li>的替代方法,可以使用{@link orgspringframeworkwebservletmvcmethodannotationResponseBodyEmitter}
 * 将多个对象异步写入响应;也支持作为{@code ResponseEntity} </li> <li>中的正文; {@link orgspringframeworkwebservletmvcmethodannotationSseEmitter}
 * 可用于将服务器发送的事件异步写入响应;也支持作为{@code ResponseEntity} </li> <li>中的正文; {@link orgspringframeworkwebservletmvcmethodannotationStreamingResponseBody}
 * 
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 2.5
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see PatchMapping
 * @see RequestParam
 * @see RequestAttribute
 * @see PathVariable
 * @see ModelAttribute
 * @see SessionAttribute
 * @see SessionAttributes
 * @see InitBinder
 * @see org.springframework.web.context.request.WebRequest
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
 * @see org.springframework.web.portlet.mvc.annotation.DefaultAnnotationHandlerMapping
 * @see org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {

	/**
	 * Assign a name to this mapping.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used on both levels, a combined name is derived by concatenation
	 * with "#" as separator.
	 * <p>
	 * 可用于异步写入响应;也支持{@code ResponseEntity}内的身体</li> <li> {@ code void}如果方法处理响应本身(通过直接写入响应内容,声明类型为{@link javaxservletServletResponse}
	 *  / {@link javaxservlethttpHttpServletResponse} / {@link javaxportletRenderResponse}的参数,该目的)或者如果视图名称应该
	 * 通过{@link orgspringframeworkwebservletRequestToViewNameTranslator}隐含地确定(不在处理程序方法签名中声明响应参数;仅适用于Servlet环
	 * 境)<li>将考虑任何其他返回类型作为要暴露给视图的单一模型属性,使用在方法级别通过{@link ModelAttribute @ModelAttribute}指定的属性名称(或者基于返回类型的类名称的
	 * 默认属性名称)该模型将隐含地丰富了命令对象和{@link ModelAttribute @ModelAttribute}注释引用数据访问器方法的结果。
	 * </ul>
	 * 
	 * 注意：</b> {@code @RequestMapping}只会在配置适当的{@code HandlerMapping}  -  {@ code HandlerAdapter}对的情况下被处理。
	 * 默认情况下,{ @code DispatcherServlet}和{@code DispatcherPortlet}但是,如果要定义自定义{@code HandlerMappings}或{@code HandlerAdapters}
	 * ,则需要将{@code DefaultAnnotationHandlerMapping}和{@code AnnotationMethodMandhodAdapter}添加到您的配置< /代码>。
	 * 注意：</b> {@code @RequestMapping}只会在配置适当的{@code HandlerMapping}  -  {@ code HandlerAdapter}对的情况下被处理。
	 * 
	 * <p> <b>注意：</b> Spring 31在名为{@code RequestMappingHandlerMapping}和{@code RequestMappingHandlerAdapter}的
	 * Servlet环境中为{@code @RequestMapping}方法引入了一组新的支持类,建议使用,甚至需要利用Spring MVC 31中的新功能(在此源文件中搜索{@literal"@MVC 31-only"}
	 * )以及前进新增支持类默认从MVC命名空间启用,并使用MVC Java配置({@code @EnableWebMvc}),但是如果不使用两者,则必须显式配置。
	 * 
	 * <p> <b>注意：</b>当使用控制器接口(例如用于AOP代理)时,请务必始终将<i>全部</i>映射到您的映射注释,例如{@code @RequestMapping}和{ @code @SessionAttributes}
	 *   - 在控制器<i>界面</i>上,而不是实现类。
	 * 
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	String name() default "";

	/**
	 * The primary mapping expressed by this annotation.
	 * <p>In a Servlet environment this is an alias for {@link #path}.
	 * For example {@code @RequestMapping("/foo")} is equivalent to
	 * {@code @RequestMapping(path="/foo")}.
	 * <p>In a Portlet environment this is the mapped portlet modes
	 * (i.e. "EDIT", "VIEW", "HELP" or any custom modes).
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 * <p>
	 *  为此映射分配名称<p> <b>在类型级别和方法级别支持！</b>当在两个级别上使用时,组合名称将通过与"#"作为分隔符
	 * 
	 */
	@AliasFor("path")
	String[] value() default {};

	/**
	 * In a Servlet environment only: the path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do").
	 * At the method level, relative paths (e.g. "edit.do") are supported within
	 * the primary mapping expressed at the type level. Path mapping URIs may
	 * contain placeholders (e.g. "/${connect}")
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 * <p>
	 * 由此注释表示的主要映射<p>在Servlet环境中,这是{@link #path}的别名例如{@code @RequestMapping("/ foo")}等价于{@code @RequestMapping(path = "/ foo")}
	 *  <p>在Portlet环境中,这是映射的portlet模式(即"编辑","视图","帮助"或任何自定义模式)<p> <b>类型级别也支持在方法级别！</b>当在类型级别使用时,所有方法级映射都会继承此
	 * 主映射,将其缩小为特定的处理程序方法。
	 * 
	 * 
	 * @see org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	 * @since 4.2
	 */
	@AliasFor("value")
	String[] path() default {};

	/**
	 * The HTTP request methods to map to, narrowing the primary mapping:
	 * GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this HTTP method restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>Supported for Servlet environments as well as Portlet 2.0 environments.
	 * <p>
	 * 仅在Servlet环境中：路径映射URI(例如"/ myPathdo")也支持Ant样式路径模式(例如"/ myPath / * do")在方法级别,支持相对路径(例如"editdo")在类型级别表示的
	 * 主映射中路径映射URI可能包含占位符(例如"/ $ {connect}")<p> <b>在类型级别和方法级别支持！</b>使用时在类型级别,所有方法级映射都会继承此主映射,将其缩小为特定的处理方法。
	 * 
	 */
	RequestMethod[] method() default {};

	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "myParam=myValue" style
	 * expressions, with a request only mapped if each such parameter is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "myParam!=myValue". "myParam" style expressions are also supported,
	 * with such parameters having to be present in the request (allowed to have
	 * any value). Finally, "!myParam" style expressions indicate that the
	 * specified parameter is <i>not</i> supposed to be present in the request.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this parameter restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>In a Servlet environment, parameter mappings are considered as restrictions
	 * that are enforced at the type level. The primary path mapping (i.e. the
	 * specified URI value) still has to uniquely identify the target handler, with
	 * parameter mappings simply expressing preconditions for invoking the handler.
	 * <p>In a Portlet environment, parameters are taken into account as mapping
	 * differentiators, i.e. the primary portlet mode mapping plus the parameter
	 * conditions uniquely identify the target handler. Different handlers may be
	 * mapped onto the same portlet mode, as long as their parameter mappings differ.
	 * <p>
	 * 要映射到的HTTP请求方法,缩小主映射：GET,POST,HEAD,OPTIONS,PUT,PATCH,DELETE,TRACE <p> <b>在类型级别和方法级别支持！</b >当在类型级别使用时,所
	 * 有方法级别映射都会继承此HTTP方法限制(即在处理程序方法均匀解决之前检查类型级限制)<p>支持Servlet环境以及Portlet 20环境。
	 * 
	 */
	String[] params() default {};

	/**
	 * The headers of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "My-Header=myValue" style
	 * expressions, with a request only mapped if each such header is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "My-Header!=myValue". "My-Header" style expressions are also supported,
	 * with such headers having to be present in the request (allowed to have
	 * any value). Finally, "!My-Header" style expressions indicate that the
	 * specified header is <i>not</i> supposed to be present in the request.
	 * <p>Also supports media type wildcards (*), for headers such as Accept
	 * and Content-Type. For instance,
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 * will match requests with a Content-Type of "text/html", "text/plain", etc.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this header restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>Maps against HttpServletRequest headers in a Servlet environment,
	 * and against PortletRequest properties in a Portlet 2.0 environment.
	 * <p>
	 * 映射请求的参数,缩小主映射<p>任何环境的格式相同：一个"myParam = myValue"样式表达式的序列,只有当每个这样的参数被发现具有给定值表达式时,才会映射请求使用"！="操作符取消,如"my
	 * Param！= myValue"中的"myParam"样式表达式也被支持,这些参数必须存在于请求中(允许有任何值)最后,"！myParam"样式表达式表示指定的参数<i>不</i>应该存在于请求中<p>
	 *  <b>在类型级别和方法级别支持！</b>在类型级别,所有方法级映射都会继承此参数限制(即类型级别的限制在处理程序方法被解析之前被检查)<p>在Servlet环境中,参数映射被认为是在类型级别强制执行的
	 * 限制。
	 * 主路径映射(即指定的URI值)仍然必须唯一标识目标处理程序,参数映射简单地表示调用处理程序的前提条件。
	 * 在Portlet环境中,将参数作为映射差异化程序考虑在内,即主portlet模式映射加参数条件唯一标识目标处理程序不同的处理程序可以映射到相同的Portlet模式,只要它们的参数映射不同。
	 * 
	 * 
	 * @see org.springframework.http.MediaType
	 */
	String[] headers() default {};

	/**
	 * The consumable media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Content-Type} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * </pre>
	 * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with a {@code Content-Type} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this consumes restriction.
	 * <p>
	 * 映射请求的头部,缩小主映射<p>任何环境的格式相同："My-Header = myValue"样式表达式的序列,如果发现每个这样的头都具有给定的值表达式,则只会映射请求可以通过使用"！="操作符来取消,
	 * 如"My-Header！= myValue"中的"My-Header"样式表达式也被支持,这样的头文件必须存在于请求中(允许有任何值)最后,"！My-Header"样式表达式表示指定的标头<i>不应该在
	 * 请求中出现<p>还支持媒体类型通配符(*),用于标头,如接受和内容-Type例如,。
	 * <pre class="code">
	 *  @RequestMapping(value ="/ something",headers ="content-type = text / *")
	 * </pre>
	 * 将匹配请求与"text / html","text / plain"等的内容类型<p> <b>在类型级别和方法级别支持！</b>在类型中使用时级别中,所有方法级别的映射都会继承此头部限制(即在处理程序方
	 * 法得到解决之前检查类型级限制)<p>针对Portlet 20环境中的Servlet环境中的HttpServletRequest头以及PortletRequest属性进行映射。
	 * 
	 * 
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
	 */
	String[] consumes() default {};

	/**
	 * The producible media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Accept} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = "application/json; charset=UTF-8"
	 * </pre>
	 * <p>It affects the actual content type written, for example to produce a JSON response
	 * with UTF-8 encoding, {@code "application/json; charset=UTF-8"} should be used.
	 * <p>Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with a {@code Accept} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this produces restriction.
	 * <p>
	 *  映射请求的可消耗媒体类型,缩小主映射格式是单一媒体类型或媒体类型序列,如果{@code Content-Type}与这些媒体类型之一匹配,则仅映射请求例子：
	 * <pre class="code">
	 *  消耗="text / plain"消耗= {"text / plain","application / *"}
	 * </pre>
	 * 表达式可以通过使用"！"运算符,如"！text / plain"所示,它匹配所有请求与除"text / plain"之外的{@code Content-Type} <p> <b>在类型级别和方法级别支持
	 * ！ </b>当在类型级别使用时,所有方法级别的映射都会覆盖此消耗限制。
	 * 
	 * 
	 * @see org.springframework.http.MediaType
	 */
	String[] produces() default {};

}
