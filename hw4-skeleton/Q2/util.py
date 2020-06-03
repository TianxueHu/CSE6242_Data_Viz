from scipy import stats
import numpy as np

# This method computes entropy for information gain
def entropy(class_y):
    # Input:            
    #   class_y         : list of class labels (0's and 1's)
    
    # TODO: Compute the entropy for a list of classes
    #
    # Example:
    #    entropy([0,0,0,1,1,1,1,1,1]) = 0.92
 
    _, counts = np.unique(class_y,return_counts = True)
    prob = counts/counts.sum();
    entropy = np.sum(prob * -np.log2(prob));
    return entropy


def partition_classes(X, y, split_attribute, split_val):
    # Inputs:
    #   X               : data containing all attributes
    #   y               : labels
    #   split_attribute : column index of the attribute to split on
    #   split_val       : either a numerical or categorical value to divide the split_attribute
    
    # TODO: Partition the data(X) and labels(y) based on the split value - BINARY SPLIT.
    # 
    # You will have to first check if the split attribute is numerical or categorical    
    # If the split attribute is numeric, split_val should be a numerical value
    # For example, your split_val could be the mean of the values of split_attribute
    # If the split attribute is categorical, split_val should be one of the categories.   
    #
    # You can perform the partition in the following way
    # Numeric Split Attribute:
    #   Split the data X into two lists(X_left and X_right) where the first list has all
    #   the rows where the split attribute is less than or equal to the split value, and the 
    #   second list has all the rows where the split attribute is greater than the split 
    #   value. Also create two lists(y_left and y_right) with the corresponding y labels.
    #
    # Categorical Split Attribute:
    #   Split the data X into two lists(X_left and X_right) where the first list has all 
    #   the rows where the split attribute is equal to the split value, and the second list
    #   has all the rows where the split attribute is not equal to the split value.
    #   Also create two lists(y_left and y_right) with the corresponding y labels.

    '''
    Example:
    
    X = [[3, 'aa', 10],                 y = [1,
         [1, 'bb', 22],                      1,
         [2, 'cc', 28],                      0,
         [5, 'bb', 32],                      0,
         [4, 'cc', 32]]                      1]
    
    Here, columns 0 and 2 represent numeric attributes, while column 1 is a categorical attribute.
    
    Consider the case where we call the function with split_attribute = 0 and split_val = 3 (mean of column 0)
    Then we divide X into two lists - X_left, where column 0 is <= 3  and X_right, where column 0 is > 3.
    
    X_left = [[3, 'aa', 10],                 y_left = [1,
              [1, 'bb', 22],                           1,
              [2, 'cc', 28]]                           0]
              
    X_right = [[5, 'bb', 32],                y_right = [0,
               [4, 'cc', 32]]                           1]

    Consider another case where we call the function with split_attribute = 1 and split_val = 'bb'
    Then we divide X into two lists, one where column 1 is 'bb', and the other where it is not 'bb'.
        
    X_left = [[1, 'bb', 22],                 y_left = [1,
              [5, 'bb', 32]]                           0]
              
    X_right = [[3, 'aa', 10],                y_right = [1,
               [2, 'cc', 28],                           0,
               [4, 'cc', 32]]                           1]
               
    ''' 
    
    X_left = []
    X_right = []
    
    y_left = []
    y_right = []
    
    X = np.array(X)
    
    N,D = X.shape
    
    ##numerical
    if isinstance(split_val, (int, float, complex)):
        for n in range(N):
            if int(X[n,split_attribute]) <= split_val:
                X_left.append(X[n,:]) 
                y_left.append(y[n])
            else:
                X_right.append(X[n,:])
                y_right.append(y[n])
    elif isinstance(split_val, str):
        for n in range(N):
            if X[n,split_attribute] == split_val:
                X_left.append(X[n,:]) 
                y_left.append(y[n])
            else:
                X_right.append(X[n,:])
                y_right.append(y[n])
        
        
    return np.array(X_left), np.array(X_right), np.array(y_left), np.array(y_right)

    
def information_gain(previous_y, current_y):
    # Inputs:
    #   previous_y: the distribution of original labels (0's and 1's)
    #   current_y:  the distribution of labels after splitting based on a particular
    #               split attribute and split value
    
    # TODO: Compute and return the information gain from partitioning the previous_y labels
    # into the current_y labels.
    # You will need to use the entropy function above to compute information gain
    # Reference: http://www.cs.cmu.edu/afs/cs.cmu.edu/academic/class/15381-s06/www/DTs.pdf
    
    """
    Example:
    
    previous_y = [0,0,0,1,1,1]
    current_y = [[0,0], [1,1,1,0]]
    
    info_gain = 0.45915
    """

    info_gain = 0
    #print(current_y[0])
    #print(current_y[1])
    probL = len(current_y[0])/len(previous_y)
    probR=len(current_y[1])/len(previous_y)
    info0 = probL * entropy(current_y[0])
    info1 = probR * entropy(current_y[1]) 
    info_gain= entropy(previous_y)- info0 - info1 
    return info_gain

    
def best_split(X, y):
    # Inputs:
    #   X       : Data containing all attributes
    #   y       : labels
    # TODO: For each node find the best split criteria and return the 
    # split attribute, spliting value along with 
    # X_left, X_right, y_left, y_right (using partition_classes)
    '''
    
    NOTE: Just like taught in class, don't use all the features for a node.
    Repeat the steps:

    1. Select m attributes out of d available attributes
    2. Pick the best variable/split-point among the m attributes
    3. return the split attributes, split point, left and right children nodes data 

    '''
    split_attribute = 0
    split_value = 0
    X_left, X_right, y_left, y_right = [], [], [], []
    
    N,D = np.array(X).shape
    #set m attibite
    m = int(np.sqrt(D))
    attr_list = np.random.choice(D, m , replace = False)
    best_attr_index = -1
    best_info_gain = 1e-10
    best_split_val = 0
    #ig_max = []
    #split_val_max= []
    for attr in attr_list:
        #discussed with classmates on how to reduce runtime, finding the beat value by
        #...taking the mean of each column instead of running every element in the column
        val = np.mean(X[:,attr])
        X_left, X_right, y_left, y_right = partition_classes(X,y,attr,val)
        info_gain = information_gain(y,[y_left,y_right])

        if info_gain > best_info_gain:
            best_info_gain = info_gain
            best_split_val = val
            best_attr_index = attr

    split_attribute = best_attr_index
    split_value = best_split_val

    X_left, X_right, y_left, y_right = partition_classes(X,y,split_attribute,split_value)

   
    return split_attribute, split_value, X_left, X_right, y_left, y_right
'''     
        info_gain = []
        split_values=list(set([X[k][i] for k in range(N)]))
        for j, vals in enumerate(split_values):
            X_left, X_right, y_left, y_right = partition_classes(X, y, i, vals)
            info_gain.append(information_gain(y, [y_left, y_right]))
        ig_max.append(max(info_gain))
        ig_index = np.argmax(info_gain)
        split_val_max.append(split_values[ig_index])

    ig_max_index = np.argmax(ig_max)
    split_attribute = ig_max_index
    split_value = split_val_max[ig_max_index]
    X_left, X_right, y_left, y_right = partition_classes(X, y, split_attribute, split_value)
'''


'''
X = [[3, 'aa', 10], [1, 'bb', 22],  [2, 'cc', 28],   [5, 'bb', 32],[4, 'cc', 32]]    
X= np.asarray(X).reshape(5,3) 
print(X)
y=[1,1,0,0,1]
a,b,c,d,e,f=best_split(X, y)
print(a,b,c,d,e,f)
'''

