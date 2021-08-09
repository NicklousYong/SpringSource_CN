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

package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ObjectUtils;

/**
 * Encapsulates an object and a {@link TypeDescriptor} that describes it.
 * The type descriptor can contain generic declarations that would not
 * be accessible through a simple {@code getClass()} call on the object.
 *
 * <p>
 *  封装一个描述它的对象和一个{@link TypeDescriptor}类型描述符可以包含通过对对象的简单{@code getClass()}调用无法访问的通用声明
 * 
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class TypedValue {

	public static final TypedValue NULL = new TypedValue(null);


	private final Object value;

	private TypeDescriptor typeDescriptor;


	/**
	 * Create a {@link TypedValue} for a simple object. The {@link TypeDescriptor}
	 * is inferred from the object, so no generic declarations are preserved.
	 * <p>
	 * 为一个简单的对象创建一个{@link TypedValue}从对象推断出{@link TypeDescriptor},所以不会保留通用的声明
	 * 
	 * 
	 * @param value the object value
	 */
	public TypedValue(Object value) {
		this.value = value;
		this.typeDescriptor = null;  // initialized when/if requested
	}

	/**
	 * Create a {@link TypedValue} for a particular value with a particular
	 * {@link TypeDescriptor} which may contain additional generic declarations.
	 * <p>
	 *  使用特定的{@link TypeDescriptor}为特定值创建一个{@link TypedValue},它可能包含其他通用声明
	 * 
	 * @param value the object value
	 * @param typeDescriptor a type descriptor describing the type of the value
	 */
	public TypedValue(Object value, TypeDescriptor typeDescriptor) {
		this.value = value;
		this.typeDescriptor = typeDescriptor;
	}


	public Object getValue() {
		return this.value;
	}

	public TypeDescriptor getTypeDescriptor() {
		if (this.typeDescriptor == null && this.value != null) {
			this.typeDescriptor = TypeDescriptor.forObject(this.value);
		}
		return this.typeDescriptor;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TypedValue)) {
			return false;
		}
		TypedValue otherTv = (TypedValue) other;
		// Avoid TypeDescriptor initialization if not necessary
		return (ObjectUtils.nullSafeEquals(this.value, otherTv.value) &&
				((this.typeDescriptor == null && otherTv.typeDescriptor == null) ||
						ObjectUtils.nullSafeEquals(getTypeDescriptor(), otherTv.getTypeDescriptor())));
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.value);
	}

	@Override
	public String toString() {
		return "TypedValue: '" + this.value + "' of [" + getTypeDescriptor() + "]";
	}

}
