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
 * Johannes Faltermeier - adaptions for independent storage
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.startup.PostStartupListener;
import org.eclipse.emf.emfstore.internal.server.startup.StartupListener;
import org.eclipse.emf.emfstore.server.ESLocationProvider;
import org.eclipse.emf.emfstore.server.auth.ESAuthenticationControlType;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.osgi.framework.Bundle;

import com.google.common.base.Optional;

/**
 * Represents the current server configuration.
 *
 * @author koegel
 * @author wesendon
 * @author jfaltermeier
 */
public final class ServerConfiguration {

	/**
	 * Name of EMFStore properties file.
	 */
	public static final String ES_PROPERTIES = "es.properties"; //$NON-NLS-1$

	private static final String RESOURCE_OPTIONS_EXTENSION_POINT = "org.eclipse.emf.emfstore.server.resourceOptions"; //$NON-NLS-1$
	private static final String CHANGEPACKAGE_FRAGMENT_SIZE_ATTRIBUTE = "changePackageFragmentSize"; //$NON-NLS-1$
	private static final String SERVER_USE_FILEBASED_CHANGEPACKAGE = "useFileBasedChangePackage"; //$NON-NLS-1$
	private static final String SAVE_PROJECT_STATE_ON_TAG = "saveProjectStateOnTag"; //$NON-NLS-1$

	private static final String CHECKSUM_KEY = "org.eclipse.emf.emfstore.server.computeChecksum"; //$NON-NLS-1$

	private static final String SERVER_BUNDLE_KEY = "org.eclipse.emf.emfstore.server"; //$NON-NLS-1$

	private static final String LOCATION_PROVIDER_KEY = "org.eclipse.emf.emfstore.server.locationProvider"; //$NON-NLS-1$

	/**
	 * Constant for the name of the Resource Storage Property.
	 */
	public static final String RESOURCE_STORAGE = "emfstore.persistence.resourceStorage"; //$NON-NLS-1$

	/**
	 * RMI encryption property, possible values are true and false.
	 */
	public static final String RMI_ENCRYPTION = "emfstore.connection.rmi.encryption"; //$NON-NLS-1$

	/**
	 * Default RMI encryption property value.
	 */
	public static final String RMI_ENCRYPTION_DEFAULT = "true"; //$NON-NLS-1$

	/**
	 * Option for defining port of XML RPC.
	 */
	public static final String XML_RPC_PORT = "emfstore.connection.xmlrpc.port"; //$NON-NLS-1$

	/**
	 * Default port for XML RPC.
	 */
	public static final String XML_RPC_PORT_DEFAULT = "8080"; //$NON-NLS-1$

	/**
	 * Default name of server keystore file.
	 */
	public static final String SERVER_KEYSTORE_FILE = "emfstoreServer.keystore"; //$NON-NLS-1$

	/**
	 * Password of keystore, in which the certificate for rmi encryption and
	 * password decryption is saved.
	 *
	 * @see #KEYSTORE_ALIAS
	 */
	public static final String KEYSTORE_PASSWORD = "emfstore.keystore.password"; //$NON-NLS-1$

	/**
	 * Default keystore password.
	 */
	public static final String KEYSTORE_PASSWORD_DEFAULT = "123456"; // av374tb$VBGGtrgwa7tosdfa"; //$NON-NLS-1$

	/**
	 * Alias for certificate in keystore.
	 *
	 * @see #KEYSTORE_PASSWORD
	 */
	public static final String KEYSTORE_ALIAS = "emfstore.keystore.alias"; //$NON-NLS-1$

	/**
	 * Default alias, intentioned for developers.
	 */
	public static final String KEYSTORE_ALIAS_DEFAULT = "testkeygeneratedbyotto"; //$NON-NLS-1$

	/**
	 * Type of server certificate used for encryption.
	 */
	public static final String KEYSTORE_CERTIFICATE_TYPE = "emfstore.keystore.certificate.type"; //$NON-NLS-1$

	/**
	 * Default certificate.
	 */
	public static final String KEYSTORE_CERTIFICATE_TYPE_DEFAULT = "SunX509"; //$NON-NLS-1$

	/**
	 * Type of cipher algorithm used for encryption.
	 */
	public static final String KEYSTORE_CIPHER_ALGORITHM = "emfstore.keystore.cipher.algorithm"; //$NON-NLS-1$

