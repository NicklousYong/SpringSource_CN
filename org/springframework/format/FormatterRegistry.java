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

import org.springframework.core.convert.converter.ConverterRegistry;

/**
 * A registry of field formatting logic.
 *
 * <p>
 *  字段格式化逻辑的注册表
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface FormatterRegistry extends ConverterRegistry {

	/**
	 * Adds a Formatter to format fields of a specific type.
	 * The field type is implied by the parameterized Formatter instance.
	 * <p>
	 *  添加格式化器以格式化特定类型的字段字段类型由参数化的Formatter实例暗示
	 * 
	 * 
	 * @param formatter the formatter to add
	 * @see #addFormatterForFieldType(Class, Formatter)
	 * @since 3.1
	 */
	void addFormatter(Formatter<?> formatter);

	/**
	 * Adds a Formatter to format fields of the given type.
	 * <p>On print, if the Formatter's type T is declared and {@code fieldType} is not assignable to T,
	 * a coercion to T will be attempted before delegating to {@code formatter} to print a field value.
	 * On parse, if the parsed object returned by {@code formatter} is not assignable to the runtime field type,
	 * a coercion to the field type will be attempted before returning the parsed field value.
	 * <p>
	 * 添加一个格式化器来格式化给定类型的字段<p>在打印中,如果Formatter的类型T被声明,并且{@code fieldType}不能分配给T,则在委托给{@code格式化程序}之前,将尝试对T进行胁迫
	 * 打印字段值在解析中,如果{@code formatter}返回的解析对象不能分配给运行时字段类型,则在返回解析的字段值之前将尝试对字段类型的强制。
	 * 
	 * 
	 * @param fieldType the field type to format
	 * @param formatter the formatter to add
	 */
	void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter);

	/**
	 * Adds a Printer/Parser pair to format fields of a specific type.
	 * The formatter will delegate to the specified {@code printer} for printing
	 * and the specified {@code parser} for parsing.
	 * <p>On print, if the Printer's type T is declared and {@code fieldType} is not assignable to T,
	 * a coercion to T will be attempted before delegating to {@code printer} to print a field value.
	 * On parse, if the object returned by the Parser is not assignable to the runtime field type,
	 * a coercion to the field type will be attempted before returning the parsed field value.
	 * <p>
	 * 添加打印机/解析器对来格式化特定格式的字段格式化程序将委托给指定的{@code打印机}进行打印和指定的{@code解析器}进行解析<p>打印时,如果打印机的类型T已声明并且{@code fieldType}
	 * 不能分配给T,在委托给{@code printer}之前,将尝试强制到T,以打印字段值在解析中,如果Parser返回的对象不能分配给运行时字段类型在返回解析的字段值之前,会尝试对字段类型的强制。
	 * 
	 * 
	 * @param fieldType the field type to format
	 * @param printer the printing part of the formatter
	 * @param parser the parsing part of the formatter
	 */
	void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser);

	/**
	 * Adds a Formatter to format fields annotated with a specific format annotation.
	 * <p>
	 * 
	 * @param annotationFormatterFactory the annotation formatter factory to add
	 */
	void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory);

}
