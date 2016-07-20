/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Change package helper class.
 *
 * @author emueller
 *
 */
public final class ChangePackageUtil {

	private ChangePackageUtil() {

	}

	/**
	 * Creates a new {@link AbstractChangePackage} depending on the client configuration behavior whether
	 * to create in-memory of file-based change packages.
	 *
	 * @param useInMemoryChangePackage
	 *            whether an in-memory change package should be created
	 *
	 * @return the created change package
	 */
	public static AbstractChangePackage createChangePackage(boolean useInMemoryChangePackage) {

		if (useInMemoryChangePackage) {
			return VersioningFactory.eINSTANCE.createChangePackage();
		}

		final FileBasedChangePackage fileBasedChangePackage = VersioningFactory.eINSTANCE
			.createFileBasedChangePackage();
		fileBasedChangePackage.initialize(FileUtil.createLocationForTemporaryChangePackage());
		return fileBasedChangePackage;
	}

	/**
	 * Given a single change package, splits it into multiple fragments.
	 *
	 * @param changePackage
	 *            the change package to be splitted
	 * @param changePackageFragmentSize
	 *            the max number of operations a single fragment may consists of
	 * @return an iterator for the created fragments
	 */
	public static Iterator<ChangePackageEnvelope> splitChangePackage(final AbstractChangePackage changePackage,
		final int changePackageFragmentSize) {

		return new Iterator<ChangePackageEnvelope>() {

			private int fragmentIndex;
			private int count;
			private ChangePackageEnvelope envelope;
			private Iterator<AbstractOperation> operationsIterator;
			private ESCloseableIterable<AbstractOperation> operationsIterable;

			private void init() {
				int leafSizeCounter = 0;
				final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
				try {
					for (final AbstractOperation operation : operations.iterable()) {
						final int countLeafOperations = countLeafOperations(operation);
						leafSizeCounter += countLeafOperations;
						if (leafSizeCounter < changePackageFragmentSize) {
							continue;
						}
						leafSizeCounter = 0;
						count += 1;
					}
				} finally {
					operations.close();
				}
				if (leafSizeCounter != 0 || count == 0) {
					count += 1;
				}
				operationsIterable = changePackage.operations();
				operationsIterator = operationsIterable.iterable().iterator();
			}

			public boolean hasNext() {

				if (operationsIterable == null) {
					init();
				}

				if (envelope == null) {
					envelope = VersioningFactory.eINSTANCE.createChangePackageEnvelope();
					final ChangePackage cp = VersioningFactory.eINSTANCE.createChangePackage();
					cp.setLogMessage(ModelUtil.clone(changePackage.getLogMessage()));
					envelope.setFragmentCount(count);
				}

				boolean iteratorHasNext = false;
				while (countLeafOperations(envelope.getFragment()) < changePackageFragmentSize
					&& (iteratorHasNext = operationsIterator.hasNext())) {

					final AbstractOperation op = operationsIterator.next();
					envelope.getFragment().add(ModelUtil.clone(op));
				}

				envelope.setFragmentIndex(fragmentIndex);

				if (!iteratorHasNext) {
					operationsIterable.close();
				}

				if (!envelope.getFragment().isEmpty() || fragmentIndex == 0) {
					return true;
				}

				return false;
			}

			public ChangePackageEnvelope next() {
				if (envelope == null) {
					final boolean hasNext = hasNext();
					if (!hasNext) {
						throw new NoSuchElementException();
					}
				}
				final ChangePackageEnvelope ret = envelope;
				envelope = null;
				fragmentIndex += 1;
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}

	/**
	 * Count all leaf operations of a collection of {@link AbstractOperation}s.
	 *
	 * @param operations
	 *            a collection of operations
	 * @return the leaf operation count of all involved operations
	 */
	public static int countLeafOperations(Collection<AbstractOperation> operations) {
		int ret = 0;
		for (final AbstractOperation operation : operations) {
			if (operation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) operation);
			} else {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * Count all leaf operations of a single {@link AbstractOperation}s.
	 *
	 * @param operation
	 *            a single operation
	 * @return the leaf operation count of the given operation
	 */
	public static int countLeafOperations(AbstractOperation operation) {
		return countLeafOperations(Collections.singleton(operation));
	}

	/**
	 * Count all leaf operations of all operations contained in the given list of {@link ChangePackage}s.
	 *
	 * @param changePackages
	 *            list of change packages
	 * @return the leaf operation count of all operations contained in the list of change packages
	 */
	public static int countLeafOperations(List<AbstractChangePackage> changePackages) {
		int count = 0;
		for (final AbstractChangePackage changePackage : changePackages) {
			final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
			try {
				for (final AbstractOperation operation : operations.iterable()) {
					count += countLeafOperations(operation);
				}
			} finally {
				operations.close();
			}
		}
		return count;
	}

	/**
	 * Count all root operations within the given list of {@link ChangePackage}s.
	 *
	 * @param changePackages
	 *            list of change packages
	 * @return the root operation count of all change packages
	 */
	public static int countOperations(List<AbstractChangePackage> changePackages) {
		int count = 0;
		for (final AbstractChangePackage changePackage : changePackages) {
			count += changePackage.size();
		}
		return count;
	}

	private static int getSize(CompositeOperation compositeOperation) {
		int ret = 0;
		final EList<AbstractOperation> subOperations = compositeOperation.getSubOperations();
		for (final AbstractOperation abstractOperation : subOperations) {
			if (abstractOperation instanceof CompositeOperation) {
				ret = ret + getSize((CompositeOperation) abstractOperation);
			} else {
				ret++;
			}
		}
		return ret;
	}
}
