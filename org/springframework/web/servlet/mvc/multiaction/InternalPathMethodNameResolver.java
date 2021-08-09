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

package org.springframework.web.servlet.mvc.multiaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.util.WebUtils;

/**
 * Simple implementation of {@link MethodNameResolver} that maps URL to
 * method name. Although this is the default implementation used by the
 * {@link MultiActionController} class (because it requires no configuration),
 * it's bit naive for most applications. In particular, we don't usually
 * want to tie URL to implementation methods.
 *
 * <p>Maps the resource name after the last slash, ignoring an extension.
 * E.g. "/foo/bar/baz.html" to "baz", assuming a "/foo/bar/baz.html"
 * controller mapping to the corresponding MultiActionController handler.
 * method. Doesn't support wildcards.
 *
 * <p>
 * 将URL映射到方法名称的{@link MethodNameResolver}的简单实现虽然这是{@link MultiActionController}类使用的默认实现(因为它不需要配置),但是对于大多
 * 数应用程序来说,这是非常天真的,特别是我们不通常希望将URL绑定到实现方法。
 * 
 *  假设"/ foo / bar / bazhtml"控制器映射到相应的MultiActionController处理程序方法时,将资源名称映射到最后一条斜杠之后,忽略扩展名Eg"/ foo / bar 
 * / bazhtml"至"baz"支持通配符。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public class InternalPathMethodNameResolver extends AbstractUrlMethodNameResolver {

	private String prefix = "";

	private String suffix = "";

	/** Request URL path String --> method name String */
	private final Map<String, String> methodNameCache = new ConcurrentHashMap<String, String>(16);


	/**
	 * Specify a common prefix for handler method names.
	 * Will be prepended to the internal path found in the URL:
	 * e.g. internal path "baz", prefix "my" -> method name "mybaz".
	 * <p>
	 *  指定处理程序方法名称的公共前缀将前缀到URL中的内部路径：例如内部路径"baz",前缀"my" - >方法名称"mybaz"
	 * 
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the common prefix for handler method names.
	 * <p>
	 *  返回处理程序方法名称的公共前缀
	 * 
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	/**
	 * Specify a common suffix for handler method names.
	 * Will be appended to the internal path found in the URL:
	 * e.g. internal path "baz", suffix "Handler" -> method name "bazHandler".
	 * <p>
	 * 指定处理程序方法名称的常用后缀将附加到URL中找到的内部路径：例如内部路径"baz",后缀"Handler" - >方法名称"bazHandler"
	 * 
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the common suffix for handler method names.
	 * <p>
	 *  返回处理程序方法名称的常用后缀
	 * 
	 */
	protected String getSuffix() {
		return this.suffix;
	}


	/**
	 * Extracts the method name indicated by the URL path.
	 * <p>
	 *  提取URL路径指示的方法名称
	 * 
	 * 
	 * @see #extractHandlerMethodNameFromUrlPath
	 * @see #postProcessHandlerMethodName
	 */
	@Override
	protected String getHandlerMethodNameForUrlPath(String urlPath) {
		String methodName = this.methodNameCache.get(urlPath);
		if (methodName == null) {
			methodName = extractHandlerMethodNameFromUrlPath(urlPath);
			methodName = postProcessHandlerMethodName(methodName);
			this.methodNameCache.put(urlPath, methodName);
		}
		return methodName;
	}

	/**
	 * Extract the handler method name from the given request URI.
	 * Delegates to {@code WebUtils.extractFilenameFromUrlPath(String)}.
	 * <p>
	 *  从给定的请求URI中提取处理程序方法名称{@code WebUtilsextractFilenameFromUrlPath(String)}的代理
	 * 
	 * 
	 * @param uri the request URI (e.g. "/index.html")
	 * @return the extracted URI filename (e.g. "index")
	 * @see org.springframework.web.util.WebUtils#extractFilenameFromUrlPath
	 */
	protected String extractHandlerMethodNameFromUrlPath(String uri) {
		return WebUtils.extractFilenameFromUrlPath(uri);
	}

	/**
	 * Build the full handler method name based on the given method name
	 * as indicated by the URL path.
	 * <p>The default implementation simply applies prefix and suffix.
	 * This can be overridden, for example, to manipulate upper case
	 * / lower case, etc.
	 * <p>
	 *  根据URL路径指定的给定方法名称构建完整的处理程序方法名称<p>默认实现仅适用前缀和后缀可以覆盖,例如,操作大写/小写字母等
	 * 
	 * @param methodName the original method name, as indicated by the URL path
	 * @return the full method name to use
	 * @see #getPrefix()
	 * @see #getSuffix()
	 */
	protected String postProcessHandlerMethodName(String methodName) {
		return getPrefix() + methodName + getSuffix();
	}

}
