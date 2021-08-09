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

package org.springframework.scheduling.support;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * {@link Trigger} implementation for cron expressions.
 * Wraps a {@link CronSequenceGenerator}.
 *
 * <p>
 *  cron表达式的{@link Trigger}实现包装一个{@link CronSequenceGenerator}
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 * @see CronSequenceGenerator
 */
public class CronTrigger implements Trigger {

	private final CronSequenceGenerator sequenceGenerator;


	/**
	 * Build a {@link CronTrigger} from the pattern provided in the default time zone.
	 * <p>
	 *  从默认时区中提供的模式构建{@link CronTrigger}
	 * 
	 * 
	 * @param expression a space-separated list of time fields, following cron
	 * expression conventions
	 */
	public CronTrigger(String expression) {
		this.sequenceGenerator = new CronSequenceGenerator(expression);
	}

	/**
	 * Build a {@link CronTrigger} from the pattern provided in the given time zone.
	 * <p>
	 * 从给定时区中提供的模式构建{@link CronTrigger}
	 * 
	 * 
	 * @param expression a space-separated list of time fields, following cron
	 * expression conventions
	 * @param timeZone a time zone in which the trigger times will be generated
	 */
	public CronTrigger(String expression, TimeZone timeZone) {
		this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
	}


	/**
	 * Return the cron pattern that this trigger has been built with.
	 * <p>
	 *  返回此触发器构建的cron模式
	 * 
	 */
	public String getExpression() {
		return this.sequenceGenerator.getExpression();
	}


	/**
	 * Determine the next execution time according to the given trigger context.
	 * <p>Next execution times are calculated based on the
	 * {@linkplain TriggerContext#lastCompletionTime completion time} of the
	 * previous execution; therefore, overlapping executions won't occur.
	 * <p>
	 *  根据给定的触发上下文确定下一个执行时间<p>下一个执行时间是基于上一次执行的{@linkplain TriggerContext#lastCompletionTime完成时间}计算的;因此,重复执行不
	 * 会发生。
	 */
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date date = triggerContext.lastCompletionTime();
		if (date != null) {
			Date scheduled = triggerContext.lastScheduledExecutionTime();
			if (scheduled != null && date.before(scheduled)) {
				// Previous task apparently executed too early...
				// Let's simply use the last calculated execution time then,
				// in order to prevent accidental re-fires in the same second.
				date = scheduled;
			}
		}
		else {
			date = new Date();
		}
		return this.sequenceGenerator.next(date);
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof CronTrigger &&
				this.sequenceGenerator.equals(((CronTrigger) other).sequenceGenerator)));
	}

	@Override
	public int hashCode() {
		return this.sequenceGenerator.hashCode();
	}

	@Override
	public String toString() {
		return this.sequenceGenerator.toString();
	}

}
