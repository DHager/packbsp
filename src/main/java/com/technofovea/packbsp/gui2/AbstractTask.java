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
import com.technofovea.packbsp.PackbspException;
import org.jdesktop.application.Task;

public abstract class AbstractTask<A, B> extends Task<A, B> {

    PackbspView outer;
    AppModel model;

    AbstractTask(PackbspApplication app, PackbspView outer) {
        // Runs on the EDT.  Copy GUI state that
        // doInBackground() depends on from parameters
        // to AdvanceSteamTask fields, here.
        super(app);
        this.outer = outer;
        model = app.getModel();
    }

    @Override
    public synchronized boolean isProgressPropertyValid() {
        return false;
    }

    @Override
    protected abstract A doInBackground() throws Exception;

    @Override
    public synchronized boolean getUserCanCancel() {
        return false;
    }

    @Override
    protected void failed(final Throwable cause) {

        String stepMessage = getMessage();
        if(stepMessage==null){
            stepMessage = "";
        }else{
            stepMessage = "\n" + stepMessage;
        }
        if (cause instanceof IllegalArgumentException) {
            final IllegalArgumentException iae = (IllegalArgumentException) cause;
            outer.showInvalidInputDialog("Invalid input in action:" +stepMessage, iae);
        } else if (cause instanceof PackbspException) {
            PackbspException pe = (PackbspException) cause;
            outer.showErrorDialog("Unexpected error in action:" + stepMessage, pe);
        } else {
            outer.showErrorDialog("Unknown PackBSP error", cause);
        }
    }

    @Override
    protected void cancelled() {
        outer.showCancelledDialog("Action was cancelled:\n" + getTitle());
    }

    @Override
    protected abstract void succeeded(A result);
}
