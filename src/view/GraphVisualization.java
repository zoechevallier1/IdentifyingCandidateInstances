package view;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import model.FileToModelGraph;
import model.PatternSearch;
import model.Source;
import model.TargetSchema;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import static controller.DataIntegration.DI_APP;

public class GraphVisualization extends JInternalFrame {

    public final Model model;
    public String name;
    public final Boolean instances;

    public Graph<RDFNode, Statement> graph;
    public VisualizationViewer<RDFNode, Statement> vv;
    public GraphVisualizationPanel graphVisualizationPanel;
    private JMenuBar menuBar;
    private PatternSearch patternSearch;
    private JPanel globalPanel;

    static int openFrameCount = 0;
    static final int xOffset = 70, yOffset = 70;
    private ActionListener actionNodeFilter;
    private ActionListener showInstances;
    private ActionListener actionLinkFilter;

    private ActionListener actionSelectAll;
    private ActionListener actionDeselectAll;

    private JMenu mnNodeFilters;
    private JMenu mnLinkFilters;
    private JTextField txtTextFilter;
    private JTextField txtSparqlFilter;

    public GraphVisualization(Model model, String name, Boolean instances){
        super(name, false, true, true, true);
        if (DI_APP.getMainWindow().getInternalFrame() != null) {
            DI_APP.getMainWindow().remove(DI_APP.getMainWindow().getInternalFrame());
        }
        DI_APP.getMainWindow().setInternalFrame(this);
        this.model = model;
        this.instances = instances;
        this.name = name;

        if (instances){
            this.graph = FileToModelGraph.ModelToGraph(model);
        }
        else {
            this.graph = FileToModelGraph.ModelToGraph(hideInstances());
        }

        initComponents();

        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;
    }

    public Model hideInstances(){
        Model model = ModelFactory.createDefaultModel();
        Query query = QueryFactory.create(
                "CONSTRUCT {" +
                        "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class>. " +
                        "?y <http://www.w3.org/2000/01/rdf-schema#domain> ?x . " +
                        "?y  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?yType." +
                        "?y <http://www.w3.org/2000/01/rdf-schema#range> ?yRange . " +
                        "?x <http://www.w3.org/2002/07/owl#equivalentClass> ?xEq} " +
                "WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Class> . " +
                        "?y <http://www.w3.org/2000/01/rdf-schema#domain> ?x ." +
                        "OPTIONAL {?y  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?yType.}" +
                        "OPTIONAL {?y <http://www.w3.org/2000/01/rdf-schema#range> ?yRange .}" +
                        "OPTIONAL {?x <http://www.w3.org/2002/07/owl#equivalentClass> ?xEq}" +

                    "} ");
        QueryExecution qexec = QueryExecutionFactory.create(query, this.model);
        model = qexec.execConstruct();

        return model;

    }



    private void initComponents(){

        setAutoscrolls(true);
        globalPanel = new JPanel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(screenSize.width/2, screenSize.height /2));

        graphVisualizationPanel = new GraphVisualizationPanel(graph, null, null, new HashMap<String,Point>(), false);
        vv = graphVisualizationPanel.vv;

        menuBar = new JMenuBar();
        menuBar.setFont(new Font("Arial", Font.PLAIN, 11));
        menuBar.setMinimumSize(new Dimension(0, 22));
        menuBar.setMaximumSize(new Dimension(0, 22));
        JButton showInstances;
        if (instances) {
            showInstances = new JButton("Hide instances");
        }
        else {
            showInstances = new JButton("Show instances");
        }

