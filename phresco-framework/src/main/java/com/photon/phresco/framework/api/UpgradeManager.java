/**
 * Phresco Framework
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
package com.photon.phresco.framework.api;

import com.photon.phresco.commons.model.VersionInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.service.client.api.ServiceManager;

public interface UpgradeManager {

	  VersionInfo checkForUpdate(ServiceManager serviceManager, String versionNo) throws PhrescoException;

	  void doUpdate(ServiceManager serviceManager, String newVersion, String customerId) throws PhrescoException;
	 
	  String getCurrentVersion() throws PhrescoException;

}