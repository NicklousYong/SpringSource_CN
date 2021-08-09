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
 * A simple {@link BackOff} implementation that provides a fixed interval
 * between two attempts and a maximum number of retries.
 *
 * <p>
 *  一个简单的{@link BackOff}实现,提供了两次尝试之间的固定间隔和最大重试次数
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public class FixedBackOff implements BackOff {

	/**
	 * The default recovery interval: 5000 ms = 5 seconds.
	 * <p>
	 *  默认恢复间隔：5000 ms = 5秒
	 * 
	 */
	public static final long DEFAULT_INTERVAL = 5000;

	/**
	 * Constant value indicating an unlimited number of attempts.
	 * <p>
	 * 恒定值,表示无限次尝试
	 * 
	 */
	public static final long UNLIMITED_ATTEMPTS = Long.MAX_VALUE;

	private long interval = DEFAULT_INTERVAL;

	private long maxAttempts = UNLIMITED_ATTEMPTS;


	/**
	 * Create an instance with an interval of {@value #DEFAULT_INTERVAL}
	 * ms and an unlimited number of attempts.
	 * <p>
	 *  创建一个间隔为{@value #DEFAULT_INTERVAL} ms的实例,无限次尝试
	 * 
	 */
	public FixedBackOff() {
	}

	/**
	 * Create an instance.
	 * <p>
	 *  创建一个实例
	 * 
	 * 
	 * @param interval the interval between two attempts
	 * @param maxAttempts the maximum number of attempts
	 */
	public FixedBackOff(long interval, long maxAttempts) {
		this.interval = interval;
		this.maxAttempts = maxAttempts;
	}


	/**
	 * Set the interval between two attempts in milliseconds.
	 * <p>
	 *  设置两次尝试之间的间隔(以毫秒为单位)
	 * 
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * Return the interval between two attempts in milliseconds.
	 * <p>
	 *  以毫秒为单位返回两次尝试之间的间隔
	 * 
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Set the maximum number of attempts in milliseconds.
	 * <p>
	 *  设置最大尝试次数(以毫秒为单位)
	 * 
	 */
	public void setMaxAttempts(long maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Return the maximum number of attempts in milliseconds.
	 * <p>
	 *  以毫秒为单位返回最大尝试次数
	 */
	public long getMaxAttempts() {
		return maxAttempts;
	}

	@Override
	public BackOffExecution start() {
		return new FixedBackOffExecution();
	}


	private class FixedBackOffExecution implements BackOffExecution {

		private long currentAttempts = 0;

		@Override
		public long nextBackOff() {
			this.currentAttempts++;
			if (this.currentAttempts <= getMaxAttempts()) {
				return getInterval();
			}
			else {
				return STOP;
			}
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("FixedBackOff{");
			sb.append("interval=").append(FixedBackOff.this.interval);
			String attemptValue = (FixedBackOff.this.maxAttempts == Long.MAX_VALUE ?
					"unlimited" : String.valueOf(FixedBackOff.this.maxAttempts));
			sb.append(", currentAttempts=").append(this.currentAttempts);
			sb.append(", maxAttempts=").append(attemptValue);
			sb.append('}');
			return sb.toString();
		}
	}

}