	/**
	 * Default cipher algorithm.
	 */
	public static final String KEYSTORE_CIPHER_ALGORITHM_DEFAULT = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"; //$NON-NLS-1$

	/**
	 * Property for projectstate persistence policy in versions. Possible values
	 * are <b>lastVersionOnly</b> and <b>everyVersion</b>. If you don't have
	 * every project state the server has to recalulate certain revisions if
	 * requested. On the other side saving every project state is quite
	 * redundant.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE = "emfstore.persistence.version.projectstate"; //$NON-NLS-1$

	/**
	 * Only the project state from the first and last version is stored, the
	 * other states are calculated by the changes.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_FIRSTANDLASTVERSIONONLY = "firstAndLastVersionOnly"; //$NON-NLS-1$

	/**
	 * The projectstate of every x versions will be stored. This is used to save
	 * memory. Use x=1 to save every version.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS = "everyXVersion"; //$NON-NLS-1$

	/**
	 * Property for the count of versions, needed by the everyXVersion policy.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X = "emfstore.persistence.version.projectstate.everyxversions"; //$NON-NLS-1$

	/**
	 * Default value for the everyXVersion policy.
	 */
	public static final String PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS_X_DEFAULT = "1"; //$NON-NLS-1$

	/**
	 * Default value for projectstate persistence policy in versions.
	 */
	public static final String PROJECTSPACE_VERSION_PERSISTENCE_DEFAULT = PROJECTSTATE_VERSION_PERSISTENCE_EVERYXVERSIONS;

	/**
	 * Property for timeout time of a user session.
	 */
	public static final String SESSION_TIMEOUT = "emfstore.accesscontrol.session.timeout"; //$NON-NLS-1$

	/**
	 * Default timeout (= 30 minutes).
	 */
	public static final String SESSION_TIMEOUT_DEFAULT = "1800000"; //$NON-NLS-1$

	/**
	 * Property for the super user.
	 */
	public static final String SUPER_USER = "emfstore.accesscontrol.authentication.superuser"; //$NON-NLS-1$

	/**
	 * Default super user name.
	 */
	public static final String SUPER_USER_DEFAULT = "super"; //$NON-NLS-1$

	/**
	 * Property for the super user's password.
	 */
	public static final String SUPER_USER_PASSWORD = "emfstore.accesscontrol.authentication.superuser.password"; //$NON-NLS-1$

	/**
	 * Property for the super user's password hash.
	 */
	public static final String SUPER_USER_PASSWORD_HASH = "emfstore.accesscontrol.authentication.superuser.password.hash"; //$NON-NLS-1$

	/**
	 * Property for the super user's password salt.
	 */
	public static final String SUPER_USER_PASSWORD_SALT = "emfstore.accesscontrol.authentication.superuser.password.salt"; //$NON-NLS-1$

	/**
	 * Property for authentication policy used by server. E.g. ldap or property
	 * file.
	 */
	public static final String AUTHENTICATION_POLICY = "emfstore.accesscontrol.authentication.policy"; //$NON-NLS-1$

	/**
	 * Beginning tag of every LDAP property.
	 */
	public static final String AUTHENTICATION_LDAP_PREFIX = "emfstore.accesscontrol.authentication.ldap"; //$NON-NLS-1$

	/**
	 * Ldap url.
	 */
	public static final String AUTHENTICATION_LDAP_URL = "url"; //$NON-NLS-1$

	/**
	 * LDAP user authentication key.
	 */
	public static final String AUTHENTICATION_LDAP_AUTHUSER = "authuser"; //$NON-NLS-1$

	/**
	 * LDAP password authentication key.
	 */
	public static final String AUTHENTICATION_LDAP_AUTHPASS = "authpass"; //$NON-NLS-1$

	/**
	 * Ldap base.
	 */
	public static final String AUTHENTICATION_LDAP_BASE = "base"; //$NON-NLS-1$

	/**
	 * Searchdn for ldap.
	 */
	public static final String AUTHENTICATION_LDAP_SEARCHDN = "searchdn"; //$NON-NLS-1$

	/**
	 * Default authentication policy is simple property file aut.
	 */
	public static final ESAuthenticationControlType AUTHENTICATION_POLICY_DEFAULT = ESAuthenticationControlType.spfv;

	/**
	 * Path to property file for SPFV authentication.
	 */
	public static final String AUTHENTICATION_SPFV_FILEPATH = "emfstore.accesscontrol.authentication.spfv"; //$NON-NLS-1$

