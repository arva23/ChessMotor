package genmath;

import java.util.ArrayList;

public class IncBinTree<K extends ComparableKey<K>, V> extends LinTreeMap<K, V> {

    private int leafLevel;

    // traversal
    private int travI;
    private double travStep;
    private int travLen;
    private boolean wasStepBack;
    private ArrayList<Boolean> stepDirHist;
    private ArrayList<Double> stepHist;
    private ArrayList<Integer> stepIndHist;
    private int numOfCumulatedStepBacks;
    
    public IncBinTree() {

        super();
        
        leafLevel = 0;

        travI = 0;
        travStep = 0.0;
        travLen = 0;
        wasStepBack = false;
        stepDirHist = new ArrayList<>();
        stepHist = new ArrayList<>();
        stepIndHist = new ArrayList<>();
        numOfCumulatedStepBacks = 0;
    }

    public IncBinTree(IncBinTree<K, V> orig) {

        super(orig);
        
        this.leafLevel = orig.leafLevel;

        this.travI = orig.travI;
        this.travStep = orig.travStep;
        this.travLen = orig.travLen;
        this.wasStepBack = orig.wasStepBack;
        this.stepDirHist = orig.stepDirHist;
        this.stepHist = orig.stepHist;
        this.stepIndHist = orig.stepIndHist;
        this.numOfCumulatedStepBacks = orig.numOfCumulatedStepBacks;
    }

    private void increaseCapacity() {

        int insertionShift = 0;
        int insertionLimit = (int) Math.ceil((int) Math.pow(2, leafLevel) / 2.0);

        for (int i = 0; i < insertionLimit; ++i) {

            container.add(insertionShift, new Pair<>());
            insertionShift += 2;
            container.add(insertionShift, new Pair<>());
            insertionShift += 2;
        }

        size = container.size();
        ++leafLevel;
    }

    @Override
    public int add(K key, V value) throws Exception {

        int cmpRes;

        if (size == 0) {

            container.add(new Pair<>(key, value));
            ++size;
            return 0;
        } else if (size == 1) {

            cmpRes = key.compareTo(container.get(0).key);

            if (cmpRes == 0) {

                throw new Exception("The item has already been inserted.");
            } else if (cmpRes < 0) {

                container.add(0, new Pair<>(key, value));
                ++size;
                return 0;
            } else {

                container.add(new Pair<>(key, value));
                ++size;
                return 1;
            }
        } else {

            int lowerKeyI = -1;// due to element shift mechanism off add function of container
            int i = (int) Math.ceil(size / 2.0);
            double step = size / 2.0;
            K higherDiff = key.maxVal();
            K tmpDiff;

            while (0 <= i && i < size && step > 0.5) {

                cmpRes = key.compareTo(container.get(i).key);

                if (cmpRes == 0) {
                    throw new Exception("The item has already been inserted earlier (redundancy is not allowed).");
                }

                tmpDiff = key.subtract(container.get(i).key);

                if (tmpDiff.sgn() > 0 && tmpDiff.compareTo(higherDiff) < 0) {

                    higherDiff = tmpDiff;
                    lowerKeyI = i;
                }

                step /= 2.0;
                
                if (cmpRes < 0) {

                    i -= (int) Math.ceil(step);
                } else {

                    i += (int) Math.ceil(step);
                }
            }

            if (0 < lowerKeyI || lowerKeyI + 1 < size
                    || !(container.get(lowerKeyI + 1).key.isPlaceholder())) {

                increaseCapacity();
            }

            container.set(lowerKeyI + 1, new Pair<>(key, value));

            ++size;

            return lowerKeyI + 1;
        }
    }

    @Override
    public int add(Pair<K, V> entry) throws Exception {

        return add(entry.key, entry.value);
    }

