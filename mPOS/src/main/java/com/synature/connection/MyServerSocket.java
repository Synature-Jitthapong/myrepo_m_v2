package com.synature.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class MyServerSocket implements ISocketConnection{
	public static final String TAG = MyServerSocket.class.getSimpleName();
	
	private ServerSocket mServSocket;
	private Socket mSocket;

    private PrintWriter mWriter;
    private BufferedReader mReader;

	public MyServerSocket(int port){
	    try {
			mServSocket = new ServerSocket(port);
			mSocket = mServSocket.accept();
			mWriter = new PrintWriter(new OutputStreamWriter(
					mSocket.getOutputStream()));
			mReader = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
	}
	
	@Override
	public void send(String msg){
		mWriter.println(msg);
		mWriter.flush();
	}

	@Override
	public String receive() {
		try {
			return mReader.readLine();
		} catch (IOException ex) {
			Log.d(TAG, ex.getMessage());
			return null;
		}
	}

	@Override
	public void close() {
		if(mServSocket != null){
			try {
				mServSocket.close();
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
			}
		}
	}
}
