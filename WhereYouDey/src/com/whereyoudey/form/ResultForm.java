/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whereyoudey.form;

import com.whereyoudey.form.helper.FormDialogs;
import com.whereyoudey.form.component.Header;
import com.whereyoudey.utils.SortUtil;
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.whereyoudey.WhereYouDey;
import com.whereyoudey.form.component.SearchResultsContainer;
import com.whereyoudey.service.helper.Result;
import com.whereyoudey.utils.DialogUtil;
import com.whereyoudey.utils.UiUtil;

/**
 *
 * @author Vikram S
 */
public abstract class ResultForm implements ActionListener, Runnable {

    public static final String OPTION_NEXT = "Next";
    public static final String OPTION_PREV = "Prev";
    private static final String OPTION_BACK = "Back";
    private static final String OPTION_CALL = "Call";
    private static final String OPTION_EXIT = "Exit";
    private static final String OPTION_HELP = "Help";
    private static final String OPTION_HOME = "Home";
    private static final String OPTION_SELECT = "Select";
    protected static final String OPTION_FILTER_BY_VIDEOS = "Filter by videos";
    protected static final String OPTION_SORT_BY_AREA = "Sort by area";
    protected static final String OPTION_SORT_BY_CITY = "Sort by city";
    protected static final String OPTION_SORT_BY_RELEVANCE = "Sort by relevance";
    protected WhereYouDey midlet;
    protected ExtendedForm form;
    protected Result[] results;
    protected final SearchForm callingForm;
    private Header header;
    private int MAX_RESULTS = 10;
    protected SearchResultsContainer resultsList;
    private DetailsForm detailsForm;
    private Dialog waitDialog;
    final Command selectCmd = new Command(OPTION_SELECT);
    final Command callCmd = new Command(OPTION_CALL);
    private int pageNumber = 1;
    private boolean backTraversal = false;
    private boolean forwardTraversal = false;

    ;

    ResultForm(WhereYouDey midlet, Result[] results, SearchForm callingForm) {
        this.callingForm = callingForm;
        initVariables(midlet);
        initForm();
        addHeader();
        addResultsSection(results);
        addCommands();
        form.show();
    }

    protected abstract DetailsForm getDetailsForm();

    private void addCommands() {
        form.addCommand(selectCmd);
        form.addCommand(new Command(OPTION_BACK));
        form.addCommand(new Command(OPTION_EXIT));
        form.addCommand(new Command(OPTION_HELP));
        form.addCommand(new Command(OPTION_HOME));
        addFormSpecificCommands();
        form.addCommand(callCmd);
        form.addCommandListener(this);
    }

    protected abstract void addFormSpecificCommands();

    protected abstract String getPhoneNumberProperty();

    private void call() {
        String phoneNumber = getSelectedItemPhoneNumber();
        if (phoneNumber == null) {
            DialogUtil.showInfo("Error", "There are no results.");
            return;
        }
        if (UiUtil.isEmpty(phoneNumber)) {
            DialogUtil.showInfo("Error", "Phone number not found in this result.");
            return;
        }
        if (phoneNumber.indexOf(",") >= 0) {
            phoneNumber = phoneNumber.substring(0, phoneNumber.indexOf(","));
        }
        phoneNumber = phoneNumber.trim();
        if (UiUtil.isEmpty(phoneNumber)) {
            DialogUtil.showInfo("Error", "Phone number not found in this result.");
            return;
        }
        midlet.requestPlatformService("tel:" + phoneNumber);
    }

    private String getSelectedItemPhoneNumber() {
        Result selectedItem = resultsList.getSelectedItemResultRecord();
        if (selectedItem == null) {
            return null;
        }
        return selectedItem.getProperty(getPhoneNumberProperty());
    }

    private void handleKeyEventAction(ActionEvent ae) {
        final int keyEvent = ae.getKeyEvent();
        switch (keyEvent) {
            case -1:
                resultsList.selectItemUp();
                break;
            case -2:
                resultsList.selectItemDown();
                break;
        }
//        updateTitle();
    }

    protected void handleSelect() {
        showDetailsForm();
    }

    private void showDetailsForm() {
        final Result selectedItemResultRecord = resultsList.getSelectedItemResultRecord();
        if (selectedItemResultRecord == null) {
            DialogUtil.showInfo("Error", "There are no results.");
            return;
        }
        if (detailsForm == null) {
            detailsForm = getDetailsForm();
        }
        try {
            detailsForm.init(selectedItemResultRecord);
        } catch (ApplicationException ae) {
            DialogUtil.showInfo("Error", ae.getMessage());
        }
    }

    protected abstract String getInitialSortProperty();

    private void goBack() {
        callingForm.resetAndShow();
    }