    @Override
    public int setOrAddByKey(K key, V value) throws Exception {

        int cmpRes;

        if (size == 0) {

            container.add(new Pair<>(key, value));
            ++size;
            return 0;
        } else if (size == 1) {

            cmpRes = key.compareTo(container.get(0).key);

            if (cmpRes == 0) {

                // setting new value
                Pair<K, V> modPair = container.get(0);
                modPair.value = value;
                container.set(0, modPair);

                return 0;
            } else if (cmpRes < 0) {

                container.add(0, new Pair<>(key, value));
                ++size;
                return 0;
            } else {

                container.add(new Pair<>(key, value));
                ++size;
                return 1;
            }
        } else {

            int lowerKeyI = -1;// due to element shift mechanism off add function of container
            int i = (int) Math.ceil(size / 2.0);
            double step = size / 2.0;
            K higherDiff = key.maxVal();
            K tmpDiff;

            while (0 <= i && i < size && step > 0.5) {

                cmpRes = key.compareTo(container.get(i).key);

                if (cmpRes == 0) {

                    // setting new value
                    Pair<K, V> modPair = container.get(i);
                    modPair.value = value;
                    container.set(i, modPair);

                    return i;
                }

                tmpDiff = key.subtract(container.get(i).key);

                if (tmpDiff.sgn() > 0 && tmpDiff.compareTo(higherDiff) < 0) {

                    higherDiff = tmpDiff;
                    lowerKeyI = i;
                }

                step /= 2.0;
                
                if (cmpRes < 0) {

                    i -= (int) Math.ceil(step);
                } else {

                    i += (int) Math.ceil(step);
                }
            }

            if (0 > lowerKeyI || lowerKeyI + 1 >= size
                    || !(container.get(lowerKeyI + 1).key.isPlaceholder())) {

                increaseCapacity();
            }

            container.set(lowerKeyI + 1, new Pair<>(key, value));

            ++size;

            return lowerKeyI + 1;
        }
    }

    @Override
    public V getByInd(int i) throws Exception {

        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        return container.get(i).value;
    }

    @Override
    public void setKeyByInd(int i, K key) throws Exception {

        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        container.set(i, modPair);
    }

    @Override
    public void setValByInd(int i, V value) throws Exception {

        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        Pair<K, V> modPair = container.get(i);
        modPair.value = value;
        container.set(i, modPair);
    }

