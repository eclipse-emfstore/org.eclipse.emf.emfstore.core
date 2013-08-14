/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.changeTracking.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.Fan;
import org.eclipse.emf.emfstore.client.test.WorkspaceTest;
import org.eclipse.emf.emfstore.client.test.model.requirement.RequirementFactory;
import org.eclipse.emf.emfstore.client.test.model.requirement.UseCase;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.UnsupportedNotificationException;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnsetType;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationsCanonizer;
import org.junit.Test;

/**
 * Tests the Attribute Operation.
 * 
 * @author koegel
 */
public class AttributeOperationTest extends WorkspaceTest {

	/**
	 * Change an attribute and check the generated operation.
	 * 
	 * @throws UnsupportedOperationException on test fail
	 * @throws UnsupportedNotificationException on test fail
	 */
	@Test
	public void changeAttribute() throws UnsupportedOperationException, UnsupportedNotificationException {

		final UseCase useCase = RequirementFactory.eINSTANCE.createUseCase();

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().addModelElement(useCase);

				clearOperations();

				useCase.setName("newName");
				assertEquals("newName", useCase.getName());
			}
		}.run(getProject(), false);

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("new UseCase", attributeOperation.getOldValue());
		assertEquals("newName", attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());

		ModelElementId useCaseId = ModelUtil.getProject(useCase).getModelElementId(useCase);

		assertEquals(useCaseId, attributeOperation.getModelElementId());

	}

	/**
	 * Change an attribute twice and check the generated operations after cannonization.
	 * 
	 * @throws UnsupportedOperationException on test fail
	 * @throws UnsupportedNotificationException on test fail
	 */
	@Test
	public void changeAttributeTwice() throws UnsupportedOperationException, UnsupportedNotificationException {
		final UseCase useCase = RequirementFactory.eINSTANCE.createUseCase();

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().addModelElement(useCase);

				clearOperations();

				useCase.setName("newName");
			}
		}.run(getProject(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				useCase.setName("otherName");
				assertEquals("otherName", useCase.getName());
			}
		}.run(getProject(), false);

		final List<AbstractOperation> operations = getProjectSpace().getOperations();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				OperationsCanonizer.canonize(operations);
			}
		}.run(getProject(), false);

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("new UseCase", attributeOperation.getOldValue());
		assertEquals("otherName", attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());

		ModelElementId useCaseId = ModelUtil.getProject(useCase).getModelElementId(useCase);

		assertEquals(useCaseId, attributeOperation.getModelElementId());
	}

	/**
	 * Change an attribute and reverse the operation and check the result.
	 * 
	 * @throws UnsupportedOperationException on test fail
	 * @throws UnsupportedNotificationException on test fail
	 */
	@Test
	public void changeAttributeAndReverse() throws UnsupportedOperationException, UnsupportedNotificationException {

		final UseCase useCase = RequirementFactory.eINSTANCE.createUseCase();

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().addModelElement(useCase);
				useCase.setName("oldName");

				clearOperations();

				useCase.setName("newName");
				assertEquals("newName", useCase.getName());
			}
		}.run(getProject(), false);

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("oldName", attributeOperation.getOldValue());
		assertEquals("newName", attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());

		ModelElementId useCaseId = ModelUtil.getProject(useCase).getModelElementId(useCase);

		assertEquals(useCaseId, attributeOperation.getModelElementId());

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				AbstractOperation reverse = operation.reverse();
				reverse.apply(getProject());
				assertEquals(true, reverse instanceof AttributeOperation);
				AttributeOperation reversedAttributeOperation = (AttributeOperation) reverse;
				assertEquals("newName", reversedAttributeOperation.getOldValue());
				assertEquals("oldName", reversedAttributeOperation.getNewValue());
				assertEquals("name", reversedAttributeOperation.getFeatureName());
				ModelElementId useCaseId = ModelUtil.getProject(useCase).getModelElementId(useCase);
				assertEquals(useCaseId, reversedAttributeOperation.getModelElementId());
			}
		}.run(getProject(), false);

		assertEquals("oldName", useCase.getName());
	}

	/**
	 * Test if attributeOperation.reverse().reverse() is a noop.
	 * 
	 * @throws UnsupportedOperationException on test fail
	 * @throws UnsupportedNotificationException on test fail
	 * @throws IOException
	 */
	@Test
	public void changeAttributeDoubleReversal() throws UnsupportedOperationException, UnsupportedNotificationException,
		IOException {

		final UseCase useCase = RequirementFactory.eINSTANCE.createUseCase();

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getProject().addModelElement(useCase);
				useCase.setName("oldName");

				clearOperations();

				useCase.setName("newName");
				assertEquals("newName", useCase.getName());
			}
		}.run(getProject(), false);

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);

		assertEquals(true, operation instanceof AttributeOperation);
		final AttributeOperation attributeOperation = (AttributeOperation) operation;

		AttributeOperation cmpOperation = (AttributeOperation) attributeOperation.reverse().reverse();

		assertEquals(attributeOperation.getFeatureName(), cmpOperation.getFeatureName());
		assertEquals(attributeOperation.getModelElementId(), cmpOperation.getModelElementId());
		assertEquals(attributeOperation.getNewValue(), cmpOperation.getNewValue());
		assertEquals(attributeOperation.getOldValue(), cmpOperation.getOldValue());

		Project expectedProject = ModelUtil.clone(getProject());

		assertTrue(ModelUtil.areEqual(getProject(), expectedProject));

		final AbstractOperation r = attributeOperation.reverse();
		final AbstractOperation rr = r.reverse();

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				r.apply(getProject());
				rr.apply(getProject());
			}
		}.run(getProject(), false);

		assertTrue(ModelUtil.areEqual(getProject(), expectedProject));

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				attributeOperation.reverse().apply(getProject());
				attributeOperation.reverse().reverse().apply(getProject());
			}
		}.run(getProject(), false);

		assertTrue(ModelUtil.areEqual(getProject(), expectedProject));

		Project loadedProject = ModelUtil.loadEObjectFromResource(
			org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE.getModelPackage().getProject(),
			getProject()
				.eResource().getURI(), false);

		assertTrue(ModelUtil.areEqual(loadedProject, expectedProject));
	}

	@Test
	public void unsetAttribute() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.setName("Fan");
				clearOperations();
			}
		}.run(getProject(), false);

		Project secondProject = ModelUtil.clone(getProject());

		// Test unsetting name
		assertEquals(true, fan.isSetName());
		assertEquals("Fan", fan.getName());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetName();
			}
		}.run(getProject(), false);

		assertEquals(false, fan.isSetName());
		assertEquals(null, fan.getName());

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("Fan", attributeOperation.getOldValue());
		assertEquals(null, attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());
		assertEquals(true, attributeOperation.getUnset() == UnsetType.IS_UNSET);

		attributeOperation.apply(secondProject);
		assertTrue(ModelUtil.areEqual(getProject(), secondProject));

		// test setting name to default value
		clearOperations();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.setName(null);
			}
		}.run(getProject(), false);

		assertEquals(true, fan.isSetName());
		assertEquals(null, fan.getName());

		operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		attributeOperation = (AttributeOperation) operation;

		assertEquals(null, attributeOperation.getOldValue());
		assertEquals(null, attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());
		assertEquals(false, attributeOperation.getUnset() == UnsetType.IS_UNSET);
	}

	@Test
	public void unsetAttributeReverse() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.setName("Fan");
				clearOperations();
			}
		}.run(getProject(), false);

		assertEquals("Fan", fan.getName());
		assertEquals(true, fan.isSetName());

		Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetName();
			}
		}.run(getProject(), false);

		assertEquals(false, fan.isSetName());
		assertEquals(null, fan.getName());

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		final AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("Fan", attributeOperation.getOldValue());
		assertEquals(null, attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());
		assertEquals(true, attributeOperation.getUnset() == UnsetType.IS_UNSET);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				attributeOperation.reverse().apply(getProject());
			}
		}.run(getProject(), false);

		assertEquals("Fan", fan.getName());
		assertEquals(true, fan.isSetName());

		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}

	@Test
	public void unsetAttributeDoubleReverse() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.setName("Fan");
				clearOperations();
			}
		}.run(getProject(), false);

		assertEquals("Fan", fan.getName());
		assertEquals(true, fan.isSetName());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetName();
			}
		}.run(getProject(), false);

		Project secondProject = ModelUtil.clone(getProject());

		assertEquals(false, fan.isSetName());
		assertEquals(null, fan.getName());

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		final AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals("Fan", attributeOperation.getOldValue());
		assertEquals(null, attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());
		assertEquals(true, attributeOperation.getUnset() == UnsetType.IS_UNSET);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				attributeOperation.reverse().reverse().apply(getProject());
			}
		}.run(getProject(), false);

		assertEquals(null, fan.getName());
		assertEquals(false, fan.isSetName());

		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}

	@Test
	public void setOfUnsettedAttributeReverse() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				clearOperations();
			}
		}.run(getProject(), false);

		assertEquals(null, fan.getName());
		assertEquals(false, fan.isSetName());

		Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.setName("Fan");
			}
		}.run(getProject(), false);

		assertEquals(true, fan.isSetName());
		assertEquals("Fan", fan.getName());

		List<AbstractOperation> operations = getProjectSpace().getOperations();

		assertEquals(1, operations.size());
		AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof AttributeOperation);
		final AttributeOperation attributeOperation = (AttributeOperation) operation;

		assertEquals(null, attributeOperation.getOldValue());
		assertEquals("Fan", attributeOperation.getNewValue());
		assertEquals("name", attributeOperation.getFeatureName());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				attributeOperation.reverse().apply(getProject());
			}
		}.run(getProject(), false);

		assertEquals(null, fan.getName());
		assertEquals(false, fan.isSetName());

		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}
}
