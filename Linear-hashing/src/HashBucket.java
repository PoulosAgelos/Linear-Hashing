

import java.io.IOException;
import java.io.RandomAccessFile;


class HashBucket {

	private int keysNumber;
	private int[] keys;
	private HashBucket overflowBucket;
	private int id;
	private int number=0;
	
	public HashBucket(int bucketSize, int id) throws IOException{		// Constructor: initialize variables
		this.id = id;

		keysNumber = 0;
		keys = new int[bucketSize];
		overflowBucket = null;
	}

	public HashBucket() {
		// TODO Auto-generated constructor stub
	}

	public int numKeys(){return keysNumber;		}
	
	public void insertKey(int key, LinearHashing lh) throws IOException{ 
        
		int i;
		int bucketSize = lh.getBucketSize();
		int keysNum = lh.getKeysNum();
		int keySpace = lh.getKeySpace();
        number+=1;
		for (i = 0; (i < this.keysNumber) && (i < bucketSize); i++){
		   if (this.keys[i] == key){			
			   return;
		   }
		}
		if (i < bucketSize){					
		    keys[i] = key;
		    this.keysNumber++;
		    keysNum++;
		    lh.setKeysNum(keysNum); 			
		   	System.out.println("HashBucket.insertKey:" +key);
		}
		else {
			number+=1;
		    System.out.println("Overflow.............");
		    if (this.overflowBucket != null){	// pass key to the overflow
		        this.overflowBucket.insertKey(key, lh);
		    }
		    else{								// create a new overflow and write the new key
				this.overflowBucket = new HashBucket(bucketSize, i);
				keySpace += bucketSize;
			    lh.setKeySpace(keySpace);		//udate linear hashing class
				this.overflowBucket.insertKey(key, lh);
		    }
		}
		System.out.println("!"+number);
	}


	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void deleteKey(int key, LinearHashing lh) {

		int i;
		int bucketSize = lh.getBucketSize();
		int keysNum = lh.getKeysNum();
		int keySpace = lh.getKeySpace();

		for (i = 0; (i < this.keysNumber) && (i < bucketSize); i++) {
		   if (this.keys[i] == key) {
		     if (this.overflowBucket == null) {		// no overflow
				 this.keys[i] = this.keys[this.keysNumber-1];
				 this.keysNumber--;
				 lh.setKeysNum(keysNum--);			//udate linear hashing class.
		     }
		     else {	// bucket has an overflow so remove a key from there and bring it here
				 this.keys[i] = this.overflowBucket.removeLastKey(lh);
				 lh.setKeysNum(keysNum--);			//udate linear hashing class
				 if (this.overflowBucket.numKeys() == 0) { // overflow empty free it
					 this.overflowBucket = null;
					 keySpace -= bucketSize;
				     lh.setKeySpace(keySpace);		//udate linear hashing class.
				 }
		     }
		     return;
		   }
		}
		if (this.overflowBucket != null) {			// look at the overflow for the key to be deleted if one exists
		  this.overflowBucket.deleteKey(key, lh);
		  if (this.overflowBucket.numKeys() == 0) {	// overflow empty free it
		    this.overflowBucket = null;
		    keySpace -= bucketSize;
		    lh.setKeySpace(keySpace);				//udate linear hashing class
		  }
	      }
	}

	public int removeLastKey(LinearHashing lh) {	// remove bucket last key

		int retval;
		int bucketSize = lh.getBucketSize();
		int keySpace = lh.getKeySpace();

		if (this.overflowBucket == null) {
		  if (this.keysNumber != 0){
		    this.keysNumber--;
		    return this.keys[this.keysNumber];
		  }
		  return 0;
		}
		else {
		  retval = this.overflowBucket.removeLastKey(lh);
		  if (this.overflowBucket.numKeys() == 0) {	// overflow empty free it
		    this.overflowBucket = null;
		    keySpace -= bucketSize;
		    lh.setKeySpace(keySpace);			//udate linear hashing class
		  }
		  return retval;
		}
	}


	public boolean searchKey(int key, LinearHashing lh) {

		int i;
		int bucketSize = lh.getBucketSize();

		for (i = 0; (i < this.keysNumber) && (i < bucketSize); i++) {
		    if (this.keys[i] == key) {	//key found
			    return true;
		    }
		}

		return false;
	}