        menuBar.add(showInstances);
        showInstances.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphVisualization viz;
                if (instances) {
                     viz = new GraphVisualization(model, name, false);
                }
                else {
                    viz = new GraphVisualization(model, name,true);
                }
                DI_APP.getMainWindow().add(viz);
                viz.setVisible(true);
            }
        });




        mnNodeFilters = new JMenu("Node filters");
        mnNodeFilters.setFont(new Font("Arial", Font.PLAIN, 11));
        mnNodeFilters.setFocusPainted(true);
        menuBar.add(mnNodeFilters);

        JMenuItem item = new JMenuItem("Select all");
        item.addActionListener(getActionSelectAll());
        mnNodeFilters.add(item);
        item = new JMenuItem(("Deselect all"));
        item.addActionListener(getActionDeselectAll());
        mnNodeFilters.add(item);
        mnNodeFilters.addSeparator();
        for (String type : graphVisualizationPanel.getTypesNode()) {
            //if (!type.equals("literal")) {
                item = new JCheckBoxMenuItem(type, true);
            /*item.setContentAreaFilled(false);
        	item.setBorder(null);
        	item.setIconTextGap(0);
        	item.setHorizontalAlignment(SwingConstants.LEFT);
        	item.setHorizontalTextPosition(SwingConstants.LEFT);
        	item.setFont(new Font("Arial", Font.PLAIN, 10));
            miFilterURI.setMaximumSize(new Dimension(35, 32767));
            miFilterURI.setMinimumSize(new Dimension(35, 0));
            miFilterURI.setPreferredSize(new Dimension(35, 22));
            miFilterURI.setSize(new Dimension(55, 0));*/
                item.addActionListener(getActionNodeFilter());
                mnNodeFilters.add(item);
            //}
        }

        JLabel libSep1 = new JLabel("");
        libSep1.setMinimumSize(new Dimension(10, 20));
        libSep1.setMaximumSize(new Dimension(10, 20));
        libSep1.setPreferredSize(new Dimension(10, 20));
        menuBar.add(libSep1);

        mnLinkFilters = new JMenu("Link filters");
        mnLinkFilters.setFont(new Font("Arial", Font.PLAIN, 11));
        mnLinkFilters.setFocusPainted(true);
        menuBar.add(mnLinkFilters);

        item = new JMenuItem("Select all");
        item.addActionListener(getActionSelectAll());
        mnLinkFilters.add(item);
        item = new JMenuItem(("Deselect all"));
        item.addActionListener(getActionDeselectAll());
        mnLinkFilters.add(item);
        mnLinkFilters.addSeparator();
        for (String type : graphVisualizationPanel.getTypesLink()) {
            item = new JCheckBoxMenuItem(type, true);
        	/*item.setContentAreaFilled(false);
        	item.setBorder(null);
        	item.setIconTextGap(0);
        	item.setHorizontalAlignment(SwingConstants.LEFT);
        	item.setHorizontalTextPosition(SwingConstants.LEFT);
        	item.setFont(new Font("Arial", Font.PLAIN, 10));
            miFilterURI.setMaximumSize(new Dimension(35, 32767));
            miFilterURI.setMinimumSize(new Dimension(35, 0));
            miFilterURI.setPreferredSize(new Dimension(35, 22));
            miFilterURI.setSize(new Dimension(55, 0));*/
            item.addActionListener(getActionLinkFilter());
            mnLinkFilters.add(item);
        }

        JLabel lblSep2 = new JLabel("");
        lblSep2.setMinimumSize(new Dimension(10, 20));
        lblSep2.setMaximumSize(new Dimension(10, 20));
        lblSep2.setPreferredSize(new Dimension(10, 20));
        menuBar.add(lblSep2);

        JLabel lblTextFilter = new JLabel("Text filter : ");
        lblTextFilter.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTextFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTextFilter.setMinimumSize(new Dimension(60, 0));
        lblTextFilter.setMaximumSize(new Dimension(60, 32767));
        menuBar.add(lblTextFilter);

        txtTextFilter = new JTextField();
        txtTextFilter.setColumns(10);
        txtTextFilter.setMaximumSize(new Dimension(150, 2147483647));
        txtTextFilter.setPreferredSize(new Dimension(80, 20));
        txtTextFilter.setMinimumSize(new Dimension(80, 20));
        txtTextFilter.setBorder(new CompoundBorder(new EmptyBorder(3, 0, 3, 0), new LineBorder(new Color(0, 0, 0))));
        txtTextFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        txtTextFilter.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    graphVisualizationPanel.setNodeTextFilter(txtTextFilter.getText());
                }
            }
        });

        menuBar.add(txtTextFilter);


        JButton btCleanTxtFilter = new JButton("x");
        btCleanTxtFilter.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        txtTextFilter.setText("");
                        graphVisualizationPanel.setNodeTextFilter(null);
                    }
                }
        );
        btCleanTxtFilter.setPreferredSize(new Dimension(18, 18));
        btCleanTxtFilter.setMaximumSize(new Dimension(18, 18));
        btCleanTxtFilter.setMinimumSize(new Dimension(18, 18));
        btCleanTxtFilter.setMargin(new Insets(0, 0, 0, 0));
        btCleanTxtFilter.setBorder(null);
        menuBar.add(btCleanTxtFilter);

        JLabel lblSep3 = new JLabel("");
        lblSep3.setMaximumSize(new Dimension(10, 20));
        lblSep3.setMinimumSize(new Dimension(10, 20));
        lblSep3.setPreferredSize(new Dimension(10, 20));
        menuBar.add(lblSep3);

        JLabel lblSparqlFilter = new JLabel("Sparql filter : ");
        lblSparqlFilter.setMinimumSize(new Dimension(70, 0));
        lblSparqlFilter.setMaximumSize(new Dimension(70, 32767));
        lblSparqlFilter.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSparqlFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        menuBar.add(lblSparqlFilter);

        txtSparqlFilter = new JTextField();
        txtSparqlFilter.setEditable(false);
        txtSparqlFilter.setPreferredSize(new Dimension(80, 20));
        txtSparqlFilter.setMinimumSize(new Dimension(80, 20));
        txtSparqlFilter.setMaximumSize(new Dimension(200, 2147483647));
        txtSparqlFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSparqlFilter.setColumns(10);
        txtSparqlFilter.setBorder(new CompoundBorder(new EmptyBorder(3, 0, 3, 0), new LineBorder(new Color(0, 0, 0))));
        txtSparqlFilter.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                getPatternSearch().setQuery(txtSparqlFilter.getText());
                getPatternSearch().setVisible(true);
                if (patternSearch.getDialogResult() == JOptionPane.OK_OPTION) {
                    txtSparqlFilter.setText(patternSearch.getQuery());
                    graphVisualizationPanel.setNodeSparqlFilter(txtSparqlFilter.getText());
                }
            }
        });
        menuBar.add(txtSparqlFilter);

        JButton btCleanSparqlFilter = new JButton("x");
        btCleanSparqlFilter.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txtSparqlFilter.setText("");
                        graphVisualizationPanel.setNodeSparqlFilter(null);
                    }
                }
        );
        btCleanSparqlFilter.setPreferredSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMinimumSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMaximumSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMargin(new Insets(0, 0, 0, 0));
        btCleanSparqlFilter.setBorder(null);
        menuBar.add(btCleanSparqlFilter);

        JLabel lblSep4 = new JLabel("");
        lblSep4.setPreferredSize(new Dimension(10, 20));
        lblSep4.setMinimumSize(new Dimension(10, 20));
        lblSep4.setMaximumSize(new Dimension(10, 20));
        menuBar.add(lblSep4);

        final JButton btnTarget = new JButton("SELECT");
        btnTarget.setToolTipText("Select Node");
        btnTarget.setIcon(new ImageIcon(getClass().getResource("/target-16px.png")));
        btnTarget.setSelectedIcon(new ImageIcon(getClass().getResource("/target-24px.png")));
        btnTarget.setBorder(null);
        btnTarget.setMinimumSize(new Dimension(30, 23));
        btnTarget.setPreferredSize(new Dimension(30, 23));
        btnTarget.setMaximumSize(new Dimension(30, 30));
        btnTarget.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (graphVisualizationPanel.getNodeTargetFilter()) {
                    btnTarget.setIcon(new ImageIcon(getClass().getResource("/target-16px.png")));
                    vv.setCursor(Cursor.getDefaultCursor());
                    graphVisualizationPanel.setNodeTargetFilter(false);
                } else {
                    btnTarget.setIcon(btnTarget.getSelectedIcon());
                    vv.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    graphVisualizationPanel.setNodeTargetFilter(true);
                }
            }
        });
        menuBar.add(btnTarget);

        final JButton btnEdit = new JButton("Edit");
        btnEdit.setToolTipText("Editing Mode");
        btnEdit.setIcon(new ImageIcon(getClass().getResource("/hand-16px.png")));
        btnEdit.setSelectedIcon(new ImageIcon(getClass().getResource("/hand-24px.png")));
        btnEdit.setMinimumSize(new Dimension(30, 23));
        btnEdit.setPreferredSize(new Dimension(30, 23));
        btnEdit.setMaximumSize(new Dimension(30, 30));
        btnEdit.setBorder(null);
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (btnEdit.getIcon() == btnEdit.getSelectedIcon()) {
                    btnEdit.setIcon(new ImageIcon(getClass().getResource("/hand-16px.png")));
                    ((DefaultModalGraphMouse<RDFNode, Statement>) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.TRANSFORMING);
                } else {
                    btnEdit.setIcon(btnEdit.getSelectedIcon());
                    ((DefaultModalGraphMouse<RDFNode, Statement>) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.PICKING);
                }
            }
        });
        menuBar.add(btnEdit);

        JLabel lblSep5 = new JLabel("");
        lblSep5.setPreferredSize(new Dimension(10, 20));
        lblSep5.setMinimumSize(new Dimension(10, 20));
        lblSep5.setMaximumSize(new Dimension(10, 20));
        menuBar.add(lblSep5);

        final ZoomBar zoomBar = new ZoomBar();
        zoomBar.setBackground(menuBar.getBackground());
        zoomBar.setPreferredSize(new Dimension(300, 30));
        zoomBar.setMinimumSize(new Dimension(300, 41));
        zoomBar.setMaximumSize(new Dimension(300, 32767));
        zoomBar.addListenerChanged(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName() == "zoomValue") {
                            float zoom = (int) evt.getNewValue() / 100f;
                            try {
                                if (zoom > 1)
                                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(zoom, zoom, vv.getCenter());
                                else
                                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(zoom, zoom, vv.getCenter());
                            } catch (Exception e) {

                            }
                        }
                    }
                }
        );
        menuBar.add(zoomBar);
        setJMenuBar(menuBar);
        vv.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
                if (zoom > 1) {
                    zoomBar.setZoomValue((int) (zoom * 100));
                } else {
                    zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                    if (zoom < 1)
                        zoomBar.setZoomValue((int) (zoom * 100));
                }
            }
        });

        final GraphZoomScrollPane graphPanel = new GraphZoomScrollPane(vv);
        graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
        GroupLayout globalPanelLayout = new GroupLayout(globalPanel);
        globalPanel.setLayout(globalPanelLayout);
        globalPanelLayout.setHorizontalGroup(
                globalPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 419, Short.MAX_VALUE)
                        .addComponent(graphPanel));
        globalPanelLayout.setVerticalGroup(
                globalPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 319, Short.MAX_VALUE)
                        .addComponent(graphPanel));

        GroupLayout layout = new GroupLayout((getContentPane()));
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));





        //for (String type : graphVisualizationPanel.getTypesNode())

        pack();

    }

    public void changeGraph(Graph<RDFNode, Statement> graph){
        graphVisualizationPanel = new GraphVisualizationPanel(graph, null, null, new HashMap<String,Point>(), false);
        vv = graphVisualizationPanel.vv;
    }
    private ActionListener getActionLinkFilter() {
        if (actionLinkFilter == null) {
            actionLinkFilter = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (((JMenuItem) evt.getSource()).isSelected())
                        graphVisualizationPanel.removeEdgeTypeFilter(evt.getActionCommand());
                    else
                        graphVisualizationPanel.addEdgeTypeFilter(evt.getActionCommand());
                }
            };
        }
        return actionLinkFilter;
    }

    private PatternSearch getPatternSearch() {
        if (patternSearch == null) {
            patternSearch = new PatternSearch();
            patternSearch.setModal(true);
        }
        return patternSearch;
    }

    private ActionListener getActionNodeFilter() {
        if (actionNodeFilter == null) {
            actionNodeFilter = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    //System.out.println(evt.getActionCommand());
                    if (((JMenuItem) evt.getSource()).isSelected())
                        graphVisualizationPanel.removeNodeTypeFilter(evt.getActionCommand());
                    else
                        graphVisualizationPanel.addNodeTypeFilter(evt.getActionCommand());
                }
            };
        }
        return actionNodeFilter;
    }

    private ActionListener getActionSelectAll() {
        if (actionSelectAll == null) {
            actionSelectAll = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    JPopupMenu menu = (JPopupMenu) ((JMenuItem) evt.getSource()).getParent();
                    for (int i = 0; i < menu.getComponentCount(); i++) {
                        graphVisualizationPanel.setIgnoreRepaint(true);
                        try {
                            Component component = menu.getComponent(i);
                            if (component instanceof JCheckBoxMenuItem)
                                if (!((JCheckBoxMenuItem) component).isSelected())
                                    ((JCheckBoxMenuItem) component).doClick();
                        } finally {
                            graphVisualizationPanel.setIgnoreRepaint(false);
                            graphVisualizationPanel.repaint();
                        }
                    }
                }
            };
        }
        return actionSelectAll;
    }

    private ActionListener getActionDeselectAll() {
        if (actionDeselectAll == null) {
            actionDeselectAll = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    JPopupMenu menu = (JPopupMenu) ((JMenuItem) evt.getSource()).getParent();
                    for (int i = 0; i < menu.getComponentCount(); i++) {
                        graphVisualizationPanel.setIgnoreRepaint(true);
                        try {
                            Component component = menu.getComponent(i);
                            if (component instanceof JCheckBoxMenuItem)
                                if (((JCheckBoxMenuItem) component).isSelected())
                                    ((JCheckBoxMenuItem) component).doClick();
                        } finally {
                            graphVisualizationPanel.setIgnoreRepaint(false);
                            graphVisualizationPanel.repaint();
                        }
                    }
                }
            };
        }
        return actionDeselectAll;
    }
}
