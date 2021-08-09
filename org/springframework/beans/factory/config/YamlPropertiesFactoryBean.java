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

package org.springframework.beans.factory.config;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Factory for Java Properties that reads from a YAML source. YAML is a nice
 * human-readable format for configuration, and it has some useful hierarchical
 * properties. It's more or less a superset of JSON, so it has a lot of similar
 * features. The Properties created by this factory have nested paths for
 * hierarchical objects, so for instance this YAML
 *
 * <pre class="code">
 * environments:
 *   dev:
 *     url: http://dev.bar.com
 *     name: Developer Setup
 *   prod:
 *     url: http://foo.bar.com
 *     name: My Cool App
 * </pre>
 *
 * is transformed into these Properties:
 *
 * <pre class="code">
 * environments.dev.url=http://dev.bar.com
 * environments.dev.name=Developer Setup
 * environments.prod.url=http://foo.bar.com
 * environments.prod.name=My Cool App
 * </pre>
 *
 * Lists are split as property keys with <code>[]</code> dereferencers, for
 * example this YAML:
 *
 * <pre class="code">
 * servers:
 * - dev.bar.com
 * - foo.bar.com
 * </pre>
 *
 * becomes Java Properties like this:
 *
 * <pre class="code">
 * servers[0]=dev.bar.com
 * servers[1]=foo.bar.com
 * </pre>
 *
 * <p>
 * 从YAML源读取的Java属性的工厂YAML是一个很好的人机可读格式的配置,它具有一些有用的层次属性它或多或少是JSON的超集,所以它有很多类似的功能由此创建的属性工厂有分层对象的嵌套路径,所以例如这个
 * YAML。
 * 
 * <pre class="code">
 *  环境：dev：url：http：// devbarcom name：Developer Setup prod：url：http：// foobarcom name：My Cool App
 * </pre>
 * 
 *  被转换成这些属性：
 * 
 * <pre class="code">
 *  environmentsdevurl = http：// devbarcom environmentsdevname = Developer Setup environmentsprodurl = h
 * ttp：// foobarcom environmentsprodname = My Cool App。
 * </pre>
 * 
 *  列表被拆分为具有<code> [] </code>取消引用的属性键,例如此YAML：
 * 
 * <pre class="code">
 * 
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 4.1
 */
public class YamlPropertiesFactoryBean extends YamlProcessor implements FactoryBean<Properties>, InitializingBean {

	private boolean singleton = true;

	private Properties properties;


	/**
	 * Set if a singleton should be created, or a new object on each request
	 * otherwise. Default is {@code true} (a singleton).
	 * <p>
	 *  服务器： -  devbarcom  -  foobarcom
	 * </pre>
	 * 
	 * 成为Java属性,如下所示：
	 * 
	 * <pre class="code">
	 *  servers [0] = devbarcom servers [1] = foobarcom
	 * </pre>
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public boolean isSingleton() {
		return this.singleton;
	}

	@Override
	public void afterPropertiesSet() {
		if (isSingleton()) {
			this.properties = createProperties();
		}
	}

	@Override
	public Properties getObject() {
		return (this.properties != null ? this.properties : createProperties());
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}


	/**
	 * Template method that subclasses may override to construct the object
	 * returned by this factory. The default implementation returns a
	 * properties with the content of all resources.
	 * <p>Invoked lazily the first time {@link #getObject()} is invoked in
	 * case of a shared singleton; else, on each {@link #getObject()} call.
	 * <p>
	 * 
	 * 
	 * @return the object returned by this factory
	 * @see #process(MatchCallback) ()
	 */
	protected Properties createProperties() {
		final Properties result = new Properties();
		process(new MatchCallback() {
			@Override
			public void process(Properties properties, Map<String, Object> map) {
				result.putAll(properties);
			}
		});
		return result;
	}

}
