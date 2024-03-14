
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** Network class
 *
 * @author Kerly Titus
 */
public class Network extends Thread {
    
    private static int maxNbPackets;                           /* Maximum number of simultaneous transactions handled by the network buffer */
    private static int inputIndexClient, inputIndexServer, outputIndexServer, outputIndexClient; /* Network buffer indices for accessing the input buffer (inputIndexClient, outputIndexServer) and output buffer (inputIndexServer, outputIndexClient) */
    private static String clientIP;                            /* IP number of the client application*/
    private static String serverIP;                            /* IP number of the server application */
    private static int portID;                                 /* Port ID of the client application */
    private static String clientConnectionStatus;              /* Client connection status - connected, disconnected, idle */
    private static String serverConnectionStatus;              /* Server connection status - connected, disconnected, idle */
    private static boolean isSendingClientFinished; // new variable to check if sending client is finished
    private static Transactions inComingPacket[];              /* Incoming network buffer */
    private static Transactions outGoingPacket[];              /* Outgoing network buffer */
    private static String inBufferStatus, outBufferStatus;     /* Current status of the network buffers - normal, full, empty */
    private static String networkStatus;                       /* Network status - active, inactive */
     // Semaphores for the incoming and outgoing buffers
     private static Semaphore inBufferS = new Semaphore(1);//Gives access to IN buffer
     private static Semaphore inBufferEmpty ;
     private static Semaphore inBufferFull  = new Semaphore(0);//Starts at 0 because buffer starts with no elements;
     
     private static Semaphore outBufferS = new Semaphore(1);
     private static Semaphore outBufferEmpty;
     private static Semaphore outBufferFull  = new Semaphore(0);//Starts at 0 because buffer starts with no elements
     private static boolean serversFinished;
    /** 
     * Constructor of the Network class
     * 
     * @return 
     * @param
     */
     Network( )
      { 
    	 int i;  
        
         //System.out.println("\n Activating the network ...");
         clientIP = "192.168.2.0";
         serverIP = "216.120.40.10";
         clientConnectionStatus = "idle";
         serverConnectionStatus = "idle";
         portID = 0;
         maxNbPackets = 10;
         outBufferEmpty = new Semaphore(maxNbPackets); //Starts at 10 because buffer starts empty; therefore,it can take 10 elements before it is empty
         inBufferEmpty = new Semaphore(maxNbPackets); //Starts at 10 because buffer starts empty; therefore,it can take 10 elements before it is empty
         /*SYNCHRONIZATION IS USING BUSY-WAITING. NEED TO BLOCK A THRED WHEN A BUFFER IS FULL OR EMPTY */
         inComingPacket = new Transactions[maxNbPackets];
         outGoingPacket = new Transactions[maxNbPackets];
         for (i=0; i < maxNbPackets; i++)
         {   inComingPacket[i] = new Transactions();
             outGoingPacket[i] = new Transactions();
         }
         inBufferStatus = "empty";
         outBufferStatus = "empty";
         inputIndexClient = 0;
         inputIndexServer = 0;
         outputIndexServer = 0;
         outputIndexClient = 0;
         isSendingClientFinished = false;
         networkStatus = "active";
         serversFinished=false;
      }     

//region Getters and Setters

