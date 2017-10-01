/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.rse.model;

import org.eclipse.core.runtime.IAdaptable;

import biz.rapidfire.core.model.IJob;
import biz.rapidfire.core.model.JobName;
import biz.rapidfire.core.model.Phase;
import biz.rapidfire.core.model.Status;

import com.ibm.as400.access.QSYSObjectPathName;

public class Job extends AbstractModelObject implements IJob, IAdaptable {

    private String name;
    private String description;
    private boolean doCreateEnvironment;
    private QSYSObjectPathName jobQueue;
    private Status status;
    private Phase phase;
    private boolean isError;
    private String errorText;
    private JobName batchJob;
    private boolean isStopApplyChanges;
    private String cmoneFormNumber;

    public Job(String name, String description, boolean doCreateEnvironment, QSYSObjectPathName jobQueue) {
        this.name = name;
        this.description = description;
        this.doCreateEnvironment = doCreateEnvironment;
        this.jobQueue = jobQueue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDoCreateEnvironment() {
        return doCreateEnvironment;
    }

    public void setDoCreateEnvironment(boolean doCreateEnvironment) {
        this.doCreateEnvironment = doCreateEnvironment;
    }

    public QSYSObjectPathName getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(QSYSObjectPathName jobQueue) {
        this.jobQueue = jobQueue;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public boolean isStopApplyChanges() {
        return isStopApplyChanges;
    }

    public void setStopApplyChanges(boolean isStopApplyChanges) {
        this.isStopApplyChanges = isStopApplyChanges;
    }

    public String getCmoneFormNumber() {
        return cmoneFormNumber;
    }

    public void setCmoneFormNumber(String cmoneFormNumber) {
        this.cmoneFormNumber = cmoneFormNumber;
    }

    public void setBatchJob(JobName job) {
        this.batchJob = job;
    }

    public JobName getBatchJob() {
        return batchJob;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.AbstractSystemViewAdapter#getText(java.lang.Object
     * )
     */
    public String getText(Object element) {

        if (element == null) {
            return null;
        }

        return ((Job)element).getName();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.AbstractSystemViewAdapter#getAbsoluteName(java
     * .lang.Object)
     */
    public String getAbsoluteName(Object object) {
        Job job = (Job)object;
        return "Team_" + job.getName();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.AbstractSystemViewAdapter#getType(java.lang.Object
     * )
     */
    public String getType(Object element) {
        return "property.job_resource.type";
    }

    // --------------------------------------
    // ISystemRemoteElementAdapter methods...
    // --------------------------------------

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.ISystemRemoteElementAdapter#getRemoteTypeCategory
     * (java.lang.Object)
     */
    public String getRemoteTypeCategory(Object element) {
        return "developers"; // Course grained. Same for all our remote
                             // resources.
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.ISystemRemoteElementAdapter#getRemoteType(java
     * .lang.Object)
     */
    public String getRemoteType(Object element) {
        return "team"; // Fine grained. Unique to this resource type.
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.rse.ui.view.ISystemRemoteElementAdapter#refreshRemoteObject
     * (java.lang.Object, java.lang.Object)
     */
    public boolean refreshRemoteObject(Object oldElement, Object newElement) {
        return false; // If developer objects held references to their team
                      // names, we'd have to return true
    }

    public Object getAdapter(Class arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
