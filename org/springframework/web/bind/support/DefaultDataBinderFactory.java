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

package org.springframework.web.bind.support;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Create a {@link WebRequestDataBinder} instance and initialize it with a
 * {@link WebBindingInitializer}.
 *
 * <p>
 *  创建一个{@link WebRequestDataBinder}实例,并使用{@link WebBindingInitializer}进行初始化
 * 
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class DefaultDataBinderFactory implements WebDataBinderFactory {

	private final WebBindingInitializer initializer;


	/**
	 * Create a new {@code DefaultDataBinderFactory} instance.
	 * <p>
	 *  创建一个新的{@code DefaultDataBinderFactory}实例
	 * 
	 * 
	 * @param initializer for global data binder initialization
	 * (or {@code null} if none)
	 */
	public DefaultDataBinderFactory(WebBindingInitializer initializer) {
		this.initializer = initializer;
	}


	/**
	 * Create a new {@link WebDataBinder} for the given target object and
	 * initialize it through a {@link WebBindingInitializer}.
	 * <p>
	 * 为给定的目标对象创建一个新的{@link WebDataBinder},并通过{@link WebBindingInitializer}初始化它
	 * 
	 * 
	 * @throws Exception in case of invalid state or arguments
	 */
	@Override
	public final WebDataBinder createBinder(NativeWebRequest webRequest, Object target, String objectName)
			throws Exception {

		WebDataBinder dataBinder = createBinderInstance(target, objectName, webRequest);
		if (this.initializer != null) {
			this.initializer.initBinder(dataBinder, webRequest);
		}
		initBinder(dataBinder, webRequest);
		return dataBinder;
	}

	/**
	 * Extension point to create the WebDataBinder instance.
	 * By default this is {@code WebRequestDataBinder}.
	 * <p>
	 *  扩展点创建WebDataBinder实例默认情况下是{@code WebRequestDataBinder}
	 * 
	 * 
	 * @param target the binding target or {@code null} for type conversion only
	 * @param objectName the binding target object name
	 * @param webRequest the current request
	 * @throws Exception in case of invalid state or arguments
	 */
	protected WebDataBinder createBinderInstance(Object target, String objectName, NativeWebRequest webRequest)
			throws Exception {

		return new WebRequestDataBinder(target, objectName);
	}

	/**
	 * Extension point to further initialize the created data binder instance
	 * (e.g. with {@code @InitBinder} methods) after "global" initializaton
	 * via {@link WebBindingInitializer}.
	 * <p>
	 *  扩展点通过{@link WebBindingInitializer}在"全局"初始化之后,进一步初始化创建的数据绑定实例(例如使用{@code @InitBinder}方法)
	 * 
	 * @param dataBinder the data binder instance to customize
	 * @param webRequest the current request
	 * @throws Exception if initialization fails
	 */
	protected void initBinder(WebDataBinder dataBinder, NativeWebRequest webRequest) throws Exception {
	}

}
