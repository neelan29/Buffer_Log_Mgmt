package test;

import java.util.ArrayList;
import java.util.List;

import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.server.SimpleDB;

public class BufferTest4 {

	public static void main(String[] args) {
		//SimpleDB.init("simpleDB");
		System.out.println("---------");
		BufferMgr basicBufferMgr = SimpleDB.bufferMgr();
	
		BufferMgr bufferMgr = new BufferMgr(4);
		Block[] blocks = new Block[12];
		for (int i=0 ; i< blocks.length; i++) {
			blocks[i]= new Block("file-block",i);
		}
	
		
		System.out.println("number of Buffers available: "+bufferMgr.available());
		
		Buffer[] buffers = new Buffer[4];
		
		for (int i = 0; i < 4; i++) {
			buffers[i] = bufferMgr.pin(blocks[i]);
			buffers[i].setInt(10, 20, 1, i);
			buffers[i].getInt(10);
			System.out.println("Available buffers : " + bufferMgr.available() + "\n");
		}

		   for (int i=0;i<blocks.length;i++) {
			   if(bufferMgr.getMapping(blocks[i])!=null)
		    	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
		      }
		bufferMgr.unpin(buffers[1]);
		bufferMgr.unpin(buffers[3]);
		bufferMgr.unpin(buffers[2]);
		buffers[8]=bufferMgr.pin(blocks[8]);
		buffers[8].setInt(12, 22, 2, 100);
		buffers[9]=bufferMgr.pin(blocks[9]);
		buffers[8].setInt(15, 22, 2, 100);
		buffers[9].getInt(15);
		//buffers[10]=bufferMgr.pin(blocks[10]);
		for (int i=0;i<blocks.length;i++) {
			   if(bufferMgr.getMapping(blocks[i])!=null)
		    	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
		      }
		
	}

}