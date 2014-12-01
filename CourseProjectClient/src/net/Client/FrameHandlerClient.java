package net.Client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FrameHandlerClient extends JFrame implements ActionListener, ListSelectionListener {
	JButton buttonAdd;
	JButton buttonDownload;
	JFrame frame;
	FileHandler fileHandler;
	JList allFilesList;
	JList myFilesList;
	JScrollPane allFilesPane;
	JScrollPane myFilesPane;
	JPanel pane;

	String[] fileList = {"N/A"};

	public FrameHandlerClient(FileHandler fh){
		fileHandler = fh;
		frame = new JFrame();
		updateFrame();
	}

	public void updateLists()
	{
		int selectedIndex = -1;
		if(allFilesList != null){
			selectedIndex = allFilesList.getSelectedIndex();
		}
		String[] allFiles = fileList;
		Object[] myFiles = fileHandler.locations.keySet().toArray();
		allFilesList = new JList(allFiles);
		allFilesPane = new JScrollPane(allFilesList);
		allFilesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		allFilesPane.setBorder(BorderFactory.createLineBorder(Color.gray));
		allFilesPane.setBackground(Color.white);
		if(allFiles != null){
			allFilesList = new JList(allFiles);
			allFilesPane = new JScrollPane(allFilesList);
			allFilesList.setBorder(BorderFactory.createLineBorder(Color.black));
			allFilesList.addListSelectionListener(this);
			if(selectedIndex < allFiles.length){
				allFilesList.setSelectedIndex(selectedIndex);
			}
		}
		myFilesList = new JList(myFiles);
		myFilesPane = new JScrollPane(myFilesList);
		myFilesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		myFilesPane.setBorder(BorderFactory.createLineBorder(Color.gray));
		myFilesList.ensureIndexIsVisible(myFilesList.getSelectedIndex());
		myFilesList.setBorder(BorderFactory.createLineBorder(Color.black));
		myFilesPane.setBackground(Color.white);

		pane.add(allFilesPane);
		pane.add(myFilesPane);
		validate();
		repaint();
	}

	public void updateFrame(){
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Client");
		frame.setSize(400, 500);
		//frame.setResizable(false);
		frame.setVisible(true);
		pane = new JPanel();
		pane.setSize(400, 500);
		frame.setContentPane(pane);
		pane.setLayout(new BasicLayout());
		buttonAdd = new JButton("Add Image File");
		//buttonAdd.setLocation(250, 250);
		//buttonAdd.setSize(100, 30);


		buttonDownload = new JButton("Download");
		//buttonDownload.setLocation(250, 300);
		//buttonDownload.setSize(100, 30);

		updateLists();
		resize();
		frame.setPreferredSize(new Dimension(400,500));

		pane.add(buttonAdd);
		pane.add(buttonDownload);
		frame.pack();
		buttonDownload.setEnabled(false);
		buttonAdd.setEnabled(false);
		buttonAdd.setEnabled(true);
		buttonAdd.addActionListener(this);
		buttonDownload.addActionListener(this);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				//Insert code that tells the server we're dead.
				frame.dispose();
				System.exit(0);
			}
		});
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				resize();     
			}

			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void resize(){
		buttonAdd.setBounds(pane.getWidth()-150, (int) (pane.getHeight()*0.6) + 40, 120, 30);
		buttonDownload.setBounds(pane.getWidth()-150, (int) (pane.getHeight()*0.6) - 25, 120, 30);
		allFilesPane.setVisible(true);
		allFilesPane.setBounds(25, 25,pane.getWidth() - 200,(int) (pane.getHeight()*0.6));
		myFilesPane.setVisible(true);
		myFilesPane.setBounds(25, (int) (pane.getHeight()*0.6) + 25,pane.getWidth() - 200,(int) (pane.getHeight()*0.4 - 50));
	}

	public JFrame getFrame(){
		return frame;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(buttonAdd)){

			JFileChooser fileopen = new JFileChooser();
			fileopen.setAcceptAllFileFilterUsed(false);
			fileopen.setMultiSelectionEnabled(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"});
			fileopen.addChoosableFileFilter(filter);
			int ret = fileopen.showOpenDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File file = fileopen.getSelectedFile();
				fileHandler.addMapping(file.getName(),file.getAbsolutePath());
				updateFrame();
				//Quickly implement file transfer
				Client.client.sendFile(file);
			}
		}
		else if(e.getSource().equals(buttonDownload)){

			JFileChooser fileopen = new JFileChooser();
			fileopen.setAcceptAllFileFilterUsed(false);
			fileopen.setMultiSelectionEnabled(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"});
			fileopen.addChoosableFileFilter(filter);
			int ret = fileopen.showSaveDialog(null);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File file = fileopen.getSelectedFile();
				fileHandler.addMapping(file.getName(),file.getAbsolutePath());
				updateFrame();
				//Quickly implement file transfer
				//Client.client.sendFile(file);
			}
		}
	}

	class BasicLayout implements LayoutManager
	{

		public void addLayoutComponent(String arg0, Component arg1) {
			// TODO Auto-generated method stub

		}

		public void layoutContainer(Container arg0) {
			// TODO Auto-generated method stub

		}

		/* Required by LayoutManager. */
		public Dimension preferredLayoutSize(Container parent) {
			return parent.getPreferredSize();
		}

		/* Required by LayoutManager. */
		public Dimension minimumLayoutSize(Container parent) {
			return parent.getMinimumSize();
		}

		public void removeLayoutComponent(Component arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void valueChanged(ListSelectionEvent arg0) {
		if(allFilesList != null && allFilesList.getSelectedIndex() != -1){
			buttonDownload.setEnabled(true);
		}
		else{
			buttonDownload.setEnabled(false);
		}
	}
}
