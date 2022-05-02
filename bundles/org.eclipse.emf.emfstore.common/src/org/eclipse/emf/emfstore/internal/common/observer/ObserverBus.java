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
package org.eclipse.emf.emfstore.internal.common.observer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.emfstore.common.ESObserver;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.Activator;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall.Result;

/**
 * This is a universal observer bus. This class follows the publish/subscribe pattern, it is a central dispatcher for
 * observers and makes use of generics in order to allow type safety. It can be used as singleton or be injected through
 * DI.
 * Observers have to implement the {@link ESObserver} interface, which is only used as a marker. Future use of
 * Annotations is possible.
 * by using {@link #notify(Class)} (e.g. <code>bus.notify(MyObserver.class).myObserverMethod()</code>) all registered
 * Observers are notified.
 * This is implemented by using the java {@link Proxy} class. By calling {@link #notify(Class)} a proxy is returned,
 * which then calls all registered observers.
 * The proxy can also be casted into {@link ObserverCall}, which allows to access all results by the different
 * observers.
 *
 *
 * Example code:
 *
 * <pre>
 * // A is ESObserver
 * A a = new A() {
 *
 * 	public void foo() {
 * 		System.out.println(&quot;A says: go!&quot;);
 * 	}
 * };
 *
 * // B extends A and is ESObserver
 * B b = new B() {
 *
 * 	public void say(String ja) {
 * 		System.out.println(&quot;B says: &quot; + ja);
 * 	}
 *
 * 	public void foo() {
 * 		System.out.println(&quot;B says: h??&quot;);
 * 	}
 * };
 *
 * // B is registered first
 * ObserverBus.register(b);
 * ObserverBus.register(a);
 *
 * ObserverBus.notify(A.class).foo();
 *
 * ObserverBus.notify(B.class).say(&quot;w00t&quot;);
 *
 * // Output:
 *
 * // B says: h??
 * // A says: go!
 * //
 * // B says: w00t
 *
 * </pre>
 *
 * @author wesendon
 */
public class ObserverBus {

	private static final String EXTENSION_POINT_ID = "org.eclipse.emf.emfstore.common.observerBusExtensionPointRegistration"; //$NON-NLS-1$
	private final transient Map<Class<? extends ESObserver>, List<ESObserver>> observerMap;
	private final Set<ObserverExceptionListener> exceptionListeners;

	/**
	 * Default constructor.
	 */
	public ObserverBus() {
		observerMap = new LinkedHashMap<Class<? extends ESObserver>, List<ESObserver>>();
		exceptionListeners = new LinkedHashSet<ObserverExceptionListener>();
		collectionExtensionPoints();
	}

	/**
	 * This method allows you to notify all observers.
	 *
	 * @param <T> class of observer
	 * @param clazz class of observer
	 * @return call object
	 */
	public <T extends ESObserver> T notify(final Class<T> clazz) {
		return notify(clazz, false);
	}

	/**
	 * This method allows you to notify all observers.
	 *
	 * @param <T> class of observer
	 * @param clazz class of observer
	 * @param prioritized sort observer after {@link ESPrioritizedObserver}
	 *
	 * @return call object
	 */
	public <T extends ESObserver> T notify(final Class<T> clazz, final boolean prioritized) {
		if (clazz == null) {
			return null;
		}
		return createProxy(clazz, prioritized);
	}

	/**
	 * Registers an observer for all observer interfaces implemented by the object or its super classes.
	 *
	 * @param observer observer object
	 */
	public void register(final ESObserver observer) {
		register(observer, getObserverInterfaces(observer));
	}

	/**
	 * Registers an observer for the specified observer interfaces.
	 *
	 * @param observer observer object
	 * @param classes set of classes
	 */
	public void register(final ESObserver observer, final Class<? extends ESObserver>... classes) {
		for (final Class<? extends ESObserver> iface : classes) {
			if (iface.isInstance(observer)) {
				addObserver(observer, iface);
			}
		}
	}

	/**
	 * Unregisters an observer for all observer interfaces implemented by the object or its super classes.
	 *
	 * @param observer observer object
	 */
	public void unregister(final ESObserver observer) {
		unregister(observer, getObserverInterfaces(observer));
	}

