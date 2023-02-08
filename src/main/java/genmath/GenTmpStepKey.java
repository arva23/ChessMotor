package genmath;

public class GenTmpStepKey extends ComparableKey<GenTmpStepKey> {

    public Double val = 0.0;
    public boolean isPlaceholder;
    
    public GenTmpStepKey(){
    
        isPlaceholder = true;
    }
    
    public GenTmpStepKey(Double val){

        super();

        this.val = val;
        isPlaceholder = false;
    }

    @Override
    public GenTmpStepKey maxVal(){

        return new GenTmpStepKey(Double.MAX_VALUE);
    }

    @Override
    public GenTmpStepKey min(GenTmpStepKey key) {

        if(val < key.val) return this;
        else return key;
    }


    @Override
    public int sgn() {

        if(val == 0) return 0;
        else if(val > 0) return 1;
        else return -1;
    }

    @Override
    public int compareTo(GenTmpStepKey key) {

        return val.compareTo(key.val);
    }


    @Override
    public GenTmpStepKey add(GenTmpStepKey key) {
        
        return new GenTmpStepKey(val + key.val);
    }


    @Override
    public GenTmpStepKey subtract(GenTmpStepKey key) {
        
        return new GenTmpStepKey(val - key.val);
    }

    public static String toString(GenTmpStepKey key) {

        return "" + key.val;
    }

    public static GenTmpStepKey fromString(String rawData) {

        return new GenTmpStepKey(Double.parseDouble(rawData));
    }
    
    @Override
    public boolean isPlaceholder(){
    
        return isPlaceholder;
    }
}