	/**
	 * Key for valid ciphers to be used with SSL.
	 */
	public static final String SSL_CIPHERS = "emfstore.ssl.ciphers"; //$NON-NLS-1$

	/**
	 * Property to validate server on start up.
	 */
	public static final String VALIDATION_PROJECT_EXCLUDE_DEFAULT = ""; //$NON-NLS-1$

	/**
	 * Property for loading startup listeners from extension point.
	 */
	public static final String LOAD_STARTUP_LISTENER = "emfstore.startup.loadlistener"; //$NON-NLS-1$

	/**
	 * Property for loading post startup listeners from extension point.
	 */
	public static final String LOAD_POST_STARTUP_LISTENER = "emfstore.startup.post.loadlistener"; //$NON-NLS-1$

	/**
	 * Default value for {@link #LOAD_STARTUP_LISTENER}.
	 */
	public static final String LOAD_STARTUP_LISTENER_DEFAULT = Boolean.TRUE.toString();

	/**
	 * Whether the server should exit if there is an error while initializing missing project branches.
	 */
	public static final String STARTUP_EXIT_ON_BRANCH_INIT_ERROR = "emfstore.startup.branch.init.exit.on.fail"; //$NON-NLS-1$

	/**
	 * Default values for {@link #STARTUP_EXIT_ON_BRANCH_INIT_ERROR}.
	 */
	public static final String STARTUP_EXIT_ON_BRANCH_INIT_ERROR_DEFAULT = Boolean.TRUE.toString();

	/**
	 * Property name of accepted client versions. Enter the version's names or
	 * any, seperate multiple entries with {@link #MULTI_PROPERTY_SEPERATOR}.
	 */
	public static final String ACCEPTED_VERSIONS = "emfstore.acceptedversions"; //$NON-NLS-1$

	/**
	 * Allow any client version.
	 */
	public static final String ACCEPTED_VERSIONS_ANY = "any"; //$NON-NLS-1$

	/**
	 * Seperator for multiple properties. E.g. acceptedversions = 0.1,0.2
	 */
	public static final String MULTI_PROPERTY_SEPERATOR = ","; //$NON-NLS-1$

	/**
	 * Prefix for EMFStore Home Startup Argument.
	 */
	public static final String EMFSTORE_HOME = "-EMFStoreHome"; //$NON-NLS-1$

	/**
	 * Whether user names should be matched case insensitively.
	 */
	public static final String AUTHENTICATION_MATCH_USERS_IGNORE_CASE = "emfstore.accesscontrol.authentication.matchusers.ignorecase"; //$NON-NLS-1$

	/**
	 * Whether authenticated users (eg. by LDAP) should be created in the server workspace on-the-fly on login.
	 */
	public static final String AUTHENTICATION_CREATE_AUTHENTICATED_USERS = "emfstore.accesscontrol.authentication.createusers"; //$NON-NLS-1$

	private static final List<PostStartupListener> POST_STARTUP_LISTENERS = new ArrayList<PostStartupListener>();
	private static final List<StartupListener> STARTUP_LISTENERS = new ArrayList<StartupListener>();

	/**
	 * Project admin privilege property key.
	 */
	public static final String PROJECT_ADMIN_PRIVILEGES_KEY = "emfstore.accesscontrol.projectadmin"; //$NON-NLS-1$

	/**
	 * Property key for enabling the immediate rejection of further verify user attempts for a given user, if there have
	 * been multiple failed attempts in shorter periods than the given delay time. The delay is specified in ms.
	 */
	public static final String AUTHENTICATION_LOGIN_DELAY_FAILED_REQUESTS = "emfstore.accesscontrol.authentication.delay"; //$NON-NLS-1$

	/**
	 * Default value for {@link #AUTHENTICATION_LOGIN_DELAY_FAILED_REQUESTS_DEFAULT} of 1s.
	 */
	public static final String AUTHENTICATION_LOGIN_DELAY_FAILED_REQUESTS_DEFAULT = "1000"; //$NON-NLS-1$

	private static boolean testing;

	private static Properties properties;

	private ServerConfiguration() {
		// nothing to do
	}

	/**
	 * Return the configuration directory location.
	 *
	 * @return the dir path string
	 */
	public static String getConfDirectory() {
		final StringBuffer sb = new StringBuffer(getServerHome());
		sb.append("."); //$NON-NLS-1$
		sb.append(File.separatorChar);
		sb.append("conf"); //$NON-NLS-1$
		sb.append(File.separatorChar);
		return sb.toString();
	}

