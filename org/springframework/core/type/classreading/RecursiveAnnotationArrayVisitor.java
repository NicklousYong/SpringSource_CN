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

package org.springframework.core.type.classreading;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ObjectUtils;

/**
/* <p>
/* 
/*  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
/* 
/* 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1.1
 */
class RecursiveAnnotationArrayVisitor extends AbstractRecursiveAnnotationVisitor {

	private final String attributeName;

	private final List<AnnotationAttributes> allNestedAttributes = new ArrayList<AnnotationAttributes>();


	public RecursiveAnnotationArrayVisitor(
			String attributeName, AnnotationAttributes attributes, ClassLoader classLoader) {

		super(classLoader, attributes);
		this.attributeName = attributeName;
	}


	@Override
	public void visit(String attributeName, Object attributeValue) {
		Object newValue = attributeValue;
		Object existingValue = this.attributes.get(this.attributeName);
		if (existingValue != null) {
			newValue = ObjectUtils.addObjectToArray((Object[]) existingValue, newValue);
		}
		else {
			Class<?> arrayClass = newValue.getClass();
			if (Enum.class.isAssignableFrom(arrayClass)) {
				while (arrayClass.getSuperclass() != null && !arrayClass.isEnum()) {
					arrayClass = arrayClass.getSuperclass();
				}
			}
			Object[] newArray = (Object[]) Array.newInstance(arrayClass, 1);
			newArray[0] = newValue;
			newValue = newArray;
		}
		this.attributes.put(this.attributeName, newValue);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
		String annotationType = Type.getType(asmTypeDescriptor).getClassName();
		AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
		this.allNestedAttributes.add(nestedAttributes);
		return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
	}

	@Override
	public void visitEnd() {
		if (!this.allNestedAttributes.isEmpty()) {
			this.attributes.put(this.attributeName,
					this.allNestedAttributes.toArray(new AnnotationAttributes[this.allNestedAttributes.size()]));
		}
	}

}