	public void splitBucket(LinearHashing lh, int n, int bucketPos, HashBucket newBucket) throws IOException {	//splits the current bucket

		int i;
		int bucketSize = lh.getBucketSize();
		int keySpace = lh.getKeySpace();
		int keysNum = lh.getKeysNum();
		for (i = 0; (i < this.keysNumber) && (i < bucketSize);) {
		   if ((this.keys[i]%n) != bucketPos){	//key goes to new bucket
			     
				 newBucket.insertKey(this.keys[i], lh);
			     this.keysNumber--;
			     keysNum = lh.getKeysNum();
			     keysNum--;
			     lh.setKeysNum(keysNum);		// update linear hashing class.
			     //System.out.println("HashBucket.splitBucket.insertKey: KeysNum = " + keysNum );
			     this.keys[i] = this.keys[this.keysNumber];
		   }
		   else {				// key stays here
			   i++;
		   }
		}

		if (this.overflowBucket != null) 	// split the overflow too if one exists
			this.overflowBucket.splitBucket(lh, n, bucketPos, newBucket);

		while (this.keysNumber != bucketSize) {
		     if (this.overflowBucket == null)
		    	 return;
		     if (this.overflowBucket.numKeys() != 0) {
		    	 this.keys[this.keysNumber] = this.overflowBucket.removeLastKey(lh);
		    	 if (this.overflowBucket.numKeys() == 0) {	// overflow empty free it
		    		 this.overflowBucket = null;
		    		 keySpace -= bucketSize;
		    		 lh.setKeySpace(keySpace);      // update linear hashing class.
		    	 }
		    	 this.keysNumber++;
		     }
		     else {				// overflow empty free it
				 this.overflowBucket = null;
				 keySpace -= bucketSize;
				 lh.setKeySpace(keySpace);	// update linear hashing class.
		     }
	 	}

	}

	public void mergeBucket(LinearHashing lh, HashBucket oldBucket) throws IOException {	//merges the current bucket

		int keysNum = 0;

		while (oldBucket.numKeys() != 0) {
		     this.insertKey(oldBucket.removeLastKey(lh), lh);
		}
	}

	public void printFlowFromFile(RandomAccessFile over) throws IOException{
	    if(this.overflowBucket != null){
	    	System.out.println("Printing overflow of bucket:"+id+" from file");

	    	if(id>0) over.seek(id*512);
	    	else 	 over.seek(0);

	    	if(this.overflowBucket != null){
		    	for(int i=0; i<this.overflowBucket.numKeys(); i++)
		    		System.out.println(over.readInt());
	    	}else{
				System.out.println("The overflow of bucket " +id+ " empty");
			}
	    }
    }

	public int keysize()		{return keys.length;    }
	
    public void printBucket(int bucketSize) {

		int i;

		System.out.println("*** Printing Bucket "+id+" *** ");
		for (i = 0; (i < this.keysNumber) && (i < bucketSize); i++) {
			 System.out.println("key at: " + i + " is: " + this.keys[i]);
		}
		if (this.overflowBucket != null) {
			 System.out.println("printing overflow---");
			 this.overflowBucket.printBucket(bucketSize);
		}
    }

	public HashBucket getOverflow() {return this.overflowBucket; }

	public void printFlowToFile(RandomAccessFile over, int overFlowNo, int bucketsTotal) throws IOException{

		over.seek(bucketsTotal * overFlowNo * 512 + (id*512));
		//r.seek(id*32);
		if(this.overflowBucket != null){
			for(int i=0; i<this.overflowBucket.numKeys(); i++){
				over.writeInt(this.overflowBucket.getKey(i));
				System.out.println("Writing flow "+ this.overflowBucket.keys[i]);
			}
			if(this.overflowBucket.overflowBucket != null){
				this.overflowBucket.printFlowToFile(over, overFlowNo+1, bucketsTotal);
			}
		}
	}
	
	
	public int getNum() {
		return number;
	}

	public void setNum(int num) {
		this.number = number;
	}
    
	public int getKey(int col)	{return keys[col]; 		}
	
	public void print() {
		
	}


} 