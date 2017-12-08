/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.handlers.job;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

import biz.rapidfire.core.Messages;
import biz.rapidfire.core.handlers.AbstractResourceMaintenanceHandler;
import biz.rapidfire.core.maintenance.MaintenanceMode;
import biz.rapidfire.core.maintenance.Result;
import biz.rapidfire.core.maintenance.job.JobManager;
import biz.rapidfire.core.maintenance.job.shared.JobAction;
import biz.rapidfire.core.maintenance.job.shared.JobKey;
import biz.rapidfire.core.model.IRapidFireJobResource;
import biz.rapidfire.core.model.dao.JDBCConnectionManager;

public abstract class AbstractJobMaintenanceHandler extends AbstractResourceMaintenanceHandler<IRapidFireJobResource, JobAction> {

    private JobManager manager;
    private JobAction jobAction;

    public AbstractJobMaintenanceHandler(MaintenanceMode mode, JobAction jobAction) {
        super(mode);

        this.jobAction = jobAction;
    }

    protected JobManager getManager() {
        return manager;
    }

    public JobAction getJobAction() {
        return jobAction;
    }

    @Override
    protected Object executeWithResource(IRapidFireJobResource resource) throws ExecutionException {

        try {

            IRapidFireJobResource job = (IRapidFireJobResource)resource;

            if (canExecuteAction(job, jobAction)) {
                Result result = initialize(job);
                if (result != null && result.isError()) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, result.getMessage());
                } else {
                    performAction(job);
                }
            }

        } catch (Throwable e) {
            logError(e);
        } finally {
            try {
                terminate();
            } catch (Throwable e) {
                logError(e);
            }
        }

        return null;
    }

    protected JobManager getOrCreateManager(IRapidFireJobResource job) throws Exception {

        if (manager == null) {
            String connectionName = job.getParentSubSystem().getConnectionName();
            String dataLibrary = job.getDataLibrary();
            boolean commitControl = isCommitControl();
            manager = new JobManager(JDBCConnectionManager.getInstance().getConnection(connectionName, dataLibrary, commitControl));
        }

        return manager;
    }

    @Override
    protected boolean isValidAction(IRapidFireJobResource job) throws Exception {
        return getOrCreateManager(job).isValidAction(job, jobAction);
    }

    @Override
    protected boolean isInstanceOf(Object object) {

        if (object instanceof IRapidFireJobResource) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean canExecuteAction(IRapidFireJobResource job, JobAction action) {

        String message = null;

        try {

            Result result = getOrCreateManager(job).checkAction(new JobKey(job.getName()), action);

            if (result.isSuccessfull()) {
                return true;
            } else {
                // TODO: fix message
                message = Messages.bindParameters(Messages.The_requested_operation_is_invalid_for_job_status_A, job.getStatus().label);
            }

        } catch (Exception e) {
            logError("*** Could not check job action. Failed creating the job manager ***", e); //$NON-NLS-1$
        }

        if (message != null) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, message);
        }

        return false;
    }

    protected Result initialize(IRapidFireJobResource job) throws Exception {

        manager.openFiles();

        Result result = manager.initialize(getMaintenanceMode(), new JobKey(job.getName()));

        return result;
    }

    protected abstract void performAction(IRapidFireJobResource job) throws Exception;

    protected void terminate() throws Exception {

        if (manager != null) {
            manager.closeFiles();
            manager = null;
        }
    }

    private void logError(Throwable e) {
        logError("*** Could not handle Rapid Fire job resource request ***", e); //$NON-NLS-1$
    }
}
