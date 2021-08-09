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

package org.springframework.web.bind;

/**
 * {@link ServletRequestBindingException} subclass that indicates a missing parameter.
 *
 * <p>
 *  {@link ServletRequestBindingException}子类,表示缺少参数
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.0.2
 */
@SuppressWarnings("serial")
public class MissingServletRequestParameterException extends ServletRequestBindingException {

	private final String parameterName;

	private final String parameterType;


	/**
	 * Constructor for MissingServletRequestParameterException.
	 * <p>
	 *  MissingServletRequestParameterException的构造方法
	 * 
	 * 
	 * @param parameterName the name of the missing parameter
	 * @param parameterType the expected type of the missing parameter
	 */
	public MissingServletRequestParameterException(String parameterName, String parameterType) {
		super("");
		this.parameterName = parameterName;
		this.parameterType = parameterType;
	}


	@Override
	public String getMessage() {
		return "Required " + this.parameterType + " parameter '" + this.parameterName + "' is not present";
	}

	/**
	 * Return the name of the offending parameter.
	 * <p>
	 *  返回违规参数的名称
	 * 
	 */
	public final String getParameterName() {
		return this.parameterName;
	}

	/**
	 * Return the expected type of the offending parameter.
	 * <p>
	 * 返回违规参数的预期类型
	 */
	public final String getParameterType() {
		return this.parameterType;
	}

}
