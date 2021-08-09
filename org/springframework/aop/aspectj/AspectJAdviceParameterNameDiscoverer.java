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

package org.springframework.aop.aspectj;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;

/**
 * {@link ParameterNameDiscoverer} implementation that tries to deduce parameter names
 * for an advice method from the pointcut expression, returning, and throwing clauses.
 * If an unambiguous interpretation is not available, it returns {@code null}.
 *
 * <p>This class interprets arguments in the following way:
 * <ol>
 * <li>If the first parameter of the method is of type {@link JoinPoint}
 * or {@link ProceedingJoinPoint}, it is assumed to be for passing
 * {@code thisJoinPoint} to the advice, and the parameter name will
 * be assigned the value {@code "thisJoinPoint"}.</li>
 * <li>If the first parameter of the method is of type
 * {@code JoinPoint.StaticPart}, it is assumed to be for passing
 * {@code "thisJoinPointStaticPart"} to the advice, and the parameter name
 * will be assigned the value {@code "thisJoinPointStaticPart"}.</li>
 * <li>If a {@link #setThrowingName(String) throwingName} has been set, and
 * there are no unbound arguments of type {@code Throwable+}, then an
 * {@link IllegalArgumentException} is raised. If there is more than one
 * unbound argument of type {@code Throwable+}, then an
 * {@link AmbiguousBindingException} is raised. If there is exactly one
 * unbound argument of type {@code Throwable+}, then the corresponding
 * parameter name is assigned the value &lt;throwingName&gt;.</li>
 * <li>If there remain unbound arguments, then the pointcut expression is
 * examined. Let {@code a} be the number of annotation-based pointcut
 * expressions (&#64;annotation, &#64;this, &#64;target, &#64;args,
 * &#64;within, &#64;withincode) that are used in binding form. Usage in
 * binding form has itself to be deduced: if the expression inside the
 * pointcut is a single string literal that meets Java variable name
 * conventions it is assumed to be a variable name. If {@code a} is
 * zero we proceed to the next stage. If {@code a} &gt; 1 then an
 * {@code AmbiguousBindingException} is raised. If {@code a} == 1,
 * and there are no unbound arguments of type {@code Annotation+},
 * then an {@code IllegalArgumentException} is raised. if there is
 * exactly one such argument, then the corresponding parameter name is
 * assigned the value from the pointcut expression.</li>
 * <li>If a returningName has been set, and there are no unbound arguments
 * then an {@code IllegalArgumentException} is raised. If there is
 * more than one unbound argument then an
 * {@code AmbiguousBindingException} is raised. If there is exactly
 * one unbound argument then the corresponding parameter name is assigned
 * the value &lt;returningName&gt;.</li>
 * <li>If there remain unbound arguments, then the pointcut expression is
 * examined once more for {@code this}, {@code target}, and
 * {@code args} pointcut expressions used in the binding form (binding
 * forms are deduced as described for the annotation based pointcuts). If
 * there remains more than one unbound argument of a primitive type (which
 * can only be bound in {@code args}) then an
 * {@code AmbiguousBindingException} is raised. If there is exactly
 * one argument of a primitive type, then if exactly one {@code args}
 * bound variable was found, we assign the corresponding parameter name
 * the variable name. If there were no {@code args} bound variables
 * found an {@code IllegalStateException} is raised. If there are
 * multiple {@code args} bound variables, an
 * {@code AmbiguousBindingException} is raised. At this point, if
 * there remains more than one unbound argument we raise an
 * {@code AmbiguousBindingException}. If there are no unbound arguments
 * remaining, we are done. If there is exactly one unbound argument
 * remaining, and only one candidate variable name unbound from
 * {@code this}, {@code target}, or {@code args}, it is
 * assigned as the corresponding parameter name. If there are multiple
 * possibilities, an {@code AmbiguousBindingException} is raised.</li>
 * </ol>
 *
 * <p>The behavior on raising an {@code IllegalArgumentException} or
 * {@code AmbiguousBindingException} is configurable to allow this discoverer
 * to be used as part of a chain-of-responsibility. By default the condition will
 * be logged and the {@code getParameterNames(..)} method will simply return
 * {@code null}. If the {@link #setRaiseExceptions(boolean) raiseExceptions}
 * property is set to {@code true}, the conditions will be thrown as
 * {@code IllegalArgumentException} and {@code AmbiguousBindingException},
 * respectively.
 *
 * <p>Was that perfectly clear? ;)
 *
 * <p>Short version: If an unambiguous binding can be deduced, then it is.
 * If the advice requirements cannot possibly be satisfied, then {@code null}
 * is returned. By setting the {@link #setRaiseExceptions(boolean) raiseExceptions}
 * property to {@code true}, descriptive exceptions will be thrown instead of
 * returning {@code null} in the case that the parameter names cannot be discovered.
 *
 * <p>
 * {@link ParameterNameDiscoverer}实现,试图从切入点表达式,返回和抛出子句中推导出一个advice方法的参数名称如果一个明确的解释不可用,则返回{@code null}
 * 
 *  <p>此类以下列方式解释参数：
 * <ol>
 * <li>如果方法的第一个参数是类型为{@link JoinPoint}或{@link ProceedingJoinPoint},则假定是将{@code thisJoinPoint}传递给建议,参数名称将
 * 被赋值{@code"thisJoinPoint"} </li> <li>如果方法的第一个参数是类型为{@code JoinPointStaticPart},则假定是将{@code"thisJoinPointStaticPart"}
 * 传递给建议,参数名称将被分配值{@code"thisJoinPointStaticPart"} </li> <li>如果设置了{@link #setThrowingName(String)throwingName}
 * ,并且没有类型为{@code Throwable +}的未绑定参数,则会引发{@link IllegalArgumentException}如果有多个非绑定参数类型为{@code Throwable +}
 * ,则引发{@link AmbiguousBindingException}如果只有一个类型为{@code Throwable +}的未绑定参数,则相应的参数名称将被赋值为&lt; ; throwingN
 * ame&gt; </li> <li>如果存在未绑定的参数,则检查切入点表达式Let {@code a}是基于注解的切入点表达式的数量(@annotation,@this,@target,@args, @
 * within,@withincode)在绑定形式中使用绑定形式中的用法本身就被推导出来：如果切入点内的表达式是符合Java变量名称约定的单个字符串字面值,则假定它是一个变量名称如果{@代码a}为零我们进
 * 入下一个阶段如果{@code a}&gt; 1然后引发{@code AmbiguousBindingException}如果{@code a} == 1,并且没有类型为{@code Annotation +}
 * 的未绑定参数,则如果只有一个此类参数,则会引发{@code IllegalArgumentException} ,那么相应的参数名称将从切入点表达式中分配值</li> <li>如果已设置了returns
 * Name,并且没有未绑定的参数,则引发了{@code IllegalArgumentException}如果存在多个未绑定参数,然后引发{@code AmbiguousBindingException}
 * 如果只有一个unbound参数,则相应的参数名称将分配值&lt; returnsName&gt;</li> <li>如果仍然存在未绑定的参数,则会在绑定表单中使用{@code this},{@code target}
 * 和{@code args}切入点表达式再次检查切入点表达式如针对基于注释的切入点所描述的那样推导)如果存在多个原始类型的未绑定参数(只能在{@code args}中绑定)),则引发{@code AmbiguousBindingException}
 * 如果只有一个参数的原始类型,那么如果找到一个{@code args}绑定变量,我们将相应的参数名称赋给变量名如果没有发现{@code args}绑定变量,则引发{@code IllegalStateException}
 * 如果有是多个{@code args}绑定变量,引发了{@code AmbiguousBindingException}在这一点上,如果还有一个以上的unbound参数,我们会引发一个{@code AmbiguousBindingException}
 * 如果没有未绑定的参数存在,那么我们已经完成如果只剩下一个unbound参数,并且只有一个候选变量名从{@代码this},{@code target}或{@code args},它被分配为相应的参数名称如
 * 果有多种可能性,将引发{@code AmbiguousBindingException} </li>。
 * </ol>
 * 
 * <p>提高{@code IllegalArgumentException}或{@code AmbiguousBindingException}的行为是可配置的,以允许此发现者用作责任链的一部分默认情况下
 * 将记录条件,并且{@code getParameterNames ()}方法将简单地返回{@code null}如果{@link #setRaiseExceptions(boolean)raiseExceptions}
 * 属性设置为{@code true},则条件将抛出为{@code IllegalArgumentException}和{@code AmbiguousBindingException }, 分别。
 * 
 *  这是非常清楚的吗? ;)
 * 
 * <p>简短版本：如果可以推导出明确的绑定,那么就是如果不能满足建议要求,则返回{@code null}通过将{@link #setRaiseExceptions(boolean)raiseExceptions}
 * 属性设置为{ @code true},在不能发现参数名称的情况下,将引发描述性异常而不是返回{@code null}。
 * 
 * 
 * @author Adrian Colyer
 * @since 2.0
 */
