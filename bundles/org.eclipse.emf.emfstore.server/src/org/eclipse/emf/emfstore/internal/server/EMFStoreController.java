/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Maximilian Koegel - initial API and implementation
 * Johannes Faltermeier - URI related refactorings
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.emfstore.common.ESResourceSetProvider;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESPriorityComparator;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.connection.ConnectionHandler;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.XmlRpcAdminConnectionHandler;
import org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.XmlRpcConnectionHandler;
import org.eclipse.emf.emfstore.internal.server.core.AdminEmfStoreImpl;
import org.eclipse.emf.emfstore.internal.server.core.EMFStoreImpl;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.core.helper.EPackageHelper;
import org.eclipse.emf.emfstore.internal.server.core.helper.ResourceHelper;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.StorageException;
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;
import org.eclipse.emf.emfstore.internal.server.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.RolesFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.startup.MigrationManager;
import org.eclipse.emf.emfstore.internal.server.startup.PostStartupListener;
import org.eclipse.emf.emfstore.internal.server.startup.ServerHrefMigrator;
import org.eclipse.emf.emfstore.internal.server.startup.StartupListener;
import org.eclipse.emf.emfstore.internal.server.storage.ServerXMIResourceSetProvider;
import org.eclipse.emf.emfstore.server.ESDynamicModelProvider;
import org.eclipse.emf.emfstore.server.ESServerURIUtil;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt;
import org.eclipse.emf.emfstore.server.exceptions.ESServerInitException;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * The {@link EMFStoreController} is controlling startup and shutdown of the
 * EmfStore.
 *
 * @author koegel
 * @author wesendonk
 * @author jfaltermeier
 */
public class EMFStoreController implements IApplication, Runnable {

	private static final String EMFSTORE_TXT_FILE = "emfstore.txt"; //$NON-NLS-1$

	private static final String SUPERUSRE_DESCRIPTION = "default server admin (superuser)"; //$NON-NLS-1$

	private static final String SUPERUSER_LAST_NAME = "user"; //$NON-NLS-1$

	private static final String SUPERUSER_FIRST_NAME = "super"; //$NON-NLS-1$

	private static final String RESOURCE_SET_PROVIDER = "org.eclipse.emf.emfstore.server.resourceSetProvider"; //$NON-NLS-1$

	private static final String CONFIG_RESOURCE_KEY = "org.eclipse.emf.emfstore.server.configurationResource"; //$NON-NLS-1$

	private static final String EMFSTORE_COMMON_BUNDLE = "org.eclipse.emf.emfstore.common.model"; //$NON-NLS-1$

	private static final String ORG_ECLIPSE_EMF_EMFSTORE_SERVER_DYNAMIC_MODEL_PROVIDER = "org.eclipse.emf.emfstore.server.dynamicModelProvider"; //$NON-NLS-1$

	/**
	 * The period of time in seconds between executing the clean memory task.
	 */
	private static EMFStoreController instance;

	private EMFStore emfStore;
	private AdminEmfStore adminEmfStore;
	private AccessControl accessControl;
	private Set<ConnectionHandler<? extends EMFStoreInterface>> connectionHandlers;
	private ServerSpace serverSpace;
	private Resource resource;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public synchronized Object start(IApplicationContext context) throws FatalESException {
		run(true);
		instance = null;
		ModelUtil.logInfo(Messages.EMFStoreController_Server_Stopped);
		return IApplication.EXIT_OK;
	}

