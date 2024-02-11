package works.quiet.resources;

import java.util.ResourceBundle;

public class Resources {
    private ResourceBundle bundle;

    public ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("strings");
        }

        return bundle;
    }
}
