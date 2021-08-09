/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.util;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple stop watch, allowing for timing of a number of tasks,
 * exposing total running time and running time for each named task.
 *
 * <p>Conceals use of {@code System.currentTimeMillis()}, improving the
 * readability of application code and reducing the likelihood of calculation errors.
 *
 * <p>Note that this object is not designed to be thread-safe and does not
 * use synchronization.
 *
 * <p>This class is normally used to verify performance during proof-of-concepts
 * and in development, rather than as part of production applications.
 *
 * <p>
 *  简单的停止监视,允许多个任务的计时,为每个命名任务暴露总运行时间和运行时间
 * 
 * <p>隐藏使用{@code SystemcurrentTimeMillis()},提高应用程序代码的可读性,并减少计算错误的可能性
 * 
 *  <p>请注意,此对象不是设计为线程安全的,不使用同步
 * 
 *  <p>此类通常用于在概念验证和开发过程中验证性能,而不是生产应用程序的一部分
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since May 2, 2001
 */
public class StopWatch {

	/**
	 * Identifier of this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them in log or console output.
	 * <p>
	 *  这个停止手表的标识当我们从多个停止手表输出并且需要在日志或控制台输出中区分它们时,方便
	 * 
	 */
	private final String id;

	private boolean keepTaskList = true;

	private final List<TaskInfo> taskList = new LinkedList<TaskInfo>();

	/** Start time of the current task */
	private long startTimeMillis;

	/** Is the stop watch currently running? */
	private boolean running;

	/** Name of the current task */
	private String currentTaskName;

	private TaskInfo lastTaskInfo;

	private int taskCount;

	/** Total running time */
	private long totalTimeMillis;


	/**
	 * Construct a new stop watch. Does not start any task.
	 * <p>
	 *  构建新的秒表不启动任何任务
	 * 
	 */
	public StopWatch() {
		this("");
	}

	/**
	 * Construct a new stop watch with the given id.
	 * Does not start any task.
	 * <p>
	 *  用给定的id构造一个新的秒表不启动任何任务
	 * 
	 * 
	 * @param id identifier for this stop watch.
	 * Handy when we have output from multiple stop watches
	 * and need to distinguish between them.
	 */
	public StopWatch(String id) {
		this.id = id;
	}


	/**
	 * Return the id of this stop watch, as specified on construction.
	 * <p>
	 *  返回这个停止手表的ID,如在施工中指定的
	 * 
	 * 
	 * @return the id (empty String by default)
	 * @since 4.2.2
	 * @see #StopWatch(String)
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Determine whether the TaskInfo array is built over time. Set this to
	 * "false" when using a StopWatch for millions of intervals, or the task
	 * info structure will consume excessive memory. Default is "true".
	 * <p>
	 * 确定TaskInfo数组是否随时间构建将此值设置为"false",当使用StopWatch数百万个间隔时,或者任务信息结构将消耗过多的内存默认值为"true"
	 * 
	 */
	public void setKeepTaskList(boolean keepTaskList) {
		this.keepTaskList = keepTaskList;
	}


	/**
	 * Start an unnamed task. The results are undefined if {@link #stop()}
	 * or timing methods are called without invoking this method.
	 * <p>
	 *  启动一个未命名的任务如果调用{@link #stop()}或定时方法而不调用此方法,则结果未定义
	 * 
	 * 
	 * @see #stop()
	 */
	public void start() throws IllegalStateException {
		start("");
	}

	/**
	 * Start a named task. The results are undefined if {@link #stop()}
	 * or timing methods are called without invoking this method.
	 * <p>
	 *  启动命名任务如果调用{@link #stop()}或时序方法而不调用此方法,则结果未定义
	 * 
	 * 
	 * @param taskName the name of the task to start
	 * @see #stop()
	 */
	public void start(String taskName) throws IllegalStateException {
		if (this.running) {
			throw new IllegalStateException("Can't start StopWatch: it's already running");
		}
		this.running = true;
		this.currentTaskName = taskName;
		this.startTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Stop the current task. The results are undefined if timing
	 * methods are called without invoking at least one pair
	 * {@code start()} / {@code stop()} methods.
	 * <p>
	 *  停止当前任务如果调用定时方法,而不调用至少一对{@code start()} / {@code stop()}方法,则结果是未定义的
	 * 
	 * 
	 * @see #start()
	 */
	public void stop() throws IllegalStateException {
		if (!this.running) {
			throw new IllegalStateException("Can't stop StopWatch: it's not running");
		}
		long lastTime = System.currentTimeMillis() - this.startTimeMillis;
		this.totalTimeMillis += lastTime;
		this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
		if (this.keepTaskList) {
			this.taskList.add(lastTaskInfo);
		}
		++this.taskCount;
		this.running = false;
		this.currentTaskName = null;
	}

	/**
	 * Return whether the stop watch is currently running.
	 * <p>
	 *  返回秒表是否正在运行
	 * 
	 * 
	 * @see #currentTaskName()
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Return the name of the currently running task, if any.
	 * <p>
	 *  返回当前正在运行的任务的名称(如果有)
	 * 
	 * 
	 * @since 4.2.2
	 * @see #isRunning()
	 */
	public String currentTaskName() {
		return this.currentTaskName;
	}


	/**
	 * Return the time taken by the last task.
	 * <p>
	 *  返回上一个任务所用的时间
	 * 
	 */
	public long getLastTaskTimeMillis() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeMillis();
	}

