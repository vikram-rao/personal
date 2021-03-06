package com.whereyoudey.form;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.TextField;
import com.whereyoudey.WhereYouDey;
import com.whereyoudey.service.helper.Result;
import com.whereyoudey.utils.DialogUtil;
import com.whereyoudey.utils.UiUtil;
import com.whereyoudey.webservice.ArrayOfString;

public class BusinessSearchForm extends SearchForm {

    private TextField business;
    private TextField area;

    public BusinessSearchForm(WhereYouDey midlet) {
        super(midlet);
    }

    protected boolean isFormValid() {
        return !UiUtil.isEmpty(getSearchBusinessText());
    }

    public Result[] searchAction(int pageNumber) {
        String businessText = getSearchBusinessText();
        String areaText = getSearchAreaText();
        ArrayOfString filter = new ArrayOfString();
        filter.setString(new String[]{"", "", "", "", "" + pageNumber, "10"});
        System.out.println("Searching results for \'" + businessText + " , " + areaText + "\' in page " + pageNumber);
        try {
            return searchService.searchBusinessData(businessText, areaText, filter);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was an error while searching results - " + e.getMessage());
            resultForm.setPageNumberOnError();
            DialogUtil.showInfo("Info", "No results found.");
        }
        return new Result[0];
    }

    protected void addFormFields() {
        addBusinessTextField();
        addAreaTextField();
        addSelectCityLink();
        this.business.setText("Barber");
    }

    private void addAreaTextField() {
        area = UiUtil.addTextFieldWithLabel(topContainer, "Area/City/State");
    }

    private void addBusinessTextField() {
        business = UiUtil.addTextFieldWithLabel(topContainer, "Businesses or Keywords");
    }

    void setAreaText(String area) {
        this.area.setText(area);
    }

    protected void moreSelectActionPerformed() {
    }

    public String getSearchBusinessText() {
        return this.business.getText().trim();
    }

    public String getSearchAreaText() {
        return this.area.getText().trim();
    }

    protected int getSelectedIconPos() {
        return -1;
    }

    protected void setFocus() {
        form.setFocused(business);
    }

    protected String getFormInvalidMessage() {
        return "Please enter business to search";
    }

    protected ResultForm getResultForm(Result[] results) {
        return new BusinessResultsForm(midlet, results, this);
    }

    protected TextField getCityDependentField() {
        return area;
    }

    protected void addFormSpecificCommands() {
    }

    protected void moreActionPerformed(String commandName) {
    }
}
