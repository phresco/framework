package com.photon.phresco.framework.repository;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.api.RepositoryManager;
import com.photon.phresco.util.Utility;

public class GitRepositoryImpl implements RepositoryManager, FrameworkConstants {
	public Document getSource(String appDirName, String username, String password, String srcRepoUrl) throws PhrescoException {
		Document document = null;
		try {
			boolean authenticated = authentication(username, password);
			if (authenticated) {
				document = getGitSourceRepo(appDirName, srcRepoUrl);
			} else {
				throw new PhrescoException(srcRepoUrl);
			}
		} catch (PhrescoException e) {
		throw new PhrescoException(e);
	}
	return document;
}
	
	private Document getGitSourceRepo(String appDirName,String srcRepoUrl) throws PhrescoException {
		Document document = null;
		String url = null;
		try {
			String gitPaths = Utility.getProjectHome() + File.separator + appDirName + File.separator +  ".git";
			String dotPhrescogitPaths = Utility.getProjectHome() + File.separator + appDirName + File.separator + appDirName + "-phresco" + File.separator +  ".git";
			File gitPath = new File(gitPaths);
			File dotPhrescogitPath = new File(dotPhrescogitPaths);
			String path = Utility.getProjectHome() + File.separator + appDirName ;
			if (gitPaths != null && gitPath.exists() || dotPhrescogitPaths != null && dotPhrescogitPath.exists()) {
				String dotFolderLocation = Utility.getDotPhrescoFolderPath(path, "");
				File sourceFolderLocation = new File(dotFolderLocation).getParentFile();
				if (!sourceFolderLocation.exists()) {
					return null;
				}
				Git git = Git.open(new File(sourceFolderLocation.getPath()));
				List<String> branchList = new ArrayList<String>();
				List<String> tagLists = new ArrayList<String>();

				Collection<Ref> remoteCall = Git.lsRemoteRepository().setHeads(true).setRemote(srcRepoUrl).call();
				for (Ref ref : remoteCall) {
					if (StringUtils.isNotEmpty(ref.getName())  && !ref.getName().contains(HEAD_REVISION)) {
						branchList.add(ref.getName());
					}
				}
				
				ListTagCommand tagList = git.tagList();
				Map<String, Ref> tags = tagList.getRepository().getTags();
				Set<Entry<String,Ref>> entrySet = tags.entrySet();
				for (Entry<String, Ref> entry : entrySet) {
					tagLists.add(entry.getKey());
				}

				StoredConfig config = git.getRepository().getConfig();
				Set<String> subsections = config.getSubsections(REMOTE);
				for (String string : subsections) {
					String[] urlList = config.getStringList(REMOTE, string, URL);
					for (String urlPath : urlList) {
						url = urlPath;
					}
				}
				document = constructGitTree(branchList, tagLists, url, appDirName);
			}
		} catch (IOException e) {
			throw new PhrescoException(e);
		} catch (GitAPIException e) {
			throw new PhrescoException(e);
		}
		return document;
	}
	
	private static Document constructGitTree(List<String> branchList,	List<String> tagLists, String url, String appDirName) throws PhrescoException {
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document doc = documentBuilder.newDocument();

			Element rootElement = doc.createElement(ROOT);
			doc.appendChild(rootElement);

			Element rootItem = doc.createElement(ITEM);
			rootItem.setAttribute(TYPE, FOLDER);
			rootItem.setAttribute(NAME, ROOT_ITEM);

			rootElement.appendChild(rootItem);

			Element urlItem = doc.createElement(ITEM);
			urlItem.setAttribute(TYPE, FOLDER);
			urlItem.setAttribute(NAME, appDirName);
			urlItem.setAttribute(URL, url);

			rootItem.appendChild(urlItem);

			Element branchItem = doc.createElement(ITEM);
			branchItem.setAttribute(TYPE, FOLDER);
			branchItem.setAttribute(NAME, BRANCHES);
			branchItem.setAttribute(URL, url);
			urlItem.appendChild(branchItem);


			for (String branch: branchList) {
				String branchName = branch.substring(branch.lastIndexOf("/") + 1, branch.length());
				Element branchItems = doc.createElement(ITEM);
				branchItems.setAttribute(TYPE, FILE);
				branchItems.setAttribute(NAME, branchName);
				branchItems.setAttribute(URL, url);
				branchItems.setAttribute(REQ_APP_DIR_NAME, appDirName);
				branchItems.setAttribute(NATURE, BRANCHES);
				branchItem.appendChild(branchItems);
			}

			Element tagItem = doc.createElement(ITEM);
			tagItem.setAttribute(TYPE, FOLDER);
			tagItem.setAttribute(NAME, TAGS);
			tagItem.setAttribute(URL, url);
			urlItem.appendChild(tagItem);

			for (String tag: tagLists) {
				Element tagItems = doc.createElement(ITEM);
				tagItems.setAttribute(TYPE, FILE);
				tagItems.setAttribute(NAME, tag);
				tagItems.setAttribute(URL, url);
				tagItems.setAttribute(REQ_APP_DIR_NAME, appDirName);
				tagItems.setAttribute(NATURE, TAGS);
				tagItem.appendChild(tagItems);
			}
			return doc;
		} catch (ParserConfigurationException e) {
			throw new PhrescoException(e);
		}
	}
	
	private static boolean authentication(String username, String password) throws PhrescoException {
		boolean validUser = false;
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
			} else {
				validUser = false;
			}
		} catch (MalformedURLException e) {
			throw new PhrescoException(e);
		} catch (ProtocolException e) {
			throw new PhrescoException(e);
		} catch (IOException e) {
			throw new PhrescoException(e);
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
