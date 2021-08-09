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

package org.springframework.aop.aspectj.annotation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PerClauseKind;

import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.support.ComposablePointcut;

/**
 * Metadata for an AspectJ aspect class, with an additional Spring AOP pointcut
 * for the per clause.
 *
 * <p>Uses AspectJ 5 AJType reflection API, so is only supported on Java 5.
 * Enables us to work with different AspectJ instantiation models such as
 * "singleton", "pertarget" and "perthis".
 *
 * <p>
 *  一个AspectJ aspect类的元数据,另外还有一个用于per子句的Spring AOP切入点
 * 
 * <p>使用AspectJ 5 AJType反射API,因此仅支持Java 5使我们能够使用不同的AspectJ实例化模型,例如"singleton","pertarget"和"perthis"
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.aop.aspectj.AspectJExpressionPointcut
 */
@SuppressWarnings("serial")
public class AspectMetadata implements Serializable {

	/**
	 * The name of this aspect as defined to Spring (the bean name) -
	 * allows us to determine if two pieces of advice come from the
	 * same aspect and hence their relative precedence.
	 * <p>
	 *  Spring定义的这个方面的名称(bean名称) - 允许我们确定两条建议是否来自同一方面,因此它们的相对优先级
	 * 
	 */
	private final String aspectName;

	/**
	 * The aspect class, stored separately for re-resolution of the
	 * corresponding AjType on deserialization.
	 * <p>
	 *  方面类,分别存储,用于重新解析反序列化时对应的AjType
	 * 
	 */
	private final Class<?> aspectClass;

	/**
	 * AspectJ reflection information (AspectJ 5 / Java 5 specific).
	 * Re-resolved on deserialization since it isn't serializable itself.
	 * <p>
	 *  AspectJ反射信息(AspectJ 5 / Java 5具体)重新解析反序列化,因为它本身不是可序列化的
	 * 
	 */
	private transient AjType<?> ajType;

	/**
	 * Spring AOP pointcut corresponding to the per clause of the
	 * aspect. Will be the Pointcut.TRUE canonical instance in the
	 * case of a singleton, otherwise an AspectJExpressionPointcut.
	 * <p>
	 *  Spring的AOP切入点对应于每个子句的方面将是PointcutTRUE规范的实例,在单例的情况下,否则一个AspectJExpressionPointcut
	 * 
	 */
	private final Pointcut perClausePointcut;


	/**
	 * Create a new AspectMetadata instance for the given aspect class.
	 * <p>
	 * 为给定的方面类创建一个新的AspectMetadata实例
	 * 
	 * 
	 * @param aspectClass the aspect class
	 * @param aspectName the name of the aspect
	 */
	public AspectMetadata(Class<?> aspectClass, String aspectName) {
		this.aspectName = aspectName;

		Class<?> currClass = aspectClass;
		AjType<?> ajType = null;
		while (currClass != Object.class) {
			AjType<?> ajTypeToCheck = AjTypeSystem.getAjType(currClass);
			if (ajTypeToCheck.isAspect()) {
				ajType = ajTypeToCheck;
				break;
			}
			currClass = currClass.getSuperclass();
		}
		if (ajType == null) {
			throw new IllegalArgumentException("Class '" + aspectClass.getName() + "' is not an @AspectJ aspect");
		}
		if (ajType.getDeclarePrecedence().length > 0) {
			throw new IllegalArgumentException("DeclarePrecendence not presently supported in Spring AOP");
		}
		this.aspectClass = ajType.getJavaClass();
		this.ajType = ajType;

		switch (this.ajType.getPerClause().getKind()) {
			case SINGLETON :
				this.perClausePointcut = Pointcut.TRUE;
				return;
			case PERTARGET : case PERTHIS :
				AspectJExpressionPointcut ajexp = new AspectJExpressionPointcut();
				ajexp.setLocation("@Aspect annotation on " + aspectClass.getName());
				ajexp.setExpression(findPerClause(aspectClass));
				this.perClausePointcut = ajexp;
				return;
			case PERTYPEWITHIN :
				// Works with a type pattern
				this.perClausePointcut = new ComposablePointcut(new TypePatternClassFilter(findPerClause(aspectClass)));
				return;
			default :
				throw new AopConfigException(
						"PerClause " + ajType.getPerClause().getKind() + " not supported by Spring AOP for " + aspectClass);
		}
	}

	/**
	 * Extract contents from String of form {@code pertarget(contents)}.
	 * <p>
	 *  从{@code pertarget(contents))形式的字符串中提取内容
	 * 
	 */
	private String findPerClause(Class<?> aspectClass) {
		// TODO when AspectJ provides this, we can remove this hack. Hence we don't
		// bother to make it elegant. Or efficient. Or robust :-)
		String str = aspectClass.getAnnotation(Aspect.class).value();
		str = str.substring(str.indexOf("(") + 1);
		str = str.substring(0, str.length() - 1);
		return str;
	}


	/**
	 * Return AspectJ reflection information.
	 * <p>
	 *  返回AspectJ反射信息
	 * 
	 */
	public AjType<?> getAjType() {
		return this.ajType;
	}

	/**
	 * Return the aspect class.
	 * <p>
	 *  返回方面类
	 * 
	 */
	public Class<?> getAspectClass() {
		return this.aspectClass;
	}

	/**
	 * Return the aspect class.
	 * <p>
	 *  返回方面类
	 * 
	 */
	public String getAspectName() {
		return this.aspectName;
	}

	/**
	 * Return a Spring pointcut expression for a singleton aspect.
	 * (e.g. {@code Pointcut.TRUE} if it's a singleton).
	 * <p>
	 *  为单例方面返回一个Spring切入点表达式(例如{@code PointcutTRUE},如果它是单例)
	 * 
	 */
	public Pointcut getPerClausePointcut() {
		return this.perClausePointcut;
	}

	/**
	 * Return whether the aspect is defined as "perthis" or "pertarget".
	 * <p>
	 *  返回该方面是否被定义为"perthis"或"pertarget"
	 * 
	 */
	public boolean isPerThisOrPerTarget() {
		PerClauseKind kind = getAjType().getPerClause().getKind();
		return (kind == PerClauseKind.PERTARGET || kind == PerClauseKind.PERTHIS);
	}

	/**
	 * Return whether the aspect is defined as "pertypewithin".
	 * <p>
	 *  返回该方面是否定义为"pertypewithin"
	 * 
	 */
	public boolean isPerTypeWithin() {
		PerClauseKind kind = getAjType().getPerClause().getKind();
		return (kind == PerClauseKind.PERTYPEWITHIN);
	}

	/**
	 * Return whether the aspect needs to be lazily instantiated.
	 * <p>
	 *  返回该方面是否需要懒散实例化
	 */
	public boolean isLazilyInstantiated() {
		return (isPerThisOrPerTarget() || isPerTypeWithin());
	}


	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		this.ajType = AjTypeSystem.getAjType(this.aspectClass);
	}

}
