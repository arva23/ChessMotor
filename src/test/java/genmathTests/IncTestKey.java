package genmathTests;

import genmath.IncComparableKey;

public class IncTestKey extends IncComparableKey<IncTestKey> {

    public Integer val;
    public boolean isPlaceholder;

    public IncTestKey(Integer val){

        super();

        this.val = val;
    }

    @Override
    public IncTestKey maxVal(){

        return new IncTestKey(Integer.MAX_VALUE);
    }

    @Override
    public IncTestKey less(IncTestKey key) {

        if(this.val <= key.val) return this;
        else return key;
    }
    
    @Override
    public IncTestKey greater(IncTestKey key){
    
        if(this.val >= key.val) return this;
        else return key;
    }

    @Override
    public int sgn() {

        if(this.val == 0) return 0;
        else if(this.val < 0) return -1;
        else return 1;
    }

    @Override
    public int compareTo(IncTestKey key) {

        return this.val.compareTo(key.val);
    }

    @Override
    public int at(int pos){
    
        return val.toString().charAt(pos);
    }
    
    @Override
    public int len(){
    
        return val.toString().length();
    }
    
    @Override
    public IncTestKey add(IncTestKey key) {

        return new IncTestKey(this.val + key.val);
    }


    @Override
    public IncTestKey subtract(IncTestKey key) {

        return new IncTestKey(this.val - key.val);
    }
    
    @Override
    public boolean isPlaceholder(){
    
        return isPlaceholder;
    }
}