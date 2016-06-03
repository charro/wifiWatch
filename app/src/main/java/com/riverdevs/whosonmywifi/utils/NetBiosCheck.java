package com.riverdevs.whosonmywifi.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.riverdevs.whosonmywifi.beans.NetBiosResult;

public class NetBiosCheck {

    private static final int NETBIOS_UDP_PORT = 137;

	private static final byte[]  NETBIOS_REQUEST  = 
	{ 
		(byte)0x82, (byte)0x28, (byte)0x0,  (byte)0x0,  (byte)0x0, 
		(byte)0x1,  (byte)0x0,  (byte)0x0,  (byte)0x0,  (byte)0x0, 
		(byte)0x0,  (byte)0x0,  (byte)0x20, (byte)0x43, (byte)0x4B, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, (byte)0x41, 
		(byte)0x0,  (byte)0x0,  (byte)0x21, (byte)0x0,  (byte)0x1
	};

    public static NetBiosResult sendToHost(String url) {
    	
		NetBiosResult result = new NetBiosResult(false, null);
		
    	try
    	{
    		InetAddress host = InetAddress.getByName(url);
        	byte[] 		   buffer  = new byte[128];
        	DatagramPacket packet  = 
        			new DatagramPacket( buffer, buffer.length, host, NETBIOS_UDP_PORT );
        	DatagramPacket query =
        			new DatagramPacket( NETBIOS_REQUEST, NETBIOS_REQUEST.length, host, NETBIOS_UDP_PORT );

			DatagramSocket socket  = new DatagramSocket();

			socket.setSoTimeout( NetUtils.getTimeoutTime() );
			
			for( int i = 0; i < 2; i++ )
			{
				socket.send( query );
				socket.receive( packet );
				
				byte[] data = packet.getData();
				if( data != null && data.length >= 74 )
				{
					String response = new String( data, "ASCII" );
					System.out.println(response);
					result.setSuccess(true);
					result.setNetBiosName(response.substring( 57, 73 ).trim());
					break;
				}
			}
			
			socket.close();
			
			return result;
		}
		catch( Exception e )
		{
			return result;
		}
		
    }
}
