/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.universalavenue.ticrosswalk;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import org.xwalk.core.XWalkPreferences;
import org.chromium.base.CommandLine;

// , propertyAccessors = {"backButtonDestroys"}
@Kroll.module(name="TiCrosswalk", id="com.universalavenue.ticrosswalk")

public class TiCrosswalkModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "TiCrosswalkModule";
	private static final boolean DBG = TiConfig.LOGD;
	private static final String INIT_SWITCHES[] = { "Xwalk", "--disable-pull-to-refresh-effect"};

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public TiCrosswalkModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		if (!CommandLine.isInitialized()) {
			CommandLine.init(INIT_SWITCHES);
		} else {
			CommandLine.getInstance().appendSwitch("--disable-pull-to-refresh-effect");
		}

		XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, false);
		XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
		XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
		XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
		XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
	}
}