	/**
	 * Run the server.
	 *
	 * @param waitForTermination
	 *            true if the server should force the calling thread to wait for
	 *            its termination
	 * @throws FatalESException
	 *             if the server fails fatally
	 */
	public synchronized void run(boolean waitForTermination) throws FatalESException {
		if (instance != null) {
			throw new FatalESException(Messages.EMFStoreController_EMFStore_Controller_Already_Running);
		}

		ModelUtil.logInfo("[EMFStoreController] Running..."); //$NON-NLS-1$

		instance = this;

		serverHeader();

		initLogging();

		// copy es.properties file to workspace if not existent
		copyFileToWorkspace(ServerConfiguration.getConfFile(), ServerConfiguration.ES_PROPERTIES,
			Messages.EMFStoreController_Could_Not_Copy_Properties_File,
			Messages.EMFStoreController_Default_Properties_File_Copied);

		final Properties properties = initProperties();

		assureSuperUserPasswordIsSet(properties);

		logGeneralInformation();

		registerDynamicModels();

		serverSpace = initServerSpace(Boolean.getBoolean("emfstore.migration")); //$NON-NLS-1$

		if (serverSpace == null) {
			return;
		}

		initializeBranchesIfRequired(serverSpace);

		handleStartupListener();

		accessControl = initAccessControl(serverSpace);
		// TODO: ugly
		emfStore = EMFStoreImpl.createInterface(serverSpace, accessControl);
		adminEmfStore = new AdminEmfStoreImpl(serverSpace, accessControl);

		// copy keystore file to workspace if not existent
		copyFileToWorkspace(ServerConfiguration.getServerKeyStorePath(), ServerConfiguration.SERVER_KEYSTORE_FILE,
			Messages.EMFStoreController_Failed_To_Copy_Keystore, Messages.EMFStoreController_Keystore_Copied);

		connectionHandlers = initConnectionHandlers();

		handlePostStartupListener();
		registerShutdownHook();

		if (ServerConfiguration.isUserPasswordMigrationRequired()) {
			performDummyLogin();
		}

		ModelUtil.logInfo(Messages.EMFStoreController_Init_Complete);
		ModelUtil.logInfo(Messages.EMFStoreController_Server_Running);
		if (waitForTermination) {
			waitForTermination();
		}

	}

