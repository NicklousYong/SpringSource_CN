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

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that parameterizes the MultiActionController class
 * using the <b>Strategy</b> GoF Design pattern, allowing
 * the mapping from incoming request to handler method name
 * to be varied without affecting other application code.
 *
 * <p>Illustrates how delegation can be more flexible than subclassing.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @see MultiActionController#setMethodNameResolver
 * @deprecated as of 4.3, in favor of annotation-driven handler methods
 */
@Deprecated
public interface MethodNameResolver {

	/**
	 * Return a method name that can handle this request. Such
	 * mappings are typically, but not necessarily, based on URL.
	 * <p>
	 * 使用<b>策略</b> GoF设计模式参数化MultiActionController类的接口,允许从传入请求到处理程序方法名称的映射变化而不影响其他应用程序代码
	 * 
	 *  <p>说明委托如何比子类更灵活
	 * 
	 * 
	 * @param request current HTTP request
	 * @return a method name that can handle this request.
	 * Never returns {@code null}; throws exception if not resolvable.
	 * @throws NoSuchRequestHandlingMethodException if no handler method
	 * can be found for the given request
	 */
	String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException;

}
