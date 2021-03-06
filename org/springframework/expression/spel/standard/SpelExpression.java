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

package org.springframework.expression.spel.standard;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.CompiledExpression;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * A {@code SpelExpression} represents a parsed (valid) expression that is ready to be
 * evaluated in a specified context. An expression can be evaluated standalone or in a
 * specified context. During expression evaluation the context may be asked to resolve
 * references to types, beans, properties, and methods.
 *
 * <p>
 * 一个{@code SpelExpression}表示可以在指定上下文中进行求值的解析(有效)表达式表达式可以独立地或在指定的上下文中进行求值在表达式求值期间,可能会要求上下文解析对类型,bean,属性和
 * 方法。
 * 
 * 
 * @author Andy Clement
 * @since 3.0
 */
public class SpelExpression implements Expression {

	// Number of times to interpret an expression before compiling it
	private static final int INTERPRETED_COUNT_THRESHOLD = 100;

	// Number of times to try compiling an expression before giving up
	private static final int FAILED_ATTEMPTS_THRESHOLD = 100;


	private final String expression;

	private final SpelNodeImpl ast;

	private final SpelParserConfiguration configuration;

	// The default context is used if no override is supplied by the user
	private EvaluationContext evaluationContext;

	// Holds the compiled form of the expression (if it has been compiled)
	private CompiledExpression compiledAst;

	// Count of many times as the expression been interpreted - can trigger compilation
	// when certain limit reached
	private volatile int interpretedCount = 0;

	// The number of times compilation was attempted and failed - enables us to eventually
	// give up trying to compile it when it just doesn't seem to be possible.
	private volatile int failedAttempts = 0;


	/**
	 * Construct an expression, only used by the parser.
	 * <p>
	 *  构造一个表达式,仅由解析器使用
	 * 
	 */
	public SpelExpression(String expression, SpelNodeImpl ast, SpelParserConfiguration configuration) {
		this.expression = expression;
		this.ast = ast;
		this.configuration = configuration;
	}


