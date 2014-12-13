package com.example.locationproject.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import android.util.Log;

import com.example.locationproject.StaticReferenceDB;
import com.example.locationproject.datamodel.Response;

public class TopKFinder {
	private static final String TAG = "TopKFinder";
	
	public static String[] getTopK(List<Response> respList){
		String [] result = new String[StaticReferenceDB.k];
		Map<String,Integer> cumulativeAddressMap = new HashMap<>();
		for(Response r:respList){
			Map<String,Integer> temp = r.getAddressMap();
			for(Entry <String,Integer> e: temp.entrySet()){
				if(cumulativeAddressMap.containsKey(e.getKey())){
					int previousVal = cumulativeAddressMap.get(e.getKey());
					cumulativeAddressMap.put(e.getKey(), e.getValue()+previousVal);
				}else{
					cumulativeAddressMap.put(e.getKey(), e.getValue());
				}
				
			}
		}
		Queue<Entry<String,Integer>> customerPriorityQueue = new PriorityQueue<>(cumulativeAddressMap.size(), idComparator);
		for(Entry<String,Integer> e:cumulativeAddressMap.entrySet()){
			Log.d(TAG+" cumulative queue elements",e.getKey()+" "+e.getValue());
			
			customerPriorityQueue.add(e);
		}
		int i=0;
		while(i<StaticReferenceDB.k){
			result[i] = customerPriorityQueue.poll().getKey();
			i += 1;
		}
		for(String s :result){
			System.out.println("TopK areas in result "+s);
		}
		
		return result;
		
		
		
	}
	
	public static Comparator<Entry<String,Integer>> idComparator = new Comparator<Entry<String,Integer>>(){
        
        
		@Override
		public int compare(Entry <String,Integer>r1, Entry<String,Integer> r2) {
			if(r2.getValue() - r1.getValue()==0){
				Log.d(TAG,"r1="+r1.getKey()+r1.getValue()+"r2="+r2.getKey()+r2.getValue());
				r2.setValue(r2.getValue()+1);
				Log.d(TAG,"r2 value after increment="+r2.getValue());
				
			}
			
				return (int) (r2.getValue() - r1.getValue());
			
		}
    };

}
