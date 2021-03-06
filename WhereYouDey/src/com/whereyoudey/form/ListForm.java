/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.whereyoudey.form;

import com.sun.lwuit.Command;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.ListCellRenderer;
import com.whereyoudey.WhereYouDey;
import com.whereyoudey.utils.Colors;
import com.whereyoudey.utils.UiUtil;

/**
 *
 * @author Vikram S
 */
public class ListForm implements ActionListener {

    public static final String OPTION_CANCEL = "Cancel";
    public static final String OPTION_SELECT = "Select";
    private ExtendedForm form;
    private final WhereYouDey midlet;
    private final List citiesList;
    private TextField associatedField;
    private SearchForm callingForm;

    public ListForm(WhereYouDey midlet, String[] data, TextField associatedField, SearchForm callingForm) {
        form = new ExtendedForm();
        form.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        citiesList = new List(data);
        final DefaultListCellRenderer renderer = (DefaultListCellRenderer) citiesList.getRenderer();
        renderer.setShowNumbers(false);
        renderer.getSelectedStyle().setBgColor(Colors.SELECTEDITEM_BACKGROUND);
        renderer.getSelectedStyle().setFgColor(Colors.BLACK);
        Font smallFont = UiUtil.getFont(Font.STYLE_PLAIN, Font.SIZE_SMALL);
        Font smallBoldFont = UiUtil.getFont(Font.STYLE_BOLD, Font.SIZE_SMALL);
        renderer.getStyle().setFont(smallFont);
        renderer.getSelectedStyle().setFont(smallBoldFont);
        form.addComponent(citiesList);
        form.addCommand(new Command(OPTION_SELECT));
        form.addCommand(new Command(OPTION_CANCEL));
        form.addCommandListener(this);
        form.show();
        this.midlet = midlet;
        this.associatedField = associatedField;
        this.callingForm = callingForm;
    }
    public static final String[] CITIES = {
        "Aba",
        "Abak",
        "Abakaliki",
        "Abazu-Akabo",
        "Abeokuta",
        "Abuja",
        "Achalla",
        "Agbara-Otor",
        "Agbarho",
        "Agenebode",
        "Ahoada",
        "Akampa",
        "Akure",
        "Aladja",
        "Apapa",
        "Asaba",
        "Auchi",
        "Awka",
        "Azare",
        "Babaloma",
        "Badagri",
        "Bauchi",
        "Benin City",
        "Bida",
        "Birnin Kebbi",
        "Bomadi",
        "Bonny",
        "Bori",
        "Bornu Yassa",
        "Burutu",
        "Calabar",
        "Damaturu",
        "Dapchi",
        "Dutse",
        "Ede",
        "Edidi",
        "Effon-Alaiye",
        "Effurun",
        "Egbe",
        "Egini",
        "Emure",
        "Eku",
        "Enugu",
        "Eket",
        "Ekpoma",
        "Epe",
        "Etinan",
        "Evwreni",
        "Forcados",
        "Funtua",
        "Gboko",
        "Gombe",
        "Gumel",
        "Gusau",
        "Ibadan",
        "Ibuno",
        "Idah",
        "Ife",
        "Ifon",
        "Ihiala",
        "Ijan",
        "Ijare",
        "Ijebu Igbo",
        "Ijebu Ode",
        "Ikare",
        "Ijare",
        "Ikeja",
        "Ikerre",
        "Ikire",
        "Ikorodu",
        "Ikot Abasi",
        "Ikot Ekpene",
        "Ikoyi",
        "Ila",
        "Ilawe-Ekiti",
        "Ilesha",
        "Ile-Oluji",
        "Ilorin",
        "Ise",
        "Iseyin",
        "Itogo-Ekingo",
        "Itu",
        "Iwo",
        "Iyede",
        "Jalingo",
        "Jebba",
        "Jega",
        "Jimeta",
        "Jos",
        "Kaduna",
        "Kano",
        "Katsina",
        "Keffi",
        "Koko",
        "Kontagora",
        "Kumo",
        "Lafia",
        "Lafiagi",
        "Lagos",
        "Lokoja",
        "Maiduguri",
        "Makurdi",
        "Minna",
        "Mubi",
        "Nembe",
        "New Bussa",
        "Nguru",
        "Nibo",
        "Nnewi",
        "Nkwerre",
        "Nsukka",
        "Numan",
        "Ogbe ijaw",
        "Obudu",
        "Ogbomoso",
        "Ogbunka",
        "Oghara",
        "Ogoja",
        "Oguta",
        "Ohizi Ogabo",
        "Okene",
        "Okigwe",
        "Oko",
        "Okpareke",
        "Okpe",
        "Olomu",
        "Omoku",
        "Okwagbe",
        "Okpe",
        "Okpogho",
        "Oleh",
        "Olomoro",
        "Ondo",
        "Onne",
        "Onitsha",
        "Onueke",
        "Opobo",
        "Orerokpe",
        "Orhuwhorun",
        "Oron",
        "Orlu",
        "Oshogbo",
        "Ossissa",
        "Osubi",
        "Otu Jeremi",
        "Ovwian",
        "Owerri",
        "Owo",
        "Oyo",
        "Ozoro",
        "Port Harcourt",
        "Potiskum",
        "Sagamu",
        "Sango Otta",
        "Sapele",
        "Sepeteri",
        "Shaki",
        "Sokoto",
        "Suleja",
        "Surulere",
        "Oge-Toto",
        "Udu",
        "Ugep",
        "Ughelli",
        "Ukat Aran",
        "Umuahia",
        "Uromi",
        "uvwie",
        "Uyo",
        "Warri",
        "Wukari",
        "Yaba",
        "Yenagoa",
        "Yola",
        "Zaria"
    };

    public void actionPerformed(ActionEvent ae) {
        Command command = ae.getCommand();
        String commandName = command.getCommandName();
        if (OPTION_SELECT.equals(commandName)) {
            String selectCity = citiesList.getSelectedItem().toString();
            associatedField.setText(selectCity);
            callingForm.show();
        } else if (OPTION_CANCEL.equals(commandName)) {
            callingForm.show();
        }
    }

    public void show(TextField associatedField, SearchForm callingForm) {
        this.associatedField = associatedField;
        this.callingForm = callingForm;
        this.form.scrollComponentToVisible(this.citiesList);
        this.citiesList.setSelectedIndex(0);
        this.form.show();
    }
}
