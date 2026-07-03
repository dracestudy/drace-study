package com.runtimeverification.rvpredict.util.ll;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.runtimeverification.rvpredict.util.Logger;
import com.runtimeverification.rvpredict.util.Pair;
import com.runtimeverification.rvpredict.util.Triplet;

public class EfficientLLView<K, V> extends EfficientLinkedList<V> {
    /**
     * Maps each reader (identified by key 'READER') to the bottom-most node in its view of the store.
     * The bottom pointer represents the current position or pointer within the store for the reader.
     * It is a mapping from reader keys to the corresponding bottom node in the store.
     * For an empty store, the value is set to null.
     *
     * @see EfficientNode
     */
    protected HashMap<K, EfficientNode<V>> storeBottomPointerForReader;
    /**
     * Maps each reader (identified by key 'READER') to the 0-based index of its view's bottom node
     * from the bottom of the actual store. This index indicates the position of the bottom node
     * within the complete store, and it helps track the relative position of the reader's view.
     *
     * @see EfficientNode
     */
    protected HashMap<K, Integer> storeBottomPointerIndexForReader;
    /**
     * Maps each reader (identified by key 'READER') to a boolean value indicating whether its view
     * of the store is currently empty (true) or not (false).
     */
    protected HashMap<K, Boolean> storeEmptyForReader;

    private HashSet<K> keys;
    private final Logger logger;

    public EfficientLLView(Set<K> keySet) {
        this.logger = Logger.getGlobal();
        this.keys = new HashSet<K>(keySet);
        this.storeBottomPointerForReader = new HashMap<K, EfficientNode<V>>();
        this.storeBottomPointerIndexForReader = new HashMap<K, Integer>();
        this.storeEmptyForReader = new HashMap<K, Boolean>();

        for (K key : keySet) {
            this.storeBottomPointerForReader.put(key, null);
            this.storeBottomPointerIndexForReader.put(key, -1);
            this.storeEmptyForReader.put(key, (Boolean) true);
        }
    }

    /**
     * Adds a new key to the set of keys and sets its bottom pointer and index to the store's head node.
     * Updates the empty status accordingly.
     *
     * <p>The method adds the given key to the set of keys. If the store is empty, the bottom pointer and index
     * are set to default values. If the store is not empty, the bottom pointer is set to the head node of the
     * store, and the index is set to 0. The empty status is updated based on whether the store is empty.
     *
     * @param key The new key to be added to the set of keys.
     */
    public void addKeyToBottom(K key) {
        this.keys.add(key);
        if (isEmpty()) {
            this.storeBottomPointerForReader.put(key, null);
            this.storeBottomPointerIndexForReader.put(key, -1);
            this.storeEmptyForReader.put(key, (Boolean) true);
        } else {
            this.storeEmptyForReader.put(key, (Boolean) false);
            this.storeBottomPointerForReader.put(key, getHeadNode());
            this.storeBottomPointerIndexForReader.put(key, 0);
        }
    }

    /**
     * Adds a new key to the set of keys and updates its associated bottom pointer based on the provided key subset.
     * The bottom pointer is set to the maximum index among the specified key subset, and the empty status is updated
     * accordingly.
     *
     * <p>The method adds the given key to the set of keys and determines its bottom pointer and index based on the
     * provided key subset. If the store is empty, the bottom pointer and index are set to default values. If the store
     * is not empty, the method iterates through the key subset to find the maximum index and corresponding bottom
     * pointer among the specified keys. The empty status is then updated based on whether the bottom pointer is null.
     *
     * @param key The new key to be added to the set of keys.
     * @param keySubset A subset of keys to determine the bottom pointer and index for the new key.
     */
    public void addKeyToTopOfKeys(K key, Set<K> keySubset) {
        this.keys.add(key);
        if (isEmpty()) {
            this.storeBottomPointerForReader.put(key, null);
            this.storeBottomPointerIndexForReader.put(key, -1);
            this.storeEmptyForReader.put(key, (Boolean) true);
        } else {
            boolean keyEmptyReader = true;
            EfficientNode<V> keyPointer = null;
            int keyIndex = -1;
            for (K kprime : keySubset) {
                int kprimeIndex = this.storeBottomPointerIndexForReader.get(kprime);
                if (kprimeIndex > keyIndex) {
                    keyIndex = kprimeIndex;
                    keyPointer = this.storeBottomPointerForReader.get(kprime);
                    keyEmptyReader = false;
                }
            }
            if (keyPointer == null) {
                this.storeEmptyForReader.put(key, (Boolean) false);
                this.storeBottomPointerForReader.put(key, getHeadNode());
                this.storeBottomPointerIndexForReader.put(key, 0);
            } else {
                this.storeEmptyForReader.put(key, keyEmptyReader);
                this.storeBottomPointerForReader.put(key, keyPointer);
                this.storeBottomPointerIndexForReader.put(key, keyIndex);
            }
        }
    }