public class AspectJAdviceParameterNameDiscoverer implements ParameterNameDiscoverer {

	private static final String THIS_JOIN_POINT = "thisJoinPoint";
	private static final String THIS_JOIN_POINT_STATIC_PART = "thisJoinPointStaticPart";

	// Steps in the binding algorithm...
	private static final int STEP_JOIN_POINT_BINDING = 1;
	private static final int STEP_THROWING_BINDING = 2;
	private static final int STEP_ANNOTATION_BINDING = 3;
	private static final int STEP_RETURNING_BINDING = 4;
	private static final int STEP_PRIMITIVE_ARGS_BINDING = 5;
	private static final int STEP_THIS_TARGET_ARGS_BINDING = 6;
	private static final int STEP_REFERENCE_PCUT_BINDING = 7;
	private static final int STEP_FINISHED = 8;

	private static final Set<String> singleValuedAnnotationPcds = new HashSet<String>();
	private static final Set<String> nonReferencePointcutTokens = new HashSet<String>();


	static {
		singleValuedAnnotationPcds.add("@this");
		singleValuedAnnotationPcds.add("@target");
		singleValuedAnnotationPcds.add("@within");
		singleValuedAnnotationPcds.add("@withincode");
		singleValuedAnnotationPcds.add("@annotation");

		Set<PointcutPrimitive> pointcutPrimitives = PointcutParser.getAllSupportedPointcutPrimitives();
		for (PointcutPrimitive primitive : pointcutPrimitives) {
			nonReferencePointcutTokens.add(primitive.getName());
		}
		nonReferencePointcutTokens.add("&&");
		nonReferencePointcutTokens.add("!");
		nonReferencePointcutTokens.add("||");
		nonReferencePointcutTokens.add("and");
		nonReferencePointcutTokens.add("or");
		nonReferencePointcutTokens.add("not");
	}