	/**
	 * Unregisters an observer for the specified observer interfaces.
	 *
	 * @param observer observer object
	 * @param classes set of classes
	 */
	public void unregister(final ESObserver observer, final Class<? extends ESObserver>... classes) {
		for (final Class<? extends ESObserver> iface : classes) {
			if (iface.isInstance(observer)) {
				removeObserver(observer, iface);
			}
		}
	}

	/**
	 * Registers the given listener as an exception observer. Has no effect if this listener was already registered.
	 *
	 * @param listener the {@link ObserverExceptionListener}
	 */
	public void registerExceptionListener(ObserverExceptionListener listener) {
		exceptionListeners.add(listener);
	}

	/**
	 * Unregisters the given listener from the exception observers. Has no effect if this listener was not registered
	 * before.
	 *
	 * @param listener the {@link ObserverExceptionListener}
	 */
	public void unregisterExceptionListener(ObserverExceptionListener listener) {
		exceptionListeners.remove(listener);
	}

	private void addObserver(ESObserver observer, final Class<? extends ESObserver> iface) {
		final List<ESObserver> observers = initObserverList(iface);
		observers.add(observer);
	}

	private void removeObserver(ESObserver observer, Class<? extends ESObserver> iface) {
		final List<ESObserver> observers = initObserverList(iface);
		observers.remove(observer);
	}

	private List<ESObserver> initObserverList(Class<? extends ESObserver> iface) {
		List<ESObserver> list = observerMap.get(iface);
		if (list == null) {
			list = new ArrayList<ESObserver>();
			observerMap.put(iface, list);
		}
		return list;
	}

	private List<ESObserver> getObserverByClass(Class<? extends ESObserver> clazz) {
		List<ESObserver> list = observerMap.get(clazz);
		if (list == null) {
			list = Collections.emptyList();
		}
		return new ArrayList<ESObserver>(list);
	}