	/**
	 * Verify a dummy user in order to trigger an eager startup of the user verifiers.
	 */
	private void performDummyLogin() {
		final ClientVersionInfo clientVersionInfo = ModelFactory.eINSTANCE.createClientVersionInfo();
		clientVersionInfo.setVersion("1.0.0."); //$NON-NLS-1$ // actual version is unimportant
		getAccessControl().getLoginService().verifyUser("", "", clientVersionInfo.toAPI()); //$NON-NLS-1$//$NON-NLS-2$
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopServer();
			}
		});
	}

	private void logGeneralInformation() {
		ModelUtil.logInfo(Messages.EMFStoreController_Server_Home + ServerConfiguration.getServerHome());
		ModelUtil.logInfo(MessageFormat.format(
			Messages.EMFStoreController_JVM_Max_Memory, Runtime.getRuntime().maxMemory() / 1000000));
	}

	private void initializeBranchesIfRequired(ServerSpace serverSpace) throws FatalESException {
		ModelUtil.logInfo("[EMFStoreController] Initialize required branches."); //$NON-NLS-1$

		final String exitOnFailProperty = ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.STARTUP_EXIT_ON_BRANCH_INIT_ERROR,
			ServerConfiguration.STARTUP_EXIT_ON_BRANCH_INIT_ERROR_DEFAULT);
		final boolean exitOnFail = Boolean.TRUE.toString().equals(exitOnFailProperty);

		for (final ProjectHistory project : serverSpace.getProjects()) {

			if (project.getBranches().size() == 0) {
				ModelUtil
					.logInfo(MessageFormat.format("[EMFStoreController] Initialize branch for project {0} (ID: {1})", //$NON-NLS-1$
						project.getProjectName(), project.getProjectId()));

				if (project.getLastVersion() != null) {
					// create branch information
					final BranchInfo branchInfo = VersioningFactory.eINSTANCE.createBranchInfo();
					branchInfo.setName(VersionSpec.BRANCH_DEFAULT_NAME);

					branchInfo.setHead(ModelUtil.clone(project.getLastVersion().getPrimarySpec()));
					// set branch source to 0 since no branches can have existed
					branchInfo.setSource(ModelUtil.clone(Versions.createPRIMARY(VersionSpec.BRANCH_DEFAULT_NAME, 0)));
					project.getBranches().add(branchInfo);
					new ResourceHelper(serverSpace).save(project);
				} else {
					final String errorMessage = MessageFormat.format(
						"[EMFStoreController] Could not initiliaze branch because the project has no versions. Project: {0} (ID: {1})", //$NON-NLS-1$
						project.getProjectName(), project.getProjectId());
					ModelUtil.logError(errorMessage);
					if (exitOnFail) {
						throw new FatalESException(errorMessage);
					}
				}
			}
		}
	}

	// delegates loading of dynamic models to the resource set provider
	private void registerDynamicModels() {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			ORG_ECLIPSE_EMF_EMFSTORE_SERVER_DYNAMIC_MODEL_PROVIDER,
			true, new ESPriorityComparator("priority", true)); //$NON-NLS-1$
		final ESDynamicModelProvider dynamicModelProvider = extensionPoint.getElementWithHighestPriority().getClass(
			"class", ESDynamicModelProvider.class); //$NON-NLS-1$
		final List<EPackage> models = dynamicModelProvider.getDynamicModels();

		for (final EPackage model : models) {
			EPackage.Registry.INSTANCE.put(model.getNsURI(), model);
			final List<EPackage> packages = EPackageHelper.getAllSubPackages(model);
			for (final EPackage subPkg : packages) {
				EPackage.Registry.INSTANCE.put(subPkg.getNsURI(), subPkg);
			}
			ModelUtil.logInfo(
				MessageFormat.format(Messages.EMFStoreController_Dynamic_Model_Loaded, model.getNsURI()));
		}

	}

	private void initLogging() {
		Platform.getLog(Platform.getBundle(EMFSTORE_COMMON_BUNDLE)).addLogListener(new ILogListener() {

			public void logging(IStatus status, String plugin) {
				if (status.getSeverity() == IStatus.INFO) {
					System.out.println(status.getMessage());
				} else if (!status.isOK()) {
					System.err.println(status.getMessage());
					final Throwable exception = status.getException();
					if (exception != null) {
						exception.printStackTrace(System.err);
					}
				}
			}

		});
	}

	private void handleStartupListener() {
		final String property = ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.LOAD_STARTUP_LISTENER,
			ServerConfiguration.LOAD_STARTUP_LISTENER_DEFAULT);
		if (Boolean.TRUE.toString().equals(property)) {
			ModelUtil.logInfo("Notifying startup listener"); //$NON-NLS-1$
			for (final StartupListener listener : ServerConfiguration.getStartupListeners()) {
				listener.startedUp(serverSpace.getProjects());
			}
		}
	}

	private void handlePostStartupListener() {
		final String property = ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.LOAD_POST_STARTUP_LISTENER, ServerConfiguration.LOAD_STARTUP_LISTENER_DEFAULT);
		if (Boolean.TRUE.toString().equals(property)) {
			ModelUtil.logInfo("Notifying post startup listener"); //$NON-NLS-1$
			for (final PostStartupListener listener : ServerConfiguration.getPostStartupListeners()) {
				listener.postStartUp(serverSpace, accessControl, connectionHandlers);
			}
		}
	}

	private void copyFileToWorkspace(String target, String source, String failure, String success) {

		final File targetFile = new File(target);

		if (!targetFile.exists()) {
			// check if the custom configuration resources are provided and if,
			// copy them to place
			final ESExtensionPoint extensionPoint = new ESExtensionPoint(
				CONFIG_RESOURCE_KEY);
			final ESExtensionElement element = extensionPoint.getFirst();

			if (element != null) {

				final String attribute = element.getAttribute(targetFile.getName());

				if (attribute != null) {
					try {
						FileUtil.copyFile(new URL("platform:/plugin/" //$NON-NLS-1$
							+ element.getIConfigurationElement().getNamespaceIdentifier() + "/" + attribute) //$NON-NLS-1$
								.openConnection().getInputStream(),
							targetFile);
						return;
					} catch (final IOException e) {
						ModelUtil.logWarning(
							MessageFormat.format(Messages.EMFStoreController_Copy_From_To, source, target), e);
					}
				}
			}
			// Guess not, lets copy the default configuration resources
			try {
				FileUtil.copyFile(getClass().getResourceAsStream(source), targetFile);
			} catch (final IOException e) {
				ModelUtil.logWarning(
					MessageFormat.format("Copy of file from {0} to {1} failed.", source, target), e); //$NON-NLS-1$
			}
		}

	}

	private Set<ConnectionHandler<? extends EMFStoreInterface>> initConnectionHandlers() throws FatalESException {
		final Set<ConnectionHandler<? extends EMFStoreInterface>> connectionHandlers = new LinkedHashSet<ConnectionHandler<? extends EMFStoreInterface>>();

		// crate XML RPC connection handlers
		try {
			final XmlRpcConnectionHandler xmlRpcConnectionHander = new XmlRpcConnectionHandler();
			xmlRpcConnectionHander.init(emfStore, accessControl);
			connectionHandlers.add(xmlRpcConnectionHander);

			final XmlRpcAdminConnectionHandler xmlRpcAdminConnectionHander = new XmlRpcAdminConnectionHandler();
			xmlRpcAdminConnectionHander.init(adminEmfStore, accessControl);
			connectionHandlers.add(xmlRpcAdminConnectionHander);
		} catch (final ESServerInitException ex) {
			throw new FatalESException(Messages.EMFStoreController_ConnectionHandlersInitException, ex);
		}

		return connectionHandlers;
	}

	private ServerSpace initServerSpace(boolean migrationMode) throws FatalESException {

		final ESResourceSetProvider resourceSetProvider = getResourceSetProvider();

		final ResourceSet resourceSet = resourceSetProvider.getResourceSet();

		final URI serverspaceURI = ESServerURIUtil.createServerSpaceURI();

		// will be used only in case of ServerXMIResourceProvider, but maybe
		// a more generic mechanism to remove any corrupt projects would make sense
		if (!resourceSet.getURIConverter().exists(serverspaceURI, null)) {
			try {
				resource = resourceSet.createResource(serverspaceURI);
				final ServerSpace serverspace = ModelFactory.eINSTANCE.createServerSpace();
				resource.getContents().add(serverspace);
				ModelUtil.saveResource(resource, ModelUtil.getResourceLogger());
			} catch (final IOException e) {
				throw new FatalESException(Messages.EMFStoreController_Could_Not_Init_XMLResource, e);
			}
		} else {
			// hrefs are persisted differently in 1.1+ in comparison to 1.0
			// migrate, if needed, before loading
			if (resourceSetProvider instanceof ServerXMIResourceSetProvider) {
				if (!new ServerHrefMigrator().migrate()) {
					throw new FatalESException(Messages.EMFStoreController_Error_During_Migration);
				}
			}
			/* file has been fixed. check if metamodel migration is needed */
			if (migrationMode) {
				MigrationManager.migrate(getResourceSetProvider().getResourceSet(),
					!ServerConfiguration.useFileBasedChangePackageOnServer());
				return null;
			}
			resource = resourceSet.createResource(serverspaceURI);
		}

		try {
			resource.load(ModelUtil.getResourceLoadOptions());
		} catch (final IOException e) {
			throw new FatalESException(StorageException.NOLOAD, e);
		}

		ServerSpace result = null;
		final EList<EObject> contents = resource.getContents();
		for (final EObject content : contents) {
			if (content instanceof ServerSpace) {
				result = (ServerSpace) content;
				break;
			}
		}

		if (result != null) {
			result.setResource(resource);
		} else {
			// if no serverspace can be loaded, create one
			ModelUtil.logInfo(Messages.EMFStoreController_Creating_Initial_ServerSpace);
			result = ModelFactory.eINSTANCE.createServerSpace();

			result.setResource(resource);
			resource.getContents().add(result);

			try {
				result.save();
			} catch (final IOException e) {
				throw new FatalESException(StorageException.NOSAVE, e);
			}
		}

		return result;
	}

	/**
	 * @return
	 */
	private ESResourceSetProvider getResourceSetProvider() {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			RESOURCE_SET_PROVIDER,
			true, new ESPriorityComparator("priority", true)); //$NON-NLS-1$

		final ESResourceSetProvider resourceSetProvider = extensionPoint.getElementWithHighestPriority().getClass(
			"class", //$NON-NLS-1$
			ESResourceSetProvider.class);
		return resourceSetProvider;
	}

	/**
	 * Return the singleton instance of EmfStoreControler.
	 *
	 * @return the instance
	 */
	public static EMFStoreController getInstance() {
		return instance;
	}

	private static synchronized AccessControl initAccessControl(ServerSpace serverSpace) throws FatalESException {
		setSuperUser(serverSpace);
		return new AccessControl(serverSpace);
	}

	private static void setSuperUser(ServerSpace serverSpace) throws FatalESException {
		final String superuser = ServerConfiguration.getProperties().getProperty(ServerConfiguration.SUPER_USER,
			ServerConfiguration.SUPER_USER_DEFAULT);
		for (final ACUser user : serverSpace.getUsers()) {
			if (user.getName().equals(superuser)) {
				return;
			}
		}
		final ACUser superUser = AccesscontrolFactory.eINSTANCE.createACUser();
		superUser.setName(superuser);
		superUser.setFirstName(SUPERUSER_FIRST_NAME);
		superUser.setLastName(SUPERUSER_LAST_NAME);
		superUser.setDescription(SUPERUSRE_DESCRIPTION);
		superUser.getRoles().add(RolesFactory.eINSTANCE.createServerAdmin());
		serverSpace.getUsers().add(superUser);
		try {
			serverSpace.save();
		} catch (final IOException e) {
			throw new FatalESException(StorageException.NOSAVE, e);
		}
		ModelUtil.logInfo(Messages.EMFStoreController_Added_SuperUser + superuser);
	}

	private void assureSuperUserPasswordIsSet(final Properties properties) {

		final boolean unhashedPasswordWasPresent = properties.containsKey(ServerConfiguration.SUPER_USER_PASSWORD);

		String password = null;
		if (!unhashedPasswordWasPresent) {

			if (properties.containsKey(ServerConfiguration.SUPER_USER_PASSWORD_HASH) &&
				properties.containsKey(ServerConfiguration.SUPER_USER_PASSWORD_SALT)) {
				/* super user password not present but hash and salt -> all is fine */
				return;
			}

			/* no password is available in properties -> get password from user */
			final Scanner scanner = new Scanner(System.in);
			try {
				boolean passwordDidNotMatch = true;
				while (passwordDidNotMatch) {
					System.out.println(Messages.EMFStoreController_NoSuperUserPasswordPrompt);
					final String pw1 = scanner.nextLine();
					System.out.println(Messages.EMFStoreController_EnterSuperUserPasswordAgain);
					final String pw2 = scanner.nextLine();
					if (pw1 != null && pw1.equals(pw2)) {
						password = pw1;
						passwordDidNotMatch = false;
					} else {
						System.out.println(Messages.EMFStoreController_PasswordDidNotMatch);
					}
				}
			} finally {
				scanner.close();
			}
		} else {
			/* get unhashed password */
			password = properties.getProperty(ServerConfiguration.SUPER_USER_PASSWORD);
		}

		/* migrate non hashed password */
		final ESPasswordHashGenerator passwordHashGenerator = AccessControl.getESPasswordHashGenerator();
		final File propertyFile = new File(ServerConfiguration.getConfFile());
		FileOutputStream propertyFileOutputStream = null;
		try {
			propertyFileOutputStream = new FileOutputStream(propertyFile);
			final ESHashAndSalt hashAndSalt = passwordHashGenerator.hashPassword(password);
			properties.setProperty(ServerConfiguration.SUPER_USER_PASSWORD_SALT, hashAndSalt.getSalt());
			properties.setProperty(ServerConfiguration.SUPER_USER_PASSWORD_HASH, hashAndSalt.getHash());
			properties.remove(ServerConfiguration.SUPER_USER_PASSWORD);
			properties.store(propertyFileOutputStream, ""); //$NON-NLS-1$
		} catch (final FileNotFoundException ex) {
			ModelUtil.logWarning(Messages.EMFStoreController_PasswordHashFailFileNotFound, ex);
		} catch (final IOException ex) {
			ModelUtil.logWarning(Messages.EMFStoreController_PasswordHashFailIOException, ex);
		} finally {
			if (propertyFileOutputStream != null) {
				try {
					propertyFileOutputStream.close();
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}
		}

		if (unhashedPasswordWasPresent) {
			ServerConfiguration.initUserVerifierMigration();
		}
	}

	private Properties initProperties() {
		final File propertyFile = new File(ServerConfiguration.getConfFile());
		final Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(propertyFile);
			properties.load(fis);
			ServerConfiguration.setProperties(properties, false);
			ModelUtil.logInfo(
				MessageFormat.format(Messages.EMFStoreController_PropertyFile_Read, propertyFile.getAbsolutePath()));
		} catch (final IOException e) {
			ModelUtil.logWarning(Messages.EMFStoreController_Property_Init_Failed, e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				ModelUtil.logWarning(Messages.EMFStoreController_Closing_Of_Properties_File_Failed, e);
			}
		}

		return properties;
	}

	/**
	 * Stops the EMFStore gracefully.
	 */
	public void stopServer() {
		if (instance == null) {
			// server already has been stopped manually
			return;
		}
		wakeForTermination();
		// connection handlers may be null in case an exception has been thrown
		// while starting
		if (connectionHandlers != null) {

			final Object monitor = MonitorProvider.getInstance().getMonitor();

			synchronized (monitor) {
				for (final ConnectionHandler<? extends EMFStoreInterface> handler : connectionHandlers) {
					handler.stop();
				}
			}
		}
		ModelUtil.logInfo(Messages.EMFStoreController_Server_Was_Stopped);
		instance = null;
		wakeForTermination();
	}

	/**
	 * Shutdown EmfStore due to an fatal exception.
	 *
	 * @param exception
	 *            the fatal exception that triggered the shutdown
	 */
	public void shutdown(FatalESException exception) {
		ModelUtil.logWarning(Messages.EMFStoreController_Stopping_All_ConnectionHandlers);
		if (connectionHandlers != null) {
			for (final ConnectionHandler<? extends EMFStoreInterface> handler : connectionHandlers) {
				ModelUtil.logWarning(
					MessageFormat.format(
						Messages.EMFStoreController_Stopping_ConnectionHandler, handler.getName()));
				handler.stop();
				ModelUtil.logWarning(
					MessageFormat.format(
						Messages.EMFStoreController_ConnectionHandler_Stopped, handler.getName()));
			}
		}
		ModelUtil.logException(Messages.EMFStoreController_Serve_Forcefully_Stopped, exception);
		ModelUtil.logException(Messages.EMFStoreController_Cause_For_Server_Shutdown, exception.getCause());
		wakeForTermination();
	}

	private synchronized void waitForTermination() {
		try {
			wait();
		} catch (final InterruptedException e) {
			ModelUtil.logWarning(Messages.EMFStoreController_Waiting_For_Termination_Interrupted, e);
		}
	}

	private synchronized void wakeForTermination() {
		notify();
	}

	private void serverHeader() {
		final InputStream inputStream = getClass().getResourceAsStream(EMFSTORE_TXT_FILE);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			ModelUtil.logWarning("[EMFStoreController] Exception while reading server headers", e); //$NON-NLS-1$
			// ignore
		} finally {
			try {
				reader.close();
				inputStream.close();
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			run(false);
		} catch (final FatalESException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts the server in a new thread.
	 *
	 * @return an controller for the running EMFStore
	 * @throws FatalESException
	 *             in case of failure
	 */
	public static EMFStoreController runAsNewThread() throws FatalESException {
		final EMFStoreController emfStoreController = new EMFStoreController();
		final Thread thread = new Thread(emfStoreController);
		thread.start();
		try {
			thread.join();
		} catch (final InterruptedException e) {
			throw new FatalESException(e);
		}
		return emfStoreController;
	}

	/**
	 * Returns the {@link ServerSpace}.
	 *
	 * @return the server space
	 */
	public ServerSpace getServerSpace() {
		return serverSpace;
	}

	/**
	 * Returns the {@link AccessControl} component of the EMFStore controller.
	 *
	 * @return the {@link AccessControl} component
	 */
	public AccessControl getAccessControl() {
		return accessControl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		stopServer();
	}

}
