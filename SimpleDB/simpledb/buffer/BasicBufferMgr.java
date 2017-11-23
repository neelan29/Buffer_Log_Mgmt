package simpledb.buffer;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import simpledb.file.*;
/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
   private ArrayList<Buffer> bufferpool;	// Just an array holding the history of all buffers
   private ArrayList<Buffer> bufferpoolQueue;
   private ArrayList<Block> blockpool;		// Array with block history
   
   private int numAvailable;
   private Map<Block, Buffer> bufferPoolMap = new HashMap<>();
   private Map<Buffer, Block> bufferBlockMap = new HashMap<>();
   private int numBuffs;
   
   /**
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on both the {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} objects 
    * that it gets from the class
    * {@link simpledb.server.SimpleDB}.
    * Those objects are created during system initialization.
    * Thus this constructor cannot be called until 
    * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
    * is called first.
    * @param numbuffs the number of buffer slots to allocate
    */
   BasicBufferMgr(int numbuffs) {
      bufferpool = new ArrayList<>();
      blockpool = new ArrayList<>();
      bufferpoolQueue = new ArrayList<>();
      numBuffs = numbuffs;
      numAvailable = numBuffs;
      for (int i=0; i<numbuffs; i++) {
    	  Buffer buff = new Buffer();
    	  buff.setBufferMgr(this);
    	  bufferpool.add(buff);
    	  bufferpoolQueue.add(0,buff);
      }
   }
   
   // Written by me
   boolean isOccuringOnce(Block Blk) {
	   int occurrences = Collections.frequency(blockpool, Blk);
	   if(occurrences == 1) 
		   return true;
	   else return false;
   }
   
   /* 
    * I think this function should not have anything else other than removing and putting
      in the start of the queue. Lets see.
   */
   void updateBufferPool(Buffer buff) {

	   Block Blk = bufferBlockMap.get(buff);
	   blockpool.add(Blk);
	   
	   if(bufferpoolQueue.remove(buff))	// If element present in the queue
		   bufferpoolQueue.add(0,buff);		// Removing and adding in the start	   
 
	   
	   // We now have a ArrayList of all blocks that has previously come.
	   // We have to use that to determine which element to be removed from the block.
	   	   
	   // TO BE UPDATED FOR LRU-K - Before this, ne

	     	
	   bufferpool.add(buff);	// NOT NEEDED I GUESS
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   synchronized void flushAll(int txnum) {
	   
      // Not sure if this should be commented or let be there
//	   for (Buffer buff : bufferpool)
//         if (buff.isModifiedBy(txnum))
//         buff.flush();
      
      for (Buffer buff : bufferpoolQueue)
          if (buff.isModifiedBy(txnum))
          buff.flush();
   }
   
   /**
    * Pins a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
         
         Block oldBlk = bufferBlockMap.get(buff);
         bufferPoolMap.remove(oldBlk);
         
         bufferPoolMap.put(blk, buff);
         bufferBlockMap.put(buff, blk);
         
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Allocates a new block in the specified file, and
    * pins a buffer to it. 
    * Returns null (without allocating the block) if 
    * there are no available buffers.
    * @param filename the name of the file
    * @param fmtr a pageformatter object, used to format the new block
    * @return the pinned buffer
    */
   synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      
      Block oldBlk = bufferBlockMap.get(buff);
      bufferPoolMap.remove(oldBlk);
      
      bufferPoolMap.put(buff.block(), buff);
      bufferBlockMap.put(buff, buff.block());
      
      numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Unpins the specified buffer.
    * @param buff the buffer to be unpinned
    */
   synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned())
         numAvailable++;
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   int available() {
	   System.out.println(bufferPoolMap);
      return numAvailable;
   }
   
   private Buffer findExistingBuffer(Block blk) {
	   return bufferPoolMap.get(blk);
   }
   
   
   /*
    *  Func that finds if there are any blocks with just one occurrence && isUNPINNED 
    *  from the back of the bufferpoolQueue
    */
   private Buffer isAnyBlkOccuringOnceAndUnpinned() {
	   
	   int sizeOfQueue = bufferpoolQueue.size();
	   for (int i=sizeOfQueue-1;i>=0;i--) {
		   Buffer buf = bufferpoolQueue.get(i);
		   Block Blk = bufferBlockMap.get(buf);
		   if(isOccuringOnce(Blk) && !(buf.isPinned())) {
			   return buf;
		   }
	   }
	   return null;
   }
   
   
   /*
    * Function to return the k=2 distance for the current block
    */
   private int findDistance(Block Blk) {
	   int blockPoolSize = blockpool.size();
	   int occurrence = 0;
	   for(int i=blockPoolSize-1 ; i>=0 ; i--) {
		   if (occurrence == 2) {
			   return (blockPoolSize-i);
		   }
		   if (blockpool.get(i) == Blk)
			   occurrence++;
	   }
	   System.out.println("MAX DISTANCE IS RETURNED TO BE MAX_VALUE...ERROR ERRROR");
	   return Integer.MAX_VALUE;
   }

   /*
    * Function to sort the Queue based on the Maximum distance from back for 2nd Occurence
    */
   private void sortBufferQueue() {
	   int sizeOfQueue = bufferpoolQueue.size();
	   for (int i=0;i<sizeOfQueue-1;i++) {
		   int distBlkJ = 0 , distBlkJ1 = 0;
		   for(int j=0;j<sizeOfQueue-i-1;j++) {
			   Buffer bufJ = bufferpoolQueue.get(j);
			   Block BlkJ = bufferBlockMap.get(bufJ);
			   
			   Buffer bufJ1 = bufferpoolQueue.get(j+1);
			   Block BlkJ1 = bufferBlockMap.get(bufJ1);	
			   
			   distBlkJ = findDistance(BlkJ);
			   distBlkJ1 = findDistance(BlkJ1);
			   // swapping
			   if(distBlkJ > distBlkJ1) {
				   Buffer tmp = bufJ;
				   bufferpoolQueue.set(j, bufferpoolQueue.get(j+1));
				   bufferpoolQueue.set(j+1, tmp);
			   }
		   }
	   	}   
   }
   // Logic to choose UnpinnedBuffer
   private Buffer chooseUnpinnedBuffer() {
	   // (May be reqd) HERE ALSO SOME LOGIC TO BE WRITTEN, to basically return not
	   // just the unpinned buff, but the unpinned buff with highest distance val.
	  
	   // Iterating from the back of the queue. Because, UpdateBufferPool func
	   // only puts the latest modified buffer in the start.
	   // Need a logic to choose the unpinned buffer from the back, that returns not just
	   // ..... Refer the comment on 186 
	   
	   // This block should take care of the infinity condition:
	   // Iterating from the back, checking if any blk is present only once:
	   // When encountering a buffer whose blk is occuring once, simply return that buff
	   Buffer removeBuff = isAnyBlkOccuringOnceAndUnpinned();	// No need for this funciton. Can as well sort with highest value and do the function.
	   // But wait, dont remove it yet. Think about the initial case, where everything is infinity. Will it work for that case? Think well before you remove it
	   
	   if(removeBuff != null) {
		   return removeBuff;
	   }
	   
	   // Sorting the bufferpoolQueue with respect to distance
	   sortBufferQueue();
	   // Need to find the blk with max distance
	   int sizeOfQueue = bufferpoolQueue.size();
	   for (int i=sizeOfQueue-1;i>=0;i--) {
		   if(!(bufferpoolQueue.get(i).isPinned()))
			   return bufferpoolQueue.get(i);
	   	}
      return null;
   }
   
   /**  
   * Determines whether the map has a mapping from  
   * the block to some buffer.  
   * @paramblk the block to use as a key  
   * @return true if there is a mapping; false otherwise  
   */  
   public boolean containsMapping(Block blk) {  
	   return bufferPoolMap.containsKey(blk);  
   } 
   /**  
   * Returns the buffer that the map maps the specified block to.  
   * @paramblk the block to use as a key  
   * @return the buffer mapped to if there is a mapping; null otherwise  
   */  
   public Buffer getMapping(Block blk) {  
	   return bufferPoolMap.get(blk);  
   } 

   public ArrayList<Buffer> getBufferHistory() {  
	   return bufferpool;  
   }  
   
   public ArrayList<Block> getBlockHistory() {  
	   return blockpool;  
   }  

}