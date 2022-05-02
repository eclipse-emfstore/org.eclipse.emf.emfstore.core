/*******************************************************************************
 * Copyright (c) 2017 EclipseSource Muenchen GmbH and others.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;

/**
 * Iterator for splitting change packages.
 *
 * @author emueller
 *
 */
public final class ChangePackageSplittingIterator implements Iterator<ChangePackageEnvelope> {
	private final int changePackageFragmentSize;
	private final FileBasedChangePackage changePackage;
	private int fragmentIndex;
	private int count;
	private ChangePackageEnvelope envelope;
	private boolean isInitialized;

	ChangePackageSplittingIterator(int changePackageFragmentSize, FileBasedChangePackage changePackage) {
		this.changePackageFragmentSize = changePackageFragmentSize;
		this.changePackage = changePackage;
		ModelUtil.logProjectDetails(MessageFormat.format("Splitting change package {0}", changePackage.getFilePath()), //$NON-NLS-1$
			null, null, null, null, -1);
	}

	public boolean hasNext() {

		if (!isInitialized) {
			init();
		}

		if (envelope == null) {
			envelope = VersioningFactory.eINSTANCE.createChangePackageEnvelope();
			envelope.setFragmentCount(count);
		}

		final List<String> readLines = readLines(fragmentIndex * changePackageFragmentSize, changePackage,
			changePackageFragmentSize);
		envelope.getFragment().addAll(readLines);

		envelope.setFragmentIndex(fragmentIndex);

		if (!envelope.getFragment().isEmpty() || fragmentIndex == 0) {
			ModelUtil.logProjectDetails(MessageFormat.format("Fragment {1} for Change package {0} prepared", //$NON-NLS-1$
				changePackage.getFilePath(), fragmentIndex), null, null, null, null, -1);
			return true;
		}

		ModelUtil.logProjectDetails(
			MessageFormat.format("No more change package fragments for {0}", changePackage.getFilePath()), null, //$NON-NLS-1$
			null, null, null, -1);
		return false;
	}

	private void init() {
		LineNumberReader lineNumberReader = null;
		try {
			lineNumberReader = new LineNumberReader(new FileReader(new File(changePackage.getTempFilePath())));
			lineNumberReader.skip(Long.MAX_VALUE);
			final int lines = lineNumberReader.getLineNumber() + 1;
			count = lines / changePackageFragmentSize;
			if (lines % changePackageFragmentSize != 0) {
				count += 1;
			}
		} catch (final FileNotFoundException ex) {
			throw new IllegalStateException(ex);
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		} finally {
			IOUtils.closeQuietly(lineNumberReader);
		}
		isInitialized = true;
	}

	private List<String> readLines(int from, final FileBasedChangePackage changePackage,
		final int changePackageFragmentSize) {

		int readLines = 0;
		FileReader reader;
		final List<String> lines = new ArrayList<String>();

		try {
			reader = new FileReader(new File(changePackage.getTempFilePath()));
			final LineIterator lineIterator = new LineIterator(reader);
			int read = 0;

			while (read < from) {
				if (!lineIterator.hasNext()) {
					return lines;
				}
				lineIterator.next();
				read += 1;
			}

			while (readLines < changePackageFragmentSize && lineIterator.hasNext()) {
				final String nextLine = lineIterator.next();
				readLines += 1;
				lines.add(nextLine);
			}

		} catch (final FileNotFoundException ex) {
			throw new IllegalStateException(ex);
		}

		return lines;
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
}