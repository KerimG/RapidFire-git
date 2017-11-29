/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.preferencepages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.rapidfire.core.Messages;
import biz.rapidfire.core.RapidFireCorePlugin;
import biz.rapidfire.core.handlers.install.TransferRapidFireLibraryHandler;
import biz.rapidfire.core.helpers.ExceptionHelper;
import biz.rapidfire.core.helpers.IntHelper;
import biz.rapidfire.core.helpers.RapidFireHelper;
import biz.rapidfire.core.helpers.StringHelper;
import biz.rapidfire.core.model.dao.JDBCConnectionManager;
import biz.rapidfire.core.preferences.Preferences;
import biz.rapidfire.core.swt.widgets.WidgetFactory;
import biz.rapidfire.core.validators.Validator;

import com.ibm.as400.access.AS400;

public class RapidFireLibrary extends PreferencePage implements IWorkbenchPreferencePage {

    private String rapidFireLibrary;
    private Validator validatorLibrary;

    private Text textHostName;
    private Text textFtpPortNumber;
    private Text textProductLibrary;
    private Label textProductLibraryVersion;
    private Button buttonUpdateProductLibraryVersion;
    private Button buttonTransfer;

    private boolean updateProductLibraryVersion;

    public RapidFireLibrary() {
        super();

        setPreferenceStore(RapidFireCorePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();

        this.updateProductLibraryVersion = false;
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        container.setLayout(gridLayout);

        Label labelHostName = new Label(container, SWT.NONE);
        labelHostName.setLayoutData(createLabelLayoutData());
        labelHostName.setText(Messages.Label_Host_name_colon);
        labelHostName.setToolTipText(Messages.Tooltip_Host_name);

        textHostName = WidgetFactory.createText(container);
        textHostName.setToolTipText(Messages.Tooltip_Host_name);
        textHostName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateProductLibraryVersion();
                return;
            }
        });
        textHostName.setLayoutData(createTextLayoutData());

        Label labelFtpPortNumber = new Label(container, SWT.NONE);
        labelFtpPortNumber.setLayoutData(createLabelLayoutData());
        labelFtpPortNumber.setText(Messages.Label_FTP_port_number_colon);
        labelFtpPortNumber.setToolTipText(Messages.Label_FTP_port_number_colon);

        textFtpPortNumber = WidgetFactory.createIntegerText(container);
        textFtpPortNumber.setToolTipText(Messages.Label_FTP_port_number_colon);
        textFtpPortNumber.setTextLimit(5);
        textFtpPortNumber.setLayoutData(createTextLayoutData());

        Label labelProductLibrary = new Label(container, SWT.NONE);
        labelProductLibrary.setLayoutData(createLabelLayoutData());
        labelProductLibrary.setText(Messages.Label_Rapid_Fire_library_colon);
        labelProductLibrary.setToolTipText(Messages.Tooltip_Rapid_Fire_library);

        textProductLibrary = WidgetFactory.createUpperCaseText(container);
        textProductLibrary.setToolTipText(Messages.Tooltip_Rapid_Fire_library);
        textProductLibrary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                rapidFireLibrary = textProductLibrary.getText().toUpperCase().trim();
                if (rapidFireLibrary.equals("") || !validatorLibrary.validate(rapidFireLibrary)) {
                    setErrorMessage(Messages.The_name_of_the_Rapid_Fire_library_is_invalid);
                    setValid(false);
                } else {
                    setErrorMessage(null);
                    setValid(true);
                }
            }
        });
        textProductLibrary.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                updateProductLibraryVersion();
                return;
            }
        });
        textProductLibrary.setLayoutData(createTextLayoutData());
        textProductLibrary.setTextLimit(10);

        validatorLibrary = Validator.getLibraryNameInstance();

        Label labelProductLibraryVersion = new Label(container, SWT.NONE);
        labelProductLibraryVersion.setLayoutData(createLabelLayoutData());
        labelProductLibraryVersion.setText(Messages.Label_Version_colon);
        labelProductLibraryVersion.setToolTipText(Messages.Tooltip_Version);

        textProductLibraryVersion = new Label(container, SWT.NONE);
        textProductLibraryVersion.setToolTipText(Messages.Tooltip_Version);
        textProductLibraryVersion.setLayoutData(createTextLayoutData(1));

        buttonUpdateProductLibraryVersion = WidgetFactory.createPushButton(container);
        buttonUpdateProductLibraryVersion.setImage(RapidFireCorePlugin.getDefault().getImageRegistry().get(RapidFireCorePlugin.IMAGE_REFRESH));
        buttonUpdateProductLibraryVersion.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                updateProductLibraryVersion = true;
                updateProductLibraryVersion();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        buttonTransfer = WidgetFactory.createPushButton(container);
        buttonTransfer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String hostName = textHostName.getText();
                int ftpPort = IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber());
                TransferRapidFireLibraryHandler handler = new TransferRapidFireLibraryHandler(hostName, ftpPort, rapidFireLibrary);
                try {
                    handler.execute(null);
                } catch (Throwable e) {
                    RapidFireCorePlugin.logError("Failed to transfer the Rapid Fire library.", e); //$NON-NLS-1$
                }
            }
        });
        buttonTransfer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        buttonTransfer.setText(Messages.ActionLabel_Transfer_Rapid_Fire_library);
        buttonTransfer.setToolTipText(Messages.ActionTooltip_Transfer_Rapid_Fire_library);

        setScreenToValues();

        return container;
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    private void setControlEnablement() {

        if (updateProductLibraryVersion) {
            buttonUpdateProductLibraryVersion.setEnabled(false);
        } else {
            buttonUpdateProductLibraryVersion.setEnabled(true);
        }
    }

    protected void setStoreToValues() {

        Preferences.getInstance().setRapidFireLibrary(rapidFireLibrary);
        Preferences.getInstance().setHostName(textHostName.getText());
        Preferences.getInstance().setFtpPortNumber(
            IntHelper.tryParseInt(textFtpPortNumber.getText(), Preferences.getInstance().getDefaultFtpPortNumber()));

    }

    protected void setScreenToValues() {

        rapidFireLibrary = Preferences.getInstance().getRapidFireLibrary();
        textHostName.setText(Preferences.getInstance().getHostName());
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getFtpPortNumber()));

        setScreenValues();
    }

    protected void setScreenToDefaultValues() {

        rapidFireLibrary = Preferences.getInstance().getDefaultRapidFireLibrary();
        textHostName.setText(Preferences.getInstance().getDefaultHostName());
        textFtpPortNumber.setText(Integer.toString(Preferences.getInstance().getDefaultFtpPortNumber()));

        setScreenValues();
    }

    protected void setScreenValues() {

        textProductLibrary.setText(rapidFireLibrary);
    }

    public void init(IWorkbench workbench) {
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private GridData createTextLayoutData() {
        return createTextLayoutData(2);
    }

    private GridData createTextLayoutData(int horizontalSpan) {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, horizontalSpan, 1);
    }

    private void updateProductLibraryVersion() {
        String text = getProductLibraryVersion(textHostName.getText(), textProductLibrary.getText());
        if (text == null) {
            return;
        }

        textProductLibraryVersion.setText(text);

        setControlEnablement();
    }

    private String getProductLibraryVersion(String hostName, String library) {

        if (!updateProductLibraryVersion) {
            return ""; //$NON-NLS-1$
        }

        if (StringHelper.isNullOrEmpty(hostName) || StringHelper.isNullOrEmpty(library)) {
            updateProductLibraryVersion = false;
            return Messages.Enter_a_host_name;
        }

        String version;

        try {

            AS400 as400 = JDBCConnectionManager.getInstance().findSystem(hostName);
            if (as400 == null) {
                updateProductLibraryVersion = false;
                return Messages.bind(Messages.Host_A_not_found_in_configured_RSE_connections, hostName);
            }

            /*
             * From here on start updating library version automatically.
             */

            version = RapidFireHelper.getRapidFireLibraryVersion(as400, library);
            if (version == null) {
                return Messages.bindParameters(Messages.Rapid_Fire_version_information_not_found_in_library_A, library);
            }

            String buildDate = RapidFireHelper.getRapidFireLibraryBuildDate(as400, library);
            if (StringHelper.isNullOrEmpty(buildDate)) {
                return version;
            }

            DateFormat dateFormatter = Preferences.getInstance().getDateFormatter();
            DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
            version = version + " - " + dateFormatter.format(dateParser.parse(buildDate)); //$NON-NLS-1$
            return version;

        } catch (Throwable e) {
            return Messages.bindParameters(Messages.Could_not_rereieve_Rapid_Fire_version_information_due_to_backend_error_A,
                ExceptionHelper.getLocalizedMessage(e));
        }
    }
}