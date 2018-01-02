/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.handlers.library;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;

import biz.rapidfire.core.dialogs.maintenance.library.LibraryMaintenanceDialog;
import biz.rapidfire.core.maintenance.MaintenanceMode;
import biz.rapidfire.core.maintenance.library.LibraryValues;
import biz.rapidfire.core.maintenance.library.shared.LibraryAction;
import biz.rapidfire.core.model.IRapidFireLibraryResource;
import biz.rapidfire.rsebase.helpers.SystemConnectionHelper;

public class NewLibraryHandler extends AbstractLibraryMaintenanceHandler implements IHandler {

    public NewLibraryHandler() {
        super(MaintenanceMode.CREATE, LibraryAction.CREATE);
    }

    @Override
    protected void performAction(IRapidFireLibraryResource library) throws Exception {

        LibraryValues values = getManager().getValues();

        LibraryMaintenanceDialog dialog = LibraryMaintenanceDialog.getCreateDialog(getShell(), getManager());
        dialog.setValue(values);

        if (dialog.open() == Dialog.OK) {
            getManager().book();

            values = dialog.getValue();
            IRapidFireLibraryResource newLibrary = library.getParentSubSystem().getLibrary(library.getParentResource(), values.getKey().getLibrary(),
                getShell());
            newLibrary.setParentNode(library.getParentNode());
            if (newLibrary != null) {
                SystemConnectionHelper.refreshUICreated(newLibrary.getParentSubSystem(), newLibrary, newLibrary.getParentNode());
            }
        }
    }
}