      // New method to keep track when sending client is finished and let the servers know
    public static void setSendingClientFinished(boolean isSendingClientFinished) {
        inBufferFull.release();
        Network.isSendingClientFinished = isSendingClientFinished;
    }
    public static boolean isSendingClientFinished() {
        return isSendingClientFinished;
    }
     /** 
      * Accessor method of Network class
     * 
     * @return clientIP
     * @param
     */
     public static String getClientIP()
     {
         return clientIP;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param cip
     */
     public static void setClientIP(String cip)
     { 
         clientIP = cip;
     }
    
    /**
     *  Accessor method of Network class
     * 
     * @return serverIP
     * @param
     */
     public static String getServerIP()
     {
         return serverIP;
     }
                          
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param sip
     */
     public static void setServerIP(String sip)
     { 
         serverIP = sip;
     }
         
    /**
     *  Accessor method of Network class
     * 
     * @return clientConnectionStatus
     * @param
     */
     public static String getClientConnectionStatus()
     {
         return clientConnectionStatus;
     }
                          
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param connectStatus
     */
     public static void setClientConnectionStatus(String connectStatus)
     { 
         clientConnectionStatus = connectStatus;
     }
         
    /**
     *  Accessor method of Network class
     * 
     * @return serverConnectionStatus
     * @param
     */
     public static String getServerConnectionStatus()
     {
         return serverConnectionStatus;
     }
                          
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param connectStatus
     */
     public static void setServerConnectionStatus(String connectStatus)
     { 
         serverConnectionStatus = connectStatus;
     } 
         
    /**
     *  Accessor method of Network class
     * 
     * @return portID
     * @param
     */
     public static int getPortID()
     {
         return portID;
     }
     
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param pid
     */
     public static void setPortID(int pid)
     { 
         portID = pid;
     }

    /**
     *  Accessor method of Netowrk class
     * 
     * @return inBufferStatus
     * @param
     */
     public static String getInBufferStatus()
     {
         return inBufferStatus;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param inBufStatus
     */
     public static void setInBufferStatus(String inBufStatus)
     { 
         inBufferStatus = inBufStatus;
     }
         
    /**
     *  Accessor method of Netowrk class
     * 
     * @return outBufferStatus
     * @param
     */
     public static String getOutBufferStatus()
     {
         return outBufferStatus;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param outBufStatus
     */
     public static void setOutBufferStatus(String outBufStatus)
     { 
         outBufferStatus = outBufStatus;
     }

    /**
     *  Accessor method of Netowrk class
     * 
     * @return networkStatus
     * @param
     */
     public static String getNetworkStatus()
     {
         return networkStatus;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param netStatus
     */
     public static void setNetworkStatus(String netStatus)
     { 
         networkStatus = netStatus;
     }
         
    /**
     *  Accessor method of Netowrk class
     * 
     * @return inputIndexClient
     * @param
     */
     public static int getinputIndexClient()
     {
         return inputIndexClient;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param i1
     */
     public static void setinputIndexClient(int i1)
     { 
         inputIndexClient = i1;
     }
         
     /**
      *  Accessor method of Netowrk class
     * 
     * @return inputIndexServer
     * @param
     */
     public static int getinputIndexServer()
     {
         return inputIndexServer;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param i2
     */
     public static void setinputIndexServer(int i2)
     { 
         inputIndexServer = i2;
     }     
         
    /**
     *  Accessor method of Netowrk class
     * 
     * @return outputIndexServer
     * @param
     */
     public static int getoutputIndexServer()
     {
         return outputIndexServer;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param o1
     */
     public static void setoutputIndexServer(int o1)
     { 
         outputIndexServer = o1;
     }
         
     /**
      *  Accessor method of Netowrk class
     * 
     * @return outputIndexClient
     * @param
     */
     public static int getoutputIndexClient()
     {
         return outputIndexClient;
     }
         
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param o2
     */
     public static void setoutputIndexClient(int o2)
     { 
         outputIndexClient = o2;
     }

	 /**
	 *  Accessor method of Netowrk class
	 * 
	 * @return maxNbPackets
	 * @param
	 */
	 public static int getMaxNbPackets()
	 {
	     return maxNbPackets;
	 }
	 
    /**
     *  Mutator method of Network class
     * 
     * @return 
     * @param maxPackets
     */
     public static void setMaxNbPackets(int maxPackets)
     { 
         maxNbPackets = maxPackets;
     }
     public static boolean getServersFinished() {
         return serversFinished;
     }
     public static void setServersFinished(boolean serversFinished) {
         Network.serversFinished = serversFinished;
         //There is possible some thread stuck waiting for more transactions to be 
     }
//endregion


    /**
     *  Transmitting the transactions from the client to the server through the network 
     *  
     * @return
     * @param inPacket transaction transferred from the client
     * 
     */
        public static boolean send(Transactions inPacket)
        {
        	try{
                //Implementing semaphore for InBufffer
                  inBufferEmpty.acquire(); // Only access if the buffer is NOT full
            	  inBufferS.acquire(); //Allows access to only one thread at a time
        		  inComingPacket[inputIndexClient].setAccountNumber(inPacket.getAccountNumber());
        		  inComingPacket[inputIndexClient].setOperationType(inPacket.getOperationType());
        		  inComingPacket[inputIndexClient].setTransactionAmount(inPacket.getTransactionAmount());
        		  inComingPacket[inputIndexClient].setTransactionBalance(inPacket.getTransactionBalance());
        		  inComingPacket[inputIndexClient].setTransactionError(inPacket.getTransactionError());
        		  inComingPacket[inputIndexClient].setTransactionStatus("transferred");
            
        		// System.out.println("\n DEBUG : Network.send() - index inputIndexClient " + inputIndexClient); 
        		// System.out.println("\n DEBUG : Network.send() - account number " + inComingPacket[inputIndexClient].getAccountNumber()); 
            
           
        		  setinputIndexClient(((getinputIndexClient( ) + 1) % getMaxNbPackets ()));	/* Increment the input buffer index  for the client */
        		  /* Check if input buffer is full */
                 //Must update buffers status to keep track of wheter buffer is full. The only purpose of this is to know when to shut off servers
                  if (getinputIndexClient() == getoutputIndexServer())
        		  {	
        			  setInBufferStatus("full");
            	
        			/* //System.out.println("\n DEBUG : Network.send() - inComingBuffer status " + getInBufferStatus()); */
        		  }
        		  else 
        		  {
        			  setInBufferStatus("normal");
        		  }
                 
            }catch(InterruptedException e){
                e.printStackTrace();
            }finally{
                inBufferS.release(); // Release access to In Buffer
                inBufferFull.release();// Release semaphore since it is now not empty 
                
            }
            return true;
        }   
         
          
    public static void releaseClientReceiver(){
        setServersFinished(true);
        outBufferFull.release();
    }
      /** Transmitting the transactions from the server to the client through the network 
     * @return
     * @param outPacket updated transaction received by the client
     * 
     */
         public static boolean receive(Transactions outPacket)
        {
            try {
                outBufferFull.acquire(); // Only access if the buffer is NOT empty
                if(getServersFinished() && Network.getOutBufferStatus().equals("empty")){
                    return true;
                }
                outBufferS.acquire();// Only allows one thread to access OUT buffer
                //Test A
        		 outPacket.setAccountNumber(outGoingPacket[outputIndexClient].getAccountNumber());
        		 outPacket.setOperationType(outGoingPacket[outputIndexClient].getOperationType());
        		 outPacket.setTransactionAmount(outGoingPacket[outputIndexClient].getTransactionAmount());
        		 outPacket.setTransactionBalance(outGoingPacket[outputIndexClient].getTransactionBalance());
        		 outPacket.setTransactionError(outGoingPacket[outputIndexClient].getTransactionError());
        		 outPacket.setTransactionStatus("done");
            
        		  //System.out.println("\n DEBUG : Network.receive() - index outputIndexClient " + outputIndexClient); 
        		  //System.out.println("\n DEBUG : Network.receive() - account number " + outPacket.getAccountNumber()); 
            
        		 setoutputIndexClient(((getoutputIndexClient( ) + 1) % getMaxNbPackets( ))); /* Increment the output buffer index for the client */
        		//Must update buffers status to keep track of wheter buffer is full. The only purpose of this is to know when to shut off servers
                 if ( getoutputIndexClient( ) == getinputIndexServer( ))
        		 {	
        			 setOutBufferStatus("empty");
            
        		 }
        		 else 
        		 {
        			 setOutBufferStatus("normal"); 
        		 }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    outBufferS.release(); //Release semaphore

                    outBufferEmpty.release(); //Release semaphore after removing from out buffer because now it is not full
                }           
             return true;
        }   
         
    
    /**
     *  Transferring the completed transactions from the server to the network buffer
     *  
     * @return
     * @param outPacket updated transaction transferred by the server to the network output buffer
     * 
     */
         public static boolean transferOut(Transactions outPacket)
        {
	   	 try {
                //Acquiring Out buffer semaphore
                outBufferEmpty.acquire(); // Only access if the buffer is NOT full
                outBufferS.acquire(); //Only allows one thread to access out Buffer
        		outGoingPacket[inputIndexServer].setAccountNumber(outPacket.getAccountNumber());
        		outGoingPacket[inputIndexServer].setOperationType(outPacket.getOperationType());
        		outGoingPacket[inputIndexServer].setTransactionAmount(outPacket.getTransactionAmount());
        		outGoingPacket[inputIndexServer].setTransactionBalance(outPacket.getTransactionBalance());
        		outGoingPacket[inputIndexServer].setTransactionError(outPacket.getTransactionError());
        		outGoingPacket[inputIndexServer].setTransactionStatus("transferred");
            
        		// System.out.println("\n DEBUG : Network.transferOut() - index inputIndexServer " + inputIndexServer); 
        		// System.out.println("\n DEBUG : Network.transferOut() - account number " + outGoingPacket[inputIndexServer].getAccountNumber());
            
        		setinputIndexServer(((getinputIndexServer() + 1) % getMaxNbPackets())); /* Increment the output buffer index for the server */
                if ( getinputIndexServer( ) == getoutputIndexClient( ))
        		{
        			setOutBufferStatus("full");
                
        			/* //System.out.println("\n DEBUG : Network.transferOut() - outGoingBuffer status " + getOutBufferStatus()); */
        		}
        		else
        		{
        			setOutBufferStatus("normal");
        		}
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                outBufferS.release(); // Release semaphore after modifying buffer
                outBufferFull.release(); // Release semaphore after adding to OUT buffer because now it is not empty
            }     
             return true;
        }   
  
    /**
     *  Transferring the transactions from the network buffer to the server
     * @return
     * @param inPacket transaction transferred from the input buffer to the server 
     * 
     */
       public static boolean transferIn(Transactions inPacket)
        {
            try{
                
                 inBufferFull.acquire();

                 //It is possible that the client server finished sending all transactions and two or more threads tried to access the buffer
                 //when there was only one transaction left. If that is the case, isSendingClientFinished will release one, and then that one will release the rest. 
                 if (Network.getInBufferStatus().equals("empty") && Network.isSendingClientFinished()){
                    //Release any possibly stuck server thread that got in at the same time as this thread not realizing only one transaction was left
                    inBufferFull.release();// This creates a train of threads where every new thread that might have been stuck here releass the next one.
                    return false;
                 }

                 inBufferS.acquire();
    		     inPacket.setAccountNumber(inComingPacket[outputIndexServer].getAccountNumber());
    		     inPacket.setOperationType(inComingPacket[outputIndexServer].getOperationType());
    		     inPacket.setTransactionAmount(inComingPacket[outputIndexServer].getTransactionAmount());
    		     inPacket.setTransactionBalance(inComingPacket[outputIndexServer].getTransactionBalance());
    		     inPacket.setTransactionError(inComingPacket[outputIndexServer].getTransactionError());
    		     inPacket.setTransactionStatus("received");
           
    		    //  System.out.println("\n DEBUG : Network.transferIn() - index outputIndexServer " + outputIndexServer); 
    		    //   System.out.println("\n DEBUG : Network.transferIn() - account number " + inPacket.getAccountNumber());

    		     setoutputIndexServer(((getoutputIndexServer() + 1) % getMaxNbPackets()));	/* Increment the input buffer index for the server */
    		     /* Check if input buffer is empty */
                 if ( getoutputIndexServer( ) == getinputIndexClient( ))
    		     {
    		    	 setInBufferStatus("empty");
                
    		    	//System.out.println("\n DEBUG : Network.transferIn() - inComingBuffer status " + getInBufferStatus()); 
    		     }
                
    		     else 
    		     {
    		    	 setInBufferStatus("normal");
    		     }
                
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {    
                    if (Network.getInBufferStatus().equals("empty") && Network.isSendingClientFinished()){
                        inBufferFull.release();// This creates the start of the train of releasing stuck threads                    
                     }
    
                    inBufferS.release(); // Release semaphore after modifying buffer
                    inBufferEmpty.release(); // Release semaphore after removing from buffer because now it is not full
                }
             return true;
        }   
         
     /**
      *  Handling of connection requests through the network 
      *  
      * @return valid connection
      * @param IP
      * 
      */
     public static boolean connect(String IP)
     {
         if (getNetworkStatus().equals("active"))
         {
             if (getClientIP().equals(IP))
             {
                setClientConnectionStatus("connected");
                setPortID(0);
             }
             else
             if (getServerIP().equals(IP))
             {
                setServerConnectionStatus("connected");
             }
             return true;
         }
         else
             return false;
     }
     
     /**
      *  Handling of disconnection requests through the network 
      * @return valid disconnection
      * @param IP
      * 
      */
     public static boolean disconnect(String IP)
     {
          if (getNetworkStatus( ).equals("active"))
         {
             if (getClientIP().equals(IP))
             {
                setClientConnectionStatus("disconnected");
             }
             else
             if (getServerIP().equals(IP))
             {
                setServerConnectionStatus("disconnected");
             }
             return true;
         }
         else
             return false;
     }
         
     /**
      *  Create a String representation based on the Network Object
      * 
      * @return String representation
      */
	    public String toString() 
	    {
	        return ("\n Network status " + getNetworkStatus() + "Input buffer " + getInBufferStatus() + "Output buffer " + getOutBufferStatus());
	    }
       
    /**
     *  Code for the run method
     * 
     * @return 
     * @param
     */
    public void run()
    {	
    	 System.out.println("\n DEBUG : Network.run() - starting network thread"); 
    	
    	while (true)
    	{
            if( getClientConnectionStatus().equals("disconnected") && getServerConnectionStatus().equals("disconnected")){
            	System.out.println("\n Terminating network thread - Client "+ getClientConnectionStatus() +" Server "+ getServerConnectionStatus()); 
                break;
            }
            else{
                Thread.yield(); // Yield while client and server still connected
            }  
    	}    
    }
}