    public void printKeys() {
        for (K key : this.keys) {
            this.logger.report(key.toString() + " @ " + key.hashCode(), Logger.MSGTYPE.VERBOSE);
        }
    }

    public boolean isEmpty(K key) {
        return this.storeEmptyForReader.get(key);
    }

    /**
     * Retrieves the earliest inserted element in the store associated with the specified key.
     *
     * <p>The method checks if the store at the given key is empty. If the store is empty, an
     * {@code IllegalArgumentException} is thrown with a message indicating that the store at the
     * specified key is empty. Otherwise, the earliest inserted element (bottom) of the store is
     * returned.
     *
     * @param key The key associated with the store from which to retrieve the earliest inserted element.
     * @return The earliest inserted element in the store associated with the specified key.
     * @throws IllegalArgumentException If the store at the given key is empty.
     */
    public V bottom(K key) {
        if (this.storeEmptyForReader.get(key)) {
            throw new IllegalArgumentException(
                    "Cannot get bottom : Store at key '" + key.toString() + "' is empty");
        }
        return this.storeBottomPointerForReader.get(key).getData();
    }

    /**
     * Pushes a new element to the top of the store and updates all associated key pointers.
     *
     * <p>The method extends the functionality of the superclass method by updating the state of all keys
     * associated with the store. If a key's store was empty, the method sets its bottom pointer to the head
     * node of the store and updates the index to 0. The empty status is also marked as false for such keys.
     *
     * @param value The value to be pushed to the top of the store.
     */
    @Override
    public void pushTop(V value) {
        super.pushTop(value);
        for (K key : this.keys) {
            if (this.storeEmptyForReader.get(key)) {
                this.storeEmptyForReader.put(key, (Boolean) false);
                this.storeBottomPointerForReader.put(key, getHeadNode());
                this.storeBottomPointerIndexForReader.put(key, 0);
            }
        }
    }

    /**
     * Retrieves the index of the minimum bottom pointer among non-empty stores.
     *
     * <p>This method iterates through the keys associated with the data stores,
     * checks if there is at least one non-empty store, and determines the index
     * of the minimum bottom pointer among them. The bottom pointer index represents
     * the position of the earliest (bottom) node in the store.
     *
     * <p>If all readers have empty stores, the method returns -1. Otherwise, it returns
     * the index of the minimum bottom pointer of the non-empty stores.
     *
     * @return -1 if all readers have empty stores, else returns the index of the
     *         minimum bottom pointer among non-empty stores.
     */
    private int getMinIndexOfReaders() {
        int minIndex = -1;
        boolean atLeastOne = false;
        for (K key : this.keys) {
            if (!this.storeEmptyForReader.get(key)) {
                int bottomReaderIndex = this.storeBottomPointerIndexForReader.get(key);
                if (!atLeastOne) {
                    atLeastOne = true;
                    minIndex = bottomReaderIndex;
                } else {
                    if (minIndex > bottomReaderIndex) {
                        minIndex = bottomReaderIndex;
                    }
                }
            }
        }
        return minIndex;
    }

    /**
     * Removes a prefix of the specified length from the view of the store and updates the bottom pointers
     * for each key accordingly.
     *
     * <p>The method first checks if the length of the store is less than the specified prefix length. If it is,
     * an {@code IllegalArgumentException} is thrown with a descriptive error message. Otherwise, the method
     * removes the bottom prefix of the specified length using the {@code removeBottomPrefixOfLength} method,
     * and then updates the bottom pointers for each key in the store by subtracting the prefix length from
     * their current values.
     *
     * <p>If the updated bottom pointer for a key becomes negative, indicating that the view is empty for that key,
     * the method marks the store as empty for the reader and sets the bottom pointer and index to default values.
     *
     * @param prefixLength The length of the prefix to remove from the view of the store.
     * @throws IllegalArgumentException If the length of the store is less than the specified prefix length.
     * @see #removeBottomPrefixOfLength(int)
     */
    private void removeViewPrefixOfLength(int prefixLength) {
        if (this.getLength() < prefixLength) {
            throw new IllegalArgumentException(
                    "Invalid operation removeViewPrefixOfLength : Size of store is "
                            + this.getLength()
                            + ", asked to remove : "
                            + prefixLength);
        }
        this.removeBottomPrefixOfLength(prefixLength);
        for (K key : this.keys) {
            if (!this.storeEmptyForReader.get(key)) {
                int minPtr = this.storeBottomPointerIndexForReader.get(key);
                minPtr = minPtr - prefixLength;
                if (minPtr >= 0) {
                    this.storeBottomPointerIndexForReader.put(key, minPtr);
                } else {
                    this.storeEmptyForReader.put(key, true);
                    this.storeBottomPointerForReader.put(key, null);
                    this.storeBottomPointerIndexForReader.put(key, -1);
                }
            }
        }
    }

