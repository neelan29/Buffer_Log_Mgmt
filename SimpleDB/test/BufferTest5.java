package test;



import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.server.SimpleDB;

public class BufferTest5 {

	public static void main(String[] args) {
		SimpleDB.init("simpleDB");
		System.out.println("---------------------STARTS HERE -----------------------------------");
		//BufferMgr basicBufferMgr = SimpleDB.bufferMgr();
	
		BufferMgr bufferMgr = new BufferMgr(4);
		Block[] blocks = new Block[12];
		for (int i=0 ; i< blocks.length; i++) {
			blocks[i]= new Block("file-block",i);
		}
	
		
		System.out.println("number of Buffers available: "+bufferMgr.available());
		
		Buffer[] buffers = new Buffer[12];
		
		buffers[0] = bufferMgr.pin(blocks[0]);
		buffers[0].setInt(10, 20, 1, 0);

		buffers[1] = bufferMgr.pin(blocks[1]);
		buffers[1].setInt(10, 20, 1, 0);
		

		buffers[2] = bufferMgr.pin(blocks[2]);
		buffers[2].setInt(10, 20, 1, 0);
		

		buffers[3] = bufferMgr.pin(blocks[3]);
		buffers[3].setInt(10, 20, 1, 0);
		
		System.out.println(" after adding 0 1 2 3");
		  for (int i=0;i<blocks.length;i++) {
			   if(bufferMgr.getMapping(blocks[i])!=null)
		    	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
		      }
		  
		 /************************************************************************************/ 

		bufferMgr.unpin(buffers[0]);
		bufferMgr.unpin(buffers[1]);
		bufferMgr.unpin(buffers[2]);
		bufferMgr.unpin(buffers[3]);
		
		buffers[1] = bufferMgr.pin(blocks[4]);
		buffers[1].setInt(10, 20, 1, 0);
				
		buffers[2] = bufferMgr.pin(blocks[5]);
		buffers[2].setInt(10, 20, 1, 0);
			
		buffers[0] = bufferMgr.pin(blocks[6]);
		buffers[0].setInt(10, 20, 1, 0);
		
		System.out.println(" after adding 4 5 6");
		for (int i=0;i<blocks.length;i++) {
		   if(bufferMgr.getMapping(blocks[i])!=null)
		   	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
		}
		
		System.out.println("BlockHistory printing below");   
		System.out.println(bufferMgr.getBlockHistory());

		/************************************************************************************/ 

			bufferMgr.unpin(buffers[0]);
			bufferMgr.unpin(buffers[1]);
			bufferMgr.unpin(buffers[2]);
			bufferMgr.unpin(buffers[3]);
			
			buffers[0] = bufferMgr.pin(blocks[2]);
			buffers[0].setInt(10, 20, 1, 0);
			
			buffers[1] = bufferMgr.pin(blocks[1]);
			buffers[1].setInt(10, 20, 1, 0);
			
			buffers[2] = bufferMgr.pin(blocks[0]);
			buffers[2].setInt(10, 20, 1, 0);
				
			buffers[3] = bufferMgr.pin(blocks[7]);
			buffers[3].setInt(10, 20, 1, 0);
			
			System.out.println(" after adding 2 1 0 7");
			   for (int i=0;i<blocks.length;i++) {
				   if(bufferMgr.getMapping(blocks[i])!=null)
			    	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
			      }	   
				System.out.println("BlockHistory printing below");   
				System.out.println(bufferMgr.getBlockHistory());
	
		    /*************************************************************************/

			bufferMgr.unpin(buffers[0]);
			bufferMgr.unpin(buffers[1]);
			bufferMgr.unpin(buffers[2]);
			bufferMgr.unpin(buffers[3]);   
			 
			System.out.println("Adding 8 .. the fuck all number");
			buffers[0] = bufferMgr.pin(blocks[8]);
			buffers[0].setInt(10, 20, 1, 0);
			buffers[1] = bufferMgr.pin(blocks[4]);
			buffers[1].setInt(10, 20, 1, 0);
			
			
			System.out.println(" after adding 8 4");
			   for (int i=0;i<blocks.length;i++) {
				   if(bufferMgr.getMapping(blocks[i])!=null)
			    	  System.out.println(bufferMgr.getMapping(blocks[i]) +" : " +blocks[i].toString());
   		
			   }


		}
}