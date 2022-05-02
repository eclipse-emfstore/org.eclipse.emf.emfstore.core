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
package org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.client.common.UnknownEMFStoreWorkloadCommand;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.ProjectSpaceContainer;
import org.eclipse.emf.emfstore.internal.client.observers.DeleteProjectSpaceObserver;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.client.ui.controller.AbstractEMFStoreUIController;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.views.changes.ChangePackageVisualizationHelper;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.graph.IPlotCommit;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.graph.PlotCommitProvider;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.graph.PlotLane;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.graph.SWTPlotRenderer;
import org.eclipse.emf.emfstore.internal.client.ui.views.scm.SCMContentProvider;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ModelElementIdToEObjectMappingImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESHistoryInfoImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ModelElementQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.RangeQuery;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.util.HistoryQueryBuilder;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESHistoryInfo;
import org.eclipse.emf.emfstore.server.model.query.ESHistoryQuery;
import org.eclipse.emf.emfstore.server.model.query.ESModelElementQuery;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.ViewPart;

/**
 * This eclipse views displays the version history of EMFStore.
 *
 * @author wesendon
 * @author Aumann
 * @author Hodaie
 * @author Shterev
 *
 */
// TODO: review setInput methods
public class HistoryBrowserView extends ViewPart implements ProjectSpaceContainer {

	// icons
	private static final String EXPAND_ALL_GIF = "icons/expandall.gif"; //$NON-NLS-1$
	private static final String COLLAPSE_ALL_GIF = "icons/collapseall.gif"; //$NON-NLS-1$

	// Config
	private static final int UPPER_LIMIT = 10;
	private static final int LOWER_LIMIT = 20;

	// model state
	private ProjectSpace projectSpace;
	private EObject modelElement;

	private List<HistoryInfo> infos;
	private PrimaryVersionSpec centerVersion;
	private boolean showAllVersions;

	// viewer
	private TreeViewerWithModelElementSelectionProvider viewer;
	private SWTPlotRenderer renderer;
	private Link noProjectHint;

	// Columns
	private TreeViewerColumn changeColumn;
	private TreeViewerColumn branchColumn;
	private TreeViewerColumn commitColumn;
	private TreeViewerColumn authorColumn;
	private static final int BRANCH_COLUMN = 1;

	// content/label provider
	private SCMContentProvider contentProvider;
	private PlotCommitProvider commitProvider;
	private AdapterFactoryLabelProvider adapterFactoryLabelProvider;
	private HistorySCMLabelProvider changeLabel;
	private LogMessageColumnLabelProvider commitLabel;

	// actions
	private ExpandCollapseAction expandAndCollapse;
	private boolean isUnlinkedFromNavigator;
	private Action showAllBranches;
	private Action filterTagAction;

	private String filteredTag;

	// installed listeners/observers
	private DeleteProjectSpaceObserver deleteProjectSpaceObserver;

	// changes can be transferred with historyInfos. However, this must be avoided if the server sends
	// FileBasedChangePackages which the client can not open
	private final static Boolean isLazyLoadingChanges;
	private static final String ENABLE_LAZY_LOADING_OF_CHANGE_PACKAGES_EXTENSION_POINT = "org.eclipse.emf.emfstore.client.ui.enableLazyLoadingOfChangePackages"; //$NON-NLS-1$

	static {
		Boolean result;
		try {
			result = new ESExtensionPoint(ENABLE_LAZY_LOADING_OF_CHANGE_PACKAGES_EXTENSION_POINT, true)
				.getBoolean("enabled", false); //$NON-NLS-1$
			// set system property to be in sync with extension point and to be queryable for menu point enablement
			System.setProperty(ENABLE_LAZY_LOADING_OF_CHANGE_PACKAGES_EXTENSION_POINT, result.toString()); // $NON-NLS-1$
		} catch (final ESExtensionPointException e) {
			// if no extension is available, check for system property
			result = Boolean.getBoolean(ENABLE_LAZY_LOADING_OF_CHANGE_PACKAGES_EXTENSION_POINT); // $NON-NLS-1$
		}
		isLazyLoadingChanges = result;
	}

