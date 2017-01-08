package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class ClientServiceThread extends Thread {
	Socket clientSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	BlockingQueue<Request> queue;
	
	ClientServiceThread(Socket s, BlockingQueue<Request> queue){
		this.clientSocket = s;
		this.queue = queue;
	}
	
	public void run(){
		// Log connection with client
		try {
			queue.put(new Request("1", clientSocket.getInetAddress().getHostName(), new Date()));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			Request r = null;
			
			do{
				do{
					r = (Request) in.readObject();
					Thread.sleep(1000);
				}while (r == null);
				
				// Add request to queue
				//queue.add(r);
				switch(r.getCommand()){
					case "1":
						sendMessage("Connected to the file server");
						queue.add(new Request("1", clientSocket.getInetAddress().getHostName(), new Date()));
						break;
					case "2":
						sendFileList();
						break;
					case "3":
						// Send file to user
						sendFile();
						break;
					default:
						System.out.println("Command failure");
				}
			}while(!clientSocket.isClosed());
		}catch (Exception e){
			System.out.println("Connection to client lost");
			try {
				queue.put(new Request("4", clientSocket.getInetAddress().getHostName(), new Date()));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendFileList() throws InterruptedException{
		// Send file list
		File folder = new File(".");
		File [] fileList = folder.listFiles();
		
		String files = "";
		
		for (int i = 0; i < fileList.length; i++) {
			files += (fileList[i].getName()) + " "; 
		}
		
		sendMessage(files);
		
		// Log what happened
		queue.put(new Request("2", clientSocket.getInetAddress().getHostName(), new Date()));
	}
	
	private void sendFile() throws FileNotFoundException, InterruptedException{
		String message = null;
		do{
			try {
				message = (String) in.readObject();
				Thread.sleep(100);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			
		}while(message == null);
		
		File get = new File(message);
		
		if (get.exists() && get.isFile()){ // If the file at the given path exists and is a file
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(get)));
			String file = "";
			String next = null;
			
			try {
				while((next = br.readLine()) != null){
					file += next + "\n"; // Write the whole file into a string variable
				}
				sendMessage(file); // Send entire file in a message
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{ 
			sendMessage("File not found");
		}
		
		DownloadRequest r = new DownloadRequest("2", clientSocket.getInetAddress().getHostName(), new Date());
		r.setFilename(message);
		queue.put(r);
	}
}
