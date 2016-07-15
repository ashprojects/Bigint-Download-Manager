
package downloader;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.*;

/**
 *
 * @developer Ashish Gupta
 */
public class Downloading extends JFrame {
    private String inputU,outputU;
    private String tempO;
    private String incomingSize;
    private String ElapsedTime;
    private Thread timerThread,resThread;
    private Thread dwnldThread;
    private DownloadModule dn;  
    private long Elapsedtime=0;
    private boolean hasStopped = false;
    private boolean hasPaused=false;
    private boolean allOK=false;
    private long startbyte=0;
    private int currProgress=0;
    
    public Downloading(String iURL, String oURL) {
        Elapsedtime=0;
     //   setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        inputU=iURL;
        outputU=oURL+".bid";
        tempO=oURL;
        initComponents();
        PauseButton.setEnabled(true);
        
        ExitButton.setVisible(false);
        setTitle(String.format("Downloading %s ",iURL));
        setVisible(true);

        urlText.setText(iURL);
        ProgressBar.setStringPainted(true);
        StopButton.addActionListener(new StopHandler());
        PauseButton.addActionListener(new PauseButtonHandler());
        dn=new DownloadModule();
        dwnldThread=new Thread(dn);
        dwnldThread.start();
        
        
    }
    
    
    private class DownloadModule implements Runnable{
        
        Timer t1;
        private final int BUFFER_SIZE=4096;
        private final int CONNECT_TIMEOUT=5000;
        public DownloadModule(){
     //       System.out.println("Thread Started: "+this.toString());
        
        
        }
        
       
        private String ParseSize(long B){
            long GB=0,MB=0,KB=0;
            String fixK,fixM,fixG;
            boolean hasGB=false,hasMB=false,hasKB=false;
            
            if(B>=1024){
                    KB=(int)(B>>10);
                    hasKB=true;                   
                    B%=1024;
            }
            if(KB>=1024){
                hasMB=true;
                MB=(int)(KB>>10);
                KB%=1024;
            }
            if(MB>=1024){
                hasGB=true;
                GB=(int)(MB>>10);
                MB%=1024;
            }
            if(KB<100.0)
                fixK="0"+(int)(KB/10.24);
            else
                fixK=""+(int)(KB/10.24);
            if(MB<100.0)
                fixM="0"+(int)(MB/10.24);
            else
                fixM=""+(int)(MB/10.24);
            if(hasGB)
                return String.format(GB+"."+ fixM + " GB");
            else if(hasMB)
                return String.format(MB+"."+ fixK + " MB");
            else 
                return String.format(KB+"."+(int)(B/10.24) + " KB");
        }
        
        public void dresume(){
            resThread=new Thread(this);
            resThread.start();
        }
        
        public void startDownload()throws IOException{
            BufferedInputStream in=null;
           // FileOutputStream fout=null;
            
            RandomAccessFile ofile;
            try{                
                int check;
                long completed=0;
                byte[] buffer;
                addToConsole("Connecting to Server...");
                URL u=new URL(inputU);
                HttpURLConnection connection=(HttpURLConnection) u.openConnection();                                
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(CONNECT_TIMEOUT);
                ofile=new RandomAccessFile(outputU,"rw");
                ofile.seek(startbyte);
                setTitle("Downloading "+u.getFile());
                in=new BufferedInputStream(connection.getInputStream());
                timerThread=new Thread(new Timer());
                long size=connection.getContentLengthLong();
                String ParsedTotSize=ParseSize(size);
                SizeLabel.setText(ParseSize(size));               
                long startTime=System.nanoTime();
                long initsize=ofile.length();
                String speed="";
                
                StopButton.setEnabled(true);
                buffer=new byte[BUFFER_SIZE];
                System.out.println("Started Timer");
                timerThread.start();
                ProgressBar.setValue(currProgress);
                in.skip(startbyte);
                while((check=in.read(buffer))!=-1&&!hasStopped&&!hasPaused){                   
                    if(hasPaused){
                      ElapsedTime=timeremainingLabel.getText();                         
                      return;
                    }
                    addToConsole("Read: "+(check>>10) +" KBs");
                    ofile.write(buffer, 0, check);                     
                    startbyte+=check;
                    completed=ofile.length();   
                    if(System.nanoTime()-startTime>=1000000000){
                        speed=""+ParseSize(ofile.length()-initsize)+"/s";
                        initsize=ofile.length();
                        startTime=System.nanoTime();
                    }                 
                    transLabel.setText(ParseSize(completed)+" of "+ParsedTotSize+ " at " + speed);
                 //   addToConsole(" Read "+check+"bytes " + "done with " + completed +" bytes");
                    currProgress=(int)(completed*100/size);
                    ProgressBar.setValue(currProgress);
                }
                
                System.out.println("outside loop");
                if(ofile.length()==size){
                    System.out.println("ALl OK CHanged");
                    allOK=true;
                    rename();
                //    new File(outputU).renameTo(new File(outputU.substring(0,outputU.lastIndexOf('.')-1)));
                }
                in.close();
                ElapsedTime=timeremainingLabel.getText();              
                incomingSize=ParseSize(ofile.length());
                //hasStopped=true;               
                
            } catch (UnknownHostException E){
                SizeLabel.setText("-");
                addToConsole("Unable to connect."); 
                
             // E.printStackTrace();
               
            } catch (ConnectException E){
                addToConsole("Connection Timed Out");
            }
            
                catch (Exception E){
                E.printStackTrace();
                
            } finally {                             
                ExitButton.setVisible(true);                
                
            }   
            System.out.println("Thread ended: "+this.toString());
        }
        
