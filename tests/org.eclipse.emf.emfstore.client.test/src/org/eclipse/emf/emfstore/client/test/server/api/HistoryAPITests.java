/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.server.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.test.testmodel.TestElement;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESHistoryInfoImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.query.ESRangeQueryImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PathQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.util.HistoryQueryBuilder;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESHistoryInfo;
import org.eclipse.emf.emfstore.server.model.query.ESRangeQuery;
import org.junit.Test;

/**
 * Branches for history test
 * 
 * <pre>
 *     b1    b2    b3
 * 
 * v7              o
 * 				/  |
 * v6         /    o
 * v5 	      o   / 
 * v4	o	  |  /
 * v3	|	  o
 * v2	o	 /
 * v1 	 \ o
 *     	   |
 * v0  	   o
 * </pre>
 * 
 * @author wesendon
 * 
 */
public class HistoryAPITests extends CoreServerTest {

	final static public PrimaryVersionSpec[] versions = { Versions.createPRIMARY("trunk", 0),
		Versions.createPRIMARY("trunk", 1), Versions.createPRIMARY("b1", 2), Versions.createPRIMARY("b2", 3),
		Versions.createPRIMARY("b1", 4), Versions.createPRIMARY("b2", 5), Versions.createPRIMARY("b3", 6),
		Versions.createPRIMARY("b3", 7) };

	final static public String[] elementNames = { "v0", "v1", "v2", "v3", "v4", "v5", "v6", "v7" };

	final static public String[] branches = { "b1", "b2", "b3" };

	public static ProjectSpace createHistory(CoreServerTest p) throws ESException {
		ProjectSpace ps = p.getProjectSpace();
		// v0
		p.createTestElement(elementNames[0]);
		assertEquals(versions[0], p.share(ps));

		// v1
		rename(ps, 1);
		assertEquals(versions[1], p.commit(ps));
		ProjectSpace ps2 = p.reCheckout(ps);

		// v2
		rename(ps, 2);
		assertEquals(versions[2], p.branch(ps, branches[0]));

		// v3
		rename(ps2, 3);
		assertEquals(versions[3], p.branch(ps2, branches[1]));

		// v4
		rename(ps, 4);
		assertEquals(versions[4], p.commit(ps));

		// v5
		rename(ps2, 5);
		assertEquals(versions[5], p.commit(ps2));

		// v6
		ProjectSpace thirdBranch = p.checkout(ps.toAPI().getRemoteProject(), versions[3]);
		rename(thirdBranch, 6);
		assertEquals(versions[6], p.branch(thirdBranch, branches[2]));

		// v7
		p.mergeWithBranch(thirdBranch, versions[5], 1);
		rename(thirdBranch, 7);
		assertEquals(versions[7], p.commit(thirdBranch));

		return ps;
	}

