import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

// Author: Ibrahim Omer Celik

public class RockPaperScissorsServer {
	
	//private static Socket clientSocket;
	//private static Socket serverSocket;
    
	public static void main(String args[]) throws UnknownHostException, IOException{
		
		Scanner scan = new Scanner(System.in);
		int roundNo = 0;
        
		// server case
		if(args.length == 1){
			
				// parse the port number from the given arguments
				int portNumber = Integer.parseInt(args[0]); 
				
				// start listening port number specified in arguments
				ServerSocket server = new ServerSocket(portNumber);
				System.out.println("Server Started and listening to the port " + portNumber+"...");
                
                
                while(true){
                   // int counter = 1;
                    Socket clientSocket = server.accept();
                    Thread clientThread = new Thread(new ThreadClass(clientSocket), "clientThread");
                    clientThread.start();
            
                }
        }
		
		//client case
		else if(args.length == 2 ){
		
            Socket clientSocket;
            
            // get the command line arguments
            int portNumber = Integer.parseInt(args[1]);
            String ipAddress = args[0];
            clientSocket = new Socket(ipAddress, portNumber);
            
			try{
				
				// prompt user
				System.out.print("Enter the number of rounds (Press Q to quit) " );
				String input = scan.nextLine();
                
                clientSocket = new Socket(ipAddress, portNumber);
                
				// loop through until user quits
				while(!input.equalsIgnoreCase("q")){
					
					// establish connection with server
		            //clientSocket = new Socket(ipAddress, portNumber);
					roundNo = Integer.parseInt(input);
					
					String[] shapes = new String[roundNo];
					
					// prompt user 
					for (int i = 0; i< roundNo; i++){
						
						System.out.print("Round " + (i+1) + ": ");
						String shapeType = scan.nextLine();
						shapeType = shapeType.toUpperCase();
						shapes[i] = shapeType;
					}
					
					// create IO streams
		            OutputStream os = clientSocket.getOutputStream();
		            OutputStreamWriter osw = new OutputStreamWriter(os);
		            BufferedWriter bw = new BufferedWriter(osw);
		 
		            // client sends the chosen shapes in an appropriate format
		            StringBuilder sb = new StringBuilder();
		            String sendMessage = "SHAPE\r\n";
		            sb.append(sendMessage);
		            
		            // apply the protocol
		            for(int j = 0; j<roundNo; j++){
		            	sb.append(shapes[j]);
		            	sb.append("\r\n");
		            }
		            sb.append("\r\n");
		            sendMessage = sb.toString();
		            
		            // write to the buffer
		            bw.write(sendMessage);
		            bw.flush();

					// receive the result from the server
	                InputStream is = clientSocket.getInputStream();
	                InputStreamReader isr = new InputStreamReader(is);
	                BufferedReader br = new BufferedReader(isr);
	                
	                int counter = roundNo + 4;
	                String[] server  = new String[ counter];
	                
	                for(int i = 0; i< counter; i++){
	                	server[i]  = br.readLine();
	                }

					//print the results
	                getClientResult(shapes,server);
	                
	                // print the total results
	                for(int i = 0; i<3; i++)
	                	System.out.println(server[roundNo+(i+1)]);

					// ask again
					System.out.print("\nEnter the number of rounds (Press Q to quit) " );
					input = scan.nextLine();
					
				}
				
			}catch(Exception e){
					e.printStackTrace();
			}
			finally
		    {
		            // close the socket in anyways
		            try
		            {
		                //clientSocket.close();
		            }
		            catch(Exception e)
		            {
		                e.printStackTrace();
		            }
		    }
			
			System.out.println("Session is over\nBye..");
		}
		
		else
			System.out.println("Arguments are wrong try again!");
	}
	
	// method for printing client result
	public static void getClientResult(String [] clientShapes, String [] randomShapes){
		
		for(int i = 0; i<clientShapes.length; i++){
			
			if(clientShapes[i].equalsIgnoreCase(randomShapes[i+1])){
				System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - tie");
			}
			else if(clientShapes[i].equalsIgnoreCase("ROCK")){
				
				if(randomShapes[i+1].equalsIgnoreCase("PAPER")){
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - server wins");
				}
				else{
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - client wins");
				}
			}
			else if(clientShapes[i].equalsIgnoreCase("PAPER")){
				
				if(randomShapes[i].equalsIgnoreCase("ROCK")){
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - client wins");
				}
				else{
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - server wins");
				}
				
			}
			else if(clientShapes[i].equalsIgnoreCase("SCISSORS")){
				
				if(randomShapes[i+1].equalsIgnoreCase("ROCK")){
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - server wins");
				}
				else{
					System.out.println("Round-"+(i+1)+": Server chooses " + randomShapes[i+1] + " - client wins");
				}
			}
		}

	}
	
