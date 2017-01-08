package ie.gmit.sw;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		// Run until user exits
		Scanner console = new Scanner(System.in);
		XMLParser p = new XMLParser();
		
		final int PORT = p.getPort();
		String host = p.getIp();
		String downloadDir = p.getDownloadDir();
		
		Socket requestSocket = null;
		ObjectOutputStream out = null;
	 	ObjectInputStream in = null;
	 	boolean connected = false;
		
		while(true){
			int option;
			System.out.println("1. Connect to Server");
			System.out.println("2. Print File Listing");
			System.out.println("3. Download File");
			System.out.println("4. Quit");
			System.out.println();
			System.out.print("Type Option [1-4]> ");
			try{
				option = console.nextInt();
			}catch(InputMismatchException e){
				System.out.println("Incorrect input");
				
				continue;
			}
			
			if(option == 1){
				if (!connected){
					//Connect to server
					requestSocket = new Socket(host, PORT);
					out = new ObjectOutputStream(requestSocket.getOutputStream());
					out.flush();
					in = new ObjectInputStream(requestSocket.getInputStream());
					connected = true;
					
					Request r = new Request("1", host, new Date());
					out.writeObject(r);
					out.flush();
					
					String message = null;
					do {
						message = (String)in.readObject();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}while (message == null);
					
					System.out.println(message);
				}else{
					System.out.println("Already connected to the file server");
				}
			}else if (option == 2){
				//Print file listing
				if (connected){
					Request r = new Request("2", host, new Date());
					out.writeObject(r);
					out.flush();
					String message = null;
					do {
						message = (String)in.readObject();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}while (message == null);
					
					System.out.println("Files: " + message);
				}else {
					System.out.println("Try connecting to the server (Option 1) first");
				}
			}else if (option == 3){
				//Download file
				if (connected){
					Request r = new Request("3", host, new Date());
					out.writeObject(r);
					out.flush();
					
					console.nextLine();
					System.out.print("Enter a file to download> ");
					String file = console.nextLine();
					
					out.writeObject(file);
					out.flush();
					
					String message = null;
					do {
						message = (String)in.readObject();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}while (message == null);
					
					if(message != "File not found"){
						// Create a directory for downloads. If one exists, the below line will do nothing
						new File(downloadDir).mkdir();
						
						PrintWriter pw = new PrintWriter(downloadDir + "/" + file);
						pw.println(message);
						pw.close();
					}else{
						System.out.println(message);
					}
				}else {
					System.out.println("Try connecting to the server (Option 1) first");
				}
			}else if (option == 4){
				//Exit
				System.exit(0);
				requestSocket.close();
				console.close();
			}else{
				System.out.println("Incorrect input");
			}
		}
	}
}
