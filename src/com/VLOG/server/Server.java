/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.VLOG.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Mathieu Deniaud
 */
public class Server {
    
    public static int defaultByteBufferSize = 1024;
    private int port;
    protected Selector selector;
    Charset charset = Charset.forName("ISO-8859-1");
    CharsetDecoder decoder = charset.newDecoder();
    CharsetEncoder encoder = charset.newEncoder();
    ServerSocketChannel ssc;
    protected HashMap<SelectionKey, ByteBuffer> readByteBuffers;
    protected HashMap<SelectionKey, CharBuffer> readCharBuffers;
    protected HashMap<SelectionKey, Integer> messageLengths;
    protected HashMap<SelectionKey, ByteBuffer> writeByteBuffers;
    protected HashMap<SelectionKey, LinkedList<String>> pendingOutgoingMessages;
    protected HashMap<SelectionKey, LinkedList<ByteBuffer>> pendingOutgoingEncodedMessages;
    protected HashMap<SelectionKey, Boolean> readBufferIsEmpty;
    protected LinkedList<SocketChannel> connectedChannels;
    
    public Server(int port) {
        this.port = port;
        
    }
    
    public void run() {
        try {
            
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(this.port));
            selector = Selector.open();
            ssc.register( selector, SelectionKey.OP_ACCEPT );
        } catch(BindException e) {
                System.err.println( "Attempted to bind to port " + this.port + " which is already in use; server going OFFLINE");
        } catch(IOException e) {
                System.err.println( "Failed to open non-blocking server port = " + this.port + "; server going OFFLINE");
        }
        
        try {
            while(true) {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if(key.isConnectable()) {
                        
                    }
                    if(key.isAcceptable()) {
                        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        connectedChannels.addLast(sc);
                    }
                    if(key.isReadable()) {
                        ReadableByteChannel rc = (ReadableByteChannel) key.channel();
                        ByteBuffer bb = readByteBuffers.get(key);
                        if(bb == null) {
                            bb = ByteBuffer.allocate(defaultByteBufferSize);
                            readByteBuffers.put(key, bb);
                        }
                        int bytesRead = -1;
                        if(bb.position() == 0) {
                            bytesRead = rc.read(bb);
                        }
                        bb.flip();
                        Integer bytesToRead = messageLengths.get(key);
                        if(bytesToRead == null) {
                            
                        }
                    }
                        
                    if(key.isWritable()) {
                        
                    }
                }
                iterator.remove();
            }
        } catch(IOException e) {
            
        }
        
    }
    
}
