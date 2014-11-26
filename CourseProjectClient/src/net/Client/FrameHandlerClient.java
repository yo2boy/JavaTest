package net.Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FrameHandlerClient extends JFrame implements ActionListener {
    JButton buttonOpen;
    JButton buttonGet;
    JFrame frame;
    FileHandler fileHandler;
    
    public FrameHandlerClient(FileHandler fh){
    	fileHandler = fh;
    	frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Client");
		frame.setSize(400, 500);
		frame.setResizable(false);
		frame.setVisible(true);
		JPanel pane = new JPanel();
		pane.setSize(400, 500);
		frame.setContentPane(pane);
		
		buttonOpen = new JButton("Open");
		buttonOpen.setLocation(250, 250);
		buttonOpen.setSize(100, 30);
		
		buttonGet = new JButton("Get");
		buttonGet.setLocation(250, 300);
		buttonGet.setSize(100, 30);
		
		pane.add(buttonOpen);
		pane.add(buttonGet);
		
		buttonOpen.addActionListener(this);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				//Insert code that tells the server we're dead.
				frame.dispose();
				System.exit(0);
			}
		});
    }
    
    public JFrame getFrame(){
    	return frame;
    }
    
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(buttonOpen)){

			JFileChooser fileopen = new JFileChooser();
			fileopen.setAcceptAllFileFilterUsed(false);
			fileopen.setMultiSelectionEnabled(false);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", new String[] {"jpg", "jpeg", "jpe", "jif", "jfif", "jfi"});
			fileopen.addChoosableFileFilter(filter);
			int ret = fileopen.showDialog(null, "Open file");
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File file = fileopen.getSelectedFile();
				fileHandler.addMapping(file.getName(),file.getAbsolutePath());
				Client.client.updateServersOfMapping(file.getName());
			}
		}

	}
}
