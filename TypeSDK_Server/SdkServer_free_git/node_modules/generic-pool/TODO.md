# State tracking design

objectPool
 - constructor takes options for factory methods
    - create [required]
    - destroy [required]
    - validate [optional]
    - activate [optional, new]
    - passivate [optional, new]

- internals
 - idleObjects : linkedlist
 - allObjects : object where, key = object, value = wrapper; what is the equivalent of a Java ConcurrentHashMap in javascript
    this holds all objects associated with the pool in any state apart from destroyed.
    use https://www.npmjs.com/package/hashmap


pooledObject
 wraps an object/resource 
 - id (internal)
 - resource/object
 - state
 - createTime
 - lastBorrowTime
 - lastUseTime
 - lastReturnTime
 - borrowCount

pooled object states
 - IDLE : in the queue, not in use
 - ALLOCATED : in use
 - EVICTION : In the queue, currently being tested for possible eviction.
 - EVICTION_RETURN_TO_HEAD : Not in the queue, currently being tested for possible eviction. An
     attempt to borrow the object was made while being tested which removed it
     from the queue. It should be returned to the head of the queue once
     eviction testing completes.
 - VALIDATION : In the queue, currently being validated
 - VALIDATION_PREALLOCATED : Not in queue, currently being validated. The object was borrowed while
      being validated and since testOnBorrow was configured, it was removed
      from the queue and pre-allocated. It should be allocated once validation
      completes.
 - VALIDATION_RETURN_TO_HEAD :  Not in queue, currently being validated. An attempt to borrow the object
      was made while previously being tested for eviction which removed it from
      the queue. It should be returned to the head of the queue once validation
      completes.
 - INVALID : Failed maintenance (e.g. eviction test or validation) and will be / has been destroyed
 - ABANDONED : Deemed abandoned, to be invalidated.
 - RETURNING : Returning to the pool