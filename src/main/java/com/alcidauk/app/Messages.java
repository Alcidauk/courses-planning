package com.alcidauk.app;

import com.vaadin.ui.UI;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by alcidauk on 13/09/16.
 */
public class Messages {

    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", UI.getCurrent().getLocale());

    public static String getMessage(String key){
        return bundle.getString(key);
    }

    public static String getMessage(String key, Object... params) {
        try {
            return MessageFormat.format(bundle.getString(key), params);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String getWorkSessionTypeNameMessage(String key, Object... params) {
        try {
            return MessageFormat.format(bundle.getString("com.alcidauk.courses.planning.work.session.type." + key), params);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
