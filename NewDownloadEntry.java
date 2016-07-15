
package downloader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Ashish Gupta
 */
public class NewDownloadEntry extends javax.swing.JFrame {
    private String In, Out;
    Graphics g;
    URL url;
    HttpURLConnection connection;
    
    public NewDownloadEntry() {
        initComponents();
        setVisible(true);
        setTitle("Add a new address");
        startDownloadButton.addActionListener(new startDownloadButtonHandler());
        BrowseButton.addActionListener(new Browser());
        startDownloadButton.setToolTipText("Click to begin Download");
        PasteButton.addActionListener(new ActionListener(){
            

            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit toolkit=Toolkit.getDefaultToolkit();
                Clipboard clipboard=toolkit.getSystemClipboard();
                try {String data=(String)clipboard.getData(DataFlavor.stringFlavor);
                URLBox.setText(data);
                } catch(Exception E){
                    System.out.println("Some error occurred");
                }
            }
        });
    }
    public void passThrough(){
        Downloading d=new Downloading(URLBox.getText(),Out);
        setVisible(false);
    }
    
    
    /* Issues */
    private String modifyURL(String A){
        if("http://".equals(A.substring(0, 6)))
                  A="http://"+A; 
        return A;       
    }
    
    private class Browser implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            In=URLBox.getText();
            if(In=="") return;
            In=In.substring(In.lastIndexOf("/")+1,In.length());
            URLBox.setText(modifyURL(URLBox.getText()));
            int retval=FileChooser.showOpenDialog(NewDownloadEntry.this);           
            //FileChooser.setVisible(true);
            if(retval==JFileChooser.APPROVE_OPTION)
            Out=FileChooser.getSelectedFile().getPath();
            
            Out=Out+'\\'+ In;
            destinationBox.setText(Out);
        }
        
    }
    private class startDownloadButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            In=URLBox.getText();
            statusDetails.setForeground(Color.GREEN);
            statusDetails.setText("Connecting");
            if(In.isEmpty()){
                
                JOptionPane.showMessageDialog(NewDownloadEntry.this,"Error 101: Empty URL" , "Invalid URL", JOptionPane.ERROR_MESSAGE);
                statusDetails.setText("Invalid URL");
                statusDetails.setForeground(Color.RED);
            } else {
                
                try{   
                    URLBox.setText(modifyURL(URLBox.getText()));           
                    url=new URL(URLBox.getText());
                    connection=(HttpURLConnection) url.openConnection();
                    if(connection.getInputStream().read()>0){ 
                        connection.disconnect();
                        passThrough();
                 
                 }
             
             
             } catch (UnknownHostException E){
                 JOptionPane.showMessageDialog(NewDownloadEntry.this,"Error 102: Connection Failed. Please make sure you are connected to a network" , "Connection Failed", JOptionPane.ERROR_MESSAGE);
             
                 statusDetails.setText("Unable to Connect");
                 statusDetails.setForeground(Color.RED);
                 System.out.println("Exception thrown");
                 E.printStackTrace();
             } catch (Exception E){
                 JOptionPane.showMessageDialog(NewDownloadEntry.this,"Error 103: Error while parsing URL" , "Invalid URL", JOptionPane.ERROR_MESSAGE);
             
                 statusDetails.setForeground(Color.RED);
                 E.printStackTrace();
                 statusDetails.setText("Error while parsing URL");
             }
            }
        }
        
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FileChooser = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        URLBox = new javax.swing.JTextField();
        startDownloadButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        statusDetails = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        destinationBox = new javax.swing.JTextField();
        BrowseButton = new javax.swing.JButton();
        PasteButton = new javax.swing.JButton();

        FileChooser.setDialogTitle("Select File");
        FileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jLabel1.setText("Enter URL:");

        URLBox.setToolTipText("Enter new Download URL here");

        startDownloadButton.setText("Start Download");
        startDownloadButton.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel2.setText("Add a new Download");

        statusDetails.setText("Please enter URL");

        jLabel3.setText("Destination:");

        BrowseButton.setText("Browse..");

        PasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pasteicon.jpg"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(URLBox, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(destinationBox, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statusDetails)
                        .addGap(114, 114, 114)
                        .addComponent(startDownloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(184, 184, 184)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(URLBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(PasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BrowseButton)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDownloadButton)
                    .addComponent(statusDetails))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BrowseButton;
    private javax.swing.JFileChooser FileChooser;
    private javax.swing.JButton PasteButton;
    private javax.swing.JTextField URLBox;
    private javax.swing.JTextField destinationBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton startDownloadButton;
    private javax.swing.JLabel statusDetails;
    // End of variables declaration//GEN-END:variables
}