	/**
	 * Set the evaluation context that will be used if none is specified on an evaluation call.
	 * <p>
	 *  设置评估上下文,如果在评估调用中未指定,将使用该上下文
	 * 
	 * 
	 * @param evaluationContext the evaluation context to use
	 */
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}

	/**
	 * Return the default evaluation context that will be used if none is supplied on an evaluation call.
	 * <p>
	 *  返回在评估调用中没有提供的默认评估上下文
	 * 
	 * 
	 * @return the default evaluation context
	 */
	public EvaluationContext getEvaluationContext() {
		if (this.evaluationContext == null) {
			this.evaluationContext = new StandardEvaluationContext();
		}
		return this.evaluationContext;
	}


	// implementing Expression

	@Override
	public Object getValue() throws EvaluationException {
		Object result;
		if (this.compiledAst != null) {
			try {
				TypedValue contextRoot = evaluationContext == null ? null : evaluationContext.getRootObject();
				return this.compiledAst.getValue(contextRoot == null ? null : contextRoot.getValue(), evaluationContext);
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
		result = this.ast.getValue(expressionState);
		checkCompile(expressionState);
		return result;
	}

	@Override
	public Object getValue(Object rootObject) throws EvaluationException {
		Object result;
		if (this.compiledAst != null) {
			try {
				return this.compiledAst.getValue(rootObject, evaluationContext);
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
		result = this.ast.getValue(expressionState);
		checkCompile(expressionState);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(Class<T> expectedResultType) throws EvaluationException {
		if (this.compiledAst != null) {
			try {
				TypedValue contextRoot = evaluationContext == null ? null : evaluationContext.getRootObject();
				Object result = this.compiledAst.getValue(contextRoot == null ? null : contextRoot.getValue(), evaluationContext);
				if (expectedResultType == null) {
					return (T)result;
				}
				else {
					return ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(result), expectedResultType);
				}
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
		TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
		checkCompile(expressionState);
		return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(Object rootObject, Class<T> expectedResultType) throws EvaluationException {
		if (this.compiledAst != null) {
			try {
				Object result = this.compiledAst.getValue(rootObject, null);
				if (expectedResultType == null) {
					return (T)result;
				}
				else {
					return ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(result), expectedResultType);
				}
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
		TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
		checkCompile(expressionState);
		return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
	}

	@Override
	public Object getValue(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		if (compiledAst!= null) {
			try {
				TypedValue contextRoot = context == null ? null : context.getRootObject();
				return this.compiledAst.getValue(contextRoot != null ? contextRoot.getValue() : null, context);
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(context, this.configuration);
		Object result = this.ast.getValue(expressionState);
		checkCompile(expressionState);
		return result;
	}

	@Override
	public Object getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		if (this.compiledAst != null) {
			try {
				return this.compiledAst.getValue(rootObject,context);
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
		Object result = this.ast.getValue(expressionState);
		checkCompile(expressionState);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(EvaluationContext context, Class<T> expectedResultType) throws EvaluationException {
		if (this.compiledAst != null) {
			try {
				TypedValue contextRoot = context == null ? null : context.getRootObject();
				Object result = this.compiledAst.getValue(contextRoot==null?null:contextRoot.getValue(),context);
				if (expectedResultType != null) {
					return ExpressionUtils.convertTypedValue(context, new TypedValue(result), expectedResultType);
				}
				else {
					return (T) result;
				}
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(context, this.configuration);
		TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
		checkCompile(expressionState);
		return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(EvaluationContext context, Object rootObject, Class<T> expectedResultType) throws EvaluationException {
		if (this.compiledAst != null) {
			try {
				Object result = this.compiledAst.getValue(rootObject,context);
				if (expectedResultType != null) {
					return ExpressionUtils.convertTypedValue(context, new TypedValue(result), expectedResultType);
				}
				else {
					return (T) result;
				}
			}
			catch (Throwable ex) {
				// If running in mixed mode, revert to interpreted
				if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
					this.interpretedCount = 0;
					this.compiledAst = null;
				}
				else {
					// Running in SpelCompilerMode.immediate mode - propagate exception to caller
					throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
				}
			}
		}
		ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
		TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
		checkCompile(expressionState);
		return ExpressionUtils.convertTypedValue(context, typedResultValue, expectedResultType);
	}

	@Override
	public Class<?> getValueType() throws EvaluationException {
		return getValueType(getEvaluationContext());
	}

	@Override
	public Class<?> getValueType(Object rootObject) throws EvaluationException {
		return getValueType(getEvaluationContext(), rootObject);
	}

	@Override
	public Class<?> getValueType(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		ExpressionState expressionState = new ExpressionState(context, this.configuration);
		TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
		return (typeDescriptor != null ? typeDescriptor.getType() : null);
	}

	@Override
	public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
		ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
		TypeDescriptor typeDescriptor = this.ast.getValueInternal(expressionState).getTypeDescriptor();
		return (typeDescriptor != null ? typeDescriptor.getType() : null);
	}

	@Override
	public TypeDescriptor getValueTypeDescriptor() throws EvaluationException {
		return getValueTypeDescriptor(getEvaluationContext());
	}

	@Override
	public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
		ExpressionState expressionState =
				new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration);
		return this.ast.getValueInternal(expressionState).getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		ExpressionState expressionState = new ExpressionState(context, this.configuration);
		return this.ast.getValueInternal(expressionState).getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		ExpressionState expressionState = new ExpressionState(context, toTypedValue(rootObject), this.configuration);
		return this.ast.getValueInternal(expressionState).getTypeDescriptor();
	}

	@Override
	public String getExpressionString() {
		return this.expression;
	}

	@Override
	public boolean isWritable(EvaluationContext context) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		return this.ast.isWritable(new ExpressionState(context, this.configuration));
	}

	@Override
	public boolean isWritable(Object rootObject) throws EvaluationException {
		return this.ast.isWritable(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration));
	}

	@Override
	public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		return this.ast.isWritable(new ExpressionState(context, toTypedValue(rootObject), this.configuration));
	}

	@Override
	public void setValue(EvaluationContext context, Object value) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		this.ast.setValue(new ExpressionState(context, this.configuration), value);
	}

	@Override
	public void setValue(Object rootObject, Object value) throws EvaluationException {
		this.ast.setValue(new ExpressionState(getEvaluationContext(), toTypedValue(rootObject), this.configuration), value);
	}

	@Override
	public void setValue(EvaluationContext context, Object rootObject, Object value) throws EvaluationException {
		Assert.notNull(context, "EvaluationContext is required");
		this.ast.setValue(new ExpressionState(context, toTypedValue(rootObject), this.configuration), value);
	}


	/**
	 * Compile the expression if it has been evaluated more than the threshold number
	 * of times to trigger compilation.
	 * <p>
	 *  编译表达式,如果它已被评估超过触发编译的阈值次数
	 * 
	 * 
	 * @param expressionState the expression state used to determine compilation mode
	 */
	private void checkCompile(ExpressionState expressionState) {
		this.interpretedCount++;
		SpelCompilerMode compilerMode = expressionState.getConfiguration().getCompilerMode();
		if (compilerMode != SpelCompilerMode.OFF) {
			if (compilerMode == SpelCompilerMode.IMMEDIATE) {
				if (this.interpretedCount > 1) {
					compileExpression();
				}
			}
			else {
				// compilerMode = SpelCompilerMode.MIXED
				if (this.interpretedCount > INTERPRETED_COUNT_THRESHOLD) {
					compileExpression();
				}
			}
		}
	}


	/**
	 * Perform expression compilation. This will only succeed once exit descriptors for all nodes have
	 * been determined. If the compilation fails and has failed more than 100 times the expression is
	 * no longer considered suitable for compilation.
	 * <p>
	 * 执行表达式编译只有在确定了所有节点的退出描述符后才会成功如果编译失败并且失败超过100次,表达式不再被认为适合于编译
	 * 
	 */
	public boolean compileExpression() {
		if (this.failedAttempts > FAILED_ATTEMPTS_THRESHOLD) {
			// Don't try again
			return false;
		}
		if (this.compiledAst == null) {
			synchronized (this.expression) {
				// Possibly compiled by another thread before this thread got into the sync block
				if (this.compiledAst != null) {
					return true;
				}
				SpelCompiler compiler = SpelCompiler.getCompiler(this.configuration.getCompilerClassLoader());
				this.compiledAst = compiler.compile(this.ast);
				if (this.compiledAst == null) {
					this.failedAttempts++;
				}
			}
		}
		return (this.compiledAst != null);
	}

	/**
	 * Cause an expression to revert to being interpreted if it has been using a compiled
	 * form. It also resets the compilation attempt failure count (an expression is normally no
	 * longer considered compilable if it cannot be compiled after 100 attempts).
	 * <p>
	 *  导致表达式恢复为被解释,如果它已经使用编译形式它还会重置编译尝试失败计数(一个表达式通常不再被视为可编译,如果在100次尝试后无法编译)
	 * 
	 */
	public void revertToInterpreted() {
		this.compiledAst = null;
		this.interpretedCount = 0;
		this.failedAttempts = 0;
	}

	/**
	 * Return the Abstract Syntax Tree for the expression.
	 * <p>
	 *  返回表达式的抽象语法树
	 * 
	 */
	public SpelNode getAST() {
		return this.ast;
	}

	/**
	 * Produce a string representation of the Abstract Syntax Tree for the expression.
	 * This should ideally look like the input expression, but properly formatted since any
	 * unnecessary whitespace will have been discarded during the parse of the expression.
	 * <p>
	 *  为表达式生成抽象语法树的字符串表示这应该理想地看起来像输入表达式,但格式正确,因为在表达式的解析期间将丢弃任何不必要的空格
	 * 
	 * @return the string representation of the AST
	 */
	public String toStringAST() {
		return this.ast.toStringAST();
	}

	private TypedValue toTypedValue(Object object) {
		if (object == null) {
			return TypedValue.NULL;
		}
		else {
			return new TypedValue(object);
		}
	}

}
