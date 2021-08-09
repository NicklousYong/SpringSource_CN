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

package org.springframework.expression.spel;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;

/**
 * Represents a node in the Ast for a parsed expression.
 *
 * <p>
 *  表示Ast中的一个节点用于解析的表达式
 * 
 * 
 * @author Andy Clement
 * @since 3.0
 */
public interface SpelNode {

	/**
	 * Evaluate the expression node in the context of the supplied expression state
	 * and return the value.
	 * <p>
	 *  在提供的表达式状态的上下文中评估表达式节点并返回值
	 * 
	 * 
	 * @param expressionState the current expression state (includes the context)
	 * @return the value of this node evaluated against the specified state
	 */
	Object getValue(ExpressionState expressionState) throws EvaluationException;

	/**
	 * Evaluate the expression node in the context of the supplied expression state
	 * and return the typed value.
	 * <p>
	 * 在提供的表达式状态的上下文中评估表达式节点,并返回类型值
	 * 
	 * 
	 * @param expressionState the current expression state (includes the context)
	 * @return the type value of this node evaluated against the specified state
	 */
	TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException;

	/**
	 * Determine if this expression node will support a setValue() call.
	 * <p>
	 *  确定此表达式节点是否将支持setValue()调用
	 * 
	 * 
	 * @param expressionState the current expression state (includes the context)
	 * @return true if the expression node will allow setValue()
	 * @throws EvaluationException if something went wrong trying to determine
	 * if the node supports writing
	 */
	boolean isWritable(ExpressionState expressionState) throws EvaluationException;

	/**
	 * Evaluate the expression to a node and then set the new value on that node.
	 * For example, if the expression evaluates to a property reference, then the
	 * property will be set to the new value.
	 * <p>
	 *  评估节点的表达式,然后在该节点上设置新值例如,如果表达式计算为属性引用,则该属性将被设置为新值
	 * 
	 * 
	 * @param expressionState the current expression state (includes the context)
	 * @param newValue the new value
	 * @throws EvaluationException if any problem occurs evaluating the expression or
	 * setting the new value
	 */
	void setValue(ExpressionState expressionState, Object newValue) throws EvaluationException;

	/**
	/* <p>
	/* 
	 * @return the string form of this AST node
	 */
	String toStringAST();

	/**
	/* <p>
	/* 
	 * @return the number of children under this node
	 */
	int getChildCount();

	/**
	 * Helper method that returns a SpelNode rather than an Antlr Tree node.
	 * <p>
	 *  Helper方法返回SpelNode而不是Antlr Tree节点
	 * 
	 * 
	 * @return the child node cast to a SpelNode
	 */
	SpelNode getChild(int index);

	/**
	 * Determine the class of the object passed in, unless it is already a class object.
	 * <p>
	 *  确定传入的对象的类,除非它已经是一个类对象
	 * 
	 * 
	 * @param obj the object that the caller wants the class of
	 * @return the class of the object if it is not already a class object,
	 * or {@code null} if the object is {@code null}
	 */
	Class<?> getObjectClass(Object obj);

	/**
	/* <p>
	/* 
	 * @return the start position of this Ast node in the expression string
	 */
	int getStartPosition();

	/**
	/* <p>
	/* 
	 * @return the end position of this Ast node in the expression string
	 */
	int getEndPosition();

}
