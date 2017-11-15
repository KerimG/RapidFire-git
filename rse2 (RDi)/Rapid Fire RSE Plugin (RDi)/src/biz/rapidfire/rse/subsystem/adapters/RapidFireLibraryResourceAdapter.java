/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.subsystem.adapters;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.ui.SystemMenuManager;
import org.eclipse.rse.ui.view.ISystemRemoteElementAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.rapidfire.core.model.IRapidFireLibraryResource;
import biz.rapidfire.core.subsystem.resources.RapidFireLibraryResource;
import biz.rapidfire.rse.Messages;
import biz.rapidfire.rse.RapidFireRSEPlugin;

public class RapidFireLibraryResourceAdapter extends AbstractResourceAdapter implements ISystemRemoteElementAdapter {

    private static final String DATA_LIBRARY = "DATA_LIBRARY"; //$NON-NLS-1$
    private static final String JOB = "JOB"; //$NON-NLS-1$
    private static final String LIBRARY = "LIBRARY"; //$NON-NLS-1$
    private static final String SHADOW_LIBRARY = "SHADOW_LIBRARY"; //$NON-NLS-1$

    public RapidFireLibraryResourceAdapter() {
        super();
    }

    @Override
    public void addActions(SystemMenuManager menu, IStructuredSelection selection, Shell shell, String menuGroup) {
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return RapidFireRSEPlugin.getDefault().getImageRegistry().getDescriptor(RapidFireRSEPlugin.IMAGE_LIBRARY);
    }

    @Override
    public boolean handleDoubleClick(Object object) {
        return false;
    }

    /**
     * Returns the name of the resource, e.g. for showing the name in the status
     * line.
     */
    @Override
    public String getText(Object element) {

        RapidFireLibraryResource resource = (RapidFireLibraryResource)element;

        return resource.getLibrary() + " -> " + resource.getShadowLibrary();
    }

    /**
     * Returns the absolute name of the resource for building hash values.
     */
    @Override
    public String getAbsoluteName(Object element) {

        RapidFireLibraryResource resource = (RapidFireLibraryResource)element;

        String name = "RapidFireLibrary." + resource.getDataLibrary() + "." + resource.getJob() + "." + resource.getLibrary(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        return name;
    }

    /**
     * Returns the type of the resource. Used for qualifying the name of the
     * resource, when displayed on the status line.
     */
    @Override
    public String getType(Object element) {
        return Messages.Resource_Rapid_Fire_Library;
    }

    @Override
    public String getRemoteType(Object element) {
        return "library";
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(IAdaptable element) {
        return false;
    }

    @Override
    public Object[] getChildren(IAdaptable element, IProgressMonitor progressMonitor) {

        return new Object[0];
    }

    @Override
    protected IPropertyDescriptor[] internalGetPropertyDescriptors() {

        PropertyDescriptor[] ourPDs = new PropertyDescriptor[4];
        ourPDs[0] = new PropertyDescriptor(DATA_LIBRARY, Messages.DataLibrary_name);
        ourPDs[0].setDescription(Messages.Tooltip_DataLibrary_name);
        ourPDs[1] = new PropertyDescriptor(JOB, Messages.Job_name);
        ourPDs[1].setDescription(Messages.Tooltip_Job_name);
        ourPDs[2] = new PropertyDescriptor(LIBRARY, Messages.Library_name);
        ourPDs[2].setDescription(Messages.Tooltip_Library_name);
        ourPDs[3] = new PropertyDescriptor(SHADOW_LIBRARY, Messages.ShadowLibrary_name);
        ourPDs[3].setDescription(Messages.Tooltip_ShadowLibrary_name);

        return ourPDs;
    }

    @Override
    public Object internalGetPropertyValue(Object propKey) {

        final IRapidFireLibraryResource resource = (IRapidFireLibraryResource)propertySourceInput;

        if (propKey.equals(DATA_LIBRARY)) {
            return resource.getDataLibrary();
        } else if (propKey.equals(JOB)) {
            return resource.getJob();
        } else if (propKey.equals(LIBRARY)) {
            return resource.getLibrary();
        } else if (propKey.equals(SHADOW_LIBRARY)) {
            return resource.getShadowLibrary();
        }
        return null;
    }
}