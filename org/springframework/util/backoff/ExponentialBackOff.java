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

package org.springframework.util.backoff;

/**
 * Implementation of {@link BackOff} that increases the back off period for each
 * retry attempt. When the interval has reached the {@link #setMaxInterval(long)
 * max interval}, it is no longer increased. Stops retrying once the
 * {@link #setMaxElapsedTime(long) max elapsed time} has been reached.
 *
 * <p>Example: The default interval is {@value #DEFAULT_INITIAL_INTERVAL} ms,
 * the default multiplier is {@value #DEFAULT_MULTIPLIER}, and the default max
 * interval is {@value #DEFAULT_MAX_INTERVAL}. For 10 attempts the sequence will be
 * as follows:
 *
 * <pre>
 * request#     back off
 *
 *  1              2000
 *  2              3000
 *  3              4500
 *  4              6750
 *  5             10125
 *  6             15187
 *  7             22780
 *  8             30000
 *  9             30000
 * 10             30000
 * </pre>
 *
 * <p>Note that the default max elapsed time is {@link Long#MAX_VALUE}. Use
 * {@link #setMaxElapsedTime(long)} to limit the maximum length of time
 * that an instance should accumulate before returning
 * {@link BackOffExecution#STOP}.
 *
 * <p>
 * 实施增加每次重试尝试的退出时间的{@link BackOff}当间隔达到{@link #setMaxInterval(long)max interval}时,不再增加停止一次{@link #setMaxElapsedTime(长)最大经过时间}
 * 已达到。
 * 
 *  <p>示例：默认间隔为{@value #DEFAULT_INITIAL_INTERVAL} ms,默认乘数为{@value #DEFAULT_MULTIPLIER},默认最大间隔为{@value #DEFAULT_MAX_INTERVAL}
 * 对于10次尝试,序列如下：。
 * 
 * <pre>
 *  请求#退出
 * 
 *  1 2000 2 3000 3 4500 4 6750 5 10125 6 15187 7 22780 8 30000 9 30000 10 30000
 * </pre>
 * 
 * <p>请注意,默认的最大经过时间为{@link Long#MAX_VALUE}使用{@link #setMaxElapsedTime(long)}限制实例在返回之前应该累积的最大时间长度{@link BackOffExecution#STOP}
 * 。
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public class ExponentialBackOff implements BackOff {

	/**
	 * The default initial interval.
	 * <p>
	 *  默认的初始间隔
	 * 
	 */
	public static final long DEFAULT_INITIAL_INTERVAL = 2000L;

	/**
	 * The default multiplier (increases the interval by 50%).
	 * <p>
	 *  默认乘数(将间隔增加50％)
	 * 
	 */
	public static final double DEFAULT_MULTIPLIER = 1.5;

	/**
	 * The default maximum back off time.
	 * <p>
	 *  默认最大退出时间
	 * 
	 */
	public static final long DEFAULT_MAX_INTERVAL = 30000L;

	/**
	 * The default maximum elapsed time.
	 * <p>
	 *  默认最大经过时间
	 * 
	 */
	public static final long DEFAULT_MAX_ELAPSED_TIME = Long.MAX_VALUE;


	private long initialInterval = DEFAULT_INITIAL_INTERVAL;

	private double multiplier = DEFAULT_MULTIPLIER;

	private long maxInterval = DEFAULT_MAX_INTERVAL;

	private long maxElapsedTime = DEFAULT_MAX_ELAPSED_TIME;


	/**
	 * Create an instance with the default settings.
	 * <p>
	 *  使用默认设置创建一个实例
	 * 
	 * 
	 * @see #DEFAULT_INITIAL_INTERVAL
	 * @see #DEFAULT_MULTIPLIER
	 * @see #DEFAULT_MAX_INTERVAL
	 * @see #DEFAULT_MAX_ELAPSED_TIME
	 */
	public ExponentialBackOff() {
	}

	/**
	 * Create an instance with the supplied settings.
	 * <p>
	 *  使用提供的设置创建一个实例
	 * 
	 * 
	 * @param initialInterval the initial interval in milliseconds
	 * @param multiplier the multiplier (should be greater than or equal to 1)
	 */
	public ExponentialBackOff(long initialInterval, double multiplier) {
		checkMultiplier(multiplier);
		this.initialInterval = initialInterval;
		this.multiplier = multiplier;
	}


	/**
	 * The initial interval in milliseconds.
	 * <p>
	 *  初始间隔(以毫秒为单位)
	 * 
	 */
	public void setInitialInterval(long initialInterval) {
		this.initialInterval = initialInterval;
	}

	/**
	 * Return the initial interval in milliseconds.
	 * <p>
	 *  返回初始间隔(以毫秒为单位)
	 * 
	 */
	public long getInitialInterval() {
		return initialInterval;
	}

	/**
	 * The value to multiply the current interval by for each retry attempt.
	 * <p>
	 *  为每次重试尝试乘以当前间隔的值
	 * 
	 */
	public void setMultiplier(double multiplier) {
		checkMultiplier(multiplier);
		this.multiplier = multiplier;
	}

	/**
	 * Return the value to multiply the current interval by for each retry attempt.
	 * <p>
	 *  为每个重试尝试返回值乘以当前间隔
	 * 
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/**
	 * The maximum back off time.
	 * <p>
	 *  最大回退时间
	 * 
	 */
	public void setMaxInterval(long maxInterval) {
		this.maxInterval = maxInterval;
	}

	/**
	 * Return the maximum back off time.
	 * <p>
	 *  返回最大回退时间
	 * 
	 */
	public long getMaxInterval() {
		return maxInterval;
	}

	/**
	 * The maximum elapsed time in milliseconds after which a call to
	 * {@link BackOffExecution#nextBackOff()} returns {@link BackOffExecution#STOP}.
	 * <p>
	 * 调用{@link BackOffExecution#nextBackOff()}返回的最长时间(以毫秒为单位){@link BackOffExecution#STOP}
	 * 
	 */
	public void setMaxElapsedTime(long maxElapsedTime) {
		this.maxElapsedTime = maxElapsedTime;
	}

	/**
	 * Return the maximum elapsed time in milliseconds after which a call to
	 * {@link BackOffExecution#nextBackOff()} returns {@link BackOffExecution#STOP}.
	 * <p>
	 *  返回最大经过时间(以毫秒为单位),之后调用{@link BackOffExecution#nextBackOff()}返回{@link BackOffExecution#STOP}
	 */
	public long getMaxElapsedTime() {
		return maxElapsedTime;
	}

	@Override
	public BackOffExecution start() {
		return new ExponentialBackOffExecution();
	}

	private void checkMultiplier(double multiplier) {
		if (multiplier < 1) {
			throw new IllegalArgumentException("Invalid multiplier '" + multiplier + "'. Should be equal" +
					"or higher than 1. A multiplier of 1 is equivalent to a fixed interval");
		}
	}


	private class ExponentialBackOffExecution implements BackOffExecution {

		private long currentInterval = -1;

		private long currentElapsedTime = 0;

		@Override
		public long nextBackOff() {
			if (this.currentElapsedTime >= maxElapsedTime) {
				return STOP;
			}

			long nextInterval = computeNextInterval();
			this.currentElapsedTime += nextInterval;
			return nextInterval;
		}

		private long computeNextInterval() {
			long maxInterval = getMaxInterval();
			if (this.currentInterval >= maxInterval) {
				return maxInterval;
			}
			else if (this.currentInterval < 0) {
			 	long initialInterval = getInitialInterval();
				this.currentInterval = (initialInterval < maxInterval
						? initialInterval : maxInterval);
			}
			else {
				this.currentInterval = multiplyInterval(maxInterval);
			}
			return this.currentInterval;
		}

		private long multiplyInterval(long maxInterval) {
			long i = this.currentInterval;
			i *= getMultiplier();
			return (i > maxInterval ? maxInterval : i);
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("ExponentialBackOff{");
			sb.append("currentInterval=").append(this.currentInterval < 0 ? "n/a" : this.currentInterval + "ms");
			sb.append(", multiplier=").append(getMultiplier());
			sb.append('}');
			return sb.toString();
		}
	}

}