	private boolean raiseExceptions;

	/**
	 * If the advice is afterReturning, and binds the return value, this is the parameter name used.
	 * <p>
	 *  如果建议是在返回结束后绑定返回值,则这是使用的参数名称
	 * 
	 */
	private String returningName;

	/**
	 * If the advice is afterThrowing, and binds the thrown value, this is the parameter name used.
	 * <p>
	 *  如果建议是afterThrowing,并绑定抛出的值,这是使用的参数名称
	 * 
	 */
	private String throwingName;

	/**
	 * The pointcut expression associated with the advice, as a simple String.
	 * <p>
	 *  与通知相关联的切入点表达式,作为一个简单的字符串
	 * 
	 */
	private String pointcutExpression;

	private Class<?>[] argumentTypes;

	private String[] parameterNameBindings;

	private int numberOfRemainingUnboundArguments;


	/**
	 * Create a new discoverer that attempts to discover parameter names
	 * from the given pointcut expression.
	 * <p>
	 *  创建一个新的发现者,尝试从给定的切入点表达式中发现参数名称
	 * 
	 */
	public AspectJAdviceParameterNameDiscoverer(String pointcutExpression) {
		this.pointcutExpression = pointcutExpression;
	}

	/**
	 * Indicate whether {@link IllegalArgumentException} and {@link AmbiguousBindingException}
	 * must be thrown as appropriate in the case of failing to deduce advice parameter names.
	 * <p>
	 * 指示是否在不能推导出建议参数名称的情况下适当地抛出{@link IllegalArgumentException}和{@link AmbiguousBindingException}
	 * 
	 * 
	 * @param raiseExceptions {@code true} if exceptions are to be thrown
	 */
	public void setRaiseExceptions(boolean raiseExceptions) {
		this.raiseExceptions = raiseExceptions;
	}

	/**
	 * If {@code afterReturning} advice binds the return value, the
	 * returning variable name must be specified.
	 * <p>
	 *  如果{@code afterReturning} advice绑定返回值,则必须指定返回的变量名称
	 * 
	 * 
	 * @param returningName the name of the returning variable
	 */
	public void setReturningName(String returningName) {
		this.returningName = returningName;
	}

