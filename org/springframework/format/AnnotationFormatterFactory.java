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

package org.springframework.format;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A factory that creates formatters to format values of fields annotated with a particular
 * {@link Annotation}.
 *
 * <p>For example, a {@code DateTimeFormatAnnotationFormatterFactory} might create a formatter
 * that formats {@code Date} values set on fields annotated with {@code @DateTimeFormat}.
 *
 * <p>
 *  一个工厂,它创建格式化程序来格式化用特定{@link注释}注释的字段的值
 * 
 * 例如,{@code DateTimeFormatAnnotationFormatterFactory}可能会创建一个格式化程序,该格式化程序可以使用{@code @DateTimeFormat}注释的字
 * 段设置{@code Date}值。
 * 
 * 
 * @author Keith Donald
 * @since 3.0
 * @param <A> the annotation type that should trigger formatting
 */
public interface AnnotationFormatterFactory<A extends Annotation> {

	/**
	 * The types of fields that may be annotated with the &lt;A&gt; annotation.
	 * <p>
	 *  可以使用&lt; A&gt;注释的字段的类型注解
	 * 
	 */
	Set<Class<?>> getFieldTypes();

	/**
	 * Get the Printer to print the value of a field of {@code fieldType} annotated with
	 * {@code annotation}.
	 * <p>If the type T the printer accepts is not assignable to {@code fieldType}, a
	 * coercion from {@code fieldType} to T will be attempted before the Printer is invoked.
	 * <p>
	 *  获取打印机打印使用{@code注释} <p>注释的{@code fieldType}字段的值如果打印机接受的类型T不能分配给{@code fieldType},则来自{@code fieldType }
	 * 到T将尝试在打印机被调用之前。
	 * 
	 * 
	 * @param annotation the annotation instance
	 * @param fieldType the type of field that was annotated
	 * @return the printer
	 */
	Printer<?> getPrinter(A annotation, Class<?> fieldType);

	/**
	 * Get the Parser to parse a submitted value for a field of {@code fieldType}
	 * annotated with {@code annotation}.
	 * <p>If the object the parser returns is not assignable to {@code fieldType},
	 * a coercion to {@code fieldType} will be attempted before the field is set.
	 * <p>
	 *  获取解析器以解析{@code注释} <p>注释的{@code fieldType}字段的提交值如果解析器返回的对象不能分配给{@code fieldType},则强制{@code fieldType }
	 * 将在字段设置之前尝试。
	 * 
	 * @param annotation the annotation instance
	 * @param fieldType the type of field that was annotated
	 * @return the parser
	 */
	Parser<?> getParser(A annotation, Class<?> fieldType);

}
