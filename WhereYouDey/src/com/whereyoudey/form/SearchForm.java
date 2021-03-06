/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whereyoudey.form;

import com.whereyoudey.constants.AppConstants;
import com.whereyoudey.form.helper.FormDialogs;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.Border;
import com.whereyoudey.WhereYouDey;
import com.whereyoudey.service.helper.Result;
import com.whereyoudey.service.SearchService;
import com.whereyoudey.utils.Colors;
import com.whereyoudey.utils.DialogUtil;
import com.whereyoudey.utils.UiUtil;

/**
 *
 * @author Vikram S
 */
public abstract class SearchForm implements ActionListener, Runnable {

    public static final int DISPLAY_WIDTH = Display.getInstance().getDisplayWidth();
    public static final String FLIGHTS_URL = "http://www.wakanow.com";
    public static final String ICON_NAME_EVENTS = "Events";
    public static final String ICON_NAME_FLIGHTS = "Flights";
    public static final String ICON_NAME_MOVIES = "Movies";
    public static final String ICON_NAME_OFFERS = "Offers";
    public static final String ICON_NAME_SMS_ADS = "SMS Ads";
    public static final String ICON_NAME_VIDEOS = "Videos";
    public static final String LINK_MORE = "More";
    public static final String LINK_SELECT_CITY = "Select City";
    public static final String LOGO_PATH = "/img/logo.png";
    public static final int MORE_LINK_POSSIBLE_WIDTH = 40;
    public static final String OPTION_ABOUT_US = "About Us";
    public static final String OPTION_CHECK_FOR_UPDATES = "Check For Updates";
    public static final String OPTION_EXIT = "Exit";
    public static final String OPTION_FIND = "Find";
    public static final String OPTION_HELP = "Help";
    public static final String OPTION_SELECT = "Select";
    public static final String UPDATE_URL = "http://www.whereyoudey.com/whereyoudey.jad";
    public static final String VIDEO_URL = "http://www.youtube.com/whereyoudey";
    private static final String[] iconIds = {
        ICON_NAME_VIDEOS,
        ICON_NAME_OFFERS,
        ICON_NAME_EVENTS,
        ICON_NAME_MOVIES,
        ICON_NAME_FLIGHTS,
        ICON_NAME_SMS_ADS
    };
    private static final String[] iconPaths = {
        "/img/icons/VideosIcon.png",
        "/img/icons/OffersIcon.png",
        "/img/icons/EventIcon.png",
        "/img/icons/MoviesIcon.png",
        "/img/icons/FlightsIcon.png",
        "/img/icons/SMSAds.png"
    };
    protected static final SearchService searchService = new SearchService();
    protected String focussed;
    protected ExtendedForm form;
    protected WhereYouDey midlet;
    protected Container topContainer;
    protected ResultForm resultForm;
    private ListForm cityOptionsform;
    private SearchForm eventsForm;
    final Command selectCommand = new Command(OPTION_SELECT);
    private Label selectedIconImage;

    public SearchForm(WhereYouDey midlet) {
        this.midlet = midlet;
        initForm();
    }

    public void actionPerformed(ActionEvent ae) {
        final String commandName = ae.getCommand().getCommandName();
        if (OPTION_EXIT.equals(commandName)) {
            midlet.exit();
        } else if (OPTION_FIND.equals(commandName)) {
            search(1);
        } else if (OPTION_ABOUT_US.equals(commandName)) {
            FormDialogs.showAbout();
        } else if (OPTION_HELP.equals(commandName)) {
            FormDialogs.showHelp();
        } else if (OPTION_CHECK_FOR_UPDATES.equals(commandName)) {
            midlet.requestPlatformService(UPDATE_URL);
        } else if (OPTION_SELECT.equals(commandName)) {
            if (selectedIconImage != null) {
                selectedIconImage.setFocus(false);
                selectedIconImage = null;
            }
            System.out.println("Focussed = " + focussed);
            if (ICON_NAME_VIDEOS.equals(focussed)) {
                midlet.requestPlatformService(VIDEO_URL);
            } else if (LINK_MORE.equals(focussed)) {
                handleUnaviableFeature();
            } else if (ICON_NAME_FLIGHTS.equals(focussed)) {
                handleUnaviableFeature();
            } else if (ICON_NAME_OFFERS.equals(focussed)) {
                handleUnaviableFeature();
            } else if (ICON_NAME_MOVIES.equals(focussed)) {
                showMoviesForm();
            } else if (ICON_NAME_SMS_ADS.equals(focussed)) {
                handleUnaviableFeature();
            } else if (ICON_NAME_EVENTS.equals(focussed)) {
                showEventsForm();
            } else if (LINK_SELECT_CITY.equals(focussed)) {
                createCitiesOptionsForm(getCityDependentField());
            } else {
                moreSelectActionPerformed();
            }
        } else {
            moreActionPerformed(commandName);
        }
    }

    private void handleUnaviableFeature() {
        if (FormDialogs.showFeatureUnavialbleMessage()) {
            midlet.requestPlatformService("http://www.whereyoudey.com");
        }
    }

    protected abstract ResultForm getResultForm(Result[] results);

    private void showEventsForm() {
        eventsForm = new EventsSearchForm(midlet);
    }

