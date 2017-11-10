/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import biz.rapidfire.core.plugin.AbstractExtendedUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class RapidFireCorePlugin extends AbstractExtendedUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.rapidfire.core"; //$NON-NLS-1$

    // Minimal required version of Rapid Fire library
    private static final String MIN_SERVER_VERSION = "4.5.0"; //$NON-NLS-1$

    // The shared instance
    private static RapidFireCorePlugin plugin;

    public static final String IMAGE_ERROR = "error.gif"; //$NON-NLS-1$
    public static final String IMAGE_REFRESH = "refresh.gif"; //$NON-NLS-1$
    public static final String IMAGE_AUTO_REFRESH_OFF = "auto_refresh_off.gif"; //$NON-NLS-1$

    public static final String COLOR_PROGRESS_BAR_FOREGROUND = "COLOR_PROGRESS_BAR_FOREGROUND"; //$NON-NLS-1$
    public static final String COLOR_PROGRESS_BAR_BACKGROUND = "COLOR_PROGRESS_BAR_BACKGROUND"; //$NON-NLS-1$

    /**
     * The constructor
     */
    public RapidFireCorePlugin() {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        // JdbcConnectionManager.getInstance().destroy();
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static RapidFireCorePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF".
     * 
     * @return Version of the plugin.
     */
    public String getVersion() {
        String version = (String)getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        if (version == null) {
            version = "0.0.0";
        }
        return version;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF" formatted as "vvrrmm".
     * 
     * @return Version of the plugin.
     */
    public String getMinServerVersion() {
        return MIN_SERVER_VERSION;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);

        reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
        reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
        reg.put(IMAGE_AUTO_REFRESH_OFF, getImageDescriptor(IMAGE_AUTO_REFRESH_OFF));
    }

    @Override
    protected void initializeColorRegistry(ColorRegistry reg) {
        super.initializeColorRegistry(reg);

        reg.put(COLOR_PROGRESS_BAR_FOREGROUND, new RGB(100, 230, 80));
        reg.put(COLOR_PROGRESS_BAR_BACKGROUND, new RGB(220, 220, 220));
    }

    /**
     * Convenience method to log error messages to the application log.
     * 
     * @param message Message
     * @param e The exception that has produced the error
     */
    public static void logError(String message, Throwable e) {
        if (plugin == null) {
            System.err.println(message);
            if (e != null) {
                e.printStackTrace();
            }
            return;
        }
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
    }

}
