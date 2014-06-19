package org.eclipse.emf.emfstore.client.ui.historybrowsercomparator;

import java.util.Calendar;

import org.eclipse.compare.CompareUI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.ui.editor.ModelCompareEditorInput;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.ui.ESCompare;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * Implementation of {@link ESCompare} using the EMF Compare Framework to compare to {@link Project}s.
 * 
 * @author jsommerfeldt
 * 
 */
public class EMFCompareComparator implements ESCompare {

	private ComparisonResourceSnapshot snapshot;

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESCompare#compare(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void compare(EObject e1, EObject e2) {
		if (!(e1 instanceof Project) || !(e2 instanceof Project)) {
			throw new IllegalArgumentException("The objects have to be Projects!");
		}

		try {
			snapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
			snapshot.setDate(Calendar.getInstance().getTime());
			final MatchModel match = MatchService.doContentMatch(e1, e2, null);
			snapshot.setMatch(match);
			snapshot.setDiff(DiffService.doDiff(match, false));
		} catch (final InterruptedException e) {
			ModelUtil.logException(e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESCompare#display()
	 */
	public void display() {
		CompareUI.openCompareEditor(new ModelCompareEditorInput(snapshot), true);
	}
}