    private void addIcons() {
        Container icons = UiUtil.getBoxLayoutColumnStyleContainer();
        int noOfIconsFittingInScreenWidth = (DISPLAY_WIDTH - MORE_LINK_POSSIBLE_WIDTH) / AppConstants.ICON_WIDTH;
        for (int i = 0; i < noOfIconsFittingInScreenWidth && i < iconPaths.length; i++) {
            final int pos = i;
            final Label image = UiUtil.getImageLabel(iconPaths[i], AppConstants.ICON_WIDTH);
            image.setFocusable(true);
            image.addFocusListener(new FocusListener() {

                public void focusGained(Component cmpnt) {
                    focussed = iconIds[pos];
                    selectedIconImage = image;
                    form.addCommand(selectCommand, form.getCommandCount());
                }

                public void focusLost(Component cmpnt) {
                    if (focussed.equals(iconIds[pos])) {
                        focussed = "";
                        selectedIconImage = null;
                        form.removeCommand(selectCommand);
                    }
                }
            });
            if (i == getSelectedIconPos()) {
                image.getStyle().setBorder(Border.createBevelRaised());
                selectedIconImage = image;
            }
            image.getSelectedStyle().setBorder(Border.createLineBorder(1));
            image.getStyle().setPadding(0, 0, 4, 4);
            icons.addComponent(image);
        }
        Label moreLink = UiUtil.getLink(LINK_MORE);
        moreLink.addFocusListener(new FocusListener() {

            public void focusGained(Component cmpnt) {
                focussed = LINK_MORE;
                form.addCommand(selectCommand, form.getCommandCount());
            }

            public void focusLost(Component cmpnt) {
                if (LINK_MORE.equals(focussed)) {
                    focussed = "";
                    form.removeCommand(selectCommand);
                }
            }
        });
        icons.addComponent(moreLink);
        topContainer.addComponent(icons);
    }

    private void addLogo() {
        int logoWidth = DISPLAY_WIDTH * 2 / 3;
        System.out.println("Logo Width - " + logoWidth);
        Label logo = UiUtil.getImageLabel(LOGO_PATH);
        topContainer.addComponent(logo);
    }

    private void addMainElements() {
        topContainer = UiUtil.geBoxLayoutContainer(BoxLayout.Y_AXIS);
        addLogo();
        addIcons();
        addFormFields();
        setFocus();
        form.addComponent(BorderLayout.NORTH, topContainer);
    }

    protected abstract void setFocus();

    private void addMenuActions() {
        form.addCommandListener(this);
        form.addMenuItem(OPTION_FIND);
        form.addMenuItem(OPTION_EXIT);
        form.addMenuItem(OPTION_ABOUT_US);
        form.addMenuItem(OPTION_CHECK_FOR_UPDATES);
        form.addMenuItem(OPTION_HELP);
        addFormSpecificCommands();
    }

    private void createForm() {
        form = new ExtendedForm();
        form.setLayout(new BorderLayout());
    }

    public Component getFocussed() {
        return form.getFocused();
    }

    private void initForm() {
        createForm();
        addMainElements();
        addMenuActions();
        show();
    }

    public void show() {
        form.show();
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            loadBanners();
            Result [] results = searchAction(1);
            initResultForm(results);
            DialogUtil.hideWait();
            resultForm.show();
        } catch (Exception e) {
            DialogUtil.showInfo("Error", "No results found.");
        }
        DialogUtil.hideWait();
    }

    private void initResultForm(Result[] results) throws NumberFormatException {
        if (resultForm == null) {
            resultForm = getResultForm(results);
        } else {
            resultForm.setPageNumber(1);
            resultForm.initResults(results);
        }
    }

    public ResultForm getResultForm() {
        return resultForm;
    }

    public void search(int pageNumber) {
        if (!isFormValid()) {
            DialogUtil.showInfo("Error", getFormInvalidMessage());
            return;
        }
        Thread t = new Thread(this);
        t.start();
        DialogUtil.showWait();
    }

    protected abstract void addFormFields();

    protected abstract void moreSelectActionPerformed();

    protected abstract boolean isFormValid();

    public abstract Result[] searchAction(int pageNumber);

    protected abstract int getSelectedIconPos();

    protected abstract String getFormInvalidMessage();

    protected void createCitiesOptionsForm(TextField associatedField) {
        if (cityOptionsform == null) {
            cityOptionsform = new ListForm(midlet, ListForm.CITIES, associatedField, this);
        } else {
            cityOptionsform.show(associatedField, this);
        }
    }

    protected abstract TextField getCityDependentField();

    protected void addSelectCityLink() {
        Label chooseCityLink = UiUtil.getLink("or Choose City");
        chooseCityLink.setFocusable(true);
        chooseCityLink.addFocusListener(new FocusListener() {

            public void focusGained(Component cmpnt) {
                focussed = SearchForm.LINK_SELECT_CITY;
                form.addCommand(selectCommand, form.getCommandCount());
            }

            public void focusLost(Component cmpnt) {
                if (SearchForm.LINK_SELECT_CITY.equals(focussed)) {
                    focussed = "";
                    form.removeCommand(selectCommand);
                }
            }
        });
        topContainer.addComponent(chooseCityLink);
    }

    protected abstract void addFormSpecificCommands();

    protected abstract void moreActionPerformed(String commandName);

    private void showMoviesForm() {
        SearchForm moviesSearchForm = new MoviesSearchForm(midlet);
    }

    void resetAndShow() {
        show();
        setFocus();
    }

    private void loadBanners() {
        if (!midlet.hasBanners()) {
            midlet.setBanners(searchService.getBanners());
        }
    }
}