	/**
	 * Return the name of the last task.
	 * <p>
	 *  返回上一个任务的名称
	 * 
	 */
	public String getLastTaskName() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task name");
		}
		return this.lastTaskInfo.getTaskName();
	}

	/**
	 * Return the last task as a TaskInfo object.
	 * <p>
	 * 将最后一个任务作为TaskInfo对象返回
	 * 
	 */
	public TaskInfo getLastTaskInfo() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task info");
		}
		return this.lastTaskInfo;
	}


	/**
	 * Return the total time in milliseconds for all tasks.
	 * <p>
	 *  返回所有任务的总时间(以毫秒为单位)
	 * 
	 */
	public long getTotalTimeMillis() {
		return this.totalTimeMillis;
	}

	/**
	 * Return the total time in seconds for all tasks.
	 * <p>
	 *  返回所有任务的总时间(以秒为单位)
	 * 
	 */
	public double getTotalTimeSeconds() {
		return this.totalTimeMillis / 1000.0;
	}

	/**
	 * Return the number of tasks timed.
	 * <p>
	 *  返回任务计时数
	 * 
	 */
	public int getTaskCount() {
		return this.taskCount;
	}

	/**
	 * Return an array of the data for tasks performed.
	 * <p>
	 *  返回执行任务的数据数组
	 * 
	 */
	public TaskInfo[] getTaskInfo() {
		if (!this.keepTaskList) {
			throw new UnsupportedOperationException("Task info is not being kept!");
		}
		return this.taskList.toArray(new TaskInfo[this.taskList.size()]);
	}


	/**
	 * Return a short description of the total running time.
	 * <p>
	 *  返回总运行时间的简短描述
	 * 
	 */
	public String shortSummary() {
		return "StopWatch '" + getId() + "': running time (millis) = " + getTotalTimeMillis();
	}

	/**
	 * Return a string with a table describing all tasks performed.
	 * For custom reporting, call getTaskInfo() and use the task info directly.
	 * <p>
	 *  返回一个描述所有执行任务的表的字符串对于自定义报告,调用getTaskInfo()并直接使用任务信息
	 * 
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(shortSummary());
		sb.append('\n');
		if (!this.keepTaskList) {
			sb.append("No task info kept");
		}
		else {
			sb.append("-----------------------------------------\n");
			sb.append("ms     %     Task name\n");
			sb.append("-----------------------------------------\n");
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMinimumIntegerDigits(5);
			nf.setGroupingUsed(false);
			NumberFormat pf = NumberFormat.getPercentInstance();
			pf.setMinimumIntegerDigits(3);
			pf.setGroupingUsed(false);
			for (TaskInfo task : getTaskInfo()) {
				sb.append(nf.format(task.getTimeMillis())).append("  ");
				sb.append(pf.format(task.getTimeSeconds() / getTotalTimeSeconds())).append("  ");
				sb.append(task.getTaskName()).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Return an informative string describing all tasks performed
	 * For custom reporting, call {@code getTaskInfo()} and use the task info directly.
	 * <p>
	 *  返回描述执行的所有任务的信息字符串对于自定义报告,调用{@code getTaskInfo()}并直接使用任务信息
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(shortSummary());
		if (this.keepTaskList) {
			for (TaskInfo task : getTaskInfo()) {
				sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
				long percent = Math.round((100.0 * task.getTimeSeconds()) / getTotalTimeSeconds());
				sb.append(" = ").append(percent).append("%");
			}
		}
		else {
			sb.append("; no task info kept");
		}
		return sb.toString();
	}


	/**
	 * Inner class to hold data about one task executed within the stop watch.
	 * <p>
	 *  内部类保存关于在秒表内执行的一个任务的数据
	 * 
	 */
	public static final class TaskInfo {

		private final String taskName;

		private final long timeMillis;

		TaskInfo(String taskName, long timeMillis) {
			this.taskName = taskName;
			this.timeMillis = timeMillis;
		}

		/**
		 * Return the name of this task.
		 * <p>
		 *  返回此任务的名称
		 * 
		 */
		public String getTaskName() {
			return this.taskName;
		}

		/**
		 * Return the time in milliseconds this task took.
		 * <p>
		 *  返回此任务所需的时间(以毫秒为单位)
		 * 
		 */
		public long getTimeMillis() {
			return this.timeMillis;
		}

		/**
		 * Return the time in seconds this task took.
		 * <p>
		 *  返回此任务所需的时间
		 */
		public double getTimeSeconds() {
			return (this.timeMillis / 1000.0);
		}
	}

}
