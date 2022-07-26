package view;

import controller.ProjectManager;

import javax.swing.*;
import java.awt.*;

public class ProjectInterface extends JInternalFrame {
    public ProjectInterfacePanel projectInterfacePanel;
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;

    public ProjectInterface(ProjectManager projectManager){

        super("Informations projet", true, true, true, true);

        initComponents(projectManager);

        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;

    }

    private void initComponents(ProjectManager projectManager) {

        setAutoscrolls(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(screenSize.width/4, screenSize.height /4));

        projectInterfacePanel = new ProjectInterfacePanel(projectManager);

        setContentPane(projectInterfacePanel);
        pack();
    }


}
