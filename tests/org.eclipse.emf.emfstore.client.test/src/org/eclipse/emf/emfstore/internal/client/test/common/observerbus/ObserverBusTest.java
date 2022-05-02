/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.common.observerbus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.emfstore.common.ESObserver;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.A;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.AImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.B;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.BImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.C;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.CImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.DImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.IAppendItem;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.P;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.PImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.QImpl;
import org.eclipse.emf.emfstore.internal.client.test.common.observerbus.assets.RImpl;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverBus;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall.Result;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverExceptionListener;
import org.junit.Before;
import org.junit.Test;

public class ObserverBusTest {

	private ObserverBus observerBus;

	@Test
	public void testUnregister() {
		final C observer = new C() {
			public String fourtyTwo() {
				return "42";
			}
		};
		getObserverBus().register(observer);
		assertEquals("42", getObserverBus().notify(C.class).fourtyTwo());
		getObserverBus().unregister(observer);
		assertFalse("42".equals(getObserverBus().notify(C.class).fourtyTwo()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSuperUnregister() {
		final DImpl d = new DImpl();
		getObserverBus().register(d, C.class);
		assertEquals("42", getObserverBus().notify(C.class).fourtyTwo());
		getObserverBus().unregister(d);
		assertFalse("42".equals(getObserverBus().notify(C.class).fourtyTwo()));
	}

	@Before
	public void reset() {
		observerBus = new ObserverBus();
	}

	private ObserverBus getObserverBus() {
		return observerBus;
	}

	@Test
	public void simpleObserverTest() {
		getObserverBus().register(new AImpl());
		assertEquals(getObserverBus().notify(A.class).returnTwo(), 2);
	}

	@Test
	public void simpleNoObserverTest() {
		// Default value for Int is returned (=0)
		// Would exception be better?
		assertEquals(getObserverBus().notify(A.class).returnTwo(), 0);
	}

	@Test
	public void simpleVoidObserverTest() {
		getObserverBus().register(new BImpl());
		final CImpl tester = new CImpl();
		getObserverBus().notify(B.class).setMSGToFoo(tester);
		assertEquals(tester.getMsg(), "foo");
	}

	@Test
	public void simpleWithTwoObserverTest() {
		getObserverBus().register(new AImpl());
		getObserverBus().register(new AImpl());
		final A observerProxy = getObserverBus().notify(A.class);
		assertEquals(observerProxy.returnTwo(), 2);
		final List<Result> callResults = ((ObserverCall) observerProxy).getObserverCallResults();
		assertEquals(callResults.size(), 2);
		assertEquals(callResults.get(0).getResult(), 2);
		assertEquals(callResults.get(1).getResult(), 2);
	}

	@Test
	public void simpleObserverInheritanceTest() {
		getObserverBus().register(new AImpl());
		// B inherits from A
		getObserverBus().register(new BImpl());
		final A observerProxy = getObserverBus().notify(A.class);
		assertEquals(observerProxy.returnTwo(), 2);
		final List<Result> callResults = ((ObserverCall) observerProxy).getObserverCallResults();
		assertEquals(callResults.size(), 2);
		assertEquals(callResults.get(0).getResult(), 2);
		assertEquals(callResults.get(1).getResult(), 2);
	}

	@Test
	public void simpleObserverInheritanceAndUnRegAllTest() {
		getObserverBus().register(new AImpl());
		// B inherits from A
		final BImpl b = new BImpl();
		getObserverBus().register(b);
		getObserverBus().unregister(b);

		final A observerProxy = getObserverBus().notify(A.class);
		assertEquals(observerProxy.returnTwo(), 2);
		final List<Result> callResults = ((ObserverCall) observerProxy).getObserverCallResults();
		assertEquals(callResults.size(), 1);
		assertEquals(callResults.get(0).getResult(), 2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void simpleObserverInheritanceAndUnRegSubTest() {
		getObserverBus().register(new AImpl());
		// B inherits from A
		final BImpl b = new BImpl();
		getObserverBus().register(b);
		getObserverBus().unregister(b, B.class);

		final A observerProxy = getObserverBus().notify(A.class);
		assertEquals(observerProxy.returnTwo(), 2);
		final List<Result> callResults = ((ObserverCall) observerProxy).getObserverCallResults();
		assertEquals(callResults.size(), 2);
		assertEquals(callResults.get(0).getResult(), 2);
		assertEquals(callResults.get(1).getResult(), 2);
	}

	@Test
	public void callObserverException() {
		getObserverBus().register(new AImpl());
		getObserverBus().register(new BImpl());

		final A proxy = getObserverBus().notify(A.class);
		proxy.returnFoobarOrException();

		final List<Result> results = ((ObserverCall) proxy).getObserverCallResults();
		assertEquals(results.size(), 2);
		assertFalse(results.get(0).exceptionOccurred());
		assertTrue(results.get(1).exceptionOccurred());
	}

	@Test
	public void observerExceptionListener() {
		final Map<ESObserver, List<Throwable>> throwables = new LinkedHashMap<ESObserver, List<Throwable>>();
		getObserverBus().registerExceptionListener(new ObserverExceptionListener() {

			public void onException(ESObserver observer, Throwable throwable) {
				if (!throwables.containsKey(observer)) {
					throwables.put(observer, new ArrayList<Throwable>());
				}
				throwables.get(observer).add(throwable);
			}
		});
		getObserverBus().register(new AImpl());
		final BImpl bImpl = new BImpl();
		getObserverBus().register(bImpl);

		final A proxy = getObserverBus().notify(A.class);
		proxy.returnFoobarOrException();

		final List<Result> results = ((ObserverCall) proxy).getObserverCallResults();
		assertEquals(results.size(), 2);
		assertFalse(results.get(0).exceptionOccurred());
		assertTrue(results.get(1).exceptionOccurred());
		assertEquals(1, throwables.size());
		assertTrue(throwables.containsKey(bImpl));
		assertEquals(1, throwables.get(bImpl).size());
	}

	@Test
	public void registerMultipleCallOne() {
		getObserverBus().register(new AImpl());
		getObserverBus().register(new BImpl());
		getObserverBus().register(new CImpl());

		getObserverBus().notify(C.class).fourtyTwo();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void registerForOneInterfaceTest() {
		getObserverBus().register(new BImpl(), B.class);

		final A a = getObserverBus().notify(A.class);
		final B b = getObserverBus().notify(B.class);

		a.returnTwo();
		assertTrue(((ObserverCall) a).getObserverCallResults().size() == 0);
		b.returnTwo();
		assertTrue(((ObserverCall) b).getObserverCallResults().size() == 1);
	}

	@Test
	public void prioritizedNotify() {
		final PImpl p = new PImpl();
		final QImpl q = new QImpl();
		getObserverBus().register(p);
		getObserverBus().register(q);
		final List<String> items = new ArrayList<String>();
		getObserverBus().notify(P.class, true).appendItem(items);
		// P should have been notified first
		assertEquals(items.get(0), "P");
		assertEquals(items.get(1), "Q");
	}

	@Test
	public void prioritizedNotifyWithMixedObserver() {
		final PImpl p = new PImpl();
		final QImpl q = new QImpl();
		final RImpl r = new RImpl();
		getObserverBus().register(p);
		getObserverBus().register(q);
		getObserverBus().register(r);
		final List<String> items = new ArrayList<String>();
		getObserverBus().notify(IAppendItem.class, true).appendItem(items);
		// P should have been notified first
		assertEquals(items.get(0), "P");
		assertEquals(items.get(1), "Q");
		assertEquals(items.get(2), "R");
	}

	public String fourtyTwo() {
		return "42";
	}
}