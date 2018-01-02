/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.subsystem.adapters;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.rapidfire.core.RapidFireCorePlugin;
import biz.rapidfire.core.dialogs.MessageDialogAsync;
import biz.rapidfire.core.model.IRapidFireAreaResource;
import biz.rapidfire.core.model.IRapidFireFileResource;
import biz.rapidfire.rse.Messages;
import biz.rapidfire.rse.RapidFireRSEPlugin;
import biz.rapidfire.rse.subsystem.actions.area.NewAreasNodePopupMenuAction;
import biz.rapidfire.rse.subsystem.resources.AreasNode;

import com.ibm.etools.systems.core.ui.SystemMenuManager;

public class AreasNodeAdapter extends AbstractNodeAdapter<AreasNode> {

    @Override
    public final boolean hasChildren(Object element) {
        return true;
    }

    @Override
    public String getText(Object element) {
        return Messages.NodeText_Areas;
    }

    @Override
    protected String getAbsoluteNamePrefix() {
        return "node.areas."; //$NON-NLS-1$
    }

    @Override
    public void addActions(SystemMenuManager menuManager, IStructuredSelection selection, Shell shell, String menuGroup) {
        menuManager.add(null, new NewAreasNodePopupMenuAction(shell));
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object element) {
        return RapidFireRSEPlugin.getDefault().getImageRegistry().getDescriptor(RapidFireRSEPlugin.IMAGE_AREA);
    }

    @Override
    public Object[] getChildren(Object element) {

        try {

            AreasNode areasNode = (AreasNode)element;
            IRapidFireFileResource fileResource = areasNode.getParentResource();

            IRapidFireAreaResource[] areas = fileResource.getParentSubSystem().getAreas(fileResource, getShell());
            for (IRapidFireAreaResource area : areas) {
                area.setParentNode(areasNode);
            }

            Arrays.sort(areas);

            return areas;

        } catch (Exception e) {
            RapidFireCorePlugin.logError("*** Could resolve filter string and load areas ***", e); //$NON-NLS-1$
            MessageDialogAsync.displayError(e.getLocalizedMessage());
        }

        return new Object[0];
    }

    @Override
    public String getType(Object element) {
        return Messages.NodeType_Areas;
    }

    @Override
    public String getRemoteType(Object element) {
        return "node.areas"; //$NON-NLS-1$
    }

}