    protected void initProcessedResults(Result[] results) throws NumberFormatException {
        this.results = results;
        this.resultsList.reset();
        for (int i = 0; i < results.length; i++) {
            Result result = results[i];
            if (result != null) {
                Container itemContainer = new ResultItem(result);
                renderResult(result, itemContainer);
                try {
                    resultsList.addComponent(itemContainer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        updateHeader();
        form.show();
    }

    private void updateHeader() {
        header.setResultCount(pageNumber, resultsList.getCount());
        updateTitle();
    }

    private void updateTitle() {
        header.setTitle(getTitle());
//        header.setTitle(resultsList.getSelectedItemResultRecord().getProperty(getTitleProperty()));
    }

    protected abstract void renderResult(Result result, Container itemContainer) throws NumberFormatException;

    private void initVariables(WhereYouDey midlet) {
        this.midlet = midlet;
        this.pageNumber = 1;
    }

    private void initForm() {
        form = new ExtendedForm();
        form.setScrollableX(false);
        form.setScrollableY(false);
        form.setLayout(new BorderLayout());
        form.addKeyListener(-1, this);
        form.addKeyListener(-2, this);
    }

    private void addHeader() {
        header = new Header(form);
    }

    private void addResultsSection(Result[] results) throws NumberFormatException {
        resultsList = new SearchResultsContainer(midlet);
        form.addComponent(BorderLayout.CENTER, resultsList);
        form.setFocused(resultsList);
        initResults(results);
    }

    public void initResults(Result[] results) throws NumberFormatException {
        SortUtil.sort(getInitialSortProperty(), results, SortUtil.SORT_ORDER_DESCENDING);
        initProcessedResults(results);
    }

    public void actionPerformed(ActionEvent ae) {
        final Command command = ae.getCommand();
        if (command != null) {
            handleCommandAction(command);
        } else {
            handleKeyEventAction(ae);
        }
        ae.consume();
    }

    void show() {
        form.show();
    }

    protected void filter(String filterProperty) {
        Result[] filteredResults = new Result[MAX_RESULTS];
        int j = 0;
        for (int i = 0; i < results.length; i++) {
            Result result = results[i];
            if (!UiUtil.isEmpty(result.getProperty(filterProperty))) {
                filteredResults[j] = result;
                j++;
            }
        }
        initProcessedResults(filteredResults);
    }

    private void handleCommandAction(Command command) {
        final String commandName = command.getCommandName();
        if (commandName.equals(OPTION_BACK)) {
            goBack();
        } else if (commandName.equals(OPTION_EXIT)) {
            midlet.exit();
        } else if (commandName.equals(OPTION_HELP)) {
            FormDialogs.showHelp();
        } else if (commandName.equals(OPTION_HOME)) {
            goBack();
        } else if (commandName.equals(OPTION_SELECT)) {
            handleSelect();
        } else if (commandName.equals(OPTION_CALL)) {
            call();
        } else if (commandName.equals(OPTION_NEXT)) {
            if (results.length == MAX_RESULTS) {
                pageNumber++;
                forwardTraversal = true;
                backTraversal = false;
                search();
            } else {
                DialogUtil.showInfo("Info", "You have reached the last page. There are no more results to show.");
            }
        } else if (commandName.equals(OPTION_PREV)) {
            if (pageNumber > 1) {
                pageNumber--;
                forwardTraversal = false;
                backTraversal = true;
                search();
            } else {
                DialogUtil.showInfo("Info", "Sorry, you are in the first page. There is no previous page to show.");
            }
        } else {
            handleFormSpecificCommandAction(commandName);
        }
    }

    private void search() {
        Thread t = new Thread(this);
        t.start();
        DialogUtil.showWait();
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            final Result[] searchResults = callingForm.searchAction(pageNumber);
            Display.getInstance().callSerially(new Runnable() {

                public void run() {
                    if (searchResults.length > 0) {
                        initResults(searchResults);
                    }
                    DialogUtil.hideWait();
                }
            });
        } catch (Exception e) {
            System.out.println("Error Occurred ....");
            e.printStackTrace();
        }
    }

    protected abstract void handleFormSpecificCommandAction(String commandName);

    protected abstract String getTitleProperty();

    protected abstract String getTitle();

    void setPageNumber(int pageNum) {
        this.pageNumber = pageNum;
        forwardTraversal = false;
        backTraversal = false;
    }

    int getPageNunber() {
        return pageNumber;
    }

    void setPageNumberOnError() {
        int oldpagen = pageNumber;
        if (backTraversal) {
            pageNumber++;
            System.out.println("Reset page number from " + oldpagen + " to " + pageNumber);
        } else if (pageNumber > 1 && forwardTraversal) {
            pageNumber--;
            System.out.println("Reset page number from " + oldpagen + " to " + pageNumber);
        } else {
            System.out.println("Reset page number - did not do anything - " + oldpagen + " to " + pageNumber);
        }
        backTraversal = false;
        forwardTraversal = false;
    }
}
