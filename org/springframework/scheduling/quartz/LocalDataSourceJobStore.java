/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.scheduling.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.impl.jdbcjobstore.SimpleSemaphore;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

/**
 * Subclass of Quartz's JobStoreCMT class that delegates to a Spring-managed
 * DataSource instead of using a Quartz-managed connection pool. This JobStore
 * will be used if SchedulerFactoryBean's "dataSource" property is set.
 *
 * <p>Supports both transactional and non-transactional DataSource access.
 * With a non-XA DataSource and local Spring transactions, a single DataSource
 * argument is sufficient. In case of an XA DataSource and global JTA transactions,
 * SchedulerFactoryBean's "nonTransactionalDataSource" property should be set,
 * passing in a non-XA DataSource that will not participate in global transactions.
 *
 * <p>Operations performed by this JobStore will properly participate in any
 * kind of Spring-managed transaction, as it uses Spring's DataSourceUtils
 * connection handling methods that are aware of a current transaction.
 *
 * <p>Note that all Quartz Scheduler operations that affect the persistent
 * job store should usually be performed within active transactions,
 * as they assume to get proper locks etc.
 *
 * <p>
 *  Quartz的JobStoreCMT类的子类,委托给Spring管理的DataSource,而不是使用Quartz管理的连接池。
 * 如果SchedulerFactoryBean的"dataSource"属性已设置,则此JobStore将被使用。
 * 
 * <p>支持事务和非事务性DataSource访问通过非XA DataSource和本地Spring事务,单个DataSource参数就足够了。
 * 在XA DataSource和全局JTA事务的情况下,应该设置SchedulerFactoryBean的"nonTransactionalDataSource"属性,传递在不会参与全球交易的非XA Da
 * taSource中。
 * <p>支持事务和非事务性DataSource访问通过非XA DataSource和本地Spring事务,单个DataSource参数就足够了。
 * 
 *  <p>此JobStore执行的操作将正确地参与任何类型的Spring管理的事务,因为它使用Spring的DataSourceUtils连接处理方法来了解当前事务
 * 
 * @author Juergen Hoeller
 * @since 1.1
 * @see SchedulerFactoryBean#setDataSource
 * @see SchedulerFactoryBean#setNonTransactionalDataSource
 * @see org.springframework.jdbc.datasource.DataSourceUtils#doGetConnection
 * @see org.springframework.jdbc.datasource.DataSourceUtils#releaseConnection
 */
@SuppressWarnings("unchecked")  // due to a warning in Quartz 2.2's JobStoreCMT
public class LocalDataSourceJobStore extends JobStoreCMT {

	/**
	 * Name used for the transactional ConnectionProvider for Quartz.
	 * This provider will delegate to the local Spring-managed DataSource.
	 * <p>
	 * 
	 *  注意,影响永久性作业存储的所有Quartz Scheduler操作通常都应在活动事务中执行,因为它们假定要获得正确的锁定
	 * 
	 * 
	 * @see org.quartz.utils.DBConnectionManager#addConnectionProvider
	 * @see SchedulerFactoryBean#setDataSource
	 */
	public static final String TX_DATA_SOURCE_PREFIX = "springTxDataSource.";

	/**
	 * Name used for the non-transactional ConnectionProvider for Quartz.
	 * This provider will delegate to the local Spring-managed DataSource.
	 * <p>
	 * 用于Quartz的事务ConnectionProvider的名称此提供程序将委派给本地的Spring管理的DataSource
	 * 
	 * 
	 * @see org.quartz.utils.DBConnectionManager#addConnectionProvider
	 * @see SchedulerFactoryBean#setDataSource
	 */
	public static final String NON_TX_DATA_SOURCE_PREFIX = "springNonTxDataSource.";


	private DataSource dataSource;


	@Override
	public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler)
			throws SchedulerConfigException {

		// Absolutely needs thread-bound DataSource to initialize.
		this.dataSource = SchedulerFactoryBean.getConfigTimeDataSource();
		if (this.dataSource == null) {
			throw new SchedulerConfigException(
				"No local DataSource found for configuration - " +
				"'dataSource' property must be set on SchedulerFactoryBean");
		}

		// Configure transactional connection settings for Quartz.
		setDataSource(TX_DATA_SOURCE_PREFIX + getInstanceName());
		setDontSetAutoCommitFalse(true);

		// Register transactional ConnectionProvider for Quartz.
		DBConnectionManager.getInstance().addConnectionProvider(
				TX_DATA_SOURCE_PREFIX + getInstanceName(),
				new ConnectionProvider() {
					@Override
					public Connection getConnection() throws SQLException {
						// Return a transactional Connection, if any.
						return DataSourceUtils.doGetConnection(dataSource);
					}
					@Override
					public void shutdown() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
					/* Quartz 2.2 initialize method */
					public void initialize() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
				}
		);

		// Non-transactional DataSource is optional: fall back to default
		// DataSource if not explicitly specified.
		DataSource nonTxDataSource = SchedulerFactoryBean.getConfigTimeNonTransactionalDataSource();
		final DataSource nonTxDataSourceToUse = (nonTxDataSource != null ? nonTxDataSource : this.dataSource);

		// Configure non-transactional connection settings for Quartz.
		setNonManagedTXDataSource(NON_TX_DATA_SOURCE_PREFIX + getInstanceName());

		// Register non-transactional ConnectionProvider for Quartz.
		DBConnectionManager.getInstance().addConnectionProvider(
				NON_TX_DATA_SOURCE_PREFIX + getInstanceName(),
				new ConnectionProvider() {
					@Override
					public Connection getConnection() throws SQLException {
						// Always return a non-transactional Connection.
						return nonTxDataSourceToUse.getConnection();
					}
					@Override
					public void shutdown() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
					/* Quartz 2.2 initialize method */
					public void initialize() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
				}
		);

		// No, if HSQL is the platform, we really don't want to use locks...
		try {
			String productName = JdbcUtils.extractDatabaseMetaData(this.dataSource, "getDatabaseProductName").toString();
			productName = JdbcUtils.commonDatabaseName(productName);
			if (productName != null && productName.toLowerCase().contains("hsql")) {
				setUseDBLocks(false);
				setLockHandler(new SimpleSemaphore());
			}
		}
		catch (MetaDataAccessException ex) {
			logWarnIfNonZero(1, "Could not detect database type. Assuming locks can be taken.");
		}

		super.initialize(loadHelper, signaler);

	}

	@Override
	protected void closeConnection(Connection con) {
		// Will work for transactional and non-transactional connections.
		DataSourceUtils.releaseConnection(con, this.dataSource);
	}

}
