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

package org.springframework.web.method.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.InvocableHandlerMethod;

/**
 * Adds initialization to a WebDataBinder via {@code @InitBinder} methods.
 *
 * <p>
 *  通过{@code @InitBinder}方法将初始化添加到WebDataBinder
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class InitBinderDataBinderFactory extends DefaultDataBinderFactory {

	private final List<InvocableHandlerMethod> binderMethods;

	/**
	 * Create a new instance.
	 * <p>
	 *  创建一个新的实例
	 * 
	 * 
	 * @param binderMethods {@code @InitBinder} methods, or {@code null}
	 * @param initializer for global data binder intialization
	 */
	public InitBinderDataBinderFactory(List<InvocableHandlerMethod> binderMethods, WebBindingInitializer initializer) {
		super(initializer);
		this.binderMethods = (binderMethods != null) ? binderMethods : new ArrayList<InvocableHandlerMethod>();
	}

	/**
	 * Initialize a WebDataBinder with {@code @InitBinder} methods.
	 * If the {@code @InitBinder} annotation specifies attributes names, it is
	 * invoked only if the names include the target object name.
	 * <p>
	 * 使用{@code @InitBinder}方法初始化WebDataBinder如果{@code @InitBinder}注释指定属性名称,则仅当名称包含目标对象名称时才调用它
	 * 
	 * 
	 * @throws Exception if one of the invoked @{@link InitBinder} methods fail.
	 */
	@Override
	public void initBinder(WebDataBinder binder, NativeWebRequest request) throws Exception {
		for (InvocableHandlerMethod binderMethod : this.binderMethods) {
			if (isBinderMethodApplicable(binderMethod, binder)) {
				Object returnValue = binderMethod.invokeForRequest(request, null, binder);
				if (returnValue != null) {
					throw new IllegalStateException("@InitBinder methods should return void: " + binderMethod);
				}
			}
		}
	}

	/**
	 * Return {@code true} if the given {@code @InitBinder} method should be
	 * invoked to initialize the given WebDataBinder.
	 * <p>The default implementation checks if target object name is included
	 * in the attribute names specified in the {@code @InitBinder} annotation.
	 * <p>
	 *  如果应该调用给定的{@code @InitBinder}方法来初始化给定的WebDataBinder <p>,返回{@code true}。
	 * 默认实现检查目标对象名称是否包含在{@code @InitBinder}中指定的属性名称中注解。
	 */
	protected boolean isBinderMethodApplicable(HandlerMethod initBinderMethod, WebDataBinder binder) {
		InitBinder annot = initBinderMethod.getMethodAnnotation(InitBinder.class);
		Collection<String> names = Arrays.asList(annot.value());
		return (names.size() == 0 || names.contains(binder.getObjectName()));
	}

}
