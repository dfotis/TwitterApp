/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterapp1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author someone2
 */
public class Similarity {
    
    Similarity() {
        
    }
    
    
    public double cosineSimilarity(String s1, String s2) {
         double similarity=0;
         String temp = s1+" "+s2; //merge two strings
         
         String[] allTokens = temp.split(" "); //tokenize the new string
         Set<String> allTokensSet = new HashSet<String>(Arrays.asList(allTokens)); //use SET to remove duplicates
                  
         String[] tokensFirstTemp = s1.split(" ");
         String[] tokensSecondTemp = s2.split(" ");
                  
         //if the size allTokens is equal to the size of (tokensFirstTemp + tokensSecondTemp) means that there are no 
         //common tokens words between s1 and s2. So cosine_similarity = 0.         
         if(allTokensSet.size() == (tokensFirstTemp.length + tokensSecondTemp.length))
         {
             return 0;
         }
         
         List<String> tokensFirstList = new ArrayList(Arrays.asList(tokensFirstTemp));
         List<String> tokensSecondList = new ArrayList(Arrays.asList(tokensSecondTemp));
         
         //vectors for each string
         int[] vctr1 = new int[allTokens.length];
         int[] vctr2 = new int[allTokens.length]; 
         
         Iterator it = allTokensSet.iterator();
         int index=0;
         while(it.hasNext()) 
         {
             String temp2 = it.next().toString();
             
             while(tokensFirstList.contains(temp2))
             {
                 tokensFirstList.remove(temp2);
                 vctr1[index]++;
             }
             while(tokensSecondList.contains(temp2))
             {
                 tokensSecondList.remove(temp2);
                 vctr2[index]++;
             }             
             index++;
         }
         
         //calculate dot product of vctr1 and vctr2
         double dotProduct = 0;
         for(int i=0; i<vctr1.length; i++)
         {
             dotProduct += vctr1[i] * vctr2[i];
         }
          
         //calculate norm1 and norm2
         double norm1=0;
         double norm2=0;
         for(int i=0; i<vctr1.length; i++){
             norm1 += vctr1[i]*vctr1[i];
             norm2 += vctr2[i]*vctr2[i];
         }
         
         similarity = dotProduct / Math.sqrt(norm1*norm2);
         
         return similarity;
     }
    
}
