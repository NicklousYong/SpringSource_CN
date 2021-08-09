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

package org.springframework.web.servlet.config.annotation;

import java.util.Map;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Assist with configuring a {@link org.springframework.web.servlet.view.UrlBasedViewResolver}.
 *
 * <p>
 *  协助配置{@link orgspringframeworkwebservletviewUrlBasedViewResolver}
 * 
 * 
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class UrlBasedViewResolverRegistration {

	protected final UrlBasedViewResolver viewResolver;


	public UrlBasedViewResolverRegistration(UrlBasedViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}


	protected UrlBasedViewResolver getViewResolver() {
		return this.viewResolver;
	}

	/**
	 * Set the prefix that gets prepended to view names when building a URL.
	 * <p>
	 *  设置前缀,以便在构建URL时查看名称
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setPrefix
	 */
	public UrlBasedViewResolverRegistration prefix(String prefix) {
		this.viewResolver.setPrefix(prefix);
		return this;
	}

	/**
	 * Set the suffix that gets appended to view names when building a URL.
	 * <p>
	 * 设置附加后缀以在构建URL时查看名称
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setSuffix
	 */
	public UrlBasedViewResolverRegistration suffix(String suffix) {
		this.viewResolver.setSuffix(suffix);
		return this;
	}

	/**
	 * Set the view class that should be used to create views.
	 * <p>
	 *  设置应用于创建视图的视图类
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setViewClass
	 */
	public UrlBasedViewResolverRegistration viewClass(Class<?> viewClass) {
		this.viewResolver.setViewClass(viewClass);
		return this;
	}

	/**
	 * Set the view names (or name patterns) that can be handled by this view
	 * resolver. View names can contain simple wildcards such that 'my*', '*Report'
	 * and '*Repo*' will all match the view name 'myReport'.
	 * <p>
	 *  设置此视图解析器可以处理的视图名称(或名称模式)查看名称可以包含简单的通配符,以便'my *','* Report'和'* Repo *'都将与视图名称'myReport'匹配
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setViewNames
	 */
	public UrlBasedViewResolverRegistration viewNames(String... viewNames) {
		this.viewResolver.setViewNames(viewNames);
		return this;
	}

	/**
	 * Set static attributes to be added to the model of every request for all
	 * views resolved by this view resolver. This allows for setting any kind of
	 * attribute values, for example bean references.
	 * <p>
	 *  设置要添加到该视图解析器解析的所有视图的每个请求的模型中的静态属性这允许设置任何种类的属性值,例如bean引用
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setAttributesMap
	 */
	public UrlBasedViewResolverRegistration attributes(Map<String, ?> attributes) {
		this.viewResolver.setAttributesMap(attributes);
		return this;
	}

	/**
	 * Specify the maximum number of entries for the view cache.
	 * Default is 1024.
	 * <p>
	 *  指定视图缓存的最大条目数默认值为1024
	 * 
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setCache(boolean)
	 */
	public UrlBasedViewResolverRegistration cacheLimit(int cacheLimit) {
		this.viewResolver.setCacheLimit(cacheLimit);
		return this;
	}

	/**
	 * Enable or disable caching.
	 * <p>This is equivalent to setting the {@link #cacheLimit "cacheLimit"}
	 * property to the default limit (1024) or to 0, respectively.
	 * <p>Default is "true": caching is enabled.
	 * Disable this only for debugging and development.
	 * <p>
	 * 启用或禁用缓存<p>这等效于将{@link #cacheLimit"cacheLimit"}属性设置为默认限制(1024)或分别为0 <p>默认值为"true"：启用缓存禁用此选项用于调试和开发
	 * 
	 * @see org.springframework.web.servlet.view.UrlBasedViewResolver#setCache(boolean)
	 */
	public UrlBasedViewResolverRegistration cache(boolean cache) {
		this.viewResolver.setCache(cache);
		return this;
	}

}


