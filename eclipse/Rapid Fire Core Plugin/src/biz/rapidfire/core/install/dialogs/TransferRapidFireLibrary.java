/*******************************************************************************
 * Copyright (c) 2017-2017 Rapid Fire Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.rapidfire.core.install.dialogs;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import biz.rapidfire.core.Messages;
import biz.rapidfire.core.RapidFireCorePlugin;
import biz.rapidfire.core.helpers.ClipboardHelper;
import biz.rapidfire.core.helpers.RapidFireHelper;
import biz.rapidfire.core.preferences.Preferences;
import biz.rapidfire.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.FTP;
import com.ibm.as400.access.QSYSObjectPathName;

public class TransferRapidFireLibrary extends Shell {

    private AS400 as400;
    private CommandCall commandCall;
    private Table tableStatus;
    private Button buttonStart;
    private Button buttonClose;
    private String rapidFireLibrary;
    private int ftpPort;
    private String hostName;

    public TransferRapidFireLibrary(Display display, int style, String rapidFireLibrary, String aHostName, int aFtpPort) {
        super(display, style);

        setImage(RapidFireCorePlugin.getDefault().getImageRegistry().get(RapidFireCorePlugin.IMAGE_TRANSFER_LIBRARY));

        this.rapidFireLibrary = rapidFireLibrary;
        this.hostName = aHostName;
        setFtpPort(aFtpPort);

        createContents();
    }

    private void setFtpPort(int aFtpPort) {
        if (aFtpPort <= 0) {
            ftpPort = Preferences.getInstance().getDefaultFtpPortNumber();
        } else {
            ftpPort = aFtpPort;
        }
    }

    protected void createContents() {

        GridLayout gl_shell = new GridLayout();
        gl_shell.marginTop = 10;
        gl_shell.verticalSpacing = 10;
        setLayout(gl_shell);

        setText(Messages.Dialog_Title_Transfer_Rapid_Fire_library);
        setSize(500, 330);

        buttonStart = WidgetFactory.createPushButton(this);
        buttonStart.addSelectionListener(new TransferLibrarySelectionAdapter());
        buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonStart.setText(Messages.ActionLabel_Start_Transfer);

        tableStatus = new Table(this, SWT.BORDER | SWT.MULTI);
        final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableStatus.setLayoutData(gd_tableStatus);

        final TableColumn columnStatus = new TableColumn(tableStatus, SWT.NONE);
        columnStatus.setWidth(getSize().x);

        tableStatus.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event) {
                Table table = (Table)event.getSource();
                if (table.getClientArea().width > 0) {
                    // Resize the column to the width of the table
                    columnStatus.setWidth(table.getClientArea().width);
                }
            }
        });

        tableStatus.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
            }

            public void keyPressed(KeyEvent event) {
                if ((event.stateMask & SWT.CTRL) != SWT.CTRL) {
                    return;
                }
                if (event.keyCode == 'a') {
                    tableStatus.selectAll();
                }
                if (event.keyCode == 'c') {
                    ClipboardHelper.setTableItemsText(tableStatus.getSelection());
                }
            }
        });

        Menu menuTableStatusContextMenu = new Menu(tableStatus);
        menuTableStatusContextMenu.addMenuListener(new TableContextMenu(tableStatus));
        tableStatus.setMenu(menuTableStatusContextMenu);

        buttonClose = WidgetFactory.createPushButton(this);
        buttonClose.setText(Messages.ActionLabel_Close);
        buttonClose.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }
        });

        GridData gd_buttonClose = new GridData(GridData.CENTER, GridData.CENTER, true, false);
        buttonClose.setLayoutData(gd_buttonClose);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setStatus(String status) {
        TableItem itemStatus = new TableItem(tableStatus, SWT.BORDER);
        itemStatus.setText(status);
        tableStatus.update();
    }

    private boolean checkLibraryPrecondition(String libraryName) {

        while (libraryExists(libraryName)) {
            if (!MessageDialog.openQuestion(
                getShell(),
                Messages.DialogTitle_Delete_Object,
                Messages.bind(Messages.Library_A_does_already_exist, libraryName) + "\n\n"
                    + Messages.bind(Messages.Do_you_want_to_delete_library_A, libraryName))) {
                return false;
            }
            setStatus(Messages.bind(Messages.Deleting_library_A, libraryName));
            deleteLibrary(libraryName, true);
        }

        return true;
    }

    private boolean libraryExists(String libraryName) {

        if (!RapidFireHelper.checkLibrary(as400, libraryName)) {
            return false;
        }

        return true;
    }

    private boolean deleteLibrary(String libraryName, boolean logErrors) {

        if (!executeCommand("DLTLIB LIB(" + libraryName + ")", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean checkSaveFilePrecondition(String workLibrary, String saveFileName) {

        while (saveFileExists(workLibrary, saveFileName)) {
            if (!MessageDialog.openQuestion(
                getShell(),
                Messages.DialogTitle_Delete_Object,
                Messages.bind(Messages.File_B_in_library_A_does_already_exist, new String[] { workLibrary, saveFileName }) + "\n\n"
                    + Messages.bind(Messages.Do_you_want_to_delete_object_A_B_type_C, new String[] { workLibrary, saveFileName, "*FILE" }))) {
                return false;
            }
            setStatus(Messages.bind(Messages.Deleting_object_A_B_of_type_C, new String[] { workLibrary, saveFileName, "*FILE" }));
            deleteSaveFile(workLibrary, saveFileName, true);
        }

        return true;
    }

    private boolean saveFileExists(String workLibrary, String saveFileName) {

        if (!RapidFireHelper.checkObject(as400, new QSYSObjectPathName(workLibrary, saveFileName, "*FILE"))) {
            return false;
        }

        return true;
    }

    private boolean deleteSaveFile(String workLibrary, String saveFileName, boolean logErrors) {

        if (!executeCommand("DLTF FILE(" + workLibrary + "/" + saveFileName + ")", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean createSaveFile(String workLibrary, String saveFileName, boolean logErrors) {

        if (!executeCommand("CRTSAVF FILE(" + workLibrary + "/" + saveFileName + ") TEXT('RAPIDFIRE')", logErrors).equals("")) {
            return false;
        }

        return true;
    }

    private boolean restoreLibrary(String workLibrary, String saveFileName, String libraryName) {

        String cpfMsg = executeCommand("RSTLIB SAVLIB(RAPIDFIRE) DEV(*SAVF) SAVF(" + workLibrary + "/" + saveFileName + ") RSTLIB(" + libraryName
            + ")", true);
        if (!cpfMsg.equals("")) {
            return false;
        }

        return true;
    }

    private String executeCommand(String command, boolean logError) {
        try {
            commandCall.run(command);
            AS400Message[] messageList = commandCall.getMessageList();
            if (messageList.length > 0) {
                AS400Message escapeMessage = null;
                for (int idx = 0; idx < messageList.length; idx++) {
                    if (messageList[idx].getType() == AS400Message.ESCAPE) {
                        escapeMessage = messageList[idx];
                    }
                }
                if (escapeMessage != null) {
                    if (logError) {
                        for (int idx = 0; idx < messageList.length; idx++) {
                            setStatus(messageList[idx].getID() + ": " + messageList[idx].getText());
                        }
                    }
                    return escapeMessage.getID();
                }
            }
            return "";
        } catch (Exception e) {
            return "CPF0000";
        }
    }

    public boolean connect() {
        as400 = new AS400(hostName, "RADDATZ2", "TOOLS400");
        buttonStart.setEnabled(false);
        buttonClose.setEnabled(false);
        SignOnDialog signOnDialog = new SignOnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), hostName);
        if (signOnDialog.open() == Dialog.OK) {
            as400 = signOnDialog.getAS400();
            if (as400 != null) {
                try {
                    as400.connectService(AS400.COMMAND);
                    commandCall = new CommandCall(as400);
                    if (commandCall != null) {
                        hostName = as400.getSystemName();
                        setStatus(Messages.bind(Messages.About_to_transfer_library_A_to_host_B_using_port_C, new String[] { rapidFireLibrary.trim(),
                            hostName, Integer.toString(ftpPort) }));
                        buttonStart.setEnabled(true);
                        buttonClose.setEnabled(true);
                        return true;
                    }
                } catch (Throwable e) {
                    RapidFireCorePlugin.logError("Failed to connect to host: " + hostName, e);
                }
            }
        }
        return false;
    }

    private class TransferLibrarySelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(final SelectionEvent event) {

            buttonStart.setEnabled(false);
            buttonClose.setEnabled(false);
            boolean successfullyTransfered = false;

            String workLibrary = "QGPL";
            String saveFileName = rapidFireLibrary;

            setStatus(Messages.bind(Messages.Checking_library_A_for_existence, rapidFireLibrary));
            if (!checkLibraryPrecondition(rapidFireLibrary)) {
                setStatus("!!!   " + Messages.bind(Messages.Library_A_does_already_exist, rapidFireLibrary) + "   !!!");
            } else {
                setStatus(Messages.bind(Messages.Checking_file_B_in_library_A_for_existence, new String[] { workLibrary, saveFileName }));
                if (!checkSaveFilePrecondition(workLibrary, saveFileName)) {
                    setStatus("!!!   " + Messages.bind(Messages.File_B_in_library_A_does_already_exist, new String[] { workLibrary, saveFileName })
                        + "   !!!");
                } else {

                    setStatus(Messages.bind(Messages.Creating_save_file_B_in_library_A, new String[] { workLibrary, saveFileName }));
                    if (!createSaveFile(workLibrary, saveFileName, true)) {
                        setStatus("!!!   "
                            + Messages.bind(Messages.Could_not_create_save_file_B_in_library_A, new String[] { workLibrary, saveFileName })
                            + "   !!!");
                    } else {

                        try {

                            setStatus(Messages.Sending_save_file_to_host);
                            setStatus(Messages.bind(Messages.Using_Ftp_port_number, new Integer(ftpPort)));
                            AS400FTP client = new AS400FTP(as400);

                            URL fileUrl = FileLocator.toFileURL(RapidFireCorePlugin.getInstallURL());
                            File file = new File(fileUrl.getPath() + "Server" + File.separator + "RAPIDFIRE.SAVF");
                            client.setPort(ftpPort);
                            client.setDataTransferType(FTP.BINARY);
                            if (client.connect()) {
                                client.put(file, "/QSYS.LIB/" + workLibrary + ".LIB/" + saveFileName + ".FILE");
                                client.disconnect();
                            }

                            setStatus(Messages.bind(Messages.Restoring_library_A, rapidFireLibrary));
                            if (!restoreLibrary(workLibrary, saveFileName, rapidFireLibrary)) {
                                setStatus("!!!   " + Messages.bind(Messages.Could_not_restore_library_A, rapidFireLibrary) + "   !!!");
                            } else {
                                setStatus("!!!   " + Messages.bind(Messages.Library_A_successfull_transfered, rapidFireLibrary) + "   !!!");
                                successfullyTransfered = true;
                            }

                        } catch (Throwable e) {
                            RapidFireCorePlugin.logError(Messages.Could_not_send_save_file_to_host, e);

                            setStatus("!!!   " + Messages.Could_not_send_save_file_to_host + "   !!!");
                            setStatus(e.getLocalizedMessage());
                        } finally {

                            setStatus(Messages.bind(Messages.Deleting_object_A_B_of_type_C, new String[] { workLibrary, saveFileName, "*FILE" }));
                            deleteSaveFile(workLibrary, saveFileName, true);
                        }

                    }
                }
            }

            if (successfullyTransfered) {
                buttonStart.setEnabled(false);
                buttonClose.setEnabled(true);
                buttonClose.setFocus();
            } else {
                buttonStart.setEnabled(true);
                buttonClose.setEnabled(true);
                buttonClose.setFocus();
            }
        }
    }

    /**
     * Class that implements the context menu for the table rows.
     */
    private class TableContextMenu extends MenuAdapter {

        private Table table;
        private MenuItem menuItemCopySelected;

        public TableContextMenu(Table table) {
            this.table = table;
        }

        @Override
        public void menuShown(MenuEvent event) {
            destroyMenuItems();
            createMenuItems();
        }

        private Menu getMenu() {
            return table.getMenu();
        }

        private void destroyMenuItems() {
            if (!((menuItemCopySelected == null) || (menuItemCopySelected.isDisposed()))) {
                menuItemCopySelected.dispose();
            }
        }

        private void createMenuItems() {

            createMenuItemCopySelected();
        }

        private void createMenuItemCopySelected() {

            menuItemCopySelected = new MenuItem(getMenu(), SWT.NONE);
            menuItemCopySelected.setText(Messages.ActionLabel_Copy);
            menuItemCopySelected.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ClipboardHelper.setTableItemsText(table.getItems());
                }
            });
        }
    }
}