	/**
	 * If {@code afterThrowing} advice binds the thrown value, the
	 * throwing variable name must be specified.
	 * <p>
	 *  如果{@code afterThrowing} advice绑定抛出的值,则必须指定throws变量名
	 * 
	 * 
	 * @param throwingName the name of the throwing variable
	 */
	public void setThrowingName(String throwingName) {
		this.throwingName = throwingName;
	}

	/**
	 * Deduce the parameter names for an advice method.
	 * <p>See the {@link AspectJAdviceParameterNameDiscoverer class level javadoc}
	 * for this class for details of the algorithm used.
	 * <p>
	 *  引用一个通知方法的参数名称<p>有关使用的算法的详细信息,请参阅此类的{@link AspectJAdviceParameterNameDiscoverer类级别javadoc}
	 * 
	 * 
	 * @param method the target {@link Method}
	 * @return the parameter names
	 */
	@Override
	public String[] getParameterNames(Method method) {
		this.argumentTypes = method.getParameterTypes();
		this.numberOfRemainingUnboundArguments = this.argumentTypes.length;
		this.parameterNameBindings = new String[this.numberOfRemainingUnboundArguments];

		int minimumNumberUnboundArgs = 0;
		if (this.returningName != null) {
			minimumNumberUnboundArgs++;
		}
		if (this.throwingName != null) {
			minimumNumberUnboundArgs++;
		}
		if (this.numberOfRemainingUnboundArguments < minimumNumberUnboundArgs) {
			throw new IllegalStateException(
					"Not enough arguments in method to satisfy binding of returning and throwing variables");
		}

		try {
			int algorithmicStep = STEP_JOIN_POINT_BINDING;
			while ((this.numberOfRemainingUnboundArguments > 0) && algorithmicStep < STEP_FINISHED) {
				switch (algorithmicStep++) {
					case STEP_JOIN_POINT_BINDING:
						if (!maybeBindThisJoinPoint()) {
							maybeBindThisJoinPointStaticPart();
						}
						break;
					case STEP_THROWING_BINDING:
						maybeBindThrowingVariable();
						break;
					case STEP_ANNOTATION_BINDING:
						maybeBindAnnotationsFromPointcutExpression();
						break;
					case STEP_RETURNING_BINDING:
						maybeBindReturningVariable();
						break;
					case STEP_PRIMITIVE_ARGS_BINDING:
						maybeBindPrimitiveArgsFromPointcutExpression();
						break;
					case STEP_THIS_TARGET_ARGS_BINDING:
						maybeBindThisOrTargetOrArgsFromPointcutExpression();
						break;
					case STEP_REFERENCE_PCUT_BINDING:
						maybeBindReferencePointcutParameter();
						break;
					default:
						throw new IllegalStateException("Unknown algorithmic step: " + (algorithmicStep - 1));
				}
			}
		}
		catch (AmbiguousBindingException ambigEx) {
			if (this.raiseExceptions) {
				throw ambigEx;
			}
			else {
				return null;
			}
		}
		catch (IllegalArgumentException ex) {
			if (this.raiseExceptions) {
				throw ex;
			}
			else {
				return null;
			}
		}

		if (this.numberOfRemainingUnboundArguments == 0) {
			return this.parameterNameBindings;
		}
		else {
			if (this.raiseExceptions) {
				throw new IllegalStateException("Failed to bind all argument names: " +
						this.numberOfRemainingUnboundArguments + " argument(s) could not be bound");
			}
			else {
				// convention for failing is to return null, allowing participation in a chain of responsibility
				return null;
			}
		}
	}

	/**
	 * An advice method can never be a constructor in Spring.
	 * <p>
	 *  一个建议方法永远不可能是Spring中的一个构造函数
	 * 
	 * 
	 * @return {@code null}
	 * @throws UnsupportedOperationException if
	 * {@link #setRaiseExceptions(boolean) raiseExceptions} has been set to {@code true}
	 */
	@Override
	public String[] getParameterNames(Constructor<?> ctor) {
		if (this.raiseExceptions) {
			throw new UnsupportedOperationException("An advice method can never be a constructor");
		}
		else {
			// we return null rather than throw an exception so that we behave well
			// in a chain-of-responsibility.
			return null;
		}
	}


	private void bindParameterName(int index, String name) {
		this.parameterNameBindings[index] = name;
		this.numberOfRemainingUnboundArguments--;
	}

