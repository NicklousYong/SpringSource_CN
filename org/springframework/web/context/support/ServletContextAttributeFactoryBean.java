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

package org.springframework.web.context.support;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

/**
 * {@link FactoryBean} that fetches a specific, existing ServletContext attribute.
 * Exposes that ServletContext attribute when used as bean reference,
 * effectively making it available as named Spring bean instance.
 *
 * <p>Intended to link in ServletContext attributes that exist before
 * the startup of the Spring application context. Typically, such
 * attributes will have been put there by third-party web frameworks.
 * In a purely Spring-based web application, no such linking in of
 * ServletContext attributes will be necessary.
 *
 * <p><b>NOTE:</b> As of Spring 3.0, you may also use the "contextAttributes" default
 * bean which is of type Map, and dereference it using an "#{contextAttributes.myKey}"
 * expression to access a specific attribute by name.
 *
 * <p>
 *  获取一个特定的现有ServletContext属性的{@link FactoryBean}当用作bean引用时将该ServletContext属性暴露出来,有效地使其可用作命名的Spring bean
 * 实例。
 * 
 * <p>旨在链接在Spring应用程序上下文启动之前存在的ServletContext属性通常,这些属性将被第三方Web框架放置在纯粹的基于Spring的Web应用程序中,ServletContext属性
 * 中没有这样的链接将是必要的。
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.1.4
 * @see org.springframework.web.context.WebApplicationContext#CONTEXT_ATTRIBUTES_BEAN_NAME
 * @see ServletContextParameterFactoryBean
 */
public class ServletContextAttributeFactoryBean implements FactoryBean<Object>, ServletContextAware {

	private String attributeName;

	private Object attribute;


	/**
	 * Set the name of the ServletContext attribute to expose.
	 * <p>
	 *  <p> <b>注意：</b>从Spring 30起,您还可以使用Map的"contextAttributes"默认bean,并使用"#{contextAttributesmyKey}"表达式取消引用它
	 * 以访问特定属性按名字。
	 * 
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		if (this.attributeName == null) {
			throw new IllegalArgumentException("Property 'attributeName' is required");
		}
		this.attribute = servletContext.getAttribute(this.attributeName);
		if (this.attribute == null) {
			throw new IllegalStateException("No ServletContext attribute '" + this.attributeName + "' found");
		}
	}


	@Override
	public Object getObject() throws Exception {
		return this.attribute;
	}

	@Override
	public Class<?> getObjectType() {
		return (this.attribute != null ? this.attribute.getClass() : null);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
