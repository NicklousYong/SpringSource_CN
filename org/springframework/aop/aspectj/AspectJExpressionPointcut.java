/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.reflect.ReflectionWorld.ReflectionWorldException;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.FuzzyBoolean;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.aspectj.weaver.tools.ShadowMatch;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.AbstractExpressionPointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Spring {@link org.springframework.aop.Pointcut} implementation
 * that uses the AspectJ weaver to evaluate a pointcut expression.
 *
 * <p>The pointcut expression value is an AspectJ expression. This can
 * reference other pointcuts and use composition and other operations.
 *
 * <p>Naturally, as this is to be processed by Spring AOP's proxy-based model,
 * only method execution pointcuts are supported.
 *
 * <p>
 *  Spring {@link orgspringframeworkaopPointcut}实现,它使用AspectJ weaver来评估切入点表达式
 * 
 * <p>切入点表达式值是一个AspectJ表达式它可以引用其他切入点并使用组合和其他操作
 * 
 *  自然地,由于这是由Spring AOP的基于代理的模型来处理的,所以只支持方法执行切入点
 * 
 * 
 * @author Rob Harrop
 * @author Adrian Colyer
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Dave Syer
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJExpressionPointcut extends AbstractExpressionPointcut
		implements ClassFilter, IntroductionAwareMethodMatcher, BeanFactoryAware {

	private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

	static {
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
	}


	private static final Log logger = LogFactory.getLog(AspectJExpressionPointcut.class);

	private Class<?> pointcutDeclarationScope;

	private String[] pointcutParameterNames = new String[0];

	private Class<?>[] pointcutParameterTypes = new Class<?>[0];

	private BeanFactory beanFactory;

	private transient ClassLoader pointcutClassLoader;

	private transient PointcutExpression pointcutExpression;

	private transient Map<Method, ShadowMatch> shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);


	/**
	 * Create a new default AspectJExpressionPointcut.
	 * <p>
	 *  创建一个新的默认AspectJExpressionPointcut
	 * 
	 */
	public AspectJExpressionPointcut() {
	}

	/**
	 * Create a new AspectJExpressionPointcut with the given settings.
	 * <p>
	 *  使用给定的设置创建一个新的AspectJExpressionPointcut
	 * 
	 * 
	 * @param declarationScope the declaration scope for the pointcut
	 * @param paramNames the parameter names for the pointcut
	 * @param paramTypes the parameter types for the pointcut
	 */
	public AspectJExpressionPointcut(Class<?> declarationScope, String[] paramNames, Class<?>[] paramTypes) {
		this.pointcutDeclarationScope = declarationScope;
		if (paramNames.length != paramTypes.length) {
			throw new IllegalStateException(
					"Number of pointcut parameter names must match number of pointcut parameter types");
		}
		this.pointcutParameterNames = paramNames;
		this.pointcutParameterTypes = paramTypes;
	}


	/**
	 * Set the declaration scope for the pointcut.
	 * <p>
	 *  设置切入点的声明范围
	 * 
	 */
	public void setPointcutDeclarationScope(Class<?> pointcutDeclarationScope) {
		this.pointcutDeclarationScope = pointcutDeclarationScope;
	}

	/**
	 * Set the parameter names for the pointcut.
	 * <p>
	 *  设置切入点的参数名称
	 * 
	 */
	public void setParameterNames(String... names) {
		this.pointcutParameterNames = names;
	}

	/**
	 * Set the parameter types for the pointcut.
	 * <p>
	 *  设置切入点的参数类型
	 * 
	 */
	public void setParameterTypes(Class<?>... types) {
		this.pointcutParameterTypes = types;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	@Override
	public ClassFilter getClassFilter() {
		checkReadyToMatch();
		return this;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		checkReadyToMatch();
		return this;
	}


	/**
	 * Check whether this pointcut is ready to match,
	 * lazily building the underlying AspectJ pointcut expression.
	 * <p>
	 *  检查这个切入点是否准备好匹配,懒洋洋地构建基础的AspectJ切入点表达式
	 * 
	 */
	private void checkReadyToMatch() {
		if (getExpression() == null) {
			throw new IllegalStateException("Must set property 'expression' before attempting to match");
		}
		if (this.pointcutExpression == null) {
			this.pointcutClassLoader = (this.beanFactory instanceof ConfigurableBeanFactory ?
					((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() :
					ClassUtils.getDefaultClassLoader());
			this.pointcutExpression = buildPointcutExpression(this.pointcutClassLoader);
		}
	}

	/**
	 * Build the underlying AspectJ pointcut expression.
	 * <p>
	 *  构建底层的AspectJ切入点表达式
	 * 
	 */
	private PointcutExpression buildPointcutExpression(ClassLoader classLoader) {
		PointcutParser parser = initializePointcutParser(classLoader);
		PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
		for (int i = 0; i < pointcutParameters.length; i++) {
			pointcutParameters[i] = parser.createPointcutParameter(
					this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
		}
		return parser.parsePointcutExpression(replaceBooleanOperators(getExpression()),
				this.pointcutDeclarationScope, pointcutParameters);
	}

	/**
	 * Initialize the underlying AspectJ pointcut parser.
	 * <p>
	 *  初始化底层的AspectJ切入点解析器
	 * 
	 */
	private PointcutParser initializePointcutParser(ClassLoader cl) {
		PointcutParser parser = PointcutParser
				.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
						SUPPORTED_PRIMITIVES, cl);
		parser.registerPointcutDesignatorHandler(new BeanNamePointcutDesignatorHandler());
		return parser;
	}


	/**
	 * If a pointcut expression has been specified in XML, the user cannot
	 * write {@code and} as "&&" (though &amp;&amp; will work).
	 * We also allow {@code and} between two pointcut sub-expressions.
	 * <p>This method converts back to {@code &&} for the AspectJ pointcut parser.
	 * <p>
	 * 如果在XML中指定了切入点表达式,用户不能将{@code and}写为"&&"(虽然&amp;&amp;&amp;&amp;&amp;&amp;&amp;&amp;&amp;&amp;)将允许{@code和}
	 * 两个切入点子表达式<p>方法转换为AspectJ切入点解析器的{@code &&}。
	 * 
	 */
	private String replaceBooleanOperators(String pcExpr) {
		String result = StringUtils.replace(pcExpr, " and ", " && ");
		result = StringUtils.replace(result, " or ", " || ");
		result = StringUtils.replace(result, " not ", " ! ");
		return result;
	}


	/**
	 * Return the underlying AspectJ pointcut expression.
	 * <p>
	 *  返回底层的AspectJ切入点表达式
	 * 
	 */
	public PointcutExpression getPointcutExpression() {
		checkReadyToMatch();
		return this.pointcutExpression;
	}

	@Override
	public boolean matches(Class<?> targetClass) {
		checkReadyToMatch();
		try {
			try {
				return this.pointcutExpression.couldMatchJoinPointsInType(targetClass);
			}
			catch (ReflectionWorldException ex) {
				logger.debug("PointcutExpression matching rejected target class - trying fallback expression", ex);
				// Actually this is still a "maybe" - treat the pointcut as dynamic if we don't know enough yet
				PointcutExpression fallbackExpression = getFallbackPointcutExpression(targetClass);
				if (fallbackExpression != null) {
					return fallbackExpression.couldMatchJoinPointsInType(targetClass);
				}
			}
		}
		catch (BCException ex) {
			logger.debug("PointcutExpression matching rejected target class", ex);
		}
		return false;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, boolean beanHasIntroductions) {
		checkReadyToMatch();
		Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
		ShadowMatch shadowMatch = getShadowMatch(targetMethod, method);

		// Special handling for this, target, @this, @target, @annotation
		// in Spring - we can optimize since we know we have exactly this class,
		// and there will never be matching subclass at runtime.
		if (shadowMatch.alwaysMatches()) {
			return true;
		}
		else if (shadowMatch.neverMatches()) {
			return false;
		}
		else {
			// the maybe case
			if (beanHasIntroductions) {
				return true;
			}
			// A match test returned maybe - if there are any subtype sensitive variables
			// involved in the test (this, target, at_this, at_target, at_annotation) then
			// we say this is not a match as in Spring there will never be a different
			// runtime subtype.
			RuntimeTestWalker walker = getRuntimeTestWalker(shadowMatch);
			return (!walker.testsSubtypeSensitiveVars() || walker.testTargetInstanceOfResidue(targetClass));
		}
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return matches(method, targetClass, false);
	}

	@Override
	public boolean isRuntime() {
		checkReadyToMatch();
		return this.pointcutExpression.mayNeedDynamicTest();
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object... args) {
		checkReadyToMatch();
		ShadowMatch shadowMatch = getShadowMatch(AopUtils.getMostSpecificMethod(method, targetClass), method);
		ShadowMatch originalShadowMatch = getShadowMatch(method, method);

		// Bind Spring AOP proxy to AspectJ "this" and Spring AOP target to AspectJ target,
		// consistent with return of MethodInvocationProceedingJoinPoint
		ProxyMethodInvocation pmi = null;
		Object targetObject = null;
		Object thisObject = null;
		try {
			MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
			targetObject = mi.getThis();
			if (!(mi instanceof ProxyMethodInvocation)) {
				throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
			}
			pmi = (ProxyMethodInvocation) mi;
			thisObject = pmi.getProxy();
		}
		catch (IllegalStateException ex) {
			// No current invocation...
			// TODO: Should we really proceed here?
			if (logger.isDebugEnabled()) {
				logger.debug("Could not access current invocation - matching with limited context: " + ex);
			}
		}

		try {
			JoinPointMatch joinPointMatch = shadowMatch.matchesJoinPoint(thisObject, targetObject, args);

			/*
			 * Do a final check to see if any this(TYPE) kind of residue match. For
			 * this purpose, we use the original method's (proxy method's) shadow to
			 * ensure that 'this' is correctly checked against. Without this check,
			 * we get incorrect match on this(TYPE) where TYPE matches the target
			 * type but not 'this' (as would be the case of JDK dynamic proxies).
			 * <p>See SPR-2979 for the original bug.
			 * <p>
			 *  进行最后检查以查看是否有这种(TYPE)类型的残差匹配为此目的,我们使用原始方法(代理方法)的阴影来确保"这个"被正确检查没有这个检查,我们得到不正确的匹配(TYPE)其中TYPE与目标类型匹配但不是
			 * "this"(就像JDK动态代理的情况)<p>请参见SPR-2979原始错误。
			 * 
			 */
			if (pmi != null) {  // there is a current invocation
				RuntimeTestWalker originalMethodResidueTest = getRuntimeTestWalker(originalShadowMatch);
				if (!originalMethodResidueTest.testThisInstanceOfResidue(thisObject.getClass())) {
					return false;
				}
				if (joinPointMatch.matches()) {
					bindParameters(pmi, joinPointMatch);
				}
			}

			return joinPointMatch.matches();
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to evaluate join point for arguments " + Arrays.asList(args) +
						" - falling back to non-match", ex);
			}
			return false;
		}
	}

	protected String getCurrentProxiedBeanName() {
		return ProxyCreationContext.getCurrentProxiedBeanName();
	}


	/**
	 * Get a new pointcut expression based on a target class's loader rather than the default.
	 * <p>
	 *  根据目标类的加载器而不是默认值获取新的切入点表达式
	 * 
	 */
	private PointcutExpression getFallbackPointcutExpression(Class<?> targetClass) {
		try {
			ClassLoader classLoader = targetClass.getClassLoader();
			if (classLoader != null && classLoader != this.pointcutClassLoader) {
				return buildPointcutExpression(classLoader);
			}
		}
		catch (Throwable ex) {
			logger.debug("Failed to create fallback PointcutExpression", ex);
		}
		return null;
	}

	private RuntimeTestWalker getRuntimeTestWalker(ShadowMatch shadowMatch) {
		if (shadowMatch instanceof DefensiveShadowMatch) {
			return new RuntimeTestWalker(((DefensiveShadowMatch) shadowMatch).primary);
		}
		return new RuntimeTestWalker(shadowMatch);
	}

	private void bindParameters(ProxyMethodInvocation invocation, JoinPointMatch jpm) {
		// Note: Can't use JoinPointMatch.getClass().getName() as the key, since
		// Spring AOP does all the matching at a join point, and then all the invocations
		// under this scenario, if we just use JoinPointMatch as the key, then
		// 'last man wins' which is not what we want at all.
		// Using the expression is guaranteed to be safe, since 2 identical expressions
		// are guaranteed to bind in exactly the same way.
		invocation.setUserAttribute(getExpression(), jpm);
	}

	private ShadowMatch getShadowMatch(Method targetMethod, Method originalMethod) {
		// Avoid lock contention for known Methods through concurrent access...
		ShadowMatch shadowMatch = this.shadowMatchCache.get(targetMethod);
		if (shadowMatch == null) {
			synchronized (this.shadowMatchCache) {
				// Not found - now check again with full lock...
				PointcutExpression fallbackExpression = null;
				Method methodToMatch = targetMethod;
				shadowMatch = this.shadowMatchCache.get(targetMethod);
				if (shadowMatch == null) {
					try {
						shadowMatch = this.pointcutExpression.matchesMethodExecution(methodToMatch);
					}
					catch (ReflectionWorldException ex) {
						// Failed to introspect target method, probably because it has been loaded
						// in a special ClassLoader. Let's try the declaring ClassLoader instead...
						try {
							fallbackExpression = getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
							if (fallbackExpression != null) {
								shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
							}
						}
						catch (ReflectionWorldException ex2) {
							fallbackExpression = null;
						}
					}
					if (shadowMatch == null && targetMethod != originalMethod) {
						methodToMatch = originalMethod;
						try {
							shadowMatch = this.pointcutExpression.matchesMethodExecution(methodToMatch);
						}
						catch (ReflectionWorldException ex3) {
							// Could neither introspect the target class nor the proxy class ->
							// let's try the original method's declaring class before we give up...
							try {
								fallbackExpression = getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
								if (fallbackExpression != null) {
									shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
								}
							}
							catch (ReflectionWorldException ex4) {
								fallbackExpression = null;
							}
						}
					}
					if (shadowMatch == null) {
						shadowMatch = new ShadowMatchImpl(org.aspectj.util.FuzzyBoolean.NO, null, null, null);
					}
					else if (shadowMatch.maybeMatches() && fallbackExpression != null) {
						shadowMatch = new DefensiveShadowMatch(shadowMatch,
								fallbackExpression.matchesMethodExecution(methodToMatch));
					}
					this.shadowMatchCache.put(targetMethod, shadowMatch);
				}
			}
		}
		return shadowMatch;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AspectJExpressionPointcut)) {
			return false;
		}
		AspectJExpressionPointcut otherPc = (AspectJExpressionPointcut) other;
		return ObjectUtils.nullSafeEquals(this.getExpression(), otherPc.getExpression()) &&
				ObjectUtils.nullSafeEquals(this.pointcutDeclarationScope, otherPc.pointcutDeclarationScope) &&
				ObjectUtils.nullSafeEquals(this.pointcutParameterNames, otherPc.pointcutParameterNames) &&
				ObjectUtils.nullSafeEquals(this.pointcutParameterTypes, otherPc.pointcutParameterTypes);
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(this.getExpression());
		hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutDeclarationScope);
		hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutParameterNames);
		hashCode = 31 * hashCode + ObjectUtils.nullSafeHashCode(this.pointcutParameterTypes);
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AspectJExpressionPointcut: ");
		if (this.pointcutParameterNames != null && this.pointcutParameterTypes != null) {
			sb.append("(");
			for (int i = 0; i < this.pointcutParameterTypes.length; i++) {
				sb.append(this.pointcutParameterTypes[i].getName());
				sb.append(" ");
				sb.append(this.pointcutParameterNames[i]);
				if ((i+1) < this.pointcutParameterTypes.length) {
					sb.append(", ");
				}
			}
			sb.append(")");
		}
		sb.append(" ");
		if (getExpression() != null) {
			sb.append(getExpression());
		}
		else {
			sb.append("<pointcut expression not set>");
		}
		return sb.toString();
	}


	/**
	 * Handler for the Spring-specific {@code bean()} pointcut designator
	 * extension to AspectJ.
	 * <p>This handler must be added to each pointcut object that needs to
	 * handle the {@code bean()} PCD. Matching context is obtained
	 * automatically by examining a thread local variable and therefore a matching
	 * context need not be set on the pointcut.
	 * <p>
	 * 适用于Spring特定的Spring的特定{@code bean()}指针代码扩展的处理程序</p>此处理程序必须添加到需要处理{@code bean()}的每个切入点对象PCD匹配上下文通过检查自动获
	 * 取线程局部变量因此不需要在切入点上设置匹配上下文。
	 * 
	 */
	private class BeanNamePointcutDesignatorHandler implements PointcutDesignatorHandler {

		private static final String BEAN_DESIGNATOR_NAME = "bean";

		@Override
		public String getDesignatorName() {
			return BEAN_DESIGNATOR_NAME;
		}

		@Override
		public ContextBasedMatcher parse(String expression) {
			return new BeanNameContextMatcher(expression);
		}
	}


	/**
	 * Matcher class for the BeanNamePointcutDesignatorHandler.
	 * <p>Dynamic match tests for this matcher always return true,
	 * since the matching decision is made at the proxy creation time.
	 * For static match tests, this matcher abstains to allow the overall
	 * pointcut to match even when negation is used with the bean() pointcut.
	 * <p>
	 *  BeanNamePointcutDesignatorHandler的匹配器类<p>该匹配器的动态匹配测试总是返回true,因为匹配决策是在代理创建时间进行的。
	 * 对于静态匹配测试,该匹配器放弃允许整体切入点匹配,即使否定与bean()切入点。
	 */
	private class BeanNameContextMatcher implements ContextBasedMatcher {

		private final NamePattern expressionPattern;

		public BeanNameContextMatcher(String expression) {
			this.expressionPattern = new NamePattern(expression);
		}

		@Override
		@SuppressWarnings("rawtypes")
		@Deprecated
		public boolean couldMatchJoinPointsInType(Class someClass) {
			return (contextMatch(someClass) == FuzzyBoolean.YES);
		}

		@Override
		@SuppressWarnings("rawtypes")
		@Deprecated
		public boolean couldMatchJoinPointsInType(Class someClass, MatchingContext context) {
			return (contextMatch(someClass) == FuzzyBoolean.YES);
		}

		@Override
		public boolean matchesDynamically(MatchingContext context) {
			return true;
		}

		@Override
		public FuzzyBoolean matchesStatically(MatchingContext context) {
			return contextMatch(null);
		}

		@Override
		public boolean mayNeedDynamicTest() {
			return false;
		}

		private FuzzyBoolean contextMatch(Class<?> targetType) {
			String advisedBeanName = getCurrentProxiedBeanName();
			if (advisedBeanName == null) {  // no proxy creation in progress
				// abstain; can't return YES, since that will make pointcut with negation fail
				return FuzzyBoolean.MAYBE;
			}
			if (BeanFactoryUtils.isGeneratedBeanName(advisedBeanName)) {
				return FuzzyBoolean.NO;
			}
			if (targetType != null) {
				boolean isFactory = FactoryBean.class.isAssignableFrom(targetType);
				return FuzzyBoolean.fromBoolean(
						matchesBeanName(isFactory ? BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName : advisedBeanName));
			}
			else {
				return FuzzyBoolean.fromBoolean(matchesBeanName(advisedBeanName) ||
						matchesBeanName(BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName));
			}
		}

		private boolean matchesBeanName(String advisedBeanName) {
			if (this.expressionPattern.matches(advisedBeanName)) {
				return true;
			}
			if (beanFactory != null) {
				String[] aliases = beanFactory.getAliases(advisedBeanName);
				for (String alias : aliases) {
					if (this.expressionPattern.matches(alias)) {
						return true;
					}
				}
			}
			return false;
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization, just initialize state after deserialization.
		ois.defaultReadObject();

		// Initialize transient fields.
		// pointcutExpression will be initialized lazily by checkReadyToMatch()
		this.shadowMatchCache = new ConcurrentHashMap<Method, ShadowMatch>(32);
	}


	private static class DefensiveShadowMatch implements ShadowMatch {

		private final ShadowMatch primary;

		private final ShadowMatch other;

		public DefensiveShadowMatch(ShadowMatch primary, ShadowMatch other) {
			this.primary = primary;
			this.other = other;
		}

		@Override
		public boolean alwaysMatches() {
			return this.primary.alwaysMatches();
		}

		@Override
		public boolean maybeMatches() {
			return this.primary.maybeMatches();
		}

		@Override
		public boolean neverMatches() {
			return this.primary.neverMatches();
		}

		@Override
		public JoinPointMatch matchesJoinPoint(Object thisObject, Object targetObject, Object[] args) {
			try {
				return this.primary.matchesJoinPoint(thisObject, targetObject, args);
			}
			catch (ReflectionWorldException ex) {
				return this.other.matchesJoinPoint(thisObject, targetObject, args);
			}
		}

		@Override
		public void setMatchingContext(MatchingContext aMatchContext) {
			this.primary.setMatchingContext(aMatchContext);
			this.other.setMatchingContext(aMatchContext);
		}
	}

}
