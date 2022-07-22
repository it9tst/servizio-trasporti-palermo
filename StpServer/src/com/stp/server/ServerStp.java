package com.stp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;




public class ServerStp {
    public static void main(String[] args) throws IOException{

        ServerSocket listener = new ServerSocket(1234);
        
        try {
			Socket socket = null;
            while (true){
                try {
                	socket = listener.accept();
                	new ThreadServer(socket);
                }catch(IOException e){
                	System.out.println("Errore accettazione client"); 
                }
            }
		}finally{
			listener.close();
		}
    }
    
    
    protected static String cryptaPass(String nick, String pass){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update((pass+nick).getBytes());
	    byte byteData[] = md.digest();

	    StringBuffer hashCodeBuffer = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	        hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return hashCodeBuffer.toString();
	}
    
    
}




