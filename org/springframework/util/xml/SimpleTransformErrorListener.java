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

package org.springframework.util.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;

/**
 * Simple {@code javax.xml.transform.ErrorListener} implementation:
 * logs warnings using the given Commons Logging logger instance,
 * and rethrows errors to discontinue the XML transformation.
 *
 * <p>
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2
 */
public class SimpleTransformErrorListener implements ErrorListener {

	private final Log logger;


	/**
	 * Create a new SimpleTransformErrorListener for the given
	 * Commons Logging logger instance.
	 * <p>
	 *  简单的{@code javaxxmltransformErrorListener}实现：使用给定的Commons Logging logger实例记录警告,并重新启动错误以停止XML转换
	 * 
	 */
	public SimpleTransformErrorListener(Log logger) {
		this.logger = logger;
	}


	@Override
	public void warning(TransformerException ex) throws TransformerException {
		logger.warn("XSLT transformation warning", ex);
	}

	@Override
	public void error(TransformerException ex) throws TransformerException {
		logger.error("XSLT transformation error", ex);
	}

	@Override
	public void fatalError(TransformerException ex) throws TransformerException {
		throw ex;
	}

}