	@SuppressWarnings("serial")
	private static Map<ESProjectAdminPrivileges, Boolean> defaultPAPrivilegs = new LinkedHashMap<ESProjectAdminPrivileges, Boolean>() {
		{
			put(ESProjectAdminPrivileges.AssignRoleToOrgUnit, Boolean.TRUE);
			put(ESProjectAdminPrivileges.ChangeAssignmentsOfOrgUnits, Boolean.FALSE);
			put(ESProjectAdminPrivileges.ChangeUserPassword, Boolean.FALSE);
			put(ESProjectAdminPrivileges.CreateGroup, Boolean.FALSE);
			put(ESProjectAdminPrivileges.ShareProject, Boolean.FALSE);
			put(ESProjectAdminPrivileges.CreateUser, Boolean.FALSE);
			put(ESProjectAdminPrivileges.DeleteOrgUnit, Boolean.FALSE);
		}
	};

	/**
	 * Whether the {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole
	 * ProjectAdminRole} has the requested privilege.
	 *
	 * @param requestedPrivileg
	 *            the privilege that is requested
	 * @return {@code true}, if the
	 *         {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole
	 *         ProjectAdminRole} has the right to perform
	 *         the requested privilege, {@code false} otherwise
	 */
	public static boolean isProjectAdminPrivileg(ESProjectAdminPrivileges requestedPrivileg) {
		final String[] definedPrivileges = ServerConfiguration.getSplittedProperty(PROJECT_ADMIN_PRIVILEGES_KEY);

		// key not present in config
		if (definedPrivileges == null) {
			return defaultPAPrivilegs.get(requestedPrivileg);
		}

		for (final String definedPrivileg : definedPrivileges) {
			final ESProjectAdminPrivileges privilege = ESProjectAdminPrivileges.valueOf(definedPrivileg);
			if (privilege.equals(requestedPrivileg)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Return the configuration file location.
	 *
	 * @return the file path string
	 */
	public static String getConfFile() {
		return getConfDirectory() + ES_PROPERTIES;
	}

	private static ESLocationProvider locationProvider;
	private static Boolean isChecksumComputationOnCommitActive;
	private static Optional<Integer> changePackageFragmentSize;
	private static Boolean useFileBasedChangePackageOnServer;
	private static Boolean saveProjectStateOnTag;

	private static boolean migrateUserPasswords;

	/**
	 * Return the server home directory location.
	 *
	 * @return the dir path string
	 */
	public static String getServerHome() {
		final String workspaceDirectory = getLocationProvider().getWorkspaceDirectory();
		final File workspace = new File(workspaceDirectory);
		if (!workspace.exists()) {
			workspace.mkdirs();
		}
		if (!workspaceDirectory.endsWith(File.separator)) {
			return workspaceDirectory + File.separatorChar;
		}

		return workspaceDirectory;
	}

	/**
	 * Returns the registered {@link ESLocationProvider} or if not existent, the
	 * {@link org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider}.
	 *
	 * @return workspace location provider
	 */
	public static synchronized ESLocationProvider getLocationProvider() {
		if (locationProvider == null) {
			// TODO EXPT PRIO
			try {
				locationProvider = new ESExtensionPoint(LOCATION_PROVIDER_KEY, true)
					.getClass("providerClass", ESLocationProvider.class); //$NON-NLS-1$
			} catch (final ESExtensionPointException e) {
				final String message = Messages.ServerConfiguration_No_Location_Provider;
				ModelUtil.logWarning(message);
			}

			if (locationProvider == null) {
				locationProvider = new DefaultServerWorkspaceLocationProvider();
			}
		}

		return locationProvider;
	}

	/**
	 * Gets startup parameter from {@link Platform#getApplicationArgs()} which
	 * are in the form of -[parameterkey]=[parametervalue].
	 *
	 * @param parameter
	 *            name of parameter key
	 * @return parameter as string or null
	 */
	public static String getStartArgument(String parameter) {
		for (final String arg : Platform.getApplicationArgs()) {
			if (arg.startsWith(parameter) && arg.length() > parameter.length()
				&& arg.charAt(parameter.length()) == '=') {
				return arg.substring(parameter.length() + 1, arg.length());
			}
		}
		return null;
	}

	/**
	 * Checks whether a parameter is set.
	 *
	 * @param parameter
	 *            checks existence of parameter
	 * @return boolean
	 */
	public static boolean isStartArgSet(String parameter) {
		for (final String arg : Platform.getApplicationArgs()) {
			if (arg.equals(parameter)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Default filepath for spfv authentication.
	 *
	 * @return path as string
	 */
	public static String getDefaultSPFVFilePath() {
		return getConfDirectory() + "user.properties"; //$NON-NLS-1$
	}

	/**
	 * Gets the server's properties.
	 *
	 * @return properties
	 */
	public static synchronized Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	/**
	 * This method calls {@link Properties#getProperty(String)} and splits the
	 * resulting string, using {@link #MULTI_PROPERTY_SEPERATOR}.
	 *
	 * @param property
	 *            property key
	 * @return String array or null
	 */
	public static String[] getSplittedProperty(String property) {
		final String result = getProperties().getProperty(property);
		return result == null ? null : splitProperty(result);
	}

	/**
	 * This method calls {@link Properties#getProperty(String, String)} and
	 * splits the resulting string, using {@link #MULTI_PROPERTY_SEPERATOR}.
	 *
	 * @param property
	 *            property key
	 * @param defaultValue
	 *            default value
	 * @return String array or null
	 */
	public static String[] getSplittedProperty(String property, String defaultValue) {
		final String result = getProperties().getProperty(property, defaultValue);
		return result == null ? null : splitProperty(result);
	}

	private static String[] splitProperty(String property) {
		final ArrayList<String> result = new ArrayList<String>();
		for (final String str : property.split(ServerConfiguration.MULTI_PROPERTY_SEPERATOR)) {
			result.add(str.trim());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Sets the server's properties. All already contained properties will be set to the values before.
	 *
	 * @param prop
	 *            properties
	 */
	public static void setProperties(Properties prop) {
		setProperties(prop, true);
	}

	/**
	 * Sets the servers properties.
	 *
	 * @param prop The properties to set.
	 * @param keepExisting Keep already contained properties?
	 */
	public static void setProperties(Properties prop, boolean keepExisting) {
		final Properties beforeProperties = properties;
		properties = prop;
		if (keepExisting && beforeProperties != null) {
			properties.putAll(beforeProperties);
		}
	}

	/**
	 * Returns the path to the server's keystore.
	 *
	 * @return path to keystore
	 */
	public static String getServerKeyStorePath() {
		return getServerHome() + SERVER_KEYSTORE_FILE;
	}

	/**
	 * Get the server version as in the org.eclipse.emf.emfstore.internal.server manifest
	 * file.
	 *
	 * @return the server version number
	 */
	@SuppressWarnings("cast")
	public static String getServerVersion() {
		final Bundle emfStoreBundle = Platform.getBundle(SERVER_BUNDLE_KEY);
		final String emfStoreVersionString = (String) emfStoreBundle.getHeaders().get(
			org.osgi.framework.Constants.BUNDLE_VERSION);
		return emfStoreVersionString;
	}

	/**
	 * Determine if this is a release version or not.
	 *
	 * @return true if it is a release version
	 */
	public static boolean isReleaseVersion() {
		return !getServerVersion().endsWith("qualifier") && !isInternalReleaseVersion(); //$NON-NLS-1$
	}

	/**
	 * Determines if this is an internal release or not.
	 *
	 * @return true if it an internal release
	 */
	public static boolean isInternalReleaseVersion() {
		return getServerVersion().endsWith("internal"); //$NON-NLS-1$
	}

	/**
	 * @param testing
	 *            if server is running for testing
	 */
	public static void setTesting(boolean testing) {
		ServerConfiguration.testing = testing;
	}

	/**
	 * @return if server is running for testing
	 */
	public static boolean isTesting() {
		return testing;
	}

	/**
	 * Whether the server should compute a checksum for the project state when a
	 * commit has happened. If the server does compute a checksum it will be
	 * sent back to the client who then can check whether there are any
	 * differences between his and the server's project state.
	 *
	 * @return true, if the server does compute a checksum in case a commit has
	 *         happened, false otherwise
	 */
	public static boolean isComputeChecksumOnCommitActive() {
		if (isChecksumComputationOnCommitActive == null) {
			try {
				isChecksumComputationOnCommitActive = new ESExtensionPoint(
					CHECKSUM_KEY, true)
						.getBoolean("shouldComputeChecksumOnCommit"); //$NON-NLS-1$
			} catch (final ESExtensionPointException e) {
				final String message = Messages.ServerConfiguration_Default_Checksum_Behavior;
				ModelUtil.logWarning(message);
				isChecksumComputationOnCommitActive = true;
			}
		}

		return isChecksumComputationOnCommitActive;
	}

	/**
	 * Returns the change package fragments size.
	 *
	 * @return the fragment size in operations. If absent, no change package splitting has been enabled.
	 */
	public static Optional<Integer> getChangePackageFragmentSize() {

		if (changePackageFragmentSize == null) {

			final Integer fragmentSize = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT, false)
				.getInteger(CHANGEPACKAGE_FRAGMENT_SIZE_ATTRIBUTE);

			if (fragmentSize == null) {
				changePackageFragmentSize = Optional.absent();
			} else {
				changePackageFragmentSize = Optional.of(fragmentSize);
			}
		}

		return changePackageFragmentSize;
	}

	/**
	 * Sets the fragment size to be used when splitting change packages.
	 *
	 * @param fragmentSize
	 *            the fragment size (operation count)
	 */
	public static void setChangePackageFragmentSize(Optional<Integer> fragmentSize) {
		changePackageFragmentSize = fragmentSize;
	}

	/**
	 * @return <code>true</code> if change-packages may be persisted in a fragmented file-based format,
	 *         <code>false</code> otherwise
	 */
	public static boolean useFileBasedChangePackageOnServer() {
		if (useFileBasedChangePackageOnServer == null) {
			try {
				useFileBasedChangePackageOnServer = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT, true)
					.getBoolean(SERVER_USE_FILEBASED_CHANGEPACKAGE, false);
			} catch (final ESExtensionPointException e) {
				// if no resource option for the filebased changepackge could be found at the extension point
				// check for the "emfstore.server.fileBasedChangePackage" system property
				// if the property is absent as well, use false as the default
				useFileBasedChangePackageOnServer = Boolean.getBoolean("emfstore.server.fileBasedChangePackage"); //$NON-NLS-1$
			}
		}
		return useFileBasedChangePackageOnServer;
	}

	/**
	 * Returns the list of all {@link StartupListener}s.
	 *
	 * @return the List of all registered {@link StartupListener}.
	 */
	public static List<StartupListener> getStartupListeners() {
		return STARTUP_LISTENERS;
	}

	/**
	 * Adds a {@link StartupListener} to the list of {@link StartupListener} which gets notified on start of the
	 * EMFStore.
	 *
	 * @param listener
	 *            the {@link StartupListener} to add
	 */
	public static void addStartupListener(StartupListener listener) {
		STARTUP_LISTENERS.add(listener);
	}

	/**
	 * Returns the list of all {@link PostStartupListener}s.
	 *
	 * @return the List of all registered {@link PostStartupListener}.
	 */
	public static List<PostStartupListener> getPostStartupListeners() {
		return POST_STARTUP_LISTENERS;
	}

	/**
	 * Adds a {@link PostStartupListener} to the list of {@link PostStartupListener} which gets notified on start of the
	 * EMFStore.
	 *
	 * @param listener
	 *            the {@link PostStartupListener} to add
	 */

	public static void addPostStartupListener(PostStartupListener listener) {
		POST_STARTUP_LISTENERS.add(listener);
	}

	/**
	 * Whether the project state of a tagged version should be kept as a file.
	 *
	 * @return <code>true</code> if project state should be kept, <code>false</code> otherwise
	 */
	public static boolean createProjectStateOnTag() {
		if (saveProjectStateOnTag == null) {
			try {
				saveProjectStateOnTag = new ESExtensionPoint(RESOURCE_OPTIONS_EXTENSION_POINT, true)
					.getBoolean(SAVE_PROJECT_STATE_ON_TAG, false);
			} catch (final ESExtensionPointException e) {
				saveProjectStateOnTag = false;
			}
		}
		return saveProjectStateOnTag;
	}

	/**
	 * Sets {@link #isUserPasswordMigrationRequired()} to <code>true</code>.
	 */
	static void initUserVerifierMigration() {
		ServerConfiguration.migrateUserPasswords = true;
	}

	/**
	 *
	 * @return <code>true</code> if the super user password was migrated from clear text to hashed property. In this
	 *         case user verifiers should hash their user password as well. <code>false</code> if no migration is
	 *         needed.
	 */
	public static boolean isUserPasswordMigrationRequired() {
		return ServerConfiguration.migrateUserPasswords;
	}
}
