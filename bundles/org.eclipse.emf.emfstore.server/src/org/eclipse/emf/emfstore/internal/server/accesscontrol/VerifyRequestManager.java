/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NotImplementedException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * Helper class for managing failed login requests.
 */
public class VerifyRequestManager {

	private static final int ALLOWED_FAILED_REQUESTS_BEFORE_RETRY = 4;

	private final Map<String, Integer> failedAttemptsCount;

	/**
	 * Constructor.
	 *
	 * @param delay the delay in ms
	 */
	public VerifyRequestManager(int delay) {
		final Cache<String, Integer> cache = CacheBuilder.newBuilder()
			.concurrencyLevel(4)
			.expireAfterWrite(
				delay,
				TimeUnit.MILLISECONDS)
			.build(new CacheLoader<String, Integer>() {

				@Override
				public Integer load(String key) throws Exception {
					/* use cacheloader for guava 10 compatibility */
					throw new NotImplementedException();
				}

			});
		failedAttemptsCount = cache.asMap();
	}

	/**
	 * Should be called when there is a successful login attempt in order to clean up any recorded failed attempts.
	 *
	 * @param username the user
	 */
	public synchronized void cleanupFailedAttempts(String username) {
		failedAttemptsCount.remove(username);
	}

	/**
	 * Records a new failed attempt.
	 *
	 * @param username the user
	 */
	public synchronized void recordFailedVerifyUserAttempt(String username) {
		Integer count = failedAttemptsCount.get(username);
		if (count == null) {
			count = 0;
		}
		failedAttemptsCount.put(username, count + 1);
	}

	/**
	 * Checks whether too many failed requests were recorded.
	 *
	 * @param username the user
	 * @return <code>true</code> if there were to many failed requests, <code>false</code> otherwise
	 */
	public synchronized boolean checkTooManyFailedRequests(String username) {
		final Integer count = failedAttemptsCount.get(username);
		if (count == null) {
			return false;
		}
		return count > ALLOWED_FAILED_REQUESTS_BEFORE_RETRY;
	}
}