	/**
	 * If the first parameter is of type JoinPoint or ProceedingJoinPoint,bind "thisJoinPoint" as
	 * parameter name and return true, else return false.
	 * <p>
	 *  如果第一个参数为JoinPoint或ProceedingJoinPoint,则将"thisJoinPoint"绑定为参数名称,并返回true,否则返回false
	 * 
	 */
	private boolean maybeBindThisJoinPoint() {
		if ((this.argumentTypes[0] == JoinPoint.class) || (this.argumentTypes[0] == ProceedingJoinPoint.class)) {
			bindParameterName(0, THIS_JOIN_POINT);
			return true;
		}
		else {
			return false;
		}
	}

	private void maybeBindThisJoinPointStaticPart() {
		if (this.argumentTypes[0] == JoinPoint.StaticPart.class) {
			bindParameterName(0, THIS_JOIN_POINT_STATIC_PART);
		}
	}

	/**
	 * If a throwing name was specified and there is exactly one choice remaining
	 * (argument that is a subtype of Throwable) then bind it.
	 * <p>
	 * 如果指定了一个抛出的名称,并且只剩下一个选项(参数是Throwable的子类型),则绑定它
	 * 
	 */
	private void maybeBindThrowingVariable() {
		if (this.throwingName == null) {
			return;
		}

		// So there is binding work to do...
		int throwableIndex = -1;
		for (int i = 0; i < this.argumentTypes.length; i++) {
			if (isUnbound(i) && isSubtypeOf(Throwable.class, i)) {
				if (throwableIndex == -1) {
					throwableIndex = i;
				}
				else {
					// Second candidate we've found - ambiguous binding
					throw new AmbiguousBindingException("Binding of throwing parameter '" +
							this.throwingName + "' is ambiguous: could be bound to argument " +
							throwableIndex + " or argument " + i);
				}
			}
		}

		if (throwableIndex == -1) {
			throw new IllegalStateException("Binding of throwing parameter '" + this.throwingName
					+ "' could not be completed as no available arguments are a subtype of Throwable");
		}
		else {
			bindParameterName(throwableIndex, this.throwingName);
		}
	}

	/**
	 * If a returning variable was specified and there is only one choice remaining, bind it.
	 * <p>
	 *  如果指定了返回的变量,并且只剩下一个选项,请将其绑定
	 * 
	 */
	private void maybeBindReturningVariable() {
		if (this.numberOfRemainingUnboundArguments == 0) {
			throw new IllegalStateException(
					"Algorithm assumes that there must be at least one unbound parameter on entry to this method");
		}

		if (this.returningName != null) {
			if (this.numberOfRemainingUnboundArguments > 1) {
				throw new AmbiguousBindingException("Binding of returning parameter '" + this.returningName +
						"' is ambiguous, there are " + this.numberOfRemainingUnboundArguments + " candidates.");
			}

			// We're all set... find the unbound parameter, and bind it.
			for (int i = 0; i < this.parameterNameBindings.length; i++) {
				if (this.parameterNameBindings[i] == null) {
					bindParameterName(i, this.returningName);
					break;
				}
			}
		}
	}


	/**
	 * Parse the string pointcut expression looking for:
	 * &#64;this, &#64;target, &#64;args, &#64;within, &#64;withincode, &#64;annotation.
	 * If we find one of these pointcut expressions, try and extract a candidate variable
	 * name (or variable names, in the case of args).
	 * <p>Some more support from AspectJ in doing this exercise would be nice... :)
	 * <p>
	 *  解析字符串切入点表达式寻找：@this,@target,@args,@within,@withincode,@annotation如果我们找到这些切入点表达式之一,尝试并提取候选变量名(或变量名称,在
	 * args)<p> AspectJ在做这个练习的一些更多的支持将是很好:)。
	 * 
	 */
	private void maybeBindAnnotationsFromPointcutExpression() {
		List<String> varNames = new ArrayList<String>();
		String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
		for (int i = 0; i < tokens.length; i++) {
			String toMatch = tokens[i];
			int firstParenIndex = toMatch.indexOf("(");
			if (firstParenIndex != -1) {
				toMatch = toMatch.substring(0, firstParenIndex);
			}
			if (singleValuedAnnotationPcds.contains(toMatch)) {
				PointcutBody body = getPointcutBody(tokens, i);
				i += body.numTokensConsumed;
				String varName = maybeExtractVariableName(body.text);
				if (varName != null) {
					varNames.add(varName);
				}
			}
			else if (tokens[i].startsWith("@args(") || tokens[i].equals("@args")) {
				PointcutBody body = getPointcutBody(tokens, i);
				i += body.numTokensConsumed;
				maybeExtractVariableNamesFromArgs(body.text, varNames);
			}
		}

		bindAnnotationsFromVarNames(varNames);
	}