    /**
     * Updates the store to match the bottom pointers with the minimum bottom pointer index among non-empty stores.
     *
     * <p>The method first checks if the length of the store is greater than 0. If it is, it retrieves the minimum
     * bottom pointer index among non-empty stores using the {@code getMinIndexOfReaders} method. If the minimum
     * index is non-negative, indicating that at least one non-empty store exists, the method removes the view prefix
     * of length equal to the minimum index using the {@code removeViewPrefixOfLength} method. If the minimum index is
     * negative, indicating that all stores are empty, the method removes the entire view prefix of the store's length.
     *
     * @see #getMinIndexOfReaders()
     * @see #removeViewPrefixOfLength(int)
     */
    private void updateStoreToMatchBottomWithMin() {
        EfficientLinkedList<V> st = this;
        int sz = st.getLength();
        if (sz > 0) {
            int minIndex = this.getMinIndexOfReaders();
            if (minIndex >= 0) {
                removeViewPrefixOfLength(minIndex);
            } else {
                removeViewPrefixOfLength(st.getLength());
            }
        }
    }

    /**
     * Flushes the store by updating it to match the bottom pointers with the minimum bottom pointer index
     * among non-empty stores.
     *
     * <p>The method calls the {@code updateStoreToMatchBottomWithMin} method to synchronize the store's view
     * with the minimum bottom pointer index among non-empty stores. This operation ensures that the store
     * reflects the current state of all readers, removing any unnecessary elements from the view.
     *
     * @see #updateStoreToMatchBottomWithMin()
     */
    public void flush() {
        updateStoreToMatchBottomWithMin();
    }

    /**
     * Finds the first node whose value is greater than the target value (according to the provided comparator)
     * corresponding to the given key in the data store.
     *
     * <p>The method returns a triplet consisting of a boolean indicating whether a qualifying node was found,
     * the value of the maximum lower bound node (if found), and a pair containing the reference to the
     * efficient node and its index in the store (iterIndex).
     *
     * <p>The method iterates through the data store for the specified key, starting from the bottom,
     * and finds the maximum lower bound node whose value is less than or equal to the target value.
     * If such a node is found, the boolean is set to true, and the value and node information are recorded.
     * The iteration stops if a node with a value greater than the target value is encountered.
     *
     * @param key The key corresponding to the nodes in the data store.
     * @param targetVal The target value to compare against.
     * @param comparator The comparator used for the comparison of values.
     * @return A triplet containing a boolean indicating whether a qualifying node was found,
     *         the value of the maximum lower bound node (if found), and a pair with the node reference
     *         and its index in the data store.
     */
    private Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>> getMaxLowerBoundNodePointer(
            K key, V targetVal, Comparator<V> comparator) {
        V maxLowerBound = null;
        boolean nodeFound = false;
        EfficientNode<V> iter = null;
        int iterIndex = -1;

        if (!this.storeEmptyForReader.get(key)) {
            iter = storeBottomPointerForReader.get(key);
            iterIndex = storeBottomPointerIndexForReader.get(key);
            int totalSize = this.getLength();
            while (iter != null && iterIndex <= totalSize - 1) {
                V data = iter.getData();
                if (comparator.compare(data, targetVal) <= 0) {
                    nodeFound = true;
                    maxLowerBound = data;
                } else {
                    break;
                }
                iter = iter.getNext();
                iterIndex = iterIndex + 1;
            }
        }
        return new Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>>(
                nodeFound, maxLowerBound, new Pair<EfficientNode<V>, Integer>(iter, iterIndex));
    }

