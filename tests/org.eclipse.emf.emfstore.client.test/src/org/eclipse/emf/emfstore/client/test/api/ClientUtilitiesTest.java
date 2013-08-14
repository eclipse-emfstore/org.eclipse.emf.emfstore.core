/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.api;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.client.exceptions.ESCertificateException;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.Test;

public class ClientUtilitiesTest extends BaseSharedProjectTest {

	// test if RunESCommand-Exception handling is as expected
	@Test
	public void testRunESCommandWithResultWithException() {
		Exception exception = null;
		Player player = null;

		try {
			player = RunESCommand.WithException.runWithResult(ESCertificateException.class, new Callable<Player>() {
				@SuppressWarnings("null")
				public Player call() throws Exception {
					Player player = null;
					player.getName();
					return player;
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof RuntimeException);
		assertTrue(player == null);
		exception = null;

		try {
			RunESCommand.WithException.runWithResult(ESCertificateException.class, new Callable<Player>() {
				public Player call() throws Exception {
					throw new ESException("test");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof RuntimeException);
		assertTrue(((RuntimeException) exception).getCause() instanceof ESException);
		exception = null;

		try {
			RunESCommand.WithException.runWithResult(ESException.class, new Callable<Player>() {
				public Player call() throws Exception {
					throw new ESException("test");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof ESException);
		exception = null;
	}

	@Test
	public void testRunESCommandWithException() {
		Exception exception = null;
		Player player = null;

		try {
			RunESCommand.WithException.run(ESCertificateException.class, new Callable<Void>() {
				@SuppressWarnings("null")
				public Void call() throws Exception {
					Player player = null;
					player.getName();
					return null;
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof RuntimeException);
		assertTrue(player == null);
		exception = null;

		try {
			RunESCommand.WithException.run(ESCertificateException.class, new Callable<Void>() {
				public Void call() throws Exception {
					throw new ESException("");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof RuntimeException);
		assertTrue(((RuntimeException) exception).getCause() instanceof ESException);
		exception = null;

		try {
			RunESCommand.WithException.run(ESException.class, new Callable<Void>() {
				public Void call() throws Exception {
					throw new ESException("");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception != null);
		assertTrue(exception instanceof ESException);
		exception = null;
	}

	@Test
	public void testRunESCommandWithResultWithoutException() {
		Exception exception = null;

		// runtime exception
		try {
			RunESCommand.runWithResult(new Callable<Player>() {
				public Player call() throws Exception {
					Player player = BowlingFactory.eINSTANCE.createPlayer();
					player.getName().length();
					return player;
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception == null);

		// non-runtime exception
		try {
			RunESCommand.runWithResult(new Callable<Player>() {
				public Player call() throws Exception {
					throw new ESException("");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception == null);
	}

	@Test
	public void testRunESCommandtWithoutException() {
		Exception exception = null;

		// runtime exception
		try {
			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					Player player = BowlingFactory.eINSTANCE.createPlayer();
					player.getName().length();
					return null;
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception == null);

		// non-runtime exception
		try {
			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					throw new ESException("");
				}
			}, ((ESLocalProjectImpl) localProject).toInternalAPI());
		} catch (Exception e) {
			exception = e;
		}
		assertTrue(exception == null);
	}
}
