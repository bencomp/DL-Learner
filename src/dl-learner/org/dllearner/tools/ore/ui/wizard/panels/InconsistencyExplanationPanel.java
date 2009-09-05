package org.dllearner.tools.ore.ui.wizard.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.dllearner.tools.ore.explanation.Explanation;
import org.dllearner.tools.ore.ui.ExplanationTable;
import org.dllearner.tools.ore.ui.ExplanationTablePanel;
import org.dllearner.tools.ore.ui.RepairPlanPanel;
import org.semanticweb.owl.apibinding.OWLManager;

public class InconsistencyExplanationPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9206626647697013786L;

	private JSplitPane statsSplitPane;
	
	private JScrollPane explanationsScrollPane;
	private JComponent explanationsPanel;
	private JPanel buttonExplanationsPanel;
	private JPanel buttonPanel;
	private ButtonGroup explanationType;
	private JRadioButton regularButton;
	private JRadioButton laconicButton;

	public InconsistencyExplanationPanel() {
	
		setLayout(new BorderLayout());

		Dimension minimumSize = new Dimension(400, 400);


		explanationsPanel = new Box(1);

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(explanationsPanel, BorderLayout.NORTH);
		explanationsScrollPane = new JScrollPane(pan);
		explanationsScrollPane.setPreferredSize(minimumSize);
		explanationsScrollPane.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));
		explanationsScrollPane.getViewport().setOpaque(false);
		explanationsScrollPane.getViewport().setBackground(null);
		explanationsScrollPane.setOpaque(false);

		regularButton = new JRadioButton("regular", true);
		regularButton.setActionCommand("regular");

		laconicButton = new JRadioButton("laconic");
		laconicButton.setActionCommand("laconic");

		explanationType = new ButtonGroup();
		explanationType.add(regularButton);
		explanationType.add(laconicButton);
		buttonPanel = new JPanel();
		buttonPanel.add(regularButton);
		buttonPanel.add(laconicButton);

		buttonExplanationsPanel = new JPanel();
		buttonExplanationsPanel.setLayout(new BorderLayout());
		buttonExplanationsPanel
				.add(explanationsScrollPane, BorderLayout.CENTER);
		buttonExplanationsPanel.add(buttonPanel, BorderLayout.NORTH);

		statsSplitPane = new JSplitPane(0);
		statsSplitPane.setResizeWeight(1.0D);
		statsSplitPane.setTopComponent(buttonExplanationsPanel);
		
		//repair panel
		JPanel repairPanelHolder = new JPanel();
		repairPanelHolder.setOpaque(false);
		repairPanelHolder.setLayout(new BorderLayout());
		repairPanelHolder.add(new JLabel("Repair plan"), BorderLayout.NORTH);
		RepairPlanPanel repairPanel = new RepairPlanPanel(); 
		repairPanelHolder.add(repairPanel);
		
		
		statsSplitPane.setBottomComponent(repairPanelHolder);
		
		statsSplitPane.setBorder(null);
		statsSplitPane.setDividerLocation(500);
		statsSplitPane.setOneTouchExpandable(true);
		
		add(statsSplitPane);
	
	}
	
	public void clearExplanationsPanel(){
		explanationsPanel.removeAll();
	}
	
	public void addExplanation(Explanation explanation, int counter){
		ExplanationTable expTable = new ExplanationTable(explanation, OWLManager.createOWLOntologyManager().getOWLDataFactory().getOWLThing());
		explanationsPanel.add(new ExplanationTablePanel(expTable, counter));
		explanationsPanel.add(Box.createVerticalStrut(10));

	}
	
	public void addActionListeners(ActionListener aL){
		regularButton.addActionListener(aL);
		laconicButton.addActionListener(aL);
	}
	
}