    /**
     * Retrieves information about the last node (n1) with a value less than or equal to the target value,
     * and the first node (n2) with a value greater than the target value, corresponding to the given key
     * in the data store.
     *
     * <p>The method returns a triplet consisting of a boolean indicating whether a qualifying node (n1) was found,
     * the value of the last node (n1) with a value less than or equal to the target value (if found),
     * and a pair containing information about the last node (n1) and the first node (n2) along with their
     * respective indices in the data store.
     *
     * <p>The method iterates through the data store for the specified key, starting from the bottom,
     * and finds the last node (n1) whose value is less than or equal to the target value.
     * If such a node is found, the boolean is set to true, and the value and node information are recorded.
     * The iteration stops if a node with a value greater than the target value (n2) is encountered.
     *
     * <p>If a qualifying node (n1) is found, the pair consists of information about the last node (n1)
     * and the first node (n2), along with their respective indices in the data store.
     * If the qualifying node is the first node in the store, n1 and prevIterIndex are null.
     *
     * @param key The key corresponding to the nodes in the data store.
     * @param targetVal The target value to compare against.
     * @param comparator The comparator used for the comparison of values.
     * @return A triplet containing a boolean indicating whether a qualifying node (n1) was found,
     *         the value of the last node (n1) with a value less than or equal to the target value (if found),
     *         and a pair with information about the last node (n1) and the first node (n2) along with their
     *         respective indices.
     */
    private Triplet<Boolean, V, Pair<Pair<EfficientNode<V>, Integer>, Pair<EfficientNode<V>, Integer>>>
            getMaxLowerBoundPenultimateNodePointer(K key, V targetVal, Comparator<V> comparator) {
        V maxLowerBound = null;
        boolean nodeFound = false;

        EfficientNode<V> iter = null;
        int iterIndex = -1;
        EfficientNode<V> prevIter = null;
        int prevIterIndex = -1;

        if (!this.storeEmptyForReader.get(key)) {
            iter = storeBottomPointerForReader.get(key);
            iterIndex = storeBottomPointerIndexForReader.get(key);
            int totalSize = this.getLength();
            while (iter != null && iterIndex <= totalSize - 1) {
                V data = iter.getData();
                if (comparator.compare(data, targetVal) <= 0) {
                    nodeFound = true;
                    maxLowerBound = data;
                } else {
                    break;
                }
                prevIter = iter;
                prevIterIndex = iterIndex;
                iter = iter.getNext();
                iterIndex = iterIndex + 1;
            }
        }
        Pair<EfficientNode<V>, Integer> prevNode =
                new Pair<EfficientNode<V>, Integer>(prevIter, prevIterIndex);
        Pair<EfficientNode<V>, Integer> iterNode =
                new Pair<EfficientNode<V>, Integer>(iter, iterIndex);
        Pair<Pair<EfficientNode<V>, Integer>, Pair<EfficientNode<V>, Integer>> prevCurr =
                new Pair<Pair<EfficientNode<V>, Integer>, Pair<EfficientNode<V>, Integer>>(
                        prevNode, iterNode);
        return new Triplet<
                Boolean, V, Pair<Pair<EfficientNode<V>, Integer>, Pair<EfficientNode<V>, Integer>>>(
                nodeFound, maxLowerBound, prevCurr);
    }

    /**
     * The store is assumed to be totally ordered according to
     * comparator.compare (denoted <), with the bottom (or headnode) being the
     * smallest and top (or tailNode) being the largest.
     *
     * <p>The function returns a pair &lt;b, val&gt; where b is true if there is
     * at least one node n in the store (corresponding to key) for which
     * n.val &lt;= targetVal. If b is true, then val is the value of the
     * latest node n (with bottom being the earliest) such that n.val &lt;= targetVal.
     * In this case, all the nodes n such that n.val &lt;= targetVal are removed
     * from the store corresponding to key. In this sense, a call to this method can MODIFY
     * the store. If b is false, then val = null.
     *
     * <p>NOTE: The second component is a reference to an object of type V.
     * Care must be taken if this object needs to be updated.
     *
     * @param key The key corresponding to the nodes in the store.
     * @param targetVal The target value to compare against.
     * @param comparator The comparator used for the total ordering.
     * @return A pair &lt;b, val&gt; where b is true if there is at least one qualifying node,
     *         and val is the value of the latest qualifying node; or false if no qualifying node.
     */
    public Pair<Boolean, V> getMaxLowerBound(K key, V targetVal, Comparator<V> comparator) {
        Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>> iterTriplet =
                this.getMaxLowerBoundNodePointer(key, targetVal, comparator);
        boolean nodeFound = iterTriplet.first;
        V maxLowerBound = iterTriplet.second;
        EfficientNode<V> iter = iterTriplet.third.first;
        int iterIndex = iterTriplet.third.second;

        if (nodeFound) {
            if (iter != null) {
                this.storeBottomPointerForReader.put(key, iter);
                this.storeBottomPointerIndexForReader.put(key, iterIndex);
            } else {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            }
            this.updateStoreToMatchBottomWithMin();
        } else {
            maxLowerBound = null;
        }
        return new Pair<Boolean, V>(nodeFound, maxLowerBound);
    }

