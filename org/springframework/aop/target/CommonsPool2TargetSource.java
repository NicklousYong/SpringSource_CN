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

package org.springframework.aop.target;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * {@link org.springframework.aop.TargetSource} implementation that holds
 * objects in a configurable Apache Commons2 Pool.
 *
 * <p>By default, an instance of {@code GenericObjectPool} is created.
 * Subclasses may change the type of {@code ObjectPool} used by
 * overriding the {@code createObjectPool()} method.
 *
 * <p>Provides many configuration properties mirroring those of the Commons Pool
 * {@code GenericObjectPool} class; these properties are passed to the
 * {@code GenericObjectPool} during construction. If creating a subclass of this
 * class to change the {@code ObjectPool} implementation type, pass in the values
 * of configuration properties that are relevant to your chosen implementation.
 *
 * <p>The {@code testOnBorrow}, {@code testOnReturn} and {@code testWhileIdle}
 * properties are explicitly not mirrored because the implementation of
 * {@code PoolableObjectFactory} used by this class does not implement
 * meaningful validation. All exposed Commons Pool properties use the
 * corresponding Commons Pool defaults.
 *
 * <p>Compatible with Apache Commons Pool 2.4, as of Spring 4.2.
 *
 * <p>
 *  {@link orgspringframeworkaopTargetSource}实现,在可配置的Apache Commons2池中保存对象
 * 
 * <p>默认情况下,创建{@code GenericObjectPool}的实例子类可以通过覆盖{@code createObjectPool()}方法来更改所使用的{@code ObjectPool}的
 * 类型。
 * 
 *  <p>提供许多配置属性镜像Commons Pool {@code GenericObjectPool}类的配置属性;这些属性在构造期间传递给{@code GenericObjectPool}如果创建此
 * 类的子类来更改{@code ObjectPool}实现类型,请传递与您选择的实现相关的配置属性值。
 * 
 * <p> {@code testOnBorrow},{@code testOnReturn}和{@code testWhileIdle}属性显式不镜像,因为此类使用的{@code PoolableObjectFactory}
 * 的实现不能实现有意义的验证所有公开的Commons Pool属性使用相应的Commons Pool默认值。
 * 
 *  <p>与Spring 42兼容,与Apache Commons Pool 24兼容
 * 
 * 
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Kazuki Shimizu
 * @since 4.2
 * @see GenericObjectPool
 * @see #createObjectPool()
 * @see #setMaxSize
 * @see #setMaxIdle
 * @see #setMinIdle
 * @see #setMaxWait
 * @see #setTimeBetweenEvictionRunsMillis
 * @see #setMinEvictableIdleTimeMillis
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class CommonsPool2TargetSource extends AbstractPoolingTargetSource implements PooledObjectFactory<Object> {

	private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;

	private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;

	private long maxWait = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;

	private long timeBetweenEvictionRunsMillis = GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

	private long minEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

	private boolean blockWhenExhausted = GenericObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

	/**
	 * The Apache Commons {@code ObjectPool} used to pool target objects
	 * <p>
	 *  Apache Commons {@code ObjectPool}用于汇集目标对象
	 * 
	 */
	private ObjectPool pool;


	/**
	 * Create a CommonsPoolTargetSource with default settings.
	 * Default maximum size of the pool is 8.
	 * <p>
	 *  使用默认设置创建CommonsPoolTargetSource池的默认最大大小为8
	 * 
	 * 
	 * @see #setMaxSize
	 * @see GenericObjectPoolConfig#setMaxTotal
	 */
	public CommonsPool2TargetSource() {
		setMaxSize(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL);
	}


	/**
	 * Set the maximum number of idle objects in the pool.
	 * Default is 8.
	 * <p>
	 *  设置池中空闲对象的最大数目默认值为8
	 * 
	 * 
	 * @see GenericObjectPool#setMaxIdle
	 */
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	/**
	 * Return the maximum number of idle objects in the pool.
	 * <p>
	 *  返回池中空闲对象的最大数量
	 * 
	 */
	public int getMaxIdle() {
		return this.maxIdle;
	}

	/**
	 * Set the minimum number of idle objects in the pool.
	 * Default is 0.
	 * <p>
	 *  设置池中空闲对象的最小数量默认为0
	 * 
	 * 
	 * @see GenericObjectPool#setMinIdle
	 */
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	/**
	 * Return the minimum number of idle objects in the pool.
	 * <p>
	 *  返回池中空闲对象的最小数量
	 * 
	 */
	public int getMinIdle() {
		return this.minIdle;
	}

	/**
	 * Set the maximum waiting time for fetching an object from the pool.
	 * Default is -1, waiting forever.
	 * <p>
	 * 设置从池中获取对象的最大等待时间默认值为-1,永远等待
	 * 
	 * 
	 * @see GenericObjectPool#setMaxWaitMillis
	 */
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	/**
	 * Return the maximum waiting time for fetching an object from the pool.
	 * <p>
	 *  返回从池中获取对象的最大等待时间
	 * 
	 */
	public long getMaxWait() {
		return this.maxWait;
	}

	/**
	 * Set the time between eviction runs that check idle objects whether
	 * they have been idle for too long or have become invalid.
	 * Default is -1, not performing any eviction.
	 * <p>
	 *  设置检查空闲对象的驱逐运行之间的时间,无论它们是否空闲时间过长或已变为无效默认值为-1,不执行任何驱逐
	 * 
	 * 
	 * @see GenericObjectPool#setTimeBetweenEvictionRunsMillis
	 */
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	/**
	 * Return the time between eviction runs that check idle objects.
	 * <p>
	 *  返回检查空闲对象的驱逐运行之间的时间
	 * 
	 */
	public long getTimeBetweenEvictionRunsMillis() {
		return this.timeBetweenEvictionRunsMillis;
	}

	/**
	 * Set the minimum time that an idle object can sit in the pool before
	 * it becomes subject to eviction. Default is 1800000 (30 minutes).
	 * <p>Note that eviction runs need to be performed to take this
	 * setting into effect.
	 * <p>
	 *  设置空闲对象可以在池中被放置之前可以坐在的最小时间默认值为1800000(30分钟)<p>请注意,需要执行逐出运行才能使此设置生效
	 * 
	 * 
	 * @see #setTimeBetweenEvictionRunsMillis
	 * @see GenericObjectPool#setMinEvictableIdleTimeMillis
	 */
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * Return the minimum time that an idle object can sit in the pool.
	 * <p>
	 *  返回空闲对象可以坐在池中的最短时间
	 * 
	 */
	public long getMinEvictableIdleTimeMillis() {
		return this.minEvictableIdleTimeMillis;
	}

	/**
	 * Set whether the call should bock when the pool is exhausted.
	 * <p>
	 *  设置当池被耗尽时,呼叫是否应该被锁定
	 * 
	 */
	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}

	/**
	 * Specify if the call should block when the pool is exhausted.
	 * <p>
	 * 指定在池用尽时呼叫是否应该阻止
	 * 
	 */
	public boolean isBlockWhenExhausted() {
		return this.blockWhenExhausted;
	}


	/**
	 * Creates and holds an ObjectPool instance.
	 * <p>
	 *  创建并保存一个ObjectPool实例
	 * 
	 * 
	 * @see #createObjectPool()
	 */
	@Override
	protected final void createPool() {
		logger.debug("Creating Commons object pool");
		this.pool = createObjectPool();
	}

	/**
	 * Subclasses can override this if they want to return a specific Commons pool.
	 * They should apply any configuration properties to the pool here.
	 * <p>Default is a GenericObjectPool instance with the given pool size.
	 * <p>
	 *  子类可以重写这个,如果他们想要返回一个特定的Commons池他们应该应用任何配置属性到这里池<p>默认是一个GenericObjectPool实例与给定的池大小
	 * 
	 * 
	 * @return an empty Commons {@code ObjectPool}.
	 * @see GenericObjectPool
	 * @see #setMaxSize
	 */
	protected ObjectPool createObjectPool() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(getMaxSize());
		config.setMaxIdle(getMaxIdle());
		config.setMinIdle(getMinIdle());
		config.setMaxWaitMillis(getMaxWait());
		config.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
		config.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
		config.setBlockWhenExhausted(isBlockWhenExhausted());
		return new GenericObjectPool(this, config);
	}


	/**
	 * Borrows an object from the {@code ObjectPool}.
	 * <p>
	 *  从{@code ObjectPool}中借用一个对象
	 * 
	 */
	@Override
	public Object getTarget() throws Exception {
		return this.pool.borrowObject();
	}

	/**
	 * Returns the specified object to the underlying {@code ObjectPool}.
	 * <p>
	 *  将指定的对象返回给底层{@code ObjectPool}
	 * 
	 */
	@Override
	public void releaseTarget(Object target) throws Exception {
		this.pool.returnObject(target);
	}

	@Override
	public int getActiveCount() throws UnsupportedOperationException {
		return this.pool.getNumActive();
	}

	@Override
	public int getIdleCount() throws UnsupportedOperationException {
		return this.pool.getNumIdle();
	}


	/**
	 * Closes the underlying {@code ObjectPool} when destroying this object.
	 * <p>
	 *  在销毁此对象时关闭底层的{@code ObjectPool}
	 */
	@Override
	public void destroy() throws Exception {
		logger.debug("Closing Commons ObjectPool");
		this.pool.close();
	}


	//----------------------------------------------------------------------------
	// Implementation of org.apache.commons.pool2.PooledObjectFactory interface
	//----------------------------------------------------------------------------

	@Override
	public PooledObject<Object> makeObject() throws Exception {
		return new DefaultPooledObject<Object>(newPrototypeInstance());
	}

	@Override
	public void destroyObject(PooledObject<Object> p) throws Exception {
		destroyPrototypeInstance(p.getObject());
	}

	@Override
	public boolean validateObject(PooledObject<Object> p) {
		return true;
	}

	@Override
	public void activateObject(PooledObject<Object> p) throws Exception {
	}

	@Override
	public void passivateObject(PooledObject<Object> p) throws Exception {
	}

}
