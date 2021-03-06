/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.recording.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.addElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.Fan;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeSetOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Tests for multiattributeset operations.
 *
 * @author wesendon
 */
public class MultiAttributeSetTest extends ESTest {

	private static final String FOO_BAR3_COM = "foo@bar3.com"; //$NON-NLS-1$
	private static final String FOO_BAR2_COM = "foo@bar2.com"; //$NON-NLS-1$
	private static final String FOO_BAR1_COM = "foo@bar1.com"; //$NON-NLS-1$
	private static final String NEW_VALUE = "newValue"; //$NON-NLS-1$
	private static final String THIRD = "third"; //$NON-NLS-1$
	private static final String SECOND = "second"; //$NON-NLS-1$
	private static final String FIRST = "first"; //$NON-NLS-1$
	private static final String INSERTED = "inserted"; //$NON-NLS-1$
	private static final String STRINGS = "strings"; //$NON-NLS-1$
	private static final String SETTED_VALUE = "settedValue"; //$NON-NLS-1$
	private static final String OLD_VALUE = "oldValue"; //$NON-NLS-1$

	private TestElement element;

	/**
	 * Set value test.
	 */
	@Test
	public void setValueToFilledTest() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				element = Create.testElement();
				ProjectUtil.addElement(getProjectSpace().toAPI(), element);
				element.getStrings().add(OLD_VALUE);
				clearOperations();
			}
		}.run(false);

		assertTrue(element.getStrings().size() == 1);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				element.getStrings().set(0, SETTED_VALUE);
			}
		}.run(false);

		final List<AbstractOperation> operations = forceGetOperations();

		assertTrue(element.getStrings().size() == 1);
		assertTrue(element.getStrings().get(0).equals(SETTED_VALUE));

		assertTrue(operations.size() == 1);
		assertTrue(operations.get(0) instanceof MultiAttributeSetOperation);
	}

	/**
	 * Apply setoperation to element.
	 */
	@Test
	public void applyValueToFilledTest() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				final TestElement testElement = Create.testElement();
				addElement(getProjectSpace().toAPI(), testElement);

				testElement.getStrings().add(OLD_VALUE);
				assertTrue(testElement.getStrings().size() == 1);

				final MultiAttributeSetOperation operation = OperationsFactory.eINSTANCE
					.createMultiAttributeSetOperation();
				operation.setFeatureName(STRINGS);
				operation.setIndex(0);
				operation.setNewValue(INSERTED);
				operation.setOldValue(OLD_VALUE);
				operation.setModelElementId(ModelUtil.getProject(testElement).getModelElementId(testElement));

				operation.apply(getProject());

				assertEquals(1, testElement.getStrings().size());
				assertTrue(testElement.getStrings().get(0).equals(INSERTED));
			}
		}.run(false);
	}

	/**
	 * apply setoperation with wrong index.
	 */
	@Test
	public void applyValueToFilledWrongIndexTest() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				final TestElement testElement = Create.testElement();
				addElement(getProjectSpace().toAPI(), testElement);
				testElement.getStrings().add(OLD_VALUE);
				assertTrue(testElement.getStrings().size() == 1);

				final MultiAttributeSetOperation operation = OperationsFactory.eINSTANCE
					.createMultiAttributeSetOperation();
				operation.setFeatureName(STRINGS);
				operation.setIndex(42);
				operation.setNewValue(INSERTED);
				operation.setOldValue(OLD_VALUE);
				operation.setModelElementId(ModelUtil.getProject(testElement).getModelElementId(testElement));

				operation.apply(getProject());

				assertTrue(testElement.getStrings().size() == 1);
				assertTrue(testElement.getStrings().get(0).equals(OLD_VALUE));
			}
		}.run(false);
	}

	/**
	 * Apply to filled list.
	 */
	@Test
	public void applyValueToMultiFilledTest() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				final TestElement testElement = Create.testElement();
				addElement(getProjectSpace().toAPI(), testElement);

				testElement.getStrings().addAll(Arrays.asList(FIRST, SECOND, THIRD));
				assertTrue(testElement.getStrings().size() == 3);

				final MultiAttributeSetOperation operation = OperationsFactory.eINSTANCE
					.createMultiAttributeSetOperation();
				operation.setFeatureName(STRINGS);
				operation.setIndex(1);
				operation.setNewValue(INSERTED);
				operation.setOldValue(SECOND);
				operation.setModelElementId(ModelUtil.getProject(testElement).getModelElementId(testElement));

				operation.apply(getProject());

				assertEquals(3, testElement.getStrings().size());
				assertTrue(testElement.getStrings().get(0).equals(FIRST));
				assertTrue(testElement.getStrings().get(1).equals(INSERTED));
				assertTrue(testElement.getStrings().get(2).equals(THIRD));
			}
		}.run(false);
	}

	/**
	 * Set and reverse.
	 */
	@Test
	public void setAndReverseTest() {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				element = Create.testElement();
				addElement(getProjectSpace().toAPI(), element);
				element.getStrings().add(OLD_VALUE);
				clearOperations();
			}
		}.run(false);

		assertTrue(element.getStrings().size() == 1);
		assertTrue(element.getStrings().get(0).equals(OLD_VALUE));

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				element.getStrings().set(0, NEW_VALUE);
				assertTrue(element.getStrings().size() == 1);
				assertTrue(element.getStrings().get(0).equals(NEW_VALUE));
			}
		}.run(false);

		final AbstractOperation op = forceGetOperations().get(0);
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				AbstractOperation operation;
				operation = op.reverse();
				operation.apply(getProject());
			}
		}.run(false);

		assertTrue(element.getStrings().size() == 1);
		assertTrue(element.getStrings().get(0).equals(OLD_VALUE));
	}

	@Test
	public void unsetMultiAttributeTest() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.getEMails().add(FOO_BAR1_COM);
				fan.getEMails().add(FOO_BAR2_COM);
				fan.getEMails().add(FOO_BAR3_COM);
				assertEquals(3, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		clearOperations();
		final Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetEMails();
				assertEquals(0, fan.getEMails().size());
				assertTrue(!fan.isSetEMails());
			}
		}.run(false);

		final List<AbstractOperation> operations = forceGetOperations();

		assertEquals(2, operations.size());

		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp = (MultiAttributeOperation) operation;

		final AbstractOperation operation2 = operations.get(1);
		assertEquals(true, operation2 instanceof MultiAttributeSetOperation);
		final MultiAttributeSetOperation multAttSetOp = (MultiAttributeSetOperation) operation2;

		final ModelElementId fanId = ModelUtil.getProject(fan).getModelElementId(fan);
		assertEquals(fanId, multAttOp.getModelElementId());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				multAttOp.apply(secondProject);
				multAttSetOp.apply(secondProject);
			}
		}.run(false);
		assertEquals(0, ((Fan) secondProject.getModelElements().get(0)).getEMails().size());
		assertTrue(!((Fan) secondProject.getModelElements().get(0)).isSetEMails());
		assertTrue(ModelUtil.areEqual(getProject(), secondProject));

	}

	@Test
	public void reverseUnsetMultiAttributeTest() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.getEMails().add(FOO_BAR1_COM);
				fan.getEMails().add(FOO_BAR2_COM);
				fan.getEMails().add(FOO_BAR3_COM);
				assertEquals(3, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		clearOperations();
		final Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetEMails();
				assertEquals(0, fan.getEMails().size());
				assertTrue(!fan.isSetEMails());
			}
		}.run(false);

		final List<AbstractOperation> operations = forceGetOperations();

		assertEquals(2, operations.size());

		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp = (MultiAttributeOperation) operation;

		final AbstractOperation operation2 = operations.get(1);
		assertEquals(true, operation2 instanceof MultiAttributeSetOperation);
		final MultiAttributeSetOperation multAttSetOp = (MultiAttributeSetOperation) operation2;

		final ModelElementId fanId = ModelUtil.getProject(fan).getModelElementId(fan);
		assertEquals(fanId, multAttOp.getModelElementId());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				multAttSetOp.reverse().apply(getProject());
				multAttOp.reverse().apply(getProject());

			}
		}.run(false);

		assertEquals(3, fan.getEMails().size());
		assertTrue(fan.isSetEMails());
		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}

	@Test
	public void doubleReverseUnsetMultiAttributeTest() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				fan.getEMails().add(FOO_BAR1_COM);
				fan.getEMails().add(FOO_BAR2_COM);
				fan.getEMails().add(FOO_BAR3_COM);
				assertEquals(3, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		clearOperations();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.unsetEMails();
				assertEquals(0, fan.getEMails().size());
				assertTrue(!fan.isSetEMails());
			}
		}.run(false);

		final Project secondProject = ModelUtil.clone(getProject());

		final List<AbstractOperation> operations = forceGetOperations();

		assertEquals(2, operations.size());

		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp = (MultiAttributeOperation) operation;

		final AbstractOperation operation2 = operations.get(1);
		assertEquals(true, operation2 instanceof MultiAttributeSetOperation);
		final MultiAttributeSetOperation multAttSetOp = (MultiAttributeSetOperation) operation2;

		final ModelElementId fanId = ModelUtil.getProject(fan).getModelElementId(fan);
		assertEquals(fanId, multAttOp.getModelElementId());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				multAttOp.reverse().reverse().apply(getProject());
				multAttSetOp.reverse().reverse().apply(getProject());
			}
		}.run(false);

		assertEquals(0, fan.getEMails().size());
		assertTrue(!fan.isSetEMails());
		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}

	@Test
	public void reverseSetOfUnsettedMultiAttributeTest() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				assertEquals(0, fan.getEMails().size());
				assertTrue(!fan.isSetEMails());
			}
		}.run(false);

		clearOperations();
		final Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.getEMails().add(FOO_BAR1_COM);
				fan.getEMails().add(FOO_BAR2_COM);
				fan.getEMails().add(FOO_BAR3_COM);
				assertEquals(3, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		final List<AbstractOperation> operations = forceGetOperations();

		assertEquals(3, operations.size());

		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp1 = (MultiAttributeOperation) operation;

		final AbstractOperation operation2 = operations.get(1);
		assertEquals(true, operation2 instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp2 = (MultiAttributeOperation) operation2;

		final AbstractOperation operation3 = operations.get(2);
		assertEquals(true, operation3 instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp3 = (MultiAttributeOperation) operation3;

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				multAttOp3.reverse().apply(getProject());
				multAttOp2.reverse().apply(getProject());
				multAttOp1.reverse().apply(getProject());
			}
		}.run(false);

		assertEquals(0, fan.getEMails().size());
		assertTrue(!fan.isSetEMails());
		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}

	@Test
	public void setUnsetMultiAttributeToEmpty() {
		final Fan fan = BowlingFactory.eINSTANCE.createFan();

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				getProject().addModelElement(fan);
				assertEquals(0, fan.getEMails().size());
				assertTrue(!fan.isSetEMails());
			}
		}.run(false);

		clearOperations();
		final Project secondProject = ModelUtil.clone(getProject());

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				fan.getEMails().clear();
				assertEquals(0, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		final List<AbstractOperation> operations = forceGetOperations();

		assertEquals(1, operations.size());
		final AbstractOperation operation = operations.get(0);
		assertEquals(true, operation instanceof MultiAttributeOperation);
		final MultiAttributeOperation multAttOp = (MultiAttributeOperation) operation;

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				multAttOp.apply(secondProject);
				assertEquals(0, fan.getEMails().size());
				assertTrue(fan.isSetEMails());
			}
		}.run(false);

		assertTrue(ModelUtil.areEqual(getProject(), secondProject));
	}
}
