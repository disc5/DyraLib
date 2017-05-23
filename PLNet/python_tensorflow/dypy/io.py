# -*- coding: utf-8 -*-
"""
I/O operations for dyad ranking

@author: ds
"""
import pandas as pd

def read_xxl_file_as_matrices(filename):
    ''' Reads label ranking file in XXL format (Seeger Group).
        The methods automatically detects how many attributes and how many
        labels exist in the data set.
                        
        Output:
            X - N x p numpy matrix of N instance vectors in rows
            Y - label ranking matrix N x M
    '''
    df = pd.read_csv(filename, header =0, skiprows=[1], sep="\t")
    a = df.axes
    header = a[1]
    
    nAtts = 0     # Detect number of attributes / labels
    nLabels = 0
    for at in header:
        if (at.startswith('A')): 
            nAtts+= 1
        elif (at.startswith('L')):
            nLabels+= 1
    insts = df.iloc[:,0:nAtts]
    labels = df.iloc[:,nAtts:]
    X = insts.as_matrix()
    Y = labels.as_matrix()
    return (X,Y)