    /**
     * Retrieves the maximum lower bound node and its index for the specified key with respect to the target value.
     * Returns a triplet containing a boolean indicating if a node was found, the maximum lower bound value (or null
     * if no node was found), and a pair representing the penultimate node and its index in the store sequence.
     *
     * <p>The method delegates the retrieval of the maximum lower bound node and its index to the
     * {@code getMaxLowerBoundPenultimateNodePointer} method. It then processes the result to update the store's
     * bottom pointer for the specified key. If a node is found, the penultimate node's index is used to update
     * the store's bottom pointer. If no node is found, the store is marked as empty for the reader.
     *
     * @param key The key for which the maximum lower bound node should be retrieved.
     * @param targetVal The target value for comparison.
     * @param comparator The comparator for comparing values.
     * @return A triplet containing a boolean indicating if a node was found, the maximum lower bound value (or null
     *     if no node was found), and a pair representing the penultimate node and its index.
     * @see #getMaxLowerBoundPenultimateNodePointer(Object, Object, Comparator)
     */
    public Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>> getMaxLowerBoundPenultimate(
            K key, V targetVal, Comparator<V> comparator) {
        Triplet<Boolean, V, Pair<Pair<EfficientNode<V>, Integer>, Pair<EfficientNode<V>, Integer>>>
                iterTriplet = this.getMaxLowerBoundPenultimateNodePointer(key, targetVal, comparator);
        boolean nodeFound = iterTriplet.first;
        V maxLowerBound = iterTriplet.second;

        EfficientNode<V> prevIter = iterTriplet.third.first.first;
        int prevIterIndex = iterTriplet.third.first.second;

        if (nodeFound) {
            if (prevIter != null) {
                this.storeBottomPointerForReader.put(key, prevIter);
                this.storeBottomPointerIndexForReader.put(key, prevIterIndex);
            } else {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            }
        } else {
            maxLowerBound = null;
        }
        return new Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>>(
                nodeFound, maxLowerBound, iterTriplet.third.second);
    }

    public void setBottom(K key, Pair<EfficientNode<V>, Integer> iter) {
        EfficientNode<V> iterNode = iter.first;
        int iterIndex = iter.second;
        if (iterNode != null) {
            this.storeBottomPointerForReader.put(key, iterNode);
            this.storeBottomPointerIndexForReader.put(key, iterIndex);
        } else {
            this.storeEmptyForReader.put(key, true);
            this.storeBottomPointerForReader.put(key, null);
            this.storeBottomPointerIndexForReader.put(key, -1);
        }
    }

    /**
     * Retrieves information about the maximum lower bound node (according to the provided comparator)
     * without modifying the data store.
     *
     * <p>This method is similar to {@link #getMaxLowerBoundNodePointer(K, V, Comparator)}, but it does
     * not alter the state of the data store. It returns a pair consisting of a boolean indicating whether
     * a qualifying node was found and the value of the maximum lower bound node (if found).
     *
     * <p>The method iterates through the data store for the specified key, starting from the bottom,
     * and finds the maximum lower bound node whose value is less than or equal to the target value.
     * If such a node is found, the boolean is set to true, and the value is recorded. The iteration stops
     * if a node with a value greater than the target value is encountered.
     *
     * <p>If no qualifying node is found, the method returns a pair with the boolean set to false, and
     * the value set to null.
     *
     * @param key The key corresponding to the nodes in the data store.
     * @param targetVal The target value to compare against.
     * @param comparator The comparator used for the comparison of values.
     * @return A pair containing a boolean indicating whether a qualifying node was found and the
     *         value of the maximum lower bound node (if found). If no qualifying node is found, the
     *         boolean is false, and the value is null.
     */
    public Pair<Boolean, V> getMaxLowerBoundWithoutUpdate(
            K key, V targetVal, Comparator<V> comparator) {
        Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>> iterTriplet =
                this.getMaxLowerBoundNodePointer(key, targetVal, comparator);
        boolean nodeFound = iterTriplet.first;
        V maxLowerBound = iterTriplet.second;
        if (!nodeFound) {
            maxLowerBound = null;
        }
        return new Pair<Boolean, V>(nodeFound, maxLowerBound);
    }

