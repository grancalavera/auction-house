package works.quiet.resources;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Resources {
    private ResourceBundle bundle;

    public ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("strings");
        }

        return bundle;
    }

    public String getString(final String key) {
        return getBundle().getString(key);
    }

    public String getFormattedString(final String key, final Object... arguments) {
        var string = getBundle().getString(key);
        return MessageFormat.format(string, arguments);
    }
}