	private static void rename(final ProjectSpace ps, final int nameIndex) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				TestElement element = (TestElement) ps.getProject().getModelElements().get(0);
				element.setName(elementNames[nameIndex]);
			}
		}.run(ps.getProject(), false);
	}

	@Test
	public void rangequery() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQueryImpl> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[3], 5, 25, false, false,
			false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(4, result.size());
		assertEquals(versions[5].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(2).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(3).getPrimarySpec());
	}

	@Test
	public void rangequeryAllVersions() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[3], 5,
			25, true, false, false, false);
		List<ESHistoryInfo> result = ps.toAPI().getHistoryInfos(rangeQuery.toAPI(),
			new NullProgressMonitor());

		assertEquals(8, result.size());
		assertEquals(versions[7].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[6].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[5].toAPI(), result.get(2).getPrimarySpec());
		assertEquals(versions[4].toAPI(), result.get(3).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(4).getPrimarySpec());
		assertEquals(versions[2].toAPI(), result.get(5).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(6).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(7).getPrimarySpec());
	}

	@Test
	public void rangequeryIncludeCp() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[3], 1,
			25, false, false, false, true);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(4, result.size());
		assertEquals(versions[5].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(2).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(3).getPrimarySpec());

		assertTrue(result.get(0).getChangePackage() != null);
		assertTrue(result.get(1).getChangePackage() != null);
		assertTrue(result.get(2).getChangePackage() != null);
		// version 0
		assertTrue(result.get(3).getChangePackage() == null);
	}

	@Test
	public void rangequeryNoUpper() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[5], 5,
			1, false, false, false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(2, result.size());
		assertEquals(versions[5].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(1).getPrimarySpec());
	}

	@Test
	public void rangequeryNoLower() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[0], 1,
			20, false, false, false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(2, result.size());
		assertEquals(versions[1].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(1).getPrimarySpec());
	}

	@Test
	public void rangequeryLimitZero() throws ESException {
		ProjectSpace ps = createHistory(this);
		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[0], 0,
			0, false, false, false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(1, result.size());
		assertEquals(versions[0].toAPI(), result.get(0).getPrimarySpec());
	}

	@Test
	public void rangequeryIncoming() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[7], 0,
			2, false, true, false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(3, result.size());
		assertEquals(versions[7].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[6].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[5].toAPI(), result.get(2).getPrimarySpec());
	}

	@Test
	public void rangequeryOutgoing() throws ESException {
		ProjectSpace ps = createHistory(this);

		RangeQuery<ESRangeQuery<?>> rangeQuery = HistoryQueryBuilder.rangeQuery(versions[3], 2,
			0, false, false, true, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(rangeQuery.toAPI(), new NullProgressMonitor());

		assertEquals(3, result.size());
		assertEquals(versions[6].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[5].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(2).getPrimarySpec());
	}

	@Test
	public void pathQuery() throws ESException {
		ProjectSpace ps = createHistory(this);

		PathQuery pathQuery = HistoryQueryBuilder.pathQuery(versions[0], versions[5], false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(pathQuery.toAPI(), new NullProgressMonitor());

		assertEquals(4, result.size());
		assertEquals(versions[0].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(2).getPrimarySpec());
		assertEquals(versions[5].toAPI(), result.get(3).getPrimarySpec());
	}

	@Test
	public void pathQueryInverse() throws ESException {
		ProjectSpace ps = createHistory(this);

		PathQuery pathQuery = HistoryQueryBuilder.pathQuery(versions[5], versions[0], false, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(pathQuery.toAPI(), new NullProgressMonitor());

		assertEquals(4, result.size());
		assertEquals(versions[5].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(2).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(3).getPrimarySpec());
	}

	@Test
	public void pathQueryAllVersions() throws ESException {
		ProjectSpace ps = createHistory(this);

		PathQuery pathQuery = HistoryQueryBuilder.pathQuery(versions[1], versions[3], true, false);
		List<ESHistoryInfo> result = ps.toAPI()
			.getHistoryInfos(pathQuery.toAPI(), new NullProgressMonitor());

		assertEquals(3, result.size());
		assertEquals(versions[1].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[2].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(2).getPrimarySpec());
	}

	@Test(expected = InvalidVersionSpecException.class)
	public void invalidPathQuery() throws ESException {
		ProjectSpace ps = createHistory(this);
		PathQuery pathQuery = HistoryQueryBuilder.pathQuery(versions[2], versions[3], false, false);
		ps.toAPI().getHistoryInfos(pathQuery.toAPI(), new NullProgressMonitor());
	}

	@Test
	public void meQuery() throws ESException {
		ProjectSpace ps = createHistory(this);
		EObject element = ps.getProject().getModelElements().get(0);
		ModelElementId id = ps.getProject().getModelElementId(element);

		ModelElementQuery modelelementQuery = HistoryQueryBuilder
			.modelelementQuery(versions[3], id, 0, 0, false, false);
		List<ESHistoryInfo> result = ps.toAPI().getHistoryInfos(modelelementQuery.toAPI(),
			new NullProgressMonitor());

		assertEquals(1, result.size());
		assertEquals(versions[3].toAPI(), result.get(0).getPrimarySpec());
	}

	@Test
	public void meQueryLimit() throws ESException {
		ProjectSpace ps = createHistory(this);
		EObject element = ps.getProject().getModelElements().get(0);
		ModelElementId id = ps.getProject().getModelElementId(element);

		ModelElementQuery modelelementQuery = HistoryQueryBuilder
			.modelelementQuery(versions[1], id, 0, 1, false, false);
		List<ESHistoryInfo> result = ps.toAPI().getHistoryInfos(modelelementQuery.toAPI(),
			new NullProgressMonitor());

		assertEquals(2, result.size());
		assertEquals(versions[1].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[0].toAPI(), result.get(1).getPrimarySpec());
	}

	@Test
	public void meQueryDifferentBranch() throws ESException {
		ProjectSpace ps = createHistory(this);
		EObject element = ps.getProject().getModelElements().get(0);
		ModelElementId id = ps.getProject().getModelElementId(element);

		ModelElementQuery modelelementQuery = HistoryQueryBuilder
			.modelelementQuery(versions[3], id, 1, 1, false, false);
		List<ESHistoryInfo> result = ps.toAPI().getHistoryInfos(modelelementQuery.toAPI(),
			new NullProgressMonitor());

		assertEquals(3, result.size());
		assertEquals(versions[5].toAPI(), result.get(0).getPrimarySpec());
		assertEquals(versions[3].toAPI(), result.get(1).getPrimarySpec());
		assertEquals(versions[1].toAPI(), result.get(2).getPrimarySpec());
	}

	@Test
	public void meQueryDifferentBranchIncludeAll() throws ESException {
		ProjectSpace ps = createHistory(this);
		EObject element = ps.getProject().getModelElements().get(0);
		ModelElementId id = ps.getProject().getModelElementId(element);

		ModelElementQuery modelElementQuery = HistoryQueryBuilder.modelelementQuery(versions[3], id, 1, 1, true, false);
		List<ESHistoryInfo> result = ps.toAPI().getHistoryInfos(modelElementQuery.toAPI(),
			new NullProgressMonitor());

		assertEquals(3, result.size());
		assertEquals(versions[4].toAPI(), ((ESHistoryInfoImpl) result.get(0)).getPrimarySpec());
		assertEquals(versions[3].toAPI(), ((ESHistoryInfoImpl) result.get(1)).getPrimarySpec());
		assertEquals(versions[2].toAPI(), ((ESHistoryInfoImpl) result.get(2)).getPrimarySpec());
	}
}