    /**
     * Removes the largest prefix of entries for the specified key that are between the given lowerBound
     * and upperBound (inclusive) in the data store. Additionally, returns the minimum entry if there is any
     * within the specified range.
     *
     * <p>The method iterates through the data store for the specified key, starting from the bottom,
     * and removes entries whose values fall between the provided lowerBound and upperBound. The largest
     * prefix of such entries is removed. If any entry is removed, the method returns true; otherwise,
     * it returns false. If there is a minimum entry within the specified range, it is also returned.
     *
     * <p>If entries are removed, the bottom pointer for the specified key is updated to point to the next
     * available entry after the removed prefix. If no entries are left after the removal, the store is
     * marked as empty for the reader. Additionally, the method updates the store to match the bottom pointer
     * and the minimum entry to maintain consistency.
     *
     * @param key The key corresponding to the entries in the data store.
     * @param lowerBound The lower bound for values to be removed.
     * @param comparatoLB The comparator used for comparing the lower bound.
     * @param upperBound The upper bound for values to be removed.
     * @param comparatorUB The comparator used for comparing the upper bound.
     * @return A pair containing a boolean indicating whether entries were removed (true if removed, false otherwise),
     *         and the minimum entry value within the specified range. If no entries are removed, the boolean is false,
     *         and the minimum entry value is null.
     */
    public Pair<Boolean, V> removePrefixWithinReturnMin(
            K key, V lowerBound, Comparator<V> comparatoLB, V upperBound, Comparator<V> comparatorUB) {

        V iterVal = null;
        boolean nodeFound = false;
        EfficientNode<V> iter = null;
        int iterIndex = -1;

        V minVal = null;
        boolean firstOne = true;

        if (!this.storeEmptyForReader.get(key)) {
            iter = storeBottomPointerForReader.get(key);
            iterIndex = storeBottomPointerIndexForReader.get(key);
            int totalSize = this.getLength();
            while (iter != null && iterIndex <= totalSize - 1) {
                iterVal = iter.getData();
                if (comparatoLB.compare(lowerBound, iterVal) <= 0
                        && comparatorUB.compare(iterVal, upperBound) <= 0) {
                    nodeFound = true;
                    if (firstOne) {
                        minVal = iterVal;
                        firstOne = false;
                    }
                } else {
                    break;
                }
                iter = iter.getNext();
                iterIndex = iterIndex + 1;
            }
        }

        if (nodeFound) {
            if (iter != null) {
                this.storeBottomPointerForReader.put(key, iter);
                this.storeBottomPointerIndexForReader.put(key, iterIndex);
            } else {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            }
            this.updateStoreToMatchBottomWithMin();
        } else {
            minVal = null;
        }
        return new Pair<Boolean, V>(nodeFound, minVal);
    }

    /**
     * Removes the largest prefix of entries for the specified key that are between the given lowerBound
     * and upperBound (inclusive) in the data store. Additionally, returns the minimum entry if there is any
     * within the specified range.
     *
     * <p>This overloaded version of the method uses the same logic as the original method
     * {@link #removePrefixWithinReturnMin(K, V, Comparator, V, Comparator)}, with the exception that it
     * uses the same comparator for both the lower and upper bounds.
     *
     * <p>The method iterates through the data store for the specified key, starting from the bottom,
     * and removes entries whose values fall between the provided lowerBound and upperBound. The largest
     * prefix of such entries is removed. If any entry is removed, the method returns true; otherwise,
     * it returns false. If there is a minimum entry within the specified range, it is also returned.
     *
     * <p>If entries are removed, the bottom pointer for the specified key is updated to point to the next
     * available entry after the removed prefix. If no entries are left after the removal, the store is
     * marked as empty for the reader. Additionally, the method updates the store to match the bottom pointer
     * and the minimum entry to maintain consistency.
     *
     * @param key The key corresponding to the entries in the data store.
     * @param lowerBound The lower bound for values to be removed.
     * @param upperBound The upper bound for values to be removed.
     * @param comparator The comparator used for comparing both the lower and upper bounds.
     * @return A pair containing a boolean indicating whether entries were removed (true if removed, false otherwise),
     *         and the minimum entry value within the specified range. If no entries are removed, the boolean is false,
     *         and the minimum entry value is null.
     */
    public Pair<Boolean, V> removePrefixWithinReturnMin(
            K key, V lowerBound, V upperBound, Comparator<V> comparator) {
        return removePrefixWithinReturnMin(key, lowerBound, comparator, upperBound, comparator);
    }

    /**
     * Retrieves the key with the minimum bottom pointer index among non-empty stores
     * from the specified set of keys. Returns null if all keys in the set have empty stores.
     *
     * <p>This method iterates through the given set of keys and checks if each key corresponds to a
     * non-empty store. If a non-empty store is found, the method compares the bottom pointer index
     * of the store with the minimum index encountered so far. If the current store has a smaller
     * bottom pointer index, it becomes the new minimum, and the corresponding key is recorded.
     *
     * <p>If all stores associated with the keys in the set are empty, the method returns null.
     * Otherwise, it returns a pair containing the key with the minimum bottom pointer index among
     * non-empty stores and the minimum index itself.
     *
     * @param keySet The set of keys to check for minimum bottom pointer index.
     * @return A pair containing the key with the minimum bottom pointer index among non-empty stores
     *         and the minimum index itself. Returns null if all keys in the set have empty stores.
     */
    public Pair<K, Integer> getMinKey(HashSet<K> keySet) {
        int minIndex = -1;
        K minKey = null;
        boolean atLeastOne = false;
        for (K key : keySet) {
            if (!this.storeEmptyForReader.get(key)) {
                int bottomReaderIndex = this.storeBottomPointerIndexForReader.get(key);
                if (!atLeastOne) {
                    atLeastOne = true;
                    minIndex = bottomReaderIndex;
                    minKey = key;
                } else {
                    if (minIndex > bottomReaderIndex) {
                        minIndex = bottomReaderIndex;
                        minKey = key;
                    }
                }
            }
        }
        return new Pair<K, Integer>(minKey, minIndex);
    }

