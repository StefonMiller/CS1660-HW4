package com.cs1660hw4;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import com.google.cloud.storage.*;
import java.nio.file.Files;

public class FileUpload
{
   public static void main(String[] args) 
    {
        //Create GUI window
        new MainWindow();
    }

}

class MainWindow implements ActionListener
{
    //UI components
    JButton btn;
    JButton btn2;
    JFrame frame;
    JTextArea textArea;
    //List of files to upload
    ArrayList<File> uploadFiles = new ArrayList<File>();

    public MainWindow()
    {
        //Create main frame
        frame = new JFrame("Upload to hadoop");

        //Panel for file choose button
        JPanel panel1 = new JPanel();
        btn = new JButton("Add files"); // Button is a Component
        btn.addActionListener(this); 
        panel1.add(btn);

        //Panel for text area displaying files to upload
        JPanel panel2 = new JPanel();
        textArea = new JTextArea();
        Color color = panel2.getBackground ();
        textArea.setBackground(color);
        textArea.setEditable(false);
        textArea.setText("Currently uploading:\n");
        panel2.add(textArea);

        //Panel for upload button
        JPanel panel3 = new JPanel();
        btn2 = new JButton("Upload files to GCP"); // Button is a Component
        btn2.addActionListener(this); 
        panel3.add(btn2);

        // Container panel for 3 previous panels
        JPanel containerPanel = new JPanel(new GridLayout(3,1));
        containerPanel.add(panel1);
        containerPanel.add(panel2);
        containerPanel.add(panel3);

        //Make UI exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,300);

        frame.getContentPane().add(containerPanel);
        frame.setVisible(true);
    }

    /**
     * Button event handler
     * @Param e: Event registered
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(btn))
        {
            //If the user pressed the select file button, call selectFiles()
            File[] tempFiles = selectFiles();
            //Check if the user actually selected any files
            if(tempFiles != null)
            {
                //Add files to arraylist and update textarea
                uploadFiles = new ArrayList<File>(Arrays.asList(tempFiles));
                StringBuilder tempStr = new StringBuilder("Currently uploading:\n");
                for(int i = 0; i < uploadFiles.size(); i++)
                {
                    tempStr.append("• " + uploadFiles.get(i).getName() + "\n");
                }
                textArea.setText(tempStr.toString());
            }


        }
        //If the user clicked the upload button, call uploadFiles()
        else if(e.getSource().equals(btn2))
        {
            uploadFiles();
        }
        else
        {
            System.out.println("Invalid event");
        }
    }

    /**
     * Opens a JFileChooser UI element allowing the user to select multiple files
     * @return list of files selected by the user
     */
    public File[] selectFiles() 
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            return files;
        }
        return null;

    }

    /**
     * Uploads all files to GCP using the GCP client libraries
     */
    public void uploadFiles()
    {
        //Make sure there are files to upload
        if(uploadFiles.size() == 0)
        {
            JOptionPane.showMessageDialog(null, "No files specified!");
            return;
        }
        //Authenticate with the user service
        Storage storage = StorageOptions.getDefaultInstance().getService();
        //Connect to the HW4 bucket
        Bucket bucket = storage.get("dataproc-staging-us-east1-364723712673-ah1i5hyd");
        //Ensure the specified bucket exists
        if (bucket != null) {
            //Upload all files to the specified bucket
            for(File f: uploadFiles)
            {
                try
                {
                    bucket.create(f.getName(), Files.readAllBytes(f.toPath()));
                }
                catch(Exception e)
                {
                    System.out.println(e.toString());
                }
            }
            JOptionPane.showMessageDialog(null, "Files successfully uploaded!");
            textArea.setText("Currently uploading:\n");


        }
    }
}