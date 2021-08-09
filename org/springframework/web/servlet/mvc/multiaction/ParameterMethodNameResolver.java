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

package org.springframework.web.servlet.mvc.multiaction;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Implementation of {@link MethodNameResolver} which supports several strategies
 * for mapping parameter values to the names of methods to invoke.
 *
 * <p>The simplest strategy looks for a specific named parameter, whose value is
 * considered the name of the method to invoke. The name of the parameter may be
 * specified as a JavaBean property, if the default {@code action} is not
 * acceptable.
 *
 * <p>The alternative strategy uses the very existence of a request parameter (
 * i.e. a request parameter with a certain name is found) as an indication that a
 * method with the same name should be dispatched to. In this case, the actual
 * request parameter value is ignored. The list of parameter/method names may
 * be set via the {@code methodParamNames} JavaBean property.
 *
 * <p>The second resolution strategy is primarily expected to be used with web
 * pages containing multiple submit buttons. The 'name' attribute of each
 * button should be set to the mapped method name, while the 'value' attribute
 * is normally displayed as the button label by the browser, and will be
 * ignored by the resolver.
 *
 * <p>Note that the second strategy also supports the use of submit buttons of
 * type 'image'. That is, an image submit button named 'reset' will normally be
 * submitted by the browser as two request parameters called 'reset.x', and
 * 'reset.y'. When checking for the existence of a parameter from the
 * {@code methodParamNames} list, to indicate that a specific method should
 * be called, the code will look for a request parameter in the "reset" form
 * (exactly as specified in the list), and in the "reset.x" form ('.x' appended
 * to the name in the list). In this way it can handle both normal and image
 * submit buttons. The actual method name resolved, if there is a match, will
 * always be the bare form without the ".x".
 *
 * <p><b>Note:</b> If both strategies are configured, i.e. both "paramName"
 * and "methodParamNames" are specified, then both will be checked for any given
 * request. A match for an explicit request parameter in the "methodParamNames"
 * list always wins over a value specified for a "paramName" action parameter.
 *
 * <p>For use with either strategy, the name of a default handler method to use
 * when there is no match, can be specified as a JavaBean property.
 *
 * <p>For both resolution strategies, the method name is of course coming from
 * some sort of view code, (such as a JSP page). While this may be acceptable,
 * it is sometimes desirable to treat this only as a 'logical' method name,
 * with a further mapping to a 'real' method name. As such, an optional
 * 'logical' mapping may be specified for this purpose.
 *
 * <p>
 *  {@link MethodNameResolver}的实现,它支持将参数值映射到要调用的方法的名称的几种策略
 * 
 * <p>最简单的策略寻找一个特定的命名参数,其值被认为是要调用的方法的名称如果默认的{@code action}不可接受,参数的名称可以被指定为JavaBean属性
 * 
 *  <p>替代策略使用请求参数的存在(即,找到具有特定名称的请求参数)作为指示应该分配具有相同名称的方法在这种情况下,实际请求参数值为ignored可以通过{@code methodParamNames}
 *  JavaBean属性设置参数/方法名称列表。
 * 
 * <p>第二种分辨率策略主要用于包含多个提交按钮的网页每个按钮的"name"属性应设置为映射方法名称,而"value"属性通常显示为按钮标签由浏览器,并将被解析器忽略
 * 
 * <p>请注意,第二种策略还支持使用"图像"类型的提交按钮,也就是说,名为"reset"的图像提交按钮通常由浏览器提交为两个请求参数,称为"resetx","resety" '当从{@code methodParamNames}
 * 列表中检查参数的存在时,为了指示应该调用一个特定的方法,代码将在"重置"窗体中查找一个请求参数(完全按照列表​​中的指定) ,并以"resetx"形式("x"附加到列表中的名称)以这种方式它可以处理正常
 * 和图像提交按钮实际的方法名称解析,如果有匹配,将永远是裸体的形式没有"x"。
 * 
 * <p> <b>注意：</b>如果配置了两种策略,即指定了"paramName"和"methodParamNames"两者,则将检查任何给定请求的匹配A对"methodParamNames中的显式请求参数
 * "的匹配"列表总是胜过为"paramName"操作参数指定的值。
 * 
 *  <p>为了与任一策略一起使用,当没有匹配时使用的默认处理程序方法的名称可以指定为JavaBean属性
 * 
 *  <p>对于这两种分辨率策略,方法名称当然来自于某种视图代码(例如JSP页面)虽然这可能是可以接受的,但有时也可以将其视为"逻辑"方法名称,进一步映射到"真实"方法名称因此,可以为此目的指定可选的"逻辑
 * "映射。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @see #setParamName
 * @see #setMethodParamNames
 * @see #setLogicalMappings
 * @see #setDefaultMethodName
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public class ParameterMethodNameResolver implements MethodNameResolver {

	/**
	 * Default name for the parameter whose value identifies the method to invoke:
	 * "action".
	 * <p>
	 */
	public static final String DEFAULT_PARAM_NAME = "action";


	protected final Log logger = LogFactory.getLog(getClass());

	private String paramName = DEFAULT_PARAM_NAME;

	private String[] methodParamNames;

	private Properties logicalMappings;

	private String defaultMethodName;


	/**
	 * Set the name of the parameter whose <i>value</i> identifies the name of
	 * the method to invoke. Default is "action".
	 * <p>Alternatively, specify parameter names where the very existence of each
	 * parameter means that a method of the same name should be invoked, via
	 * the "methodParamNames" property.
	 * <p>
	 * 其值标识要调用的方法的参数的默认名称："action"
	 * 
	 * 
	 * @see #setMethodParamNames
	 */
	public void setParamName(String paramName) {
		if (paramName != null) {
			Assert.hasText(paramName, "'paramName' must not be empty");
		}
		this.paramName = paramName;
	}

	/**
	 * Set a String array of parameter names, where the <i>very existence of a
	 * parameter</i> in the list (with value ignored) means that a method of the
	 * same name should be invoked. This target method name may then be optionally
	 * further mapped via the {@link #logicalMappings} property, in which case it
	 * can be considered a logical name only.
	 * <p>
	 *  设置<i>值</i>标识要调用的方法的名称的参数的名称Default是"action"<p>或者,指定参数名称,其中每个参数的存在意味着相同的方法应通过"methodParamNames"属性调用名称
	 * 。
	 * 
	 * 
	 * @see #setParamName
	 */
	public void setMethodParamNames(String... methodParamNames) {
		this.methodParamNames = methodParamNames;
	}

	/**
	 * Specifies a set of optional logical method name mappings. For both resolution
	 * strategies, the method name initially comes in from the view layer. If that needs
	 * to be treated as a 'logical' method name, and mapped to a 'real' method name, then
	 * a name/value pair for that purpose should be added to this Properties instance.
	 * Any method name not found in this mapping will be considered to already be the
	 * real method name.
	 * <p>Note that in the case of no match, where the {@link #defaultMethodName} property
	 * is used if available, that method name is considered to already be the real method
	 * name, and is not run through the logical mapping.
	 * <p>
	 *  设置参数名称的String数组,其中列表中</i>的<i>非常存在"(忽略值)意味着应该调用相同名称的方法此方法名称可以随意地进一步通过{@link #logicalMappings}属性映射,在这种
	 * 情况下,它只能被视为一个逻辑名称。
	 * 
	 * 
	 * @param logicalMappings a Properties object mapping logical method names to real
	 * method names
	 */
	public void setLogicalMappings(Properties logicalMappings) {
		this.logicalMappings = logicalMappings;
	}

	/**
	 * Set the name of the default handler method that should be
	 * used when no parameter was found in the request
	 * <p>
	 * 指定一组可选的逻辑方法名称映射对于两种分辨率策略,方法名称最初从视图层进入。
	 * 如果需要将其视为"逻辑"方法名称,并映射到"真实"方法名称,则该属性实例中的名称/值对应该被添加到此属性实例在此映射中找不到任何方法名称将被认为已经是真正的方法名称<p>请注意,在不匹配的情况下,{@link# defaultMethodName}
	 * 属性,如果可用,该方法名称被认为已经是真正的方法名称,并且不通过逻辑映射运行。
	 * 指定一组可选的逻辑方法名称映射对于两种分辨率策略,方法名称最初从视图层进入。
	 */
	public void setDefaultMethodName(String defaultMethodName) {
		if (defaultMethodName != null) {
			Assert.hasText(defaultMethodName, "'defaultMethodName' must not be empty");
		}
		this.defaultMethodName = defaultMethodName;
	}


	@Override
	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String methodName = null;

		// Check parameter names where the very existence of each parameter
		// means that a method of the same name should be invoked, if any.
		if (this.methodParamNames != null) {
			for (String candidate : this.methodParamNames) {
				if (WebUtils.hasSubmitParameter(request, candidate)) {
					methodName = candidate;
					if (logger.isDebugEnabled()) {
						logger.debug("Determined handler method '" + methodName +
								"' based on existence of explicit request parameter of same name");
					}
					break;
				}
			}
		}

		// Check parameter whose value identifies the method to invoke, if any.
		if (methodName == null && this.paramName != null) {
			methodName = request.getParameter(this.paramName);
			if (methodName != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Determined handler method '" + methodName +
							"' based on value of request parameter '" + this.paramName + "'");
				}
			}
		}

		if (methodName != null && this.logicalMappings != null) {
			// Resolve logical name into real method name, if appropriate.
			String originalName = methodName;
			methodName = this.logicalMappings.getProperty(methodName, methodName);
			if (logger.isDebugEnabled()) {
				logger.debug("Resolved method name '" + originalName + "' to handler method '" + methodName + "'");
			}
		}

		if (methodName != null && !StringUtils.hasText(methodName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Method name '" + methodName + "' is empty: treating it as no method name found");
			}
			methodName = null;
		}

		if (methodName == null) {
			if (this.defaultMethodName != null) {
				// No specific method resolved: use default method.
				methodName = this.defaultMethodName;
				if (logger.isDebugEnabled()) {
					logger.debug("Falling back to default handler method '" + this.defaultMethodName + "'");
				}
			}
			else {
				// If resolution failed completely, throw an exception.
				throw new NoSuchRequestHandlingMethodException(request);
			}
		}

		return methodName;
	}

}
