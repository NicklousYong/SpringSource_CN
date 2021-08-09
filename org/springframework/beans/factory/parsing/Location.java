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

package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Class that models an arbitrary location in a {@link Resource resource}.
 *
 * <p>Typically used to track the location of problematic or erroneous
 * metadata in XML configuration files. For example, a
 * {@link #getSource() source} location might be 'The bean defined on
 * line 76 of beans.properties has an invalid Class'; another source might
 * be the actual DOM Element from a parsed XML {@link org.w3c.dom.Document};
 * or the source object might simply be {@code null}.
 *
 * <p>
 *  在{@link资源资源}中建模任意位置的类
 * 
 * <p>通常用于跟踪XML配置文件中有问题或错误的元数据的位置例如,{@link #getSource()source}位置可能是"beansproperties第76行定义的bean具有无效的类";另一
 * 个来源可能是解析的XML {@link orgw3cdomDocument}中的实际DOM元素;或源对象可能只是{@code null}。
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 */
public class Location {

	private final Resource resource;

	private final Object source;


	/**
	 * Create a new instance of the {@link Location} class.
	 * <p>
	 *  创建{@link位置}类的新实例
	 * 
	 * 
	 * @param resource the resource with which this location is associated
	 */
	public Location(Resource resource) {
		this(resource, null);
	}

	/**
	 * Create a new instance of the {@link Location} class.
	 * <p>
	 *  创建{@link位置}类的新实例
	 * 
	 * 
	 * @param resource the resource with which this location is associated
	 * @param source the actual location within the associated resource
	 * (may be {@code null})
	 */
	public Location(Resource resource, Object source) {
		Assert.notNull(resource, "Resource must not be null");
		this.resource = resource;
		this.source = source;
	}


	/**
	 * Get the resource with which this location is associated.
	 * <p>
	 *  获取与此位置相关联的资源
	 * 
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Get the actual location within the associated {@link #getResource() resource}
	 * (may be {@code null}).
	 * <p>See the {@link Location class level javadoc for this class} for examples
	 * of what the actual type of the returned object may be.
	 * <p>
	 *  获取关联的{@link #getResource()资源中的实际位置}(可能是{@code null})<p>有关实际类型的实例,请参阅{@link Location class javadoc for this class}
	 * 返回的对象可能是。
	 */
	public Object getSource() {
		return this.source;
	}

}