	/**
	 * {@inheritDoc}
	 */
	public ProjectSpace getProjectSpace() {
		return projectSpace;
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayoutFactory.fillDefaults().applyTo(parent);

		initNoProjectHint(parent);

		// init viewer
		viewer = new TreeViewerWithModelElementSelectionProvider(parent);
		viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (filteredTag == null || filteredTag.length() == 0) {
					return true;
				}
				if (!HistoryInfo.class.isInstance(element)) {
					return true;
				}
				for (final TagVersionSpec tagVersionSpec : HistoryInfo.class.cast(element).getTagSpecs()) {
					if (filteredTag.equalsIgnoreCase(tagVersionSpec.getName())) {
						return true;
					}
				}
				return false;
			}
		} });
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());
		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		ColumnViewerToolTipSupport.enableFor(viewer);
		getSite().setSelectionProvider(viewer);

		initMenuManager();

		changeColumn = createColumn(Messages.HistoryBrowserView_Changes, 250);
		branchColumn = createColumn(Messages.HistoryBrowserView_Branches, 150);
		commitColumn = createColumn(Messages.HistoryBrowserView_CommitMessage, 250);
		authorColumn = createColumn(Messages.HistoryBrowserView_AuthorAndDate, 250);

		initContentAndLabelProvider();
		initGraphRenderer();
		initToolBar();
		initProjectDeleteListener();
	}

	private void initContentAndLabelProvider() {
		contentProvider = new SCMContentProvider();
		commitProvider = new PlotCommitProvider();
		viewer.setContentProvider(contentProvider);

		changeLabel = new HistorySCMLabelProvider();
		changeColumn.setLabelProvider(changeLabel);
		branchColumn.setLabelProvider(new BranchGraphLabelProvider());
		commitLabel = new LogMessageColumnLabelProvider();
		commitColumn.setLabelProvider(commitLabel);
		authorColumn.setLabelProvider(new CommitInfoColumnLabelProvider());

		adapterFactoryLabelProvider = new AdapterFactoryLabelProvider(new ComposedAdapterFactory(
			ComposedAdapterFactory.Descriptor.Registry.INSTANCE));
	}

	private void initGraphRenderer() {
		renderer = new SWTPlotRenderer(viewer.getTree().getDisplay());
		// XXX SWT.PaintItem is not available in RAP, so we are using the numerical constant here
		viewer.getTree().addListener(/* SWT.PaintItem */42, new Listener() {
			public void handleEvent(Event event) {
				doPaint(event);
			}

		});
	}

	private void doPaint(Event event) {
		if (event.index != BRANCH_COLUMN) {
			return;
		}

		Object data;
		TreeItem currItem = (TreeItem) event.item;
		data = currItem.getData();
		boolean isCommitItem = true;

		while (!(data instanceof HistoryInfo)) {
			isCommitItem = false;
			currItem = currItem.getParentItem();
			if (currItem == null) {
				// no history info in parent hierarchy, do not draw.
				// Happens e.g. if the user deactivates showing the commits
				return;
			}
			data = currItem.getData();
		}

		final IPlotCommit c = commitProvider.getCommitFor((HistoryInfo) data, !isCommitItem);
		final PlotLane lane = c.getLane();
		if (lane != null && lane.getSaturatedColor().isDisposed()) {
			return;
		}
		// if (highlight != null && c.has(highlight))
		// event.gc.setFont(hFont);
		// else
		event.gc.setFont(PlatformUI.getWorkbench().getDisplay().getSystemFont());

		renderer.paint(event, c);
	}

	private TreeViewerColumn createColumn(String label, int width) {
		final TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.MULTI);
		column.getColumn().setText(label);
		column.getColumn().setWidth(width);
		return column;
	}

	// TODO review this stuff
	private void initMenuManager() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.add(new Separator("additions")); //$NON-NLS-1$
		getSite().registerContextMenu(menuMgr, viewer);
		final Control control = viewer.getControl();
		final Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Reloads the view with the current parameters.
	 */
	public void refresh() {
		RunInUI.run(new Callable<Void>() {
			public Void call() throws Exception {
				setDescription();
				return null;
			}
		});
		resetExpandCollapse();
		if (projectSpace == null || modelElement == null) {
			RunInUI.run(new ESVoidCallable() {
				@Override
				public void run() {
					viewer.setInput(Collections.EMPTY_LIST);
				}
			});
			return;
		}
		infos = getHistoryInfos();
		addBaseVersionTag(infos);
		resetProviders(infos);
		viewer.setInput(infos);
	}

	/**
	 * Refresh a history info. Useful if a change package has been loaded lazily.
	 *
	 * @param historyInfo the {@link HistoryInfo} to refresh
	 */
	public void refresh(HistoryInfo historyInfo) {
		viewer.refresh(historyInfo);
	}

	private void addBaseVersionTag(List<HistoryInfo> infos) {
		final HistoryInfo historyInfo = getHistoryInfo(projectSpace.getBaseVersion());
		if (historyInfo != null) {
			historyInfo.getTagSpecs().add(Versions.createTAG(
				ESVersionSpec.BASE, ESVersionSpec.GLOBAL));
		}
	}

	private void resetExpandCollapse() {
		expandAndCollapse.setChecked(false);
		expandAndCollapse.setImage(true);
	}

	private void setDescription() {
		if (projectSpace == null) {
			setContentDescription(Messages.HistoryBrowserView_NoSelection);
			showNoProjectHint(true);
			return;
		}
		String label = Messages.HistoryBrowserView_HistoryFor;
		if (modelElement == projectSpace) {
			label += projectSpace.getProjectName() + " [" + projectSpace.getBaseVersion().getBranch() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			label += adapterFactoryLabelProvider.getText(modelElement);
		}
		if (filteredTag != null && filteredTag.length() > 0) {
			label += MessageFormat.format(" [{0}]", //$NON-NLS-1$
				MessageFormat.format(Messages.HistoryBrowserView_TaggedWithOnly, filteredTag));
		}
		showNoProjectHint(false);
		setContentDescription(label);
	}

	private List<HistoryInfo> getHistoryInfos() {
		final Shell shell = getViewSite().getShell();

		final List<HistoryInfo> result = new AbstractEMFStoreUIController<List<HistoryInfo>>(shell, true,
			false) {
			@Override
			public List<HistoryInfo> doRun(final IProgressMonitor monitor) throws ESException {
				return new UnknownEMFStoreWorkloadCommand<List<HistoryInfo>>(monitor) {
					@Override
					public List<HistoryInfo> run(final IProgressMonitor monitor) throws ESException {
						final List<HistoryInfo> historyInfosFromServer = getHistoryInfosFromServer(monitor);
						return historyInfosFromServer;
					}
				}.execute(); // UnknownEMFStoreWorkloadCommand
			}
		}.execute(); // AbstractEMFStoreUIController

		return result != null ? result : new ArrayList<HistoryInfo>();
	}

	private List<HistoryInfo> getHistoryInfosFromServer(final IProgressMonitor monitor)
		throws ESException {

		return new ServerCall<List<HistoryInfo>>(projectSpace) {
			@Override
			protected List<HistoryInfo> run() throws ESException {
				monitor.beginTask(Messages.HistoryBrowserView_FetchingHistory, 100);
				final List<HistoryInfo> historyInfos = getLocalChanges();
				monitor.worked(10);
				if (projectSpace != modelElement) {
					final List<ESHistoryInfo> infos = modelElementQuery();
					for (final ESHistoryInfo info : infos) {
						historyInfos.add(((ESHistoryInfoImpl) info).toInternalAPI());
					}
				} else {
					// TODO monitor
					final List<ESHistoryInfo> infos = rangeQuery();
					for (final ESHistoryInfo info : infos) {
						historyInfos.add(((ESHistoryInfoImpl) info).toInternalAPI());
					}
				}
				monitor.worked(90);
				return historyInfos;
			}

		}.execute(); // ServerCall

	}

	private List<ESHistoryInfo> modelElementQuery() throws ESException {
		final ModelElementQuery query = HistoryQueryBuilder.modelelementQuery(
			centerVersion,
			Arrays.asList(ModelUtil.getModelElementId(modelElement)),
			UPPER_LIMIT,
			LOWER_LIMIT,
			showAllVersions,
			!isLazyLoadingChanges);
		// TODO: proivde util method
		final ESHistoryQuery<ESModelElementQuery> api = query.toAPI();
		final List<ESHistoryInfo> infos = projectSpace.toAPI().getHistoryInfos(api, new NullProgressMonitor());
		return infos;
	}

	private List<ESHistoryInfo> rangeQuery() throws ESException {
		final RangeQuery<?> rangeQuery = HistoryQueryBuilder
			.rangeQuery(
				centerVersion,
				UPPER_LIMIT,
				LOWER_LIMIT,
				showAllVersions, true, true, !isLazyLoadingChanges);
		final List<ESHistoryInfo> infos = projectSpace.toAPI().getHistoryInfos(
			rangeQuery.toAPI(),
			new NullProgressMonitor());
		return infos;
	}

	private List<HistoryInfo> getLocalChanges() {

		final ArrayList<HistoryInfo> revisions = new ArrayList<HistoryInfo>();
		if (projectSpace != null) {
			// TODO: add a feature "hide local revision"
			final HistoryInfo localHistoryInfo = VersioningFactory.eINSTANCE.createHistoryInfo();
			final AbstractChangePackage changePackage = projectSpace.getLocalChangePackage(false);
			// filter for modelelement, do additional sanity check as the
			// project space could've been also selected
			if (modelElement != null && projectSpace.getProject().contains(modelElement)) {
				final Set<AbstractOperation> operationsToRemove = new LinkedHashSet<AbstractOperation>();
				final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
				try {
					for (final AbstractOperation operation : operations.iterable()) {

						if (!operation.getAllInvolvedModelElements().contains(
							ModelUtil.getProject(modelElement).getModelElementId(modelElement))) {
							operationsToRemove.add(operation);
						}
					}
				} finally {
					operations.close();
				}
				// TODO: LCP - bummer..
				// changePackage.getOperations().removeAll(operationsToRemove);
			}
			// TODO: LCP
			// localHistoryInfo.setChangePackage(changePackage);
			final PrimaryVersionSpec versionSpec = VersioningFactory.eINSTANCE.createPrimaryVersionSpec();
			versionSpec.setIdentifier(-1);
			localHistoryInfo.setPrimarySpec(versionSpec);
			localHistoryInfo.setPreviousSpec(ModelUtil.clone(projectSpace.getBaseVersion()));
			revisions.add(localHistoryInfo);
		}
		return revisions;
	}

	private void resetProviders(List<HistoryInfo> infos) {
		// TODO: LCP
		final ArrayList<AbstractChangePackage> cps = new ArrayList<AbstractChangePackage>();
		for (final HistoryInfo info : infos) {
			if (info.getChangePackage() != null) {
				cps.add(info.getChangePackage());
			}
		}

		final ChangePackageVisualizationHelper newHelper = new ChangePackageVisualizationHelper(
			new ModelElementIdToEObjectMappingImpl(projectSpace.getProject(), cps));
		changeLabel.setProject(projectSpace.getProject());
		changeLabel.setChangePackageVisualizationHelper(newHelper);
		commitLabel.setProject(projectSpace.getProject());
		commitLabel.setChangePackageVisualizationHelper(newHelper);
		commitProvider.reset(infos);
	}

	/**
	 * Displays the history for the given input.
	 *
	 * @param input eobject in projectspace or projectspace itself
	 */
	public void setInput(EObject input) {
		if (viewer.getControl().isDisposed()) {
			return;
		}
		try {
			if (input instanceof ProjectSpace) {
				projectSpace = (ProjectSpace) input;
			} else if (input != null) {
				final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
				projectSpace = workspace.toInternalAPI().getProjectSpace(ModelUtil.getProject(input));
			} else {
				projectSpace = null;
			}
			modelElement = input;

			showAll(true);
			setCenterVersion();
			refresh();
		} catch (final ESException e) {
		}
	}

	/**
	 * Sets a {@link ESLocalProject} as an input for the view. The history for the input will be shown.
	 *
	 * @param localProject the project to show the history for.
	 */
	public void setInput(ESLocalProject localProject) {
		setInput((EObject) localProject);
	}

	private void showAll(boolean show) {
		showAllVersions = show;
		showAllBranches.setChecked(show);
	}

	private void setCenterVersion() {
		if (projectSpace != null) {
			centerVersion = projectSpace.getBaseVersion();
		} else {
			centerVersion = null;
		}
	}

	private void showNoProjectHint(boolean b) {
		noProjectHint.setVisible(b);
		noProjectHint.getParent().layout();
	}

	private void initNoProjectHint(final Composite parent) {
		noProjectHint = new Link(parent, SWT.WRAP);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(noProjectHint);

		noProjectHint
			.setText(Messages.HistoryBrowserView_SelectProjectOrCallHistory);
		noProjectHint.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				final ElementListSelectionDialog elsd = new ElementListSelectionDialog(parent.getShell(),
					new ESBrowserLabelProvider());
				final List<ProjectSpace> relevantProjectSpaces = new ArrayList<ProjectSpace>();
				final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
				for (final ProjectSpace ps : workspace.toInternalAPI().getProjectSpaces()) {
					if (ps.getUsersession() != null) {
						relevantProjectSpaces.add(ps);
					}
				}
				elsd.setElements(relevantProjectSpaces.toArray());
				elsd.setMultipleSelection(false);
				elsd.setTitle(Messages.HistoryBrowserView_SelectProjectTitle);
				elsd.setMessage(Messages.HistoryBrowserView_SelectProjectMsg);
				if (Window.OK == elsd.open()) {
					for (final Object o : elsd.getResult()) {
						final ProjectSpace resultSelection = (ProjectSpace) o;
						if (resultSelection != null) {
							setInput(resultSelection);
						}
						break;
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void initProjectDeleteListener() {
		deleteProjectSpaceObserver = new DeleteProjectSpaceObserver() {
			public void projectSpaceDeleted(ProjectSpace projectSpace) {
				if (HistoryBrowserView.this.projectSpace == projectSpace) {
					setInput((EObject) null);
				}
			}
		};
		ESWorkspaceProviderImpl.getObserverBus().register(deleteProjectSpaceObserver);
	}

	@Override
	public void dispose() {
		if (deleteProjectSpaceObserver != null) {
			ESWorkspaceProviderImpl.getObserverBus().unregister(deleteProjectSpaceObserver);
		}
		adapterFactoryLabelProvider.dispose();
		changeLabel.dispose();
		commitLabel.dispose();
		super.dispose();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * ====================================================================
	 *
	 * TOOLBAR.
	 *
	 * ====================================================================
	 */

	private void initToolBar() {
		final IActionBars bars = getViewSite().getActionBars();
		final IToolBarManager menuManager = bars.getToolBarManager();

		addRefreshAction(menuManager);
		addShowAllBranchesAction(menuManager);
		addExpandAllAndCollapseAllAction(menuManager);
		addNextAndPreviousAction(menuManager);
		addJumpToRevisionAction(menuManager);
		addFilterTagAction(menuManager);
		addLinkWithNavigatorAction(menuManager);
	}

	private void addRefreshAction(IToolBarManager menuManager) {
		final Action refresh = new Action() {
			@Override
			public void run() {
				refresh();
			}

		};
		refresh.setImageDescriptor(Activator.getImageDescriptor("/icons/refresh.png")); //$NON-NLS-1$
		refresh.setToolTipText(Messages.HistoryBrowserView_Refresh);
		menuManager.add(refresh);
	}

	private void addNextAndPreviousAction(IToolBarManager menuManager) {
		final Action prev = new Action() {
			@Override
			public void run() {
				centerVersion = prevNextCenter(false);
				refresh();
			}

		};
		prev.setImageDescriptor(Activator.getImageDescriptor("/icons/prev.png")); //$NON-NLS-1$
		prev.setToolTipText(Messages.HistoryBrowserView_PreviousItems);
		menuManager.add(prev);

		final Action next = new Action() {
			@Override
			public void run() {
				centerVersion = prevNextCenter(true);
				refresh();
			}

		};
		next.setImageDescriptor(Activator.getImageDescriptor("/icons/next.png")); //$NON-NLS-1$
		next.setToolTipText(Messages.HistoryBrowserView_NextItems);
		menuManager.add(next);
	}

	private PrimaryVersionSpec prevNextCenter(boolean next) {
		if (projectSpace == null || centerVersion == null) {
			return null;
		}
		// all versions pages only based on version numbers
		if (showAllVersions) {
			return biggestOrSmallesInfo(next);
		}
		// if center is not on the selected branch (base version of ps) jump to base before paging
		if (!projectSpace.getBaseVersion().getBranch().equals(centerVersion.getBranch())) {
			return projectSpace.getBaseVersion();
		}

		// search next or prev version on given branch
		HistoryInfo current = getHistoryInfo(centerVersion);
		while (current != null) {
			if (next) {
				if (current.getNextSpec().size() > 0) {
					final HistoryInfo nextInfo = getHistoryInfo(current.getNextSpec().get(0));
					if (nextInfo == null) {
						return current.getPrimarySpec();
					}
					current = nextInfo;
				} else {
					break;
				}
			} else {
				if (current.getPreviousSpec() != null
					&& current.getPreviousSpec().getBranch().equals(projectSpace.getBaseVersion().getBranch())) {
					final HistoryInfo prevInfo = getHistoryInfo(current.getPreviousSpec());
					if (prevInfo == null) {
						return current.getPrimarySpec();
					}
					current = prevInfo;
				} else {
					break;
				}
			}
		}

		if (current == null) {
			return centerVersion;
		}
		return current.getPrimarySpec();
	}

	private HistoryInfo getHistoryInfo(PrimaryVersionSpec version) {
		if (version == null) {
			return null;
		}
		for (final HistoryInfo info : infos) {
			if (version.equals(info.getPrimarySpec())) {
				return info;
			}
		}
		return null;
	}

	private PrimaryVersionSpec biggestOrSmallesInfo(boolean biggest) {
		@SuppressWarnings("unchecked")
		final List<HistoryInfo> input = (List<HistoryInfo>) viewer.getInput();
		if (input == null) {
			return centerVersion;
		}
		final ArrayList<HistoryInfo> resultCandidates = new ArrayList<HistoryInfo>(input);
		PrimaryVersionSpec result = centerVersion;
		for (final HistoryInfo info : resultCandidates) {
			if (info.getPrimarySpec().getIdentifier() != -1
				&& (biggest && info.getPrimarySpec().compareTo(result) == 1 || !biggest && info.getPrimarySpec()
					.compareTo(result) == -1)) {
				result = info.getPrimarySpec();
			}
		}
		return result;
	}

	private void addLinkWithNavigatorAction(IToolBarManager menuManager) {
		isUnlinkedFromNavigator = Activator.getDefault().getDialogSettings().getBoolean("LinkWithNavigator"); //$NON-NLS-1$
		final Action linkWithNavigator = new Action(Messages.HistoryBrowserView_LinkWithNavigator, SWT.TOGGLE) {

			@Override
			public void run() {
				Activator.getDefault().getDialogSettings().put("LinkWithNavigator", !isChecked()); //$NON-NLS-1$
				isUnlinkedFromNavigator = !isChecked();
			}

		};
		linkWithNavigator.setImageDescriptor(Activator.getImageDescriptor("icons/link_with_editor.gif")); //$NON-NLS-1$
		linkWithNavigator.setToolTipText("Link with Navigator"); //$NON-NLS-1$
		linkWithNavigator.setChecked(!isUnlinkedFromNavigator);
		menuManager.add(linkWithNavigator);
	}

	private void addShowAllBranchesAction(IToolBarManager menuManager) {
		showAllBranches = new Action("", SWT.TOGGLE) { //$NON-NLS-1$
			@Override
			public void run() {
				showAllVersions = isChecked();
				refresh();
			}

		};
		showAllBranches.setImageDescriptor(Activator.getImageDescriptor("icons/arrow_branch.png")); //$NON-NLS-1$
		showAllBranches.setToolTipText(Messages.HistoryBrowserView_ShowAllBranches);
		showAllBranches.setChecked(true);
		menuManager.add(showAllBranches);
	}

	private void addFilterTagAction(IToolBarManager menuManager) {
		filterTagAction = new Action("", SWT.TOGGLE) { //$NON-NLS-1$

			@Override
			public void run() {
				if (filterTagAction.isChecked()) {
					final InputDialog inputDialog = new InputDialog(getSite().getShell(),
						Messages.HistoryBrowserView_FilterByTagDialog,
						Messages.HistoryBrowserView_TagDialog,
						StringUtils.EMPTY,
						null);
					if (inputDialog.open() == Window.OK) {
						filteredTag = inputDialog.getValue();
					} else {
						filterTagAction.setChecked(false);
					}
				} else {
					filteredTag = null;
				}
				refresh();
			}

		};
		filterTagAction.setImageDescriptor(Activator.getImageDescriptor("icons/find.png")); //$NON-NLS-1$
		filterTagAction.setToolTipText(Messages.HistoryBrowserView_FilterByTagAction);
		filterTagAction.setChecked(false);
		menuManager.add(filterTagAction);
	}

	private void addJumpToRevisionAction(IToolBarManager menuManager) {
		final Action jumpTo = new Action() {
			@Override
			public void run() {
				final InputDialog inputDialog = new InputDialog(getSite().getShell(),
					Messages.HistoryBrowserView_GoToRevision,
					Messages.HistoryBrowserView_Revision,
					StringUtils.EMPTY,
					null);
				if (inputDialog.open() == Window.OK) {
					if (projectSpace != null) {
						final PrimaryVersionSpec versionSpec = resolveVersion(inputDialog.getValue());
						if (versionSpec != null) {
							showAll(true);
							centerVersion = versionSpec;
							refresh();
						}
					}
				}
			}

		};
		jumpTo.setImageDescriptor(Activator.getImageDescriptor("/icons/magnifier.png")); //$NON-NLS-1$
		jumpTo.setToolTipText(Messages.HistoryBrowserView_GoToRevisionToolTip);
		menuManager.add(jumpTo);
	}

	private PrimaryVersionSpec resolveVersion(final String value) {
		return new AbstractEMFStoreUIController<PrimaryVersionSpec>(getViewSite().getShell()) {
			@Override
			public PrimaryVersionSpec doRun(IProgressMonitor monitor) throws ESException {
				return new UnknownEMFStoreWorkloadCommand<PrimaryVersionSpec>(monitor) {
					@Override
					public PrimaryVersionSpec run(IProgressMonitor monitor) throws ESException {
						try {
							return projectSpace.resolveVersionSpec(Versions.createPRIMARY(
								VersionSpec.GLOBAL, Integer.parseInt(value)), new NullProgressMonitor());
						} catch (final ESException e) {
							EMFStoreMessageDialog.showExceptionDialog(
								Messages.HistoryBrowserView_VersionDoesNotExist, e);
						} catch (final NumberFormatException e) {
							MessageDialog.openError(getSite().getShell(), Messages.HistoryBrowserView_Error,
								Messages.HistoryBrowserView_NumericValueExpected);
						}
						return null;
					}
				}.execute();
			}
		}.execute();
	}

	private void addExpandAllAndCollapseAllAction(IToolBarManager menuManager) {
		final ImageDescriptor expandImg = Activator.getImageDescriptor(EXPAND_ALL_GIF);
		final ImageDescriptor collapseImg = Activator.getImageDescriptor(COLLAPSE_ALL_GIF);

		expandAndCollapse = new ExpandCollapseAction(StringUtils.EMPTY, SWT.TOGGLE, expandImg, collapseImg);
		expandAndCollapse.setImageDescriptor(expandImg);
		expandAndCollapse.setToolTipText(Messages.HistoryBrowserView_ExpandCollapseToggle);
		menuManager.add(expandAndCollapse);
	}

	/**
	 * Expand/Collapse action.
	 *
	 * @author wesendon
	 */
	private final class ExpandCollapseAction extends Action {
		private final ImageDescriptor expandImg;
		private final ImageDescriptor collapseImg;

		private ExpandCollapseAction(String text, int style, ImageDescriptor expandImg, ImageDescriptor collapseImg) {
			super(text, style);
			this.expandImg = expandImg;
			this.collapseImg = collapseImg;
		}

		@Override
		public void run() {
			if (!isChecked()) {
				setImage(true);
				viewer.collapseAll();
			} else {
				setImage(false);
				viewer.expandToLevel(2);
			}
		}

		public void setImage(boolean expand) {
			setImageDescriptor(expand ? expandImg : collapseImg);
		}
	}

	/**
	 * Treeviewer that provides a model element selection for selected
	 * operations and mode element ids.
	 *
	 * @author koegel
	 */
	private final class TreeViewerWithModelElementSelectionProvider extends TreeViewer {
		private TreeViewerWithModelElementSelectionProvider(Composite parent) {
			super(parent, SWT.MULTI);
		}

		@Override
		protected Widget internalExpand(Object elementOrPath, boolean expand) {
			// TODO Auto-generated method stub
			return super.internalExpand(elementOrPath, expand);
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.jface.viewers.AbstractTreeViewer#getSelection()
		 */
		@Override
		public ISelection getSelection() {
			final Control control = getControl();

			if (control == null || control.isDisposed()) {
				return super.getSelection();
			}

			final Widget[] items = getSelection(getControl());
			if (items.length != 1) {
				return super.getSelection();
			}

			final Widget item = items[0];
			final Object data = item.getData();
			if (data == null) {
				return super.getSelection();
			}

			// TODO: remove assignment
			final Object element = data;
			EObject selectedModelElement = null;

			if (element instanceof CompositeOperation) {
				selectedModelElement = handleCompositeOperation((CompositeOperation) element);
			} else if (projectSpace != null && element instanceof AbstractOperation) {
				selectedModelElement = handleAbstractOperation((AbstractOperation) element);
			} else if (element instanceof ProjectSpace) {
				selectedModelElement = ((ProjectSpace) element).getProject();
			} else if (projectSpace != null && element instanceof ModelElementId
				&& projectSpace.getProject().contains((ModelElementId) element)) {
				selectedModelElement = projectSpace.getProject().getModelElement((ModelElementId) element);
			} else if (projectSpace != null && projectSpace.getProject().contains((EObject) element)) {
				selectedModelElement = (EObject) element;
			}

			if (selectedModelElement != null) {
				return new StructuredSelection(selectedModelElement);
			}

			return super.getSelection();
		}

		private EObject handleCompositeOperation(CompositeOperation op) {
			final AbstractOperation mainOperation = op.getMainOperation();
			if (projectSpace != null && mainOperation != null) {
				final ModelElementId modelElementId = mainOperation.getModelElementId();
				final EObject modelElement = projectSpace.getProject().getModelElement(modelElementId);
				return modelElement;
			}

			return null;
		}

		private EObject handleAbstractOperation(AbstractOperation op) {
			final ModelElementId modelElementId = op.getModelElementId();
			final EObject modelElement = projectSpace.getProject().getModelElement(modelElementId);
			return modelElement;
		}
	}

}
