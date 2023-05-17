package genmath;

import genmath.genmathexceptions.NoObjectFoundException;
import java.util.ArrayList;

public class LinTreeMultiMap<K extends ComparableKey<K>, V> extends LinTreeMap<K, V> {

    public LinTreeMultiMap() {

        super();
    }

    public LinTreeMultiMap(LinTreeMultiMap<K, V> orig) {

        super(orig);
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

            if (cmpRes <= 0) {

                container.add(0, new Pair<>(key, value));
                ++size;
                return 0;
            } else {

                container.add(new Pair<>(key, value));
                ++size;
                return 1;
            }
        } else {

            int lowerKeyI = -1;
            int i = (int) Math.ceil(size / 2.0);
            double step = size / 2.0;
            K lowerDiff = key.maxVal();
            K tmpDiff;
            while (0 <= i && i < size && step > 0.5) {

                cmpRes = key.compareTo(container.get(i).key);

                tmpDiff = key.subtract(container.get(i).key);

                if (tmpDiff.sgn() > 0 && tmpDiff.compareTo(lowerDiff) < 0) {

                    lowerDiff = tmpDiff;
                    lowerKeyI = i;
                }

                step /= 2.0;

                if (cmpRes <= 0) {

                    i -= (int) Math.ceil(step);
                } else {

                    i += (int) Math.ceil(step);
                }
            }

            container.add(lowerKeyI + 1, new Pair<>(key, value));

            ++size;

            return lowerKeyI + 1;
        }
    }

    @Override
    public int add(Pair<K, V> entry) throws Exception {

        return add(entry.key, entry.value);
    }

    public int setOrAddByKey(K key, V value, int occ) throws Exception {

        int ind = getIndByKey(key, occ);

        if (ind != -1) {

            setByInd(ind, key, value);
            return ind;
        } else {

            return add(key, value);
        }
    }

    public V getByKey(K key, int occ) {

        int i = (int) Math.ceil(size / 2.0);
        double step = size / 2.0;
        int cmpRes;
        int occCount = 0;
        int prevResInd = 0;

        while (0 <= i && i < size && step > 0.5) {

            cmpRes = key.compareTo(container.get(i).key);

            if (cmpRes == 0) {

                ++occCount;
                prevResInd = i;

                if (occCount == occ) {

                    return container.get(i).value;
                }
            }

            step /= 2.0;

            if (cmpRes <= 0) {

                i -= (int) Math.ceil(step);
            } else {

                i += (int) Math.ceil(step);
            }
        }

        return container.get(prevResInd).value;
    }

    public int getIndByKey(K key, int occ) {

        int i = (int) Math.ceil(size / 2.0);
        double step = size / 2.0;
        int cmpRes;
        int occCount = 0;
        int prevResInd = 0;

        while (0 <= i && i < size && step > 0.5) {

            cmpRes = key.compareTo(container.get(i).key);

            if (cmpRes == 0) {

                ++occCount;
                prevResInd = i;

                if (occCount == occ) {

                    return i;
                }
            }

            step /= 2.0;

            if (cmpRes <= 0) {

                i -= (int) Math.ceil(step);
            } else {

                i += (int) Math.ceil(step);
            }
        }

        return prevResInd;
    }

    public int getLevelByKey(K key, int occ) {

        int i = (int) Math.ceil(size / 2.0);
        double step = size / 2.0;
        int cmpRes;
        int level = -1;
        int occCount = 0;
        int prevLevel = 0;

        while (0 <= i && i < size && step > 0.5) {

            cmpRes = key.compareTo(container.get(i).key);

            if (cmpRes == 0) {

                prevLevel = level;
                ++occCount;

                if (occCount == occ) {

                    return level;
                }
            }

            step /= 2.0;

            if (cmpRes <= 0) {

                i -= (int) Math.ceil(step);

            } else {

                i += (int) Math.ceil(step);
            }

            ++level;
        }

        if (0 < occCount && occCount < occ) {

            return prevLevel;
        } else {

            return -1;
        }
    }

    public void setNewRootByKey(K key, int occ) throws Exception {

        int ind = getIndByKey(key, occ);

        if (ind != -1) {

            throw new NoObjectFoundException("No key has been found.");
        } else {

            int level = getLevelByKey(key, occ);
            int step = (int) Math.ceil(size / Math.pow(2, level));
            container = new ArrayList<>(
                    container.subList(ind - (int) step, ind + (int) step));
        }
    }
}
