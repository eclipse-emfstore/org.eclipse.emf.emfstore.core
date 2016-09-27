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
package org.eclipse.emf.emfstore.client.test.common.mocks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class ResourceFactoryMock extends XMIResourceFactoryImpl implements Registry {

	private final List<Resource> resources = new LinkedList<Resource>();

	@Override
	public Resource createResource(URI uri) {
		final Resource result = new ResourceImpl() {
			@Override
			protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
			}

			@Override
			protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
			}

			@Override
			protected void doUnload() {
			}

			@Override
			public void save(Map<?, ?> options) throws IOException {
			}

			@Override
			public void load(Map<?, ?> options) throws IOException {
				throw new FileNotFoundException("Mock Resource does not support loading"); //$NON-NLS-1$
			}

			@Override
			public URI getURI() {
				final URI uri = super.getURI();
				return uri == null ? URI.createURI("/f/a/k/e") : uri; //$NON-NLS-1$
			}
		};

		resources.add(result);
		return result;
	}

	public Map<String, Object> getContentTypeToFactoryMap() {
		return null;
	}

	public Map<String, Object> getExtensionToFactoryMap() {
		return null;
	}

	public Factory getFactory(URI uri) {
		return this;
	}

	public Factory getFactory(URI uri, String contentType) {
		return this;
	}

	public Map<String, Object> getProtocolToFactoryMap() {
		return null;
	}

}