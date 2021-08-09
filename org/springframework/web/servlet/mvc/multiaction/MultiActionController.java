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

package org.springframework.web.servlet. mvc.multiaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * {@link org.springframework.web.servlet.mvc.Controller Controller}
 * implementation that allows multiple request types to be handled by the same
 * class. Subclasses of this class can handle several different types of
 * request with methods of the form
 *
 * <pre class="code">public (ModelAndView | Map | String | void) actionName(HttpServletRequest request, HttpServletResponse response, [,HttpSession] [,AnyObject]);</pre>
 *
 * A Map return value indicates a model that is supposed to be passed to a default view
 * (determined through a {@link org.springframework.web.servlet.RequestToViewNameTranslator}).
 * A String return value indicates the name of a view to be rendered without a specific model.
 *
 * <p>May take a third parameter (of type {@link HttpSession}) in which an
 * existing session will be required, or a third parameter of an arbitrary
 * class that gets treated as the command (that is, an instance of the class
 * gets created, and request parameters get bound to it)
 *
 * <p>These methods can throw any kind of exception, but should only let
 * propagate those that they consider fatal, or which their class or superclass
 * is prepared to catch by implementing an exception handler.
 *
 * <p>When returning just a {@link Map} instance view name translation will be
 * used to generate the view name. The configured
 * {@link org.springframework.web.servlet.RequestToViewNameTranslator} will be
 * used to determine the view name.
 *
 * <p>When returning {@code void} a return value of {@code null} is
 * assumed meaning that the handler method is responsible for writing the
 * response directly to the supplied {@link HttpServletResponse}.
 *
 * <p>This model allows for rapid coding, but loses the advantage of
 * compile-time checking. It is similar to a Struts {@code DispatchAction},
 * but more sophisticated. Also supports delegation to another object.
 *
 * <p>An implementation of the {@link MethodNameResolver} interface defined in
 * this package should return a method name for a given request, based on any
 * aspect of the request, such as its URL or an "action" parameter. The actual
 * strategy can be configured via the "methodNameResolver" bean property, for
 * each {@code MultiActionController}.
 *
 * <p>The default {@code MethodNameResolver} is
 * {@link InternalPathMethodNameResolver}; further included strategies are
 * {@link PropertiesMethodNameResolver} and {@link ParameterMethodNameResolver}.
 *
 * <p>Subclasses can implement custom exception handler methods with names such
 * as:
 *
 * <pre class="code">public ModelAndView anyMeaningfulName(HttpServletRequest request, HttpServletResponse response, ExceptionClass exception);</pre>
 *
 * The third parameter can be any subclass or {@link Exception} or
 * {@link RuntimeException}.
 *
 * <p>There can also be an optional {@code xxxLastModified} method for
 * handlers, of signature:
 *
 * <pre class="code">public long anyMeaningfulNameLastModified(HttpServletRequest request)</pre>
 *
 * If such a method is present, it will be invoked. Default return from
 * {@code getLastModified} is -1, meaning that the content must always be
 * regenerated.
 *
 * <p><b>Note that all handler methods need to be public and that
 * method overloading is <i>not</i> allowed.</b>
 *
 * <p>See also the description of the workflow performed by
 * {@link AbstractController the superclass} (in that section of the class
 * level Javadoc entitled 'workflow').
 *
 * <p><b>Note:</b> For maximum data binding flexibility, consider direct usage of a
 * {@link ServletRequestDataBinder} in your controller method, instead of relying
 * on a declared command argument. This allows for full control over the entire
 * binder setup and usage, including the invocation of {@link Validator Validators}
 * and the subsequent evaluation of binding/validation errors.
 *
 * <p>
 * {@link orgspringframeworkwebservletmvcController Controller}实现,允许多个请求类型由同一类处理该类的子类可以使用表单的方法来处理几种不同类型的
 * 请求。
 * 
 *  public(ModelAndView | Map | String | void)actionName(HttpServletRequest request,HttpServletResponse 
 * response,[,HttpSession] [,AnyObject]); </pre>。
 * 
 *  Map返回值表示应该传递给默认视图的模型(通过{@link orgspringframeworkwebservletRequestToViewNameTranslator}确定))String返回值指
 * 示要呈现而不具有特定模型的视图的名称。
 * 
 * <p>可以使用第三个参数(类型为{@link HttpSession}),其中将需要现有会话,或者将被视为命令的任意类的第三个参数(即类的一个实例获取)创建并请求参数绑定到它)
 * 
 *  <p>这些方法可以引发任何类型的异常,但是只应该传播那些他们认为是致命的,或者通过实现一个异常处理程序来让他们的类或者超类准备捕获
 * 
 *  <p>只返回{@link Map}实例视图名称转换将用于生成视图名称配置的{@link orgspringframeworkwebservletRequestToViewNameTranslator}
 * 将用于确定视图名称。
 * 
 * <p>当返回{@code void}时,假定返回值为{@code null},这意味着处理程序方法负责将响应直接写入提供的{@link HttpServletResponse}
 * 
 *  <p>这个模型允许快速编码,但失去编译时检查的优点它类似于Struts {@code DispatchAction},但更复杂也支持委托给另一个对象
 * 
 *  <p>此包中定义的{@link MethodNameResolver}接口的实现应根据请求的任何方面(例如其URL或"action"参数)返回给定请求的方法名称。
 * 实际策略可以是通过"methodNameResolver"bean属性配置,对于每个{@code MultiActionController}。
 * 
 * <p>默认的{@code MethodNameResolver}是{@link InternalPathMethodNameResolver};进一步包括的策略是{@link PropertiesMethodNameResolver}
 * 和{@link ParameterMethodNameResolver}。
 * 
 *  <p>子类可以实现自定义异常处理程序方法,其名称如下：
 * 
 *  public classAndView anyMeaningfulName(HttpServletRequest request,HttpServletResponse response,Except
 * ionClass exception); </pre>。
 * 
 *  第三个参数可以是任何子类或{@link Exception}或{@link RuntimeException}
 * 
 *  <p>还可以使用可选的{@code xxxLastModified}处理程序方法,签名：
 * 
 *  <pre class ="code"> public long anyMeaningfulNameLastModified(HttpServletRequest request)</pre>
 * 
 * 如果存在这样一种方法,它将被调用{@code getLastModified}的默认返回值为-1,这意味着内容必须始终被重新生成
 * 
 *  <p> <b>请注意,所有处理程序方法都需要公开,方法重载不允许</i> </b>
 * 
 *  <p>另请参阅{@link AbstractController the superclass}执行的工作流的描述(在类级别为Javadoc的"工作流"部分)
 * 
 * <p> <b>注意：</b>为了实现最大的数据绑定灵活性,请考虑在控制器方法中直接使用{@link ServletRequestDataBinder},而不是依赖于声明的命令参数。
 * 这样可以完全控制整个绑定器设置和使用,包括调用{@link Validator Validators}以及后续的绑定/验证错误评估。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Rob Harrop
 * @author Sam Brannen
 * @see MethodNameResolver
 * @see InternalPathMethodNameResolver
 * @see PropertiesMethodNameResolver
 * @see ParameterMethodNameResolver
 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified
 * @see org.springframework.web.bind.ServletRequestDataBinder
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public class MultiActionController extends AbstractController implements LastModified {

	/** Suffix for last-modified methods */
	public static final String LAST_MODIFIED_METHOD_SUFFIX = "LastModified";

	/** Default command name used for binding command objects: "command" */
	public static final String DEFAULT_COMMAND_NAME = "command";

	/**
	 * Log category to use when no mapped handler is found for a request.
	 * <p>
	 *  没有找到请求的映射处理程序时使用的日志类别
	 * 
	 * 
	 * @see #pageNotFoundLogger
	 */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";


	/**
	 * Additional logger to use when no mapped handler is found for a request.
	 * <p>
	 *  未找到请求的映射处理程序时要使用的附加记录器
	 * 
	 * 
	 * @see #PAGE_NOT_FOUND_LOG_CATEGORY
	 */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

	/** Object we'll invoke methods on. Defaults to this. */
	private Object delegate;

	/** Delegate that knows how to determine method names from incoming requests */
	private MethodNameResolver methodNameResolver = new InternalPathMethodNameResolver();

	/** List of Validators to apply to commands */
	private Validator[] validators;

	/** Optional strategy for pre-initializing data binding */
	private WebBindingInitializer webBindingInitializer;

	/** Handler methods, keyed by name */
	private final Map<String, Method> handlerMethodMap = new HashMap<String, Method>();

	/** LastModified methods, keyed by handler method name (without LAST_MODIFIED_SUFFIX) */
	private final Map<String, Method> lastModifiedMethodMap = new HashMap<String, Method>();

	/** Methods, keyed by exception class */
	private final Map<Class<?>, Method> exceptionHandlerMap = new HashMap<Class<?>, Method>();


	/**
	 * Constructor for {@code MultiActionController} that looks for
	 * handler methods in the present subclass.
	 * <p>
	 *  {@code MultiActionController}的构造方法,用于查找本子类中的处理程序方法
	 * 
	 */
	public MultiActionController() {
		this.delegate = this;
		registerHandlerMethods(this.delegate);
		// We'll accept no handler methods found here - a delegate might be set later on.
	}

	/**
	 * Constructor for {@code MultiActionController} that looks for
	 * handler methods in delegate, rather than a subclass of this class.
	 * <p>
	 *  {@code MultiActionController}的构造方法,在委托中查找处理程序方法,而不是此类的子类
	 * 
	 * 
	 * @param delegate handler object. This does not need to implement any
	 * particular interface, as everything is done using reflection.
	 */
	public MultiActionController(Object delegate) {
		setDelegate(delegate);
	}


	/**
	 * Set the delegate used by this class; the default is {@code this},
	 * assuming that handler methods have been added by a subclass.
	 * <p>This method does not get invoked once the class is configured.
	 * <p>
	 * 设置此类使用的委托;默认值为{@code this},假设处理程序方法已被子类添加<p>一旦配置了类,该方法就不会被调用
	 * 
	 * 
	 * @param delegate an object containing handler methods
	 * @throws IllegalStateException if no handler methods are found
	 */
	public final void setDelegate(Object delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
		registerHandlerMethods(this.delegate);
		// There must be SOME handler methods.
		if (this.handlerMethodMap.isEmpty()) {
			throw new IllegalStateException("No handler methods in class [" + this.delegate.getClass() + "]");
		}
	}

	/**
	 * Set the method name resolver that this class should use.
	 * <p>Allows parameterization of handler method mappings.
	 * <p>
	 *  设置此类应该使用的方法名称解析器<p>允许对参数化处理程序方法映射
	 * 
	 */
	public final void setMethodNameResolver(MethodNameResolver methodNameResolver) {
		this.methodNameResolver = methodNameResolver;
	}

	/**
	 * Return the MethodNameResolver used by this class.
	 * <p>
	 *  返回此类使用的MethodNameResolver
	 * 
	 */
	public final MethodNameResolver getMethodNameResolver() {
		return this.methodNameResolver;
	}

	/**
	 * Set the {@link Validator Validators} for this controller.
	 * <p>The {@code Validators} must support the specified command class.
	 * <p>
	 *  为此控制器设置{@link Validator Validators} <p> {@code Validators}必须支持指定的命令类
	 * 
	 */
	public final void setValidators(Validator[] validators) {
		this.validators = validators;
	}

	/**
	 * Return the Validators for this controller.
	 * <p>
	 *  返回此控制器的验证器
	 * 
	 */
	public final Validator[] getValidators() {
		return this.validators;
	}

	/**
	 * Specify a WebBindingInitializer which will apply pre-configured
	 * configuration to every DataBinder that this controller uses.
	 * <p>Allows for factoring out the entire binder configuration
	 * to separate objects, as an alternative to {@link #initBinder}.
	 * <p>
	 *  指定一个WebBindingInitializer,它将预先配置的配置应用于该控制器使用的每个DataBinder <p>允许将整个绑定器配置分解为单独的对象,作为{@link #initBinder}
	 * 的替代方法。
	 * 
	 */
	public final void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	/**
	 * Return the WebBindingInitializer (if any) which will apply pre-configured
	 * configuration to every DataBinder that this controller uses.
	 * <p>
	 * 返回WebBindingInitializer(如果有的话),将将预配置的配置应用到该控制器使用的每个DataBinder
	 * 
	 */
	public final WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}


	/**
	 * Registers all handlers methods on the delegate object.
	 * <p>
	 *  在委托对象上注册所有处理程序方法
	 * 
	 */
	private void registerHandlerMethods(Object delegate) {
		this.handlerMethodMap.clear();
		this.lastModifiedMethodMap.clear();
		this.exceptionHandlerMap.clear();

		// Look at all methods in the subclass, trying to find
		// methods that are validators according to our criteria
		Method[] methods = delegate.getClass().getMethods();
		for (Method method : methods) {
			// We're looking for methods with given parameters.
			if (isExceptionHandlerMethod(method)) {
				registerExceptionHandlerMethod(method);
			}
			else if (isHandlerMethod(method)) {
				registerHandlerMethod(method);
				registerLastModifiedMethodIfExists(delegate, method);
			}
		}
	}

	/**
	 * Is the supplied method a valid handler method?
	 * <p>Does not consider {@code Controller.handleRequest} itself
	 * as handler method (to avoid potential stack overflow).
	 * <p>
	 *  提供的方法是否是有效的处理程序方法? <p>不将{@code ControllerhandleRequest}本身视为处理程序方法(以避免潜在的堆栈溢出)
	 * 
	 */
	private boolean isHandlerMethod(Method method) {
		Class<?> returnType = method.getReturnType();
		if (ModelAndView.class == returnType || Map.class == returnType || String.class == returnType ||
				void.class == returnType) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			return (parameterTypes.length >= 2 &&
					HttpServletRequest.class == parameterTypes[0] &&
					HttpServletResponse.class == parameterTypes[1] &&
					!("handleRequest".equals(method.getName()) && parameterTypes.length == 2));
		}
		return false;
	}

	/**
	 * Is the supplied method a valid exception handler method?
	 * <p>
	 *  提供的方法是否是有效的异常处理程序方法?
	 * 
	 */
	private boolean isExceptionHandlerMethod(Method method) {
		return (isHandlerMethod(method) &&
				method.getParameterTypes().length == 3 &&
				Throwable.class.isAssignableFrom(method.getParameterTypes()[2]));
	}

	/**
	 * Registers the supplied method as a request handler.
	 * <p>
	 *  将提供的方法注册为请求处理程序
	 * 
	 */
	private void registerHandlerMethod(Method method) {
		if (logger.isDebugEnabled()) {
			logger.debug("Found action method [" + method + "]");
		}
		this.handlerMethodMap.put(method.getName(), method);
	}

	/**
	 * Registers a last-modified handler method for the supplied handler method
	 * if one exists.
	 * <p>
	 *  为提供的处理程序方法注册最后修改的处理程序方法(如果存在)
	 * 
	 */
	private void registerLastModifiedMethodIfExists(Object delegate, Method method) {
		// Look for corresponding LastModified method.
		try {
			Method lastModifiedMethod = delegate.getClass().getMethod(
					method.getName() + LAST_MODIFIED_METHOD_SUFFIX,
					new Class<?>[] {HttpServletRequest.class});
			Class<?> returnType = lastModifiedMethod.getReturnType();
			if (!(long.class == returnType || Long.class == returnType)) {
				throw new IllegalStateException("last-modified method [" + lastModifiedMethod +
						"] declares an invalid return type - needs to be 'long' or 'Long'");
			}
			// Put in cache, keyed by handler method name.
			this.lastModifiedMethodMap.put(method.getName(), lastModifiedMethod);
			if (logger.isDebugEnabled()) {
				logger.debug("Found last-modified method for handler method [" + method + "]");
			}
		}
		catch (NoSuchMethodException ex) {
			// No last modified method. That's ok.
		}
	}

	/**
	 * Registers the supplied method as an exception handler.
	 * <p>
	 *  将提供的方法注册为异常处理程序
	 * 
	 */
	private void registerExceptionHandlerMethod(Method method) {
		this.exceptionHandlerMap.put(method.getParameterTypes()[2], method);
		if (logger.isDebugEnabled()) {
			logger.debug("Found exception handler method [" + method + "]");
		}
	}


	//---------------------------------------------------------------------
	// Implementation of LastModified
	//---------------------------------------------------------------------

	/**
	 * Try to find an XXXXLastModified method, where XXXX is the name of a handler.
	 * Return -1 if there's no such handler, indicating that content must be updated.
	 * <p>
	 *  尝试找到一个XXXXLastModified方法,其中XXXX是处理程序的名称返回-1,如果没有这样的处理程序,表示内容必须更新
	 * 
	 * 
	 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified(HttpServletRequest)
	 */
	@Override
	public long getLastModified(HttpServletRequest request) {
		try {
			String handlerMethodName = this.methodNameResolver.getHandlerMethodName(request);
			Method lastModifiedMethod = this.lastModifiedMethodMap.get(handlerMethodName);
			if (lastModifiedMethod != null) {
				try {
					// Invoke the last-modified method...
					Long wrappedLong = (Long) lastModifiedMethod.invoke(this.delegate, request);
					return (wrappedLong != null ? wrappedLong : -1);
				}
				catch (Exception ex) {
					// We encountered an error invoking the last-modified method.
					// We can't do anything useful except log this, as we can't throw an exception.
					logger.error("Failed to invoke last-modified method", ex);
				}
			}
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			// No handler method for this request. This shouldn't happen, as this
			// method shouldn't be called unless a previous invocation of this class
			// has generated content. Do nothing, that's OK: We'll return default.
		}
		return -1L;
	}


	//---------------------------------------------------------------------
	// Implementation of AbstractController
	//---------------------------------------------------------------------

	/**
	 * Determine a handler method and invoke it.
	 * <p>
	 * 确定一个处理程序方法并调用它
	 * 
	 * 
	 * @see MethodNameResolver#getHandlerMethodName
	 * @see #invokeNamedMethod
	 * @see #handleNoSuchRequestHandlingMethod
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			String methodName = this.methodNameResolver.getHandlerMethodName(request);
			return invokeNamedMethod(methodName, request, response);
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			return handleNoSuchRequestHandlingMethod(ex, request, response);
		}
	}

	/**
	 * Handle the case where no request handler method was found.
	 * <p>The default implementation logs a warning and sends an HTTP 404 error.
	 * Alternatively, a fallback view could be chosen, or the
	 * NoSuchRequestHandlingMethodException could be rethrown as-is.
	 * <p>
	 *  处理没有发现请求处理程序方法的情况<p>默认实现记录一个警告并发送HTTP 404错误或者,可以选择回退视图,否则NoSuchRequestHandlingMethodException可以按原样重新
	 * 抛出。
	 * 
	 * 
	 * @param ex the NoSuchRequestHandlingMethodException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return a ModelAndView to render, or {@code null} if handled directly
	 * @throws Exception an Exception that should be thrown as result of the servlet request
	 */
	protected ModelAndView handleNoSuchRequestHandlingMethod(
			NoSuchRequestHandlingMethodException ex, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		pageNotFoundLogger.warn(ex.getMessage());
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	/**
	 * Invokes the named method.
	 * <p>Uses a custom exception handler if possible; otherwise, throw an
	 * unchecked exception; wrap a checked exception or Throwable.
	 * <p>
	 *  调用命名方法<p>如果可能,使用自定义异常处理程序;否则,抛出未经检查的异常;包装检查异常或Throwable
	 * 
	 */
	protected final ModelAndView invokeNamedMethod(
			String methodName, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Method method = this.handlerMethodMap.get(methodName);
		if (method == null) {
			throw new NoSuchRequestHandlingMethodException(methodName, getClass());
		}

		try {
			Class<?>[] paramTypes = method.getParameterTypes();
			List<Object> params = new ArrayList<Object>(4);
			params.add(request);
			params.add(response);

			if (paramTypes.length >= 3 && HttpSession.class == paramTypes[2]) {
				HttpSession session = request.getSession(false);
				if (session == null) {
					throw new HttpSessionRequiredException(
							"Pre-existing session required for handler method '" + methodName + "'");
				}
				params.add(session);
			}

			// If last parameter isn't of HttpSession type, it's a command.
			if (paramTypes.length >= 3 && HttpSession.class != paramTypes[paramTypes.length - 1]) {
				Object command = newCommandObject(paramTypes[paramTypes.length - 1]);
				params.add(command);
				bind(request, command);
			}

			Object returnValue = method.invoke(this.delegate, params.toArray(new Object[params.size()]));
			return massageReturnValueIfNecessary(returnValue);
		}
		catch (InvocationTargetException ex) {
			// The handler method threw an exception.
			return handleException(request, response, ex.getTargetException());
		}
		catch (Exception ex) {
			// The binding process threw an exception.
			return handleException(request, response, ex);
		}
	}

	/**
	 * Processes the return value of a handler method to ensure that it either returns
	 * {@code null} or an instance of {@link ModelAndView}. When returning a {@link Map},
	 * the {@link Map} instance is wrapped in a new {@link ModelAndView} instance.
	 * <p>
	 *  处理handler方法的返回值,以确保它返回{@code null}或{@link ModelAndView}的一个实例返回{@link Map}时,{@link Map}实例被包装在一个新的{ @link ModelAndView}
	 * 实例。
	 * 
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView massageReturnValueIfNecessary(Object returnValue) {
		if (returnValue instanceof ModelAndView) {
			return (ModelAndView) returnValue;
		}
		else if (returnValue instanceof Map) {
			return new ModelAndView().addAllObjects((Map<String, ?>) returnValue);
		}
		else if (returnValue instanceof String) {
			return new ModelAndView((String) returnValue);
		}
		else {
			// Either returned null or was 'void' return.
			// We'll assume that the handle method already wrote the response.
			return null;
		}
	}


	/**
	 * Create a new command object of the given class.
	 * <p>This implementation uses {@code BeanUtils.instantiateClass},
	 * so commands need to have public no-arg constructors.
	 * Subclasses can override this implementation if desired.
	 * <p>
	 * 创建给定类的新命令对象<p>此实现使用{@code BeanUtilsinstantiateClass},因此命令需要有public no-arg构造函数如果需要,子类可以覆盖此实现
	 * 
	 * 
	 * @throws Exception if the command object could not be instantiated
	 * @see org.springframework.beans.BeanUtils#instantiateClass(Class)
	 */
	protected Object newCommandObject(Class<?> clazz) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new command of class [" + clazz.getName() + "]");
		}
		return BeanUtils.instantiateClass(clazz);
	}

	/**
	 * Bind request parameters onto the given command bean
	 * <p>
	 *  将请求参数绑定到给定的命令bean上
	 * 
	 * 
	 * @param request request from which parameters will be bound
	 * @param command command object, that must be a JavaBean
	 * @throws Exception in case of invalid state or arguments
	 */
	protected void bind(HttpServletRequest request, Object command) throws Exception {
		logger.debug("Binding request parameters onto MultiActionController command");
		ServletRequestDataBinder binder = createBinder(request, command);
		binder.bind(request);
		if (this.validators != null) {
			for (Validator validator : this.validators) {
				if (validator.supports(command.getClass())) {
					ValidationUtils.invokeValidator(validator, command, binder.getBindingResult());
				}
			}
		}
		binder.closeNoCatch();
	}

	/**
	 * Create a new binder instance for the given command and request.
	 * <p>Called by {@code bind}. Can be overridden to plug in custom
	 * ServletRequestDataBinder subclasses.
	 * <p>The default implementation creates a standard ServletRequestDataBinder,
	 * and invokes {@code initBinder}. Note that {@code initBinder}
	 * will not be invoked if you override this method!
	 * <p>
	 *  为给定的命令创建一个新的binder实例并请求<p>由{@code bind}调用可以覆盖以插入自定义ServletRequestDataBinder子类<p>默认实现创建一个标准的ServletRe
	 * questDataBinder,并调用{@code initBinder}注意如果您重写此方法,则不会调用{@code initBinder}！。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param command the command to bind onto
	 * @return the new binder instance
	 * @throws Exception in case of invalid state or arguments
	 * @see #bind
	 * @see #initBinder
	 */
	protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object command) throws Exception {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command, getCommandName(command));
		initBinder(request, binder);
		return binder;
	}

	/**
	 * Return the command name to use for the given command object.
	 * <p>Default is "command".
	 * <p>
	 *  返回用于给定命令对象的命令名称<p>默认值为"command"
	 * 
	 * 
	 * @param command the command object
	 * @return the command name to use
	 * @see #DEFAULT_COMMAND_NAME
	 */
	protected String getCommandName(Object command) {
		return DEFAULT_COMMAND_NAME;
	}

	/**
	 * Initialize the given binder instance, for example with custom editors.
	 * Called by {@code createBinder}.
	 * <p>This method allows you to register custom editors for certain fields of your
	 * command class. For instance, you will be able to transform Date objects into a
	 * String pattern and back, in order to allow your JavaBeans to have Date properties
	 * and still be able to set and display them in an HTML interface.
	 * <p>The default implementation is empty.
	 * <p>Note: the command object is not directly passed to this method, but it's available
	 * via {@link org.springframework.validation.DataBinder#getTarget()}
	 * <p>
	 * 初始化给定的绑定实例,例如使用自定义编辑器调用{@code createBinder} <p>此方法允许您为命令类的某些字段注册自定义编辑器例如,您可以将Date对象转换为String模式和返回,以便允
	 * 许您的JavaBeans具有Date属性,并且仍然可以在HTML界面中设置和显示它们<p>默认实现为空<p>注意：命令对象不直接传递到此方法,但可以通过{@link orgspringframeworkvalidationDataBinder#getTarget()}
	 * 获得。
	 * 
	 * 
	 * @param request current HTTP request
	 * @param binder new binder instance
	 * @throws Exception in case of invalid state or arguments
	 * @see #createBinder
	 * @see org.springframework.validation.DataBinder#registerCustomEditor
	 * @see org.springframework.beans.propertyeditors.CustomDateEditor
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		if (this.webBindingInitializer != null) {
			this.webBindingInitializer.initBinder(binder, new ServletWebRequest(request));
		}
	}


	/**
	 * Determine the exception handler method for the given exception.
	 * <p>Can return {@code null} if not found.
	 * <p>
	 *  确定给定异常的异常处理程序方法<p>如果没有找到可返回{@code null}
	 * 
	 * 
	 * @return a handler for the given exception type, or {@code null}
	 * @param exception the exception to handle
	 */
	protected Method getExceptionHandler(Throwable exception) {
		Class<?> exceptionClass = exception.getClass();
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to find handler for exception class [" + exceptionClass.getName() + "]");
		}
		Method handler = this.exceptionHandlerMap.get(exceptionClass);
		while (handler == null && exceptionClass != Throwable.class) {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to find handler for exception superclass [" + exceptionClass.getName() + "]");
			}
			exceptionClass = exceptionClass.getSuperclass();
			handler = this.exceptionHandlerMap.get(exceptionClass);
		}
		return handler;
	}

	/**
	 * We've encountered an exception thrown from a handler method.
	 * Invoke an appropriate exception handler method, if any.
	 * <p>
	 * 我们遇到从处理程序方法抛出的异常调用适当的异常处理程序方法(如果有)
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param ex the exception that got thrown
	 * @return a ModelAndView to render the response
	 */
	private ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Throwable ex)
			throws Exception {

		Method handler = getExceptionHandler(ex);
		if (handler != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking exception handler [" + handler + "] for exception: " + ex);
			}
			try {
				Object returnValue = handler.invoke(this.delegate, request, response, ex);
				return massageReturnValueIfNecessary(returnValue);
			}
			catch (InvocationTargetException ex2) {
				logger.error("Original exception overridden by exception handling failure", ex);
				ReflectionUtils.rethrowException(ex2.getTargetException());
			}
			catch (Exception ex2) {
				logger.error("Failed to invoke exception handler method", ex2);
			}
		}
		else {
			// If we get here, there was no custom handler or we couldn't invoke it.
			ReflectionUtils.rethrowException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

}
