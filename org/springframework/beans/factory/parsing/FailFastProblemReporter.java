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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple {@link ProblemReporter} implementation that exhibits fail-fast
 * behavior when errors are encountered.
 *
 * <p>The first error encountered results in a {@link BeanDefinitionParsingException}
 * being thrown.
 *
 * <p>Warnings are written to
 * {@link #setLogger(org.apache.commons.logging.Log) the log} for this class.
 *
 * <p>
 *  简单的{@link ProblemReporter}实现,遇到错误时表现出故障快速的行为
 * 
 *  <p>遇到的第一个错误导致抛出{@link BeanDefinitionParsingException}
 * 
 * <p>警告已写入此类的{@link #setLogger(orgapachecommonsloggingLog)log}
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 */
public class FailFastProblemReporter implements ProblemReporter {

	private Log logger = LogFactory.getLog(getClass());


	/**
	 * Set the {@link Log logger} that is to be used to report warnings.
	 * <p>If set to {@code null} then a default {@link Log logger} set to
	 * the name of the instance class will be used.
	 * <p>
	 *  设置要用于报告警告的{@link Log logger} <p>如果设置为{@code null},则将使用设置为实例类名称的默认{@link Log logger}
	 * 
	 * 
	 * @param logger the {@link Log logger} that is to be used to report warnings
	 */
	public void setLogger(Log logger) {
		this.logger = (logger != null ? logger : LogFactory.getLog(getClass()));
	}


	/**
	 * Throws a {@link BeanDefinitionParsingException} detailing the error
	 * that has occurred.
	 * <p>
	 *  引发{@link BeanDefinitionParsingException},详细说明发生的错误
	 * 
	 * 
	 * @param problem the source of the error
	 */
	@Override
	public void fatal(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	/**
	 * Throws a {@link BeanDefinitionParsingException} detailing the error
	 * that has occurred.
	 * <p>
	 *  引发{@link BeanDefinitionParsingException},详细说明发生的错误
	 * 
	 * 
	 * @param problem the source of the error
	 */
	@Override
	public void error(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	/**
	 * Writes the supplied {@link Problem} to the {@link Log} at {@code WARN} level.
	 * <p>
	 *  在{@code WARN}级别将{@link问题}提交到{@link日志}
	 * 
	 * @param problem the source of the warning
	 */
	@Override
	public void warning(Problem problem) {
		this.logger.warn(problem, problem.getRootCause());
	}

}