    @Override
    public void setByInd(int i, K key, V value) throws Exception {

        if (i >= size) {

            throw new Exception("Index out of bounds.");
        }

        if (container.get(i).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        Pair<K, V> modPair = container.get(i);
        modPair.key = key;
        modPair.value = value;
        container.set(i, modPair);
    }

    @Override
    public K getKeyByInd(int ind) throws Exception {

        if (ind >= size) {
            throw new Exception("Index out of bounds.");
        }

        if (container.get(ind).key.isPlaceholder()) {

            throw new Exception("Item is placeholder.");
        }

        return container.get(ind).key;
    }

    @Override
    public IncBinTree<K, V> subMap(K lowerKeyBound, K upperKeyBound) throws Exception {

        // submap by leaf level interval
        IncBinTree<K, V> resMap = new IncBinTree<>(this);

        if (size == 0) {
            throw new Exception("Empty container.");
        }

        lowerKeyBound = higherKey(lowerKeyBound);
        upperKeyBound = lowerKey(upperKeyBound);

        // finding level limit for cut
        int begLevel = Math.max(getLevelByKey(lowerKeyBound),
                getLevelByKey(upperKeyBound));

        // dropping unnecessary levels
        for (int lvl = leafLevel; lvl > begLevel; --lvl) {
            for (int i = 0; i < size; ++i) {
                resMap.container.remove(i);
            }
        }

        // finding minimum node by index
        int i = (int) Math.ceil(size / 2.0);
        double step = size / 2.0;
        int cmpRes;

        ArrayList<Integer> minIList = new ArrayList<>();
        ArrayList<Integer> maxIList = new ArrayList<>();

        // defining lower bounds
        while (0 <= i && i < size && step > 0.5) {

            cmpRes = resMap.container.get(i).key.compareTo(lowerKeyBound);

            if (cmpRes == 0) {
                break;
            }

            if (cmpRes < 0) {

                step /= 2.0;
                i -= (int) Math.ceil(step);
            } else {

                step /= 2.0;
                i += (int) Math.ceil(step);
            }

            minIList.add(i);
        }

        // defining upper bounds
        i = (int) Math.ceil(size / 2.0);
        step = size / 2.0;

        while (0 <= i && i < size && step > 0.5) {

            cmpRes = resMap.container.get(i).key.compareTo(upperKeyBound);

            if (cmpRes == 0) {
                break;
            }

            step /= 2.0;
            
            if (cmpRes < 0) {

                i -= (int) Math.ceil(step);
            } else {

                i += (int) Math.ceil(step);
            }

            maxIList.add(i);
        }

        if (minIList.size() < maxIList.size()) {

            int iListSizeDiff = maxIList.size() - minIList.size();

            for (i = 0; i < iListSizeDiff; ++i) {
                minIList.add(maxIList.get(i));
            }
        } else {

            int iListSizeDiff = minIList.size() - maxIList.size();

            for (i = 0; i < iListSizeDiff; ++i) {
                maxIList.add(minIList.get(i));
            }
        }

        // nulling not selected nodes
        int sizeOfLimits = minIList.size();
        int startInd = 0;
        int indStep;

        for (i = 0; i < sizeOfLimits; ++i) {

            startInd += i;
            indStep = (int) Math.pow(2, sizeOfLimits - i);

            for (int j = startInd; j < minIList.get(i); j += indStep) {
                container.set(j, new Pair<>());
            }

            for (int j = size - startInd; j > maxIList.get(i); j -= indStep) {
                container.set(j, new Pair<>());
            }
        }

        return resMap;
    }

    @Override
    public void sort() throws Exception {

        throw new Exception("Sort is not allowed due to structural preservation.");
    }

    public void initDFS() {

        stepDirHist = new ArrayList<>();
        stepHist = new ArrayList<>();
        stepIndHist = new ArrayList<>();

        travI = (int) Math.ceil(size / 2.0);
        travStep = (int) Math.ceil(size / 2.0);
        travLen = 1;
        wasStepBack = false;

        stepDirHist.add(true);
        stepHist.add(travStep);
        stepIndHist.add(travI);
        
        numOfCumulatedStepBacks = 0;
    }

    public Pair<K, V> getNextItemDFS() {

        if (size == 0) {
            return new Pair<>();
        }

        if (travI >= size) {
            return container.get(stepIndHist.get(travLen - 1));
        }

        if (container.get(travI).key.isPlaceholder()) {

            numOfCumulatedStepBacks = 0;
            
            while (travLen > 0 && !stepDirHist.get(travLen - 1)) {

                stepDirHist.remove(travLen - 1);
                stepHist.remove(travLen - 1);
                stepIndHist.remove(travLen - 1);
                --travLen;

                wasStepBack = true;
                ++numOfCumulatedStepBacks;
            }

            if (travLen == 0) {
                return new Pair<>();
            }

            stepDirHist.set(travLen - 1, false);

            stepDirHist.add(true);

            travI += (int) Math.ceil(stepHist.get(travLen - 1));
            stepIndHist.add(travI);

            stepHist.add(stepHist.get(travLen - 1) / 2.0);

            ++travLen;

            return container.get(travI);
        } else {

            wasStepBack = false;

            stepDirHist.add(true);

            travI -= (int) Math.ceil(stepHist.get(travLen - 1));
            stepIndHist.add(travI);

            stepHist.add(stepHist.get(travLen - 1) / 2.0);

            ++travLen;

            return container.get(travI);
        }
    }

    public boolean hasNextDFS() {

        return travLen > 0 && stepDirHist.get(0);
    }

    public boolean wasRecentLeaf() {

        return wasStepBack;
    }
    
    public int getNumOfRecentStepBacks(){
    
        return numOfCumulatedStepBacks;
    }
}
