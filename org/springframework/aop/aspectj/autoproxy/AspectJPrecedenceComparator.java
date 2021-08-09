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

package org.springframework.aop.aspectj.autoproxy;

import java.util.Comparator;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJAopUtils;
import org.springframework.aop.aspectj.AspectJPrecedenceInformation;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/**
 * Orders AspectJ advice/advisors by precedence (<i>not</i> invocation order).
 *
 * <p>Given two pieces of advice, {@code a} and {@code b}:
 * <ul>
 * <li>if {@code a} and {@code b} are defined in different aspects, then the advice
 * in the aspect with the lowest order value has the highest precedence</li>
 * <li>if {@code a} and {@code b} are defined in the same aspect, then if one of
 * {@code a} or {@code b} is a form of after advice, then the advice declared last
 * in the aspect has the highest precedence. If neither {@code a} nor {@code b} is
 * a form of after advice, then the advice declared first in the aspect has the
 * highest precedence.</li>
 * </ul>
 *
 * <p>Important: Note that unlike a normal comparator a return of 0 means
 * we don't care about the ordering, not that the two elements must be sorted
 * identically. Used with AspectJ PartialOrder class.
 *
 * <p>
 *  按优先级(<i>不</i>调用顺序)订购AspectJ建议/顾问
 * 
 *  给出两条建议,{@code a}和{@code b}：
 * <ul>
 * 如果{@code a}和{@code b}在不同方面进行了定义,那么{@code a}和{@code b}的定义如果{@code a}和{@code b}在同一方面定义,那么如果{@code a}或{@code b}
 * 之一是一个后面的建议形式,那么在该方面最后宣告的建议是最高优先级如果{@代码a}或{@code b}是一种后建议的形式,那么在该方面首先声明的建议具有最高优先级</li>。
 * </ul>
 * 
 * 
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @since 2.0
 */
class AspectJPrecedenceComparator implements Comparator<Advisor> {

	private static final int HIGHER_PRECEDENCE = -1;

	private static final int SAME_PRECEDENCE = 0;

	private static final int LOWER_PRECEDENCE = 1;


	private final Comparator<? super Advisor> advisorComparator;


	/**
	 * Create a default AspectJPrecedenceComparator.
	 * <p>
	 *  重要提示：请注意,与普通比较器不同,返回值0表示我们不关心排序,而不是两个元素必须进行相同的排序与AspectJ PartialOrder类一起使用
	 * 
	 */
	public AspectJPrecedenceComparator() {
		this.advisorComparator = AnnotationAwareOrderComparator.INSTANCE;
	}

	/**
	 * Create a AspectJPrecedenceComparator, using the given Comparator
	 * for comparing {@link org.springframework.aop.Advisor} instances.
	 * <p>
	 *  创建一个默认的AspectJPrecedenceComparator
	 * 
	 * 
	 * @param advisorComparator the Comparator to use for Advisors
	 */
	public AspectJPrecedenceComparator(Comparator<? super Advisor> advisorComparator) {
		Assert.notNull(advisorComparator, "Advisor comparator must not be null");
		this.advisorComparator = advisorComparator;
	}


	@Override
	public int compare(Advisor o1, Advisor o2) {
		int advisorPrecedence = this.advisorComparator.compare(o1, o2);
		if (advisorPrecedence == SAME_PRECEDENCE && declaredInSameAspect(o1, o2)) {
			advisorPrecedence = comparePrecedenceWithinAspect(o1, o2);
		}
		return advisorPrecedence;
	}

	private int comparePrecedenceWithinAspect(Advisor advisor1, Advisor advisor2) {
		boolean oneOrOtherIsAfterAdvice =
				(AspectJAopUtils.isAfterAdvice(advisor1) || AspectJAopUtils.isAfterAdvice(advisor2));
		int adviceDeclarationOrderDelta = getAspectDeclarationOrder(advisor1) - getAspectDeclarationOrder(advisor2);

		if (oneOrOtherIsAfterAdvice) {
			// the advice declared last has higher precedence
			if (adviceDeclarationOrderDelta < 0) {
				// advice1 was declared before advice2
				// so advice1 has lower precedence
				return LOWER_PRECEDENCE;
			}
			else if (adviceDeclarationOrderDelta == 0) {
				return SAME_PRECEDENCE;
			}
			else {
				return HIGHER_PRECEDENCE;
			}
		}
		else {
			// the advice declared first has higher precedence
			if (adviceDeclarationOrderDelta < 0) {
				// advice1 was declared before advice2
				// so advice1 has higher precedence
				return HIGHER_PRECEDENCE;
			}
			else if (adviceDeclarationOrderDelta == 0) {
				return SAME_PRECEDENCE;
			}
			else {
				return LOWER_PRECEDENCE;
			}
		}
	}

	private boolean declaredInSameAspect(Advisor advisor1, Advisor advisor2) {
		return (hasAspectName(advisor1) && hasAspectName(advisor2) &&
				getAspectName(advisor1).equals(getAspectName(advisor2)));
	}

	private boolean hasAspectName(Advisor anAdvisor) {
		return (anAdvisor instanceof AspectJPrecedenceInformation ||
				anAdvisor.getAdvice() instanceof AspectJPrecedenceInformation);
	}

	// pre-condition is that hasAspectName returned true
	private String getAspectName(Advisor anAdvisor) {
		return AspectJAopUtils.getAspectJPrecedenceInformationFor(anAdvisor).getAspectName();
	}

	private int getAspectDeclarationOrder(Advisor anAdvisor) {
		AspectJPrecedenceInformation precedenceInfo =
			AspectJAopUtils.getAspectJPrecedenceInformationFor(anAdvisor);
		if (precedenceInfo != null) {
			return precedenceInfo.getDeclarationOrder();
		}
		else {
			return 0;
		}
	}

}
