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

/**
 * SPI interface allowing tools and other external processes to handle errors
 * and warnings reported during bean definition parsing.
 *
 * <p>
 *  SPI接口允许工具和其他外部进程处理在bean定义解析期间报告的错误和警告
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see Problem
 */
public interface ProblemReporter {

	/**
	 * Called when a fatal error is encountered during the parsing process.
	 * <p>Implementations must treat the given problem as fatal,
	 * i.e. they have to eventually raise an exception.
	 * <p>
	 * 在解析过程中遇到致命错误时调用<p>实现必须将给定的问题视为致命的,即他们必须最终引发异常
	 * 
	 * 
	 * @param problem the source of the error (never {@code null})
	 */
	void fatal(Problem problem);

	/**
	 * Called when an error is encountered during the parsing process.
	 * <p>Implementations may choose to treat errors as fatal.
	 * <p>
	 *  在解析过程中遇到错误时调用<p>实现可能会将错误视为致命的
	 * 
	 * 
	 * @param problem the source of the error (never {@code null})
	 */
	void error(Problem problem);

	/**
	 * Called when a warning is raised during the parsing process.
	 * <p>Warnings are <strong>never</strong> considered to be fatal.
	 * <p>
	 *  在解析过程中发出警告时调用<p>警告<strong>永远不会</strong>被认为是致命的
	 * 
	 * @param problem the source of the warning (never {@code null})
	 */
	void warning(Problem problem);

}
