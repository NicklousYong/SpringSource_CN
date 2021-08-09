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

package org.springframework.expression.spel.support;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;

/**
/* <p>
/* 
/*  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
/* 
/* 
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardOperatorOverloader implements OperatorOverloader {

	@Override
	public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand)
			throws EvaluationException {
		return false;
	}

	@Override
	public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
		throw new EvaluationException("No operation overloaded by default");
	}

}
