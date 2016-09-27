/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.model.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Element Id</b></em>'. <!-- end-user-doc -->
 *
 * @generated
 */
public class ModelElementIdImpl extends UniqueIdentifierImpl implements ModelElementId {

	/**
	 * @generated NOT
	 */
	private ESModelElementIdImpl apiImpl;

	/**
	 *
	 * @generated
	 */
	protected ModelElementIdImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.MODEL_ELEMENT_ID;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESModelElementIdImpl toAPI() {

		if (apiImpl == null) {
			apiImpl = createAPI();
		}

		return apiImpl;
	}

	/**
	 * Sets the api impl.
	 *
	 * @param esModelElementIdImpl api impl
	 */
	public void setAPIImpl(ESModelElementIdImpl esModelElementIdImpl) {
		apiImpl = esModelElementIdImpl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 */
	public ESModelElementIdImpl createAPI() {
		return new ESModelElementIdImpl(this);
	}

} // ModelElementIdImpl