/**
 * Phresco Framework Implementation
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.photon.phresco.framework.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.CredentialsProviderUserInfo;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNCommitParameters;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.google.gson.Gson;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.microsoft.tfs.client.clc.commands.Command;
import com.microsoft.tfs.client.clc.commands.shared.CommandEULA;
import com.microsoft.tfs.client.clc.exceptions.ArgumentException;
import com.microsoft.tfs.client.clc.exceptions.CLCException;
import com.microsoft.tfs.client.clc.exceptions.InvalidOptionException;
import com.microsoft.tfs.client.clc.exceptions.InvalidOptionValueException;
import com.microsoft.tfs.client.clc.exceptions.LicenseException;
import com.microsoft.tfs.client.clc.options.Option;
import com.microsoft.tfs.client.clc.vc.VersionControlCommands;
import com.microsoft.tfs.client.clc.vc.VersionControlOptions;
import com.microsoft.tfs.client.clc.vc.commands.CommandAdd;
import com.microsoft.tfs.client.clc.vc.commands.CommandCheckin;
import com.microsoft.tfs.client.clc.vc.commands.CommandGet;
import com.microsoft.tfs.client.clc.vc.commands.CommandWorkFold;
import com.microsoft.tfs.client.clc.vc.commands.CommandWorkspace;
import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;
import com.microsoft.tfs.core.exceptions.TFSFederatedAuthException;
import com.microsoft.tfs.core.exceptions.TFSUnauthorizedException;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.DefaultNTCredentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;
import com.microsoft.tfs.core.util.CredentialsUtils;
import com.microsoft.tfs.core.util.URIUtils;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.exception.P4JavaException;
import com.perforce.p4java.exception.RequestException;
import com.perforce.p4java.impl.generic.client.ClientOptions;
import com.perforce.p4java.impl.generic.client.ClientView;
import com.perforce.p4java.impl.generic.client.ClientView.ClientViewMapping;
import com.perforce.p4java.impl.mapbased.client.Client;
import com.perforce.p4java.option.client.SyncOptions;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.LockUtil;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.Customer;
import com.photon.phresco.commons.model.ModuleInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.api.SCMManager;
import com.photon.phresco.framework.impl.util.FrameworkUtil;
import com.photon.phresco.framework.model.RepoDetail;
import com.photon.phresco.framework.model.RepoFileInfo;
import com.photon.phresco.framework.model.RepoInfo;
import com.photon.phresco.service.client.api.ServiceManager;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.FileUtil;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.DeploymentRepository;
import com.phresco.pom.model.DistributionManagement;
import com.phresco.pom.model.Plugin;
import com.phresco.pom.util.PomProcessor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.Base64;

public class SCMManagerImpl implements SCMManager, FrameworkConstants {

	private static final Logger S_LOGGER = Logger.getLogger(SCMManagerImpl.class);
	private static Boolean debugEnabled = S_LOGGER.isDebugEnabled();
	public static String HTTP_PROXY_URL = "";
	boolean dotphresco ;
	SVNClientManager cm = null;
	VersionControlOptions optionsMap = new VersionControlOptions();

	public void importProject(ApplicationInfo applicationInfo, RepoInfo repoInfo, String displayName, String uniqueKey) throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importProject()");
		}
		if(applicationInfo != null) {
			RepoDetail srcRepoDetail = repoInfo.getSrcRepoDetail();
			com.photon.phresco.framework.impl.util.FrameworkUtil.saveCredential(srcRepoDetail, null);
			if (SVN.equals(srcRepoDetail.getType())) {
				checkoutSVN(applicationInfo, repoInfo, 
						displayName, uniqueKey);
				if(debugEnabled){
					S_LOGGER.debug("SVN type");
				}
			} else if (GIT.equals(srcRepoDetail.getType())) {
				if(debugEnabled){
					S_LOGGER.debug("GIT type");
				}
				String uuid = UUID.randomUUID().toString();
				File gitImportTemp = new File(Utility.getPhrescoTemp(), uuid);
				if(debugEnabled){
					S_LOGGER.debug("gitImportTemp " + gitImportTemp);
				}
				if (gitImportTemp.exists()) {
					if(debugEnabled){
						S_LOGGER.debug("Empty git directory need to be removed before importing from git ");
					}
					FileUtils.deleteDirectory(gitImportTemp);
				}
				if(debugEnabled){
					S_LOGGER.debug("gitImportTemp " + gitImportTemp);
				}
				importFromGit(repoInfo.getSrcRepoDetail(), gitImportTemp);
				if(debugEnabled){
					S_LOGGER.debug("Validating Phresco Definition");
				}
				cloneFilter(applicationInfo, gitImportTemp, displayName, uniqueKey, repoInfo);
				if (gitImportTemp.exists()) {
					if(debugEnabled){
						S_LOGGER.debug("Deleting ~Temp");
					}
					FileUtils.deleteDirectory(gitImportTemp);
				}
				if(debugEnabled){
					S_LOGGER.debug("Completed");
				}
			} else if (BITKEEPER.equals(srcRepoDetail.getType())) {
				String uuid = UUID.randomUUID().toString();
				File bkImportTemp = new File(Utility.getPhrescoTemp(), uuid);
				boolean isImported = importFromBitKeeper(srcRepoDetail.getRepoUrl(), bkImportTemp);
				if (isImported) {
					String appId = applicationInfo.getId();
					LockUtil.generateLock(Collections.singletonList(LockUtil.getLockDetail(appId, FrameworkConstants.IMPORT, 
							displayName, uniqueKey)), true);
					String workDirPath = workDirPath(repoInfo, applicationInfo);
					importToWorkspace(bkImportTemp, new File(workDirPath.toString()));
				} 	
			} else if(PERFORCE.equals(srcRepoDetail.getType())) {
				String uuid = UUID.randomUUID().toString();
				File tempFile = new File(Utility.getPhrescoTemp(), uuid);
				importFromPerforce(srcRepoDetail, tempFile);
				String workDirPath = workDirPath(repoInfo, applicationInfo);
				importToWorkspace(tempFile, new File(workDirPath));
			} else if (TFS.equals(srcRepoDetail.getType())) {
				String workDirPath = workDirPath(repoInfo, applicationInfo);
				File appDirectoryPath = new File (workDirPath);
				if (appDirectoryPath.exists()) {
					if(debugEnabled){
						S_LOGGER.debug("workspaceProjectDir exists "+ appDirectoryPath);
					}
					throw new PhrescoException(appDirectoryPath.getName() + " " + "Already Exists");
				}
				importFromTfs(srcRepoDetail, appDirectoryPath, ""); 
			}
		}
	}

	private String workDirPath(RepoInfo repoInfo, ApplicationInfo applicaionInfo) throws Exception {
		StringBuilder str = new StringBuilder(Utility.getProjectHome()).append(applicaionInfo.getAppDirName());
		if(repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
			//    		File pom = getPomFromRepository(applicaionInfo, repoInfo);
			//    		PomProcessor processor = new PomProcessor(pom);
			//    		String property = processor.getProperty("sourcename");
			str.append(File.separator).append(applicaionInfo.getAppDirName());
		}
		return str.toString();
	}

	public ProjectInfo validatePhrescoProject(RepoDetail repoDetail, File tempFolder) throws PhrescoException {
		ProjectInfo projInfo = null;
		try {
			File dotProjectFile = new File(tempFolder, FOLDER_DOT_PHRESCO+ File.separator + PROJECT_INFO);
			if(repoDetail.getType().equals(SVN)) {
				projInfo = checkOutFilter(repoDetail);
			}
			if(repoDetail.getType().equals(GIT)) {
				importFromGit(repoDetail, tempFolder);
				ProjectInfo gitAppInfo = getRepoProjectInfo(dotProjectFile);
				if(gitAppInfo != null) {
					return gitAppInfo;
				}
			}
			if(repoDetail.getType().equals(BITKEEPER)) {
				boolean imported = importFromBitKeeper(repoDetail.getRepoUrl(), tempFolder);
				if(imported) {
					ProjectInfo projectInfo = getRepoProjectInfo(dotProjectFile);
					if (projectInfo != null) {
						return projectInfo;
					}
				}
			}
			if(repoDetail.getType().equals(PERFORCE)) {
				projInfo = importFromPerforce(repoDetail, tempFolder);
			}
			if (repoDetail.getType().equals(TFS)) {
				projInfo = importFromTfs(repoDetail, tempFolder, Constants.DOT_PHRESCO_FOLDER);
			}
		} catch(Exception e){
			throw new PhrescoException(e);
		} finally {
			if (tempFolder.exists()) {
				FileUtil.delete(tempFolder);
				deleteWorkspace(repoDetail);
			}
		}
		return projInfo;
	}

	private SVNURL getSVNURL(String repoURL) throws PhrescoException {
		SVNURL svnurl = null;
		try {
			svnurl = SVNURL.parseURIEncoded(repoURL);
		} catch (SVNException e) {
			throw new PhrescoException(e);
		}
		return svnurl;
	}

	private SVNClientManager getSVNClientManager(String userName, String password) {
		DAVRepositoryFactory.setup();
		DefaultSVNOptions options = new DefaultSVNOptions();
		return SVNClientManager.newInstance(options, userName, password);
	}

	public boolean updateProject(RepoDetail repodetail, File updateDir) throws Exception  {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.updateproject()");
		}
		com.photon.phresco.framework.impl.util.FrameworkUtil.saveCredential(repodetail, null);
		if (SVN.equals(repodetail.getType())) {
			if(debugEnabled){
				S_LOGGER.debug("SVN type");
			}
			DAVRepositoryFactory.setup();
			SVNClientManager svnClientManager = getSVNClientManager(repodetail.getUserName(), repodetail.getPassword());
			if(debugEnabled){
				S_LOGGER.debug("update SCM Connection " + repodetail.getRepoUrl());
			}
			//			 updateSCMConnection(appInfo, repodetail.getRepoUrl());
			// revision = HEAD_REVISION.equals(revision) ? revision
			// : revisionVal;
			if(debugEnabled){
				S_LOGGER.debug("updateDir SVN... " + updateDir);
				S_LOGGER.debug("Updating...");
			}
			SVNUpdateClient uc = svnClientManager.getUpdateClient();
			uc.doUpdate(updateDir, SVNRevision.parse(repodetail.getRevision()), SVNDepth.UNKNOWN, true, true);
			if(debugEnabled){
				S_LOGGER.debug("Updated!");
			}
			return true;
		} else if (GIT.equals(repodetail.getType())) {
			if(debugEnabled){
				S_LOGGER.debug("GIT type");
			}
			//			updateSCMConnection(appInfo, repodetail.getRepoUrl());
			if(debugEnabled){
				S_LOGGER.debug("updateDir GIT... " + updateDir);
			}
			//for https and ssh
			additionalAuthentication(repodetail.getPassPhrase());

			UsernamePasswordCredentialsProvider userCredential = new UsernamePasswordCredentialsProvider(repodetail.getUserName(), repodetail.getPassword());
            Git git = Git.open(updateDir); // checkout is the folder with .git
            git.pull().setCredentialsProvider(userCredential).call(); // succeeds
			git.getRepository().close();
			if(debugEnabled){
				S_LOGGER.debug("Updated!");
			}
			return true;
		} else if (BITKEEPER.equals(repodetail.getType())) {
			if (debugEnabled) {
				S_LOGGER.debug("BITKEEPER type");
			}
			updateFromBitKeeperRepo(repodetail.getRepoUrl(), updateDir.getPath());
		} else if (PERFORCE.equals(repodetail.getType())) {
			if (debugEnabled) {
				S_LOGGER.debug("PERFORCE type");
			}
			String baseDir = updateDir.getAbsolutePath();
			perforceSync(repodetail, baseDir, updateDir.getName(),"update");
			//			updateSCMConnection(appInfo, repodetail.getRepoUrl()+repodetail.getStream());
		} else if (TFS.equals(repodetail.getType())) {
			int responseCode = getProjectFromTFS(repodetail, updateDir.getCanonicalPath());
			if (responseCode == -1) {
				return true;
			}
		}

		return false;
	}

	private boolean updateFromBitKeeperRepo(String repoUrl, String appDir) throws PhrescoException {
		BufferedReader reader = null;
		File file = new File(Utility.getPhrescoTemp() + "bitkeeper.info");
		boolean isUpdated = false;
		try {
			List<String> commands = new ArrayList<String>();
			commands.add(BK_PARENT + SPACE + repoUrl);
			commands.add(BK_PULL);
			for (String command : commands) {
				Utility.executeStreamconsumer(appDir, command, new FileOutputStream(file));
			}
			reader = new BufferedReader(new FileReader(file));
			String strLine;
			while ((strLine = reader.readLine()) != null) {
				if (strLine.contains("Nothing to pull")) {
					throw new PhrescoException("Nothing to pull");
				} else if (strLine.contains("[pull] 100%")) {
					isUpdated = true;
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
			if (file.exists()) {
				file.delete();
			}
		}

		return isUpdated;
	}

	private void importToWorkspace(File gitImportTemp, File workspaceProjectDir) throws Exception {
		try {
			if(debugEnabled){
				S_LOGGER.debug("Entering Method  SCMManagerImpl.importToWorkspace()");
			}
			if(debugEnabled){
				S_LOGGER.debug("workspaceProjectDir " + workspaceProjectDir);
			}
			if (workspaceProjectDir.exists()) {
				if(debugEnabled){
					S_LOGGER.debug("workspaceProjectDir exists "+ workspaceProjectDir);
				}
				throw new PhrescoException(workspaceProjectDir.getName() + " " + "Already Exists");
			} 

			if(debugEnabled){
				S_LOGGER.debug("Copyin from Temp to workspace...");
				S_LOGGER.debug("gitImportTemp " + gitImportTemp);
				S_LOGGER.debug("workspaceProjectDir " + workspaceProjectDir);
			}

			FileUtils.copyDirectory(gitImportTemp, workspaceProjectDir);
			if(debugEnabled){
				S_LOGGER.debug("Deleting pack file");
			}
			FileUtils.deleteDirectory(gitImportTemp);
		} catch (IOException e) {
			if(debugEnabled){
				S_LOGGER.error("Entering into catch block of importToWorkspace() "+ e.getLocalizedMessage());
				S_LOGGER.error("pack file is not deleted ");
			}
		}
	}

	private void updateSCMConnection(ApplicationInfo appInfo, String repoUrl)throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method SCMManagerImpl.updateSCMConnection()");
		}

		try {
			PomProcessor processor = getPomProcessor(appInfo);
			if(debugEnabled){
				S_LOGGER.debug("processor.getSCM() exists and repo url "+ repoUrl);
			}
			processor.setSCM(repoUrl, "", "", "");
			processor.save();

			// To write in phresco-pom.xml
			PomProcessor phrescoPomProcessor = getPhrescoPomProcessor(appInfo);
			phrescoPomProcessor.setSCM(repoUrl, "", "", "");
			phrescoPomProcessor.save();
		} catch (Exception e) {
			if(debugEnabled){
				S_LOGGER.error("Entering catch block of updateSCMConnection()"+ e.getLocalizedMessage());
			}

			throw new PhrescoException(POM_URL_FAIL);
		}
	}

	public void updateSCMConnection(RepoInfo repoInfo, ApplicationInfo appInfo)throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method SCMManagerImpl.updateSCMConnection()");
		}
		try {
			//Update scm in pom.xml
			String repoUrl = repoInfo.getSrcRepoDetail().getRepoUrl();
			StringBuilder builder = new StringBuilder(Utility.getProjectHome()).append(File.separator).append(appInfo.getAppDirName());
			if(repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
				//				File pomfile = getPomFromRepository(appInfo, repoInfo);
				//				PomProcessor processor = new PomProcessor(pomfile);
				builder.append(File.separator).append(appInfo.getAppDirName());
			}
			builder.append(File.separator).append(appInfo.getPomFile());
			File pomFile = new File(builder.toString());
			PomProcessor processor = new PomProcessor(pomFile);
			processor.setSCM(repoUrl, "", "", "");
			processor.save();

			// To write in phresco-pom.xml
			if(StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
				if(repoInfo.isSplitPhresco()) {
					builder = new StringBuilder(Utility.getProjectHome()).append(File.separator).append(appInfo.getAppDirName());
					builder.append(File.separator).append(appInfo.getAppDirName()+ Constants.SUFFIX_PHRESCO).append(File.separator).append(appInfo.getPhrescoPomFile());
					pomFile = new File(builder.toString());
					PomProcessor phrescoPomProcessor = new PomProcessor(pomFile);
					phrescoPomProcessor.setSCM(repoUrl, "", "", "");
					phrescoPomProcessor.save();
				}
			}
		} catch (Exception e) {
			if(debugEnabled){
				S_LOGGER.error("Entering catch block of updateSCMConnection()"+ e.getLocalizedMessage());
			}
			throw new PhrescoException(POM_URL_FAIL);
		}
	}

	private File getPomFromRepository(ApplicationInfo applicationInfo, RepoInfo repoInfo) throws Exception {
		File pomFile = null;
		String pomFileName = applicationInfo.getPhrescoPomFile();
		RepoDetail phrescoRepoDetail = repoInfo.getPhrescoRepoDetail();
		if(StringUtils.isEmpty(pomFileName)) {
			pomFileName = applicationInfo.getPomFile();
			phrescoRepoDetail = repoInfo.getSrcRepoDetail();
		}
		if(phrescoRepoDetail.getType().equals(SVN)) {
			pomFile = getPomFromSVN(applicationInfo, phrescoRepoDetail, pomFileName);
		}
		if(phrescoRepoDetail.getType().equals(GIT)) {
			pomFile = getPomFromGit(applicationInfo, phrescoRepoDetail, pomFileName);
		}
		if(phrescoRepoDetail.getType().equals(BITKEEPER)) {
			pomFile = getPomFromBitkeeper(applicationInfo, phrescoRepoDetail, pomFileName);
		}

		return pomFile;
	}

	private File getPomFromBitkeeper(ApplicationInfo applicationInfo, RepoDetail repoDetail, String pomName) throws PhrescoException {
		File pomfile = null;
		String uuid = UUID.randomUUID().toString();
		File bkImportTemp = new File(Utility.getPhrescoTemp(), uuid);
		boolean imported = importFromBitKeeper(repoDetail.getRepoUrl(), bkImportTemp);
		if(imported) {
			pomfile = new File(bkImportTemp, pomName);
		}
		return pomfile;
	}

	private PomProcessor getPomProcessor(ApplicationInfo appInfo)throws Exception {
		try {
			StringBuilder builder = new StringBuilder(Utility.getProjectHome());
			builder.append(appInfo.getAppDirName());
			builder.append(File.separatorChar);
			builder.append(Utility.getPomFileName(appInfo));
			File pomPath = new File(builder.toString());
			return new PomProcessor(pomPath);
		} catch (Exception e) {
			throw new PhrescoException(NO_POM_XML);
		}
	}

	private PomProcessor getPhrescoPomProcessor(ApplicationInfo appInfo)throws Exception {
		try {
			StringBuilder builder = new StringBuilder(Utility.getProjectHome());
			builder.append(appInfo.getAppDirName());
			builder.append(File.separatorChar);
			builder.append(Utility.getPhrescoPomFile(appInfo));
			File pomPath = new File(builder.toString());
			return new PomProcessor(pomPath);
		} catch (Exception e) {
			throw new PhrescoException(NO_POM_XML);
		}
	}

	private static void setupLibrary() {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.setupLibrary()");
		}
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	private ProjectInfo checkOutFilter(RepoDetail srcRepoDetail) throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.checkOutFilter()");
		}
		String repoURL = srcRepoDetail.getRepoUrl();
		String userName = srcRepoDetail.getUserName();
		String password = srcRepoDetail.getPassword();
		setupLibrary();
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repoURL));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);
		repository.setAuthenticationManager(authManager);
		SVNNodeKind nodeKind = repository.checkPath("", -1);
		if (nodeKind == SVNNodeKind.NONE) {
			if(debugEnabled){
				S_LOGGER.error("There is no entry at '" + srcRepoDetail.getRepoUrl() + "'.");
			}
		} else if (nodeKind == SVNNodeKind.FILE) {
			if(debugEnabled){
				S_LOGGER.error("The entry at '" + srcRepoDetail.getRepoUrl() + " is a file while a directory was expected.");
			}
		}
		if(debugEnabled){
			S_LOGGER.debug("Repository Root: " + repository.getRepositoryRoot(true));
			S_LOGGER.debug("Repository UUID: " + repository.getRepositoryUUID(true));
		}
		return recurseMethod(repository, "", srcRepoDetail, getSVNURL(srcRepoDetail.getRepoUrl()), true);
	}

	private ProjectInfo recurseMethod(SVNRepository repository, String path, RepoDetail repoDetail, SVNURL svnURL, 
			boolean recursive) throws Exception {
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		ProjectInfo projInfo = dotPhrescoEvaluator(entries, repoDetail);
		if (recursive) {
			String repoUrlString = repoDetail.getRepoUrl();
			svnURL = getSVNURL(repoUrlString);
			Iterator iterator = entries.iterator();
			if (entries.size() != 0) {
				while (iterator.hasNext()) {
					SVNDirEntry entry = (SVNDirEntry) iterator.next();
					if ((entry.getKind() == SVNNodeKind.DIR)) {
						SVNURL svnnewURL = svnURL.appendPath(FORWARD_SLASH + entry.getName(), true);
						if(debugEnabled){
							S_LOGGER.debug("Appended SVNURL for subdir " + svnURL);
							S_LOGGER.debug("checking subdirectories");
						}
						recurseMethod(repository,(path.equals("")) ? entry.getName() : path
								+ FORWARD_SLASH + entry.getName(), repoDetail ,svnnewURL, false);
					}
				}
			}
		} 
		return projInfo;
	}

	private ProjectInfo dotPhrescoEvaluator(Collection entries, RepoDetail repoDetail) throws Exception {
		Iterator iterator = entries.iterator();
		if(debugEnabled){
			S_LOGGER.debug("Entry size " + entries.size());
		}
		if (entries.size() != 0) {
			while (iterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) iterator.next();
				if ((entry.getName().equals(FOLDER_DOT_PHRESCO))
						&& (entry.getKind() == SVNNodeKind.DIR)) {
					if(debugEnabled){
						S_LOGGER.debug("Entry name " + entry.getName());
						S_LOGGER.debug("Entry Path " + entry.getURL());
					}
					return getSvnAppInfo(repoDetail);
				}
			}
		}
		return null;
	}

	boolean checkoutSVN(ApplicationInfo appInfo, RepoInfo repoInfo, String displayName, String uniqueKey) throws Exception {
		RepoDetail srcRepoDetail = repoInfo.getSrcRepoDetail();
		SVNUpdateClient uc = getSVNClientManager(srcRepoDetail.getUserName(), srcRepoDetail.getPassword()).getUpdateClient();
		StringBuilder strbuilder = new StringBuilder(Utility.getProjectHome());
		strbuilder.append(File.separator);
		strbuilder.append(appInfo.getAppDirName());
		if(repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
			strbuilder.append(File.separator);
			strbuilder.append(appInfo.getAppDirName());
		}
		File file = new File(strbuilder.toString());
		if (file.exists()) {
			throw new PhrescoException(PROJECT_ALREADY);
		} else {
			//generate import lock
			String appId = appInfo.getId();
			LockUtil.generateLock(Collections.singletonList(LockUtil.getLockDetail(appId, FrameworkConstants.IMPORT, displayName, uniqueKey)), true);
		}
		if(debugEnabled){
			S_LOGGER.debug("Checking out...");
		}
		uc.doCheckout(getSVNURL(srcRepoDetail.getRepoUrl()), file, SVNRevision.UNDEFINED,
				SVNRevision.parse(srcRepoDetail.getRevision()), SVNDepth.UNKNOWN,
				false);
		if(debugEnabled){
			S_LOGGER.debug("updating pom.xml");
		}
		return true;
	}

	public void updatePoms(RepoInfo repoInfo, ProjectInfo projInfo, ServiceManager serviceManager) throws PhrescoException {
		try {
			ApplicationInfo appInfo = projInfo.getAppInfos().get(0);
			if (repoInfo != null && appInfo != null) {
				String srcUrl = "";
				String phrescoUrl = "";
				String testUrl = "";
				String srcType = "";
				String phrescoType = "";
				String testType = "";
				String srcWorkspaceName = "";
				String phrWorkspaceName = "";
				String testWorkspaceName = "";
				String pomFileName = appInfo.getPomFile();
				File baseDir = new File(Utility.getProjectHome(), appInfo.getAppDirName());
				File file = baseDir;

				File appPom = baseDir;
				if(repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
					appPom = new File(appPom, appInfo.getAppDirName());
				}
				PomProcessor appPomProcessor = new PomProcessor(new File(appPom, POM_XML));
				RepoDetail srcRepoDetail = repoInfo.getSrcRepoDetail();
				if (srcRepoDetail != null) {
					srcWorkspaceName = srcRepoDetail.getWorkspaceName();
					srcUrl = srcRepoDetail.getRepoUrl();
					srcType = srcRepoDetail.getType();

					if (repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
						file = new File(baseDir, appInfo.getAppDirName());
					}

					if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile()) && !repoInfo.isSplitPhresco() && repoInfo.isSplitTest()) {
						pomFileName = appInfo.getPhrescoPomFile();
					} else if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile()) && !repoInfo.isSplitPhresco() && !repoInfo.isSplitTest()) {
						pomFileName = appInfo.getPhrescoPomFile();
					} 

					updatePom(file, srcUrl, srcType, pomFileName, appPomProcessor, "");


					File alwaysPom = baseDir;
					if (repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
						alwaysPom = new File(baseDir,appInfo.getAppDirName());
					}
					if (!POM_XML.equals(pomFileName)) {
						updatePom(alwaysPom, srcUrl, srcType, POM_XML, appPomProcessor, "");
					}
				}

				RepoDetail phrescoRepoDetail = repoInfo.getPhrescoRepoDetail();
				if (phrescoRepoDetail != null && repoInfo.isSplitPhresco()) {
					String phrDirName = appInfo.getAppDirName()+ Constants.SUFFIX_PHRESCO;
					phrWorkspaceName = phrescoRepoDetail.getWorkspaceName();
					file = new File(baseDir, phrDirName);
					phrescoUrl = phrescoRepoDetail.getRepoUrl();
					phrescoType = phrescoRepoDetail.getType();

					if (StringUtils.isNotBlank(appInfo.getPhrescoPomFile())) {
						pomFileName = appInfo.getPhrescoPomFile();
					} else {
						pomFileName = "phresco-pom.xml";
					}

					updatePom(file, phrescoUrl, phrescoType, pomFileName, appPomProcessor, "");
				}


				RepoDetail testRepoDetail = repoInfo.getTestRepoDetail();
				if (testRepoDetail != null && repoInfo.isSplitTest()) {
					String testDirName = appInfo.getAppDirName()+ Constants.SUFFIX_TEST;
					testWorkspaceName = testRepoDetail.getWorkspaceName();
					file = new File(baseDir, testDirName);
					testUrl = testRepoDetail.getRepoUrl();
					testType = testRepoDetail.getType();
					pomFileName = appInfo.getPomFile();

					updatePom(file, testUrl, testType, pomFileName, appPomProcessor, "");
				}

				pomFileName = appInfo.getPomFile();
				if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
					pomFileName = appInfo.getPhrescoPomFile();
				}

				File propertyPom = returnPropertyPom(pomFileName, repoInfo, new File(baseDir, pomFileName), baseDir, "",appInfo);
				updatePomProperties(appInfo, "", propertyPom, phrescoUrl, srcUrl, testUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);


				List<ModuleInfo> modules = appInfo.getModules();

				//module Pom update
				if (CollectionUtils.isNotEmpty(modules)) {
					for (ModuleInfo module : modules) {

						String moduleAppInfoPath = baseDir.getPath() + File.separator + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO;
						if (repoInfo.isSplitPhresco()) {
							moduleAppInfoPath = baseDir.getPath() + File.separator + appInfo.getAppDirName()+"-phresco" + File.separator + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO; 
						}
						ApplicationInfo moduleAppInfo = getApplicationInfo(moduleAppInfoPath);
						if (moduleAppInfo != null) {
							String modPomFileName = moduleAppInfo.getPomFile();
							if (StringUtils.isNotEmpty(moduleAppInfo.getPhrescoPomFile())) {
								modPomFileName = moduleAppInfo.getPhrescoPomFile();
							}

							File modulePropertyPom = returnPropertyPom(modPomFileName, repoInfo, new File(baseDir, module.getCode() + File.separator + modPomFileName), baseDir, module.getCode(),appInfo);
							updatePomProperties(appInfo, module.getCode(), modulePropertyPom, phrescoUrl, srcUrl, testUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
						}
					}
				}

				//Write Distribution Tag
				File distributionPath1 = baseDir;
				if (repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
					distributionPath1 = new File(baseDir, appInfo.getAppDirName());
				}
				writeDistributionTag(new File(distributionPath1, POM_XML), projInfo, serviceManager);

				if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
					File distributionPath2 = baseDir;
					if (repoInfo.isSplitPhresco() && !repoInfo.isSplitTest()) {
						distributionPath2 = new File(baseDir, appInfo.getAppDirName()+"-phresco");
					} else if (!repoInfo.isSplitPhresco() && repoInfo.isSplitTest()) {
						distributionPath2 = new File(baseDir, appInfo.getAppDirName());
					} else if (repoInfo.isSplitPhresco() && repoInfo.isSplitTest()) {
						distributionPath2 = new File(baseDir, appInfo.getAppDirName()+"-phresco");
					} 
					writeDistributionTag(new File(distributionPath2, appInfo.getPhrescoPomFile()), projInfo, serviceManager);
				}
			} 
		}catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		}
	}

	private File returnPropertyPom(String pomFileName, RepoInfo repoInfo, File propertyPom, File baseDir, String module, ApplicationInfo appInfo) {
		propertyPom = baseDir;
		if (!POM_XML.equals(pomFileName) && repoInfo.isSplitPhresco() && repoInfo.isSplitTest()) {
			propertyPom = new File(baseDir, appInfo.getAppDirName()+"-phresco");
		} else if (!POM_XML.equals(pomFileName) && !repoInfo.isSplitPhresco() && repoInfo.isSplitTest()) {
			propertyPom = new File(baseDir, appInfo.getAppDirName());
		} else if (!POM_XML.equals(pomFileName) && repoInfo.isSplitPhresco() && !repoInfo.isSplitTest()) {
			propertyPom = new File(baseDir, appInfo.getAppDirName()+"-phresco");
		} else if (POM_XML.equals(pomFileName) && repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
			propertyPom = new File(baseDir, appInfo.getAppDirName());
		}

		if (StringUtils.isNotEmpty(module)) {
			propertyPom = new File(propertyPom, module);
		}

		propertyPom = new File(propertyPom, pomFileName);
		return propertyPom;
	}

	private void writeDistributionTag(File pomFile, ProjectInfo projInfo, ServiceManager serviceManager) throws PhrescoException {
		String customerId = "";
		try {
			if (CollectionUtils.isNotEmpty(projInfo.getCustomerIds())) {
				customerId = projInfo.getCustomerIds().get(0);
			}
			Customer customer = serviceManager.getCustomer(customerId);
			com.photon.phresco.commons.model.RepoInfo repoInfo = customer.getRepoInfo();
			PomProcessor processor = new PomProcessor(pomFile);
			processor.setName(projInfo.getAppInfos().get(0).getName());
			processor.addRepositories(customerId, repoInfo.getGroupRepoURL());
			DistributionManagement distributionManagement = new DistributionManagement();
			DeploymentRepository repository = new DeploymentRepository();
			repository.setId(projInfo.getName().concat("-release"));
			repository.setUrl(repoInfo.getReleaseRepoURL());
			distributionManagement.setRepository(repository);
			if(StringUtils.isNotEmpty(repoInfo.getSnapshotRepoURL())) {
				repository = new DeploymentRepository();
				repository.setId(projInfo.getName().concat("-snapshot"));
				repository.setUrl(repoInfo.getSnapshotRepoURL());
				distributionManagement.setSnapshotRepository(repository);
			}
			processor.getModel().setDistributionManagement(distributionManagement);
			processor.save();
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}

	private ProjectInfo importFromTfs(RepoDetail repodetail, File appDirectoryPath, String dotPhrescoPath) throws PhrescoException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importFromTfs()");
		}
		ProjectInfo projectInfo = null;
		try {
			FileUtils.forceMkdir(appDirectoryPath);
			createWorkspace(repodetail);
			mapLocalWorkspaceToRemote(appDirectoryPath.getCanonicalPath(), dotPhrescoPath, repodetail);
			getProjectFromTFS(repodetail, appDirectoryPath.getCanonicalPath());
			if (StringUtils.isNotEmpty(dotPhrescoPath)) {
				File dotProjectFile = new File(appDirectoryPath, PROJECT_INFO);
				projectInfo = getRepoProjectInfo(dotProjectFile);	
			}
		} catch(Exception e) {
			throw new PhrescoException(e);
		}

		return projectInfo;
	}

	private void importFromGit(RepoDetail repodetail, File gitImportTemp)throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importFromGit()");
		}	
		if (StringUtils.isEmpty(repodetail.getBranch())) {
			repodetail.setBranch(MASTER);
		}
		// For https and ssh
		additionalAuthentication(repodetail.getPassPhrase());

		UsernamePasswordCredentialsProvider userCredential = new UsernamePasswordCredentialsProvider(repodetail.getUserName(), repodetail.getPassword());
		Git r = Git.cloneRepository().setDirectory(gitImportTemp)
		.setCredentialsProvider(userCredential)
		.setURI(repodetail.getRepoUrl())
		//			.setProgressMonitor(new TextProgressMonitor())
		.setBranch(repodetail.getBranch())
		.call();
		r.getRepository().close();      
	}

	void additionalAuthentication(String passPhrase) {
		final String passwordPhrase = passPhrase;
		JschConfigSessionFactory sessionFactory = new JschConfigSessionFactory() {
			@Override
			protected void configure(OpenSshConfig.Host hc, Session session) {
				CredentialsProvider provider = new CredentialsProvider() {
					@Override
					public boolean isInteractive() {
						return false;
					}

					@Override
					public boolean supports(CredentialItem... items) {
						return true;
					}

					@Override
					public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
						for (CredentialItem item : items) {
							if (item instanceof CredentialItem.StringType) {
								((CredentialItem.StringType) item).setValue(passwordPhrase);
							}
						}
						return true;
					}
				};
				UserInfo userInfo = new CredentialsProviderUserInfo(session, provider);
				// Unknown host key for ssh
				java.util.Properties config = new java.util.Properties(); 
				config.put(STRICT_HOST_KEY_CHECKING, NO);
				session.setConfig(config);

				session.setUserInfo(userInfo);
			}
		};

		SshSessionFactory.setInstance(sessionFactory);

		/*
		 * Enable clone of https url by trusting those urls
		 */
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {     
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
						return null;
					} 
					public void checkClientTrusted( 
							java.security.cert.X509Certificate[] certs, String authType) {
					} 
					public void checkServerTrusted( 
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				} 
		}; 

		final String https_proxy = System.getenv(HTTPS_PROXY);
		final String http_proxy = System.getenv(HTTP_PROXY);

		ProxySelector.setDefault(new ProxySelector() {
			final ProxySelector delegate = ProxySelector.getDefault();

			@Override
			public List<Proxy> select(URI uri) {
				// Filter the URIs to be proxied


				if (uri.toString().contains(HTTPS) && StringUtils.isNotEmpty(http_proxy) && http_proxy != null) {
					try {
						URI httpsUri = new URI(https_proxy);
						String host = httpsUri.getHost();
						int port = httpsUri.getPort();
						return Arrays.asList(new Proxy(Type.HTTP, InetSocketAddress
								.createUnresolved(host, port)));
					} catch (URISyntaxException e) {
						if(debugEnabled){
							S_LOGGER.debug("Url exception caught in https block of additionalAuthentication()");
						}	
					}
				}

				if (uri.toString().contains(HTTP) && StringUtils.isNotEmpty(http_proxy) && http_proxy != null) {
					try {
						URI httpUri = new URI(http_proxy);
						String host = httpUri.getHost();
						int port = httpUri.getPort();
						return Arrays.asList(new Proxy(Type.HTTP, InetSocketAddress
								.createUnresolved(host, port)));
					} catch (URISyntaxException e) {
						if(debugEnabled){
							S_LOGGER.debug("Url exception caught in http block of additionalAuthentication()");
						}					
					}
				}

				// revert to the default behaviour
				return delegate == null ? Arrays.asList(Proxy.NO_PROXY)
						: delegate.select(uri);
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa,
					IOException ioe) {
				if (uri == null || sa == null || ioe == null) {
					throw new IllegalArgumentException("Arguments can't be null.");
				}
			}
		});

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance(SSL); 
			sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
			e.getLocalizedMessage();
		} 
	}

	private boolean importFromBitKeeper(String repoUrl, File bkImportTemp) throws PhrescoException {
		BufferedReader reader = null;
		File file = new File(Utility.getPhrescoTemp() + "bitkeeper.info");
		boolean isImported = false;
		try {
			String command = BK_CLONE + SPACE + repoUrl;
			if(debugEnabled){
				S_LOGGER.debug("bkImportTemp " + bkImportTemp);
			}
			if (bkImportTemp.exists()) {
				if(debugEnabled){
					S_LOGGER.debug("Empty Bk directory need to be removed before importing from BitKeeper ");
				}
				FileUtils.deleteDirectory(bkImportTemp);
			}
			Utility.executeStreamconsumer(bkImportTemp.getPath(), command, new FileOutputStream(file));
			reader = new BufferedReader(new FileReader(file));
			String strLine;
			while ((strLine = reader.readLine()) != null) {
				if (strLine.contains("OK")) {
					isImported = true;
					break;
				} else if (strLine.contains(ALREADY_EXISTS)) {
					throw new PhrescoException("Project already imported");
				} else if (strLine.contains("FAILED")) {
					throw new PhrescoException("Failed to import project");
				}
			}
			return isImported;
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private void cloneFilter(ApplicationInfo applicationInfo, File appDir, String displayName, 
			String uniqueKey, RepoInfo repoInfo)throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.cloneFilter()");
		}
		if (appDir.isDirectory()) {
			//generate import lock
			String appId = applicationInfo.getId();
			LockUtil.generateLock(Collections.singletonList(LockUtil.getLockDetail(appId, FrameworkConstants.IMPORT, 
					displayName, uniqueKey)), true);
			StringBuilder str = new StringBuilder(Utility.getProjectHome()).append(applicationInfo.getAppDirName());
			if(repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
				//        		pomFromGit = getPomFromRepository(applicationInfo, repoInfo);
				//        		PomProcessor processor = new PomProcessor(pomFromGit);
				//        		String property = processor.getProperty("sourcename");
				str.append(File.separator).append(applicationInfo.getAppDirName());
			}
			importToWorkspace(appDir, new File(str.toString()));
			if(debugEnabled){
				S_LOGGER.debug("updating pom.xml");
			}
			// update connection in pom.xml
			//			updateSCMConnection(applicationInfo, repoInfo.getSrcRepoDetail().getRepoUrl());
		}
	}

	private File getPomFromGit(ApplicationInfo applicationInfo, RepoDetail repoDetail, String pomFile) throws Exception {
		String uuid = UUID.randomUUID().toString();
		File gitImportTemp = new File(Utility.getPhrescoTemp(), uuid);
		importFromGit(repoDetail, gitImportTemp);
		File pom = new File(gitImportTemp, pomFile);
		return pom;
	}

	private ProjectInfo getRepoProjectInfo(File dotProjectFile)throws PhrescoException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.getGitAppInfo()");
		}

		BufferedReader reader = null;
		try {
			if(debugEnabled){
				S_LOGGER.debug(dotProjectFile.getAbsolutePath());
				S_LOGGER.debug("dotProjectFile" + dotProjectFile);
			}
			if (!dotProjectFile.exists()) {
				return null;
			}
			reader = new BufferedReader(new FileReader(dotProjectFile));
			return new Gson().fromJson(reader, ProjectInfo.class);
		} catch (FileNotFoundException e) {
			if(debugEnabled){
				S_LOGGER.error("Entering into catch block of getGitAppInfo() "+ e.getLocalizedMessage());
			}
			throw new PhrescoException(INVALID_FOLDER);
		} finally {
			Utility.closeStream(reader);
		}
	}

	private ProjectInfo getSvnAppInfo(RepoDetail repoDetail) throws  Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.getSvnAppInfo()");
		}
		BufferedReader reader = null;
		File tempDir = new File(Utility.getSystemTemp(), UUID.randomUUID().toString());
		if(debugEnabled){
			S_LOGGER.debug("temp dir : SVNAccessor " + tempDir);
		}
		String repoURL = repoDetail.getRepoUrl();
		String userName = repoDetail.getUserName();
		String password = repoDetail.getPassword();
		String revision = repoDetail.getRevision();
		try {
			SVNClientManager svnClientManager = getSVNClientManager(userName, password);
			SVNUpdateClient uc = svnClientManager.getUpdateClient();
			try {
				uc.doCheckout(getSVNURL(repoURL).appendPath(PHRESCO, true), tempDir,
						SVNRevision.UNDEFINED, SVNRevision.parse(revision),
						SVNDepth.UNKNOWN, false);
			} catch(SVNException er) {
				er.printStackTrace();
				ProjectInfo projInfo = new ProjectInfo();
				projInfo.setId("#SEP#");
				return projInfo;
			}
			File dotProjectFile = new File(tempDir, PROJECT_INFO);
			if (!dotProjectFile.exists()) {
				throw new PhrescoException(INVALID_FOLDER);
			}
			reader = new BufferedReader(new FileReader(dotProjectFile));
			return new Gson().fromJson(reader, ProjectInfo.class);
		} finally {
			Utility.closeStream(reader);

			if (tempDir.exists()) {
				FileUtil.delete(tempDir);
			}
		}
	}

	public File getPomFromSVN(ApplicationInfo appInfo, RepoDetail repoDetail, String pomName) throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.getPomFromSVN()");
		}
		File tempDir = new File(Utility.getSystemTemp(), UUID.randomUUID().toString());
		if(debugEnabled){
			S_LOGGER.debug("temp dir : SVNAccessor " + tempDir);
		}
		SVNUpdateClient uc = getSVNClientManager(repoDetail.getUserName(), repoDetail.getPassword()).getUpdateClient();
		try {
			uc.doCheckout(getSVNURL(repoDetail.getRepoUrl()), tempDir,
					SVNRevision.UNDEFINED, SVNRevision.parse(repoDetail.getRevision()),
					SVNDepth.UNKNOWN, false);
		} catch(SVNException er) {
			er.printStackTrace();
		}
		File pom = new File(tempDir, pomName);
		return pom;
	}

	public boolean addToRepo(RepoInfo repoInfo, ApplicationInfo appInfo) throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importToRepo()");
		}
		String appDirName = appInfo.getAppDirName();
		String tempBasePath = Utility.getPhrescoTemp() + appDirName;

		File tempPhrescoFile = new File(tempBasePath + Constants.SUFFIX_PHRESCO);
		File tempTestFile = new File(tempBasePath + Constants.SUFFIX_TEST);
		File tempSrcFile = new File(tempBasePath);
		RepoDetail srcRepoDetail = repoInfo.getSrcRepoDetail();
		RepoDetail phrescoRepoDetail = repoInfo.getPhrescoRepoDetail();
		RepoDetail testRepoDetail = repoInfo.getTestRepoDetail();
		try {
			String phrescoDirName = appDirName + Constants.SUFFIX_PHRESCO;
			String srcDirName = appDirName;
			String testDirName = appDirName + Constants.SUFFIX_TEST;
			
			String srcWorkspaceName = "";
			String phrWorkspaceName = "";
			String testWorkspaceName = "";

			
			String repoType = srcRepoDetail.getType();
			String srcRepoUrl = srcRepoDetail.getRepoUrl();
			StringBuilder appendedSrcUrl = new StringBuilder(srcRepoUrl);
			if (SVN.equalsIgnoreCase(repoType)) {
				if (!srcRepoUrl.endsWith(FORWARD_SLASH)) {
					appendedSrcUrl.append(FORWARD_SLASH);
				}
				appendedSrcUrl.append(srcDirName);
				appendedSrcUrl.append(FORWARD_SLASH);
				appendedSrcUrl.append(TRUNK);
			}
			srcWorkspaceName = srcDirName + UUID.randomUUID().toString();
			if(TFS.equals(repoType)) {
			srcRepoDetail.setWorkspaceName(srcWorkspaceName);
			}
				
			FrameworkUtil.saveCredential(srcRepoDetail, appendedSrcUrl);

			File dir = new File(Utility.getProjectHome() + appDirName);
			String pomFileName = appInfo.getPomFile();
			if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
				pomFileName = appInfo.getPhrescoPomFile();
			}
			PomProcessor appPomProcessor = new PomProcessor(new File(dir, pomFileName));
			boolean hasSplit = false; 
			if (repoInfo.isSplitPhresco() || repoInfo.isSplitTest()) {
				hasSplit = true;
			}
			if (hasSplit) {
				String phrescoRepoUrl = "";
				StringBuilder appendedPhrUrl = new StringBuilder();
				if (phrescoRepoDetail != null) {
					phrescoRepoUrl = phrescoRepoDetail.getRepoUrl();
					appendedPhrUrl.append(phrescoRepoUrl);
					if (SVN.equalsIgnoreCase(phrescoRepoDetail.getType())) {
						if (!phrescoRepoUrl.endsWith(FORWARD_SLASH)) {
							appendedPhrUrl.append(FORWARD_SLASH);
						}
						appendedPhrUrl.append(phrescoDirName);
						appendedPhrUrl.append(FORWARD_SLASH);
						appendedPhrUrl.append(TRUNK);
					}
					phrWorkspaceName = phrescoDirName + UUID.randomUUID().toString();
					if(TFS.equals(phrescoRepoDetail.getType())) {
					phrescoRepoDetail.setWorkspaceName(phrWorkspaceName);
					}
					FrameworkUtil.saveCredential(phrescoRepoDetail, appendedPhrUrl);
				}

				String testRepoUrl = "";
				StringBuilder appendedTestUrl = new StringBuilder();
				if (testRepoDetail != null) {
					testRepoUrl = testRepoDetail.getRepoUrl();
					appendedTestUrl.append(testRepoUrl);
					if (SVN.equalsIgnoreCase(testRepoDetail.getType())) {
						if (!testRepoUrl.endsWith(FORWARD_SLASH)) {
							appendedTestUrl.append(FORWARD_SLASH);
						}
						appendedTestUrl.append(testDirName);
						appendedTestUrl.append(FORWARD_SLASH);
						appendedTestUrl.append(TRUNK);
					}
					testWorkspaceName = testDirName + UUID.randomUUID().toString();
					if(TFS.equals(testRepoDetail.getType())) {
					testRepoDetail.setWorkspaceName(testWorkspaceName);
					}
					FrameworkUtil.saveCredential(testRepoDetail, appendedTestUrl);
				}

				if (repoInfo.isSplitPhresco()) {					
					boolean validUser = authentication(repoInfo.getPhrescoRepoDetail().getUserName(), repoInfo.getPhrescoRepoDetail().getPassword(), repoType, repoInfo.getPhrescoRepoDetail().getRepoUrl());
					if(!validUser){ 
						throw new PhrescoException("Invalid User ");
					}
					splitDotPhrescoContents(appInfo, tempPhrescoFile, appendedPhrUrl.toString(), appendedSrcUrl.toString(), appendedTestUrl.toString(), srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
					updatePom(tempPhrescoFile, appendedPhrUrl.toString(), repoType, PHR_POM_XML, appPomProcessor, Constants.PHRESCO);
					File pomFile = new File(tempPhrescoFile, PHR_POM_XML);
					updatePom(appPomProcessor, Constants.PHRESCO, pomFile);
					if (!TFS.equals(repoType)) {
						addToRepo(phrescoRepoDetail, appInfo, dir, phrescoDirName, tempPhrescoFile, hasSplit);
					}
				}
				if (repoInfo.isSplitTest()) {					
					boolean validUser = authentication(repoInfo.getTestRepoDetail().getUserName(), repoInfo.getTestRepoDetail().getPassword(), repoType, repoInfo.getTestRepoDetail().getRepoUrl());
					if(!validUser){ 
						throw new PhrescoException("Invalid User ");
					}
					splitTestContents(appInfo, tempTestFile);
					updatePom(tempTestFile, appendedTestUrl.toString(), repoType, POM_FILE, appPomProcessor, Constants.POM);
					if (!TFS.equals(repoType)) {
						addToRepo(testRepoDetail, appInfo, dir, testDirName, tempTestFile, hasSplit);
					}
				}
				File pomDest = null;
				boolean validUser = authentication(repoInfo.getSrcRepoDetail().getUserName(), repoInfo.getSrcRepoDetail().getPassword(), repoType, repoInfo.getSrcRepoDetail().getRepoUrl());
				if(!validUser) { 
					throw new PhrescoException("Invalid User ");
				}
				splitSrcContents(appInfo, tempSrcFile, repoInfo, appendedPhrUrl.toString(), appendedSrcUrl.toString(), appendedTestUrl.toString(), srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
				if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
					pomDest = new File(tempSrcFile, appInfo.getPhrescoPomFile());
					if(pomDest.exists()) {
						updatePom(tempSrcFile, appendedSrcUrl.toString(), repoType, pomDest.getName(),appPomProcessor, "");
					}
				} 

				if (StringUtils.isNotEmpty(appInfo.getPomFile())) {
					pomDest = new File(tempSrcFile, appInfo.getPomFile());
					PomProcessor sourceProcessor = new PomProcessor(pomDest);
					String andSrcDir = sourceProcessor.getProperty("source.dir");
					if(StringUtils.isNotEmpty(andSrcDir)) {
						File androidSourcePom = new File(pomDest.getParent() + andSrcDir);
						updatePom(androidSourcePom, appendedSrcUrl.toString(), repoType, Constants.POM_NAME, appPomProcessor, "");
					}
					if (pomDest.exists()) {
						updatePom(tempSrcFile, appendedSrcUrl.toString(), repoType, appInfo.getPomFile(), appPomProcessor, "");
					}
				}
				if (!TFS.equals(repoType)) {
					addToRepo(srcRepoDetail, appInfo, dir, srcDirName, tempSrcFile, hasSplit);
				}
				FileUtil.delete(dir);
				if (repoType.equals(SVN)) {
					File copySrcDir = new File(Utility.getPhrescoTemp(), CHECKOUT_TEMP + File.separator + appDirName);
					FileUtils.copyDirectoryToDirectory(copySrcDir, new File(Utility.getProjectHome()));
				}
				if (repoType.equals(GIT)) {
					copySplitProjectToWorkspace(tempPhrescoFile, tempTestFile, tempSrcFile, dir);
				}
				
				if (TFS.equals(repoType)) {
					copySplitProjectToWorkspace(tempPhrescoFile, tempTestFile, tempSrcFile, dir);
					addSplittedProjectToTFS(srcRepoDetail, phrescoRepoDetail, testRepoDetail, dir);
				}
			} else {
				File pom = null;
					boolean validUser = authentication(repoInfo.getSrcRepoDetail().getUserName(), repoInfo.getSrcRepoDetail().getPassword(), repoType, appendedSrcUrl.toString());
				if(!validUser){
					throw new PhrescoException("Invalid Credentials ");
				}
					appPomProcessor.setProperty(Constants.POM_PROP_KEY_SRC_REPO_URL, appendedSrcUrl.toString());
					if (TFS.equals(repoType)) {
					appPomProcessor.setProperty(Constants.POM_PROP_KEY_TFS_SRC_WORKSPACE_NAME, srcWorkspaceName);
					}
					String scmUrl = SCM + COLON + repoType + COLON + appendedSrcUrl.toString();
					appPomProcessor.setSCM(appendedSrcUrl.toString(), scmUrl, scmUrl, "");
					appPomProcessor.save();
					if(StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
						 pom = new File(dir, appInfo.getPomFile());
						if(pom.exists()) {
							PomProcessor processor = new PomProcessor(pom);
							processor.setSCM(appendedSrcUrl.toString(), scmUrl, scmUrl, "");
							processor.save();
						}
	
					}
					 pom = new File(dir, appInfo.getPomFile());
					PomProcessor sourceProcessor = new PomProcessor(pom);
					String andSrcDir = sourceProcessor.getProperty("source.dir");
					if(StringUtils.isNotEmpty(andSrcDir)) {
						File androidSourcePom = new File(pom.getParent() + File.separator + andSrcDir + File.separator + Constants.POM_NAME);
						PomProcessor andSrcProcessor = new PomProcessor(androidSourcePom);
						andSrcProcessor.setProperty(Constants.POM_PROP_KEY_SRC_REPO_URL, appendedSrcUrl.toString());
						andSrcProcessor.setSCM(appendedSrcUrl.toString(), scmUrl, scmUrl, "");
						andSrcProcessor.save();
					}
					addToRepo(srcRepoDetail, appInfo, dir, appDirName, dir, hasSplit);
			}
		} catch (Exception e) {
			e.printStackTrace();
			deleteWorkspace(srcRepoDetail);
			if (phrescoRepoDetail != null) {
				deleteWorkspace(phrescoRepoDetail);	
			}
			if (testRepoDetail != null) {
				deleteWorkspace(testRepoDetail);	
			}
			throw new PhrescoException(e);
		} finally {
			FileUtil.delete(tempPhrescoFile);
			FileUtil.delete(tempTestFile);
			FileUtil.delete(tempSrcFile);
			FileUtil.delete(new File(tempBasePath));
			FileUtil.delete(new File(Utility.getPhrescoTemp(), CHECKOUT_TEMP));
		}
		return true;
	}
	
	private void copySplitProjectToWorkspace(File tempPhrescoFile, File tempTestFile, File tempSrcFile, File dir) throws IOException {
		if (tempPhrescoFile.exists()) {
			FileUtils.copyDirectoryToDirectory(tempPhrescoFile, dir);
		}

		if (tempSrcFile.exists()) {
			FileUtils.copyDirectoryToDirectory(tempSrcFile, dir);
		}

		if (tempTestFile.exists()) {
			FileUtils.copyDirectoryToDirectory(tempTestFile, dir);
		}
	}
	
	private void addSplittedProjectToTFS(RepoDetail srcRepoDetail, RepoDetail phrRepoDetail, RepoDetail testRepoDetail, File dir) throws PhrescoException {
		File srcDir =  new File (dir, dir.getName());
		File phrescoDir = new File(dir, dir.getName().concat(Constants.SUFFIX_PHRESCO));
		File testDir = new File(dir, dir.getName().concat(Constants.SUFFIX_TEST));
		if (srcDir.exists()) {
			addToTfsRepo(srcRepoDetail, srcDir); 
		}
		if (phrescoDir.exists()) {
			addToTfsRepo(phrRepoDetail, phrescoDir); 
		}
		if (testDir.exists()) {
			addToTfsRepo(testRepoDetail, testDir); 
		}
	}
	
	private void addToTfsRepo(RepoDetail repoDetail, File dir) throws PhrescoException {
		int mapped = 100;
		try {
			createWorkspace(repoDetail);
			mapped = mapLocalWorkspaceToRemote(dir.getCanonicalPath(), "", repoDetail);
			addFilesToTFSRepo(dir.getCanonicalPath(), repoDetail);
			checkinProjectToTFSRepo(dir.getCanonicalPath(), repoDetail);
		} catch (Exception e) {
			if (mapped == -1) {
				deleteWorkspace(repoDetail);
			}
			throw new PhrescoException(e);
		}
	}
	
	private void updatePom(File file, String url, String repoType, String pomFileName, PomProcessor appPomProcessor, String packaging) throws PhrescoException {
		try {
			File pomFile = new File(file, pomFileName);
			String scmUrl = SCM + COLON + repoType + COLON + url;
			PomProcessor splitPomProcessor = null;
			if (!pomFile.exists()) {
				splitPomProcessor = updatePom(appPomProcessor, packaging, pomFile);
			} else {
				splitPomProcessor = new PomProcessor(pomFile);
			}
			splitPomProcessor.setSCM(url, scmUrl, scmUrl, "");
			splitPomProcessor.save();
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}

	private PomProcessor updatePom(PomProcessor appPomProcessor,
			String packaging, File pomFile) throws PhrescoPomException {
		PomProcessor splitPomProcessor = new PomProcessor(pomFile);
		String name = appPomProcessor.getName();
		String groupId = appPomProcessor.getGroupId();
		String artifactId = appPomProcessor.getArtifactId();
		String version = appPomProcessor.getVersion();
		Plugin plugin = appPomProcessor.getPlugin(COM_PHOTON_PHRESCO_PLUGINS, PHRESCO_MAVEN_PLUGIN);

		splitPomProcessor.setName(name);
		splitPomProcessor.setGroupId(groupId);
		splitPomProcessor.setArtifactId(artifactId);
		splitPomProcessor.setVersion(version);
		splitPomProcessor.setPackaging(packaging);
		splitPomProcessor.getModel().setModelVersion(MODEL_VERSION);
		if (Constants.PHRESCO.endsWith(packaging) && plugin != null) {
			Plugin addedPlugin = splitPomProcessor.addPlugin(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion());
			addedPlugin.setExtensions(true);
			splitPomProcessor.setProperty("source.pom", "pom.xml");
		}
		splitPomProcessor.save();
		return splitPomProcessor;
	}

	private void splitDotPhrescoContents(ApplicationInfo appInfo, File tempPhrescoFile, String phrescoRepoUrl, String srcRepoUrl, String testRepoUrl, String srcWorkspaceName, String phrWorkspaceName, String testWorkspaceName) throws PhrescoException {
		try {
			String appDirName = appInfo.getAppDirName();
			String appHome = Utility.getProjectHome() + appDirName + File.separator;
			List<ModuleInfo> modules = appInfo.getModules();
			if (CollectionUtils.isNotEmpty(modules)) {
				for (ModuleInfo module : modules) {
					String moduleAppInfoPath = appHome + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO;
					ApplicationInfo moduleAppInfo = getApplicationInfo(moduleAppInfoPath);
					File tempDest = new File(tempPhrescoFile, module.getCode());
					tempDest.mkdirs();
					File phrescoSrc = new File(appHome + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER);
					FileUtils.copyDirectoryToDirectory(phrescoSrc, tempDest);
					String phrescoPomFile = moduleAppInfo.getPhrescoPomFile();
					if (StringUtils.isNotEmpty(phrescoPomFile)) {
						File phrescoPomSrc = new File(appHome + module.getCode() + File.separator + phrescoPomFile);
						File phrescoPomDest = new File(tempDest, phrescoPomFile);
						FileUtils.copyFileToDirectory(phrescoPomSrc, tempDest);
						updatePomProperties(appInfo, moduleAppInfo.getAppDirName(), phrescoPomDest, phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
					}
				}
			}
			tempPhrescoFile.mkdirs();
			File phrescoSrc = new File(appHome + Constants.DOT_PHRESCO_FOLDER);
			FileUtils.copyDirectoryToDirectory(phrescoSrc, tempPhrescoFile);
			if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
				File phrescoPomSrc = new File(appHome + appInfo.getPhrescoPomFile());
				FileUtils.copyFileToDirectory(phrescoPomSrc, tempPhrescoFile);
			}
			updatePomProperties(appInfo, "", new File(tempPhrescoFile, PHR_POM_XML), phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void splitTestContents(ApplicationInfo appInfo, File tempTestFile) throws PhrescoException {
		try {
			String appDirName = appInfo.getAppDirName();
			String appHome = Utility.getProjectHome() + appDirName + File.separator;
			List<ModuleInfo> modules = appInfo.getModules();
			if (CollectionUtils.isNotEmpty(modules)) {
				for (ModuleInfo module : modules) {
					String moduleAppInfoPath = appHome + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO;
					ApplicationInfo moduleAppInfo = getApplicationInfo(moduleAppInfoPath);
					String pomFile = moduleAppInfo.getPomFile();
					if (StringUtils.isNotEmpty(moduleAppInfo.getPhrescoPomFile())) {
						pomFile = moduleAppInfo.getPhrescoPomFile();
					}
					PomProcessor pomProcessor = new PomProcessor(new File(appHome + module.getCode() + File.separator + pomFile));
					String testDir = pomProcessor.getProperty(Constants.POM_PROP_KEY_TEST_DIR);
					if (StringUtils.isNotEmpty(testDir)) {
						File tempDest = new File(tempTestFile, module.getCode());
						tempDest.mkdirs();
						File testSrc = new File(appHome + moduleAppInfo.getAppDirName() + File.separator + testDir);
						FileUtils.copyDirectoryToDirectory(testSrc, tempDest);
					}
				}
			} else {
				tempTestFile.mkdirs();
				String pomFile = appInfo.getPomFile();
				if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
					pomFile = appInfo.getPhrescoPomFile();
				}
				PomProcessor pomProcessor = new PomProcessor(new File(appHome + pomFile));
				String testDir = pomProcessor.getProperty(Constants.POM_PROP_KEY_TEST_DIR);
				File testSrc = new File(appHome, testDir);
				FileUtils.copyDirectoryToDirectory(testSrc, tempTestFile);
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void splitSrcContents(ApplicationInfo appInfo, File tempSrcFile, RepoInfo repoInfo, String phrescoRepoUrl, String srcRepoUrl, String testRepoUrl, String srcWorkspaceName, String phrWorkspaceName, String testWorkspaceName) throws PhrescoException {
		try {
			File pomDest = null;
			String appDirName = appInfo.getAppDirName();
			String appHome = Utility.getProjectHome() + appDirName + File.separator;
			List<ModuleInfo> modules = appInfo.getModules();
			if (CollectionUtils.isNotEmpty(modules)) {
				for (ModuleInfo module : modules) {
					File srcDest = new File(tempSrcFile, module.getCode());
					File srcDir = new File(appHome, module.getCode());
					FileUtils.copyDirectory(srcDir, srcDest, false);
					String moduleAppInfoPath = appHome + module.getCode() + File.separator + Constants.DOT_PHRESCO_FOLDER + File.separator + PROJECT_INFO;
					ApplicationInfo moduleAppInfo = getApplicationInfo(moduleAppInfoPath);

					if (StringUtils.isNotEmpty(moduleAppInfo.getPhrescoPomFile())) {
						pomDest = new File(srcDest, moduleAppInfo.getPhrescoPomFile());
						if(pomDest.exists()) {
							updatePomProperties(appInfo, moduleAppInfo.getAppDirName(), pomDest, phrescoRepoUrl, srcRepoUrl,
									testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
						}
					} else {
						pomDest = new File(srcDest, moduleAppInfo.getPomFile());
						PomProcessor sourceProcessor = new PomProcessor(pomDest);
						String andSrcDir = sourceProcessor.getProperty("source.dir");
						if(StringUtils.isNotEmpty(andSrcDir)) {
							File androidSourcePom = new File(pomDest.getParent() + File.separator + andSrcDir + File.separator + Constants.POM_NAME);
							updatePomProperties(appInfo, moduleAppInfo.getAppDirName(), androidSourcePom, phrescoRepoUrl, srcRepoUrl,
									testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
						}
						updatePomProperties(appInfo, moduleAppInfo.getAppDirName(), pomDest, phrescoRepoUrl, srcRepoUrl,
								testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
					}

					if (repoInfo.isSplitPhresco()) {
						FileUtils.deleteDirectory(new File(srcDest, Constants.DOT_PHRESCO_FOLDER));
						if (StringUtils.isNotEmpty(moduleAppInfo.getPhrescoPomFile())) {
							File phrescoPomFile = new File(srcDest, moduleAppInfo.getPhrescoPomFile());
							FileUtil.delete(phrescoPomFile);
						}
					}
					if (repoInfo.isSplitTest()) {
						String pomFile = moduleAppInfo.getPomFile();
						if (StringUtils.isNotEmpty(moduleAppInfo.getPhrescoPomFile())) {
							pomFile = moduleAppInfo.getPhrescoPomFile();
						}
						PomProcessor pomProcessor = new PomProcessor(new File(appHome + module.getCode() + File.separator + pomFile));
						String testDir = pomProcessor.getProperty(Constants.POM_PROP_KEY_TEST_DIR);
						if (StringUtils.isNotEmpty(testDir)) {
							FileUtils.deleteDirectory(new File(srcDest, testDir));
						}
					}
				}
				if (!repoInfo.isSplitPhresco()) {
					File srcDir = new File(appHome, Constants.DOT_PHRESCO_FOLDER);
					File srcDotPhresco = new File(tempSrcFile, Constants.DOT_PHRESCO_FOLDER);
					FileUtils.copyDirectory(srcDir, srcDotPhresco, false);
					if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
						File phrescoPomFile = new File(appHome, appInfo.getPhrescoPomFile());
						FileUtils.copyFileToDirectory(phrescoPomFile, tempSrcFile, false);
					}
					if (StringUtils.isNotEmpty(appInfo.getPomFile())) {
						File pomFile = new File(appHome, appInfo.getPomFile());
						FileUtils.copyFileToDirectory(pomFile, tempSrcFile, false);
					}

					String pomFileName = appInfo.getPomFile();
					if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
						pomFileName = appInfo.getPhrescoPomFile();
					}
					pomDest = new File(tempSrcFile, pomFileName);
					updatePomProperties(appInfo, "", pomDest, phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
				}
			} else {
				tempSrcFile.mkdirs();
				File srcDir = new File(Utility.getProjectHome() + appDirName);
				FileUtils.copyDirectory(srcDir, tempSrcFile, false);
				if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
					pomDest = new File(tempSrcFile, appInfo.getPhrescoPomFile());
					if(pomDest.exists()) {
						updatePomProperties(appInfo, "", pomDest, phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
					}
				} else {
					pomDest = new File(tempSrcFile, appInfo.getPomFile());
					PomProcessor sourceProcessor = new PomProcessor(pomDest);
					String andSrcDir = sourceProcessor.getProperty("source.dir");
					if(StringUtils.isNotEmpty(andSrcDir)) {
						File androidSourcePom = new File(pomDest.getParent() + File.separator + andSrcDir + File.separator + Constants.POM_NAME);
						updatePomProperties(appInfo, "", androidSourcePom, phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
					}
					updatePomProperties(appInfo, "", pomDest, phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
				}

				if (repoInfo.isSplitPhresco()) {
					FileUtils.deleteDirectory(new File(tempSrcFile, Constants.DOT_PHRESCO_FOLDER));
					if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
						File phrescoPomFile = new File(tempSrcFile, appInfo.getPhrescoPomFile());
						FileUtil.delete(phrescoPomFile);
					}
				}
				if (repoInfo.isSplitTest()) {
					String pomFile = appInfo.getPomFile();
					if (StringUtils.isNotEmpty(appInfo.getPhrescoPomFile())) {
						pomFile = appInfo.getPhrescoPomFile();
					}
					PomProcessor pomProcessor = new PomProcessor(new File(appHome, pomFile));
					String testDir = pomProcessor.getProperty(Constants.POM_PROP_KEY_TEST_DIR);
					FileUtils.deleteDirectory(new File(tempSrcFile, testDir));
				}
			}

			if (StringUtils.isNotEmpty(appInfo.getPomFile())) {
				File phrescoPomSrc = new File(appHome + appInfo.getPomFile());
				FileUtils.copyFileToDirectory(phrescoPomSrc, tempSrcFile);
				if (StringUtils.isEmpty(appInfo.getPhrescoPomFile()))  {
					updatePomProperties(appInfo, "", new File(tempSrcFile, appInfo.getPomFile()), phrescoRepoUrl, srcRepoUrl, testRepoUrl, srcWorkspaceName, phrWorkspaceName, testWorkspaceName);
				}
			}

		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private void updatePomProperties(ApplicationInfo appInfo, String moduleName, File pomFile, String phrescoRepoUrl, String srcRepoUrl, String testRepoUrl, String srcWorkspaceName, String phrWorkspaceName, String testWorkspaceName) throws PhrescoException {
		try {
			String appDirName = appInfo.getAppDirName();
			PomProcessor pomProcessor = new PomProcessor(pomFile);
			StringBuilder sb = new StringBuilder(PREV_DIR);
			if (StringUtils.isNotEmpty(moduleName)) {
				sb.append(PREV_DIR);
			}
			sb.append(appDirName);
			if (StringUtils.isNotEmpty(moduleName)) {
				sb.append(FORWARD_SLASH);
				sb.append(moduleName);
				sb.append(FORWARD_SLASH);
			}

			String srcRootPrpty = pomProcessor.getProperty(Constants.POM_PROP_KEY_ROOT_SRC_DIR);
			if (!sb.toString().endsWith(FORWARD_SLASH) && !srcRootPrpty.startsWith(FORWARD_SLASH)) {
				sb.append(FORWARD_SLASH);
			}

			if (srcRootPrpty.startsWith(BACK_SLASH)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_ROOT_SRC_DIR, sb.toString());
			} else if (srcRootPrpty.startsWith("..")) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_ROOT_SRC_DIR, srcRootPrpty);
			} else {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_ROOT_SRC_DIR, sb.toString() + srcRootPrpty);
			}

			pomProcessor.setProperty(Constants.POM_PROP_KEY_SRC_REPO_URL, srcRepoUrl);

			if (StringUtils.isNotEmpty(phrescoRepoUrl) || StringUtils.isNotEmpty(testRepoUrl)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_SPLIT_SRC_DIR, appDirName);
			}

			if (StringUtils.isNotEmpty(phrescoRepoUrl)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_SPLIT_PHRESCO_DIR, appDirName + Constants.SUFFIX_PHRESCO);
				pomProcessor.setProperty(Constants.POM_PROP_KEY_PHRESCO_REPO_URL, phrescoRepoUrl);
			}
			if (StringUtils.isNotEmpty(testRepoUrl)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR, appDirName + Constants.SUFFIX_TEST);
				pomProcessor.setProperty(Constants.POM_PROP_KEY_TEST_REPO_URL, testRepoUrl);
			}
			if (StringUtils.isNotEmpty(srcWorkspaceName)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_TFS_SRC_WORKSPACE_NAME, srcWorkspaceName);
			}
			if (StringUtils.isNotEmpty(phrWorkspaceName)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_TFS_PHR_WORKSPACE_NAME, phrWorkspaceName);
			}
			if (StringUtils.isNotEmpty(testWorkspaceName)) {
				pomProcessor.setProperty(Constants.POM_PROP_KEY_TFS_TEST_WORKSPACE_NAME, testWorkspaceName);
			}
			pomProcessor.save();
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}

	private ApplicationInfo getApplicationInfo(String path) throws PhrescoException {
		BufferedReader bufferedReader = null;
		try {
			if (!new File(path).exists()) {
				return null;
			}
			bufferedReader = new BufferedReader(new FileReader(path));
			Gson gson = new Gson();
			ProjectInfo projectInfo = gson.fromJson(bufferedReader, ProjectInfo.class);
			return projectInfo.getAppInfos().get(0);
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
		}
	}

	private void addToRepo(RepoDetail repodetail, ApplicationInfo appInfo, File dir, String dirName, File srcDir, boolean hasSplit) throws PhrescoException, PhrescoPomException {
		String phrescoTemp = Utility.getPhrescoTemp();
		String uuid = UUID.randomUUID().toString();
		File tempUuidFile = new File(phrescoTemp, uuid);
		try {
			String repoType = repodetail.getType();
			if (SVN.equals(repoType)) {
				String repoUrl = repodetail.getRepoUrl();
				StringBuilder appendedUrl = new StringBuilder(repoUrl);
				if (!repoUrl.endsWith(FORWARD_SLASH)) {
					appendedUrl.append(FORWARD_SLASH);
				}
				appendedUrl.append(dirName);
				StringBuilder trunkUrl = new StringBuilder(appendedUrl.toString());
				trunkUrl.append(FORWARD_SLASH);
				trunkUrl.append(TRUNK);
				repodetail.setRepoUrl(trunkUrl.toString());
				importDirectoryContentToSubversion(repodetail, srcDir.getPath());
				// checkout to get .svn folder
				checkoutImportedApp(repodetail, appInfo, dirName, hasSplit);
				//Add tags/branches folder to the svn
				repodetail.setRepoUrl(appendedUrl.toString());
				tempUuidFile.mkdir();
				File tempTagsFile = new File(tempUuidFile, TAGS);
				tempTagsFile.mkdir();
				File tempBranchesFile = new File(tempUuidFile, BRANCHES);
				tempBranchesFile.mkdir();
				importDirectoryContentToSubversion(repodetail, tempUuidFile.getPath());
			} else if (GIT.equals(repoType)) {
				importToGITRepo(repodetail, appInfo, srcDir);
			} else if (TFS.equals(repoType)) {
				addToTfsRepo(repodetail, dir); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			removeSrcProperty(new File(dir,appInfo.getPhrescoPomFile()));
			throw new PhrescoException(e);
		} finally {
			if (tempUuidFile.exists()) {
				FileUtil.delete(tempUuidFile);
			}
		}
	}
	
	private void removeSrcProperty(File phrescoPomFile) throws PhrescoPomException {	
		PomProcessor appPomProcessor = new PomProcessor(phrescoPomFile);
		appPomProcessor.getModel().setScm(null);
		appPomProcessor.removeProperty(Constants.POM_PROP_KEY_SRC_REPO_URL);
		appPomProcessor.save();
	}

	private SVNCommitInfo importDirectoryContentToSubversion(RepoDetail repodetail, final String subVersionedDirectory) throws SVNException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importDirectoryContentToSubversion()");
		}
		setupLibrary();
		DefaultSVNOptions defaultSVNOptions = new DefaultSVNOptions();
		defaultSVNOptions.setIgnorePatterns(new String[] {DO_NOT_CHECKIN_DIR,RUN});
		final SVNClientManager cm = SVNClientManager.newInstance(defaultSVNOptions, repodetail.getUserName(), repodetail.getPassword());
		return cm.getCommitClient().doImport(new File(subVersionedDirectory), SVNURL.parseURIEncoded(repodetail.getRepoUrl()), repodetail.getCommitMessage(), null, true, true, SVNDepth.fromRecurse(true));
	}

	private void checkoutImportedApp(RepoDetail repodetail, ApplicationInfo appInfo, String dirName, boolean hasSplit) throws PhrescoException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.checkoutImportedApp()");
		}
		try {
			DefaultSVNOptions options = new DefaultSVNOptions();
			SVNClientManager cm = SVNClientManager.newInstance(options, repodetail.getUserName(), repodetail.getPassword());
			SVNUpdateClient uc = cm.getUpdateClient();
			SVNURL svnURL = SVNURL.parseURIEncoded(repodetail.getRepoUrl());
			if(debugEnabled){
				S_LOGGER.debug("Checking out...");
			}
			String subVersionedDirectory = Utility.getProjectHome() + appInfo.getAppDirName();
			if (hasSplit) {
				subVersionedDirectory = Utility.getPhrescoTemp() + CHECKOUT_TEMP + File.separator + appInfo.getAppDirName() + File.separator + dirName;
			}
			File subVersDir = new File(subVersionedDirectory);
			if (!subVersDir.exists()) {
				subVersDir.mkdirs();
			}
			uc.doCheckout(SVNURL.parseURIEncoded(repodetail.getRepoUrl()), subVersDir, SVNRevision.UNDEFINED, SVNRevision.parse(HEAD_REVISION), SVNDepth.INFINITY, true);
			if(debugEnabled){
				S_LOGGER.debug("updating pom.xml");
			}
			// update connection url in pom.xml
			updateSCMConnection(appInfo, svnURL.toDecodedString());
		} catch (Exception e) {
			throw new PhrescoException(e);
		}
	}
	private void importToGITRepo(RepoDetail repodetail,ApplicationInfo appInfo, File appDir) throws Exception {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.importToGITRepo()");
		}
		boolean gitExists = false;
		if(new File(appDir.getPath() + FORWARD_SLASH + DOT + GIT).exists()) {
			gitExists = true;
		}
		try {
			//For https and ssh
			additionalAuthentication(repodetail.getPassPhrase());

			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(repodetail.getUserName(), repodetail.getPassword());
			
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setGitDir(appDir).readEnvironment().findGitDir().build();
			String dirPath = appDir.getPath();
			File gitignore = new File(dirPath + GITIGNORE_FILE);
			gitignore.createNewFile();
			if (gitignore.exists()) {
				String contents = FileUtils.readFileToString(gitignore);
				if (!contents.isEmpty() && !contents.contains(DO_NOT_CHECKIN_DIR)) {
					String source = NEWLINE + DO_NOT_CHECKIN_DIR + NEWLINE;
					OutputStream out = new FileOutputStream((dirPath + GITIGNORE_FILE), true);
					byte buf[] = source.getBytes();
					out.write(buf);
					out.close();
				} else if (contents.isEmpty()){
					String source = NEWLINE + DO_NOT_CHECKIN_DIR + NEWLINE;
					OutputStream out = new FileOutputStream((dirPath + GITIGNORE_FILE), true);
					byte buf[] = source.getBytes();
					out.write(buf);
					out.close();
				}
			}

			Git git = new Git(repository);
			
			InitCommand initCommand = Git.init();
			initCommand.setDirectory(appDir);
			git = initCommand.call();

			AddCommand add = git.add();
			add.addFilepattern(".");
			add.call();

			CommitCommand commit = git.commit().setAll(true);
			commit.setMessage(repodetail.getCommitMessage()).call();
			StoredConfig config = git.getRepository().getConfig();

			config.setString(REMOTE, ORIGIN, URL, repodetail.getRepoUrl());
			config.setString(REMOTE, ORIGIN, FETCH, REFS_HEADS_REMOTE_ORIGIN);
			config.setString(BRANCH, MASTER, REMOTE, ORIGIN);
			config.setString(BRANCH, MASTER, MERGE, REF_HEAD_MASTER);
			config.save();
			
			try {
				PushCommand pc = git.push();
				pc.setCredentialsProvider(cp).setForce(true);
				pc.setPushAll().call();
			} catch (Exception e){
				git.getRepository().close();
				PomProcessor appPomProcessor = new PomProcessor(new File(appDir,appInfo.getPhrescoPomFile()));
				appPomProcessor.removeProperty(Constants.POM_PROP_KEY_SRC_REPO_URL);
				appPomProcessor.save();
				throw e;
			}
			
			if (appInfo != null) {
				updateSCMConnection(appInfo, repodetail.getRepoUrl());
			}
			git.getRepository().close();
		} catch (Exception e) {
			Exception s = e;
			resetLocalCommit(appDir, gitExists, e);
			throw s;
		}
	}

	private void resetLocalCommit(File appDir, boolean gitExists, Exception e) throws PhrescoException {
		try {
			if(gitExists == true && e.getLocalizedMessage().contains("not authorized")) {
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				Repository repository = builder.setGitDir(appDir).readEnvironment().findGitDir().build();
				Git git = new Git(repository);

				InitCommand initCommand = Git.init();
				initCommand.setDirectory(appDir);
				git = initCommand.call();

				ResetCommand reset = git.reset();
				ResetType mode = ResetType.SOFT;
				reset.setRef("HEAD~1").setMode(mode);
				reset.call();

				git.getRepository().close();
			}
		} catch (Exception pe) {
			new PhrescoException(pe);
		}
	}

	public boolean commitToRepo(RepoDetail repodetail, File dir) throws Exception {
		if(debugEnabled) {
			S_LOGGER.debug("Entering Method  SCMManagerImpl.commitToRepo()");
		}
		try {
			if (SVN.equals(repodetail.getType())) {
				commitDirectoryContentToSubversion(repodetail, dir.getPath());
			} else if (GIT.equals(repodetail.getType())) {
				importToGITRepo(repodetail, null, dir);
			} else if (BITKEEPER.equals(repodetail.getType())) {
				return commitToBitKeeperRepo(repodetail.getRepoUrl(), dir.getPath(), repodetail.getCommitMessage());
			} else if (TFS.equals(repodetail.getType())) {
				return commitToTFSRepo(repodetail, dir.getCanonicalPath());
			}
		} catch (PhrescoException e) {
			throw e;
		}
		return true;
	}

	private SVNCommitInfo commitDirectoryContentToSubversion(RepoDetail repodetail, String subVersionedDirectory) throws SVNException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.commitDirectoryContentToSubversion()");
		}
		setupLibrary();

		final SVNClientManager cm = SVNClientManager.newInstance(new DefaultSVNOptions(), repodetail.getUserName(), repodetail.getPassword());
		SVNWCClient wcClient = cm.getWCClient();
		File subVerDir = new File(subVersionedDirectory);
		// This one recursively adds an existing local item under version control (schedules for addition)
		wcClient.doAdd(subVerDir, true, false, false, SVNDepth.INFINITY, false, false);
		return cm.getCommitClient().doCommit(new File[]{subVerDir}, false, repodetail.getCommitMessage(), null, null, false, true, SVNDepth.INFINITY);
	}

	private boolean commitToBitKeeperRepo(String repoUrl, String appDir, String commitMsg) throws PhrescoException {
		BufferedReader reader = null;
		File file = new File(Utility.getPhrescoTemp() + "bitkeeper.info");
		boolean isCommitted = false;
		try {
			List<String> commands = new ArrayList<String>();
			commands.add(BK_PARENT + SPACE + repoUrl);
			commands.add(BK_PULL);
			commands.add(BK_CI + SPACE + BK_ADD_COMMENT + commitMsg + KEY_QUOTES);
			commands.add(BK_ADD_FILES + SPACE + BK_ADD_COMMENT + commitMsg + KEY_QUOTES);
			commands.add(BK_COMMIT + SPACE + BK_ADD_COMMENT + commitMsg + KEY_QUOTES);
			commands.add(BK_PUSH);
			for (String command : commands) {
				Utility.executeStreamconsumer(appDir, command, new FileOutputStream(file));
			}
			reader = new BufferedReader(new FileReader(file));
			String strLine;
			while ((strLine = reader.readLine()) != null) {
				if (strLine.contains("push") && strLine.contains("OK")) {
					isCommitted = true;
				} else if (strLine.contains("Nothing to push")) {
					throw new PhrescoException("Nothing to push");
				} else if (strLine.contains("Cannot resolve host")) {
					throw new PhrescoException("Failed to commit");
				}
			}
		} catch (Exception e) {
			throw new PhrescoException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new PhrescoException(e);
				}
			}
			if (file.exists()) {
				file.delete();
			}
		}

		return isCommitted;
	}

	private boolean commitToTFSRepo(RepoDetail repoDetail, String appDir) throws PhrescoException {
		int commited = 100;
		if (CollectionUtils.isNotEmpty(repoDetail.getTfsAddedFiles())) {
			int added = addNewFilesToTFS(appDir, repoDetail);
			if (added != -1) {
				throw new PhrescoException("Unable to add new files to repo");
			}
		} 
		if (CollectionUtils.isNotEmpty(repoDetail.getTfsEditedFiles())) {
			commited = commitPendingChangesToTFS(appDir, repoDetail);
		}
		boolean success = (commited == 0) ? true : false;
		return success;
	}

	public SVNCommitInfo deleteDirectoryInSubversion(RepoDetail repodetail, String subVersionedDirectory) throws SVNException, IOException {
		if(debugEnabled){
			S_LOGGER.debug("Entering Method  SCMManagerImpl.commitDirectoryContentToSubversion()");
		}
		setupLibrary();

		File subVerDir = new File(subVersionedDirectory);		
		final SVNClientManager cm = SVNClientManager.newInstance(new DefaultSVNOptions(), repodetail.getUserName(), repodetail.getPassword());

		SVNWCClient wcClient = cm.getWCClient();
		//wcClient.doDelete(subVerDir, true, false);
		FileUtils.listFiles(subVerDir, TrueFileFilter.TRUE, FileFilterUtils.makeSVNAware(null));
		for (File child : subVerDir.listFiles()) {
			if (!(DOT+SVN).equals(child.getName())) {
				wcClient.doDelete(child, true, true, false);
			}
		}

		return cm.getCommitClient().doCommit(new File[]{subVerDir}, false, repodetail.getCommitMessage(), null, null, false, true, SVNDepth.INFINITY);
	}

	public List<RepoFileInfo> getCommitableFiles(File path, String revision) throws SVNException {

		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		final List<RepoFileInfo> filesList = new ArrayList<RepoFileInfo>();
		svnClientManager.getStatusClient().doStatus(path, SVNRevision.parse(revision), SVNDepth.INFINITY, false, false, false, false, new ISVNStatusHandler() {
			public void handleStatus(SVNStatus status) throws SVNException {
				SVNStatusType statusType = status.getContentsStatus();
				if (statusType != SVNStatusType.STATUS_NONE && statusType != SVNStatusType.STATUS_NORMAL
						&& statusType != SVNStatusType.STATUS_IGNORED) {
					RepoFileInfo repoFileInfo = new RepoFileInfo();
					String filePath = status.getFile().getPath();
					String FileStatus = Character.toString(statusType.getCode());
					repoFileInfo.setContentsStatus(statusType);
					repoFileInfo.setStatus(FileStatus);
					repoFileInfo.setCommitFilePath(filePath);
					filesList.add(repoFileInfo);
				}
			}
		}, null);

		return filesList;
	}

	public List<RepoFileInfo> getGITCommitableFiles(File path) throws IOException, GitAPIException
	{
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(path).readEnvironment().findGitDir().build(); 
		Git git = new Git(repository);
		List<RepoFileInfo> fileslist = new ArrayList<RepoFileInfo>();
		InitCommand initCommand = Git.init();
		initCommand.setDirectory(path);
		git = initCommand.call();
		Status status = git.status().call();

		Set<String> added = status.getAdded();
		Set<String> changed = status.getChanged();
		Set<String> conflicting = status.getConflicting();
		Set<String> missing= status.getMissing();
		Set<String> modified = status.getModified();
		Set<String> removed = status.getRemoved();
		Set<String> untracked = status.getUntracked();

		if (!added.isEmpty()) {
			for (String add : added) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + add;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("A");
				fileslist.add(repoFileInfo);
			}
		}

		if (!changed.isEmpty()) {
			for (String change : changed) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + change;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("M");
				fileslist.add(repoFileInfo);
			}
		}

		if (!conflicting.isEmpty()) {
			for (String conflict : conflicting) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + conflict;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("C");
				fileslist.add(repoFileInfo);
			}
		}

		if (!missing.isEmpty()) {
			for (String miss : missing) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + miss;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("!");
				fileslist.add(repoFileInfo);
			}
		}

		if (!modified.isEmpty()) {
			for (String modify : modified) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + modify;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("M");
				fileslist.add(repoFileInfo);
			}
		}

		if (!removed.isEmpty()) {
			for (String remove : removed) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + remove;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("D");
				fileslist.add(repoFileInfo);
			}
		}

		if (!untracked.isEmpty()) {
			for (String untrack : untracked) {
				RepoFileInfo repoFileInfo = new RepoFileInfo();
				String filePath = path + BACK_SLASH + untrack;
				repoFileInfo.setCommitFilePath(filePath);
				repoFileInfo.setStatus("?");
				fileslist.add(repoFileInfo);
			}
		}
		git.getRepository().close();
		return fileslist;
	}

	public List<String> getSvnLogMessages(String Url, String userName, String Password, String repoType, String appDirName) throws PhrescoException {
		List<String> logMessages = new ArrayList<String>();
		if(repoType.equalsIgnoreCase(SVN)) {
			setupLibrary();
			long startRevision = 0;
			long endRevision = -1; //HEAD (the latest) revision
			SVNRepository repository = null;
			try {
				repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(Url));
				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, Password);
				repository.setAuthenticationManager(authManager);
				Collection logEntries = null;
				logEntries = repository.log( new String[] { "" } , null , startRevision , endRevision , false , true );
				for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
					SVNLogEntry logEntry = (SVNLogEntry) entries.next();
					logMessages.add(logEntry.getMessage());
				}
			} catch (SVNException e) {
				throw new PhrescoException(e);
			}finally {
				if (repository != null) {
					repository.closeSession();
				}
			}
		}
		else if(repoType.equalsIgnoreCase(GIT))	{
			try {
					FileRepositoryBuilder builder = new FileRepositoryBuilder();
					String dotGitDir = Utility.getProjectHome() + appDirName+ File.separator + DOT_GIT;
					File dotGitDirectory = new File(dotGitDir);
					if(!dotGitDirectory.exists()) {
						dotGitDir = Utility.getProjectHome() +appDirName + File.separator + appDirName+ File.separator + DOT_GIT;
						dotGitDirectory= new File(dotGitDir);
					}
					if(!dotGitDir.isEmpty()) {
					 Repository repo = builder.setGitDir(dotGitDirectory).setMustExist(true).build();
					 Git git = new Git(repo);
					 Iterable<RevCommit> log = git.log().call();
					 for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
						 RevCommit rev = iterator.next();
						if(!rev.getFullMessage().isEmpty()) {	
							logMessages.add(rev.getFullMessage());				}
						}
					 repo.close();
				}
			}
			catch(GitAPIException ge) {
				throw new PhrescoException(ge);
			}
			catch(IOException ioe) {
				throw new PhrescoException(ioe);
			}
		}
		return logMessages;
	}

	public SVNCommitInfo commitSpecifiedFiles(List<File> listModifiedFiles, String username, String password, String commitMessage) throws Exception {
		setupLibrary();

		final SVNClientManager cm = SVNClientManager.newInstance(new DefaultSVNOptions(), username, password);
		SVNWCClient wcClient = cm.getWCClient();
		File[] comittableFiles = listModifiedFiles.toArray(new File[listModifiedFiles.size()]);

		SVNCommitClient cc = cm.getCommitClient(); 
		cc.setCommitParameters(new ISVNCommitParameters() { 

			// delete even those files 
			// that are not scheduled for deletion. 
			public Action onMissingFile(File file) { 
				return DELETE; 
			} 
			public Action onMissingDirectory(File file) { 
				return DELETE; 
			} 

			// delete files from disk after committing deletion. 
			public boolean onDirectoryDeletion(File directory) { 
				return true; 
			} 
			public boolean onFileDeletion(File file) { 
				return true; 
			} 
		}); 

		//to List Unversioned Files 
		List<File> unversionedFiles = new ArrayList<File>();
		for (File file : comittableFiles) {
			List<RepoFileInfo> status = getCommitableFiles(new File(file.getPath()), HEAD_REVISION);
			if (CollectionUtils.isNotEmpty(status)) {
				RepoFileInfo svnStatus = status.get(0);
				SVNStatusType contentsStatus = svnStatus.getContentsStatus();
				if(UNVERSIONED.equalsIgnoreCase(contentsStatus.toString())) {
					unversionedFiles.add(file);
				} 
			}
		}

		//Add only Unversioned Files
		if (CollectionUtils.isNotEmpty(unversionedFiles)) {
			File[] newlyAddedFiles = unversionedFiles.toArray(new File[unversionedFiles.size()]);
			wcClient.doAdd(newlyAddedFiles, true, false, false, SVNDepth.INFINITY, false, false, false);
		}

		SVNCommitInfo commitInfo = cc.doCommit(comittableFiles, false, commitMessage, null, null, false, true, SVNDepth.INFINITY);

		return commitInfo;
	}

	public String svnCheckout(String path, String userName, String password, String repoURL, String revision) {
		DAVRepositoryFactory.setup();
		SVNClientManager clientManager = SVNClientManager.newInstance();
		ISVNAuthenticationManager authManager = new BasicAuthenticationManager(userName, password);
		clientManager.setAuthenticationManager(authManager);
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		try
		{
			File file = new File(path);
			SVNURL url = SVNURL.parseURIEncoded(repoURL);
			updateClient.doCheckout(url, file, SVNRevision.UNDEFINED, SVNRevision.parse(revision), true);
		}
		catch (SVNException e) {
			return e.getLocalizedMessage();
		}
		return SUCCESSFUL;
	}

	public ProjectInfo importFromPerforce(RepoDetail repodetail, File tempFile) throws Exception {
		perforceSync(repodetail, tempFile.getAbsolutePath(), tempFile.getName(),"import");
		String path = tempFile.getAbsolutePath();
		String[] pathArr = repodetail.getStream().split("/");
		String projName = pathArr[pathArr.length-1];
		File actualFile = new  File(path+"/"+projName);
		File dotProjectFile = new File(actualFile, FOLDER_DOT_PHRESCO+ File.separator + PROJECT_INFO);
		ProjectInfo projectInfo = getRepoProjectInfo(dotProjectFile);
		if(projectInfo!= null){
			ApplicationInfo applicationInfo = projectInfo.getAppInfos().get(0);
			if (applicationInfo != null) {
				updateSCMConnection(applicationInfo, repodetail.getRepoUrl()+repodetail.getStream());
			}
		}
		return projectInfo;
	}

	public void perforceSync(RepoDetail repodetail,String baseDir , String projectName,String flag) throws ConnectionException, RequestException  {
		String url=repodetail.getRepoUrl();
		String userName=repodetail.getUserName();
		String password=repodetail.getPassword();
		String stream=repodetail.getStream();
		try {		
			IOptionsServer server = ServerFactory.getOptionsServer("p4java://"+url, null, null);
			server.connect();
			server.setUserName(userName);
			if(password!=""){
				server.login(password);
			}			
			IClient client = new Client();
			client.setName(projectName);
			if(flag.equals("update")){
				String[] rootArr=baseDir.split(projectName);
				String root=rootArr[0].substring(0,rootArr[0].length()-1);
				client.setRoot(root);
			} else {
				client.setRoot(baseDir);
			}
			client.setServer(server); 
			server.setCurrentClient(client);
			ClientViewMapping tempMappingEntry = new ClientViewMapping();
			tempMappingEntry.setLeft(stream+"/...");
			tempMappingEntry.setRight("//"+projectName+"/...");
			ClientView clientView = new ClientView();
			clientView.addEntry(tempMappingEntry);
			try {
				String[] arr=repodetail.getStream().split("//");
				String[] arr1=arr[1].split("/");
				client.setStream("//"+arr1[0]+"/"+arr1[1]);
				client.setClientView(clientView);
				client.setOptions(new ClientOptions("noallwrite clobber nocompress unlocked nomodtime normdir"));
			}catch (ArrayIndexOutOfBoundsException e) {
				throw new RequestException();
			}
			if (client != null) {	
				List<IFileSpec> syncList = client.sync(FileSpecBuilder.makeFileSpecList(stream+"/..."),new SyncOptions());
				for (IFileSpec fileSpec : syncList) {
					if (fileSpec != null) {
						if (fileSpec.getOpStatus() == FileSpecOpStatus.VALID) {

						} else {
							System.err.println(fileSpec.getStatusMessage());
						}
					}
				}}
			IOFileFilter filter=new IOFileFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					return true;
				}
				@Override
				public boolean accept(File arg0) {
					return true;
				}
			};
			Iterator<File> iterator= FileUtils.iterateFiles(new File(baseDir), filter, filter);
			while(iterator.hasNext()){
				File file = iterator.next();
				file.setWritable(true);
			}

		} catch (RequestException rexc) {
			System.err.println(rexc.getDisplayString());
			rexc.printStackTrace();
			throw new RequestException();
		} catch (P4JavaException jexc) {
			System.err.println(jexc.getLocalizedMessage());
			jexc.printStackTrace();
			throw new ConnectionException();
		} catch (Exception exc) {
			System.err.println(exc.getLocalizedMessage());
			exc.printStackTrace();
		}

	}

	@Override
	public void importTest(ApplicationInfo applicationInfo, RepoInfo repoInfo) throws Exception {
		StringBuilder builder = new StringBuilder(Utility.getProjectHome()).append(File.separator);
		builder.append(applicationInfo.getAppDirName());
		if(repoInfo.isSplitTest()) {
			File pomFile = getPomFromRepository(applicationInfo, repoInfo);
			PomProcessor processor = new PomProcessor(pomFile);
			String testDir = processor.getProperty(Constants.POM_PROP_KEY_SPLIT_TEST_DIR);
			if(repoInfo.isSplitTest() && StringUtils.isEmpty(testDir)) {
				builder.append(File.separator).append(applicationInfo.getAppDirName() + Constants.SUFFIX_TEST);
			} else if (repoInfo.isSplitTest() && StringUtils.isNotEmpty(testDir)) {
				builder.append(File.separator).append(testDir);
			}
		}
		RepoDetail testRepoDetail = repoInfo.getTestRepoDetail();
		String type = testRepoDetail.getType();
		if(type.equals(SVN)) {
			svnCheckout(builder.toString(), testRepoDetail.getUserName(), testRepoDetail.getPassword(), 
					testRepoDetail.getRepoUrl(), testRepoDetail.getRevision());
		}
		if(type.equals(GIT)) {
			String uuid = UUID.randomUUID().toString();
			File gitImportTemp = new File(Utility.getPhrescoTemp(), uuid);
			importFromGit(testRepoDetail, gitImportTemp);
			importToWorkspace(gitImportTemp, new File(builder.toString()));
		}
		if(type.equals(BITKEEPER)) {
			String uuid = UUID.randomUUID().toString();
			File importTemp = new File(Utility.getPhrescoTemp(), uuid);
			boolean imported = importFromBitKeeper(testRepoDetail.getRepoUrl(), importTemp);
			if(imported) {
				importToWorkspace(importTemp, new File(builder.toString()));
			}
		}
		if(type.equals(PERFORCE)) {
			String uuid = UUID.randomUUID().toString();
			File tempFile = new File(Utility.getPhrescoTemp(), uuid);
			FileUtils.forceMkdir(tempFile);
			importFromPerforce(testRepoDetail, tempFile);
			importToWorkspace(tempFile, new File(builder.toString()));
		}
		if (type.equals(TFS)) {
			String uuid = UUID.randomUUID().toString();
			File tempFile = new File(Utility.getPhrescoTemp(), uuid);
			FileUtils.forceMkdir(tempFile);
			importFromTfs(testRepoDetail, tempFile, "");
			importToWorkspace(tempFile, new File(builder.toString()));
		}
	}

	@Override
	public void importPhresco(ApplicationInfo applicationInfo, RepoInfo repoInfo)
	throws Exception {
		StringBuilder builder = new StringBuilder(Utility.getProjectHome()).append(File.separator);
		builder.append(applicationInfo.getAppDirName());
		if(repoInfo.isSplitPhresco()) {
			builder.append(File.separator).append(applicationInfo.getAppDirName() + Constants.SUFFIX_PHRESCO);
		}
		RepoDetail phrescoRepoDetail = repoInfo.getPhrescoRepoDetail();
		String type = phrescoRepoDetail.getType();
		if(type.equals(SVN)) {
			svnCheckout(builder.toString(), phrescoRepoDetail.getUserName(), phrescoRepoDetail.getPassword(), 
					phrescoRepoDetail.getRepoUrl(), phrescoRepoDetail.getRevision());
		}
		if(type.equals(GIT)) {
			String uuid = UUID.randomUUID().toString();
			File gitImportTemp = new File(Utility.getPhrescoTemp(), uuid);
			importFromGit(phrescoRepoDetail, gitImportTemp);
			importToWorkspace(gitImportTemp, new File(builder.toString()));
		}
		if(type.equals(BITKEEPER)) {
			String uuid = UUID.randomUUID().toString();
			File importTemp = new File(Utility.getPhrescoTemp(), uuid);
			boolean imported = importFromBitKeeper(phrescoRepoDetail.getRepoUrl(), importTemp);
			if(imported) {
				importToWorkspace(importTemp, new File(builder.toString()));
			}
		}
		if(type.equals(PERFORCE)) {
			String uuid = UUID.randomUUID().toString();
			File tempFile = new File(Utility.getPhrescoTemp(), uuid);
			importFromPerforce(phrescoRepoDetail, tempFile);
			importToWorkspace(tempFile, new File(builder.toString()));
		}
		if(type.equals(TFS)) {
			String uuid = UUID.randomUUID().toString();
			File tempFile = new File(Utility.getPhrescoTemp(), uuid);
			importFromTfs(phrescoRepoDetail, tempFile, "");
			importToWorkspace(tempFile, new File(builder.toString()));
		}
	}
	public static TFSTeamProjectCollection connectToTFS(String userName, String password, String collectionUrl ) throws PhrescoException {
		TFSTeamProjectCollection tpc = null;
		Credentials credentials;

		try {
			// In case no username is provided and the current platform supports
			// default credentials, use default credentials
			if ((userName == null || userName.length() == 0) && CredentialsUtils.supportsDefaultCredentials()) {
				credentials = new DefaultNTCredentials();
			} else {
				credentials = new UsernamePasswordCredentials(userName, password);
			}

			URI httpProxyURI = null;
			if (HTTP_PROXY_URL != null && HTTP_PROXY_URL.length() > 0) {
				try {
					httpProxyURI = new URI(HTTP_PROXY_URL);
				} catch (URISyntaxException e) {
					// Do Nothing
				}
			}
			ConsoleSamplesConnectionAdvisor connectionAdvisor = new ConsoleSamplesConnectionAdvisor(httpProxyURI);
			tpc = new TFSTeamProjectCollection(URIUtils.newURI(collectionUrl), credentials, connectionAdvisor);
		} catch (Exception e) {
			throw new PhrescoException(e);
		}

		return tpc;
	}

	private int addNewFilesToTFS(String appDir, RepoDetail repoDetail) throws PhrescoException {
		int added = -1; 
		try {
			Command cmdAdd = new CommandAdd();
			List<Option> options = new ArrayList<Option>(3);
			appendRecursive(options);
			appendNoPrompt(options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			String[] freeArguments = new String[repoDetail.getTfsAddedFiles().size()];
			freeArguments = repoDetail.getTfsAddedFiles().toArray(freeArguments);
			added = executeCmd(freeArguments, cmdAdd, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}

		return added;
	}

	private int addFilesToTFSRepo(String appDir, RepoDetail repoDetail) throws PhrescoException {
		int added = -1; 
		try {
			Command cmdAdd = new CommandAdd();
			List<Option> options = new ArrayList<Option>(3);
			appendRecursive(options);
			appendNoPrompt(options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			added = executeCmd(new String[] { appDir }, cmdAdd, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}

		return added;
	}

	private int checkinProjectToTFSRepo(String appDir, RepoDetail repoDetail) throws PhrescoException {
		int commited = -1; 
		try {
			Command cmdCheckIn = new CommandCheckin();
			List<Option> options = new ArrayList<Option>(4);
			appendRecursive(options);
			appendNoPrompt(options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			appendComment(repoDetail.getCommitMessage(), options);

			commited = executeCmd(new String[] { appDir }, cmdCheckIn, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}

		return commited;
	}

	private int commitPendingChangesToTFS(String appDir, RepoDetail repoDetail) throws PhrescoException {
		int commited = -1; 
		try {
			Command cmdCheckIn = new CommandCheckin();
			List<Option> options = new ArrayList<Option>(4);
			appendRecursive(options);
			appendNoPrompt(options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			appendComment(repoDetail.getCommitMessage(), options);

			String[] freeArguments = new String[repoDetail.getTfsEditedFiles().size()];
			freeArguments = repoDetail.getTfsEditedFiles().toArray(freeArguments);
			commited = executeCmd(freeArguments, cmdCheckIn, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		}  catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}

		return commited;
	}

	public Map<String, List<String>> getPendingChanges(String appDir) throws PhrescoException {
		Map<String, List<String>> changes;
		try {
			com.photon.phresco.framework.impl.CommandStatus cmdStatus = new com.photon.phresco.framework.impl.CommandStatus();
			List<Option> options = new ArrayList<Option>(2);
			VersionControlOptions optionsMap1 = new VersionControlOptions();
			options.add(optionsMap1.findOption("-recursive"));
			appendCredentials("someone@company.com", "dummy", options);
			cmdStatus.setOptions((Option[]) options .toArray(new Option[0]), new VersionControlCommands().getGlobalOptions());
			cmdStatus.setFreeArguments(new String[] { appDir });
			cmdStatus.run();
			cmdStatus.close();
			PendingSet[] pendingSets = cmdStatus.getPendingSets();
			List<String> editedFiles = new ArrayList<String> ();
			List<String> addedFiles = new ArrayList<String> ();
			changes = new HashMap<String, List<String>>();
			if (pendingSets != null && pendingSets.length > 0) {
				for (PendingSet pendingSet : pendingSets) {
					PendingChange[] pendingChanges = pendingSet.getPendingChanges();
					getChangedFilesList(pendingChanges, addedFiles, editedFiles);
					PendingChange[] candidatePendingChanges = pendingSet.getCandidatePendingChanges();
					getChangedFilesList(candidatePendingChanges, addedFiles, editedFiles);
				}
				if (CollectionUtils.isNotEmpty(addedFiles)) {
					changes.put(ADD, addedFiles);
				}
				if (CollectionUtils.isNotEmpty(editedFiles)) {
					changes.put(EDIT, editedFiles);
				}
			}
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		}  catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}

		return changes;
	}

	private static void getChangedFilesList(PendingChange[] pendingChanges, List<String> addedFiles, List<String> editedFiles) {
		for (PendingChange pendingChange : pendingChanges) {
			ChangeType changeType = pendingChange.getChangeType();
			int intFlags = changeType.toIntFlags();
			//4 -- edit, 14 -- add
			if (intFlags == 4) {
				editedFiles.add(pendingChange.getLocalItem());
			} 
			if (intFlags == 14) {
				addedFiles.add(pendingChange.getLocalItem());
			}
		}
	}

	private int createWorkspace(RepoDetail repoDetail)  throws PhrescoException {
		int executeCmd;
		try {
			acceptLicense();
			Command cmdWorkspace = new CommandWorkspace();
			List<Option> options = new ArrayList<Option>(4);
			options.add(optionsMap.findOption("-new"));
			appendNoPrompt(options);
			appendServerUrl(repoDetail.getRepoUrl(), options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			executeCmd = executeCmd(new String[] { repoDetail.getWorkspaceName() }, cmdWorkspace, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}
		return executeCmd;
	}

	public int deleteWorkspace(RepoDetail repoDetail) throws PhrescoException {
		int executeCmd = 1;
		try {
			if (repoDetail.getType().equals(TFS)) {
			Command cmdWorkspace = new CommandWorkspace();
			List<Option> options = new ArrayList<Option>(4);
			options.add(optionsMap.findOption("-delete"));
			appendNoPrompt(options);
			appendServerUrl(repoDetail.getRepoUrl(), options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			executeCmd = executeCmd(new String[] { repoDetail.getWorkspaceName() }, cmdWorkspace, options);
			}
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(AUTHENTICATION_FAILED);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		} catch (TFSFederatedAuthException e) {
			throw new PhrescoException(AUTHENTICATION_FAILED);
		}
		return executeCmd;
	}

	private void acceptLicense() throws PhrescoException {
		try {
			Command licenseCmd = new CommandEULA();
			List<Option> options = new ArrayList<Option>(4);
			options.add(optionsMap.findOption("-accept"));
			executeCmd(new String[] {}, licenseCmd, options);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}
	}

	private int mapLocalWorkspaceToRemote(String localPath, String dotPhrescoPath, RepoDetail repodetail)
	throws PhrescoException {
		int executeCmd;
		try {
			Command cmdWorkFold = new CommandWorkFold();
			List<Option> options = new ArrayList<Option>(4);
			appendMapWorkspace(options);
			appendWorkspaceName(repodetail.getWorkspaceName(), options);
			appendServerUrl(repodetail.getRepoUrl(), options);
			appendCredentials(repodetail.getUserName(), repodetail.getPassword(), options);
			String serverPath = repodetail.getServerPath();
			if (StringUtils.isNotEmpty(dotPhrescoPath)) {
				if (!serverPath.endsWith(FORWARD_SLASH)) {
					serverPath = serverPath.concat(FORWARD_SLASH);
				}
				serverPath = serverPath.concat(dotPhrescoPath);
			}
			executeCmd = executeCmd(new String[] { serverPath , localPath }, cmdWorkFold, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		} 

		return executeCmd;
	}

	private int getProjectFromTFS(RepoDetail repoDetail, String localPath) 
	throws PhrescoException {
		int executeCmd; 
		try {
			Command cmdGet = new CommandGet();
			List<Option> options = new ArrayList<Option>(4);
			options.add(optionsMap.findOption("-all"));
			appendNoPrompt(options);
			appendCredentials(repoDetail.getUserName(), repoDetail.getPassword(), options);
			appendRecursive(options);
			executeCmd = executeCmd(new String[] { localPath }, cmdGet, options);
		} catch (TFSUnauthorizedException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionValueException e) {
			throw new PhrescoException(e);
		} catch (InvalidOptionException e) {
			throw new PhrescoException(e);
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ArgumentException e) {
			throw new PhrescoException(e);
		} catch (CLCException e) {
			throw new PhrescoException(e);
		} catch (LicenseException e) {
			throw new PhrescoException(e);
		}  

		return executeCmd;
	}

	private void appendMapWorkspace(List<Option> options) throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-map"));
	}

	private int executeCmd(String[] args, Command cmd, List<Option> options) 
	throws InvalidOptionException, ArgumentException, MalformedURLException, CLCException, LicenseException {
		cmd.setOptions((Option[]) options .toArray(new Option[0]), new VersionControlCommands().getGlobalOptions());
		cmd.setFreeArguments(args);
		cmd.run();
		cmd.close();

		return cmd.getExitCode();
	}

	private void appendWorkspaceName(String workspaceName, List<Option> options)
	throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-workspace:"+workspaceName));
	}

	private void appendCredentials(String username, String password, List<Option> options)
	throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-login:" + username + "," + password));
	}

	private void appendServerUrl(String url, List<Option> options) throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-server:"+url));
	}

	private void appendComment(String comment, List<Option> options) throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-comment:\""+comment+"\""));
	}

	private void appendRecursive(List<Option> options) throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-recursive"));
	}

	private void appendNoPrompt(List<Option> options)
	throws InvalidOptionValueException {
		options.add(optionsMap.findOption("-noprompt"));
	}
	
	private static boolean authentication(String username, String password, String repoType, String repoUrl) throws PhrescoException {		
		boolean validUser = false;
		if(GIT.equalsIgnoreCase(repoType))
		{
			try {
				URL url = new URL(GIT_URL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(GET);
				if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
					String credentials = BASIC_SPACE + toBase64(username + COLON + password);
					connection.setRequestProperty( AUTHORIZATION, credentials);
					if (connection.getResponseCode() == 200) {
						validUser = true;
					}else {
						validUser = false;
					} 
				}
			} catch (MalformedURLException e) {
				throw new PhrescoException(e);
			} catch (ProtocolException e) {
				throw new PhrescoException(e);
			} catch (IOException e) {
				throw new PhrescoException(e);
			}
		}else if(SVN.equalsIgnoreCase(repoType)) {
			try {
					DAVRepositoryFactory.setup();
					SVNRepositoryFactoryImpl.setup();
					SVNURL url = SVNURL.parseURIEncoded(repoUrl);
					SVNRepository svnRepository = SVNRepositoryFactory.create(url);
					BasicAuthenticationManager bm=new BasicAuthenticationManager(username, password);
					svnRepository.setAuthenticationManager(bm);
					svnRepository.testConnection();
					validUser=true;
				} catch(SVNException e) {
					e.printStackTrace();
					if(e.getErrorMessage().getErrorCode() == SVNErrorCode.RA_DAV_REQUEST_FAILED) {
						throw new PhrescoException("Invalid URL");
					}
					else if(e.getErrorMessage().getErrorCode() == SVNErrorCode.RA_NOT_AUTHORIZED) {
						throw new PhrescoException("Invalid User ");
					}
				}
				}
		return validUser;
	}
	
	private static final String toBase64(final String content) {
		byte[] bytes;
		try {
			bytes = content.getBytes(UTF_8);
		} catch (UnsupportedEncodingException e) {
			bytes = content.getBytes();
		}
		return toBase64(bytes);
	}
	
	private static final String toBase64(final byte[] content) {
		return Base64.encodeBytes(content);
	}

}
