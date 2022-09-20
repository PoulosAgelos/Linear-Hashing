

import java.io.*;
import java.util.*;


public class LinearMain {

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		int initPages = 1, pageSize = 128, i=0, j=0, keysNo, searchNo, deleteNo, insertNo, searchTotal, insertTotal, deleteTotal, step;
		int[] keysEntered;
		HashBucket hash2 = new HashBucket();
		LinearHashing Hash1 = new LinearHashing(pageSize, initPages);
		System.out.println("Creating Linear Hash Table....");

		keysNo =45000;
	    int[] array ;
	     array = new int [41];
	     for(int ar=0; ar<41; ar++) {
	    	 array[ar]=0;
	     }
		keysEntered = new int[1000];
		Random r = new Random();
		int help=Hash1.getN();
		
		for(int k = 0; k<1000; k++) {
			keysEntered[k]=r.nextInt()%10000;
		}
	    
		step=keysNo/40;
		System.out.println( "Keys currently stored in the table = " + Hash1.getKeysNum());
		while(Hash1.getN() < 40){
			int value = r.nextInt()%10000;
			if(value<0)
				value = -value;
			Hash1.insertKey(value,array,hash2);
			
			j++;
			
		}
		//Hash1.putInBucket(0, 1000);
		System.out.println("done");

		/*Hash1.printHash();
		Hash1.printToFile();
		Hash1.printHashFromFile();*/

		
	    searchNo=20;
	

		int result, sum_result=0, search_key;
		//System.out.println("\n****     Searching for " + searchNo + " existing keys........");

		for (i = 0; i < 15; i++){
			
		//System.out.println("Searching for "+keysEntered[i]);
			result = Hash1.searchDisk(keysEntered[i]/*, Hash1.getBucket(Hash1.hashFunction(search_key)).getBucketSize()*/);
			if (result != 0){
			//System.out.println("i = "+i+" Key: " +keysEntered[i] + " found after "+result+" disk accesses." );
			sum_result += result;
				
			}
			else{
				//System.out.println("i = "+i+" Not found!!!!");
				//System.in.read();
				sum_result+=1;
				
			}
			
	}
		System.out.println("SUM disk accesses : "+ sum_result);
       
		float mo =(float)sum_result/(float)15;
		System.out.println("MO disk accesses : "+ mo); 
		
		Hash1.r.close();
	}
}