	@SuppressWarnings("unchecked")
	private <T extends ESObserver> T createProxy(Class<T> clazz, boolean prioritized) {
		final ProxyHandler handler = new ProxyHandler((Class<ESObserver>) clazz, prioritized);
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz, ObserverCall.class }, handler);
	}

	private void logExceptions(List<Result> results) {
		for (final Result result : results) {
			final Throwable throwable = result.getException();
			if (throwable != null) {
				for (final ObserverExceptionListener listener : exceptionListeners) {
					listener.onException(result.getObserver(), throwable);
				}
				Activator.getDefault().getLog().log(
					new Status(
						IStatus.ERROR, "org.eclipse.emf.emfstore.common", //$NON-NLS-1$
						throwable.getMessage(),
						throwable));
			}
		}
	}

	/**
	 * Proxyobserver which notifies all observers.
	 *
	 * @author wesendon
	 */
	private final class ProxyHandler implements InvocationHandler, ObserverCall {

		private final Class<ESObserver> clazz;
		private final boolean prioritized;
		private List<ObserverCall.Result> lastResults;

		public ProxyHandler(Class<ESObserver> clazz, boolean prioritized) {
			this.clazz = clazz;
			this.prioritized = prioritized;
			lastResults = new ArrayList<ObserverCall.Result>();
		}

		// BEGIN SUPRESS CATCH EXCEPTION
		public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
			// END SUPRESS CATCH EXCEPTION
			// fork for calls to ObserverCall.class
			if (ObserverCall.class.equals(method.getDeclaringClass())) {
				return accessObserverCall(method, args);
			}

			final List<ESObserver> observers = getObserverByClass(clazz);

			if (prioritized) {
				final List<ESObserver> nonPrioritizedObservers = filterNonPrioritizedObservers(observers);
				observers.removeAll(nonPrioritizedObservers);
				sortObservers(observers);

				// return default value if no observers are registered
				if (observers.size() == 0 && nonPrioritizedObservers.size() == 0) {
					lastResults = new ArrayList<ObserverCall.Result>();
					return Result.getDefaultValue(method);
				}

				lastResults = notifiyObservers(observers, method, args);
				final List<Result> nonPrioritizedResults = notifiyObservers(nonPrioritizedObservers, method, args);
				if (lastResults.size() == 0) {
					lastResults = nonPrioritizedResults;
				}
				return lastResults.get(0).getResult();
			}

			// return default value if no observers are registered
			if (observers.size() == 0) {
				lastResults = new ArrayList<ObserverCall.Result>();
				return Result.getDefaultValue(method);
			}

			lastResults = notifiyObservers(observers, method, args);
			logExceptions(lastResults);
			return lastResults.get(0).getResultOrDefaultValue();
		}

		/**
		 * Returns all observers that are not an instance of {@link ESPrioritizedObserver}.
		 *
		 * @param observers
		 *            the list of observers to be filtered
		 * @return a list of non prioritized observers
		 */
		private List<ESObserver> filterNonPrioritizedObservers(List<ESObserver> observers) {
			final List<ESObserver> nonPrioritizedObservers = new ArrayList<ESObserver>();
			for (final ESObserver o : observers) {
				if (!ESPrioritizedObserver.class.isInstance(o)) {
					nonPrioritizedObservers.add(o);
				}
			}
			return nonPrioritizedObservers;
		}

		private Object accessObserverCall(Method method, Object[] args) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
			return method.invoke(this, args);

		}

		private List<ObserverCall.Result> notifiyObservers(List<ESObserver> observers, Method method, Object[] args) {
			final List<ObserverCall.Result> results = new ArrayList<ObserverCall.Result>(observers.size());
			for (final ESObserver observer : observers) {
				try {
					results.add(new Result(observer, method, method.invoke(observer, args)));
					// BEGIN SUPRESS CATCH EXCEPTION
				} catch (final Exception exceptin) {
					// END SUPRESS CATCH EXCEPTION
					results.add(new Result(observer, method, exceptin));
				}
			}
			return results;
		}

		public List<Result> getObserverCallResults() {
			return lastResults;
		}

		// END SUPRESS CATCH EXCEPTION
	}

	/**
	 * Sorts Observers. Make sure they are {@link ESPrioritizedObserver}!!
	 *
	 * @param observers list of observers
	 */
	private void sortObservers(List<ESObserver> observers) {
		Collections.sort(observers, new Comparator<ESObserver>() {
			public int compare(ESObserver observer1, ESObserver observer2) {
				final int prio1 = ((ESPrioritizedObserver) observer1).getPriority();
				final int prio2 = ((ESPrioritizedObserver) observer2).getPriority();
				if (prio1 == prio2) {
					return 0;
				}
				return prio1 < prio2 ? 1 : -1;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private Class<? extends ESObserver>[] getObserverInterfaces(ESObserver observer) {
		final HashSet<Class<? extends ESObserver>> observerIFacesFound = new LinkedHashSet<Class<? extends ESObserver>>();
		getClasses(observer.getClass(), observerIFacesFound);
		return observerIFacesFound.toArray(new Class[observerIFacesFound.size()]);
	}

	@SuppressWarnings("unchecked")
	private boolean getClasses(Class<?> clazz, Set<Class<? extends ESObserver>> result) {
		for (final Class<?> iface : getAllInterfaces(clazz, new LinkedHashSet<Class<?>>())) {
			if (iface.equals(ESObserver.class) && clazz.isInterface()) {
				result.add((Class<? extends ESObserver>) clazz);
				return true;
			}
			if (getClasses(iface, result) && clazz.isInterface()) {
				result.add((Class<? extends ESObserver>) clazz);
			}
		}
		return false;
	}

	private Set<Class<?>> getAllInterfaces(final Class<?> clazz, final Set<Class<?>> interfacesFound) {

		for (final Class<?> iface : clazz.getInterfaces()) {
			interfacesFound.add(iface);
		}

		if (clazz.getSuperclass() == null) {
			return interfacesFound;
		}

		return getAllInterfaces(clazz.getSuperclass(), interfacesFound);
	}

	/**
	 * Pulls observers from an extension point and registers them.
	 */
	public void collectionExtensionPoints() {
		for (final ESExtensionElement outer : new ESExtensionPoint(
			EXTENSION_POINT_ID, true)
				.getExtensionElements()) {
			try {
				for (final ESExtensionElement inner : new ESExtensionPoint(outer.getAttribute("extensionPointName"), //$NON-NLS-1$
					true)
						.getExtensionElements()) {
					register(inner.getClass(outer.getAttribute("observerAttributeName"), ESObserver.class)); //$NON-NLS-1$
				}
			} catch (final ESExtensionPointException e) {
			}
		}
	}
}
