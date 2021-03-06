/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementatin
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.parser.ByteArrayParser;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.emfstore.internal.common.CommonUtil;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.SerializationException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;

/**
 * Parser for EObjects.
 *
 * @author ovonwesen
 * @author emueller
 */
public class EObjectDeserializer extends ByteArrayParser {

	private static final String COULDN_T_PARSE_E_OBJECT_TEXT = Messages.EObjectDeserializer_ParsingFailed;
	private static final String FAILED_TO_READ_RESULT_OBJECT_TEXT = Messages.EObjectDeserializer_ReadResultFailed;

	@Override
	public Object getResult() throws XmlRpcException {
		BufferedReader reader = null;
		try {
			final byte[] res = (byte[]) super.getResult();
			final ByteArrayInputStream bais = new ByteArrayInputStream(res);
			reader = new BufferedReader(new InputStreamReader(bais, CommonUtil.getEncoding()));
			final URIConverter.ReadableInputStream ris = new URIConverter.ReadableInputStream(reader,
				CommonUtil.getEncoding());
			try {
				final XMIResource resource = (XMIResource) new ResourceSetImpl().createResource(ModelUtil.VIRTUAL_URI);
				((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
				resource.load(ris, ModelUtil.getResourceLoadOptions());
				return getResultfromResource(resource);
			} catch (final UnsupportedEncodingException e) {
				throw new XmlRpcException(COULDN_T_PARSE_E_OBJECT_TEXT + e.getMessage(), e);
			} catch (final IOException e) {
				throw new XmlRpcException(COULDN_T_PARSE_E_OBJECT_TEXT + e.getMessage(), e);
			} catch (final SerializationException e) {
				throw new XmlRpcException(COULDN_T_PARSE_E_OBJECT_TEXT + e.getMessage(), e);
			} finally {
				ris.close();
			}
		} catch (final IOException e) {
			try {
				reader.close();
			} catch (final IOException e1) {
				throw new XmlRpcException(FAILED_TO_READ_RESULT_OBJECT_TEXT + e1.getMessage(), e1);
			}
			throw new XmlRpcException(FAILED_TO_READ_RESULT_OBJECT_TEXT + e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					throw new XmlRpcException(FAILED_TO_READ_RESULT_OBJECT_TEXT + e.getMessage(), e);
				}
			}
		}
	}

	private static EObject getResultfromResource(XMIResource res) throws SerializationException {
		final EObject result = res.getContents().get(0);

		if (result instanceof FileBasedChangePackage) {
			throw new IllegalStateException(Messages.EObjectDeserializer_NoFileBasedChangePackageAllowed);
		} else if (result instanceof IdEObjectCollection) {
			final IdEObjectCollection collection = (IdEObjectCollection) result;
			final Map<EObject, String> eObjectToIdMap = new LinkedHashMap<EObject, String>();
			final Map<String, EObject> idToEObjectMap = new LinkedHashMap<String, EObject>();

			for (final EObject modelElement : collection.getAllModelElements()) {
				String modelElementId = res.getID(modelElement);
				if (modelElementId == null) {
					// create random ID for generic types, won't get serialized
					// anyway
					modelElementId = ModelFactory.eINSTANCE.createModelElementId().getId();
				}

				if (modelElementId == null) {
					throw new SerializationException(Messages.EObjectDeserializer_NoIdFound
						+ modelElement);
				}

				eObjectToIdMap.put(modelElement, modelElementId);
				idToEObjectMap.put(modelElementId, modelElement);
			}

			collection.initMapping(eObjectToIdMap, idToEObjectMap);
		}

		EcoreUtil.resolveAll(result);
		res.getContents().remove(result);

		return result;
	}
}
