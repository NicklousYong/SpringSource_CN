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

package org.springframework.scheduling;

import java.util.Date;

/**
 * Context object encapsulating last execution times and last completion time
 * of a given task.
 *
 * <p>
 *  上下文对象封装给定任务的最后执行时间和最后完成时间
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface TriggerContext {

	/**
	 * Return the last <i>scheduled</i> execution time of the task,
	 * or {@code null} if not scheduled before.
	 * <p>
	 *  返回任务的最后一个<i>执行时间,或者{@code null}(如果没有安排的话)
	 * 
	 */
	Date lastScheduledExecutionTime();

	/**
	 * Return the last <i>actual</i> execution time of the task,
	 * or {@code null} if not scheduled before.
	 * <p>
	 * 返回任务的最后一个<i>实际执行时间,否则返回{@code null}
	 * 
	 */
	Date lastActualExecutionTime();

	/**
	 * Return the last completion time of the task,
	 * or {@code null} if not scheduled before.
	 * <p>
	 *  返回任务的最后完成时间,或者如果没有安排,则返回{@code null}
	 */
	Date lastCompletionTime();

}
