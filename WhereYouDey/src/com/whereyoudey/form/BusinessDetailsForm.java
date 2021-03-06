/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whereyoudey.form;

import com.sun.lwuit.Container;
import com.sun.lwuit.Font;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BoxLayout;
import com.whereyoudey.WhereYouDey;
import com.whereyoudey.form.component.Section;
import com.whereyoudey.service.helper.Result;
import com.whereyoudey.utils.Colors;
import com.whereyoudey.utils.FontUtil;
import com.whereyoudey.utils.UiUtil;

/**
 *
 * @author Vikram S
 */
public class BusinessDetailsForm extends DetailsForm {

    private WrappingLabel address;
    private WrappingLabel phoneNumber;
    private Container ratingContainer;
    private Section hoursOfOperation;
    private Section description;
    private Section productsInformation;
    private Section pricingInformation;
    private Section businessCategory;
    private Section additionalInformation;
    private Section keyWords;
    private WrappingLabel state;
    private WrappingLabel city;

    public BusinessDetailsForm(WhereYouDey midlet, ResultForm callingForm) {
        super(midlet, callingForm);
    }

    protected void addBasicInfo() {
        address = new WrappingLabel("", FontUtil.getSmallNormalFont());//addSmallFontLabel("");
        form.addComponent(address);
        city = new WrappingLabel("", FontUtil.getSmallNormalFont());//addSmallFontLabel("");
        form.addComponent(city);
        state = new WrappingLabel("", FontUtil.getSmallNormalFont());//addSmallFontLabel("");
        form.addComponent(state);
        phoneNumber = new WrappingLabel("", FontUtil.getBigBoldFont());//addBigFontLabel("");
        form.addComponent(phoneNumber);
        ratingContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
        form.addComponent(ratingContainer);
        setRating("0", "0");
    }

    protected void addFormSpecificSections() {
        hoursOfOperation = new Section(form, "Hours Of Operations", "");
        description = new Section(form, "DESCRIPTION", "");
        productsInformation = new Section(form, "Products Information", "");
        pricingInformation = new Section(form, "Pricing Information", "");
        businessCategory = new Section(form, "Business Category", "");
        additionalInformation = new Section(form, "Additional Information", "");
        keyWords = new Section(form, "Keywords", "");
    }

    private void setRating(final String ratingStr, String reviewCount) throws NumberFormatException {
        int rating = Integer.parseInt(ratingStr);
        ratingContainer.removeAll();
        for (int j = 1; j <= rating; j++) {
            Label ratingIcon = new Label(getImage("/img/rating_mark.png", 5));
            ratingContainer.addComponent(ratingIcon);
        }
        for (int j = rating + 1; j <= 5; j++) {
            Label ratingIcon = new Label(getImage("/img/rating_empty.png", 5));
            ratingContainer.addComponent(ratingIcon);
        }
        if (UiUtil.isEmpty(reviewCount)) {
            reviewCount = "0";
        }
        reviewCount = reviewCount.trim();
        final Label l = UiUtil.addSmallFontLabel(ratingContainer, "(" + reviewCount + ")");
        l.getSelectedStyle().setBgColor(Colors.SELECTEDITEM_BACKGROUND);
        l.getSelectedStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
    }

    protected void initResult(Result result) {
        final String address = result.getProperty("Address");
        final String city = result.getProperty("City");
        final String state = result.getProperty("State");
        final String phone = result.getProperty("Phone");
        final String ratingStr = result.getProperty("StarReview");
        final String description = result.getProperty("Description") + "\n" + result.getProperty("GeneralInfoP1") + "\n" + result.getProperty("GeneralInfoP2");
        final String category = result.getProperty("Category");
        final String additionalInfo = result.getProperty("AdditionalInfo") + "\n" + result.getProperty("Email");
        final String prodServices = result.getProperty("ProdServices") + "\n" + result.getProperty("Products") + "\n" + result.getProperty("ProdServices");
        final String keyWords = result.getProperty("KeyWords");
        final String reviewCount = result.getProperty("ReviewCount");
        final String hoursOfOperation = result.getProperty("BusinessHours");
        this.address.setText(address, FontUtil.getSmallNormalFont());
        this.city.setText(city, FontUtil.getSmallNormalFont());
        this.state.setText(state, FontUtil.getSmallNormalFont());
        this.phoneNumber.setText(phone, FontUtil.getBigBoldFont());
        setRating(ratingStr, reviewCount);
        this.description.setDetails(description);
        this.businessCategory.setDetails(category);
        this.productsInformation.setDetails(prodServices);
        this.keyWords.setDetails(keyWords);
        this.additionalInformation.setDetails(additionalInfo);
        this.hoursOfOperation.setDetails(hoursOfOperation);
        this.videoUrl = result.getProperty("YouTubeVideoLink");
    }

    protected String getHeaderProperty() {
        return "Name";
    }

    protected String getPhoneProperty() {
        return "Phone";
    }

    protected String getAddress() {
        return result.getProperty("Address")
               + "," + result.getProperty("Area")
               + "," + result.getProperty("City")
               + "," + result.getProperty("State");
    }

    protected String getAdditionalHeaderText() {
        String additionalText = "";
        try {
            final String subType = result.getProperty("SubType");
            final int subTypeInt = Integer.parseInt(subType);
            if (subTypeInt == 2) {
                additionalText = "*";
            } else if (subTypeInt == 1) {
                additionalText = "**";
            }
        } catch (NumberFormatException numberFormatException) {
        }
        return additionalText;
    }
}
