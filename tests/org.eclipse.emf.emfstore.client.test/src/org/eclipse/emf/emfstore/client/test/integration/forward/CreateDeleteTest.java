/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Hodaie
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.integration.forward;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.SerializationException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.Test;

/**
 * @author Hodaie
 */

public class CreateDeleteTest extends IntegrationTest {

	private long randomSeed = 1;

	/**
	 * create a random ME and change one of its attributes.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void createAndChangeAttributeTest() throws SerializationException, ESException {
		System.out.println("CreateAndChangeAttributeTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {

				testHelper.doCreateAndChangeAttribute();

			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Create a random ME and change one of its attributes, then changes one of its references, then changes one of its
	 * attributes, and again changes one of its references.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	// @Test
	public void createAndMultipleChangeTest() throws SerializationException, ESException {
		System.out.println("CreateAndMultipleChangeTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {

				testHelper.doCreateAndMultipleChange();

			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Create a random ME and change one of its references.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void createAndChangeRefTest() throws SerializationException, ESException {
		System.out.println("CreateAndChangeRefTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {

				testHelper.doCreateAndChangeRef();

			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Create a random ME. Change one of its non-containment references. Delete ME.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void createChangeRefDeleteTest() throws SerializationException, ESException {
		System.out.println("CreateChangeRefDeleteTest");
		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testHelper.doCreateChangeRefDelete();
			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Create a random ME. Delete ME.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void createDeleteTest() throws SerializationException, ESException {
		System.out.println("CreateDeleteTest");
		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testHelper.doCreateDelete();
			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Delete a random ME. Revert delete.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void deleteAndRevertDeleteTest() throws SerializationException, ESException {
		System.out.println("DeleteAndRevertDeleteTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		testHelper.doDeleteAndRevert();

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));
	}

	/**
	 * Delete a random ME.
	 * 
	 * @throws ESException ESException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void deleteTest() throws SerializationException, ESException {

		System.out.println("DeleteTest");
		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testHelper.doDelete();

			}

		}.run(getTestProject(), false);

		commitChanges();
		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

}
