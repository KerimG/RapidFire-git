/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.maintenance.file.shared;

import biz.rapidfire.core.RapidFireCorePlugin;
import biz.rapidfire.core.helpers.ExceptionHelper;
import biz.rapidfire.core.maintenance.IResourceKey;
import biz.rapidfire.core.maintenance.job.shared.JobKey;

public class FileKey implements IResourceKey {

    private JobKey jobKey;
    private int position;

    public static FileKey createNew(JobKey jobKey) {

        FileKey key = new FileKey(jobKey, 0);

        return key;
    }

    public FileKey(JobKey jobKey, int position) {

        this.jobKey = jobKey;
        this.position = position;
    }

    public String getJobName() {
        return jobKey.getJobName();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append(jobKey.toString());
        buffer.append(DELIMITER);
        buffer.append(getPosition());

        return buffer.toString();
    }

    @Override
    public Object clone() {
        try {

            FileKey fileKey = (FileKey)super.clone();
            fileKey.jobKey = (JobKey)jobKey.clone();

            return fileKey;

        } catch (CloneNotSupportedException e) {
            RapidFireCorePlugin.logError("*** Clone not supported. ***", e); //$NON-NLS-1$
            throw new biz.rapidfire.core.exceptions.CloneNotSupportedException(ExceptionHelper.getLocalizedMessage(e), e);
        }
    }
}
