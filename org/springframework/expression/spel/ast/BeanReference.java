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

package org.springframework.expression.spel.ast;

import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;

/**
 * Represents a bean reference to a type, for example <tt>@foo</tt> or <tt>@'foo.bar'</tt>.
 * For a FactoryBean the syntax <tt>&foo</tt> can be used to access the factory itself.
 * 
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Andy Clement
 */
public class BeanReference extends SpelNodeImpl {

	private final static String FACTORY_BEAN_PREFIX = "&";
	
	private final String beanName;


	public BeanReference(int pos,String beanName) {
		super(pos);
		this.beanName = beanName;
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		BeanResolver beanResolver = state.getEvaluationContext().getBeanResolver();
		if (beanResolver == null) {
			throw new SpelEvaluationException(
					getStartPosition(), SpelMessage.NO_BEAN_RESOLVER_REGISTERED, this.beanName);
		}

		try {
			return new TypedValue(beanResolver.resolve(state.getEvaluationContext(), this.beanName));
		}
		catch (AccessException ex) {
			throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_BEAN_RESOLUTION,
				this.beanName, ex.getMessage());
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		if (!this.beanName.startsWith(FACTORY_BEAN_PREFIX)) {
			sb.append("@");
		}
		if (!this.beanName.contains(".")) {
			sb.append(this.beanName);
		}
		else {
			sb.append("'").append(this.beanName).append("'");
		}
		return sb.toString();
	}

}
