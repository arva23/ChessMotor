package chessmotor.enginecontroller;

import genmath.ComparableKey;

public class GenStepKey extends ComparableKey<GenStepKey> {

    public String val;
    
    public GenStepKey(){
    
    }
    
    public GenStepKey(String val){

        super();

        this.val = val;
    }

    @Override
    public GenStepKey maxVal(){

        return new GenStepKey("z");
    }

    @Override
    public GenStepKey min(GenStepKey key) {

        if(val.compareTo(key.val) < 0) return this;
        else return key;
    }


    @Override
    public int sgn() {

        if(val.isEmpty()) return 0;
        else return 1;
    }

    @Override
    public int compareTo(GenStepKey key) {

        return val.compareTo(key.val);
    }


    @Override
    public int len(){
    
        return val.length();
    }
    
    
    @Override
    public int at(int pos){
    
        if(val.length() < pos + 1) return Integer.MIN_VALUE;
        
        return val.charAt(pos);
    }
    
    
    @Override
    public GenStepKey add(GenStepKey key) {
        
        return new GenStepKey(val + key.val);
    }


    @Override
    public GenStepKey subtract(GenStepKey key) {
        
        GenStepKey newKey = new GenStepKey(key.val);
        
        int size = val.length();
        String newVal = "";
        
        for(int i = 0; i < size; ++i){
        
            // suboptimal
            if(!(key.val.contains("" + val.charAt(i)))) newVal += val.charAt(i);
        }
        
        return newKey;
    }

    public static String toString(GenStepKey key) {

        return key.val;
    }

    public static GenStepKey fromString(String rawData) {

        return new GenStepKey(rawData);
    }
}