    /**
     * Locates the node in the data store with a value less than or equal to the specified target value (V),
     * such that the next node's value is greater than V. For each key in the given key set, if the key is
     * positioned before the located node, it is advanced to point to the located node.
     *
     * <p>The method first determines the key with the minimum bottom pointer index among non-empty stores
     * from the provided key set. It then finds the maximum lower bound node for that key with a value less
     * than or equal to the target value. If such a node is found, the method iterates through the key set,
     * and for each key positioned before the located node, it updates the bottom pointer and index to point
     * to the located node. If the located node is the last node in the store for a key, the store for that
     * key is marked as empty for the reader.
     *
     * <p>If no maximum lower bound node is found, the method marks the stores as empty for the reader for
     * all keys in the set.
     *
     * <p>After any updates to the stores, the method ensures the data store is consistent by updating it
     * to match the bottom pointer with the minimum key and index.
     *
     * @param keySet The set of keys to check for updating bottom pointers.
     * @param targetVal The target value to compare against.
     * @param comparator The comparator used for comparing values.
     * @return A pair containing a boolean indicating whether a maximum lower bound node was found (true if found,
     *         false otherwise), and the value of the maximum lower bound node (if found). If no maximum lower bound
     *         node is found, the boolean is false, and the value is null.
     */
    public Pair<Boolean, V> getMaxLowerBoundKeySet(
            HashSet<K> keySet, V targetVal, Comparator<V> comparator) {
        Pair<K, Integer> minKeyMinIndex = this.getMinKey(keySet);
        K minKey = minKeyMinIndex.first;

        Triplet<Boolean, V, Pair<EfficientNode<V>, Integer>> iterTriplet =
                this.getMaxLowerBoundNodePointer(minKey, targetVal, comparator);
        boolean nodeFound = iterTriplet.first;
        V maxLowerBound = iterTriplet.second;
        EfficientNode<V> iter = iterTriplet.third.first;
        int iterIndex = iterTriplet.third.second;

        if (nodeFound) {
            if (iter != null) {
                for (K key : keySet) {
                    int keyIndex = this.storeBottomPointerIndexForReader.get(key);
                    if (keyIndex < iterIndex) {
                        this.storeBottomPointerForReader.put(key, iter);
                        this.storeBottomPointerIndexForReader.put(key, iterIndex);
                    }
                }
            } else {
                for (K key : keySet) {
                    this.storeEmptyForReader.put(key, true);
                    this.storeBottomPointerForReader.put(key, null);
                    this.storeBottomPointerIndexForReader.put(key, -1);
                }
            }
            this.updateStoreToMatchBottomWithMin();
        } else {
            maxLowerBound = null;
        }
        return new Pair<Boolean, V>(nodeFound, maxLowerBound);
    }

    /**
     * Advances the bottom pointer for the specified key by one position in the store.
     *
     * <p>The method first checks if the store is empty for the specified key. If it is not empty, it retrieves
     * the current bottom pointer and index for the key. If the key is already at the last position in the store,
     * indicating that it has reached the end, the method marks the store as empty for the reader and sets the
     * bottom pointer and index to default values. Otherwise, the method increments the index, moves the bottom
     * pointer to the next node in the sequence, and updates the bottom pointer and index for the key.
     *
     * @param key The key for which the bottom pointer should be advanced.
     */
    public void advanceKeyByOne(K key) {
        if (!this.storeEmptyForReader.get(key)) {
            EfficientNode<V> iter = storeBottomPointerForReader.get(key);
            int keyIndex = storeBottomPointerIndexForReader.get(key);
            if (keyIndex == this.getLength() - 1) {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            } else {
                keyIndex = keyIndex + 1;
                iter = iter.getNext();
                this.storeBottomPointerForReader.put(key, iter);
                this.storeBottomPointerIndexForReader.put(key, keyIndex);
            }
        }
    }