        public void run(){
            try{
                System.out.println("Thread Started: "+this.toString());
            startDownload();
                    if(allOK){
                    System.out.println("Download Completed Successfully");
                    Biginttext.setForeground(Color.GREEN);
                    DownloadCompletedBox comp=new DownloadCompletedBox(inputU,outputU.substring(0,outputU.lastIndexOf('.')),incomingSize,ElapsedTime);
                    setVisible(false);
                }
            }
            catch(Exception E){
                E.printStackTrace();
            }
            }
        
    }
    
    private class Timer implements Runnable{
        private long time=0;
        private long donewith;
        public Timer(){
            
        }
        //@Override
        private String toProp(long sec){  
        boolean hashrs=false;
        boolean hasmins=false;
        long min=0,hour=0;
        while(sec>=60){
            hasmins=true;
            sec/=60;
            min++;
        }
        while(min>=60){
            min/=60;
            hashrs=true;
            hour++;
        }
        if(!hasmins)
            return String.format("%d seconds", sec);
        else if(!hashrs)
            return String.format("%d minutes and %d seconds", min,sec);
        else
            return String.format("%d hour(s) %d minute(s) and %d seconds", hour,min,sec);
    }
        
        public void run(){
            
            time=System.nanoTime();
           // timeremainingLabel.setText("Not");
            while(!hasStopped&&!hasPaused){            
                donewith=(Elapsedtime+System.nanoTime()-time)/1000000000;
                timeremainingLabel.setText(toProp(donewith));
                Elapsedtime=donewith;
            }
            }
        
    }  
     
    
    
    private class StopHandler implements ActionListener{        
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
           // dwnldThread.
            hasStopped=true;
            File todelete=new File(outputU+".bid");
            todelete.delete();
            StopButton.setEnabled(false);
            ProgressBar.setValue(0);
            setTitle("Download Stopped");
            Biginttext.setForeground(new Color(255,0,0));
            ExitButton.setVisible(true);
        }
    }
    
    private class ExitButtonHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            CloseMe();
        }
        
    }
    
    private class PauseButtonHandler implements ActionListener{
/*
        dn = new DownloadModule(inputU,outputU);
        dwnldThread=new Thread(dn);
        dwnldThread.start();
        
        */
        
        @Override
        public void actionPerformed(ActionEvent e) {
          if(!hasPaused){ //Previouly running
              hasPaused=true;
              
              PauseButton.setText("Resume");   
               addToConsole("Download Paused at "+currProgress+ "%");                          
              
          } else {
              hasPaused=false;
              dn.dresume();
              PauseButton.setText("Pause");            
              addToConsole("Download resumed!");
          }
        }
        
    }
    
    
    private void rename(){
       /* File newFile,oldFile;
        newFile=new File(tempO);
        oldFile=new File(outputU);
        System.out.println("inside rename folder");
       if (!oldFile.renameTo(newFile)){
           System.out.println("Unable to rename!");
           tempO+='1';
           rename();
       }*/
    }
    
    public void addToConsole(String a){
        ConsoleArea.setText(ConsoleArea.getText()+"\n"+a);
    }
    public int returnCurrentProg(){
        return ProgressBar.getValue();
    }    
    private void CloseMe(){
        super.dispose();
    }
    public long getStartByte(){
        return startbyte;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ProgressBar = new javax.swing.JProgressBar();
        URL_ = new javax.swing.JLabel();
        StopButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        timeremainingLabel = new javax.swing.JLabel();
        SizeLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ConsoleArea = new javax.swing.JTextArea();
        urlText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ExitButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        transLabel = new javax.swing.JLabel();
        Biginttext = new javax.swing.JLabel();
        PauseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        URL_.setText("URL:");

        StopButton.setText("Stop");
        StopButton.setEnabled(false);

        jLabel2.setText("Elapsed Time:");

        timeremainingLabel.setText("--:--");

        SizeLabel.setText("-");
        SizeLabel.setToolTipText("");

        ConsoleArea.setColumns(20);
        ConsoleArea.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        ConsoleArea.setRows(5);
        jScrollPane1.setViewportView(ConsoleArea);

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel1.setText("Logs");

        ExitButton.setText("Exit");

        jLabel3.setText("Transferred: ");

        transLabel.setText("0");

        Biginttext.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        Biginttext.setForeground(new java.awt.Color(255, 51, 0));
        Biginttext.setText("BigInt Downloader");

        PauseButton.setText("Pause");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(115, 115, 115)
                                .addComponent(StopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(PauseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ExitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(URL_)
                                .addGap(18, 18, 18)
                                .addComponent(urlText, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(transLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(timeremainingLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(32, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Biginttext)
                .addGap(181, 181, 181))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Biginttext)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlText, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(URL_))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(timeremainingLabel)
                    .addComponent(SizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(transLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StopButton)
                    .addComponent(ExitButton)
                    .addComponent(PauseButton))
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Biginttext;
    private javax.swing.JTextArea ConsoleArea;
    private javax.swing.JButton ExitButton;
    private javax.swing.JButton PauseButton;
    private javax.swing.JProgressBar ProgressBar;
    private javax.swing.JLabel SizeLabel;
    private javax.swing.JButton StopButton;
    private javax.swing.JLabel URL_;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel timeremainingLabel;
    private javax.swing.JLabel transLabel;
    private javax.swing.JTextField urlText;
    // End of variables declaration//GEN-END:variables
}
