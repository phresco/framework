/**
 * Framework Web Archive
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
package com.photon.phresco.framework.rest.api.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.photon.phresco.commons.FrameworkConstants;
import com.photon.phresco.commons.model.ApplicationInfo;
import com.photon.phresco.commons.model.ProjectInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.framework.FrameworkConfiguration;
import com.photon.phresco.framework.PhrescoFrameworkFactory;
import com.photon.phresco.framework.api.ProjectManager;
import com.photon.phresco.util.Constants;
import com.photon.phresco.util.Utility;
import com.phresco.pom.exception.PhrescoPomException;
import com.phresco.pom.model.Model.Modules;
import com.phresco.pom.util.PomProcessor;

public class FrameworkServiceUtil implements Constants, FrameworkConstants {
	
	/**
	 * To get the application info of the given appDirName
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static ApplicationInfo getApplicationInfo(String appDirName) throws PhrescoException {
		StringBuilder builder  = new StringBuilder();
		builder.append(Utility.getProjectHome())
		.append(appDirName)
		.append(File.separatorChar)
		.append(DOT_PHRESCO_FOLDER)
		.append(File.separatorChar)
		.append(PROJECT_INFO_FILE);
		ApplicationInfo applicationInfo;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(builder.toString()));
			Gson gson = new Gson();
			ProjectInfo projectInfo = gson.fromJson(bufferedReader, ProjectInfo.class);
			applicationInfo = projectInfo.getAppInfos().get(0);
			return applicationInfo;
		} catch (JsonSyntaxException e) {
			throw new PhrescoException(e);
		} catch (JsonIOException e) {
			throw new PhrescoException(e);
		} catch (FileNotFoundException e) {
			throw new PhrescoException(e);
		}
	}
	
	/**
	 * To get the PomProcessor instance for the given application
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static PomProcessor getPomProcessor(String appDirName) throws PhrescoException {
		try {
			StringBuilder builder  = new StringBuilder();
			builder.append(Utility.getProjectHome())
			.append(appDirName)
			.append(File.separatorChar)
			.append(POM_NAME);
			return new PomProcessor(new File(builder.toString()));
		} catch (PhrescoPomException e) {
			throw new PhrescoException(e);
		}
	}

	/**
	 * To get the application home for the given appDirName
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static String getApplicationHome(String appDirName) throws PhrescoException {
        StringBuilder builder = new StringBuilder(Utility.getProjectHome());
        builder.append(appDirName);
        return builder.toString();
	}

	/**
	 * To get the modules of the given application
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static List<String> getProjectModules(String appDirName) throws PhrescoException {
    	try {
            PomProcessor processor = getPomProcessor(appDirName);
    		Modules pomModule = processor.getPomModule();
    		if (pomModule != null) {
    			return pomModule.getModule();
    		}
    	} catch (PhrescoPomException e) {
    		 throw new PhrescoException(e);
    	}
    	
    	return null;
    }
	
	/**
	 * To get the war project modules of the given application
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static List<String> getWarProjectModules(String appDirName) throws PhrescoException {
    	try {
			List<String> projectModules = getProjectModules(appDirName);
			List<String> warModules = new ArrayList<String>(5);
			if (CollectionUtils.isNotEmpty(projectModules)) {
				for (String projectModule : projectModules) {
					PomProcessor processor = getPomProcessor(appDirName);
					String packaging = processor.getModel().getPackaging();
					if (StringUtils.isNotEmpty(packaging) && WAR.equalsIgnoreCase(packaging)) {
						warModules.add(projectModule);
					}
				}
			}
			return warModules;
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		}
    }
	
	/**
	 * To ge the unit test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getUnitTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_UNITTEST_DIR);
    }
	
	/**
	 * To get the functional test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getFunctionalTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
		return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_FUNCTEST_DIR);
	}
	
	/**
	 * To get the component test directory
	 * @param appinfo
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getComponentTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_COMPONENTTEST_DIR);
    }
	
	/**
	 * To get the load test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getLoadTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
    	return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_LOADTEST_DIR);
    }
	
	
	/**
	 * To get the load test report directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getLoadTestReportDir(String appDirName) throws PhrescoException, PhrescoPomException {
    	return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_LOADTEST_RPT_DIR);
    }
	
	/**
	 * To get the load test result file extension
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getLoadResultFileExtension(String appDirName) throws PhrescoException, PhrescoPomException {
			return getPomProcessor(appDirName).getProperty(Constants.POM_PROP_KEY_LOADTEST_RESULT_EXTENSION);
	}
	
	/**
	 * To get the performance test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getPerformanceTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_PERFORMANCETEST_DIR);
    }
	
	/**
	 * To get the performance test result directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getPerformanceTestReportDir(String appDirName) throws PhrescoException, PhrescoPomException {
			return getPomProcessor(appDirName).getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_RPT_DIR);
	}
	
	/**
	 * To get the performance test result file extension
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getPerformanceResultFileExtension(String appDirName) throws PhrescoException, PhrescoPomException {
			return getPomProcessor(appDirName).getProperty(Constants.POM_PROP_KEY_PERFORMANCETEST_RESULT_EXTENSION);
	}
	/**
	 * To get the manual test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 * @throws PhrescoPomException
	 */
	public static String getManualTestDir(String appDirName) throws PhrescoException, PhrescoPomException {
        return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_MANUALTEST_RPT_DIR);
    }
	
	/**
	 * To get the build test directory
	 * @param appDirName
	 * @return
	 * @throws PhrescoException
	 */
	public static String getBuildDir(String appDirName) throws PhrescoException {
		StringBuilder builder = new StringBuilder(getApplicationHome(appDirName))
		.append(File.separator)
		.append(BUILD_DIR);
        return builder.toString();
    }
	
	public static String getFunctionalTestFramework(String appDirName) throws PhrescoException, PhrescoPomException {
		return getPomProcessor(appDirName).getProperty(POM_PROP_KEY_FUNCTEST_SELENIUM_TOOL);
	}
	
	public static List<ApplicationInfo> getAppInfos(String customerId, String projectId) throws PhrescoException {
		try {
			ProjectManager projectManager = PhrescoFrameworkFactory.getProjectManager();
			ProjectInfo projectInfo = projectManager.getProject(projectId, customerId);
			if(projectInfo != null) {
				return projectInfo.getAppInfos();
			}
		} catch (PhrescoException e) {
			throw new PhrescoException(e);
		}	
		return new ArrayList<ApplicationInfo>();
	}
	
	public static String getConfigFileDir(String appDirName) {
		StringBuilder builder = new StringBuilder();
		builder.append(Utility.getProjectHome())
		.append(appDirName)
		.append(File.separatorChar)
		.append(Constants.DOT_PHRESCO_FOLDER)
		.append(File.separatorChar)
		.append(Constants.CONFIGURATION_INFO_FILE);
		return builder.toString();
	}
	
	 //get server Url for sonar
    public static String getSonarURL(HttpServletRequest request) throws PhrescoException {
    	FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
    	String serverUrl = getSonarHomeURL(request);
	    String sonarReportPath = frameworkConfig.getSonarReportPath();
	    String[] sonar = sonarReportPath.split("/");
	    serverUrl = serverUrl.concat(FORWARD_SLASH + sonar[1]);
	    return serverUrl;
    }
    
    //get server Url for sonar
    public static String getSonarHomeURL(HttpServletRequest request) throws PhrescoException {
    	FrameworkConfiguration frameworkConfig = PhrescoFrameworkFactory.getFrameworkConfig();
    	String serverUrl = "";
    	
	    if (StringUtils.isNotEmpty(frameworkConfig.getSonarUrl())) {
	    	serverUrl = frameworkConfig.getSonarUrl();
	    } else {
	    	serverUrl = request.getRequestURL().toString();
	    	StringBuilder tobeRemoved = new StringBuilder();
	    	tobeRemoved.append(request.getContextPath());
	    	tobeRemoved.append(request.getServletPath());
	    	tobeRemoved.append(request.getPathInfo());

	    	Pattern pattern = Pattern.compile(tobeRemoved.toString());
	    	Matcher matcher = pattern.matcher(serverUrl);
	    	serverUrl = matcher.replaceAll("");
	    }
	    return serverUrl;
    }
	
	/**
	 * To the phresco plugin info file path based on the goal
	 * @param goal
	 * @return
	 * @throws PhrescoException 
	 */
	public String getPhrescoPluginInfoFilePath(String goal, String phase, String appDirName) throws PhrescoException {
		StringBuilder sb = new StringBuilder(getApplicationHome(appDirName));
		sb.append(File.separator);
		sb.append(FOLDER_DOT_PHRESCO);
		sb.append(File.separator);
		sb.append(PHRESCO_HYPEN);
		// when phase is CI, it have to take ci info file for update dependency
		if (PHASE_CI.equals(phase)) {
			sb.append(phase);
		} else if (StringUtils.isNotEmpty(goal) && goal.contains(FUNCTIONAL)) {
			sb.append(PHASE_FUNCTIONAL_TEST);
		} else if (PHASE_RUNGAINST_SRC_START.equals(goal)|| PHASE_RUNGAINST_SRC_STOP.equals(goal) ) {
			sb.append(PHASE_RUNAGAINST_SOURCE);
		} else {
			sb.append(goal);
		}
		sb.append(INFO_XML);

		return sb.toString();
	}
}