	// method for printing server result and get the total result.
	public static int[] getServerResult(String [] clientShapes, String [] randomShapes,long id){
		
		int[] results = new int[3];
		
		int clientResult = 0;
		int tieResult = 0;
		int serverResult = 0;
		
		for(int i = 0; i<randomShapes.length; i++){
			
			if(clientShapes[i+1].equalsIgnoreCase(randomShapes[i])){
				System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - tie");
				tieResult++;
			}
			else if(clientShapes[i+1].equalsIgnoreCase("ROCK")){
				
				if(randomShapes[i].equalsIgnoreCase("PAPER")){
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - server wins");
					serverResult++;
				}
				else{
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - client wins");
					clientResult++;
				}
			}
			else if(clientShapes[i+1].equalsIgnoreCase("PAPER")){
				
				if(randomShapes[i].equalsIgnoreCase("ROCK")){
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - client wins");
					clientResult++;
				}
				else{
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - server wins");
					serverResult++;
				}
				
			}
			else if(clientShapes[i+1].equalsIgnoreCase("SCISSORS")){
				
				if(randomShapes[i].equalsIgnoreCase("ROCK")){
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - server wins");
					serverResult++;
				}
				else{
					System.out.println("THREAD-"+id+" Round-"+(i+1)+": Client: "+clientShapes[i+1] + " Server: "+ randomShapes[i] + " - client wins");
					clientResult++;
				}
			}
		}
		
		results[0] = clientResult;
		results[1] = tieResult;
		results[2] = serverResult;
		
		return results;
	}
    
    public static class ThreadClass implements Runnable{
       
        Socket clientSocket;
        
        ThreadClass(Socket clientSocket){
            this.clientSocket = clientSocket;
        }
        
        @Override
        public void run(){
            
            long id = Thread.currentThread().getId();
            
            int totalClient = 0;
            int totalTie = 0;
            int totalServer = 0;
            
            boolean synchronize = false;
            boolean status = true;
            
            try{
                
                // server should run until program is terminated
                while(status){
                    
                    //wait until message is sent from the client
                    //server.accept();
                    
                    // read message sent by the client
                    InputStream iStream = clientSocket.getInputStream();
                    InputStreamReader iReader = new InputStreamReader(iStream);
                    BufferedReader bReader = new BufferedReader(iReader);
                    String message = "";
                    String [] receivedShapes = new String[50]; // create enough space since we don't know the round number
                    int count = 0;
                    while((message = bReader.readLine())!= null&&bReader.ready()){
                        
                        receivedShapes[count] = message;
                        count++;
                    }
                    
                    if (count == 0){
                        status = false;
                        break;
                    }
                    
                    System.out.println("\nTHREAD-"+ id +" Client has sent " + (count -1) + " shapes..");
                    System.out.println("THREAD-"+id+" Shapes are chosen..");
                    
                    // generate the random shapes
                    String [] randomShapes = new String[count-1];
                    for (int i = 0; i< count-1; i++){
                        
                        //System.out.println(receivedShapes[i]);
                        int randNo =  (int)(Math.random() * ((2) + 1));
                        
                        // case for rock
                        if(randNo == 0){
                            randomShapes[i] = "ROCK";
                        }
                        
                        // case for paper
                        else if(randNo == 1){
                            randomShapes[i] = "PAPER";
                        }
                        
                        //case for scissors
                        else{
                            randomShapes[i] = "SCISSORS";
                        }
                    }
                    
                    //compare with clients' shapes and print the result
                    int [] results = getServerResult(receivedShapes, randomShapes,id);
                   /* System.out.println("Client: " + results[0]);
                    System.out.println("Tie: " + results[1]);
                    System.out.println("Server: " + results[2]);*/
                    
                    // create message by applying protocol
                    String returnMessage = "RESULT\r\n";
                    StringBuilder sBuilder = new StringBuilder();
                    sBuilder.append(returnMessage);
                    
                    for(int i = 0; i<count-1; i++){
                        sBuilder.append(randomShapes[i]);
                        sBuilder.append("\r\n");
                    }
                    
                    //append the total results also
                    sBuilder.append("CLIENT="+results[0]+"\r\n");
                    sBuilder.append("TIE="+results[1]+"\r\n");
                    sBuilder.append("SERVER="+results[2]+"\r\n");
                    sBuilder.append("\r\n");
                    returnMessage = sBuilder.toString(); //unique protocol message
            
                    //Sending the response back to the client.
                    OutputStream oStream = clientSocket.getOutputStream();
                    OutputStreamWriter oWriter = new OutputStreamWriter(oStream);
                    BufferedWriter bWriter = new BufferedWriter(oWriter);
                    bWriter.write(returnMessage);
                    bWriter.flush();
                    
                    totalClient += results[0];
                    totalTie += results[1];
                    totalServer += results[2];

                }
                
                // in case no game is played do not print this out
                if(totalClient != 0 || totalTie != 0 || totalServer != 0){
                    
                    System.out.println();
                    System.out.println("THREAD-"+id+" Terminate Connection");
                    System.out.println("THREAD-"+id+" Results are as follows:");
                    System.out.println("THREAD-"+ id +" Client: " + totalClient);
                    System.out.println("THREAD-"+ id +" Tie: " + totalTie);
                    System.out.println("THREAD-"+ id +" Server: " + totalServer);
                }
                
            }catch(IOException e){
                e.printStackTrace();
            }
            finally
            {
                
                // close the socket in anyways
                try
                {
                    clientSocket.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        
        }
    
    }
}
