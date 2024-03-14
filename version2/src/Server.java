
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.InputMismatchException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*package comp546pa1w2020;*/

/** Server class
 *
 * @author Kerly Titus
 */

public class Server extends Thread {
	
	/* NEW : Shared member variables are now static for the 2 receiving threads */
	private static int numberOfTransactions;         	/* Number of transactions handled by the server */
	private static int numberOfAccounts;             	/* Number of accounts stored in the server */
	private static int maxNbAccounts;                		/* maximum number of transactions */
	private static Accounts [] account;              		/* Accounts to be accessed or updated */
	/* NEW : member variabes to be used in PA2 with appropriate accessor and mutator methods */
	private String serverThreadId;				 /* Identification of the two server threads - Thread1, Thread2 */
	private static String serverThreadRunningStatus1;	 /* Running status of thread 1 - idle, running, terminated */
	private static String serverThreadRunningStatus2;
	private static String serverThreadRunningStatus3;/* Running status of thread 2 - idle, running, terminated */
  
    /** 
     * Constructor method of Client class
     * 
     * @return 
     * @param stid
     */
    Server(String stid)
    {
    	if ( !(Network.getServerConnectionStatus().equals("connected")))
    	{
    		System.out.println("\n Initializing the server ...");
    		numberOfTransactions = 0;
    		numberOfAccounts = 0;
    		maxNbAccounts = 100;
    		serverThreadId = stid;							/* unshared variable so each thread has its own copy */
    		serverThreadRunningStatus1 = "idle";				
    		account = new Accounts[maxNbAccounts];
    		System.out.println("\n Inializing the Accounts database ...");
    		initializeAccounts( );
    		System.out.println("\n Connecting server to network ...");
    		if (!(Network.connect(Network.getServerIP())))
    		{
    			System.out.println("\n Terminating server application, network unavailable");
    			System.exit(0);
    		}
            
    	}
    	else
    	{
    		serverThreadId = stid;							/* unshared variable so each thread has its own copy */
    		serverThreadRunningStatus2 = "idle";
    		serverThreadRunningStatus3 = "idle";	
    	}
    }
//region Getters and Setters  
    /** 
     * Accessor method of Server class
     * 
     * @return numberOfTransactions
     * @param
     */
     public  int getNumberOfTransactions()
     {
         return numberOfTransactions;
     }
         
    /** 
     * Mutator method of Server class
     * 
     * @return 
     * @param nbOfTrans
     */
    //Must synchronize because no threads should set at the same time
     public synchronized  void  setNumberOfTransactions(int nbOfTrans)
     { 
         numberOfTransactions = nbOfTrans;
     }

    /** 
     * Accessor method of Server class
     * 
     * @return numberOfAccounts
     * @param
     */
     public synchronized int getNumberOfAccounts()
     {
         return numberOfAccounts;
     }
         
    /** 
     * Mutator method of Server class
     * 
     * @return 
     * @param nbOfAcc
     */
     public void setNumberOfAccounts(int nbOfAcc)
     { 
         numberOfAccounts = nbOfAcc;
     }
         
     /** 
      * Accessor method of Server class
      * 
      * @return maxNbAccounts
      * @param
      */
      public int getMxNbAccounts()
      {
          return maxNbAccounts;
      }
          
     /** 
      * Mutator method of Server class
      * 
      * @return 
      * @param nbOfAcc
      */
      public void setMaxNbAccounts(int nbOfAcc)
      { 
    	  maxNbAccounts = nbOfAcc;
      }
           
      /** 
       * Accessor method of Server class
       * 
       * @return serverThreadId
       * @param
       */
       public String getServerThreadId()
       {
           return serverThreadId;
       }
           
      /** 
       * Mutator method of Server class
       * 
       * @return 
       * @param tId
       */
       public void setServerThreadId(String stid)
       { 
     	  serverThreadId = stid;
       }

       /** 
        * Accessor method of Server class
        * 
        * @return serverThreadRunningStatus1
        * @param
        */
        public String getServerThreadRunningStatus1()
        {
            return serverThreadRunningStatus1;
        }
            
       /** 
        * Mutator method of Server class
        * 
        * @return 
        * @param runningStatus
        */
        public void setServerThreadRunningStatus1(String runningStatus)
        { 
      	  serverThreadRunningStatus1 = runningStatus;
        }
        
        /** 
         * Accessor method of Server class
         * 
         * @return serverThreadRunningStatus2
         * @param
         */
         public String getServerThreadRunningStatus2()
         {
             return serverThreadRunningStatus2;
         }
         
         public String getServerThreadRunningStatus3()
         {
             return serverThreadRunningStatus3;
         }
             
