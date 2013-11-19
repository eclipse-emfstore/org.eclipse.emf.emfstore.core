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
package org.eclipse.emf.emfstore.client.test.conflictDetection.merging;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.test.testmodel.TestElement;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts.DeletionConflict;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.junit.Test;

public class DeleteMergeTest extends MergeTest {

	@Test
	public void attVsDel() {
		final TestElement element = getTestElement();

		final MergeCase mc = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setName("Blub");
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiAttVsDel() {
		final TestElement element = getTestElement();

		final MergeCase mc = newMergeCase(element);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).getStrings().add("Blub");
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(MultiAttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void attVsDelDifferentNC() {
		final TestElement element = getTestElement();
		final TestElement element2 = getTestElement();

		final MergeCase mc = newMergeCase(element, element2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setName("Blub");
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element2));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(null);
	}

	@Test
	public void singleVsDel() {
		final TestElement element = getTestElement();
		final TestElement link = getTestElement();

		final MergeCase mc = newMergeCase(element, link);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).setReference(mc.getMyItem(link));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(SingleReferenceOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiRefVsDel() {
		final TestElement element = getTestElement();
		final TestElement link = getTestElement();

		final MergeCase mc = newMergeCase(element, link);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(element).getReferences().add(mc.getMyItem(link));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(element));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(MultiReferenceOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void multiRefContainmentVsDel() {
		final TestElement parent = getTestElement();
		final TestElement parent2 = getTestElement();
		final TestElement child = getTestElement();
		parent.getContainedElements().add(child);

		final MergeCase mc = newMergeCase(parent, parent2);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(parent2).getContainedElements().add(mc.getMyItem(child));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				EcoreUtil.delete(mc.getTheirItem(parent));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(CompositeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class).andNoOtherTheirOps();
	}

	@Test
	public void attVsDelInSteps() {
		final TestElement parent = getTestElement();
		final TestElement child = getTestElement();
		final TestElement child2 = getTestElement();
		parent.getContainedElements().add(child);
		child.getContainedElements().add(child2);

		final MergeCase mc = newMergeCase(parent);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(child2).setName("Ja.");
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(child).getContainedElements().remove(mc.getTheirItem(child2));
				mc.getTheirItem(parent).getContainedElements().remove(mc.getTheirItem(child));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class);
	}

	@Test
	public void attVsDelIndirectInSteps() {
		final TestElement parent = getTestElement("parent");
		final TestElement child = getTestElement("child");
		final TestElement child2 = getTestElement("child2");
		final TestElement child3 = getTestElement("child3");
		parent.getContainedElements().add(child);
		child.getContainedElements().add(child2);
		child2.getContainedElements().add(child3);

		final MergeCase mc = newMergeCase(parent);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getMyItem(child3).setName("Ja.");
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				mc.getTheirItem(child).getContainedElements().remove(mc.getTheirItem(child2));
				mc.getTheirItem(parent).getContainedElements().remove(mc.getTheirItem(child));
			}
		}.run(getProjectSpace().getContentEditingDomain(), false);

		mc.hasConflict(DeletionConflict.class)
			// my
			.myIs(AttributeOperation.class).andNoOtherMyOps()
			// theirs
			.theirsIs(CreateDeleteOperation.class);
	}
}