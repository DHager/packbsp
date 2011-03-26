/**
 * Copyright (C) 2011 Darien Hager
 *
 * This code is part of the "PackBSP" project, and is licensed under
 * a Creative Commons Attribution-ShareAlike 3.0 Unported License. For
 * either a summary of conditions or the full legal text, please visit:
 *
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Permissions beyond the scope of this license may be available
 * at http://technofovea.com/ .
 */
package com.technofovea.packbsp.gui2;

import com.technofovea.packbsp.AppModel;
import java.util.EventObject;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Darien Hager
 */
public class PackbspApplication extends SingleFrameApplication {

    private Logger log = LoggerFactory.getLogger(PackbspApplication.class);
    AppModel model;
    PackbspView mainView;

    @Override
    protected void startup() {

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(PackbspView.class);
        ImageIcon mainIcon = resourceMap.getImageIcon("Application.icon");


        model = new AppModel();
        mainView = new PackbspView(this);

        /**
         * I don't know why, but getMainFrame()==getMainView.getFrame()
         * while mainView.getFrame() is entirely different.
         */
        mainView.getFrame().setIconImage(mainIcon.getImage());


        ExitListener maybeExit = new ExitListener() {

            public boolean canExit(EventObject e) {
                int option = JOptionPane.showConfirmDialog(mainView.getFrame(), "Really Exit?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                return option == JOptionPane.YES_OPTION;
            }

            public void willExit(EventObject event) {
            }
        };
        addExitListener(maybeExit);

        // Add controller logic
        //TODO WizardController firstStep = new FirstController(mainView);


        //mainView.advanceController(firstStep);
        show(mainView);




        /*
        String arch = System.getProperty("sun.arch.data.model");
        if (!arch.equals("32")) {
            JOptionPane.showMessageDialog(mainView.getFrame(),
                    "It appears as if this version of Java is running in 64-bit mode.\n\n"
                    + "64-bit support is very new, so please inform the author if you have problems. ",
                    "64-bit Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        mainView.showBetaMessage();
         */
    }

    public AppModel getModel() {
        return model;
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of PackbspApplication
     */
    public static PackbspApplication getApplication() {
        return Application.getInstance(PackbspApplication.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(PackbspApplication.class, args);
    }
}
