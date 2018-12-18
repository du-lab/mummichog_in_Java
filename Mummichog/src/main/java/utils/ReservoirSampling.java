package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservoirSampling  
{ 
    // A function to randomly select k items from stream[0..n-1]. 
    public static List<String> selectKItems(List<String> stream, int k) 
    { 
        int i;   // index for elements in stream[] 
          
        // reservoir[] is the output array. Initialize it with 
        // first k elements from stream[] 
       
        List<String> reservoir = new ArrayList<String>();
        for (i = 0; i < k; i++) 
            reservoir.add(stream.get(i)); 
          
        Random r = new Random(); 
          
        // Iterate from the (k+1)th element to nth element 
        for (; i < stream.size(); i++) 
        { 
            // Pick a random index from 0 to i. 
            int j = r.nextInt(i + 1); 
              
            // If the randomly  picked index is smaller than k, 
            // then replace the element present at the index 
            // with new element from stream 
            if(j < k) 
                reservoir.set(j, stream.get(i));             
        } 
          
        return reservoir;
    }
}