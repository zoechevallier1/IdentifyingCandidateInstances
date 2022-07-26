package view;
import  controller.DesktopManager;

import javax.swing.*;

public class DesktopPane extends JDesktopPane {

    private DesktopManager manager;

    public DesktopPane(){
        manager = new DesktopManager(this);
        setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }
}
