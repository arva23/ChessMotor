package genmath;

import java.util.ArrayList;

/* !@brief TreeMap with linear container to provide backward and forward iteration along with
 *    the benefit of tree structure (logarithmic access of elements)
 */
public class LinTreeMap<K extends ComparableKey<K>, V> {

    // pair auxiliary type
    public static class Pair<K extends ComparableKey<K>, V> implements Comparable<Pair<K, V> >{

        public Pair(){

            this.key = null;
            this.value = null;
        }

        public Pair(K key, V value) throws Exception{

            if(key == null) throw new Exception("Key is null.");

            this.key = key;

            if(value == null) throw new Exception("Value is null.");
            this.value = value;
        }

        public int compareTo(Pair<K, V> item){

            return key.compareTo(item.key);
        }

        public K key;
        public V value;

    }
    //private int size;// number of points

    private int size;
    // inorder traversal based linear storage
    // linearized AVL tree, all nodes are used for storage, not only leaves
    //  (augment all null leaves to fulfill the condition of logarithmic indexing)
    private ArrayList<Pair<K, V> > container;


    public LinTreeMap(){

        size = 0;
        container = new ArrayList<Pair<K, V> >();
    }


    public void add(K key, V value) throws Exception{

        int cmpRes = 0;

        if(size == 0){

            container.add(new Pair<K, V>(key, value));
            ++size;
        }
        else if(size == 1){

            cmpRes = key.compareTo(container.get(0).key);
            if(cmpRes == 0)
                throw new Exception("The item has already been inserted earlier (redundancy is not allowed).");
            else if(cmpRes < 0)
                container.add(0, new Pair<K, V>(key, value));
            else
                container.add(new Pair<K, V>(key, value));

            ++size;
        }
        else {

            int higherKeyI = -1;// due to element shift mechanism of add function of container
            int i = (int)Math.ceil(size / 2.0);
            int step = (int)Math.ceil(size / 2.0);
            K higherDiff = key.maxVal();
            K tmpDiff = key.maxVal();

            while(0 <= i && i < size && step > 0){

                cmpRes = key.compareTo(container.get(i).key);

                if(cmpRes == 0)
                    throw new Exception("The item has already been inserted earlier (redundancy is not allowed).");

                tmpDiff = key.subtract(container.get(i).key);

                if(tmpDiff.sgn() > 0 && tmpDiff.compareTo(higherDiff) < 0){

                    higherDiff = tmpDiff;
                    higherKeyI = i;
                }

                if(cmpRes < 0){

                    step = (int)Math.ceil(step / 2.0);
                    i -= step;
                }
                else{

                    step = (int)Math.floor(step / 2.0);
                    i += step;
                }
            }

            // balancing tree
            //if(higherKeyI > 0)
                container.add(higherKeyI + 1, new Pair<K, V>(key, value));
            //else
              //  container.add(0, new Pair<K, V>(key, value));

            ++size;
            // the order of the tree is balanced, it does not require any sort
            //  due to preserved order by finding the higherKey
        }
    }


    public void add(Pair<K, V> entry) throws Exception {

        add(entry.key, entry.value);
    }
    
    
    public void setOrAddByKey(K key, V value) throws Exception{
        
        int cmpRes = 0;

        if(size == 0){

            container.add(new Pair<K, V>(key, value));
            ++size;
        }
        else if(size == 1){

            cmpRes = key.compareTo(container.get(0).key);
            if(cmpRes == 0){
            
                // setting new value
                Pair<K, V> modPair = container.get(0);
                modPair.value = value;
                container.set(0, modPair);
                
                return;
            }
            else if(cmpRes < 0){
                
                container.add(0, new Pair<K, V>(key, value));
            }
            else{
                
                container.add(new Pair<K, V>(key, value));
            }

            ++size;
        }
        else {

            int higherKeyI = -1;// due to element shift mechanism of add function of container
            int i = (int)Math.ceil(size / 2.0);
            int step = (int)Math.ceil(size / 2.0);
            K higherDiff = key.maxVal();
            K tmpDiff = key.maxVal();

            while(0 <= i && i < size && step > 0){

                cmpRes = key.compareTo(container.get(i).key);

                if(cmpRes == 0){
                
                    // setting new value
                    Pair<K, V> modPair = container.get(i);
                    modPair.value = value;
                    container.set(i, modPair);
                    
                    return;
                }

                tmpDiff = key.subtract(container.get(i).key);

                if(tmpDiff.sgn() > 0 && tmpDiff.compareTo(higherDiff) < 0){

                    higherDiff = tmpDiff;
                    higherKeyI = i;
                }

                if(cmpRes < 0){

                    step = (int)Math.ceil(step / 2.0);
                    i -= step;
                }
                else{

                    step = (int)Math.floor(step / 2.0);
                    i += step;
                }
            }

            // balancing tree
            //if(higherKeyI > 0)
                container.add(higherKeyI + 1, new Pair<K, V>(key, value));
            //else
              //  container.add(0, new Pair<K, V>(key, value));

            ++size;
            // the order of the tree is balanced, it does not require any sort
            //  due to preserved order by finding the higherKey
        }
    }

    public V getByInd(int i) throws Exception{

        if(i >= size) throw new Exception("Index out of bounds.");

        return container.get(i).value;
    }


    /* !@brief Setter that modifies key by index
     *          It does not check equivalence.
     *
     * @param[in] i Index to the given element
     * @param[in] key New key that replaces old one */
    public void setKeyByInd(int i, K key) throws Exception{

        if(i >= size) throw new Exception("Index out of bounds.");

        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        container.set(i, modPair);
    }


