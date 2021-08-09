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

package org.springframework.core.io.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Editor for {@link org.springframework.core.io.Resource} arrays, to
 * automatically convert {@code String} location patterns
 * (e.g. {@code "file:C:/my*.txt"} or {@code "classpath*:myfile.txt"})
 * to {@code Resource} array properties. Can also translate a collection
 * or array of location patterns into a merged Resource array.
 *
 * <p>A path may contain {@code ${...}} placeholders, to be
 * resolved as {@link org.springframework.core.env.Environment} properties:
 * e.g. {@code ${user.dir}}. Unresolvable placeholders are ignored by default.
 *
 * <p>Delegates to a {@link ResourcePatternResolver},
 * by default using a {@link PathMatchingResourcePatternResolver}.
 *
 * <p>
 * 编辑{@link orgspringframeworkcoreioResource}数组,自动将{@code String}位置模式(例如{@code"file：C：/ my * txt"}或{@code"classpath *：myfiletxt"}
 * )转换为{@代码资源}数组属性还可以将集合或数组的位置模式转换为合并的资源数组。
 * 
 *  <p>路径可能包含{@code $ {}}占位符,要解析为{@link orgspringframeworkcoreenvEnvironment}属性：例如{@code $ {userdir}}默认情
 * 况下忽略不可解决的占位符。
 * 
 *  <p>委派给{@link ResourcePatternResolver},默认情况下使用{@link PathMatchingResourcePatternResolver}
 * 
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.2
 * @see org.springframework.core.io.Resource
 * @see ResourcePatternResolver
 * @see PathMatchingResourcePatternResolver
 */
public class ResourceArrayPropertyEditor extends PropertyEditorSupport {

	private static final Log logger = LogFactory.getLog(ResourceArrayPropertyEditor.class);

	private final ResourcePatternResolver resourcePatternResolver;

	private PropertyResolver propertyResolver;

	private final boolean ignoreUnresolvablePlaceholders;


	/**
	 * Create a new ResourceArrayPropertyEditor with a default
	 * {@link PathMatchingResourcePatternResolver} and {@link StandardEnvironment}.
	 * <p>
	 *  使用默认的{@link PathMatchingResourcePatternResolver}和{@link StandardEnvironment}创建一个新的ResourceArrayPrope
	 * rtyEditor。
	 * 
	 * 
	 * @see PathMatchingResourcePatternResolver
	 * @see Environment
	 */
	public ResourceArrayPropertyEditor() {
		this(new PathMatchingResourcePatternResolver(), null, true);
	}

	/**
	 * Create a new ResourceArrayPropertyEditor with the given {@link ResourcePatternResolver}
	 * and {@link PropertyResolver} (typically an {@link Environment}).
	 * <p>
	 * 使用给定的{@link ResourcePatternResolver}和{@link PropertyResolver}(通常是{@link Environment})创建一个新的ResourceAr
	 * rayPropertyEditor。
	 * 
	 * 
	 * @param resourcePatternResolver the ResourcePatternResolver to use
	 * @param propertyResolver the PropertyResolver to use
	 */
	public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver, PropertyResolver propertyResolver) {
		this(resourcePatternResolver, propertyResolver, true);
	}

	/**
	 * Create a new ResourceArrayPropertyEditor with the given {@link ResourcePatternResolver}
	 * and {@link PropertyResolver} (typically an {@link Environment}).
	 * <p>
	 *  使用给定的{@link ResourcePatternResolver}和{@link PropertyResolver}(通常是{@link Environment})创建一个新的ResourceA
	 * rrayPropertyEditor。
	 * 
	 * 
	 * @param resourcePatternResolver the ResourcePatternResolver to use
	 * @param propertyResolver the PropertyResolver to use
	 * @param ignoreUnresolvablePlaceholders whether to ignore unresolvable placeholders
	 * if no corresponding system property could be found
	 */
	public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver,
			PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {

		Assert.notNull(resourcePatternResolver, "ResourcePatternResolver must not be null");
		this.resourcePatternResolver = resourcePatternResolver;
		this.propertyResolver = propertyResolver;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}


	/**
	 * Treat the given text as a location pattern and convert it to a Resource array.
	 * <p>
	 *  将给定的文本视为位置模式并将其转换为资源数组
	 * 
	 */
	@Override
	public void setAsText(String text) {
		String pattern = resolvePath(text).trim();
		try {
			setValue(this.resourcePatternResolver.getResources(pattern));
		}
		catch (IOException ex) {
			throw new IllegalArgumentException(
					"Could not resolve resource location pattern [" + pattern + "]: " + ex.getMessage());
		}
	}

	/**
	 * Treat the given value as a collection or array and convert it to a Resource array.
	 * Considers String elements as location patterns and takes Resource elements as-is.
	 * <p>
	 *  将给定值视为集合或数组,并将其转换为资源数组将字符串元素作为位置模式进行考虑,并将资源元素按原样
	 * 
	 */
	@Override
	public void setValue(Object value) throws IllegalArgumentException {
		if (value instanceof Collection || (value instanceof Object[] && !(value instanceof Resource[]))) {
			Collection<?> input = (value instanceof Collection ? (Collection<?>) value : Arrays.asList((Object[]) value));
			List<Resource> merged = new ArrayList<Resource>();
			for (Object element : input) {
				if (element instanceof String) {
					// A location pattern: resolve it into a Resource array.
					// Might point to a single resource or to multiple resources.
					String pattern = resolvePath((String) element).trim();
					try {
						Resource[] resources = this.resourcePatternResolver.getResources(pattern);
						for (Resource resource : resources) {
							if (!merged.contains(resource)) {
								merged.add(resource);
							}
						}
					}
					catch (IOException ex) {
						// ignore - might be an unresolved placeholder or non-existing base directory
						if (logger.isDebugEnabled()) {
							logger.debug("Could not retrieve resources for pattern '" + pattern + "'", ex);
						}
					}
				}
				else if (element instanceof Resource) {
					// A Resource object: add it to the result.
					Resource resource = (Resource) element;
					if (!merged.contains(resource)) {
						merged.add(resource);
					}
				}
				else {
					throw new IllegalArgumentException("Cannot convert element [" + element + "] to [" +
							Resource.class.getName() + "]: only location String and Resource object supported");
				}
			}
			super.setValue(merged.toArray(new Resource[merged.size()]));
		}

		else {
			// An arbitrary value: probably a String or a Resource array.
			// setAsText will be called for a String; a Resource array will be used as-is.
			super.setValue(value);
		}
	}

	/**
	 * Resolve the given path, replacing placeholders with
	 * corresponding system property values if necessary.
	 * <p>
	 *  解决给定的路径,如有必要,用相应的系统属性值替换占位符
	 * 
	 * @param path the original file path
	 * @return the resolved file path
	 * @see PropertyResolver#resolvePlaceholders
	 * @see PropertyResolver#resolveRequiredPlaceholders(String)
	 */
	protected String resolvePath(String path) {
		if (this.propertyResolver == null) {
			this.propertyResolver = new StandardEnvironment();
		}
		return (this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) :
				this.propertyResolver.resolveRequiredPlaceholders(path));
	}

}
