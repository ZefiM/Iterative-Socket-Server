//Client Program for Iterative Socket Server
//Miringen Zefi / David Munera
//CNT4504

import java.util.concurrent.*;
import java.io.*;
import java.net.*;


public class Client {
	public static void main(String[] args)
	{
   String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int numsClient = 0;
        String cmd = null;
        boolean contin = true;
        boolean loop = false;
        while(contin == true)
        {
              ExecutorService executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
               System.out.print("\n" + "Enter a command you would like to run or enter exit to exit: ");
               try{ 
      			BufferedReader command = new BufferedReader(new InputStreamReader(System.in));
               cmd = command.readLine();
               if(!cmd.equals("exit"))
               {
                     System.out.print("Type number of client requests: ");
                     try{
            			BufferedReader NumClient = new BufferedReader(new InputStreamReader(System.in));
                     String clients = NumClient.readLine();
                     numsClient = Integer.parseInt(clients);
                     }catch(Exception e){
                     System.out.println(e.getMessage());
                     }
                     
            		   try {
                     int i = 0;
                     loop = true;
                     long start = 0;
                     long finish = 0;
                     long timeElapsed = 0;
                     float total = 0;
                     while(loop == true)
                     {
                     int[] times = new int[numsClient];
                     //for loop to go through the number of client requests
                     for(i = 0;i < numsClient; i++)
                     {
                        Socket sock = new Socket(hostname, port);
                        SendThread sendThread = new SendThread(sock, cmd);
                        Thread thread = new Thread(sendThread);
                        RecieveThread recieveThread = new RecieveThread(sock);
                  		Thread thread2 =new Thread(recieveThread);
                        thread.start();
                        start = System.currentTimeMillis();
                        try {
                            thread.join();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                          }
                        //start thread recieve when thread send is done  
                        thread2.start();
                        
                        try {
                            thread2.join();
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                          }
                           finish = System.currentTimeMillis(); 
                           //Divide the time to figure out the turn around time
                           timeElapsed = finish - start;
                           System.out.println("Turn Around Time: " + timeElapsed);  
                           times[i] = (int)timeElapsed;
                           total = total + times[i];                                    
                      }loop = false;
                      float avgTime = (total/numsClient);
                      System.out.println("Total Time is:" + total);
                      System.out.println("Average Turn Around Time is:" + avgTime);
                      }
                           
            		} catch (Exception e) {
                  System.out.println(e.getMessage());
                  } 
               }//end if
               else
               {
                  System.exit(1);
                  contin = false;
               }
   }catch(Exception e){System.out.println(e.getMessage());}
}
System.exit(1);
}
}
class RecieveThread implements Runnable
{
	Socket sock=null;
	BufferedReader reader=null;
   StringBuffer stringBuffer = new StringBuffer("");
	
	public RecieveThread(Socket sock) {
		this.sock = sock;
	}//end constructor
	public void run() {
		try{
		this.reader = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
       for (String line = reader.readLine(); line != null; line = reader.readLine())
           {
                System.out.println(line);
                if (!line.trim().equals("")) {
                stringBuffer.append(line);
                }
            }
      System.out.println("Request Recieved!");
		}catch(Exception e){
      System.out.println(e.getMessage());
      }
	}//end run
}//end class recievethread
class SendThread implements Runnable
{
	Socket sock=null;
	PrintWriter print=null;
   String cmd = null;
	//BufferedReader brinput=null;
	
	public SendThread(Socket sock, String cmd)
	{
		this.sock = sock;
      this.cmd = cmd;
	}//end constructor
	public void run(){
		try{
		if(sock.isConnected())
		{
			this.print = new PrintWriter(sock.getOutputStream(), true);	
			this.print.println(cmd);
         System.out.println("\n" + "New Client Request Sent!");
			this.print.flush();
      }
      }catch(Exception e){
      System.out.println(e.getMessage());
      }
	}//end run method
}//end class
