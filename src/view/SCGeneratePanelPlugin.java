package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import model.Model;

public class SCGeneratePanelPlugin extends ViewShrinkPanelPlugin implements
		ActionListener {
	
	private final JButton btnGenerateIC = new JButton("Generate!");
	private final JButton btnGeneratePC = new JButton("Generate!");
	private final JButton btnBackStartOver = new JButton("Back");
	private final JPanel panel = new JPanel();
	private final JPanel pNamesPanel = new JPanel();
	private final JPanel initialComplexPanel = new JPanel();
	private final JPanel chromaticPanel = new JPanel();
	private final JPanel protocolComplexPanel = new JPanel();
	private final JComboBox<String> communicationModelOptions = new JComboBox<String>();
	private final JComboBox<String> communicationModelSubOptions = new JComboBox<String>();
	private final JLabel lbModelOptions = new JLabel("Select communication model");
	private final JLabel lbModelSubOptions = new JLabel("Select specific model");
	private final JRadioButton rbOneProcess = new JRadioButton("1");
	private final JRadioButton rbTwoProcess = new JRadioButton("2");
	private final JRadioButton rbThreeProcess = new JRadioButton("3");
	private final JRadioButton rbChromatic =  new JRadioButton("Chromatic");
	private final JRadioButton rbNonChromatic =  new JRadioButton("Non-chromatic");
	private List<JTextField> txtProcessNames ;
	private Model model;
	
	public SCGeneratePanelPlugin(Model m){
		this.model = m;
		
		rbOneProcess.setActionCommand("rbProcess_1");
		rbTwoProcess.setActionCommand("rbProcess_2");
		rbThreeProcess.setActionCommand("rbProcess_3");
		
		ButtonGroup group = new ButtonGroup();
		group.add(rbOneProcess);
		group.add(rbTwoProcess);
		group.add(rbThreeProcess);
		
		rbOneProcess.addActionListener(this);
		rbTwoProcess.addActionListener(this);
		rbThreeProcess.addActionListener(this);
		
		rbChromatic.setActionCommand("c");
		rbNonChromatic.setActionCommand("nc");
		
		ButtonGroup chromGroup = new ButtonGroup();
		chromGroup.add(rbChromatic);
		chromGroup.add(rbNonChromatic);
		
		chromaticPanel.add(rbChromatic);
		chromaticPanel.add(rbNonChromatic);
		chromaticPanel.setLayout(new BoxLayout(chromaticPanel,BoxLayout.Y_AXIS));
		chromaticPanel.setBorder(BorderFactory.createTitledBorder("Choose kind of complex"));
		
		setInitialPosition(SHRINKER_LEFT);
		
		btnGenerateIC.setActionCommand("generateIC");
		btnGenerateIC.addActionListener(this);
		
		pNamesPanel.setBorder(BorderFactory.createTitledBorder("Enter processes names"));
		pNamesPanel.setToolTipText("Enter processes names");
		pNamesPanel.setLayout(new BoxLayout(pNamesPanel,BoxLayout.Y_AXIS));
		
		initialComplexPanel.setLayout(new BoxLayout(initialComplexPanel,BoxLayout.Y_AXIS));
		initialComplexPanel.add(chromaticPanel);
		initialComplexPanel.add(rbOneProcess);
		initialComplexPanel.add(rbTwoProcess);
		initialComplexPanel.add(rbThreeProcess);
		initialComplexPanel.add(pNamesPanel);
		initialComplexPanel.setBorder(BorderFactory.createTitledBorder("Initial complex"));
		
		showInitialComplexControls();
		
		communicationModelOptions.addActionListener(this);
		communicationModelOptions.setActionCommand("modelOptions");
		communicationModelSubOptions.addActionListener(this);
		communicationModelSubOptions.setActionCommand("subModelOptions");
		protocolComplexPanel.setLayout(new BoxLayout(protocolComplexPanel,BoxLayout.Y_AXIS));
		protocolComplexPanel.setBorder(BorderFactory.createTitledBorder("Choose communication model"));
		btnGeneratePC.setActionCommand("generatePC");
		btnGeneratePC.addActionListener(this);
		btnGeneratePC.setText("Generate!");
		btnBackStartOver.setText("Back");
		btnBackStartOver.addActionListener(this);
		btnBackStartOver.setActionCommand("back");
		
		panel.setBorder(BorderFactory.createTitledBorder("Simplicial Complex generator"));
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		getShrinkPanel().add(panel);
	}

	public PluginInfo getPluginInfo() {
		return new PluginInfo("Simplicial Complex Panel");
	}
	
	public void install(Controller c) throws Exception{
		super.install(c);
//		c.getPlugin(Scene.class)
//			.getContentComponent()
//			.addChild(sgc);
	}
	
	public void uninstall(Controller c) throws Exception {
//		c.getPlugin(Scene.class)
//			.getContentComponent()
//		    .removeChild(sgc);
		super.uninstall(c);
	}
	
	private void enableProcessNamesButtons(int n){
		
		pNamesPanel.setVisible(true);
		pNamesPanel.removeAll();
				
		txtProcessNames = new ArrayList<JTextField>(n);
		for (int i = 0; i<n ; i++){
			JLabel lbN = new JLabel("Process " + i + "'s name");
			JTextField txtN = new JTextField(Integer.toString(i));
			txtN.setSize(10,45);
			txtProcessNames.add(txtN);
			pNamesPanel.add(lbN);
			pNamesPanel.add(txtN);
		}
		pNamesPanel.add(btnGenerateIC);
	}
	
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if (command.startsWith("rbProcess")){
			enableProcessNamesButtons(Integer.parseInt(command.substring(command.length() - 1)));
		}
		else if (command == "generateIC"){
			model.setInitialComplex(null);
			model.setProtocolComplex(null);
			
			int n = txtProcessNames.size();
			Set<String> pNames = new LinkedHashSet<String>(n);
			
			Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");
			
			for (JTextField txtPName : txtProcessNames){
				String text = txtPName.getText();
				Matcher m = p.matcher(text);
				if (text.length() == 1 && 
						m.matches() && 
						!pNames.contains(text)){
					pNames.add(text);
				}
				else{
					//JOptionPane.showMessageDialog(frame, "Process names must be non-empty, alphanumeric, one-character and unique.");
					return;
				}
			}
			List<String> lpNames = new ArrayList<String>(n);
			lpNames.addAll(pNames);
			//model.createInitialComplex(n, lpNames);
			initialComplexPanel.setVisible(false);
			showProtocolComplexControls();
		}else if (command == "generatePC"){
			model.executeRound();
			protocolComplexPanel.removeAll();
			protocolComplexPanel.add(btnGeneratePC);
			protocolComplexPanel.add(btnBackStartOver);
			btnGeneratePC.setText("Execute next round");
			btnBackStartOver.setText("Start over");
		}
		else if (command=="back"){
			initialComplexPanel.setVisible(true);
		}
		else if (command=="start"){
			protocolComplexPanel.setVisible(false);
			showInitialComplexControls();
		}
		else if (command == "modelOptions"){
			JComboBox<String> box = (JComboBox<String>)e.getSource();
			String item = (String) box.getSelectedItem();
			displayModelSubOptions(item);
		}
	}

	private void displayModelSubOptions(String selectedModel) {
//		Map<String, List<String>> modelOptions = model.getAvailableCommunicationModels();
//		List<String> subOptions = modelOptions.get(selectedModel);
//		
//		String[] subOptionsArr = new String[subOptions.size()];
//		subOptions.toArray(subOptionsArr);
//		
//		communicationModelSubOptions.setModel(new DefaultComboBoxModel<String>(subOptionsArr));
		
	}

	protected void displayModelOptions() {
//		Map<String, List<String>> modelOptions = model.getAvailableCommunicationModels();
//		List<String> options = new ArrayList<String>(modelOptions.keySet());
//		String[] optionsArr = new String[options.size()];
//		options.toArray(optionsArr);
//		communicationModelOptions.setModel(new DefaultComboBoxModel<String>(optionsArr));
//		
//		displayModelSubOptions(options.get(0));
//		
//		protocolComplexPanel.setVisible(true);
//		panel.setBorder(BorderFactory.createTitledBorder("Protocol complex"));
	}
	
	protected void showProtocolComplexControls(){
		panel.remove(protocolComplexPanel);
		communicationModelOptions.removeAll();

		displayModelOptions();
		
		protocolComplexPanel.add(lbModelOptions);
		protocolComplexPanel.add(communicationModelOptions);
		protocolComplexPanel.add(lbModelSubOptions);
		protocolComplexPanel.add(communicationModelSubOptions);
		protocolComplexPanel.add(btnGeneratePC);
		protocolComplexPanel.add(btnBackStartOver);
		protocolComplexPanel.setVisible(true);
		panel.add(protocolComplexPanel);
	}
	
	protected void showInitialComplexControls(){
		panel.remove(initialComplexPanel);
		initialComplexPanel.setVisible(true);
		pNamesPanel.setVisible(false);
		panel.add(chromaticPanel);
		panel.add(initialComplexPanel);
	}
}