    /* !@brief Setter that modifies value by index
    *          It does not check equivalence.
    *
    * @param[in] i Index to the given element
    * @param[in] value New value that replaces the old one */
    public void setValByInd(int i, V value) throws Exception{

        if(i >= size) throw new Exception("Index out of bounds.");

        Pair<K, V> modPair = container.get(i);
        modPair.value = value;
        container.set(i, modPair);
    }


    /* !@brief Setter that modifies key and value by index
    *          It does not check equivalence.
    *
    * @param[in] i Index to the given element
    * @param[in] key New key that replaces old one
    * @param[in] value New value that replaces the old one */
    public void setByInd(int i, K key, V value) throws Exception{

        if(i >= size) throw new Exception("Index out of bounds.");

        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        modPair.value = value;
        container.set(i, modPair);
    }


    public V getByKey(K key){

        int i = (int)Math.ceil(size / 2.0);
        int step = (int)Math.ceil(size / 2.0);
        int cmpRes = 0;

        while(0 <= i && i < size && step > 0){

            cmpRes = key.compareTo(container.get(i).key);

            if(cmpRes == 0) return container.get(i).value;// equal

            if(cmpRes < 0){

                step = (int)Math.ceil(step / 2.0);// less, left child node
                i -= step;
            }
            else{

                step = (int)Math.floor(step / 2.0);// greater, right child node
                i += step;
            }
        }

        return container.get(0).value;// no element has matched with the given key, returning first element
    }


    public int getIndByKey(K key){

        int i = (int)Math.ceil(size / 2.0);
        int step = (int)Math.ceil(size / 2.0);
        int cmpRes = 0;

        while(0 <= i && i < size && step > 0){

            cmpRes = key.compareTo(container.get(i).key);

            if(cmpRes == 0) return i;

            if(cmpRes < 0){

                step = (int)Math.ceil(step / 2.0);
                i -= step;
            }
            else{

                step = (int)Math.floor(step / 2.0);
                i += step;
            }
        }

        return i;// no item found, return the last element
    }

    public K getKeyByInd(int ind) throws Exception{

        if(ind >= size) throw new Exception("Index out of bounds.");

        return container.get(ind).key;
    }


    public K lowerKey(K key) throws Exception{

        if(size > 0){

            int lowerKeyI = 0;
            int i = (int)Math.ceil(size / 2.0);
            int step = (int)Math.ceil(size / 2.0);
            int cmpRes = 0;
            K diff = key.maxVal();
            K tmpDiff = key.maxVal();

            while(0 <= i && i < size && step > 0){

                cmpRes = key.compareTo(container.get(i).key);

                if(cmpRes == 0) return key;

                tmpDiff = key.subtract(container.get(i).key);

                if(tmpDiff.sgn() < 0 && tmpDiff.compareTo(diff) >= 0){

                    diff = tmpDiff;
                    lowerKeyI = i;
                }

                if(cmpRes < 0){

                    step = (int)Math.ceil(step / 2.0);
                    i -= step;// less, left child node
                }
                else{

                    step = (int)Math.floor(step / 2.0);
                    i += step;// greater, right child node
                }
            }

            return container.get(lowerKeyI).key;
        }
        else{

            throw new Exception("Container is empty.");
        }
    }


    public K higherKey(K key) throws Exception{

        if(size > 0){

            int higherKeyI = size - 1;
            int i = (int)Math.ceil(size / 2.0);
            int step = (int)Math.ceil(size / 2.0);
            int cmpRes = 0;
            K diff = container.get(0).key;
            K tmpDiff = container.get(0).key;

            while(0 <= i && i < size && step > 0){

                cmpRes = key.compareTo(container.get(i).key);

                if(cmpRes == 0) return key;

                tmpDiff = key.subtract(container.get(i).key);

                if(tmpDiff.sgn() > 0 && tmpDiff.compareTo(diff) <= 0){

                    diff = tmpDiff;
                    higherKeyI = i;
                }

                if(cmpRes < 0){

                    step = (int)Math.ceil(step / 2.0);
                    i -= step;// less, left child node
                }
                else{

                    step = (int)Math.floor(step / 2.0);
                    i += step;// greater, right child node
                }
            }

            return container.get(higherKeyI).key;
        }
        else{

            throw new Exception("Container is empty.");
        }
    }


    public int size(){

        return size;
    }


    /* !@brief Creates a submap from the actual map using lower and upper bound keys.
    *          If the lower bound, upper bound or both do not exist, it finds the higher key of lower
    *          bound and in need the lower key of upper bound. The infimum and supremum boundaries.
    *   @param[in] lowerKeyBound the infiumum lower bound or identical lower bound of the new interval
    *   @param[in] upperKeyBound the supremum upper bound or identical upper bound of the new interval
    *   @param[out] the newly created submap */
    public LinTreeMap<K, V> subMap(K lowerKeyBound, K upperKeyBound) throws Exception{

        int i = 0;
        LinTreeMap<K, V> resMap = new LinTreeMap<K, V>();

        if(size == 0)
            throw new Exception("Empty container.");

        lowerKeyBound = higherKey(lowerKeyBound);
        upperKeyBound = lowerKey(upperKeyBound);

        for(; i < size; ++i)
            if(container.get(i).key.compareTo(lowerKeyBound) == 0)
                break;

        int lowerI = i;

        for(; i < size; ++i){

            resMap.add(container.get(i));

            if(container.get(i).key.compareTo(upperKeyBound) == 0) break;
        }

        return resMap;
    }

    public ArrayList<Pair<K, V> > getContainer(){

        return container;
    }

    public void RemoveAll(){

        container.clear();
    }

    public void sort() throws Exception{

        LinTreeMap<K, V> tmp = new LinTreeMap<K, V>();

        for(int i = 0; i < size; ++i)
            tmp.add(container.get(i));

        container = tmp.getContainer();
    }
}
