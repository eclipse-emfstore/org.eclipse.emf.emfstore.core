/**
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 */
package org.eclipse.emf.emfstore.bowling.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.emfstore.bowling.BowlingPackage;
import org.eclipse.emf.emfstore.bowling.Merchandise;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Merchandise</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.bowling.impl.MerchandiseImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.bowling.impl.MerchandiseImpl#getPrice <em>Price</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.bowling.impl.MerchandiseImpl#getSerialNumber <em>Serial Number</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MerchandiseImpl extends EObjectImpl implements Merchandise {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPrice() <em>Price</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getPrice()
	 * @generated
	 * @ordered
	 */
	protected static final BigDecimal PRICE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPrice() <em>Price</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getPrice()
	 * @generated
	 * @ordered
	 */
	protected BigDecimal price = PRICE_EDEFAULT;

	/**
	 * This is true if the Price attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	protected boolean priceESet;

	/**
	 * The default value of the '{@link #getSerialNumber() <em>Serial Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getSerialNumber()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger SERIAL_NUMBER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSerialNumber() <em>Serial Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getSerialNumber()
	 * @generated
	 * @ordered
	 */
	protected BigInteger serialNumber = SERIAL_NUMBER_EDEFAULT;

	/**
	 * This is true if the Serial Number attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 * @ordered
	 */
	protected boolean serialNumberESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected MerchandiseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BowlingPackage.Literals.MERCHANDISE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setName(String newName) {
		final String oldName = name;
		name = newName;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BowlingPackage.MERCHANDISE__NAME, oldName, name));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setPrice(BigDecimal newPrice) {
		final BigDecimal oldPrice = price;
		price = newPrice;
		final boolean oldPriceESet = priceESet;
		priceESet = true;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BowlingPackage.MERCHANDISE__PRICE, oldPrice, price,
				!oldPriceESet));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void unsetPrice() {
		final BigDecimal oldPrice = price;
		final boolean oldPriceESet = priceESet;
		price = PRICE_EDEFAULT;
		priceESet = false;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.UNSET, BowlingPackage.MERCHANDISE__PRICE, oldPrice,
				PRICE_EDEFAULT, oldPriceESet));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public boolean isSetPrice() {
		return priceESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BigInteger getSerialNumber() {
		return serialNumber;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setSerialNumber(BigInteger newSerialNumber) {
		final BigInteger oldSerialNumber = serialNumber;
		serialNumber = newSerialNumber;
		final boolean oldSerialNumberESet = serialNumberESet;
		serialNumberESet = true;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, BowlingPackage.MERCHANDISE__SERIAL_NUMBER,
				oldSerialNumber, serialNumber, !oldSerialNumberESet));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void unsetSerialNumber() {
		final BigInteger oldSerialNumber = serialNumber;
		final boolean oldSerialNumberESet = serialNumberESet;
		serialNumber = SERIAL_NUMBER_EDEFAULT;
		serialNumberESet = false;
		if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.UNSET, BowlingPackage.MERCHANDISE__SERIAL_NUMBER,
				oldSerialNumber, SERIAL_NUMBER_EDEFAULT, oldSerialNumberESet));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public boolean isSetSerialNumber() {
		return serialNumberESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case BowlingPackage.MERCHANDISE__NAME:
			return getName();
		case BowlingPackage.MERCHANDISE__PRICE:
			return getPrice();
		case BowlingPackage.MERCHANDISE__SERIAL_NUMBER:
			return getSerialNumber();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case BowlingPackage.MERCHANDISE__NAME:
			setName((String) newValue);
			return;
		case BowlingPackage.MERCHANDISE__PRICE:
			setPrice((BigDecimal) newValue);
			return;
		case BowlingPackage.MERCHANDISE__SERIAL_NUMBER:
			setSerialNumber((BigInteger) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case BowlingPackage.MERCHANDISE__NAME:
			setName(NAME_EDEFAULT);
			return;
		case BowlingPackage.MERCHANDISE__PRICE:
			unsetPrice();
			return;
		case BowlingPackage.MERCHANDISE__SERIAL_NUMBER:
			unsetSerialNumber();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case BowlingPackage.MERCHANDISE__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case BowlingPackage.MERCHANDISE__PRICE:
			return isSetPrice();
		case BowlingPackage.MERCHANDISE__SERIAL_NUMBER:
			return isSetSerialNumber();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) {
			return super.toString();
		}

		final StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", price: "); //$NON-NLS-1$
		if (priceESet) {
			result.append(price);
		} else {
			result.append("<unset>"); //$NON-NLS-1$
		}
		result.append(", serialNumber: "); //$NON-NLS-1$
		if (serialNumberESet) {
			result.append(serialNumber);
		} else {
			result.append("<unset>"); //$NON-NLS-1$
		}
		result.append(')');
		return result.toString();
	}

} // MerchandiseImpl
