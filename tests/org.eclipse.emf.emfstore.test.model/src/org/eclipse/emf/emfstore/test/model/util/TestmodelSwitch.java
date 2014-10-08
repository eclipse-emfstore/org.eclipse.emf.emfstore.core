/**
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 */
package org.eclipse.emf.emfstore.test.model.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.eclipse.emf.emfstore.test.model.TestType;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapContainment;
import org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each
 * class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.emfstore.test.model.TestmodelPackage
 * @generated
 */
public class TestmodelSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected static TestmodelPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TestmodelSwitch() {
		if (modelPackage == null)
		{
			modelPackage = TestmodelPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage)
		{
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		List<EClass> eSuperTypes = theEClass.getESuperTypes();
		return eSuperTypes.isEmpty() ?
			defaultCase(theEObject) :
			doSwitch(eSuperTypes.get(0), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
	 * result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID)
		{
		case TestmodelPackage.TEST_ELEMENT: {
			TestElement testElement = (TestElement) theEObject;
			T result = caseTestElement(testElement);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.TEST_ELEMENT_TO_STRING_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<TestElement, String> testElementToStringMap = (Map.Entry<TestElement, String>) theEObject;
			T result = caseTestElementToStringMap(testElementToStringMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.STRING_TO_STRING_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>) theEObject;
			T result = caseStringToStringMap(stringToStringMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.TEST_ELEMENT_TO_TEST_ELEMENT_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<TestElement, TestElement> testElementToTestElementMap = (Map.Entry<TestElement, TestElement>) theEObject;
			T result = caseTestElementToTestElementMap(testElementToTestElementMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.STRING_TO_TEST_ELEMENT_MAP: {
			@SuppressWarnings("unchecked")
			Map.Entry<String, TestElement> stringToTestElementMap = (Map.Entry<String, TestElement>) theEObject;
			T result = caseStringToTestElementMap(stringToTestElementMap);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.TEST_TYPE: {
			TestType testType = (TestType) theEObject;
			T result = caseTestType(testType);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_NON_CONTAINMENT: {
			TypeWithFeatureMapNonContainment typeWithFeatureMapNonContainment = (TypeWithFeatureMapNonContainment) theEObject;
			T result = caseTypeWithFeatureMapNonContainment(typeWithFeatureMapNonContainment);
			if (result == null)
				result = caseTestType(typeWithFeatureMapNonContainment);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case TestmodelPackage.TYPE_WITH_FEATURE_MAP_CONTAINMENT: {
			TypeWithFeatureMapContainment typeWithFeatureMapContainment = (TypeWithFeatureMapContainment) theEObject;
			T result = caseTypeWithFeatureMapContainment(typeWithFeatureMapContainment);
			if (result == null)
				result = caseTestType(typeWithFeatureMapContainment);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestElement(TestElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Element To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Element To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestElementToStringMap(Map.Entry<TestElement, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToStringMap(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Element To Test Element Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Element To Test Element Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestElementToTestElementMap(Map.Entry<TestElement, TestElement> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To Test Element Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To Test Element Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToTestElementMap(Map.Entry<String, TestElement> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Test Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Test Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTestType(TestType object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Type With Feature Map Non Containment</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Type With Feature Map Non Containment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTypeWithFeatureMapNonContainment(TypeWithFeatureMapNonContainment object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Type With Feature Map Containment</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Type With Feature Map Containment</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTypeWithFeatureMapContainment(TypeWithFeatureMapContainment object)
	{
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * 
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} // TestmodelSwitch
