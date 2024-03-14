import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kerly Titus
 */
public class Driver {

    /** 
     * main class
     * @param args the command line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
    	
    	//PrintStream out = new PrintStream(new FileOutputStream("synchronized with semaphores.txt"));
    	//System.setOut(out);
        
    	Network objNetwork = new Network( );            /* Activate the network */
        objNetwork.start();
        Server objServer1 = new Server("Thread1");
        objServer1.start();
        Server objServer2 = new Server("Thread2");
        objServer2.start();
        Server objServer3 = new Server("Thread3");
        objServer3.start();
        Client objClient1 = new Client("sending");          /* Start the sending client thread */
        objClient1.start();
        Client objClient2 = new Client("receiving");        /* Start the receiving client thread */
        objClient2.start();
        
      /*..............................................................................................................................................................*/
       
    }
    
 }