    /**
     * Advances the bottom pointer and index for the specified key to match the bottom pointer and index
     * of the target key if the target key is positioned earlier in the data store.
     *
     * <p>The method checks if the store for the specified key is non-empty. If it is, the method further
     * checks if the store for the target key is empty. If the store for the target key is empty, the method
     * marks the store for the specified key as empty for the reader. Otherwise, it compares the bottom
     * pointer index of the specified key with that of the target key. If the specified key is positioned
     * earlier than the target key, the bottom pointer and index for the specified key are updated to match
     * those of the target key.
     *
     * @param key The key for which to advance the bottom pointer and index.
     * @param target The target key whose position is used for comparison.
     * @see EfficientNode
     * @param <V> The type of values stored in the nodes.
     * @param <K> The type of keys used to identify data in the store.
     */
    public void advanceKeyToTarget(K key, K target) {
        if (!this.storeEmptyForReader.get(key)) {
            if (this.storeEmptyForReader.get(target)) {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            } else {
                int keyIndex = storeBottomPointerIndexForReader.get(key);
                int targetIndex = storeBottomPointerIndexForReader.get(target);
                if (keyIndex < targetIndex) {
                    EfficientNode<V> targetIter = storeBottomPointerForReader.get(target);
                    this.storeBottomPointerForReader.put(key, targetIter);
                    this.storeBottomPointerIndexForReader.put(key, targetIndex);
                }
            }
        }
    }

    /**
     * Sets the bottom pointer and index for each key in the specified key set to match the bottom pointer
     * and index of the given key, if the view of the given key is non-empty and the key in the set is positioned
     * behind the given key. If the view of the given key is empty, sets all keys in the set to have empty views.
     *
     * <p>The method first checks if the view for the given key is empty. If it is, the method iterates through
     * the specified key set and marks each key's store as empty for the reader. Otherwise, it retrieves the
     * bottom pointer and index of the given key and iterates through the key set. For each key positioned behind
     * the given key, the bottom pointer and index are updated to match those of the given key.
     *
     * @param keySet The set of keys for which to set the bottom pointers and indices.
     * @param givenKey The key whose bottom pointer and index are used as a reference.
     */
    public void setBottomOfAllKeysToGivenKey(HashSet<K> keySet, K givenKey) {
        if (this.storeEmptyForReader.get(givenKey)) {
            for (K key : keySet) {
                this.storeEmptyForReader.put(key, true);
                this.storeBottomPointerForReader.put(key, null);
                this.storeBottomPointerIndexForReader.put(key, -1);
            }
        } else {
            EfficientNode<V> givenKeyIter = storeBottomPointerForReader.get(givenKey);
            int givenKeyIndex = storeBottomPointerIndexForReader.get(givenKey);
            for (K key : keySet) {
                int keyIndex = storeBottomPointerIndexForReader.get(key);
                if (keyIndex < givenKeyIndex) {
                    this.storeBottomPointerForReader.put(key, givenKeyIter);
                    this.storeBottomPointerIndexForReader.put(key, givenKeyIndex);
                }
            }
        }
    }

    public int getSize() {
        return getLength();
    }

    public void printSize() {
        this.logger.report("Size = " + Integer.toString(this.getSize()), Logger.MSGTYPE.VERBOSE);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(super.toString());
        output.append("\n[ ");
        for (K key : this.keys) {
            output.append("<");
            output.append(key.toString());
            output.append(" : ");
            output.append(this.storeEmptyForReader.get(key) ? "{_|_}" : bottom(key));
            output.append("> | \n");
        }
        output.append(" ]");
        return output.toString();
    }

    public String toStoreString() {
        return super.toString();
    }

    /**
     * Destroys the store associated with the specified key and removes key-related pointers.
     *
     * <p>The method first checks if the key exists in the set of keys. If the key does not exist,
     * an {@code IllegalArgumentException} is thrown with a message indicating that the store cannot
     * be deleted for a non-existent key. If the key exists, the method removes the associated pointers
     * for the store, including the bottom pointer, empty status, and bottom pointer index. Finally,
     * the method updates the store to match the bottom with the minimum index of all remaining keys.
     *
     * @param key The key for which to destroy the associated store.
     * @throws IllegalArgumentException If the specified key does not exist in the set of keys.
     */
    public void destroyKey(K key) {
        if (!keys.contains(key)) {
            throw new IllegalArgumentException(
                    "Cannot delete store for non-existent key " + key.toString());
        } else {
            this.storeBottomPointerForReader.remove(key);
            this.storeEmptyForReader.remove(key);
            this.storeBottomPointerIndexForReader.remove(key);
            this.updateStoreToMatchBottomWithMin();
        }
    }
}
