package controller;

import view.DesktopPane;

import javax.swing.*;

public class DesktopManager extends DefaultDesktopManager {

    private DesktopPane desktop;

    public DesktopManager(DesktopPane desktop){
        this.desktop = desktop;
    }
}