	/**
	 * Match the given list of extracted variable names to argument slots.
	 * <p>
	 *  将提取的变量名称的给定列表与参数插槽相匹配
	 * 
	 */
	private void bindAnnotationsFromVarNames(List<String> varNames) {
		if (!varNames.isEmpty()) {
			// we have work to do...
			int numAnnotationSlots = countNumberOfUnboundAnnotationArguments();
			if (numAnnotationSlots > 1) {
				throw new AmbiguousBindingException("Found " + varNames.size() +
						" potential annotation variable(s), and " +
						numAnnotationSlots + " potential argument slots");
			}
			else if (numAnnotationSlots == 1) {
				if (varNames.size() == 1) {
					// it's a match
					findAndBind(Annotation.class, varNames.get(0));
				}
				else {
					// multiple candidate vars, but only one slot
					throw new IllegalArgumentException("Found " + varNames.size() +
							" candidate annotation binding variables" +
							" but only one potential argument binding slot");
				}
			}
			else {
				// no slots so presume those candidate vars were actually type names
			}
		}
	}

	/*
	 * If the token starts meets Java identifier conventions, it's in.
	 * <p>
	 *  如果令牌启动符合Java标识符约定,那么它就在
	 * 
	 */
	private String maybeExtractVariableName(String candidateToken) {
		if (candidateToken == null || candidateToken.equals("")) {
			return null;
		}
		if (Character.isJavaIdentifierStart(candidateToken.charAt(0)) &&
				Character.isLowerCase(candidateToken.charAt(0))) {
			char[] tokenChars = candidateToken.toCharArray();
			for (char tokenChar : tokenChars) {
				if (!Character.isJavaIdentifierPart(tokenChar)) {
					return null;
				}
			}
			return candidateToken;
		}
		else {
			return null;
		}
	}