        /** 
         * Mutator method of Server class
         * 
         * @return 
         * @param runningStatus
         */
         public void setServerThreadRunningStatus2(String runningStatus)
         { 
       	  serverThreadRunningStatus2 = runningStatus;
         }
         public void setServerThreadRunningStatus3(String runningStatus)
         { 
       	  serverThreadRunningStatus3 = runningStatus;
         }
    /** 
     * Find and return the index position of an account 
     * 
     * @return account index position or -1
     * @param accNumber
     */
    public int findAccount(String accNumber)
    {
        int i = 0;
        

        /* Find account */
        while ( !(account[i].getAccountNumber().equals(accNumber))){     
            i++;
            //First have to check if we reached the end of the array 
            if (i == getNumberOfAccounts())
                return -1;
        }
        return i;
    }
//endregion
    /** 
     * Initialization of the accounts from an input file
     * 
     * @return 
     * @param
     */  
     public void initializeAccounts()
     {
        Scanner inputStream = null; /* accounts input file stream */
        int i = 0;                  /* index of accounts array */
        
        try
        {
         inputStream = new Scanner(new FileInputStream("account.txt"));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File account.txt was not found");
            System.out.println("or could not be opened.");
            System.exit(0);
        }
        while (inputStream.hasNextLine())
        {
            try
             {   account[i] = new Accounts();
                account[i].setAccountNumber(inputStream.next());    /* Read account number */
                account[i].setAccountType(inputStream.next());      /* Read account type */
                account[i].setFirstName(inputStream.next());        /* Read first name */
                account[i].setLastName(inputStream.next());         /* Read last name */
                account[i].setBalance(inputStream.nextDouble());    /* Read account balance */                
            }
            catch(InputMismatchException e)
            {
                System.out.println("Line " + i + "file account.txt invalid input");
                System.exit(0);
            }
            i++;
        }
        setNumberOfAccounts(i);			/* Record the number of accounts processed */
        
        //System.out.println("\n DEBUG : Server.initializeAccounts() " + getNumberOfAccounts() + " accounts processed");
        
        inputStream.close( );
     }
         

     
    /** 
     * Processing of the transactions
     * 
     * @return 
     * @param trans
     */
     public boolean processTransactions(Transactions trans)
     {   int accIndex;             	/* Index position of account to update */
         double newBalance; 		/* Updated account balance */
         
        //   System.out.println("\n DEBUG : Server.processTransactions() " + getServerThreadId() ); 
         
         
        while (! (Network.getInBufferStatus().equals("empty") && Network.isSendingClientFinished())) 
        {
            //? SERVER THREAD 1
            //? SERVER THREAD 2
            // System.out.println("\n DEBUG : Server.processTransactions() - transferring in account " + trans.getAccountNumber()); 
            //THIS IS ATOMITC: 
            
            boolean Notdone = Network.transferIn(trans);                              /* Transfer a transaction from the network input buffer */
            if (!Notdone){
                break;
            }

            accIndex = findAccount(trans.getAccountNumber());
            //If account is found, we try to to the operations. Otherwise, the transaction is sent without any operation
            if(accIndex != -1){

            /* Process deposit operation */
            if (trans.getOperationType().equals("DEPOSIT"))
            {
                newBalance = deposit(accIndex, trans.getTransactionAmount()); 
                trans.setTransactionBalance(newBalance);
                trans.setTransactionStatus("done");
                
                // System.out.println("\n DEBUG : Server.processTransactions() - Deposit of " + trans.getTransactionAmount() + " in account " + trans.getAccountNumber());
            }
            else
                /* Process withdraw operation */
                if (trans.getOperationType().equals("WITHDRAW"))
                {
                    newBalance = withdraw(accIndex, trans.getTransactionAmount());
                    trans.setTransactionBalance(newBalance);
                    trans.setTransactionStatus("done");
                    
                    // System.out.println("\n DEBUG : Server.processTransactions() - Withdrawal of " + trans.getTransactionAmount() + " from account " + trans.getAccountNumber());
                }
                else
                    /* Process query operation */
                    if (trans.getOperationType().equals("QUERY"))
                    {
                    newBalance = query(accIndex);
                    trans.setTransactionBalance(newBalance);
                    trans.setTransactionStatus("done");
                    
                    // System.out.println("\n DEBUG : Server.processTransactions() - Obtaining balance from account" + trans.getAccountNumber());
            } 
            }else{
            	
                //System.out.println("Account not found");
                trans.setTransactionError("Account Not found!");
            }
            // while (Network.getOutBufferStatus().equals("full")) 
            // { 
            //     Thread.yield();		/* Yield the cpu if the network output buffer is full */
            // }
        
            // System.out.println("\n DEBUG : Server.processTransactions() - transferring out account " + trans.getAccountNumber());
            
            Network.transferOut(trans);                            		/* Transfer a completed transaction from the server to the network output buffer */
            setNumberOfTransactions( (getNumberOfTransactions() +  1) ); 
            		/* Count the number of transactions processed */
         }
        //  System.out.println("\n DEBUG : Server.processTransactions() - " + getNumberOfTransactions() + " accounts updated");
              
         return true;
     }
         
