

import java.io.IOException;
import java.io.RandomAccessFile;

class LinearHashing {

	private HashBucket[] hashBuckets;	// pointer to the hash buckets
    HashBucket ha =new HashBucket();
	private float maxThreshold;		// max load factor threshold
	private float minThreshold;		// min load factor threshold
    public   int[] access;
   public int number,z=0,help;
	private int bucketSize;			// max number of keys in each bucket
	private int keysNum;			// number of keys currently stored in the table
	private int keySpace;			// total space the hash table has for keys
	private int p;				// pointer to the next bucket to be split
	private int n;				// current number of buckets
	private int j;				// the n used for the hash function
	private int minBuckets;			// minimum number of buckets this hash table can have
	//private RandomAccessFile r;


	public LinearHashing(int itsBucketSize, int initPages) throws IOException { 	// Constructor.
		int i;

		bucketSize = itsBucketSize;
		keysNum = 0;
		p = 0;
		n = initPages;
		j = initPages;
		minBuckets = initPages;
		keySpace = n*bucketSize;
		maxThreshold = (float)0.8;

		if ((bucketSize == 0) || (n == 0)) {
			System.out.println("error: space for the table cannot be 0");
			System.exit(1);
		}
		hashBuckets = new HashBucket[n];
		for (i = 0; i < n; i++)
			hashBuckets[i] = new HashBucket(bucketSize, i);
		//out = new FileOutputStream("hashtable" );
		//in 	= new FileInputStream ("hashtable" );
		r = new RandomAccessFile("hashtable", "rw");
		over = new RandomAccessFile("overflow", "rw");
	}


	public int hashFunction(int key){	// Returns a hash based on the key

		int retval;

		retval = key%this.j;
		if (retval < 0)
			retval *= -1;
		if (retval >= p){
		//  System.out.println( "Retval = " + retval);
			return retval;
		}
		else {
			retval = key%(2*this.j);
			if (retval < 0)
				retval *= -1;
			// System.out.println( "Retval = " + retval);
		    return retval;
		}
	}

	private float loadFactor() {		// Returns the current load factor of the hash table.

		return ((float)this.keysNum)/((float)this.keySpace);
	}

	private void bucketSplit() throws IOException {		// Splits the bucket pointed by p.

		int i;
		HashBucket[] newHashBuckets;

		newHashBuckets= new HashBucket[n+1];
		for (i = 0; i < this.n; i++){
		   newHashBuckets[i] = this.hashBuckets[i];
		}
        
		hashBuckets = newHashBuckets;
		hashBuckets[this.n] = new HashBucket(this.bucketSize, this.n);
		this.keySpace += this.bucketSize;
		this.hashBuckets[this.p].splitBucket(this, 2*this.j, this.p, hashBuckets[this.n]);
		this.n++;
		if (this.n == 2*this.j) {
			this.j = 2*this.j;
			this.p = 0;
		}
		else {
		    this.p++;
		}
	}

	
	public RandomAccessFile r;
	public static RandomAccessFile over;
	public int getKeysNum() 			{	return keysNum;		}
	public void setBucketSize(int size) {	bucketSize = size;	}
	public int getKeySpace() 			{	return keySpace;	}

	
	public HashBucket getBucket(int col){
        if(this.hashBuckets[col] != null)
            return this.hashBuckets[col];
        else
			System.out.println("No bucket in possition:" + col);
            return null;
    }

    public int getN() {
		return n;
	}


	public void setN(int n) {
		this.n = n;
	}


	public void setKeysNum(int num) 	{	keysNum = num;		}
	public int getBucketSize()  		{	return bucketSize;	}
    public void setKeySpace(int space) 	{	keySpace = space;	}

	public boolean searchKey(int key) {		// Search for a key.

		return this.hashBuckets[this.hashFunction(key)].searchKey(key, this);
	}

	public void printHash() {

		int i;
		System.out.println(" ******  Printing the Hash ********");
		for (i = 0; i < this.n; i++) {
		   this.hashBuckets[i].printBucket(this.bucketSize);
		}
	}

	public void printToFile() throws IOException{
		for(int i=0; i<n; i++){
			r.seek(i*512);
			int keys = this.hashBuckets[i].numKeys();
			for(int j=0; j<keys; j++){
				int key = this.hashBuckets[i].getKey(j);
				System.out.println("writing key :" + key + " To file");
				r.writeInt(key);
			}
			this.hashBuckets[i].printFlowToFile(this.over, 0, n);
		}

	}
	 
	public void insertKey(int key,int [] access,HashBucket hashb) throws IOException {	// Insert a new key.
		number+=1;
		System.out.println( "hashBuckets[" + this.hashFunction(key) + "] =  " + key);
		
		int row = this.hashFunction(key);
		System.out.println(number+ "bucket" + n);
		access[n]=hashb.getNumber();
		this.hashBuckets[row].insertKey(key, this);
		if (this.loadFactor() > maxThreshold){
			access[n]=access[n]/number;
		    number=0;
		 	// System.out.println("loadFactor = " + this.loadFactor() );
		  	this.bucketSplit();
		  	
		  	System.out.println("BucketSplit");
		}
	}
	
	
	
	
	
	
	
	
	


	

	public int searchDisk(int key) throws IOException{
		int row = this.hashFunction(key);
		int counter = 0;
		boolean found = false;
		int keys=0;
		// Edw h metavlhth row den mas xreiazetai gia thn dhlwsh tou HashBucket.
		HashBucket loader; //= new HashBucket(128/4, row);
		r.seek(row*512);
		keys = this.hashBuckets[row].numKeys();
		//System.out.println("keys are "+ keys);
		loader = this.hashBuckets[row];

		HashBucket iter = loader;
		System.out.println("Printing the itteration.");
		//iter.printBucket(128);
		//Edw metrame ths fores pou diavazoume dhladh ta overflow.
		while(iter != null){

			if(iter.searchKey(key, this) == true){
				return counter++;
			}else{
				iter = iter.getOverflow();
				counter++;				//Edw pragmatopoioume allh mia prospelash ston disko.
			}
		}
		return counter;
	}

	public void printHashFromFile() throws IOException{
		System.out.println(" Printing HASH from FILE /n");

		for(int i=0; i<n; i++){
			System.out.println(" Bucket number : ["+i+"].");
			int keys = hashBuckets[i].numKeys();
			r.seek(i*512);

			for(int j=0; j<keys; j++)
				System.out.println("The key "+j+": "+ r.readInt());

			this.hashBuckets[i].printFlowFromFile(this.over);
		}
	}

} // Edw teleiwnei h Linear Hashing Class.
