/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.expression;

import java.lang.reflect.Method;
import java.util.List;

/**
 * MethodFilter instances allow SpEL users to fine tune the behaviour of the method
 * resolution process. Method resolution (which translates from a method name in an
 * expression to a real method to invoke) will normally retrieve candidate methods for
 * invocation via a simple call to 'Class.getMethods()' and will choose the first one that
 * is suitable for the input parameters. By registering a MethodFilter the user can
 * receive a callback and change the methods that will be considered suitable.
 *
 * <p>
 * 
 * @author Andy Clement
 * @since 3.0.1
 */
public interface MethodFilter {

	/**
	 * Called by the method resolver to allow the SpEL user to organize the list of
	 * candidate methods that may be invoked. The filter can remove methods that should
	 * not be considered candidates and it may sort the results. The resolver will then
	 * search through the methods as returned from the filter when looking for a suitable
	 * candidate to invoke.
	 * <p>
	 * MethodFilter实例允许SpEL用户微调方法解析过程的行为方法解析(从表达式中的方法名称转换为实际调用方法)通常将通过简单调用"ClassgetMethods()"来检索调用的候选方法, '并且
	 * 将选择适合于输入参数的第一个。
	 * 通过注册一个MethodFilter,用户可以接收回调并更改将被认为合适的方法。
	 * 
	 * 
	 * @param methods the full list of methods the resolver was going to choose from
	 * @return a possible subset of input methods that may be sorted by order of relevance
	 */
	List<Method> filter(List<Method> methods);

}