    /** 
     * Processing of a deposit operation in an account
     * 
     * @return balance
     * @param i, amount
     */
   
     //synchronized the account array because if it was synchronized with the entire method it is possible that a different thread enters one of the other
     //2 methods and edits the value of the account at the same index. Hence account needs to protected so two methods don't conflict with each other.
    public double deposit(int i, double amount)
     {  synchronized(account[i]) {double curBalance;      /* Current account balance */
     		curBalance = account[i].getBalance( );          /* Get current account balance */
     		/* NEW : A server thread is blocked before updating the 10th , 20th, ... 70th account balance in order to simulate an inconsistency situation */
     		if (((i + 1) % 10 ) == 0)
     		{
     			try {
     					Thread.sleep(100);
     				}
     				catch (InterruptedException e) {
        	
     				} 
     		} 
     		//System.out.println("\n DEBUG : Server.deposit - " + "i " + i + " Current balance " + curBalance + " Amount " + amount + " " + getServerThreadId());
     		account[i].setBalance(curBalance + amount);     /* Deposit amount in the account */
     		return account[i].getBalance (); }               /* Return updated account balance */
     }
         
    /**
     *  Processing of a withdrawal operation in an account
     * 
     * @return balance
     * @param i, amount
     */
    //synchronized the account array because if it was synchronized with the entire method it is possible that a different thread enters one of the other
    //2 methods and edits the value of the account at the same index. Hence account needs to protected so two methods don't conflict with each other.
     public double withdraw(int i, double amount)
     {  
    	 synchronized(account[i]) {
    	 double curBalance;      /* Current account balance */
        
     	curBalance = account[i].getBalance( );          /* Get current account balance */
          
        //System.out.println("\n DEBUG : Server.withdraw - " + "i " + i + " Current balance " + curBalance + " Amount " + amount + " " + getServerThreadId());
        
        account[i].setBalance(curBalance - amount);     /* Withdraw amount in the account */
        return account[i].getBalance ();   }             /* Return updated account balance */
     	
     }

    /**
     *  Processing of a query operation in an account
     * 
     * @return balance
     * @param i
     */
 
     public double query(int i)
     {  
    	synchronized(account[i]) {
    	double curBalance;      /* Current account balance */
        
     	curBalance = account[i].getBalance( );          /* Get current account balance */
        
        //System.out.println("\n DEBUG : Server.query - " + "i " + i + " Current balance " + curBalance + " " + getServerThreadId()); 
        
        return curBalance;  }                            /* Return current account balance */
     }
         
     /**
      *  Create a String representation based on the Server Object
     * 
     * @return String representation
     */
     public String toString() 
     {	
    	 return ("\n server IP " + Network.getServerIP() + "connection status " + Network.getServerConnectionStatus() + "Number of accounts " + getNumberOfAccounts());
     }
     
    /**
     * Code for the run method
     * 
     * @return 
     * @param
     */
      
    public void run()
    {   Transactions trans = new Transactions();
    	 long serverStartTime, serverEndTime;
    
	    //System.out.println("\n DEBUG : Server.run() - starting server thread " + getServerThreadId() + " " + Network.getServerConnectionStatus());
        
        //Processing Transactions
        serverStartTime = System.currentTimeMillis();
        processTransactions(trans);
        serverEndTime = System.currentTimeMillis();

        //Terminating thread status
        if (getServerThreadId().equals("Thread1")) {

            //System.out.println("\n Terminating server thread 1 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
            setServerThreadRunningStatus1("terminated");

        } else if (getServerThreadId().equals("Thread2")) {

            //System.out.println("\n Terminating server thread 2 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
            setServerThreadRunningStatus2("terminated");
        }
        else if (getServerThreadId().equals("Thread3")) {

            //System.out.println("\n Terminating server thread 2 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
            setServerThreadRunningStatus3("terminated");
        }
        // If both threads are terminated, disconnect server, only one thread can attempt to disconnect server to avoid two threads
        // trying to disconnect at the same time
        synchronized(this){
        if(Network.getServerConnectionStatus().equals("disconnected")){
            // DO nothing if anohter thread has already disconnected, we don't want to disconnect twice
        }
        else if (getServerThreadRunningStatus1().equals("terminated") && getServerThreadRunningStatus2().equals("terminated") && getServerThreadRunningStatus3().equals("terminated")) {
            //Test A
            //System.out.println("Server Both Threads terminated");
        	System.out.println("\n Terminating server thread 1 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
        	System.out.println("\n Terminating server thread 2 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
        	System.out.println("\n Terminating server thread 3 - " + " Running time " + (serverEndTime - serverStartTime) + " milliseconds");
            //System.out.println("WE HANDLED" + getNumberOfTransactions() + "TRANSACTIONS");
            Network.setServersFinished(true);
            Network.setServerConnectionStatus("disconnected");
            Network.disconnect(Network.getServerIP());
        }
        }
    	/* .....................................................................................................................................................................................................*/
        
	
    }
}