	/**
	 * Given an args pointcut body (could be {@code args} or {@code at_args}),
	 * add any candidate variable names to the given list.
	 * <p>
	 * 给定一个args切入点(可以是{@code args}或{@code at_args}),将任何候选变量名添加到给定的列表中
	 * 
	 */
	private void maybeExtractVariableNamesFromArgs(String argsSpec, List<String> varNames) {
		if (argsSpec == null) {
			return;
		}
		String[] tokens = StringUtils.tokenizeToStringArray(argsSpec, ",");
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = StringUtils.trimWhitespace(tokens[i]);
			String varName = maybeExtractVariableName(tokens[i]);
			if (varName != null) {
				varNames.add(varName);
			}
		}
	}

	/**
	 * Parse the string pointcut expression looking for this(), target() and args() expressions.
	 * If we find one, try and extract a candidate variable name and bind it.
	 * <p>
	 *  解析查找this(),target()和args()表达式的字符串切入点表达式如果我们找到一个,尝试并提取候选变量名称并绑定它
	 * 
	 */
	private void maybeBindThisOrTargetOrArgsFromPointcutExpression() {
		if (this.numberOfRemainingUnboundArguments > 1) {
			throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments
					+ " unbound args at this(),target(),args() binding stage, with no way to determine between them");
		}

		List<String> varNames = new ArrayList<String>();
		String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals("this") ||
					tokens[i].startsWith("this(") ||
					tokens[i].equals("target") ||
					tokens[i].startsWith("target(")) {
				PointcutBody body = getPointcutBody(tokens, i);
				i += body.numTokensConsumed;
				String varName = maybeExtractVariableName(body.text);
				if (varName != null) {
					varNames.add(varName);
				}
			}
			else if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
				PointcutBody body = getPointcutBody(tokens, i);
				i += body.numTokensConsumed;
				List<String> candidateVarNames = new ArrayList<String>();
				maybeExtractVariableNamesFromArgs(body.text, candidateVarNames);
				// we may have found some var names that were bound in previous primitive args binding step,
				// filter them out...
				for (String varName : candidateVarNames) {
					if (!alreadyBound(varName)) {
						varNames.add(varName);
					}
				}
			}
		}


		if (varNames.size() > 1) {
			throw new AmbiguousBindingException("Found " + varNames.size() +
					" candidate this(), target() or args() variables but only one unbound argument slot");
		}
		else if (varNames.size() == 1) {
			for (int j = 0; j < this.parameterNameBindings.length; j++) {
				if (isUnbound(j)) {
					bindParameterName(j, varNames.get(0));
					break;
				}
			}
		}
		// else varNames.size must be 0 and we have nothing to bind.
	}

	private void maybeBindReferencePointcutParameter() {
		if (this.numberOfRemainingUnboundArguments > 1) {
			throw new AmbiguousBindingException("Still " + this.numberOfRemainingUnboundArguments
					+ " unbound args at reference pointcut binding stage, with no way to determine between them");
		}

		List<String> varNames = new ArrayList<String>();
		String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
		for (int i = 0; i < tokens.length; i++) {
			String toMatch = tokens[i];
			if (toMatch.startsWith("!")) {
				toMatch = toMatch.substring(1);
			}
			int firstParenIndex = toMatch.indexOf("(");
			if (firstParenIndex != -1) {
				toMatch = toMatch.substring(0, firstParenIndex);
			}
			else {
				if (tokens.length < i + 2) {
					// no "(" and nothing following
					continue;
				}
				else {
					String nextToken = tokens[i + 1];
					if (nextToken.charAt(0) != '(') {
						// next token is not "(" either, can't be a pc...
						continue;
					}
				}

			}

			// eat the body
			PointcutBody body = getPointcutBody(tokens, i);
			i += body.numTokensConsumed;

			if (!nonReferencePointcutTokens.contains(toMatch)) {
				// then it could be a reference pointcut
				String varName = maybeExtractVariableName(body.text);
				if (varName != null) {
					varNames.add(varName);
				}
			}
		}

		if (varNames.size() > 1) {
			throw new AmbiguousBindingException("Found " + varNames.size() +
					" candidate reference pointcut variables but only one unbound argument slot");
		}
		else if (varNames.size() == 1) {
			for (int j = 0; j < this.parameterNameBindings.length; j++) {
				if (isUnbound(j)) {
					bindParameterName(j, varNames.get(0));
					break;
				}
			}
		}
		// else varNames.size must be 0 and we have nothing to bind.
	}

	/*
	 * We've found the start of a binding pointcut at the given index into the
	 * token array. Now we need to extract the pointcut body and return it.
	 * <p>
	 *  我们已经在给定的索引中找到了令牌数组中的绑定切入点的开始现在我们需要提取切入点并返回它
	 * 
	 */
	private PointcutBody getPointcutBody(String[] tokens, int startIndex) {
		int numTokensConsumed = 0;
		String currentToken = tokens[startIndex];
		int bodyStart = currentToken.indexOf('(');
		if (currentToken.charAt(currentToken.length() - 1) == ')') {
			// It's an all in one... get the text between the first (and the last)
			return new PointcutBody(0, currentToken.substring(bodyStart + 1, currentToken.length() - 1));
		}
		else {
			StringBuilder sb = new StringBuilder();
			if (bodyStart >= 0 && bodyStart != (currentToken.length() - 1)) {
				sb.append(currentToken.substring(bodyStart + 1));
				sb.append(" ");
			}
			numTokensConsumed++;
			int currentIndex = startIndex + numTokensConsumed;
			while (currentIndex < tokens.length) {
				if (tokens[currentIndex].equals("(")) {
					currentIndex++;
					continue;
				}

				if (tokens[currentIndex].endsWith(")")) {
					sb.append(tokens[currentIndex].substring(0, tokens[currentIndex].length() - 1));
					return new PointcutBody(numTokensConsumed, sb.toString().trim());
				}

				String toAppend = tokens[currentIndex];
				if (toAppend.startsWith("(")) {
					toAppend = toAppend.substring(1);
				}
				sb.append(toAppend);
				sb.append(" ");
				currentIndex++;
				numTokensConsumed++;
			}

		}

		// We looked and failed...
		return new PointcutBody(numTokensConsumed, null);
	}

	/**
	 * Match up args against unbound arguments of primitive types
	 * <p>
	 *  匹配反对原始类型的未绑定参数
	 * 
	 */
	private void maybeBindPrimitiveArgsFromPointcutExpression() {
		int numUnboundPrimitives = countNumberOfUnboundPrimitiveArguments();
		if (numUnboundPrimitives > 1) {
			throw new AmbiguousBindingException("Found '" + numUnboundPrimitives +
					"' unbound primitive arguments with no way to distinguish between them.");
		}
		if (numUnboundPrimitives == 1) {
			// Look for arg variable and bind it if we find exactly one...
			List<String> varNames = new ArrayList<String>();
			String[] tokens = StringUtils.tokenizeToStringArray(this.pointcutExpression, " ");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals("args") || tokens[i].startsWith("args(")) {
					PointcutBody body = getPointcutBody(tokens, i);
					i += body.numTokensConsumed;
					maybeExtractVariableNamesFromArgs(body.text, varNames);
				}
			}
			if (varNames.size() > 1) {
				throw new AmbiguousBindingException("Found " + varNames.size() +
						" candidate variable names but only one candidate binding slot when matching primitive args");
			}
			else if (varNames.size() == 1) {
				// 1 primitive arg, and one candidate...
				for (int i = 0; i < this.argumentTypes.length; i++) {
					if (isUnbound(i) && this.argumentTypes[i].isPrimitive()) {
						bindParameterName(i, varNames.get(0));
						break;
					}
				}
			}
		}
	}

	/*
	 * Return true if the parameter name binding for the given parameter
	 * index has not yet been assigned.
	 * <p>
	 *  如果给定参数索引的参数名称绑定尚未分配,则返回true
	 * 
	 */
	private boolean isUnbound(int i) {
		return this.parameterNameBindings[i] == null;
	}

	private boolean alreadyBound(String varName) {
		for (int i = 0; i < this.parameterNameBindings.length; i++) {
			if (!isUnbound(i) && varName.equals(this.parameterNameBindings[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Return {@code true} if the given argument type is a subclass
	 * of the given supertype.
	 * <p>
	 *  如果给定的参数类型是给定的超类型的子类,则返回{@code true}
	 * 
	 */
	private boolean isSubtypeOf(Class<?> supertype, int argumentNumber) {
		return supertype.isAssignableFrom(this.argumentTypes[argumentNumber]);
	}

	private int countNumberOfUnboundAnnotationArguments() {
		int count = 0;
		for (int i = 0; i < this.argumentTypes.length; i++) {
			if (isUnbound(i) && isSubtypeOf(Annotation.class, i)) {
				count++;
			}
		}
		return count;
	}

	private int countNumberOfUnboundPrimitiveArguments() {
		int count = 0;
		for (int i = 0; i < this.argumentTypes.length; i++) {
			if (isUnbound(i) && this.argumentTypes[i].isPrimitive()) {
				count++;
			}
		}
		return count;
	}

	/*
	 * Find the argument index with the given type, and bind the given
	 * {@code varName} in that position.
	 * <p>
	 *  找到具有给定类型的参数索引,并在该位置绑定给定的{@code varName}
	 * 
	 */
	private void findAndBind(Class<?> argumentType, String varName) {
		for (int i = 0; i < this.argumentTypes.length; i++) {
			if (isUnbound(i) && isSubtypeOf(argumentType, i)) {
				bindParameterName(i, varName);
				return;
			}
		}
		throw new IllegalStateException("Expected to find an unbound argument of type '" +
				argumentType.getName() + "'");
	}


	/**
	 * Simple struct to hold the extracted text from a pointcut body, together
	 * with the number of tokens consumed in extracting it.
	 * <p>
	 * 简单的结构体保存来自切入点主体的提取的文本,以及在提取它时消耗的令牌数
	 * 
	 */
	private static class PointcutBody {

		private int numTokensConsumed;

		private String text;

		public PointcutBody(int tokens, String text) {
			this.numTokensConsumed = tokens;
			this.text = text;
		}
	}


	/**
	 * Thrown in response to an ambiguous binding being detected when
	 * trying to resolve a method's parameter names.
	 * <p>
	 *  在尝试解析方法的参数名称时,会响应检测到不明确的绑定而抛出
	 * 
	 */
	@SuppressWarnings("serial")
	public static class AmbiguousBindingException extends RuntimeException {

		/**
		 * Construct a new AmbiguousBindingException with the specified message.
		 * <p>
		 *  使用指定的消息构造一个新的AmbiguousBindingException
		 * 
		 * @param msg the detail message
		 */
		public AmbiguousBindingException(String msg) {
			super(msg);
		}
	}

}
