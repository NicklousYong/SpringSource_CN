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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Property editor for {@link Class java.lang.Class}, to enable the direct
 * population of a {@code Class} property without recourse to having to use a
 * String class name property as bridge.
 *
 * <p>Also supports "java.lang.String[]"-style array class names, in contrast to the
 * standard {@link Class#forName(String)} method.
 *
 * <p>
 *  {@link Class javalangClass}的属性编辑器,可以直接使用{@code Class}属性,而不用使用String类名称属性作为桥接
 * 
 * <p>与标准的{@link Class#forName(String)}方法相反,还支持"javalangString []" - 样式数组类名称
 * 
 * 
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 13.05.2003
 * @see Class#forName
 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
 */
public class ClassEditor extends PropertyEditorSupport {

	private final ClassLoader classLoader;


	/**
	 * Create a default ClassEditor, using the thread context ClassLoader.
	 * <p>
	 *  使用线程上下文ClassLoader创建一个默认的ClassEditor
	 * 
	 */
	public ClassEditor() {
		this(null);
	}

	/**
	 * Create a default ClassEditor, using the given ClassLoader.
	 * <p>
	 *  使用给定的ClassLoader创建一个默认的ClassEditor
	 * 
	 * @param classLoader the ClassLoader to use
	 * (or {@code null} for the thread context ClassLoader)
	 */
	public ClassEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			setValue(ClassUtils.resolveClassName(text.trim(), this.classLoader));
		}
		else {
			setValue(null);
		}
	}

	@Override
	public String getAsText() {
		Class<?> clazz = (Class<?>) getValue();
		if (clazz != null) {
			return ClassUtils.getQualifiedName(clazz);
		}
		else {
			return "";
		}
	}

}
