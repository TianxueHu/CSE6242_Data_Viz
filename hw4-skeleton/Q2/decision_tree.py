import numpy as np 
import ast
import util
from scipy.stats import mode


class DecisionTree(object):
    def __init__(self, max_depth):
        # Initializing the tree as an empty dictionary or list, as preferred
        self.tree = {}
        self.max_depth = max_depth
        pass
    	
    def learn(self, X, y, par_node = {}, depth=0):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree

        # Use the function best_split in util.py to get the best split and 
        # data corresponding to left and right child nodes
        
        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a 
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value)
        ### Implement your code here
        #############################################
        
        self.tree['parent'] = par_node
        '''
        if util.entropy(y)<0.1:
            self.tree['status']=mode(y).mode[0]#'leaf'
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
            #current_node.leaf=True
        '''
        
        if len(y)==0:
            self.tree['status']= 0#'leaf'
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
        
        elif len(y) == 1:
            self.tree["status"] = y[0]
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
            
        elif np.sum(y) == len(y):
            self.tree['status'] = 1 
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'

        elif np.sum(y) == 0:
            self.tree['status'] = 0
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
        
        elif depth<self.max_depth:
            split_attribute, split_value, X_left, X_right, y_left, y_right = util.best_split(X, y)
            depth+=1
            self.tree['split_attribute']= split_attribute
            self.tree['split_value']= split_value
            self.tree['left'] = DecisionTree(self.max_depth)
            self.tree['right'] = DecisionTree(self.max_depth)
            self.tree['left'].learn(X_left,y_left,depth=depth)
            self.tree['right'].learn(X_right,y_right,depth=depth)
            self.tree['status']="parent"
            
        
        else: # if large than max depth 
            self.tree['status']=mode(y).mode[0]#'leaf'
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
            return

        '''

        elif (X == X[0]).all():
            self.tree['status']= 'leaf'
            self.tree['split_attribute'] = 'Null'
            self.tree['split_value'] = 'Null'
            self.tree['left'] = y
            self.tree['right'] = y
            return
        '''


    def classify(self, record):
        # TODO: classify the record using self.tree and return the predicted label
        ### Implement your code here
        #############################################
        
        if self.tree['split_value'] == 'Null': ##at leaf
            return self.tree['status']
        else: ##at parent
            att = self.tree['split_attribute']
            value = self.tree['split_value']
                
            if isinstance(value, str):
                if record[att] ==value:
                    return self.tree['left'].classify(record)
                else:
                    return self.tree['right'].classify(record)  
            else: 
                if record[att]<=value:
                    return self.tree['left'].classify(record)
                else:
                    return self.tree['right'].classify(record)  
                    
        #############################################
