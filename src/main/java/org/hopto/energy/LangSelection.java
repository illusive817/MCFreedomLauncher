package org.hopto.energy;

import net.minecraft.launcher.locale.LocaleHelper;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by Energy on 13年7月13日.
 */
public class LangSelection {
    static final String Select = "Select Language";
    static Locale locale = new Locale("en", "US");

    public static Locale getLocale() {
        String localeValue = Util.getProperties("locale");


        if (localeValue != null && !localeValue.equals("")) {

            String[] localeSetting = localeValue.split("_");
            locale = new Locale(localeSetting[0], localeSetting[1]);
            // JOptionPane.showMessageDialog(null, "You have selected: " + locale);

        } else {
            return selectLang();
        }

        return locale;

    }

    public static Locale selectLang() {

        //   JButton button=new JButton("Add");

        Locale[] locales = LocaleHelper.getLocales();

        locale = (Locale) JOptionPane.showInputDialog(null,
                "Please select your language\n" +
                        "請選擇你的語言", Select, JOptionPane.INFORMATION_MESSAGE,
                null, locales, locales[0]);
        if (locale == null) {
            locale = LocaleHelper.getCurrentLocale();
        }
        LocaleHelper.setCurrentLocale(locale);
        JOptionPane.showMessageDialog(null, "You have selected: " + locale);
        Util.setProperties("locale", locale.toString());
        System.out.println(locale);
        // button.addActionListener(lst);
        // add(button);
        return locale;

    }

    public static Locale setLang(Locale locale) {

        //   JButton button=new JButton("Add");

        //Locale[] locales = LocaleHelper.getLocales();


        if (locale == null) {
            locale = LocaleHelper.getCurrentLocale();
        }
        LocaleHelper.setCurrentLocale(locale);
        JOptionPane.showMessageDialog(null, "You have selected: " + locale);
        Util.setProperties("locale", locale.toString());
        // System.out.println(locale);
        // button.addActionListener(lst);
        // add(button);
        return locale;

    }
}
